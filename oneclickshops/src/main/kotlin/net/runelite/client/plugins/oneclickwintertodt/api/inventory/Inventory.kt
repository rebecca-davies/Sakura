package net.runelite.client.plugins.oneclickwintertodt.api.inventory

import net.runelite.api.Client
import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetInfo
import javax.inject.Inject

class Inventory {

    @Inject
    lateinit var client: Client

    fun WidgetInfo.getItem(id: Int): Widget? {
        return client.getWidget(this)?.dynamicChildren?.first { it.itemId == id }
    }

    fun WidgetInfo.contains(id: List<Any>): Boolean {
        return client.getWidget(this)?.dynamicChildren?.map { it.itemId }?.any(id::contains)!!
    }

    fun WidgetInfo.contains(id: Int): Boolean {
        return client.getWidget(this)?.dynamicChildren?.map { it.itemId }?.contains(id)!!
    }

    fun WidgetInfo.quantity(id: Int): Int {
        return client.getWidget(this)?.dynamicChildren?.filter { it.itemId == id }?.size!!
    }

    fun WidgetInfo.freeSpace(): Int {
        return 28 - client.getWidget(this)?.dynamicChildren?.filter { it.itemId != 6512 }?.size!!
    }
}