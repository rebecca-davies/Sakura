package net.runelite.client

import com.google.inject.Provides
import net.runelite.api.*
import net.runelite.api.coords.LocalPoint
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.ChatMessage
import net.runelite.api.events.HitsplatApplied
import net.runelite.api.events.MenuOptionClicked
import net.runelite.api.widgets.WidgetInfo
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.oneclicklavas.*
import net.runelite.client.plugins.oneclickwintertodt.States
import net.runelite.client.plugins.oneclickwintertodt.magic.*
import net.runelite.client.plugins.oneclickwintertodt.util.Actions
import net.runelite.client.plugins.oneclickwintertodt.util.Log
import net.runelite.client.plugins.oneclickwintertodt.OneClickWintertodtConfig
import org.pf4j.Extension
import javax.inject.Inject
import kotlin.properties.Delegates

@Extension
@PluginDescriptor(
    name = "One Click Wintertodt",
    description = ":Prayje:",
    tags = ["rebecca, oneclick, one click, wintertodt"]
)
class OneClickWintertodtPlugin : Plugin() {

    @Inject
    private lateinit var config: OneClickWintertodtConfig

    @Inject
    lateinit var client: Client

    @Inject
    lateinit var actions: Actions

    companion object : Log()

    @Provides
    fun provideConfig(configManager: ConfigManager): OneClickWintertodtConfig {
        return configManager.getConfig(OneClickWintertodtConfig::class.java)
    }

    var process = true
    private var gameStarted = true
    var se = true
    lateinit var food: OneClickWintertodtConfig.Food

    override fun startUp() {
        log.info("Starting One Click Wintertodt")

        reset()
    }

    override fun shutDown() {
        log.info("Stopping One Click Wintertodt")
    }

    private fun reset() {
        process = true
        gameStarted = false
        state = States.IDLE
        se = true
    }

    private var items: Array<Item> by Delegates.observable(arrayOf()) { _, prev, curr ->
        if(state == States.EAT) {
            process = true
        }
    }

    private var state by Delegates.observable(States.IDLE) { _, prev, curr ->
        if (prev != curr) {
            process = true
        }
    }

    @Subscribe
    fun onChatMessage(event: ChatMessage) {
    }

    @Subscribe
    private fun onHitsplatApplied(event: HitsplatApplied) {
        if(event.actor == client.localPlayer) {
            process = true
            return
        }
    }

    @Subscribe
    fun onMenuEntryClicked(event: MenuOptionClicked) {
        with(actions) {
            checkStates()
            client.addChatMessage(ChatMessageType.GAMEMESSAGE, "Sakura", "state=$state process=$process", "")
            client.getItemContainer(InventoryID.INVENTORY.id)?.let {
                items = it.items
            }
            if (!process) {
                event.consume()
            }
            process = false
            val bank = client.findGameObject(BANK)
            val door = client.findGameObject(DOOR)
            val hammerCrate = client.findGameObject(HAMMER_CRATE)
            val tinderboxCrate = client.findGameObject(TINDERBOX_CRATE)
            val knifeCrate = client.findGameObject(KNIFE_CRATE)
            val vialCrate = client.findGameObject(VIAL_CRATE)
            val unlit = client.findGameObject(UNLIT_BRAZIER)?.takeIf { it.worldLocation == SE_POS}
            val lit = client.findGameObject(LIT_BRAZIER)?.takeIf { it.worldLocation == SE_POS}
            val broken = client.findGameObject(BROKEN_BRAZIER)?.takeIf { it.worldLocation == SE_POS}
            val roots = client.findGameObject(ROOT)?.takeIf { it.worldLocation == SE_ROOT_POS}
            val herbPatch = client.findGameObject(HERB)?.takeIf { it.worldLocation == HERB_POS}

            when (state) {
                States.HEAL_PYROMANCER -> {
                    if(client.inventoryContains(POTIONS)) {
                        event.heal()
                        return
                    }
                    return
                }
                States.WALK_TO_SE -> {
                    event.walk()
                    return
                }
                States.PICK_HERB -> {
                    herbPatch?.let {
                        event.use(it)
                        return
                    }
                }
                States.MIX_VIAL -> {
                    client.getInventoryItem(HERB)?.let {
                        event.useOn(it, client.getInventoryItem(ItemID.VIAL)!!)
                        return
                    }
                    return
                }
                States.REPAIR -> {
                    broken?.let {
                        event.use(it)
                        return
                    }
                }
                States.WITHDRAW_FOOD -> {
                    client.getBankItem(food.id)?.let {
                        event.clickItem(it, 4, WidgetInfo.BANK_ITEM_CONTAINER.id)
                        return
                    }
                }
                States.DEPOSIT_ITEMS -> {
                    client.getBankInventoryItem(ItemID.SUPPLY_CRATE)?.let {
                        event.clickItem(it, 3, WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.id)
                        return
                    }
                }
                States.EAT -> {
                    val food = client.getInventoryItem(food.id)
                    if(food != null) {
                        state = States.IDLE
                        event.clickItem(food, 2, WidgetInfo.INVENTORY.id)
                        return
                    }
                    state = States.PREPARE
                    return
                }
                States.RETURN_INSIDE -> {
                    door?.let {
                        event.use(it)
                        return
                    }
                }
                States.PREPARE -> {
                    door?.let {
                        event.use(it)
                        state = States.CONFIRM
                        return
                    }
                }
                States.CONFIRM -> {
                    event.talk(1, 14352385)
                    return
                }
                States.NEED_VIAL -> {
                    event.use(vialCrate!!)
                    return
                }
                States.NEED_HAMMER -> {
                    event.use(hammerCrate!!)
                    return
                }
                States.NEED_KNIFE -> {
                    event.use(knifeCrate!!)
                    return
                }
                States.NEED_TINDERBOX -> {
                    event.use(tinderboxCrate!!)
                    return
                }
                States.LIGHT_BRAZIER, States.GO_TO_BRAZIER -> {
                    unlit?.let {
                        event.use(it)
                        return
                    }
                }
                States.FIREMAKING -> {
                    lit?.let {
                        event.use(it)
                        return
                    }
                }
                States.WOODCUTTING -> {
                    roots?.let {
                        event.use(it)
                        return
                    }
                }
                States.FLETCHING -> {
                    client.getInventoryItem(LOG)?.let {
                        event.useOn(it, client.getInventoryItem(ItemID.KNIFE)!!)
                        return
                    }
                }
                States.BANK -> {
                    bank?.let {
                        event.use(it)
                        return
                    }
                }
                else -> return
            }
        }
    }

    private fun checkStates() {
        food = config.food()
        val unlit = client.findGameObject(UNLIT_BRAZIER)?.takeIf { it.worldLocation == SE_POS }
        val lit = client.findGameObject(LIT_BRAZIER)?.takeIf { it.worldLocation == SE_POS }
        val broken = client.findGameObject(BROKEN_BRAZIER)?.takeIf { it.worldLocation == SE_POS }
        val bank = client.findGameObject(BANK)
        val roots = client.findGameObject(ROOT)?.takeIf { it.worldLocation == SE_ROOT_POS}
        food = config.food()

        when(client.localPlayer!!.worldLocation.regionID) {
            BANK_REGION -> {
                if(client.banking()) {
                    if(client.getBankInventoryItem(food.id) == null || client.bankInventoryQuantity(food.id) <= 5) {
                        state = States.WITHDRAW_FOOD
                        return
                    }
                    if(client.getBankInventoryItem(ItemID.SUPPLY_CRATE) != null) {
                        state = States.DEPOSIT_ITEMS
                        return
                    }
                }
                bank?.let {
                    if(!client.inventoryContains(food.id) || client.inventoryContains(ItemID.SUPPLY_CRATE)) {
                        if(!client.localPlayer.isMoving) {
                            process = true
                        }
                        state = States.BANK
                        return
                    }
                }
                state = States.RETURN_INSIDE
                return
            }
            LOBBY_REGION -> {
                if(state == States.CONFIRM) {
                    if(client.getWidget(14352385) != null) {
                        process = true
                    } else {
                        return
                    }
                    return
                }
                if(state == States.PREPARE) {
                    return
                }
                if(client.localPlayer.worldLocation.isInArea(LOBBY_AREA)) {
                    process = true
                }
                if(client.getBoostedSkillLevel(Skill.HITPOINTS) <= (client.getRealSkillLevel(Skill.HITPOINTS) / 2.5)) {
                    if(!client.inventoryContains(food.id)) {
                        state = States.PREPARE
                        return
                    }
                    state = States.EAT
                    return
                }
                if(roots == null || state == States.GO_TO_BRAZIER && unlit == null) {
                    state = States.WALK_TO_SE
                    process = true
                    return
                }
                if(lit != null && !gameStarted) {
                    gameStarted = true
                }
                if(client.findNpc(DOWNED_PYROMANCER)?.worldLocation == SE_PYROMANCER_POS && client.inventoryContains(POTIONS)) {
                    state = States.HEAL_PYROMANCER
                    return
                }
                if(client.inventoryContains(HERB) && client.inventoryContains(VIAL)) {
                    state = States.MIX_VIAL
                    return
                }
                if(client.findNpc(DOWNED_PYROMANCER)?.worldLocation == SE_PYROMANCER_POS && client.inventoryContains(VIAL) && !client.inventoryContains(HERB)) {
                    state = States.PICK_HERB
                    return
                }
                if(client.inventoryContains(ItemID.SUPPLY_CRATE)) {
                    state = States.PREPARE
                    return
                }
                if(state == States.FIREMAKING && broken != null) {
                    state = States.REPAIR
                    return
                }
                if(state == States.LIGHT_BRAZIER && unlit != null) {
                    return
                }
                if(!client.inventoryContains(ItemID.REJUVENATION_POTION_UNF) && !client.inventoryContains(POTIONS)) {
                    state = States.NEED_VIAL
                    return
                }
                if(!client.inventoryContains(ItemID.HAMMER)) {
                    state = States.NEED_HAMMER
                    return
                }
                if(!client.inventoryContains(ItemID.KNIFE)) {
                    state = States.NEED_KNIFE
                    return
                }
                if(!client.inventoryContains(ItemID.TINDERBOX)) {
                    state = States.NEED_TINDERBOX
                    return
                }
                if(gameStarted && state == States.GO_TO_BRAZIER && client.getWidget(INTERFACE_TEXT)!!.text.isEmpty()) {
                    state = States.LIGHT_BRAZIER
                    return
                }
                if(client.getWidget(INTERFACE_TEXT)?.text?.contains("0:00", true) == true) {
                    gameStarted = true
                    return
                }
                if(client.getWidget(INTERFACE_TEXT)?.text?.contains("returns in", true) == true) {
                    state = States.GO_TO_BRAZIER
                    gameStarted = false
                    return
                }
                if(unlit != null && (state == States.FIREMAKING || state == States.GO_TO_BRAZIER)) {
                    state = States.LIGHT_BRAZIER
                    return
                }
                if(client.inventoryContains(KINDLING) && !client.inventoryContains(LOG)) {
                    state = States.FIREMAKING
                    return
                }
                if(client.inventoryContains(LOG) && (client.getInventorySpace() <= 0 || (client.inventoryQuantity(LOG) + client.inventoryQuantity(KINDLING)) >= 10)) {
                    state = States.FLETCHING
                    return
                }
                if(!client.inventoryContains(LOG) && !client.inventoryContains(KINDLING)) {
                    state = States.WOODCUTTING
                    return
                }
                state = States.IDLE
                return
            }
        }
    }
}


