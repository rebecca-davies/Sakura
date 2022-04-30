package net.runelite.client.plugins.externals.oneclicklava

enum class Pouches(val pouch: String) {
    LOWER("Small + Medium + Large"),
    HIGHER("Large + Giant"),
    COLOSSAL("Colossal");

    override fun toString(): String {
        return pouch
    }
}