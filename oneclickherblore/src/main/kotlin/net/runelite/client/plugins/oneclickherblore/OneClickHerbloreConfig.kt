package net.runelite.client.plugins.oneclickherblore

import net.runelite.api.ItemID
import net.runelite.client.config.*


@ConfigGroup("OneClickHerblore")
interface OneClickHerbloreConfig : Config {
    enum class Potions(private val type: String, val ingredients: List<Int>, val finished: Int) {
        BASTION("Bastion", listOf(ItemID.WINE_OF_ZAMORAK, ItemID.CADANTINE_BLOOD_POTION_UNF), ItemID.BASTION_POTION3);
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
        name = "Select potion",
        description = "Select the potion to create",
        position = 2,
    )
    @JvmDefault
    fun potion(): Potions {
        return Potions.BASTION
    }
}


