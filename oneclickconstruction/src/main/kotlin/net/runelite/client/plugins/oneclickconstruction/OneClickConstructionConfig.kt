package net.runelite.client.plugins.oneclickconstruction

import net.runelite.api.ItemID
import net.runelite.client.config.Config
import net.runelite.client.config.ConfigGroup
import net.runelite.client.config.ConfigItem
import net.runelite.client.config.ConfigTitle


@ConfigGroup("OneClickConstruction")
interface OneClickConstructionConfig : Config {
    companion object {
        @JvmField
        @ConfigTitle(
            keyName = "constructionConfig",
            name = "OneClickConstruction",
            description = "Configure your plugins settings to suit your needs",
            position = 1)
        val construction: String = "construction"

    }
    @ConfigItem(
        keyName = "text",
        name = "",
        description = "",
        position = 2)
    @JvmDefault
    fun text(): String {
        return "Select the method of training you want to use, make sure you have money in a servant bag and a bell in your dining room."
    }
    @ConfigItem(
        keyName = "method",
        name = "Method",
        description = "Select the method to train with",
        position = 3)
    @JvmDefault
    fun method(): Constructables {
        return Constructables.OAK_LARDER
    }
   /* @ConfigItem(
        position = 4,
        keyName = "runToUnnote",
        name = "Unnote at Phials",
        description = "Recommended to below level 40.",
    )
    @JvmDefault
    fun runToUnnote(): Boolean {
        return false
    }*/

    enum class Constructables(val type: String, val buildable: Int, val built: Int, val childId: Int, val plank: Int, val amount: Int) {
        OAK_LARDER("Oak Larder", 15403, 13566, 5, ItemID.OAK_PLANK, 0),
        OAK_DOOR("Oak Door",15328, 13344, 4, ItemID.OAK_PLANK, 10),
        MAHOGANY_TABLE("Mahogany table", 15298, 13298, 9, ItemID.MAHOGANY_PLANK, 0),
    }
}