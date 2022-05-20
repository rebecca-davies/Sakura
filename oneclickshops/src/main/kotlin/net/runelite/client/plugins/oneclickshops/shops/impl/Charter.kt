package net.runelite.client.plugins.oneclickshops.shops.impl

import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.OneClickShopsPlugin
import net.runelite.client.plugins.oneclickshops.OneClickShopsConfig
import net.runelite.client.plugins.oneclickshops.States
import net.runelite.client.plugins.oneclickshops.api.entry.Entries
import net.runelite.client.plugins.oneclickshops.api.inventory.Inventory
import net.runelite.client.plugins.oneclickshops.client.findNpc
import net.runelite.client.plugins.oneclickshops.client.shopping
import net.runelite.client.plugins.oneclickshops.shops.Shop
import net.runelite.client.plugins.oneclickwintertodt.magic.shop
import javax.inject.Inject

class Charter : Shop {

    @Inject
    lateinit var inventories: Inventory

    @Inject
    lateinit var events: Entries

    @Inject
    lateinit var plugin: OneClickShopsPlugin

    @Inject
    lateinit var config: OneClickShopsConfig

    override fun handleLogic() {
        with(inventories) {
            val npc = client.findNpc(config.shop().npc.toList())

            if(WidgetInfo.INVENTORY.freeSpace() <= 0) {
                plugin.state =States.BANK
                return
            }

            if(client.shopping() && shop.contains(plugin.items)) {
                plugin.state = States.BUY
                return
            }

            if(npc != null) {
                plugin.state = States.TRADE_NPC
                return
            }
        }
    }

    override fun handleProcess() {
        TODO("Not yet implemented")
    }
}