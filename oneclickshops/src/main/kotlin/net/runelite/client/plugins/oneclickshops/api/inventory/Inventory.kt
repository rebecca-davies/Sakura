package net.runelite.client.plugins.oneclickshops.api.inventory

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

    fun WidgetInfo.amount(id: Int): Int {
        return client.getWidget(this)?.dynamicChildren?.filter { it.itemId == id }?.first()?.itemQuantity!!
    }

    fun WidgetInfo.freeSpace(): Int {
        return 28 - client.getWidget(this)?.dynamicChildren?.filter { it.itemId != 6512 }?.size!!
    }

    fun Int.getItem(id: Int): Widget? {
        return client.getWidget(this)?.dynamicChildren?.first { it.itemId == id }
    }

    fun Int.contains(id: List<Any>): Boolean {
        return client.getWidget(this)?.dynamicChildren?.map { it.itemId }?.any(id::contains)!!
    }

    fun Int.contains(id: Int): Boolean {
        return client.getWidget(this)?.dynamicChildren?.map { it.itemId }?.contains(id)!!
    }

    fun Int.quantity(id: Int): Int {
        return client.getWidget(this)?.dynamicChildren?.filter { it.itemId == id }?.size!!
    }

    fun Int.amount(id: Int): Int {
        return client.getWidget(this)?.dynamicChildren?.filter { it.itemId == id }?.first()?.itemQuantity!!
    }

    fun Int.freeSpace(): Int {
        return 28 - client.getWidget(this)?.dynamicChildren?.filter { it.itemId != 6512 }?.size!!
    }
}