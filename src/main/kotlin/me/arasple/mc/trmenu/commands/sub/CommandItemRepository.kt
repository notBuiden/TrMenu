package me.arasple.mc.trmenu.commands.sub

import io.izzel.taboolib.cronus.CronusUtils
import io.izzel.taboolib.module.command.base.Argument
import io.izzel.taboolib.module.command.base.BaseSubCommand
import io.izzel.taboolib.module.command.base.CommandType
import io.izzel.taboolib.module.locale.TLocale
import io.izzel.taboolib.util.lite.Sounds
import me.arasple.mc.trmenu.api.factory.MenuFactory
import me.arasple.mc.trmenu.modules.repo.ItemRepository
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author Arasple
 * @date 2020/7/26 8:56
 */
class CommandItemRepository : BaseSubCommand() {

    override fun getArguments() = arrayOf(
        Argument("Type", true) {
            listOf("give", "add", "remove")
        },
        Argument("ID", true) {
            ItemRepository.getItemStacks().map { it.key }
        }
    )

    override fun getType(): CommandType = CommandType.PLAYER

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>) {
        val player = sender as Player

        Sounds.ITEM_BOTTLE_FILL.play(player, 1f, 0f)

        when (args[0].toLowerCase()) {
            "give" -> {
                val id = args[1]
                val item = ItemRepository.getItem(id).let {
                    if (it == null) {
                        TLocale.sendTo(sender, "COMMANDS.ITEM-REPOSITORY.NOT-EXIST", id)
                        return
                    }
                    return@let it
                }
                CronusUtils.addItem(player, item)
                TLocale.sendTo(sender, "COMMANDS.ITEM-REPOSITORY.GIVED", id)
            }
            "add" -> {
                val id = args[1]
                ItemRepository.getItem(id)?.let {
                    TLocale.sendTo(sender, "COMMANDS.ITEM-REPOSITORY.EXISTED", id)
                    return
                }
                val item = player.inventory.itemInMainHand
                ItemRepository.addItem(id, item)
                TLocale.sendTo(sender, "COMMANDS.ITEM-REPOSITORY.ADDED", id)
            }
            "remove" -> {
                val id = args[1]
                ItemRepository.getItem(id).let {
                    if (it == null) {
                        TLocale.sendTo(sender, "COMMANDS.ITEM-REPOSITORY.NOT-EXIST", id)
                        return
                    }
                }
                ItemRepository.removeItem(id)
                TLocale.sendTo(sender, "COMMANDS.ITEM-REPOSITORY.REMOVED", id)
            }
        }
    }

    private fun listItems(player: Player, page: Int) {
        // TODO
        MenuFactory()
            .title("Item Repository #$page")
            .layout(
                "########`Close`",
                "         ",
                "         ",
                "         ",
                "         ",
                "`Pre`#######`Next`",
            )
            .build {}
            .display(player)
    }

}