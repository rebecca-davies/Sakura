package net.runelite.client.plugins.zeahcrafter

import net.runelite.client.config.*
import net.runelite.client.plugins.oneclicklavas.*

@ConfigGroup("OneClickLavas")
interface OneClickLavasConfig : Config {

    companion object {
        @JvmField
        @ConfigTitle(
            keyName = "pouchConfig",
            name = "OneClickLavas",
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
        return "Configure your settings to suit your seeds, currently only supports colossal pouches, make sure your run energy potions are decanted into one doses with barbarian vial smashing enabled."
    }
    @ConfigItem(
        keyName = "pouch",
        name = "Pouch",
        description = "Select the pouches you want to use",
        position = 2,
        title = "pouchConfig")
    @JvmDefault
    fun pouch(): Pouches {
        return Pouches.COLOSSAL
    }

    @ConfigItem(
        keyName = "teleport",
        name = "Altar",
        description = "Select the altar teleport method",
        position = 3,
        title = "pouchConfig")
    @JvmDefault
    fun banking(): AltarTeleport {
        return AltarTeleport.RING_OF_THE_ELEMENTS
    }

    @ConfigItem(
        keyName = "teleport",
        name = "Bank",
        description = "Select the banking teleport method",
        position = 4,
        title = "pouchConfig")
    @JvmDefault
    fun altar(): BankTeleport {
        return BankTeleport.CRAFTING_CAPE
    }

    @ConfigItem(
        keyName = "stamina",
        name = "Energy",
        description = "Select if the method of run energy restoration you'd like to use",
        position = 5,
        title = "pouchConfig")
    @JvmDefault
    fun stamina(): RunEnergy {
        return RunEnergy.STAMINA_POTION
    }
}
