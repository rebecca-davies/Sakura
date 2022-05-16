package net.runelite.client.plugins.oneclickwintertodt.util

import net.runelite.api.*
import net.runelite.api.events.MenuOptionClicked
import net.runelite.api.widgets.Widget
import net.runelite.client.OneClickWintertodtPlugin
import net.runelite.client.plugins.oneclicklavas.getInventoryItem
import javax.inject.Inject

class Actions {

    @Inject
    lateinit var client: Client

    @Inject
    lateinit var plugin: OneClickWintertodtPlugin

    fun MenuOptionClicked.clickItem(item: Widget, action: Int, container: Int) {
        try {
            this.menuOption = ""
            this.menuTarget = ""
            this.id = action
            this.menuAction = if (action < 6) MenuAction.CC_OP else MenuAction.CC_OP_LOW_PRIORITY
            this.param0 = item.index
            this.param1 = container
        } catch (e: Exception) {
            this.consume()
        }
    }

    fun MenuOptionClicked.closeBank() {
        try {
            this.menuOption = "Close"
            this.menuTarget = ""
            this.id = 1
            this.menuAction = MenuAction.CC_OP
            this.param0 = 11
            this.param1 = 786434
        } catch (e: Exception) {
            this.consume()
        }
    }

    fun MenuOptionClicked.use(gameObject: TileObject) {
        val location = if(gameObject is GameObject) Location(gameObject.sceneMinLocation.x, gameObject.sceneMinLocation.y) else Location(gameObject.localLocation.sceneX, gameObject.localLocation.sceneY)
        try {
            this.menuOption = ""
            this.menuTarget = ""
            this.id = gameObject.id
            this.menuAction = MenuAction.GAME_OBJECT_FIRST_OPTION
            this.param0 = location.x
            this.param1 = location.y
        } catch (e: Exception) {
            this.consume()
        }
    }

    fun MenuOptionClicked.heal() {
        try {
            this.menuOption = "Help"
            this.menuTarget = "<col=ffff00>Incapacitated Pyromancer"
            this.id = 475
            this.menuAction = MenuAction.NPC_FIRST_OPTION
            this.param0 = 0
            this.param1 = 0
        } catch (e: Exception) {
            this.consume()
        }
    }

    fun MenuOptionClicked.useOn(gameObject: GameObject) {
        try {
            client.getInventoryItem(ItemID.EARTH_RUNE)?.let {
                client.selectedSpellWidget = it.id
                client.selectedSpellChildIndex = it.index
                client.selectedSpellItemId = it.itemId
                this.menuOption = "Use"
                this.menuTarget = ""
                this.id = gameObject.id
                this.menuAction = MenuAction.WIDGET_TARGET_ON_GAME_OBJECT
                this.param0 = gameObject.sceneMinLocation.x
                this.param1 = gameObject.sceneMinLocation.y
            }
        } catch (e: Exception) {
            this.consume()
        }
    }

    fun MenuOptionClicked.useOn(item: Widget, used: Widget) {
        try {
            client.getInventoryItem(used.itemId)?.let {
                client.selectedSpellWidget = used.id
                client.selectedSpellChildIndex = used.index
                client.selectedSpellItemId = used.itemId
                this.menuOption = ""
                this.menuTarget = ""
                this.id = 0
                this.menuAction = MenuAction.WIDGET_TARGET_ON_WIDGET
                this.param0 = item.index
                this.param1 = 9764864
            }
        } catch (e: Exception) {
            this.consume()
        }
    }

    fun MenuOptionClicked.talk(param0: Int, param1: Int) {
        try {
            this.menuOption = ""
            this.menuTarget = ""
            this.id = 0
            this.menuAction = MenuAction.WIDGET_CONTINUE
            this.param0 = param0
            this.param1 = param1
        } catch (e: Exception) {
            this.consume()
        }
    }
}

