package net.runelite.client.plugins.zeahcrafter

import net.runelite.client.config.*
import net.runelite.client.plugins.externals.oneclicklava.Pouches

@ConfigGroup("OneClickLavas")
interface OneClickLavasConfig : Config {

    companion object {
        @JvmField
        @ConfigTitle(
            keyName = "pouchConfig",
            name = "Configuration",
            description = "Configure your plugins settings to suit your needs",
            position = 0)
        val pouchConfig: String = "pouchConfig"
    }

    @ConfigItem(
        keyName = "pouch",
        name = "Select pouches",
        description = "Select the pouches you want to use",
        position = 1,
        title = "pouchconfig")
    fun pouch(): Pouches {
        return Pouches.COLOSSAL
    }
}
