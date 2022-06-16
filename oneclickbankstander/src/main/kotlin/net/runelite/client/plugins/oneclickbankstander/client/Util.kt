package net.runelite.client.plugins.oneclickbankstander.client

import net.runelite.api.Client
import net.runelite.api.GameObject
import net.runelite.api.InventoryID
import net.runelite.api.NPC
import net.runelite.api.queries.GameObjectQuery
import net.runelite.api.queries.InventoryItemQuery
import net.runelite.api.queries.NPCQuery


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

fun Client.inventoryContains(id: Int): Boolean {
    val query = InventoryItemQuery(InventoryID.INVENTORY)
    return query.result(this).map { it.id }.contains(id)
}

fun Client.inventoryContains(id: List<Int>): Boolean {
    val query = InventoryItemQuery(InventoryID.INVENTORY)
    return query.result(this).map { it.id }.any(id::contains)
}

fun Client.inventoryContainsAll(id: List<Int>): Boolean {
    val query = InventoryItemQuery(InventoryID.INVENTORY)
    return query.result(this).map { it.id }.all(id::contains)
}

fun Client.findNpc(id: Int): NPC? {
    val query = NPCQuery()
    query.idEquals(id)
    return query.result(this).nearestTo(this.localPlayer)
}

fun Client.banking(): Boolean {
    return this.getItemContainer(InventoryID.BANK) != null
}