package net.runelite.client

import com.google.inject.Provides
import net.runelite.api.*
import net.runelite.api.events.GameTick
import net.runelite.api.events.HitsplatApplied
import net.runelite.api.events.MenuOptionClicked
import net.runelite.api.widgets.WidgetInfo.BANK_ITEM_CONTAINER as bank
import net.runelite.api.widgets.WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER as bankInventory
import net.runelite.api.widgets.WidgetInfo.INVENTORY as inventory
import net.runelite.client.config.ConfigManager
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.events.ConfigChanged
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor
import net.runelite.client.plugins.oneclickwintertodt.States
import net.runelite.client.plugins.oneclickwintertodt.magic.*
import net.runelite.client.plugins.oneclickwintertodt.api.entry.Entries
import net.runelite.client.plugins.oneclickwintertodt.util.Log
import net.runelite.client.plugins.oneclickwintertodt.OneClickWintertodtConfig
import net.runelite.client.plugins.oneclickwintertodt.api.inventory.Inventory
import net.runelite.client.plugins.oneclickwintertodt.client.*
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
    lateinit var events: Entries

    @Inject
    lateinit var inventories: Inventory

    companion object : Log()

    @Provides
    fun provideConfig(configManager: ConfigManager): OneClickWintertodtConfig {
        return configManager.getConfig(OneClickWintertodtConfig::class.java)
    }

    var performAction = true
    private lateinit var food: OneClickWintertodtConfig.Food
    private var gameStarted = true
    private var bankChest: GameObject? = null
    private var door: GameObject? = null
    private var hammerCrate: GameObject? = null
    private var tinderboxCrate: GameObject? = null
    private var knifeCrate: GameObject? = null
    private var vialCrate: GameObject? = null
    private var unlitBrazier: GameObject? = null
    private var litBrazier: GameObject? = null
    private var brokenBrazier: GameObject? = null
    private var roots: GameObject? = null
    private var herbPatch: GameObject? = null

    override fun startUp() {
        log.info("Starting One Click Wintertodt")
        reset()
    }

    override fun shutDown() {
        log.info("Stopping One Click Wintertodt")
    }

    private fun reset() {
        performAction = true
        gameStarted = false
        food = config.food()
        state = States.IDLE
    }

    private var itemContainer: Array<Item> by Delegates.observable(arrayOf()) { property, previous, current ->
        if(state == States.EAT) {
            performAction = true
        }
    }

    private var state by Delegates.observable(States.IDLE) { property, previous, current ->
        if (previous != current) {
            performAction = true
        }
    }

    @Subscribe
    private fun onGameTick(event: GameTick) {
        bankChest = client.findGameObject(BANK_CHEST)
        door = client.findGameObject(DOOR)
        hammerCrate = client.findGameObject(HAMMER_CRATE)
        tinderboxCrate = client.findGameObject(TINDERBOX_CRATE)
        knifeCrate = client.findGameObject(KNIFE_CRATE)
        vialCrate = client.findGameObject(VIAL_CRATE)
        unlitBrazier = client.findGameObject(UNLIT_BRAZIER)?.takeIf { it.worldLocation == SE_POS }
        litBrazier = client.findGameObject(LIT_BRAZIER)?.takeIf { it.worldLocation == SE_POS }
        brokenBrazier = client.findGameObject(BROKEN_BRAZIER)?.takeIf { it.worldLocation == SE_POS }
        roots = client.findGameObject(ROOT)?.takeIf { it.worldLocation == SE_ROOT_POS }
        herbPatch = client.findGameObject(HERB_PATCH)?.takeIf { it.worldLocation == HERB_POS }
    }

    @Subscribe
    private fun onConfigChanged(event: ConfigChanged) {
        food = config.food()
    }

    @Subscribe
    private fun onHitsplatApplied(event: HitsplatApplied) {
        if(event.actor == client.localPlayer) {
            performAction = true
            return
        }
    }

    @Subscribe
    fun onMenuEntryClicked(event: MenuOptionClicked) {
        with(inventories) {
            with(events) {
                performChecks()
                client.getItemContainer(InventoryID.INVENTORY.id)?.let { itemContainer = it.items }
                if (!performAction) {
                    event.consume()
                }
                performAction = false
                when (state) {
                    States.HEAL_PYROMANCER -> {
                        if (inventory.contains(HEALING_POTIONS)) {
                            event.healPyromancer()
                        }
                        return
                    }
                    States.WALK_TO_SE -> {
                        event.walkTo(SOUTHEAST)
                        return
                    }
                    States.PICK_HERB -> {
                        herbPatch?.let {
                            event.use(it)
                            return
                        }
                    }
                    States.MIX_VIAL -> {
                        inventory.getItem(ItemID.BRUMA_ROOT)?.let {
                            event.useOn(it, inventory.getItem(ItemID.REJUVENATION_POTION_UNF))
                            return
                        }
                    }
                    States.REPAIR -> {
                        brokenBrazier?.let {
                            event.use(it)
                            return
                        }
                    }
                    States.WITHDRAW_FOOD -> {
                        bank.getItem(food.id)?.let {
                            event.clickItem(it, 3, bank)
                            return
                        }
                    }
                    States.DEPOSIT_ITEMS -> {
                        bankInventory.getItem(ItemID.SUPPLY_CRATE)?.let {
                            event.clickItem(it, 3, bankInventory)
                            return
                        }
                    }
                    States.EAT -> {
                        val food = inventory.getItem(food.id)
                        if (food != null) {
                            state = States.IDLE
                            event.clickItem(food, 2, inventory)
                        }
                        return
                    }
                    States.RETURN_INSIDE -> {
                        door?.let {
                            event.use(it)
                            return
                        }
                    }
                    States.LEAVE_DOOR -> {
                        door?.let {
                            event.use(it)
                            state = States.CONFIRM_EXIT
                            return
                        }
                    }
                    States.CONFIRM_EXIT -> {
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
                        unlitBrazier?.let {
                            event.use(it)
                            return
                        }
                    }
                    States.FIREMAKING -> {
                        litBrazier?.let {
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
                        inventory.getItem(ItemID.BRUMA_ROOT)?.let {
                            event.useOn(it, inventory.getItem(ItemID.KNIFE))
                            return
                        }
                    }
                    States.BANK -> {
                        bankChest?.let {
                            event.use(it)
                            return
                        }
                    }
                    States.IDLE -> {}
                }
            }
        }
    }

    private fun performChecks() {
        with(inventories) {
            when (client.localPlayer!!.worldLocation.regionID) {
                BANK_REGION -> {
                    if (client.banking()) {
                        if (bankInventory.quantity(food.id) <= 4) {
                            state = States.WITHDRAW_FOOD
                            return
                        }
                        if (bankInventory.contains(ItemID.SUPPLY_CRATE)) {
                            state = States.DEPOSIT_ITEMS
                            return
                        }
                    }
                    bankChest?.let {
                        if (inventory.quantity(food.id) <= 4 ||  inventory.contains(ItemID.SUPPLY_CRATE)) {
                            if (!client.localPlayer.isMoving) {
                                performAction = true
                            }
                            state = States.BANK
                            return
                        }
                    }
                    state = States.RETURN_INSIDE
                    return
                }
                LOBBY_REGION -> {
                    if (state == States.CONFIRM_EXIT) {
                        if (client.getWidget(14352385) != null) {
                            performAction = true
                        } else {
                            return
                        }
                        return
                    }
                    if (state == States.LEAVE_DOOR) {
                        return
                    }
                    if (client.localPlayer.worldLocation.isInArea(LOBBY_AREA)) {
                        performAction = true
                    }
                    if (client.getBoostedSkillLevel(Skill.HITPOINTS) <= (client.getRealSkillLevel(Skill.HITPOINTS) / 2.5)) {
                        if (!inventory.contains(food.id)) {
                            state = States.LEAVE_DOOR
                            return
                        }
                        state = States.EAT
                        return
                    }
                    if (roots == null || state == States.GO_TO_BRAZIER && unlitBrazier == null) {
                        state = States.WALK_TO_SE
                        performAction = true
                        return
                    }
                    if (client.findNpc(NpcID.INCAPACITATED_PYROMANCER)?.worldLocation == SE_PYROMANCER_POS && inventory.contains(HEALING_POTIONS)) {
                        state = States.HEAL_PYROMANCER
                        return
                    }
                    if (inventory.contains(ItemID.BRUMA_HERB) && inventory.contains(ItemID.REJUVENATION_POTION_UNF)) {
                        state = States.MIX_VIAL
                        return
                    }
                    if (client.findNpc(NpcID.INCAPACITATED_PYROMANCER)?.worldLocation == SE_PYROMANCER_POS && inventory.contains(ItemID.REJUVENATION_POTION_UNF) && !inventory.contains(ItemID.BRUMA_HERB)) {
                        state = States.PICK_HERB
                        return
                    }
                    if (!gameStarted && inventory.quantity(food.id) <= 4) {
                        state = States.LEAVE_DOOR
                        return
                    }
                    if (litBrazier != null && !gameStarted) {
                        gameStarted = true
                    }
                    if (inventory.contains(ItemID.SUPPLY_CRATE)) {
                        state = States.LEAVE_DOOR
                        return
                    }
                    if (state == States.FIREMAKING && brokenBrazier != null) {
                        state = States.REPAIR
                        return
                    }
                    if (state == States.LIGHT_BRAZIER && unlitBrazier != null) {
                        return
                    }
                    if (!inventory.contains(ItemID.REJUVENATION_POTION_UNF) && !inventory.contains(HEALING_POTIONS)) {
                        state = States.NEED_VIAL
                        return
                    }
                    if (!inventory.contains(ItemID.HAMMER)) {
                        state = States.NEED_HAMMER
                        return
                    }
                    if (!inventory.contains(ItemID.KNIFE)) {
                        state = States.NEED_KNIFE
                        return
                    }
                    if (!inventory.contains(ItemID.TINDERBOX)) {
                        state = States.NEED_TINDERBOX
                        return
                    }
                    if (gameStarted && state == States.GO_TO_BRAZIER && client.getWidget(INTERFACE_TEXT)!!.text.isEmpty()) {
                        state = States.LIGHT_BRAZIER
                        return
                    }
                    if (client.getWidget(INTERFACE_TEXT)?.text?.contains("0:00", true) == true) {
                        gameStarted = true
                        return
                    }
                    if (client.getWidget(INTERFACE_TEXT)?.text?.contains("returns in", true) == true) {
                        state = States.GO_TO_BRAZIER
                        gameStarted = false
                        return
                    }
                    if (unlitBrazier != null && (state == States.FIREMAKING || state == States.GO_TO_BRAZIER)) {
                        state = States.LIGHT_BRAZIER
                        return
                    }
                    if (inventory.contains(ItemID.BRUMA_KINDLING) && !inventory.contains(ItemID.BRUMA_ROOT)) {
                        state = States.FIREMAKING
                        return
                    }
                    if (inventory.contains(ItemID.BRUMA_ROOT) && (inventory.freeSpace() <= 0 || (inventory.quantity(ItemID.BRUMA_ROOT) + inventory.quantity(ItemID.BRUMA_KINDLING)) >= 10)) {
                        state = States.FLETCHING
                        return
                    }
                    if (!inventory.contains(ItemID.BRUMA_KINDLING)) {
                        state = States.WOODCUTTING
                        return
                    }
                    state = States.IDLE
                    return
                }
                else -> {}
            }
        }
    }
}