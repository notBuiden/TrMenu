package me.arasple.mc.trmenu.commands.sub

import io.izzel.taboolib.internal.apache.lang3.ArrayUtils
import io.izzel.taboolib.loader.PluginHandle
import io.izzel.taboolib.module.command.base.Argument
import io.izzel.taboolib.module.command.base.BaseSubCommand
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.ArrayUtil
import me.arasple.mc.trmenu.TrMenu
import me.arasple.mc.trmenu.api.TrMenuAPI
import me.arasple.mc.trmenu.data.MetaPlayer.getArguments
import me.arasple.mc.trmenu.data.MetaPlayer.getMeta
import me.arasple.mc.trmenu.data.Sessions.getMenuSession
import me.arasple.mc.trmenu.display.Menu
import me.arasple.mc.trmenu.modules.metrics.MetricsHandler
import me.arasple.mc.trmenu.modules.expression.Expressions
import me.arasple.mc.trmenu.utils.Msger
import me.arasple.mc.trmenu.utils.Skulls
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scheduler.BukkitWorker

/**
 * @author Arasple
 * @date 2020/3/7 12:36
 */
class CommandDebug : BaseSubCommand() {

    override fun getArguments() = arrayOf(
        Argument("Type", false) {
            listOf(
                "info",
                "player",
                "menu",
                "msgreplace",
                "skulls",
                "parseExpression"
            )
        }
    )

    val description: YamlConfiguration = PluginHandle.getPluginDescription()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>) {
        if (args.isEmpty()) {
            debugSender(sender)
        } else {
            val content = ArrayUtils.remove(args, 0).joinToString("")
            when (args[0].toLowerCase()) {
                "info" -> printInfo(sender)
                "skulls" -> printSkulls(sender)
                "player" -> if (args.size > 1) printPlayer(sender, Bukkit.getPlayerExact(args[1]))
                "menu" -> if (args.size > 1) printMenu(sender, TrMenuAPI.getMenuById(args[1]))
                "msgreplace" -> {
                    val player = sender as Player
                    val message = ArrayUtil.arrayJoin(args, 1)
                    sender.sendMessage(
                        Msger.replace(player, message)
                    )
                }
                "parseexpression" -> TLocale.sendTo(sender, "DEBUG.EXPRESSION", content, Expressions.parseExpression(content))
            }
        }
    }

    private fun printSkulls(sender: CommandSender) {
        sender.sendMessage(
            arrayOf(
                "§3§l「§8--------------------------------------------------§3§l」",
                "",
                "§cCached_Skulls: §4${Skulls.cache.keys}",
                "§cCached_PlayerTextures: §4${Skulls.cachePlayerTexture}",
                "",
                "§3§l「§8--------------------------------------------------§3§l」"
            )
        )
    }

    private fun debugSender(sender: CommandSender) {
        if (sender is Player) {
            if (sender.hasMetadata("TrMenu:Debug")) {
                sender.removeMetadata("TrMenu:Debug", TrMenu.plugin)
                TLocale.sendTo(sender, "COMMANDS.DEBUG.OFF")
            } else {
                sender.setMetadata("TrMenu:Debug", FixedMetadataValue(TrMenu.plugin, ""))
                TLocale.sendTo(sender, "COMMANDS.DEBUG.ON")
            }
        } else TLocale.sendTo(sender, "COMMANDS.DEBUG.${if (Msger.debug()) "ON" else "OFF"}")
    }

    private fun printPlayer(sender: CommandSender, player: Player?) {
        if (player != null) {
            val session = player.getMenuSession()
            sender.sendMessage(
                arrayOf(
                    "§3§l「§8--------------------------------------------------§3§l」",
                    "",
                    "§eCurrentPage: §6${session.page}",
                    "§eArguments: §6${player.getArguments().joinToString(", ", "{", "}")}",
                    "§eMetas: §6${player.getMeta()}",
                    "§eInternalFunctions: §6${session.menu?.settings?.functions?.internalFunctions?.map { it.id }}",
                    "",
                    "§3§l「§8--------------------------------------------------§3§l」"
                )
            )
        }
    }


    private fun printMenu(sender: CommandSender, menu: Menu?) {
        if (menu != null) {
            sender.sendMessage(
                arrayOf(
                    "§3§l「§8--------------------------------------------------§3§l」",
                    "",
                    "§aTitle: §6${menu.settings.title.titles} / ${menu.settings.title.update}",
                    "§aBindings: §6${menu.settings.bindings.boundCommands.joinToString(",")}",
                    "§aIcons: §6${menu.icons.size}",
                    "§aOpenEvent: §6${menu.settings.events.openEvent}",
                    "§aCloseEvent: §6${menu.settings.events.closeEvent}",
                    "",
                    "§3§l「§8--------------------------------------------------§3§l」"
                )
            )
        }
    }

    private fun printInfo(sender: CommandSender) {
        sender.sendMessage(
            arrayOf(
                "§3§l「§8--------------------------------------------------§3§l」",
                "",
                "§2Total Menus: §6"
            )
        )
        Menu.getAllMenus().forEach { if (it.value.isNotEmpty()) sender.sendMessage("§r  §8▪ ${it.key}§8: §7${it.value.size}") }
        sender.sendMessage(
            arrayOf(
                "§2Cached Skulls: §6${Skulls.cache.size}",
                "§2Cached Expressions: §6${Expressions.cachedParsed.size}",
                "§2Running Tasks: §6${Bukkit.getScheduler().activeWorkers.stream().filter { t: BukkitWorker -> t.owner === TrMenu.plugin }.count() + Bukkit.getScheduler().pendingTasks.stream().filter { t: BukkitTask -> t.owner === TrMenu.plugin }.count()}",
                "§2bStats: §3${MetricsHandler.B_STATS?.isEnabled}", "§2cStats: §3${MetricsHandler.C_STATS?.isEnabled}", "§2TabooLib: §f${PluginHandle.getVersion()}", "",
                "§3TrMenu Built-Info: §b${description.getString("built-time")}§7, §3By §a${description.getString("built-by")}",
                "",
                "§3§l「§8--------------------------------------------------§3§l」"
            )
        )
    }

}