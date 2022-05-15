package net.runelite.client

import com.google.inject.Provides
import net.runelite.api.*
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.ChatMessage
import net.runelite.api.events.HitsplatApplied
import net.runelite.api.events.MenuOptionClicked
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.loginscreen.LoginScreenConfig
import net.runelite.client.plugins.oneclicklavas.*
import net.runelite.client.plugins.oneclicklavas.magic.*
import net.runelite.client.plugins.oneclicklavas.util.*
import net.runelite.client.plugins.zeahcrafter.OneClickWintertodtConfig
import org.pf4j.Extension
import javax.inject.Inject
import kotlin.properties.Delegates

@Extension
@PluginDescriptor(
    name = "One Click Wintertodt",
    description = ":Prayje:",
    tags = ["rebecca, oneclick, one click, wintertodt"]
)
class OneClickWintertodtPlugin : Plugin() {

    @Inject
    lateinit var config: OneClickWintertodtConfig

    @Inject
    lateinit var client: Client

    @Inject
    lateinit var actions: Actions

    companion object : Log()

    var attributes = linkedMapOf("restock" to 0)
    private var process = true
    private var gameStarted = true

    @Provides
    fun provideConfig(configManager: ConfigManager): OneClickWintertodtConfig {
        return configManager.getConfig(OneClickWintertodtConfig::class.java)
    }

    override fun startUp() {
        log.info("Starting One Click Wintertodt")
        reset()
    }

    override fun shutDown() {
        log.info("Stopping One Click Wintertodt")
    }

    private fun reset() {
        process = true
        gameStarted = false
        attributes["restock"] = 0
        state = States.IDLE
    }

    private var items: Array<Item> by Delegates.observable(arrayOf()) { _, prev, curr ->
        if(!prev.contentEquals(curr)) {
        }
    }

    private var state by Delegates.observable(States.IDLE) { _, prev, curr ->
        if (prev != curr) {
            process = true
        }
    }

    @Subscribe
    fun onChatMessage(event: ChatMessage) {
    }

    @Subscribe
    fun onMenuEntryClicked(event: MenuOptionClicked) {
        with(actions) {
            checkStates()
            println("$state")
            client.getItemContainer(InventoryID.INVENTORY.id)?.let {
                items = it.items
            }
            if (!process) {
                event.consume()
            }
            process = false

            val bank = client.findGameObject(BANK)
            val door = client.findGameObject(DOOR)
            val hammerCrate = client.findGameObject(HAMMER_CRATE)
            val tinderboxCrate = client.findGameObject(TINDERBOX_CRATE)
            val knifeCrate = client.findGameObject(KNIFE_CRATE)
            val unlit = client.findGameObject(UNLIT_BRAZIER)?.takeIf { it.worldLocation == SE }
            val lit = client.findGameObject(LIT_BRAZIER)?.takeIf { it.worldLocation == SE }
            val roots = client.findGameObject(ROOT)?.takeIf { it.worldLocation == SE_ROOT }


            when (state) {
                States.WITHDRAW_FOOD -> {
                    client.getBankItem(ItemID.SHARK)?.let {
                        event.clickItem(it, 4, WidgetInfo.BANK_ITEM_CONTAINER.id)
                        return
                    }
                }
                States.DEPOSIT_ITEMS -> {
                    client.getBankItem(ItemID.SUPPLY_CRATE)?.let {
                        event.clickItem(it, 4, WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.id)
                        return
                    }
                }
                States.EAT -> {
                    val shark = client.getInventoryItem(ItemID.SHARK)
                    if(shark != null) {
                        event.clickItem(shark, 2, WidgetInfo.INVENTORY.id)
                        return
                    }
                    state = States.PREPARE
                    return
                }
                States.RETURN_INSIDE -> {
                    door?.let {
                        event.use(it)
                        state = States.IDLE
                        return
                    }
                }
                States.PREPARE -> {
                    door?.let {
                        event.use(it)
                        state = States.CONFIRM
                        return
                    }
                }
                States.CONFIRM -> {
                    event.talk(1, 14352385)
                    return
                }
                States.NEED_HAMMER -> {
                    event.use(hammerCrate!!)
                    return
                }
                States.NEED_KNIFE -> {
                    event.use(knifeCrate!!)
                    return
                }
                States.NEED_TINDERBOX -> {
                    event.use(tinderboxCrate!!)
                    return
                }
                States.LIGHT_BRAZIER, States.GO_TO_BRAZIER -> {
                    unlit?.let {
                        event.use(it)
                        return
                    }
                }
                States.FIREMAKING -> {
                    lit?.let {
                        event.use(it)
                        return
                    }
                }
                States.WOODCUTTING -> {
                    roots?.let {
                        event.use(it)
                        return
                    }
                }
                States.FLETCHING -> {
                    client.getInventoryItem(LOG)?.let {
                        event.useOn(it)
                        return
                    }
                }
                States.BANK -> {
                    bank?.let {
                        event.use(it)
                        return
                    }
                }
                else -> return
            }
        }
    }

    private fun onHitsplatApplied(event: HitsplatApplied) {
        if(event.actor == client.localPlayer) {
            process = true
            return
        }
    }

    private fun checkStates() {
        val unlit = client.findGameObject(UNLIT_BRAZIER)?.takeIf { it.worldLocation == SE }
        val lit = client.findGameObject(LIT_BRAZIER)?.takeIf { it.worldLocation == SE }
        val bank = client.findGameObject(BANK)

        if(state == States.PREPARE) {
            return
        }

        if(client.getBoostedSkillLevel(Skill.HITPOINTS) <= (client.getRealSkillLevel(Skill.HITPOINTS) / 2.5) && client.getInventoryItem(ItemID.SHARK) != null) {
            state = States.EAT
            return
        }

        when(client.localPlayer!!.worldLocation.regionID) {
            BANK_REGION -> {
                if(client.banking()) {
                    if(client.getBankInventoryItem(ItemID.SHARK) == null) {
                        state = States.WITHDRAW_FOOD
                        return
                    }
                    if(client.getBankInventoryItem(ItemID.SUPPLY_CRATE) != null) {
                        state = States.DEPOSIT_ITEMS
                        return
                    }
                }
                bank?.let {
                    if(client.getInventoryItem(ItemID.SHARK) == null || client.getInventoryItem(ItemID.SUPPLY_CRATE) != null) {
                        state = States.BANK
                        return
                    }
                }
                state = States.RETURN_INSIDE
                return
            }
            LOBBY_REGION -> {
                if(state == States.CONFIRM) {
                    if(client.getWidget(14352385) != null) {
                        process = true
                    } else {
                        return
                    }
                    return
                }
                if(state == States.LIGHT_BRAZIER && unlit != null) {
                    return
                }
                if(client.getInventoryItem(ItemID.HAMMER) == null) {
                    state = States.NEED_HAMMER
                    return
                }
                if(client.getInventoryItem(ItemID.KNIFE) == null) {
                    state = States.NEED_KNIFE
                    return
                }
                if(client.getInventoryItem(ItemID.TINDERBOX) == null) {
                    state = States.NEED_TINDERBOX
                    return
                }
                if(gameStarted && state == States.GO_TO_BRAZIER && client.getWidget(INTERFACE_TEXT)!!.text.isEmpty()) {
                    state = States.LIGHT_BRAZIER
                    return
                }
                if(client.getWidget(INTERFACE_TEXT)?.text!!.contains("0:00", true)) {
                    gameStarted = true
                    return
                }
                if(client.getWidget(INTERFACE_TEXT)?.text!!.contains("returns in", true)) {
                    state = States.GO_TO_BRAZIER
                    gameStarted = false
                    return
                }
                if(unlit != null && (state == States.FIREMAKING || state == States.GO_TO_BRAZIER)) {
                    state = States.LIGHT_BRAZIER
                    return
                }
                if(client.getInventoryItem(KINDLING) != null && client.getInventoryItem(LOG) == null) {
                    state = States.FIREMAKING
                    return
                }
                if((state == States.WOODCUTTING || state == States.FLETCHING) && client.localPlayer!!.animation == -1) {
                    process = true
                    return
                }
                if(client.getInventoryItem(LOG) != null && (client.getInventorySpace() <= 0 || (client.inventoryQuantity(LOG) + client.inventoryQuantity(KINDLING)) >= 10)) {
                    state = States.FLETCHING
                    return
                }
                if(client.getInventoryItem(LOG) == null && client.getInventoryItem(KINDLING) == null) {
                    state = States.WOODCUTTING
                    return
                }
                state = States.WOODCUTTING
                return
            }
        }
    }
}


