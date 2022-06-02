package net.runelite.client

import com.google.inject.Provides
import net.runelite.api.*
import net.runelite.api.events.BeforeRender
import net.runelite.api.widgets.ItemQuantityMode
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.callback.ClientThread
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.events.ConfigChanged
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.oneclickshops.util.Log
import net.runelite.client.plugins.oneclickshops.AnonymizerConfig
import net.runelite.client.util.RSTimeUnit
import net.runelite.client.util.Text
import org.pf4j.Extension
import java.text.Format
import javax.inject.Inject

@Extension
@PluginDescriptor(
    name = "Sakura Anon",
    description = ":Prayje:",
    tags = ["rebecca", "anonymizer", "sakura", "anon"]
)
class AnonymizerPlugin : Plugin() {

    @Inject
    private lateinit var config: AnonymizerConfig

    @Inject
    lateinit var client: Client

    @Inject
    lateinit var clientThread: ClientThread

    companion object : Log()

    @Provides
    fun provideConfig(configManager: ConfigManager): AnonymizerConfig {
        return configManager.getConfig(AnonymizerConfig::class.java)
    }

    override fun startUp() {
        log.info("Starting Anonymizer")
    }

    override fun shutDown() {
        log.info("Stopping Anonymizer")
    }

    @Subscribe
    fun onBeforeRender(event: BeforeRender) {
        when(config.hideXp()) {
            true -> {
                client.getWidget(122, 10)?.text = "------"
                client.getWidget(122, 11)?.isHidden = true
            }
        }
        when(config.hideOrbs()) {
            true -> {
                client.getWidget(160, 5)?.text = "--"
                client.getWidget(160, 16)?.text = "--"
                client.getWidget(WidgetInfo.MINIMAP_RUN_ORB_TEXT)?.text = "--"
                client.getWidget(160,32)?.text = "--"
            }
        }
        when(config.username().isNotEmpty()) {
            true -> {
                val txt = client.getWidget(162, 55)?.text?.split(":")
                client.getWidget(162, 55)?.text = config.username() + ":" + (txt?.get(1) ?: "")
            }
        }
        when(config.hideAmounts()) {
            true -> {
                client.getWidget(WidgetInfo.INVENTORY)?.dynamicChildren?.forEach {
                    it.itemQuantityMode = ItemQuantityMode.NEVER
                }
            }
        }
        when(config.hideChat()) {
            true -> {
                client.getWidget(162, 56)?.isHidden = true
            }
        }
    }

    @Subscribe
    fun onConfigChanged(event: ConfigChanged) {
        when(config.hideXp()) {
            false -> {
                client.getWidget(122, 10)?.text = "%,d".format(client.overallExperience)
            }
        }
        when(config.hideOrbs()) {
            false -> {
                client.getWidget(160, 5)?.text = client.getBoostedSkillLevel(Skill.HITPOINTS).toString()
                client.getWidget(160, 16)?.text = client.getBoostedSkillLevel(Skill.PRAYER).toString()
                client.getWidget(WidgetInfo.MINIMAP_RUN_ORB_TEXT)?.text = client.energy.toString()
                client.getWidget(160,32)?.text = (client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT) / 10).toString()
            }
        }
        when(config.username().isEmpty()) {
            true -> {
                val txt = client.getWidget(162, 55)?.text?.split(":")
                client.getWidget(162, 55)?.text = client.localPlayer.name + ":" + (txt?.get(1) ?: "")
            }
        }
        when(config.hideAmounts()) {
            false -> {
                client.getWidget(WidgetInfo.INVENTORY)?.dynamicChildren?.forEach {
                    it.itemQuantityMode = ItemQuantityMode.STACKABLE
                }
            }
        }
        when(config.hideChat()) {
            false -> {
                client.getWidget(162, 56)?.isHidden = false
            }
        }
    }
}
