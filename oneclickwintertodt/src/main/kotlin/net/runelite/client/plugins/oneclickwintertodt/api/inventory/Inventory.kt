package net.runelite.client.plugins.oneclickwintertodt.api.inventory

import net.runelite.api.Client
import net.runelite.api.InventoryID
import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetInfo
import javax.inject.Inject

class Inventory {

    @Inject
    lateinit var client: Client

    fun WidgetInfo.getItem(id: Int): Widget? {
        try {
            return client.getWidget(this)?.dynamicChildren?.first { it.itemId == id }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
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

    fun WidgetInfo.quantity(id: List<Int>): Int {
        return client.getWidget(this)?.dynamicChildren?.filter { id.contains(it.itemId) }?.size!!
    }

    fun WidgetInfo.freeSpace(): Int {
        return 28 - client.getWidget(this)?.dynamicChildren?.filter { it.itemId != 6512 }?.size!!
    }

    fun WidgetInfo.wearing(id: Int): Boolean {
        return this.contains(id)
    }

    fun InventoryID.contains(id: List<Any>): Boolean {
        return client.getItemContainer(this)?.items?.map { it.id }?.any(id::contains)!!
    }

    fun InventoryID.contains(id: Int): Boolean {
        return client.getItemContainer(this)?.items?.map { it.id }?.contains(id)!!
    }

    fun InventoryID.wearing(id: Int): Boolean {
        return this.contains(id)
    }
}