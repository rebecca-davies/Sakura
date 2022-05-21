package net.runelite.client.plugins.oneclickwintertodt

import net.runelite.api.ItemID
import net.runelite.client.config.*


@ConfigGroup("OneClickWintertodt")
interface OneClickWintertodtConfig : Config {

    enum class Food(private val type: String, val id: List<Int>) {
        WINE("Wine", listOf(ItemID.JUG_OF_WINE)),
        CAKE("Cake", listOf(ItemID.SLICE_OF_CAKE, ItemID._23_CAKE, ItemID.CAKE)),
        TROUT("Trout", listOf(ItemID.TROUT)),
        SALMON("Salmon", listOf(ItemID.SALMON)),
        LOBSTER("Lobster", listOf(ItemID.LOBSTER)),
        SHARK("Shark", listOf(ItemID.SHARK));

        override fun toString(): String {
            return type
        }
    }

    companion object {
        @JvmField
        @ConfigTitle(
            keyName = "foodConfig",
            name = "OneClickWintertodt Settings",
            description = "Configure your plugins settings to suit your needs",
            position = 1
        )
        val foodConfig: String = "foodConfig"
    }

    @ConfigItem(
        keyName = "food",
        name = "Select food",
        description = "Select the food to eat",
        position = 2,
    )
    @JvmDefault
    fun food(): Food {
        return Food.CAKE
    }

    @ConfigItem(
        position = 3,
        keyName = "health",
        name = "Eat health",
        description = "Set the health your player will eat at",
    )
    @JvmDefault
    fun health(): Int {
        return 25
    }
    @ConfigItem(
        position = 5,
        keyName = "healPyro",
        name = "Heal Pyromancer",
        description = "Heals the pyromancer instead of switching sides. (3 herblore)",
    )
    fun healPyro(): Boolean {
        return false
    }
    @ConfigItem(
        position = 6,
        keyName = "debugger",
        name = "Debug text in chatbox",
        description = "Debugger text",
    )
    fun debugger(): Boolean {
        return false
    }
}


