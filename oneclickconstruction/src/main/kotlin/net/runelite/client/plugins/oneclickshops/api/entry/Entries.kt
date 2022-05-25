package net.runelite.client.plugins.oneclickshops.api.entry

import net.runelite.api.*
import net.runelite.api.coords.LocalPoint
import net.runelite.api.coords.WorldPoint
import net.runelite.api.events.MenuOptionClicked
import net.runelite.api.widgets.Widget
import net.runelite.api.widgets.WidgetInfo
import net.runelite.rs.api.RSClient
import javax.inject.Inject

class Entries {

    @Inject
    lateinit var client: Client

    fun MenuOptionClicked.clickItem(item: Widget, action: Int, container: WidgetInfo) {
        try {
            this.menuOption = ""
            this.menuTarget = ""
            this.id = action
            this.menuAction = if (action < 6) MenuAction.CC_OP else MenuAction.CC_OP_LOW_PRIORITY
            this.param0 = item.index
            this.param1 = container.id
        } catch (e: Exception) {
            this.consume()
        }
    }

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

    fun MenuOptionClicked.walkTo(point: WorldPoint) {
        try {
            this.menuOption = "Walk here"
            this.menuTarget = ""
            this.id = 0
            this.menuAction = MenuAction.WALK
            this.param0 = point.x
            this.param1 = point.y
            this.consume()
            val rsclient = client as RSClient
            val local = LocalPoint.fromWorld(client, WorldPoint(this.param0, this.param1, client.plane))!!
            rsclient.selectedSceneTileX = local.sceneX
            rsclient.selectedSceneTileY = local.sceneY
            rsclient.setViewportWalking(true)
            rsclient.isCheckClick = false
        } catch (e: Exception) {
            this.consume()
        }
    }

    fun MenuOptionClicked.use(gameObject: GameObject) {
        try {
            this.menuOption = ""
            this.menuTarget = ""
            this.id = gameObject.id
            this.menuAction = MenuAction.GAME_OBJECT_FIRST_OPTION
            this.param0 = gameObject.sceneMinLocation.x
            this.param1 = gameObject.sceneMinLocation.y
        } catch (e: Exception) {
            this.consume()
        }
    }

    fun MenuOptionClicked.interact(gameObject: GameObject) {
        try {
            this.menuOption = "One click construction"
            this.menuTarget = ""
            this.id = gameObject.id
            this.menuAction = MenuAction.GAME_OBJECT_FIFTH_OPTION
            this.param0 = gameObject.sceneMinLocation.x
            this.param1 = gameObject.sceneMinLocation.y
        } catch (e: Exception) {
            this.consume()
        }
    }

    fun MenuOptionClicked.useOn(item: Widget, used: Widget?) {
        try {
            used?.let {
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

    fun MenuOptionClicked.talk(option: Int, widget: Int) {
        try {
            this.menuOption = ""
            this.menuTarget = ""
            this.id = 0
            this.menuAction = MenuAction.WIDGET_CONTINUE
            this.param0 = option
            this.param1 = widget
        } catch (e: Exception) {
            this.consume()
        }
    }

    fun MenuOptionClicked.trade(npc: NPC, action: MenuAction) {
        try {
            this.menuOption = ""
            this.menuTarget = ""
            this.id = npc.index
            this.menuAction = action
            this.param0 = 0
            this.param1 = 0
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
}

