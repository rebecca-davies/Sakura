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
        TUNA("Tuna", listOf(ItemID.TUNA)),
        LOBSTER("Lobster", listOf(ItemID.LOBSTER)),
        POTATO_AND_CHEESE("Potato with Cheese", listOf(ItemID.POTATO_WITH_CHEESE)),
        SWORDFISH("Swordfish", listOf(ItemID.SWORDFISH)),
        KARAMBWAN("Karambwan", listOf(ItemID.COOKED_KARAMBWAN)),
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
        keyName = "foodAmount",
        name = "Amount of food",
        description = "Set the amount of food to bring",
    )
    @JvmDefault
    fun foodAmount(): Int {
        return 4
    }
    @ConfigItem(
        position = 4,
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
        keyName = "doFletch",
        name = "Fletch logs",
        description = "Fletches the logs into kindling for more reward points.",
    )
    @JvmDefault
    fun doFletch(): Boolean {
        return true
    }
    @ConfigItem(
        position = 6,
        keyName = "healPyro",
        name = "Heal Pyromancer",
        description = "Heals the pyromancer instead of switching sides. (3 herblore)",
    )
    @JvmDefault
    fun healPyro(): Boolean {
        return false
    }
    @ConfigItem(
        position = 7,
        keyName = "debugger",
        name = "Debug text in chatbox",
        description = "Debugger text",
    )
    @JvmDefault
    fun debugger(): Boolean {
        return false
    }
}


