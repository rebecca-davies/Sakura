package net.runelite.client.plugins.oneclicklavas

import net.runelite.api.ItemID

enum class Pouches(val pouch: String) {
    COLOSSAL("Colossal");

    override fun toString(): String {
        return pouch
    }
}

enum class AltarTeleport(val ring: String, val items: List<Int>) {
    RING_OF_THE_ELEMENTS("Ring of the elements", listOf(ItemID.RING_OF_THE_ELEMENTS_26818)),
    RING_OF_DUELING("Ring of dueling", listOf(ItemID.RING_OF_DUELING1, ItemID.RING_OF_DUELING2, ItemID.RING_OF_DUELING3, ItemID.RING_OF_DUELING4, ItemID.RING_OF_DUELING5, ItemID.RING_OF_DUELING6, ItemID.RING_OF_DUELING7, ItemID.RING_OF_DUELING8));

    override fun toString(): String {
        return ring
    }
}

enum class BankTeleport(val method: String, val items: List<Int>) {
    CRAFTING_CAPE("Crafting cape", listOf(ItemID.CRAFTING_CAPE, ItemID.CRAFTING_CAPET)),
    RING_OF_DUELING("Ring of dueling", listOf(ItemID.RING_OF_DUELING1, ItemID.RING_OF_DUELING2, ItemID.RING_OF_DUELING3, ItemID.RING_OF_DUELING4, ItemID.RING_OF_DUELING5, ItemID.RING_OF_DUELING6, ItemID.RING_OF_DUELING7, ItemID.RING_OF_DUELING8));

    override fun toString(): String {
        return method
    }
}

enum class RunEnergy(val potion: String, val itemId: Int) {
    NONE("None", -1),
    ENERGY_POTION("Energy potion", ItemID.ENERGY_POTION1),
    SUPER_ENERGY_POTION("Super energy potion", ItemID.SUPER_ENERGY1),
    STAMINA_POTION("Stamina potion", ItemID.STAMINA_POTION1);

    override fun toString(): String {
        return potion
    }
}