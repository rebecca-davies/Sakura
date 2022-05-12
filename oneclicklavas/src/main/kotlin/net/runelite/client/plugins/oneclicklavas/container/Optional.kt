package net.runelite.client.plugins.oneclicklavas.container

data class Optional<T>(val value: T?)
fun <T> T?.asOptional() = Optional(this)
