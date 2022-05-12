package net.runelite.client.plugins.externals.oneclicklavas

import com.google.inject.Provides
import net.runelite.api.Client
import net.runelite.api.ItemID
import net.runelite.api.MenuAction
import net.runelite.api.events.MenuOptionClicked
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.zeahcrafter.OneClickLavasConfig
import org.pf4j.Extension
import javax.inject.Inject

@Extension
@PluginDescriptor(
    name = "One Click Lavas",
    description = "A one click lavas test",
    tags = ["rebecca, oneclick, one click, lavas"]
)
class OneClickLavasPlugin : Plugin() {

    @Inject
    lateinit var config: OneClickLavasConfig

    @Inject
    lateinit var client: Client

    var state = States.NEED_DEPOSIT

    companion object : Log()

    @Provides
    fun provideConfig(configManager: ConfigManager): OneClickLavasConfig {
        return configManager.getConfig(OneClickLavasConfig::class.java)
    }

    override fun startUp() {
        log.debug("Starting One click lavas")
    }

    override fun shutDown() {}

    @Subscribe
    fun onMenuOptionClicked(event: MenuOptionClicked) {
        val bank = client.findGameObjectByName("Bank chest")
        if(client.banking()) {
            when(state) {
                States.NEED_DEPOSIT -> {
                    val lavas = client.getBankItem(ItemID.LAVA_RUNE)
                    client.insertMenuItem("Deposit Lava rune", "", MenuAction.UNKNOWN.id, event.id, event.param0, event.param1, true)
                    client.setTempMenuEntry(client.menuEntries.first { it.option.equals("Deposit Lava Rune") })
                }
            }
        }
    }
}