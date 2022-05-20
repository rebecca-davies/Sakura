package net.runelite.client.plugins.oneclickshops

import net.runelite.client.config.Config
import net.runelite.client.config.ConfigGroup
import net.runelite.client.config.ConfigItem
import net.runelite.client.config.ConfigTitle
import net.runelite.client.plugins.oneclickshops.data.Shops


@ConfigGroup("OneClickShops")
interface OneClickShopsConfig : Config {
    companion object {
        @JvmField
        @ConfigTitle(
            keyName = "shopConfig",
            name = "OneClickShops",
            description = "Configure your plugins settings to suit your needs",
            position = 1)
        val shop: String = "shop"

    }
    @ConfigItem(
        keyName = "text",
        name = "",
        description = "",
        position = 2,
        title = "shopConfig")
    @JvmDefault
    fun text(): String {
        return "Select the store you are buying from, and enter the items on each new line that you want to buy."
    }
    @ConfigItem(
        keyName = "shop",
        name = "Shop",
        description = "Select the shop to buy from",
        position = 3,
        title = "shopConfig")
    @JvmDefault
    fun shop(): Shops {
        return Shops.CHARTER
    }
    @ConfigItem(
        keyName = "items",
        name = "Enter item ids to buy",
        description = "Enter item ids you want to buy on each new line",
        position = 4,
        title = "shopConfig")
    @JvmDefault
    fun items(): String {
        return ""
    }
    @ConfigItem(
        position = 5,
        keyName = "hopOnFull",
        name = "Automatic hop after empty",
        description = "Instantly hop to the next world after the world is empty.",
    )
    fun hopOnFull(): Boolean {
        return true
    }
}