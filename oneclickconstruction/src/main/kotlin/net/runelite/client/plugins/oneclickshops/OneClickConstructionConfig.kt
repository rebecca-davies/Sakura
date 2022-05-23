package net.runelite.client.plugins.oneclickshops

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
        return "Select the method of training you want to use, if you're below 40 construction it's recommended to have your house at Rimmington and having noted planks to unnote which you can toggle with \"Un-note at Phials\""
    }
    @ConfigItem(
        keyName = "method",
        name = "method",
        description = "Select the method to train with",
        position = 3)
    @JvmDefault
    fun method(): Constructables {
        return Constructables.OAK_LARDER
    }
    @ConfigItem(
        position = 4,
        keyName = "runToUnnote",
        name = "Un-note at Phials",
        description = "Recommended to below level 40.",
    )
    fun runToUnnote(): Boolean {
        return false
    }

    enum class Constructables(val type: String, val buildable: Int, val built: Int) {
        OAK_LARDER("Oak Larder", 0, 0),
        OAK_DOOR("Oak Door",0, 0),
        MAHOGANY_TABLE("Mahogany table", 0, 0),
        MYTH_CAPE("Mounted Myth Cape", 0, 0),
    }
}