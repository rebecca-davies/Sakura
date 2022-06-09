package net.runelite.client.plugins.oneclickshops.client

import net.runelite.api.Client
import net.runelite.api.GameState
import net.runelite.client.game.WorldService
import net.runelite.http.api.worlds.World
import net.runelite.http.api.worlds.WorldType
import javax.inject.Inject

class WorldHop {

	@Inject
	lateinit var client: Client

	@Inject
	private var worldService: WorldService? = null

	fun findNextWorld(): Int {
		val worldResult = worldService!!.worlds
		if (worldResult == null || client.gameState != GameState.LOGGED_IN) {
			return -1
		}
		val currentWorld = worldResult.findWorld(client.world) ?: return -1
		val currentWorldTypes = currentWorld.types.clone()
		currentWorldTypes.remove(WorldType.PVP)
		currentWorldTypes.remove(WorldType.HIGH_RISK)
		currentWorldTypes.remove(WorldType.BOUNTY)
		currentWorldTypes.remove(WorldType.SKILL_TOTAL)
		currentWorldTypes.remove(WorldType.LAST_MAN_STANDING)
		val worlds = worldResult.worlds
		var worldIdx = worlds.indexOf(currentWorld)
		val totalLevel = client.totalLevel
		var world: World
		do {
				worldIdx++
				if (worldIdx >= worlds.size) {
					worldIdx = 0
				}
			world = worlds[worldIdx]
			val types = world.types.clone()
			types.remove(WorldType.BOUNTY)
			types.remove(WorldType.LAST_MAN_STANDING)
			if (types.contains(WorldType.SKILL_TOTAL)) {
				try {
					val totalRequirement = world.activity.substring(0, world.activity.indexOf(" ")).toInt()
					if (totalLevel >= totalRequirement) {
						types.remove(WorldType.SKILL_TOTAL)
					}
				} catch (ex: NumberFormatException) {
				}
			}

			if (world.players >= 1500) {
				continue
			}

			if (currentWorldTypes == types) {
				break
			}
		} while (world !== currentWorld)
			return world.id
		}
}
