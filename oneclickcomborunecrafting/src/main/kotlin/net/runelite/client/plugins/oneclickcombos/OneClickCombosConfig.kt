package net.runelite.client.plugins.zeahcrafter

import net.runelite.client.config.*
import net.runelite.client.plugins.oneclickcombos.*

@ConfigGroup("oneclickcombos")
interface OneClickCombosConfig : Config {

    companion object {
        @JvmField
        @ConfigTitle(
            keyName = "pouchConfig",
            name = "OneClickCombos",
            description = "Configure your plugins settings to suit your needs",
            position = 0)
        val pouchConfig: String = "pouchConfig"
    }
    @ConfigItem(
        keyName = "text",
        name = "",
        description = "",
        position = 1,
        title = "pouchConfig")
    @JvmDefault
    fun text(): String {
        return "Configure your settings to suit your needs, make sure your run energy potions are decanted into one doses with barbarian vial smashing enabled. If you get stuck, please check out the discord help section."
    }
    @ConfigItem(
        keyName = "rune",
        name = "Rune",
        description = "Select the rune you want to craft",
        position = 2,
        title = "pouchConfig")
    @JvmDefault
    fun rune(): RuneType {
        return RuneType.LAVA
    }

    @ConfigItem(
        keyName = "pouch",
        name = "Pouch",
        description = "Select the pouches you want to use",
        position = 3,
        title = "pouchConfig")
    @JvmDefault
    fun pouch(): Pouches {
        return Pouches.COLOSSAL
    }

    @ConfigItem(
        keyName = "altar",
        name = "Altar",
        description = "Select the altar teleport method",
        position = 4,
        title = "pouchConfig")
    @JvmDefault
    fun altar(): AltarTeleport {
        return AltarTeleport.RING_OF_THE_ELEMENTS
    }

    @ConfigItem(
        keyName = "banking",
        name = "Bank",
        description = "Select the banking teleport method",
        position = 5,
        title = "pouchConfig")
    @JvmDefault
    fun banking(): BankTeleport {
        return BankTeleport.CRAFTING_CAPE
    }

    @ConfigItem(
        keyName = "stamina",
        name = "Energy",
        description = "Select if the method of run energy restoration you'd like to use",
        position = 6,
        title = "pouchConfig")
    @JvmDefault
    fun stamina(): RunEnergy {
        return RunEnergy.STAMINA_POTION
    }
}
