package net.runelite.client

import com.google.inject.Provides
import net.runelite.api.*
import net.runelite.api.coords.LocalPoint
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.GameTick
import net.runelite.api.events.MenuOptionClicked
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.oneclickpestboarding.OneClickPestboardingPlugin
import net.runelite.client.plugins.oneclickpestboarding.States
import net.runelite.client.plugins.oneclickpestboarding.api.entry.Entries
import net.runelite.client.plugins.oneclickpestboarding.client.findGameObject
import net.runelite.client.plugins.oneclickpestboarding.util.Log
import org.pf4j.Extension
import javax.inject.Inject
import kotlin.properties.Delegates

@Extension
@PluginDescriptor(
    name = "One Click Pestboarding",
    description = ":Prayje:",
    tags = ["rebecca", "one click", "pest", "control", "boarding"]
)
class OneClickPestboardingPlugin : Plugin() {

    @Inject
    lateinit var client: Client

    @Inject
    lateinit var entries: Entries

    companion object : Log()

    @Provides
    fun provideConfig(configManager: ConfigManager): OneClickPestboardingPlugin {
        return configManager.getConfig(OneClickPestboardingPlugin::class.java)
    }

    var performAction = true
    private var bridge: GameObject? = null
    private var waitingBoatArea = WorldArea(WorldPoint(2632, 2649, 0), WorldPoint(2635, 2654, 0))

    override fun startUp() {
        log.info("Starting One Click Pestboarding")
        reset()
    }

    override fun shutDown() {
        log.info("Stopping One Click Pestboarding")
    }

    private fun reset() {
        performAction = true
    }

    var state by Delegates.observable(States.IDLE) { property, previous, current ->
        if (previous != current) {
            performAction = true
        }
    }


    @Subscribe
    private fun onGameTick(event: GameTick) {
        bridge = client.findGameObject(ObjectID.GANGPLANK_25632).takeIf { it?.worldLocation == WorldPoint(2637, 2653, 0) }
    }

    @Subscribe
    fun onMenuEntryClicked(event: MenuOptionClicked) {
        handleLogic()
        println("$state $performAction")
        if(!performAction && state != States.IDLE) {
            event.consume()
        }
        performAction = false
        with(entries) {
            when (state) {
                States.BOARD -> {
                    bridge?.let {
                        event.use(it)
                        return
                    }
                }
                States.WAIT -> {
                    return
                }
                States.RUN_TO_MIDDLE -> {
                    event.walkTo(LocalPoint(6336, 4288))
                    state = States.IDLE
                    return
                }
                else -> {}
            }
        }
        if(state != States.IDLE && event.menuOption.equals("Walk here", ignoreCase = true)){
            event.consume()
            return;
        }

    }

    private fun handleLogic() {
        if(client.localPlayer.worldLocation.isInArea(waitingBoatArea)) {
            state = States.WAIT
            return
        }
        if(client.mapRegions.contains(10537) && !client.localPlayer.worldLocation.isInArea(waitingBoatArea)) {
            state = States.BOARD
            return
        }
        if(client.localPlayer.localLocation.sceneX >= 48 && client.localPlayer.localLocation.sceneY >= 49 && client.localPlayer.localLocation.sceneX <= 51 && client.localPlayer.localLocation.sceneY <= 54) {
            state = States.RUN_TO_MIDDLE
            return
        }
    }
}