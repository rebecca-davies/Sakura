package net.runelite.client

import com.google.inject.Guice
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
import net.runelite.client.plugins.oneclickshops.OneClickShopsConfig
import net.runelite.client.plugins.oneclickshops.api.entry.Entries
import net.runelite.client.plugins.oneclickshops.api.inventory.Inventory
import net.runelite.client.plugins.oneclickshops.data.Shops
import net.runelite.client.plugins.oneclickshops.shops.Shop
import net.runelite.client.plugins.oneclickshops.shops.ShopFactory
import org.pf4j.Extension
import javax.inject.Inject
import kotlin.properties.Delegates

@Extension
@PluginDescriptor(
    name = "One Click Shops",
    description = ":Prayje:",
    tags = ["rebecca, oneclick, one click, shops"]
)
class OneClickShopsPlugin : Plugin() {

    @Inject
    private lateinit var config: OneClickShopsConfig

    @Inject
    private lateinit var factory: ShopFactory

    companion object : Log()

    @Provides
    fun provideConfig(configManager: ConfigManager): OneClickShopsConfig {
        return configManager.getConfig(OneClickShopsConfig::class.java)
    }

    var performAction = true
    private lateinit var shop: Shops
    lateinit var items: List<Int>
    lateinit var store: Shop

    override fun startUp() {
        log.info("Starting One Click Shops")
        reset()
    }

    override fun shutDown() {
        log.info("Stopping One Click Shops")
    }

    private fun reset() {
        performAction = true
        shop = config.shop()
        items = config.items().lines().map { it.toInt() }.toList()
        store = factory.createInstance(config.shop().clazz)!!
    }

    var state by Delegates.observable(States.IDLE) { _, previous, current ->
        if (previous != current) {
            performAction = true
        }
    }

    @Subscribe
    private fun onConfigChanged(event: ConfigChanged) {
        shop = config.shop()
        items = config.items().lines().map { it.toInt() }.toList()
    }

    @Subscribe
    fun onMenuEntryClicked(event: MenuOptionClicked) {
        if(!performAction) {
            event.consume()
            return
        }
        performAction = false
        store.handleLogic()
        store.handleEvent(event)
    }

}
