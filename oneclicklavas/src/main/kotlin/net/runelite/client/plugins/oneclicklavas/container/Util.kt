package net.runelite.client.plugins.oneclicklavas.container

import net.runelite.api.Client
import net.runelite.api.Item
import net.runelite.api.ItemContainer
import net.runelite.client.OneClickLavasPlugin
import net.runelite.client.plugins.oneclicklavas.States
import javax.inject.Inject

@Inject
lateinit var client: Client

@Inject
lateinit var plugin: OneClickLavasPlugin

infix fun Array<Item>?.compare(item: Array<Item>?) {
    with(plugin) {
        when (state) {
            States.FILL_POUCH -> {
                println("test1")
                attributes.computeIfPresent("filled") { k, v -> v + 1 }
                return
            }
            States.EMPTY_POUCHES -> {
                attributes.computeIfPresent("binds") { k, v -> v + 1 }
                return
            }
            else -> return
        }
    }
}