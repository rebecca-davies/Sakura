package net.runelite.client.plugins.oneclicklavas

import net.runelite.api.Client
import net.runelite.api.GameObject
import net.runelite.api.InventoryID
import net.runelite.api.NPC
import net.runelite.api.queries.GameObjectQuery
import net.runelite.api.queries.NPCQuery
import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetInfo

fun Client.findGameObject(vararg ids: String?): GameObject? {
    val query = GameObjectQuery()
    query.nameEquals(*ids)
    return query.result(this).nearestTo(this.localPlayer)
}

fun Client.findGameObject(id: Int): GameObject? {
    val query = GameObjectQuery()
    query.idEquals(id)
    return query.result(this).nearestTo(this.localPlayer)
}

fun Client.findNpc(id: Int): NPC? {
    val query = NPCQuery()
    query.idEquals(id)
    return query.result(this).nearestTo(this.localPlayer)
}

fun Client.banking(): Boolean {
    return this.getItemContainer(InventoryID.BANK) != null
}

fun Client.getBankInventoryItem(id: Int): Widget? {
    val bankWidget: Widget? = this.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER)
    return getWidgetItem(id, bankWidget)
}

fun Client.getBankItem(id: Int): Widget? {
    val bankWidget: Widget? = this.getWidget(WidgetInfo.BANK_ITEM_CONTAINER)
    return getWidgetItem(id, bankWidget)
}

fun Client.getInventoryItem(id: Int): Widget? {
    val inventoryWidget: Widget? = this.getWidget(WidgetInfo.INVENTORY)
    return getWidgetItem(id, inventoryWidget)
}

fun Client.inventoryQuantity(id: Int) : Int {
    val inventoryWidget: Widget? = this.getWidget(WidgetInfo.INVENTORY)
    if (inventoryWidget != null) {
        val items = inventoryWidget.dynamicChildren ?: return 0
        return items.filter { it.itemId == id }.size
    }
    return 0
}

fun getWidgetItem(id: Int, inventoryWidget: Widget?): Widget? {
    if (inventoryWidget != null) {
        val items = inventoryWidget.dynamicChildren ?: return null
        for (item in items) {
            if (item.itemId == id) {
                return item
            }
        }
    }
    return null
}

fun Client.isBankInventoryFull(): Boolean {
    val inventoryWidget = this.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER)
    if (inventoryWidget != null) {
        val items = inventoryWidget.widgetItems
        items?.let {
            if (items.size >= 28) {
                return true
            }
        }
    }
    return false
}

fun Client.getBankInventorySpace(): Int {
    val inventoryWidget: Widget? = this.getWidget(WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER)
    return if (inventoryWidget != null) {
        28 - inventoryWidget.dynamicChildren.filter { it.itemId != 6512 }.size
    } else {
        -1
    }
}

fun Client.getInventorySpace(): Int {
    val inventoryWidget: Widget? = this.getWidget(WidgetInfo.INVENTORY)
    return if (inventoryWidget != null) {
        28 - inventoryWidget.dynamicChildren.filter { it.itemId != 6512 }.size
    } else {
        -1
    }
}
