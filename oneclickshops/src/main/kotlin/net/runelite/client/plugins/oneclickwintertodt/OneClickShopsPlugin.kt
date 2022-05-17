package net.runelite.client

import com.google.inject.Provides
import net.runelite.api.*
import net.runelite.api.events.GameTick
import net.runelite.api.events.HitsplatApplied
import net.runelite.api.events.MenuOptionClicked
import net.runelite.api.widgets.WidgetInfo.BANK_ITEM_CONTAINER as bank
import net.runelite.api.widgets.WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER as bankInventory
import net.runelite.api.widgets.WidgetInfo.INVENTORY as inventory
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.events.ConfigChanged
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.oneclickwintertodt.States
import net.runelite.client.plugins.oneclickwintertodt.magic.*
import net.runelite.client.plugins.oneclickwintertodt.api.entry.Entries
import net.runelite.client.plugins.oneclickwintertodt.util.Log
import net.runelite.client.plugins.oneclickwintertodt.OneClickShopsConfig
import net.runelite.client.plugins.oneclickwintertodt.api.inventory.Inventory
import net.runelite.client.plugins.oneclickwintertodt.client.*
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
    lateinit var client: Client

    @Inject
    lateinit var events: Entries

    @Inject
    lateinit var inventories: Inventory

    companion object : Log()

    @Provides
    fun provideConfig(configManager: ConfigManager): OneClickShopsConfig {
        return configManager.getConfig(OneClickShopsConfig::class.java)
    }

    var performAction = true
    private lateinit var food: OneClickShopsConfig.Food

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

    private var itemContainer: Array<Item> by Delegates.observable(arrayOf()) { property, previous, current ->
    }

    private var state by Delegates.observable(States.IDLE) { property, previous, current ->
        if (previous != current) {
            performAction = true
        }
    }

    @Subscribe
    private fun onConfigChanged(event: ConfigChanged) {
    }

    @Subscribe
    fun onMenuEntryClicked(event: MenuOptionClicked) {
        with(inventories) {
            with(events) {
                handleLogic()
                if(!performAction) {
                    event.consume()
                }
                performAction = false
            }
        }
    }

    private fun handleLogic() {
        with(inventories) {
        }
    }
}