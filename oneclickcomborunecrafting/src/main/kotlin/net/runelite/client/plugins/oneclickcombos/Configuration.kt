package net.runelite.client.plugins.oneclickcombos

import net.runelite.api.ItemID
import net.runelite.api.coords.WorldPoint

enum class Pouches(val pouch: String, val items: List<Int>) {
    MED_LARGE("Medium + Large", listOf(ItemID.MEDIUM_POUCH, ItemID.LARGE_POUCH)),
    LARGE_GIANT("Large + Giant", listOf(ItemID.LARGE_POUCH, ItemID.GIANT_POUCH)),
    COLOSSAL("Colossal", listOf(ItemID.COLOSSAL_POUCH));

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
    MAX_CAPE("Max cape", listOf(13342)),
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

enum class RuneType(val rune: String, val runeId: Int, val comboRune: Int, val actionId: Int, val ruinsId: Int, val altarId: Int, val insideId: Int, val outsideId: Int, val location: WorldPoint) {
    LAVA("Lava Rune", ItemID.LAVA_RUNE, ItemID.EARTH_RUNE,6, 34817, 34764, 10315, 13107, WorldPoint(3133,3268, 0)),
    STEAM("Steam Rune", ItemID.STEAM_RUNE, ItemID.WATER_RUNE,6,  34817, 34764, 10315,  13107, LAVA.location),
    MUD("Mud Rune", ItemID.MUD_RUNE, ItemID.EARTH_RUNE, 4, 34815, 34762,10827, 12593, WorldPoint(3177,3163, 0));

    override fun toString(): String {
        return rune
    }
}

enum class Essence(val essence: String, val essenceId: Int) {

    PURE_ESSENCE("Pure essence", ItemID.PURE_ESSENCE),
    DAEYALT_ESSENCE("Daeyalt essence", ItemID.DAEYALT_ESSENCE);

    override fun toString(): String {
        return essence
    }
}