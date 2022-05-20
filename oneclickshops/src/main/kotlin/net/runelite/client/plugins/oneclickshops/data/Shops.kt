package net.runelite.client.plugins.oneclickshops.data

import net.runelite.client.plugins.oneclickshops.shops.impl.Charter

enum class Shops(val store: String, val npc: Array<Int>, val stock: Array<Int>, val category: String, val clazz: Class<Charter>) {
    CHARTER(store = "Charter shops", npc = arrayOf(9307, 9313, 9321, 9327, 9329, 9330, 9334,  9338, 9342, 9343, 9346, 9353, 9357, 9358, 9363, 9366, 9373, 9374, 9379, 9382),
        stock = arrayOf(1931, 1935, 20742, 1735, 1925, 22660, 1923, 1887, 590, 1755, 2347, 550, 9003, 954, 946, 2114, 1963, 2108, 4286, 1785, 1783, 401, 1781, 301, 307, 1941, 9629, 3226, 1025),
        category = "Charter", clazz = Charter::class.java);

    override fun toString(): String {
        return store
    }
}