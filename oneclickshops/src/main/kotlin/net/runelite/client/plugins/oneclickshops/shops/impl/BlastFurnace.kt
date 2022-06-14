package net.runelite.client.plugins.oneclickshops.shops.impl

import net.runelite.api.GameObject
import net.runelite.api.InventoryID
import net.runelite.api.MenuAction
import net.runelite.api.NPC
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.MenuOptionClicked
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.OneClickShopsPlugin
import net.runelite.client.callback.ClientThread
import net.runelite.client.plugins.oneclickshops.OneClickShopsConfig
import net.runelite.client.plugins.oneclickshops.States
import net.runelite.client.plugins.oneclickshops.api.entry.Entries
import net.runelite.client.plugins.oneclickshops.api.inventory.Inventory
import net.runelite.client.plugins.oneclickshops.client.*
import net.runelite.client.plugins.oneclickshops.shops.Shop
import net.runelite.client.plugins.oneclickwintertodt.magic.shop
import net.runelite.api.widgets.WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER as deposit

class BlastFurnace() : Shop {

    var npc: NPC? = null
    var bank: GameObject? = null
    var lastKnownLocation: WorldPoint? = null
    override lateinit var events: Entries
    override lateinit var inventories: Inventory
    override lateinit var plugin: OneClickShopsPlugin
    override lateinit var config: OneClickShopsConfig
    override lateinit var worldHop: WorldHop
    override lateinit var clientThread: ClientThread

    override fun handleLogic() {
        with(inventories) {
            npc = client.findNpc(config.shop().npc.toList())
            bank = client.findGameObject("Bank chest")

            if(client.banking() && WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.contains(plugin.items) ) {
                plugin.state = States.DEPOSIT
                return
            }
            if(!client.banking() && client.getItemContainer(InventoryID.INVENTORY)?.freeSpace() == 0) {
                plugin.state = States.BANK
                return
            }
            if(!client.banking() && plugin.readyToHop && client.getItemContainer(InventoryID.INVENTORY)?.contains(plugin.items) == true && client.getItemContainer(InventoryID.INVENTORY)?.freeSpace() == 0) {
                plugin.state = States.BANK
                return
            }
            if(plugin.readyToHop) {
                if(client.banking()) {
                    plugin.state = States.CLOSE_INTERFACE
                    return
                }
                plugin.world = worldHop.findNextWorld()
                plugin.state = States.HOP
                return
            }
            if(client.shopping()) {
                val isEmpty: Boolean = client.getWidget(WidgetInfo.PACK(300, 16))?.dynamicChildren?.filter { plugin.items.contains(it.itemId) }!!.any { it.itemQuantity <= 47 }
                if(isEmpty) {
                    plugin.readyToHop = true
                    plugin.state = States.CLOSE_INTERFACE
                    return
                } else {
                    plugin.state = States.BUY
                    return
                }
            }
            if(npc == null) {
                plugin.state = States.WALK_NEAR_NPC
                return
            } else {
                lastKnownLocation = npc?.worldLocation
                plugin.state = States.TRADE_NPC
                return
            }
        }
    }

    override fun handleEvent(event: MenuOptionClicked) {
        with(events) {
            with(inventories) {
                when (plugin.state) {
                    States.WALK_NEAR_NPC -> {
                        event.walkTo(lastKnownLocation!!)
                        return
                    }
                    States.CLOSE_INTERFACE -> {

                    }
                    States.HOP -> {
                        clientThread.invoke(Runnable {
                            if (client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null) {
                                client.openWorldHopper()
                                return@Runnable
                            }
                            client.invokeMenuAction("Switch", "<col=ff9040>${plugin.world}</col>", 1, MenuAction.CC_OP.id, plugin.world, WidgetInfo.WORLD_SWITCHER_LIST.id)
                        })
                        return
                    }
                    States.DEPOSIT -> {
                        plugin.items.forEach { item ->
                            deposit.getItem(item)?.let {
                                event.clickItem(it, 8, deposit)
                                return
                            }
                        }
                    }
                    States.BANK -> {
                        bank?.let {
                            event.use(it)
                            return
                        }
                    }
                    States.BUY -> {
                        plugin.items.forEach { item ->
                            shop.getItem(item)?.let {
                                if(it.itemQuantity <= 47) {
                                    return@forEach
                                }
                                event.clickItem(it, 5, shop)
                                return
                            }
                        }
                    }
                    States.TRADE_NPC -> {
                        npc?.let {
                            event.trade(it, MenuAction.NPC_THIRD_OPTION)
                            return
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}