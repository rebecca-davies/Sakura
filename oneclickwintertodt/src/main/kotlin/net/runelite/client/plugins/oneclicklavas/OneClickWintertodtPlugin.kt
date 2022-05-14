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
            if (!process || event.menuOption.contains("walk here", true)) {
                event.consume()
            }
            process = false

            val bank = client.findGameObject(BANK)
            val door = client.findGameObject(DOOR)
            val hammerCrate = client.findGameObject(HAMMER_CRATE)
            val tinderboxCrate = client.findGameObject(TINDERBOX_CRATE)
            val knifeCrate = client.findGameObject(KNIFE_CRATE)
            val unlit = client.findGameObject(UNLIT_BRAZIER)?.takeUnless { it.worldLocation == WorldPoint(1639, 4016, 0) || it.worldLocation == WorldPoint(1621, 4016, 0) }
            val lit = client.findGameObject(LIT_BRAZIER)?.takeUnless { it.worldLocation == WorldPoint(1639, 4016, 0) || it.worldLocation == WorldPoint(1621, 4016, 0) }
            val roots = client.findGameObject(ROOT)


            when (state) {
                States.EAT -> {
                    client.getInventoryItem(ItemID.SHARK)?.let {
                        event.clickItem(it, 2, WidgetInfo.INVENTORY.id)
                        return
                    }
                    state = States.PREPARE
                    attributes["restock"] = 1
                    return
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
        val unlit = client.findGameObject(UNLIT_BRAZIER)?.takeUnless { it.worldLocation == WorldPoint(1639, 4016, 0) || it.worldLocation == WorldPoint(1621, 4016, 0) }
        val lit = client.findGameObject(LIT_BRAZIER)?.takeUnless { it.worldLocation == WorldPoint(1639, 4016, 0) || it.worldLocation == WorldPoint(1621, 4016, 0) }
        val bank = client.findGameObject(BANK)

        if(client.getBoostedSkillLevel(Skill.HITPOINTS) <= (client.getRealSkillLevel(Skill.HITPOINTS) / 2.5)) {
            state = States.EAT
            return
        }

        if(state == States.WOODCUTTING && client.localPlayer!!.animation == -1) {
            state = States.WOODCUTTING
            process = true
            return
        }
        if(state == States.FLETCHING && client.localPlayer!!.animation == -1) {
            state = States.FLETCHING
            process = true
            return
        }

        if(client.localPlayer!!.worldLocation.regionID == LOBBY_REGION) {
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
        }

        if(!client.banking()) {
            if(client.localPlayer!!.worldLocation.regionID == BANK_REGION && attributes["restock"] == 1) {
                bank?.let {
                    state = States.BANK
                    return
                }
            }
            if(attributes["restock"] == 1 && state == States.CONFIRM && client.localPlayer!!.worldLocation.regionID == LOBBY_REGION) {
                process = true
                return
            }
            if(state == States.LIGHT_BRAZIER && client.localPlayer!!.interacting == unlit) {
                return
            }
            if(unlit != null && (state == States.FIREMAKING || state == States.GO_TO_BRAZIER) && client.localPlayer!!.interacting != lit) {
                state = States.LIGHT_BRAZIER
                return
            }
            if(client.getWidget(INTERFACE_TEXT)?.text!!.contains("0:00", true)) {
                state = States.LIGHT_BRAZIER
                gameStarted = true
                return
            }
            if(client.getWidget(INTERFACE_TEXT)?.text!!.contains("returns in", true)) {
                state = States.GO_TO_BRAZIER
                gameStarted = false
                return
            }
            if(client.getInventoryItem(KINDLING) != null && client.getInventoryItem(LOG) == null) {
                if(client.localPlayer!!.interacting == null) {
                    process = true
                    return
                }
                state = States.FIREMAKING
                return
            }
            if(state == States.FLETCHING && client.getInventoryItem(LOG) != null) {
                state = States.FLETCHING
                return
            }
            if(client.getInventoryItem(LOG) != null && (client.getInventorySpace() <= 0 || client.inventoryQuantity(LOG) >= 10)) {
                state = States.FLETCHING
                return
            }
            if(client.getInventoryItem(LOG) == null && client.getInventoryItem(KINDLING) == null) {
                state = States.WOODCUTTING
                return
            }
            if(!gameStarted && lit != null) {
                gameStarted = true
                state = States.WOODCUTTING
                process = true
                return
            }
            state = States.WOODCUTTING
            return
        }
        if (client.banking()) {
        }
    }
}


