package net.runelite.client.plugins.oneclickshops.shops

import net.runelite.client.OneClickShopsPlugin
import net.runelite.client.plugins.oneclickshops.OneClickShopsConfig
import net.runelite.client.plugins.oneclickshops.api.entry.Entries
import net.runelite.client.plugins.oneclickshops.api.inventory.Inventory
import javax.inject.Inject

class ShopFactory {

    @Inject
    lateinit var inventories: Inventory

    @Inject
    lateinit var events: Entries

    @Inject
    lateinit var plugin: OneClickShopsPlugin

    @Inject
    lateinit var config: OneClickShopsConfig

    fun createInstance(clazz: String): Shop? {
        val store = Class.forName(config.shop().clazz).getConstructor().newInstance() as Shop
        store.events = this.events
        store.inventories = this.inventories
        store.plugin = this.plugin
        store.config = this.config
        return store
    }


}