package net.runelite.client

import com.google.inject.Provides
import net.runelite.api.*
import net.runelite.api.events.ChatMessage
import net.runelite.api.events.MenuOptionClicked
import net.runelite.api.widgets.WidgetID
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.oneclicklavas.*
import net.runelite.client.plugins.oneclicklavas.magic.ALTAR
import net.runelite.client.plugins.oneclicklavas.magic.BANK
import net.runelite.client.plugins.oneclicklavas.magic.RUINS
import net.runelite.client.plugins.oneclicklavas.util.*
import net.runelite.client.plugins.zeahcrafter.OneClickLavasConfig
import org.pf4j.Extension
import javax.inject.Inject
import kotlin.properties.Delegates

@Extension
@PluginDescriptor(
    name = "One Click Lavas",
    description = ":Prayje:",
    tags = ["rebecca, oneclick, one click, lavas"]
)
class OneClickLavasPlugin : Plugin() {

    @Inject
    lateinit var config: OneClickLavasConfig

    @Inject
    lateinit var client: Client

    @Inject
    lateinit var actions: Actions

    companion object : Log()

    var attributes = linkedMapOf("charges" to 0, "fill" to 0, "filled" to 0, "emptied" to 0, "stamina" to 0, "repair" to 0)
    private var process = true
    var repaired = false

    @Provides
    fun provideConfig(configManager: ConfigManager): OneClickLavasConfig {
        return configManager.getConfig(OneClickLavasConfig::class.java)
    }

    override fun startUp() {
        log.info("Starting One click lavas")
        attributes["charges"] = 0
        attributes["repair"] = 0
        repaired = true
        reset()
    }

    override fun shutDown() {
        log.info("Stopping One click lavas")
    }

    private fun reset() {
        attributes["fill"] = 0
        attributes["filled"] = 0
        attributes["emptied"] = 0
        attributes["stamina"] = 0
        process = true
        state = States.OPEN_BANK
    }

    private var items: Array<Item> by Delegates.observable(arrayOf()) { _, prev, curr ->
        if(!prev.contentEquals(curr)) {
           /* if(state == States.FILL_POUCH) {
                attributes.computeIfPresent("fill") { _, v -> v + 1 }
            }*/
        }
    }

    private var state by Delegates.observable(States.OPEN_BANK) { _, prev, curr ->
        if (prev != curr) {
            if(curr == States.FILL_POUCH) {
                attributes.computeIfPresent("fill") { _, v -> v + 1 }
            }
            process = true
        }
    }

    @Subscribe
    fun onChatMessage(event: ChatMessage) {
        if(event.type == ChatMessageType.GAMEMESSAGE && event.message.contains("there is no essence in this pouch", true)) {
            attributes["emptied"] = 1
            return
        }
        if(event.type == ChatMessageType.GAMEMESSAGE && event.message.contains("you bind the temple", true)) {
            state = States.EMPTY_POUCHES
            attributes.computeIfPresent("charges") { _, v -> v - 1 }
            return
        }
    }

    @Subscribe
    fun onMenuEntryClicked(event: MenuOptionClicked) {
        with(actions) {
            checkStates()
            client.getItemContainer(InventoryID.INVENTORY.id)?.let {
                items = it.items
            }
            if(attributes["repair"]!! >= 1) {
                event.handleMage()
                return
            }
            if (!process || event.menuOption.contains("walk here", true)) {
                event.consume()
            }
            process = false

            val bank = client.findGameObject(BANK)
            val ruin = client.findGameObject(RUINS)
            val altar = client.findGameObject(ALTAR)

            when (state) {
                States.TELEPORT_TO_BANK -> {
                    event.teleport()
                    return
                }
                States.NEED_DEPOSIT -> {
                    client.getBankInventoryItem(ItemID.LAVA_RUNE)?.let {
                        event.clickItem(it, 2, WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.id)
                        return
                    }
                }
                States.OPEN_BANK -> {
                    bank?.let {
                        event.use(it)
                        return
                    }
                }
                States.NEED_STAMINA -> {
                    attributes["stamina"] = 1
                    client.getBankItem(ItemID.STAMINA_POTION1)?.let {
                        event.clickItem(it, 2, WidgetInfo.BANK_ITEM_CONTAINER.id)
                        return
                    }
                }
                States.NEED_NECKLACE -> {
                    attributes["charges"] = -1
                    client.getBankItem(ItemID.BINDING_NECKLACE)?.let {
                        event.clickItem(it, 2, WidgetInfo.BANK_ITEM_CONTAINER.id)
                        return
                    }
                }
                States.NEED_ESSENCE -> {
                    client.getBankItem(ItemID.PURE_ESSENCE)?.let {
                        event.clickItem(it, 1, WidgetInfo.BANK_ITEM_CONTAINER.id)
                        return
                    }
                }
                States.FILL_POUCH -> {
                    client.getBankInventoryItem(ItemID.COLOSSAL_POUCH)?.let {
                        event.clickItem(it, 9, WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.id)
                        return
                    }
                }
                States.CLOSE_BANK -> {
                    event.closeBank()
                    state = States.TELEPORT_FROM_BANK
                    return
                }
                States.TELEPORT_FROM_BANK -> {
                    event.rub()
                    return
                }
                States.DESTROY_NECKLACE -> {
                    event.destroy()
                    state = States.CONFIRM_DESTROY
                    return
                }
                States.CONFIRM_DESTROY -> {
                    attributes["charges"] = 15
                    event.confirm()
                    state = States.ENTER_RUINS
                    return
                }
                States.DRINK_STAMINA -> {
                    client.getInventoryItem(ItemID.STAMINA_POTION1)?.let {
                        attributes["stamina"] = 0
                        event.clickItem(it, 2, WidgetInfo.INVENTORY.id)
                    }
                    state = States.ENTER_RUINS
                    return
                }
                States.ENTER_RUINS -> {
                    ruin?.let {
                        event.use(it)
                        return
                    }
                }
                States.IMBUE -> {
                    event.imbue()
                    state = States.CRAFT_RUNES
                    return
                }
                States.CRAFT_RUNES -> {
                    altar?.let {
                        event.useOn(it)
                    }
                }
                States.EMPTY_POUCHES -> {
                    client.getInventoryItem(ItemID.COLOSSAL_POUCH)?.let {
                        event.clickItem(it, 3, WidgetInfo.INVENTORY.id)
                        state = States.CRAFT_RUNES
                    }
                }
                else -> return
            }
        }
    }

    private fun checkStates() {
        if(!client.banking()) {
            if (!repaired && attributes["repair"] == 0) {
                attributes["repair"] = 1
                return
            }
            if (client.mapRegions.contains(13107) && state == States.ENTER_RUINS) {
                if(client.getInventoryItem(ItemID.BINDING_NECKLACE) != null) {
                    state = States.DESTROY_NECKLACE
                    return
                }
                if(client.getInventoryItem(ItemID.STAMINA_POTION1) != null) {
                    state = States.DRINK_STAMINA
                    return
                }
                return
            }
            if (client.mapRegions.contains(13107) && !client.localPlayer!!.isMoving) {
                state = States.ENTER_RUINS
                return
            }
            if (attributes["emptied"]!! >= 1 && client.findGameObject(BANK) != null) {
                state = States.OPEN_BANK
                reset()
                return
            }
            if (attributes["emptied"]!! >= 1 && state == States.CRAFT_RUNES) {
                state = States.TELEPORT_TO_BANK
                return
            }
            if (client.mapRegions.contains(10315) && state != States.CRAFT_RUNES && client.getVarbitValue(5438) == 0) {
                state = States.CRAFT_RUNES
                return
            }
            if (client.mapRegions.contains(10315) && client.getVarbitValue(5438) == 0) {
                state = States.IMBUE
                return
            }
            if (state == States.TELEPORT_FROM_BANK) {
                return
            }
        }

        if (client.banking()) {
            if(attributes["filled"] == 1 && client.getInventorySpace() <= 0) {
                state = States.CLOSE_BANK
                return
            }
            if(client.getBankInventoryItem(ItemID.LAVA_RUNE) != null) {
                state = States.NEED_DEPOSIT
                return
            }
            if(attributes["charges"] == 0) {
                state = States.NEED_NECKLACE
                repaired = false
                return
            }
            if (attributes["stamina"] == 0 && client.getVarbitValue(25) == 0) {
                state = States.NEED_STAMINA
                return
            }
            if(client.getBankInventoryItem(ItemID.PURE_ESSENCE) == null) {
                state = States.NEED_ESSENCE
                return
            }
            if(client.getBankInventoryItem(ItemID.PURE_ESSENCE) != null) {
                if (attributes["fill"]!! >= 2) {
                    attributes["filled"] = 1
                    state = States.NEED_ESSENCE
                    return
                }
                state = States.FILL_POUCH
                return
            }
        }
    }


}