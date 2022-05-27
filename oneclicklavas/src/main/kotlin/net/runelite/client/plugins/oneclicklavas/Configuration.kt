package net.runelite.client.plugins.oneclicklavas

import net.runelite.api.ItemID

enum class Pouches(val pouch: String) {
    COLOSSAL("Colossal");

    override fun toString(): String {
        return pouch
    }
}

enum class AltarTeleport(val ring: String) {
    RING_OF_THE_ELEMENTS("Ring of the elements"),
    RING_OF_DUELING("Ring of dueling");

    override fun toString(): String {
        return ring
    }
}

enum class BankTeleport(val method: String) {
    CRAFTING_CAPE("Crafting cape"),
    RING_OF_DUELING("Ring of dueling");

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