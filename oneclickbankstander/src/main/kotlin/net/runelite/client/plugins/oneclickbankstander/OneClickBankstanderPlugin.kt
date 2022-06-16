package net.runelite.client.plugins.oneclickbankstander

import com.google.inject.Provides
import net.runelite.api.*
import net.runelite.api.events.GameTick
import net.runelite.api.events.MenuOptionClicked
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.callback.ClientThread
import net.runelite.api.widgets.WidgetInfo.INVENTORY as inventory
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.oneclickbankstander.OneClickBankstanderConfig.*
import net.runelite.client.plugins.oneclickbankstander.util.Log
import net.runelite.client.plugins.oneclickbankstander.api.entry.Entries
import net.runelite.client.plugins.oneclickbankstander.api.inventory.Inventory
import net.runelite.client.plugins.oneclickbankstander.client.banking
import net.runelite.client.plugins.oneclickbankstander.client.findGameObject
import net.runelite.client.plugins.oneclickbankstander.client.inventoryContains
import net.runelite.client.plugins.oneclickbankstander.client.inventoryContainsAll
import org.pf4j.Extension
import javax.inject.Inject
import kotlin.properties.Delegates
import net.runelite.api.widgets.WidgetInfo.BANK_ITEM_CONTAINER as bank


@Extension
@PluginDescriptor(
    name = "One Click Bankstander",
    description = ":Prayje:",
    tags = ["rebecca, oneclick, one click, bankstander"]
)
class OneClickBankstanderPlugin : Plugin() {

    @Inject
    private lateinit var config: OneClickBankstanderConfig

    @Inject
    lateinit var client: Client

    @Inject
    lateinit var events: Entries

    @Inject
    lateinit var inventories: Inventory

    @Inject
    lateinit var clientThread: ClientThread

    companion object : Log()
    private var bankObject: GameObject? = null
    private var prev = 0
    private var timeout = 0
    private var mixing = false
    private var performAction = true
    private var index = 0
    private var closed = false

    @Provides
    fun provideConfig(configManager: ConfigManager): OneClickBankstanderConfig {
        return configManager.getConfig(OneClickBankstanderConfig::class.java)
    }

    override fun startUp() {
        log.info("Starting One Click Herblore")
        reset()
    }

    override fun shutDown() {
        log.info("Stopping One Click Herblore")
    }

    private fun reset() {
        performAction = true
        prev = 0
        timeout = 0
        mixing = false
        index = 0
        closed = false

    }

    private var state by Delegates.observable(States.IDLE) { _, previous, current ->
        if (previous != current) {
            performAction = true
        }
    }

    @Subscribe
    private fun onGameTick(event: GameTick) {
        bankObject = client.findGameObject("Bank chest")
        if(timeout > 0) {
            timeout--
        }
    }

    @Subscribe
    fun onMenuEntryClicked(event: MenuOptionClicked) {
        with(inventories) {
            with(events) {
                handleLogic()
                client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "state = $state performAction = $performAction", "")
                if(!performAction || timeout != 0) {
                    event.consume()
                    return
                }
                performAction = false
                when(state) {
                    States.WITHDRAW -> {
                        val item = config.potion().ingredients.first { !client.inventoryContains(it) && prev != it }.also { prev = it }
                        performAction = true
                        bank.getItem(item)?.let {
                            event.clickItem(it, 1, bank)
                            return
                        }
                    }
                    States.DEPOSIT -> {
                            event.click(-1, 786474)
                            return
                    }
                    States.CONFIRM -> {
                        mixing = true
                        event.click(-1, 17694734)
                        return
                    }
                    States.MIX -> {
                        if(config.potion() == Potions.SERUM_207) {
                            event.useOn(inventory.getItemFromIndex(index), inventory.getItemFromIndex(index + 14))
                            index++
                            return
                        }
                        event.useOn(inventory.getItem(config.potion().ingredients.first()), inventory.getItem(config.potion().ingredients.last()))
                        return
                    }
                    States.OPEN_BANK -> {
                        bankObject?.let {
                            event.use(it)
                            return
                        }
                    }
                    States.CLOSE_INTERFACE -> {
                        event.closeBank()
                    }
                    States.IDLE -> {}
                }
            }
        }
        if(event.menuOption.equals("Walk here", ignoreCase = true)) {
            event.consume()
            return;
        }
    }

    private fun handleLogic() {
        with(inventories) {
            if(state == States.DEPOSIT || state == States.WITHDRAW || state == States.OPEN_BANK) {
                if(!client.banking()) {
                    state = States.OPEN_BANK
                    closed = false
                    return
                }
            }
            if(client.banking()) {
                if(client.inventoryContains(config.potion().product) && !client.inventoryContains(config.potion().ingredients)) {
                    state = States.DEPOSIT
                    return
                }
                if(!client.inventoryContainsAll(config.potion().ingredients) && !client.inventoryContains(config.potion().product)) {
                    state = States.WITHDRAW
                    return
                }
                if(!closed && (client.inventoryContains(config.potion().ingredients) || client.inventoryContainsAll(config.potion().ingredients) && !mixing)) {
                    state = States.CLOSE_INTERFACE
                    closed = true
                    return
                }
            }
            when(config.potion()) {
                Potions.SERUM_207 -> {
                    if(client.inventoryContains(config.potion().ingredients)) {
                        state = States.MIX
                        performAction = true
                        return
                    }
                }
                else -> {
                    if(client.getWidget(WidgetInfo.MULTI_SKILL_MENU) != null && !mixing) {
                        state = States.CONFIRM
                        return
                    }
                    if(client.inventoryContainsAll(config.potion().ingredients) && !mixing) {
                        state = States.MIX
                        return
                    }
                }
            }
            if(client.inventoryContains(config.potion().product) && !client.inventoryContains(config.potion().ingredients)) {
                mixing = false
                index = 0
                state = States.DEPOSIT
                return
            }
            if(!client.inventoryContains(config.potion().ingredients.first()) && !client.inventoryContains(config.potion().ingredients.last()) && !client.inventoryContains(config.potion().product)) {
                state = States.WITHDRAW
                return
            }
            if((!client.inventoryContains(config.potion().ingredients.first()) || !client.inventoryContains(config.potion().ingredients.last())) && !client.inventoryContains(config.potion().product)) {
                state = States.WITHDRAW
                return
            }

        }
    }
}