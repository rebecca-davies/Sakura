package net.runelite.client.plugins.oneclickherblore

import net.runelite.api.ItemID
import net.runelite.client.config.*


@ConfigGroup("OneClickHerblore")
interface OneClickHerbloreConfig : Config {
    enum class Potions(private val type: String, val ingredients: List<Int>, val product: Int) {
        GUAM_POTION(type = "Guam potion", ingredients = listOf(ItemID.VIAL_OF_WATER, ItemID.GUAM_LEAF), product = ItemID.GUAM_POTION_UNF),
        MARRENTILL_POTION(type = "Marrentill potion", ingredients = listOf(ItemID.VIAL_OF_WATER, ItemID.MARRENTILL), product = ItemID.MARRENTILL_POTION_UNF),
        TARROMIN_POTION(type = "Tarromin potion", ingredients = listOf(ItemID.VIAL_OF_WATER, ItemID.TARROMIN), product = ItemID.TARROMIN_POTION_UNF),
        HARRALANDER_POTION(type = "Harralander potion", ingredients = listOf(ItemID.VIAL_OF_WATER, ItemID.HARRALANDER), product = ItemID.HARRALANDER_POTION_UNF),
        RANARR_POTION(type = "Ranarr potion", ingredients = listOf(ItemID.VIAL_OF_WATER, ItemID.RANARR_WEED), product = ItemID.RANARR_POTION_UNF),
        TOADFLAX_POTION(type = "Toadflax potion", ingredients = listOf(ItemID.VIAL_OF_WATER, ItemID.TOADFLAX), product = ItemID.TOADFLAX_POTION_UNF),
        IRIT_POTION(type = "Irit potion", ingredients = listOf(ItemID.VIAL_OF_WATER, ItemID.IRIT_LEAF), product = ItemID.IRIT_POTION_UNF),
        AVANTOE_POTION(type = "Avantoe potion", ingredients = listOf(ItemID.VIAL_OF_WATER, ItemID.AVANTOE), product = ItemID.AVANTOE_POTION_UNF),
        KWUARM_POTION(type = "Kwuarm potion", ingredients = listOf(ItemID.VIAL_OF_WATER, ItemID.KWUARM), product = ItemID.KWUARM_POTION_UNF),
        SNAPDRAGON_POTION(type = "Snapdragon potion", ingredients = listOf(ItemID.VIAL_OF_WATER, ItemID.SNAPDRAGON), product = ItemID.SNAPDRAGON_POTION_UNF),
        CADANTINE_POTION(type = "Cadantine potion", ingredients = listOf(ItemID.VIAL_OF_WATER, ItemID.CADANTINE), product = ItemID.CADANTINE_POTION_UNF),
        LANTADYME_POTION(type = "Lantadyme potion", ingredients = listOf(ItemID.VIAL_OF_WATER, ItemID.LANTADYME), product = ItemID.LANTADYME_POTION_UNF),
        DWARF_WEED_POTION(type = "Dwarf weed potion", ingredients = listOf(ItemID.VIAL_OF_WATER, ItemID.DWARF_WEED), product = ItemID.DWARF_WEED_POTION_UNF),
        TORSTOL_POTION(type = "Torstol potion", ingredients = listOf(ItemID.VIAL_OF_WATER, ItemID.TORSTOL), product = ItemID.TORSTOL_POTION_UNF),
        ATTACK_POTION(type = "Attack potion", ingredients = listOf(ItemID.EYE_OF_NEWT, ItemID.GUAM_POTION_UNF), product = ItemID.ATTACK_POTION3),
        ANTIPOISON("Antipoison", listOf(ItemID.UNICORN_HORN_DUST, ItemID.MARRENTILL_POTION_UNF), ItemID.ANTIPOISON3),
        STRENGTH_POTION("Strength potion", listOf(ItemID.LIMPWURT_ROOT, ItemID.TARROMIN_POTION_UNF), ItemID.STRENGTH_POTION3),
        SERUM_207("Serum 207", listOf(ItemID.ASHES, ItemID.TARROMIN_POTION_UNF), ItemID.SERUM_207_3),
        RESTORE_POTION("Restore potion", listOf(ItemID.RED_SPIDERS_EGGS, ItemID.HARRALANDER_POTION_UNF), ItemID.RESTORE_POTION3),
        ENERGY_POTION("Energy potion", listOf(ItemID.CHOCOLATE_DUST, ItemID.HARRALANDER_POTION_UNF), ItemID.ENERGY_POTION3),
        AGILITY_POTION("Agility potion", listOf(ItemID.TOADS_LEGS, ItemID.TOADFLAX_POTION_UNF), ItemID.AGILITY_POTION3),
        COMBAT_POTION("Combat potion", listOf(ItemID.GOAT_HORN_DUST, ItemID.HARRALANDER_POTION_UNF), ItemID.COMBAT_POTION3),
        PRAYER_POTION("Prayer potion", listOf(ItemID.SNAPE_GRASS, ItemID.RANARR_POTION_UNF), ItemID.PRAYER_POTION3),
        SUPER_ATTACK("Super attack", listOf(ItemID.EYE_OF_NEWT, ItemID.IRIT_POTION_UNF), ItemID.SUPER_ATTACK3),
        SUPERANTIPOISON("Superantipoison", listOf(ItemID.UNICORN_HORN_DUST, ItemID.IRIT_POTION_UNF), ItemID.SUPERANTIPOISON3),
        FISHING_POTION("Fishing potion", listOf(ItemID.SNAPE_GRASS, ItemID.AVANTOE_POTION_UNF), ItemID.FISHING_POTION3),
        SUPER_ENERGY("Super energy", listOf(ItemID.MORT_MYRE_FUNGUS, ItemID.AVANTOE_POTION_UNF), ItemID.SUPER_ENERGY3),
        HUNTER_POTION("Hunter potion", listOf(ItemID.KEBBIT_TEETH_DUST, ItemID.AVANTOE_POTION_UNF), ItemID.HUNTER_POTION3),
        SUPER_STRENGTH("Super strength", listOf(ItemID.LIMPWURT_ROOT, ItemID.KWUARM_POTION_UNF), ItemID.SUPER_STRENGTH3),
        SUPER_RESTORE("Super restore", listOf(ItemID.RED_SPIDERS_EGGS, ItemID.SNAPDRAGON_POTION_UNF), ItemID.SUPER_RESTORE3),
        SUPER_DEFENCE("Super defence", listOf(ItemID.WHITE_BERRIES, ItemID.CADANTINE_POTION_UNF), ItemID.SUPER_DEFENCE3),
        ANTIFIRE_POTION("Antifire potion", listOf(ItemID.DRAGON_SCALE_DUST, ItemID.LANTADYME_POTION_UNF), ItemID.ANTIFIRE_POTION3),
        RANGING_POTION("Ranging potion", listOf(ItemID.WINE_OF_ZAMORAK, ItemID.DWARF_WEED_POTION_UNF), ItemID.RANGING_POTION3),
        MAGIC_POTION("Magic potion", listOf(ItemID.POTATO_CACTUS, ItemID.LANTADYME_POTION_UNF), ItemID.MAGIC_POTION3),
        BASTION_POTION("Bastion potion", listOf(ItemID.WINE_OF_ZAMORAK, ItemID.CADANTINE_BLOOD_POTION_UNF), ItemID.BASTION_POTION3),
        BATTLEMAGE_POTION("Battlemage potion", listOf(ItemID.POTATO_CACTUS, ItemID.CADANTINE_BLOOD_POTION_UNF), ItemID.BATTLEMAGE_POTION3),
        SARADOMIN_BREW("Saradomin brew", listOf(ItemID.CRUSHED_NEST, ItemID.TOADFLAX_POTION_UNF), ItemID.SARADOMIN_BREW3),
        ANCIENT_BREW("Ancient brew", listOf(ItemID.NIHIL_DUST, ItemID.DWARF_WEED_POTION_UNF), ItemID.ANCIENT_BREW3),
        SUPER_ANTIFIRE_POTION("Super antifire potion", listOf(ItemID.CRUSHED_SUPERIOR_DRAGON_BONES, ItemID.ANTIFIRE_POTION4), ItemID.SUPER_ANTIFIRE_POTION4);

        override fun toString(): String {
            return type
        }
    }

    companion object {
        @JvmField
        @ConfigTitle(
            keyName = "potionConfig",
            name = "OneClickHerblore Settings",
            description = "Configure your plugins settings to suit your needs",
            position = 1
        )
        val potionConfig: String = "potionConfig"
    }

    @ConfigItem(
        keyName = "potion",
        name = "Potion",
        description = "Select the potion to create",
        position = 2,
    )
    @JvmDefault
    fun potion(): Potions {
        return Potions.ATTACK_POTION
    }
}


