package net.runelite.client

import com.google.inject.Provides
import net.runelite.api.*
import net.runelite.api.events.MenuOptionClicked
import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.oneclicklavas.*
import net.runelite.client.plugins.oneclicklavas.container.compare
import net.runelite.client.plugins.oneclicklavas.magic.ALTAR
import net.runelite.client.plugins.oneclicklavas.magic.BANK
import net.runelite.client.plugins.oneclicklavas.magic.RUINS
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

    companion object : Log()

    var attributes = linkedMapOf("binds" to 15, "fill" to 0, "filled" to 0)
    var process = true

    var items: Array<Item> by Delegates.observable(arrayOf()) { _, prev, curr ->
        if(!prev.contentEquals(curr)) {
            if(state == States.FILL_POUCH) {
                attributes.computeIfPresent("fill") { k, v -> v + 1 }
            }
        }
    }

    var state by Delegates.observable(States.OPEN_BANK) { _, prev, curr ->
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
        reset()
    }

    override fun shutDown() {
        log.info("Stopping One click lavas")
    }

    private fun reset() {
        attributes["binds"] = 15
        attributes["fill"] = 0
        attributes["filled"] = 0
        process = true
        state = States.OPEN_BANK
    }

    @Subscribe
    fun onMenuEntryClicked(event: MenuOptionClicked) {
        client.getItemContainer(InventoryID.INVENTORY.id)?.let {
            this.items = it.items

        }
        if (!process) {
            event.consume()
            return
        }
        log.info("$state $process ${attributes["filled"]}")
        process = false
        val bank = client.findGameObject(BANK)
        val ruin = client.findGameObject(RUINS)
        val altar = client.findGameObject(ALTAR)
        checkStates()
        when (state) {
            States.OPEN_BANK -> {
                bank?.let {
                    event.set(use(it)!!)
                    return
                }
            }
            States.NEED_ESSENCE -> {
                client.getBankItem(ItemID.PURE_ESSENCE)?.let {
                    event.set(it, 1, WidgetInfo.BANK_ITEM_CONTAINER.id)
                    return
                }
            }
            States.FILL_POUCH -> {
                client.getBankInventoryItem(ItemID.COLOSSAL_POUCH)?.let {
                    event.set(it, 9, WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.id)
                    return
                }
            }
            States.CLOSE_BANK -> {
                event.set(closeBank()!!)
                state = States.TELEPORT_FROM_BANK
                return
            }
            States.TELEPORT_FROM_BANK -> {
                event.set(teleportToAltar()!!)
                return
            }
            States.ENTER_ALTAR -> {
                ruin?.let {
                    event.set(use(it)!!)
                    return
                }
            }
            States.IMBUE -> {
                event.set(castImbue()!!)
                state = States.CRAFT_RUNES
                return
            }
            States.CRAFT_RUNES -> {
                if(client.getInventoryItem(ItemID.PURE_ESSENCE) != null) {
                    altar?.let { event.set(useOn(it)!!) }
                    return
                }
                if(client.getInventoryItem(ItemID.PURE_ESSENCE) == null) {
                    state = States.EMPTY_POUCHES
                    return
                }
            }
            else -> return
        }
    }

    private fun checkStates() {
        if (client.mapRegions.contains(13107)) {
            state = States.ENTER_ALTAR
            return
        }
        if (client.mapRegions.contains(10315) && client.getVarbitValue(5438) == 0) {
            state = States.IMBUE
            return
        }
        if (state == States.TELEPORT_FROM_BANK) {
            return
        }
        if (client.banking()) {
            if(client.getBankInventoryItem(ItemID.PURE_ESSENCE) == null) {
                state = States.NEED_ESSENCE
                return
            }
            if(client.getBankInventoryItem(ItemID.PURE_ESSENCE) != null) {
                if (attributes["fill"]!! >= 2) {
                    if(attributes["filled"] == 1 && client.getInventorySpace() <= 0) {
                        state = States.CLOSE_BANK
                        return
                    }
                    state = States.NEED_ESSENCE
                    attributes["filled"] = 1
                    return
                }
                state = States.FILL_POUCH
                return
            }
        }
    }

    private fun MenuOptionClicked.set(entry: MenuEntry) {
        try {
            this.menuOption = entry.option
            this.menuTarget = entry.target
            this.id = entry.identifier
            this.menuAction = entry.type
            this.param0 = entry.param0
            this.param1 = entry.param1
        } catch (e: Exception) {
            this.consume()
        }
    }

    private fun MenuOptionClicked.set(item: Widget, action: Int, container: Int) {
        try {
            this.menuOption = ""
            this.menuTarget = ""
            this.id = action
            this.menuAction = if (action < 6) MenuAction.CC_OP else MenuAction.CC_OP_LOW_PRIORITY
            this.param0 = item.index
            this.param1 = container
        } catch (e: Exception) {
            this.consume()
        }
    }

    private fun closeBank(): MenuEntry? {
        return client.createMenuEntry(
            "Close",
            "",
            1,
            MenuAction.CC_OP.id,
            11,
            786434,
            false
        )
    }

    private fun teleportToAltar(): MenuEntry? {
        return client.createMenuEntry(
            "Fire Altar",
            "<col=ff9040>Ring of the elements</col>",
            6,
            MenuAction.CC_OP_LOW_PRIORITY.id,
            -1,
            25362456,
            false
        )
    }

    private fun teleportToBank(): MenuEntry? {
        return client.createMenuEntry(
            "Teleport",
            "<col=ff9040>Crafting cape(t)</col>",
            3,
            MenuAction.CC_OP.id,
            -1,
            25362448,
            false
        )
    }

    private fun castImbue(): MenuEntry? {
        return client.createMenuEntry(
            "Cast",
            "<col=00ff00>Magic Imbue</col>",
            1,
            MenuAction.CC_OP.id,
            -1,
            14286973,
            false
        )
    }

    private fun use(gameObject: GameObject): MenuEntry? {
        return client.createMenuEntry(
            "",
            "",
            gameObject.id,
            MenuAction.GAME_OBJECT_FIRST_OPTION.id,
            gameObject.sceneMinLocation.x,
            gameObject.sceneMinLocation.y,
            false
        )
    }

    private fun destroyNecklace(): MenuEntry? {
        return client.createMenuEntry(
            "Destroy",
            "<col=ff9040>Binding necklace</col>",
            7,
            MenuAction.CC_OP_LOW_PRIORITY.id,
            2,
            9764864,
            false
        )
    }

    private fun confirm(): MenuEntry? {
        return client.createMenuEntry(
            "Yes",
            "",
            1,
            MenuAction.CC_OP.id,
            -1,
            38273025,
            false
        )
    }

    private fun dead(): MenuEntry? {
        return client.createMenuEntry(
            "Cancel",
            "",
            14886,
            MenuAction.CANCEL.id,
            56,
            48,
            false
        )
    }

    private fun useOn(gameObject: GameObject): MenuEntry? {
        val runes = client.getInventoryItem(ItemID.EARTH_RUNE)!!
        client.selectedSpellWidget = runes.id
        client.selectedSpellChildIndex = runes.index
        client.selectedSpellItemId = runes.itemId
        return client.createMenuEntry(
            "Use",
            "",
            gameObject.id,
            MenuAction.WIDGET_TARGET_ON_GAME_OBJECT.id,
            gameObject.sceneMinLocation.x,
            gameObject.sceneMinLocation.y,
            false
        )
    }

    private fun repair(gameObject: GameObject): MenuEntry? {
        val runes = client.getInventoryItem(ItemID.EARTH_RUNE)!!
        client.selectedSpellWidget = runes.id
        client.selectedSpellChildIndex = runes.index
        client.selectedSpellItemId = runes.itemId
        return client.createMenuEntry(
            "Use",
            "",
            gameObject.id,
            MenuAction.WIDGET_TARGET_ON_GAME_OBJECT.id,
            gameObject.sceneMinLocation.x,
            gameObject.sceneMinLocation.y,
            false
        )
    }
}
/*
        when(instructions) {
            States.TELEPORT_TO_BANK -> {
                client.getBankItem(ItemID.PURE_ESSENCE)?.let { return }
                setEntry(event, teleportToBank()!!)
                instructions = States.OPEN_BANK
                return
            }
            States.PREPARE_TO_TELEPORT -> {
                    if(bindsUsed == -1) {
                        setEntry(event, destroyNecklace()!!)
                        instructions = States.DESTROY_NECKLACE
                        return
                    }
                    instructions = States.TELEPORT_FROM_BANK
                    return
            }
            States.DESTROY_NECKLACE -> {
                if(client.getInventoryItem(ItemID.BINDING_NECKLACE) == null) {
                    bindsUsed = 0
                    instructions = States.TELEPORT_FROM_BANK
                    return
                }
                if(bindsUsed == -1) {
                    setEntry(event, confirm()!!)
                    return
                }
                instructions = States.TELEPORT_FROM_BANK
                return
            }
            States.TELEPORT_FROM_BANK -> {
                setEntry(event, teleportToAltar()!!)
                instructions = States.ENTER_ALTAR
                return
            }
            States.ENTER_ALTAR -> {
                ruin?.let { setEntry(event, use(ruin)!!) }
                return
            }
            States.OPEN_BANK -> {
                bank?.let { setEntry(event, use(bank)!!) }
                return
            }
            States.IMBUE -> {
                setEntry(event,castImbue()!!)
                instructions = States.CRAFT_RUNES
                return
            }
            States.CRAFT_RUNES -> {
                if(client.getInventoryItem(ItemID.PURE_ESSENCE) != null) {
                    altar?.let { setEntry(event, useOn(altar)!!) }
                    return
                }
                if(client.getInventoryItem(ItemID.PURE_ESSENCE) == null) {
                    instructions = States.EMPTY_POUCHES
                    return
                }
            }
            States.EMPTY_POUCHES -> {
                val pouch = client.getInventoryItem(ItemID.COLOSSAL_POUCH)!!
                setEntry(event, itemEntry(pouch, 3, WidgetInfo.INVENTORY.id)!!)
                return
            }
        }
        if (client.banking()) {
            when (instructions) {
                States.NEED_NECKLACE -> {
                    val necklace = client.getBankItem(ItemID.BINDING_NECKLACE)!!
                    setEntry(event, itemEntry(necklace, 2, WidgetInfo.BANK_ITEM_CONTAINER.id)!!)
                    bindsUsed = -1
                    return
                }
                States.NEED_DEPOSIT -> {
                    val lavas = client.getBankInventoryItem(ItemID.LAVA_RUNE)!!
                    setEntry(event, itemEntry(lavas, 2, WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.id)!!)
                    return
                }
                States.NEED_ESSENCE -> {
                    val ess = client.getBankItem(ItemID.PURE_ESSENCE)!!
                    setEntry(event, itemEntry(ess, 1, WidgetInfo.BANK_ITEM_CONTAINER.id)!!)
                    itemContainer = client.getItemContainer(InventoryID.INVENTORY.id)!!.items
                    return
                }
                States.FILL_POUCH -> {
                    val pouch = client.getBankInventoryItem(ItemID.COLOSSAL_POUCH)!!
                    setEntry(event, itemEntry(pouch, 9, WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.id)!!)
                    return
                }
                States.CLOSE_BANK -> {
                    setEntry(event, closeBank()!!)
                    instructions = States.PREPARE_TO_TELEPORT
                    return
                }
            }

            /* if (bindsUsed >= 15) {
        }
        if (instructions == States.TELEPORT_FROM_BANK) {
            return
        }
        if (client.mapRegions.contains(13107)) {
            instructions = States.ENTER_ALTAR
            return
        }
        if (client.mapRegions.contains(10315) && client.getVarbitValue(5438) == 0) {
            instructions = States.IMBUE
            return
        }
        if (!client.banking()) {
            if (instructions == States.NEED_ESSENCE && filled || instructions == States.CLOSE_BANK && filled) {
                instructions = States.PREPARE_TO_TELEPORT
                return
            }
        }

        if (client.banking()) {
            if (bindsUsed >= 15) {
                instructions = States.NEED_NECKLACE
                return
            }
            if (client.getBankInventoryItem(ItemID.LAVA_RUNE) != null) {
                instructions = States.NEED_DEPOSIT
                return
            }
            if (client.getBankInventoryItem(ItemID.PURE_ESSENCE) == null) {
                instructions = States.NEED_ESSENCE
                return
            }
            if (client.getBankInventoryItem(ItemID.PURE_ESSENCE) != null) {
                if (filledStep >= 2 && client.getInventorySpace() > 0 && !filled) {
                    instructions = States.NEED_ESSENCE
                    filled = true
                    return
                }
                if (filledStep < 2) {
                    instructions = States.FILL_POUCH
                    return
                }
            }
            if (filled && client.getInventorySpace() <= 0) {
                instructions = States.CLOSE_BANK
                return
            }
        }*/
         */
