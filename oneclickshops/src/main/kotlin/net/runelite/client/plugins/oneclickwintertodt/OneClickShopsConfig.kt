package net.runelite.client.plugins.oneclickwintertodt

import net.runelite.api.ItemID
import net.runelite.client.config.*

@ConfigGroup("OneClickWintertodt")
interface OneClickShopsConfig : Config {
    companion object {
        @JvmField
        @ConfigTitle(
            keyName = "foodConfig",
            name = "OneClickWintertodt Settings",
            description = "Configure your plugins settings to suit your needs",
            position = 1)
        val food: String = "food"
    }

    @ConfigItem(
        keyName = "food",
        name = "Select food",
        description = "Select the food to eat",
        position = 2,
        title = "foodConfig")
    @JvmDefault
    fun food(): Food {
        return Food.WINE
    }

    enum class Food(private val food: String, val id: Int) {
        WINE("Wine", ItemID.JUG_OF_WINE),
        TROUT("Trout", ItemID.TROUT),
        SALMON("Salmon", ItemID.SALMON),
        LOBSTER("Lobster", ItemID.LOBSTER),
        SHARK("Shark", ItemID.SHARK);

        override fun toString(): String {
            return food
        }
    }
}