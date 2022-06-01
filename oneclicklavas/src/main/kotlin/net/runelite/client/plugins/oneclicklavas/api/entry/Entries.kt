package net.runelite.client.plugins.oneclicklavas.api.entry

import net.runelite.api.*
import net.runelite.api.events.MenuOptionClicked
import net.runelite.api.widgets.Widget
import net.runelite.client.OneClickLavasPlugin
import net.runelite.client.plugins.oneclicklavas.client.getInventoryItem
import javax.inject.Inject

class Entries {

    @Inject
    lateinit var client: Client

    @Inject
    lateinit var plugin: OneClickLavasPlugin

    fun MenuOptionClicked.clickItem(item: Widget, action: Int, container: Int) {
        try {
            this.menuOption = "One Click Lavas"
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
            this.menuOption = "One Click Lavas"
            this.menuTarget = ""
            this.id = 1
            this.menuAction = MenuAction.CC_OP
            this.param0 = 11
            this.param1 = 786434
        } catch (e: Exception) {
            this.consume()
        }
    }

    fun MenuOptionClicked.teleport(action: Int, container: Int) {
        try {
            this.menuOption = "One Click Lavas"
            this.menuTarget = ""
            this.id = action
            this.menuAction = if (action < 6) MenuAction.CC_OP else MenuAction.CC_OP_LOW_PRIORITY
            this.param0 = -1
            this.param1 = container
        } catch (e: Exception) {
            this.consume()
        }
    }

    fun MenuOptionClicked.imbue() {
        try {
            this.menuOption = "One Click Lavas"
            this.menuTarget = ""
            this.id = 1
            this.menuAction = MenuAction.CC_OP
            this.param0 = -1
            this.param1 = 14286973
        } catch (e: Exception) {
            this.consume()
        }
    }

    fun MenuOptionClicked.use(gameObject: GameObject) {
        try {
            this.menuOption = "One Click Lavas"
            this.menuTarget = ""
            this.id = gameObject.id
            this.menuAction = MenuAction.GAME_OBJECT_FIRST_OPTION
            this.param0 = gameObject.sceneMinLocation.x
            this.param1 = gameObject.sceneMinLocation.y
        } catch (e: Exception) {
            this.consume()
        }
    }

    fun MenuOptionClicked.destroy() {
        try {
            this.menuOption = "Destroy"
            this.menuTarget = "<col=ff9040>Binding necklace</col>"
            this.id = 7
            this.menuAction = MenuAction.CC_OP_LOW_PRIORITY
            this.param0 = client.getInventoryItem(ItemID.BINDING_NECKLACE)!!.index
            this.param1 = 9764864
        } catch (e: Exception) {
            this.consume()
        }
    }

    fun MenuOptionClicked.confirm() {
        try {
            this.menuOption = "Yes"
            this.menuTarget = ""
            this.id = 1
            this.menuAction = MenuAction.CC_OP
            this.param0 = -1
            this.param1 = 38273025
        } catch (e: Exception) {
            this.consume()
        }
    }

    fun MenuOptionClicked.useOn(item: Int, gameObject: GameObject) {
        try {
            client.getInventoryItem(item)?.let {
                client.selectedSpellWidget = it.id
                client.selectedSpellChildIndex = it.index
                client.selectedSpellItemId = it.itemId
                this.menuOption = "One Click Lavas"
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

    private fun MenuOptionClicked.repair() {
        try {
            this.menuOption = "One Click Lavas"
            this.menuTarget = ""
            this.id = 1
            this.menuAction = MenuAction.CC_OP
            this.param0 = -1
            this.param1 = 14286953
        } catch (e: Exception) {
            this.consume()
        }
    }

    private fun MenuOptionClicked.mage() {
        try {
            this.menuOption = "One Click Lavas"
            this.menuTarget = ""
            this.id = 1
            this.menuAction = MenuAction.CC_OP
            this.param0 = -1
            this.param1 = 4915212
        } catch (e: Exception) {
            this.consume()
        }
    }

    private fun MenuOptionClicked.talk(param0: Int, param1: Int) {
        try {
            this.menuOption = "One Click Lavas"
            this.menuTarget = ""
            this.id = 0
            this.menuAction = MenuAction.WIDGET_CONTINUE
            this.param0 = param0
            this.param1 = param1
        } catch (e: Exception) {
            this.consume()
        }
    }

    fun MenuOptionClicked.handleMage() {
        when (plugin.attributes["repair"]) {
            1 -> {
                repair()
                plugin.attributes["repair"] = 2
            }
            2 -> {
                mage()
                plugin.attributes["repair"] = 3
            }
            3 -> {
                client.getWidget(15138821)?.let {
                    talk(-1, 15138821)
                }

                client.getWidget(14352385)?.dynamicChildren?.forEachIndexed { index, widget ->
                    if(widget.text.contains("repair", true)) {
                        talk(index, 14352385)
                    }
                }
                client.getWidget(14221317)?.let {
                    talk(-1, 14221317)
                    plugin.attributes["repair"] = 0
                    plugin.repaired = true
                }
            }
        }
        if (menuOption.equals("Walk here", ignoreCase = true)) {
            consume()
            return
        }
    }
}

