package net.runelite.client

import com.google.inject.Provides
import net.runelite.api.*
import net.runelite.api.events.ChatMessage
import net.runelite.api.events.MenuOptionClicked
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.events.ConfigChanged
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.oneclicklavas.*
import net.runelite.client.plugins.oneclicklavas.api.entry.Entries
import net.runelite.client.plugins.oneclicklavas.api.inventory.Inventory
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
    lateinit var entries: Entries

    @Inject
    lateinit var inventories: Inventory

    companion object : Log()

    var attributes = linkedMapOf("charges" to 0, "fill" to 0, "filled" to 0, "emptied" to 0, "stamina" to 0, "repair" to 0)
    private var process = true
    var repaired = false
    private var energyPot = 0
    private lateinit var bankTeleport: List<Int>
    private lateinit var altarTeleport: List<Int>

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
        energyPot = config.stamina().itemId
        bankTeleport = config.banking().items
        altarTeleport = config.altar().items
    }

    private var items: Array<Item> by Delegates.observable(arrayOf()) { _, prev, curr ->
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
    private fun onConfigChanged(event: ConfigChanged) {
        energyPot = config.stamina().itemId
        bankTeleport = config.banking().items
        altarTeleport = config.altar().items
    }

    @Subscribe
    fun onMenuEntryClicked(event: MenuOptionClicked) {
        with(inventories) {
            with(entries) {
                checkStates()
                    //client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "State = $state Processing = $process ${config.banking()}  ${!WidgetInfo.EQUIPMENT_RING.contains(bankTeleport)}", "")
                client.getItemContainer(InventoryID.INVENTORY.id)?.let {
                    items = it.items
                }
                if (attributes["repair"]!! >= 1) {
                    event.handleMage()
                    return
                }
                if (!process || event.menuOption.contains("walk here", true)) {
                    event.consume()
                }
                process = false

                val bank = client.findGameObject("Bank chest")
                val ruin = client.findGameObject(RUINS)
                val altar = client.findGameObject(ALTAR)

                when (state) {
                    States.TELEPORT_TO_BANK -> {
                        when (config.banking()) {
                            BankTeleport.CRAFTING_CAPE -> {
                                bankTeleport.forEach {
                                    if (InventoryID.EQUIPMENT.wearing(it)) {
                                        event.teleport(3, 25362448)
                                        return
                                    }
                                }

                            }
                            BankTeleport.RING_OF_DUELING -> {
                                bankTeleport.forEach {
                                    if (InventoryID.EQUIPMENT.wearing(it)) {
                                        event.teleport(3, 25362456)
                                        return
                                    }
                                }
                            }
                        }
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
                        client.getBankItem(energyPot)?.let {
                            event.clickItem(it, 2, WidgetInfo.BANK_ITEM_CONTAINER.id)
                            return
                        }
                    }
                    States.NEED_RING -> {
                        client.getBankItem(ItemID.RING_OF_DUELING8)?.let {
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
                    States.EQUIP_RING -> {
                        client.getInventoryItem(ItemID.RING_OF_DUELING8)?.let {
                            event.clickItem(it, 3, WidgetInfo.INVENTORY.id)
                        }
                        state = States.TELEPORT_FROM_BANK
                    }
                    States.CLOSE_BANK -> {
                        event.closeBank()
                        if(!InventoryID.EQUIPMENT.contains(altarTeleport)){
                            state = States.EQUIP_RING
                            return
                        }
                        state = States.TELEPORT_FROM_BANK
                        return
                    }
                    States.TELEPORT_FROM_BANK -> {
                        when (config.altar()) {
                            AltarTeleport.RING_OF_DUELING -> {
                                altarTeleport.forEach {
                                    if (InventoryID.EQUIPMENT.wearing(it)) {
                                        event.teleport(2, 25362456)
                                        return
                                    }
                                }

                            }
                            AltarTeleport.RING_OF_THE_ELEMENTS -> {
                                altarTeleport.forEach {
                                    if (InventoryID.EQUIPMENT.wearing(it)) {
                                        event.teleport(6, 25362456)
                                        return
                                    }
                                }
                            }
                        }
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
                        client.getInventoryItem(energyPot)?.let {
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
        if (event.menuOption.equals("Walk here", ignoreCase = true)) {
            log.info("Consuming walk")
            event.consume()
            return;
        }
    }

    private fun checkStates() {
        with(inventories) {
                if (!client.banking()) {
                    if (!repaired && attributes["repair"] == 0) {
                        attributes["repair"] = 1
                        return
                    }
                    if (client.mapRegions.contains(13107) && state == States.ENTER_RUINS) {
                        if (client.getInventoryItem(ItemID.BINDING_NECKLACE) != null) {
                            state = States.DESTROY_NECKLACE
                            return
                        }
                        if (client.getInventoryItem(energyPot) != null) {
                            state = States.DRINK_STAMINA
                            return
                        }
                        return
                    }
                    if (client.mapRegions.contains(13107) && !client.localPlayer!!.isMoving) {
                        state = States.ENTER_RUINS
                        return
                    }
                    if (attributes["emptied"]!! >= 1 && client.findGameObject("Bank chest") != null) {
                        state = States.OPEN_BANK
                        reset()
                        return
                    }
                    if (attributes["emptied"]!! >= 1 && state == States.CRAFT_RUNES && client.localPlayer!!.animation != 714) {
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
                if (attributes["filled"] == 1 && client.getInventorySpace() <= 0) {
                    state = States.CLOSE_BANK
                    return
                }
                if (client.getBankInventoryItem(ItemID.LAVA_RUNE) != null) {
                    state = States.NEED_DEPOSIT
                    return
                }
                if ((config.altar() == AltarTeleport.RING_OF_DUELING && !InventoryID.EQUIPMENT.contains(altarTeleport) && !WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.contains(altarTeleport)) || (config.banking() == BankTeleport.RING_OF_DUELING && !InventoryID.EQUIPMENT.contains(bankTeleport) && !WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.contains(bankTeleport))) {
                    state = States.NEED_RING
                    return
                }
                if (attributes["charges"] == 0) {
                    state = States.NEED_NECKLACE
                    repaired = false
                    return
                }
                if (config.stamina() != RunEnergy.NONE && attributes["stamina"] == 0 && client.energy <= 70) {
                    state = States.NEED_STAMINA
                    return
                }
                if (client.getBankInventoryItem(ItemID.PURE_ESSENCE) == null) {
                    state = States.NEED_ESSENCE
                    return
                }
                if (client.getBankInventoryItem(ItemID.PURE_ESSENCE) != null) {
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
}