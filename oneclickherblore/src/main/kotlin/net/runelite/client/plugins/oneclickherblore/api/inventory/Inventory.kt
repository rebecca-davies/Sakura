package net.runelite.client.plugins.oneclickherblore.api.inventory

import net.runelite.api.Client
import net.runelite.api.ItemContainer
import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetInfo
import javax.inject.Inject

class Inventory {

    @Inject
    lateinit var client: Client

    fun WidgetInfo.getItem(id: Int): Widget? {
        try {
            return client.getWidget(this)?.dynamicChildren?.firstOrNull { it.itemId == id }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun WidgetInfo.contains(id: List<Any>): Boolean {
        return client.getWidget(this)?.dynamicChildren?.map { it.itemId }?.any(id::contains)!!
    }

    fun WidgetInfo.containsAll(id: List<Any>): Boolean {
        return client.getWidget(this)?.dynamicChildren?.map { it.itemId }?.all(id::contains)!!
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
        return client.getWidget(this)?.dynamicChildren?.filter { it.itemId != 6512 }?.size ?: -1
    }

    fun Int.getItem(id: Int): Widget? {
        try {
            return client.getWidget(this)?.dynamicChildren?.first { it.itemId == id }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
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

    fun ItemContainer.contains(id: List<Any>): Boolean {
        return try {
            this.items.map { it.id }.any(id::contains)
        } catch (e: Exception) {
            false
        }

    }

    fun ItemContainer.containsAll(id: List<Any>): Boolean {
        return this.items.map { it.id }.all(id::contains)
    }

    fun ItemContainer.freeSpace(): Int {
        println(this.items.map { it.id }.joinToString(", "))
        return 28 - this.items.filter { it.id != -1 }.size
    }
}