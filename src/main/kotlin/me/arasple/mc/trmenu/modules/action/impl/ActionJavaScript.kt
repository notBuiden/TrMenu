package me.arasple.mc.trmenu.modules.action.impl

import me.arasple.mc.trmenu.modules.action.base.Action
import me.arasple.mc.trmenu.modules.script.Scripts
import me.arasple.mc.trmenu.utils.Msger
import org.bukkit.entity.Player

/**
 * @author Arasple
 * @date 2020/3/28 19:28
 */
class ActionJavaScript : Action("(java)?(-)?script(s)?|js") {

    override fun onExecute(player: Player) {
        val js = getContent()
        val cache = js.endsWith("<cache>")
        Scripts.script(player, if (cache) js else Msger.replace(player, js), cache)
    }

}