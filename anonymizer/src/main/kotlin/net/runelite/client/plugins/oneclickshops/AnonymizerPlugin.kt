package net.runelite.client

import com.google.inject.Provides
import net.runelite.api.*
import net.runelite.api.events.BeforeRender
import net.runelite.api.events.WidgetLoaded
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
    name = "Anonymizer",
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

    var needRefresh = false

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
        when(config.hideStats()) {
            true -> {
                client.getWidget(712,2)?.getChild(88)?.text = "<col=0dc10d>--/1395</col>"
                client.getWidget(712,2)?.getChild(75)?.text = "<col=0dc10d>--/400</col>"
                client.getWidget(712,2)?.getChild(62)?.text = "<col=0dc10d>--/492</col>"
                client.getWidget(712,2)?.getChild(49)?.text = "<col=0dc10d>--/153</col>"
                client.getWidget(712,2)?.getChild(36)?.text = "Total XP: <col=0dc10d>--</col>"
                client.getWidget(712,2)?.getChild(24)?.text = "<col=0dc10d>--</col>"
                client.getWidget(712,2)?.getChild(11)?.text = "<col=0dc10d>--</col>"
                client.getWidget(712,2)?.getChild(88)?.width = 200
                client.getWidget(712,2)?.getChild(75)?.width = 200
                client.getWidget(712,2)?.getChild(62)?.width = 200
                client.getWidget(712,2)?.getChild(49)?.width = 200
                client.getWidget(712,2)?.getChild(36)?.width = 200
                client.getWidget(712,2)?.getChild(24)?.width = 200
                client.getWidget(712,2)?.getChild(11)?.width = 200
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
                client.getWidget(162, 55)?.text = "${config.username()}:${(txt?.get(1) ?: "")}"
                client.getWidget(712, 1)?.text = config.username()
                client.getWidget(217, 4)?.text = config.username()
                needRefresh = true
            }
            false -> {
                if(needRefresh) {
                    clientThread.invoke(Runnable {
                        client.runScript(ScriptID.CHAT_PROMPT_INIT)
                        needRefresh = false
                    })
                }
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
    fun onWidgetLoaded(event: WidgetLoaded) {
        println(event.groupId)
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
