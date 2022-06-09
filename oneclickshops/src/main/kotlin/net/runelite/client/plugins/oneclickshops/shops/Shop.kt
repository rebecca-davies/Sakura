package net.runelite.client.plugins.oneclickshops.shops

import net.runelite.api.events.MenuOptionClicked
import net.runelite.client.OneClickShopsPlugin
import net.runelite.client.callback.ClientThread
import net.runelite.client.plugins.oneclickshops.OneClickShopsConfig
import net.runelite.client.plugins.oneclickshops.api.entry.Entries
import net.runelite.client.plugins.oneclickshops.api.inventory.Inventory
import net.runelite.client.plugins.oneclickshops.client.WorldHop

interface Shop {

    var inventories: Inventory
    var events: Entries
    var plugin: OneClickShopsPlugin
    var config: OneClickShopsConfig
    var worldHop: WorldHop
    var clientThread: ClientThread

    fun handleLogic()

    fun handleEvent(event: MenuOptionClicked)

}