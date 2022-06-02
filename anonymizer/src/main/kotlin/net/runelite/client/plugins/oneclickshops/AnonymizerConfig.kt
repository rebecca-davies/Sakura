package net.runelite.client.plugins.oneclickshops

import net.runelite.client.config.Config
import net.runelite.client.config.ConfigGroup
import net.runelite.client.config.ConfigItem
import net.runelite.client.config.ConfigTitle


@ConfigGroup("Anonymizer")
interface AnonymizerConfig : Config {
    companion object {
        @JvmField
        @ConfigTitle(
            keyName = "anonymousConfig",
            name = "Anonymizer",
            description = "Configure your plugins settings to suit your needs",
            position = 1)
        val anonymousConfig: String = "anonymousConfig"

    }
    @ConfigItem(
        keyName = "username",
        name = "Username",
        description = "Enter the username to show in the chatbox",
        position = 2)
    @JvmDefault
    fun username(): String {
        return ""
    }
    @ConfigItem(
        position = 3,
        keyName = "hideOrbs",
        name = "Hide orbs",
        description = "Hides identifying information in the orbs",
    )
    @JvmDefault
    fun hideOrbs(): Boolean {
        return false
    }
    @ConfigItem(
        position = 4,
        keyName = "hideXp",
        name = "Hide XP",
        description = "Hides shown XP in the xp bar",
    )
    @JvmDefault
    fun hideXp(): Boolean {
        return false
    }
    @ConfigItem(
        position = 5,
        keyName = "hideAmounts",
        name = "Hides Quantities",
        description = "Hides identifying quantity information",
    )
    @JvmDefault
    fun hideAmounts(): Boolean {
        return false
    }

    @ConfigItem(
        position = 6,
        keyName = "hideChat",
        name = "Hide Chat",
        description = "Clears the chat",
    )
    @JvmDefault
    fun hideChat(): Boolean {
        return false
    }

    @ConfigItem(
        position = 7,
        keyName = "hideStats",
        name = "Hide Stats",
        description = "Hides account stats in various interfaces",
    )
    @JvmDefault
    fun hideStats(): Boolean {
        return false
    }

}