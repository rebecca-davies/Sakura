package net.runelite.client.plugins.oneclickcakes

import com.google.inject.Provides
import net.runelite.api.*
import net.runelite.api.events.GameTick
import net.runelite.api.events.MenuOptionClicked
import net.runelite.api.widgets.WidgetInfo
import net.runelite.api.widgets.WidgetInfo.INVENTORY as inventory
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.events.ConfigChanged
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.oneclickcakes.api.entry.Entries
import net.runelite.client.plugins.oneclickcakes.api.inventory.Inventory
import net.runelite.client.plugins.oneclickcakes.client.banking
import net.runelite.client.plugins.oneclickcakes.client.findGameObject
import net.runelite.client.plugins.oneclickcakes.util.*
import org.pf4j.Extension
import javax.inject.Inject
import kotlin.properties.Delegates
import net.runelite.api.widgets.WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER as bankInventory


@Extension
@PluginDescriptor(
    name = "One Click Cakes",
    description = ":Prayje:",
    tags = ["rebecca, oneclick, one click, cakes"]
)
class OneClickCakesPlugin : Plugin() {

    @Inject
    private lateinit var config: OneClickCakesPlugin

    @Inject
    lateinit var client: Client

    @Inject
    lateinit var events: Entries

    @Inject
    lateinit var inventories: Inventory

    companion object : Log()
    private var bankBooth: GameObject? = null
    private var cakeStall: GameObject? = null

    @Provides
    fun provideConfig(configManager: ConfigManager): OneClickCakesConfig {
        return configManager.getConfig(OneClickCakesConfig::class.java)
    }

    var performAction = true

    override fun startUp() {
        log.info("Starting One Click Cakes")
        reset()
    }

    override fun shutDown() {
        log.info("Stopping One Click Cakes")
    }

    private fun reset() {
        performAction = true
    }

    private var state by Delegates.observable(States.IDLE) { _, previous, current ->
        if (previous != current) {
            performAction = true
        }
    }

    @Subscribe
    private fun onConfigChanged(event: ConfigChanged) {
    }

    @Subscribe
    private fun onGameTick(event: GameTick) {
        bankBooth = client.findGameObject(BANK_BOOTH).takeIf { it?.worldLocation == BANK_BOOTH_POS }
        cakeStall = client.findGameObject(CAKE_STALL).takeIf { it?.worldLocation?.distanceTo(client.localPlayer.worldLocation)!! <= 2}
    }

    @Subscribe
    fun onMenuEntryClicked(event: MenuOptionClicked) {
        with(inventories) {
            with(events) {
                handleLogic()
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "state = $state performAction = $performAction", "")
                if(!performAction) {
                    event.consume()
                    return
                }
                performAction = false
                when(state) {
                    States.STEAL -> {
                        performAction = true
                        cakeStall?.let {
                            event.use(it, MenuAction.GAME_OBJECT_SECOND_OPTION)
                            return
                        }
                        return
                    }
                    States.RUN_TO_STALL -> {
                        event.walkTo(THIEVING_SPOT)
                        return
                    }
                    States.RUN_TO_BANK -> {
                        event.walkNear(BANK_AREA)
                        return
                    }
                    States.USE_BANK -> {
                        bankBooth?.let {
                            event.use(it, MenuAction.GAME_OBJECT_SECOND_OPTION)
                            return
                        }
                        return
                    }
                    States.DEPOSIT -> {
                        event.click(-1, 786474)
                        return
                    }
                    States.CLOSE_BANK -> {
                        event.closeBank()
                        return
                    }
                    States.IDLE -> {}
                }
            }
        }
        if(event.menuOption.equals("Walk here", ignoreCase = true)) {
            event.consume()
            return;
        }
    }

    private fun handleLogic() {
        with(inventories) {
            when(client.localPlayer.worldLocation) {
                THIEVING_SPOT -> {
                    if(inventory.freeSpace() > 0) {
                        state = States.STEAL
                        return
                    }
                    if(inventory.freeSpace() <= 0) {
                        state = States.RUN_TO_BANK
                        return
                    }
                }
                else -> {
                    if(inventory.freeSpace() > 0) {
                        state = States.RUN_TO_STALL
                        return
                    }
                    if(inventory.freeSpace() <= 0) {
                        if(client.banking()) {
                            if(bankInventory.freeSpace() > 0) {
                                state = States.CLOSE_BANK
                                return
                            }
                            state = States.DEPOSIT
                            return
                        }
                        if(!client.banking()) {
                            bankBooth?.let {
                                state = States.USE_BANK
                                return
                            }
                        }
                    }
                }
            }
        }
    }
}