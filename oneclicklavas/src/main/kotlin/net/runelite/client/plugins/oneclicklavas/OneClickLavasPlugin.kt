package net.runelite.client

import com.google.inject.Provides
import net.runelite.api.*
import net.runelite.api.events.MenuOptionClicked
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

    var attributes = linkedMapOf("charges" to 0, "fill" to 0, "filled" to 0, "emptied" to 0)
    var process = true

    var items: Array<Item> by Delegates.observable(arrayOf()) { _, prev, curr ->
        if(!prev.contentEquals(curr)) {
            if(state == States.FILL_POUCH) {
                attributes.computeIfPresent("fill") { k, v -> v + 1 }
            }
            if(state == States.EMPTY_POUCHES) {
                attributes.computeIfPresent("emptied") { k, v -> v + 1 }
            }
        }
    }

    private var state by Delegates.observable(States.OPEN_BANK) { _, prev, curr ->
        println("$prev $curr")
        if (prev != curr) {
            process = true
        }
    }

    @Provides
    fun provideConfig(configManager: ConfigManager): OneClickLavasConfig {
        return configManager.getConfig(OneClickLavasConfig::class.java)
    }

    override fun startUp() {
        log.info("Starting One click lavas")
        attributes["charges"] = 0
        reset()
    }

    override fun shutDown() {
        log.info("Stopping One click lavas")
    }

    private fun reset() {
        attributes["fill"] = 0
        attributes["filled"] = 0
        attributes["emptied"] = 0
        process = true
        state = States.OPEN_BANK
    }

    @Subscribe
    fun onMenuEntryClicked(event: MenuOptionClicked) {
        with(actions) {
            checkStates()
            println("$state ${attributes["charges"]} $process ${client.banking()}")
            client.getItemContainer(InventoryID.INVENTORY.id)?.let {
                items = it.items
            }
            if (!process) {
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
                States.NEED_NECKLACE -> {
                    attributes["charges"] = -1
                    client.getBankItem(ItemID.BINDING_NECKLACE)?.let {
                        event.clickItem(it, 2, WidgetInfo.BANK_ITEM_CONTAINER.id)
                        state = States.NEED_ESSENCE
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
                    event.confirm()
                    state = States.TELEPORT_FROM_BANK
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
                    if (client.getInventoryItem(ItemID.PURE_ESSENCE) != null) {
                        altar?.let { event.useOn(it) }
                        return
                    }
                    if (client.getInventoryItem(ItemID.PURE_ESSENCE) == null) {
                        state = States.EMPTY_POUCHES
                        return
                    }
                    return
                }
                States.EMPTY_POUCHES -> {
                    client.getInventoryItem(ItemID.COLOSSAL_POUCH)?.let {
                        event.clickItem(it, 3, WidgetInfo.INVENTORY.id)
                        state = States.CRAFT_RUNES
                        return
                    }

                }
                else -> return
            }
        }
    }

    private fun checkStates() {
        if(!client.banking()) {
            if (client.mapRegions.contains(13107)) {
                state = States.ENTER_RUINS
                return
            }
            if (attributes["emptied"]!! >= 2 && client.getInventoryItem(ItemID.PURE_ESSENCE) == null) {
                state = States.TELEPORT_TO_BANK
                return
            }
            if (client.mapRegions.contains(10315) && client.getVarbitValue(5438) == 0) {
                state = States.IMBUE
                return
            }
            if (state == States.TELEPORT_FROM_BANK) {
                if(attributes["charges"] == -1) {
                    state = States.DESTROY_NECKLACE
                    return
                }
                return
            }
        }

        if (client.banking()) {
            if(attributes["filled"] == 1 && client.getInventorySpace() <= 0) {
                state = States.CLOSE_BANK
                return
            }
            if(attributes["charges"] == 0) {
                state = States.NEED_NECKLACE
                return
            }
            if(client.getInventoryItem(ItemID.LAVA_RUNE) != null) {
                state = States.NEED_DEPOSIT
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