package net.runelite.client

import com.google.inject.Provides
import net.runelite.api.*
import net.runelite.api.events.MenuOptionClicked
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.events.ConfigChanged
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.oneclickshops.States
import net.runelite.client.plugins.oneclickshops.util.Log
import net.runelite.client.plugins.oneclickshops.OneClickConstructionConfig
import org.pf4j.Extension
import javax.inject.Inject
import kotlin.properties.Delegates
import kotlin.reflect.KClass

@Extension
@PluginDescriptor(
    name = "One Click Shops",
    description = ":Prayje:",
    tags = ["rebecca, oneclick, one click, shops"]
)
class OneClickConstructionPlugin : Plugin() {

    @Inject
    private lateinit var config: OneClickConstructionConfig

    @Inject
    lateinit var client: Client

    companion object : Log()

    @Provides
    fun provideConfig(configManager: ConfigManager): OneClickConstructionConfig {
        return configManager.getConfig(OneClickConstructionConfig::class.java)
    }

    var performAction = true

    override fun startUp() {
        log.info("Starting One Click Shops")
        reset()
    }

    override fun shutDown() {
        log.info("Stopping One Click Shops")
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
    private fun onConfigChanged(event: ConfigChanged) {
    }

    @Subscribe
    fun onMenuEntryClicked(event: MenuOptionClicked) {
        if(!performAction) {
            event.consume()
        }
        performAction = false
    }

}