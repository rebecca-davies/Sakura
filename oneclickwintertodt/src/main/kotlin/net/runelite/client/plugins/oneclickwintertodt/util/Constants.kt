package net.runelite.client.plugins.oneclickwintertodt.magic

import net.runelite.api.ItemID
import net.runelite.api.coords.WorldArea
import net.runelite.api.coords.WorldPoint

const val BANK_CHEST = 29321
const val DOOR = 29322
const val BANK_REGION = 6461
const val LOBBY_REGION = 6462
const val HAMMER_CRATE = 29316
const val KNIFE_CRATE = 29317
const val VIAL_CRATE = 29320
const val HERB_PATCH = 29315
const val TINDERBOX_CRATE = 29319
const val LIT_BRAZIER = 29314
const val UNLIT_BRAZIER = 29312
const val BROKEN_BRAZIER = 29313
const val INTERFACE_TEXT = 25952259
const val ROOT = 29311
const val DOWNED_PYROMANCER = 7372
const val WINTERTODT_INTERFACE = 25952258

val SE_POS = WorldPoint(1639, 3998, 0)
val SE_PYROMANCER_POS = WorldPoint(1641, 3996, 0)
val SE_ROOT_POS = WorldPoint(1639, 3988, 0)
val SE_HERB_POS = WorldPoint(1649, 4007, 0)
val SOUTHEAST = WorldPoint(1640, 3993, 0)

val SW_POS = WorldPoint(1621, 3998, 0)
val SW_PYROMANCER_POS = WorldPoint(1619, 3996, 0)
val SW_ROOT_POS = WorldPoint(1620, 3988, 0)
val SOUTHWEST = WorldPoint(1619, 3993, 0)

val LOBBY_AREA = WorldArea(1626, 3978, 8, 8, 0)
val HEALING_POTIONS = listOf(ItemID.REJUVENATION_POTION_1, ItemID.REJUVENATION_POTION_2, ItemID.REJUVENATION_POTION_3, ItemID.REJUVENATION_POTION_4)
