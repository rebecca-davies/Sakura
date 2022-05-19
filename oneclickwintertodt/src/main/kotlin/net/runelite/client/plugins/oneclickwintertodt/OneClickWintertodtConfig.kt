package net.runelite.client.plugins.oneclickwintertodt

import net.runelite.api.ItemID
import net.runelite.client.config.*


@ConfigGroup("OneClickWintertodt")
interface OneClickWintertodtConfig : Config {
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
        return Food.CAKE
    }

    @ConfigItem(
        position = 3,
        keyName = "food",
        name = "Eat health",
        description = "Set the health your player will eat at",
        title = "foodConfig")
    @JvmDefault
    fun health(): Int {
        return 25
    }

    enum class Food(private val food: String, val id: List<Int>) {
        WINE("Wine", listOf(ItemID.JUG_OF_WINE)),
        CAKE("Cake", listOf(ItemID.SLICE_OF_CAKE, ItemID._23_CAKE, ItemID.CAKE)),
        TROUT("Trout", listOf(ItemID.TROUT)),
        SALMON("Salmon", listOf(ItemID.SALMON)),
        LOBSTER("Lobster", listOf(ItemID.LOBSTER)),
        SHARK("Shark", listOf(ItemID.SHARK));

        override fun toString(): String {
            return food
        }
    }
}