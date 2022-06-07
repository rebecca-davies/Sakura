package net.runelite.client.plugins.oneclickshops.shops.impl

import com.google.inject.Guice
import com.google.inject.Module
import net.runelite.api.Client
import net.runelite.api.MenuAction
import net.runelite.api.NPC
import net.runelite.api.events.MenuOptionClicked
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
import javax.inject.Named

class Charter : Shop {

    var npc: NPC? = null
    override lateinit var events: Entries
    override lateinit var inventories: Inventory
    override lateinit var plugin: OneClickShopsPlugin
    override lateinit var config: OneClickShopsConfig

    override fun handleLogic() {
        with(inventories) {
            npc = client.findNpc(config.shop().npc.toList())

            if(WidgetInfo.INVENTORY.freeSpace() <= 0) {
                plugin.state = States.BANK
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

    override fun handleEvent(event: MenuOptionClicked) {
        with(events) {
            when (plugin.state) {
                States.BANK -> {

                }
                States.TRADE_NPC -> {
                    npc?.let {
                        event.trade(it, MenuAction.NPC_FIRST_OPTION)
                        return
                    }
                }
                else -> {}
            }
        }
    }
}