package net.runelite.client

import com.google.inject.Provides
import net.runelite.api.*
import net.runelite.api.events.GameTick
import net.runelite.api.events.MenuOptionClicked
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.events.ConfigChanged
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.oneclickshops.States
import net.runelite.client.plugins.oneclickshops.util.Log
import net.runelite.client.plugins.oneclickshops.OneClickConstructionConfig
import net.runelite.client.plugins.oneclickshops.api.entry.Entries
import net.runelite.client.plugins.oneclickshops.client.findGameObject
import org.pf4j.Extension
import javax.inject.Inject
import kotlin.properties.Delegates
import kotlin.reflect.KClass

@Extension
@PluginDescriptor(
    name = "One Click Construction",
    description = ":Prayje:",
    tags = ["rebecca, oneclick, one click, construction"]
)
class OneClickConstructionPlugin : Plugin() {



    @Inject
    private lateinit var config: OneClickConstructionConfig

    @Inject
    lateinit var client: Client

    @Inject
    lateinit var entries: Entries

    companion object : Log()

    @Provides
    fun provideConfig(configManager: ConfigManager): OneClickConstructionConfig {
        return configManager.getConfig(OneClickConstructionConfig::class.java)
    }

    var performAction = true
    lateinit var method: OneClickConstructionConfig.Constructables
    private var buildable: GameObject? = null
    private var built: GameObject? = null

    override fun startUp() {
        log.info("Starting One Click Construction")
        reset()
    }

    override fun shutDown() {
        log.info("Stopping One Click Construction")
    }

    private fun reset() {
        performAction = true
        method = config.method()
    }

    var state by Delegates.observable(States.IDLE) { property, previous, current ->
        if (previous != current) {
            performAction = true
        }
    }

    @Subscribe
    private fun onConfigChanged(event: ConfigChanged) {
        method = config.method()
    }

    @Subscribe
    private fun onGameTick(event: GameTick) {
        buildable = client.findGameObject(method.buildable)
        built = client.findGameObject(method.built)
    }

    @Subscribe
    fun onMenuEntryClicked(event: MenuOptionClicked) {
        handleLogic()
        client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "$state ${buildable?.name} ${built?.name} ${method.built} ${method.buildable} ${method.type}", "")
        if(!performAction) {
            event.consume()
        }
        performAction = false

        with(entries) {
            when (state) {
                States.BUILD -> {
                    buildable?.let {
                        event.interact(it)
                        return
                    }
                }
                States.REMOVE -> {
                    built?.let {
                        event.interact(it)
                        return
                    }
                }
                else -> {}
            }
        }

    }

    private fun handleLogic() {
       if(buildable != null) {
           state = States.BUILD
           return
       }
        if(built != null) {
            state = States.REMOVE
            return
        }
    }
}