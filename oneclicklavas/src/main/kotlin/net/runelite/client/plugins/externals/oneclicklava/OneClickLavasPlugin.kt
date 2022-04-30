package net.runelite.client.plugins.externals.oneclicklava

import net.runelite.api.Client
import net.runelite.client.config.ConfigManager
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.zeahcrafter.OneClickLavasConfig
import org.pf4j.Extension
import javax.inject.Inject

@Extension
@PluginDescriptor(
    name = "One Click Lavas",
    description = ""
)
class OneClickLavasPlugin : Plugin() {

    @Inject
    lateinit var config: OneClickLavasConfig

    @Inject
    lateinit var client: Client

    var state = States.IDLE

    companion object : Log()

    fun provideConfig(configManager: ConfigManager): OneClickLavasConfig {
        return configManager.getConfig(OneClickLavasConfig::class.java)
    }

}