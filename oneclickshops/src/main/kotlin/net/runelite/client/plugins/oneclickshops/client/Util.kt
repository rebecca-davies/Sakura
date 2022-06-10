package net.runelite.client.plugins.oneclickshops.client

import net.runelite.api.Client
import net.runelite.api.GameObject
import net.runelite.api.InventoryID
import net.runelite.api.NPC
import net.runelite.api.queries.GameObjectQuery
import net.runelite.api.queries.NPCQuery
import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.plugins.oneclickwintertodt.magic.shop


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

fun Client.findNpc(ids: Collection<Int>): NPC? {
    val query = NPCQuery()
    query.idEquals(ids)
    return query.result(this).nearestTo(this.localPlayer)
}

fun Client.findGameObject(ids: Collection<Int>): GameObject? {
    val query = GameObjectQuery()
    query.idEquals(ids)
    return query.result(this).nearestTo(this.localPlayer)
}

fun Client.banking(): Boolean {
    return this.getItemContainer(InventoryID.BANK) != null || this.getWidget(192, 0) != null
}

fun Client.shopping(): Boolean {
    return this.getWidget(300, 16) != null
}