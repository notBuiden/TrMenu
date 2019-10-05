package me.arasple.mc.trmenu.utils;

import io.izzel.taboolib.module.locale.TLocale;
import io.izzel.taboolib.util.Strings;
import io.izzel.taboolib.util.lite.Scripts;
import me.arasple.mc.trmenu.TrMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;

import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Arasple
 * @date 2019/10/5 13:57
 */
public class JavaScript {

    public static Object run(Player player, String script, InventoryEvent event, String... args) {
        Map<String, Object> bind = new HashMap<>();
        bind.put("player", player);
        bind.put("bukkitServer", Bukkit.getServer());
        if (event != null) {
            if (event instanceof InventoryClickEvent) {
                InventoryClickEvent clickEvent = (InventoryClickEvent) event;

                bind.put("clickEvent", clickEvent);
                bind.put("clickType", clickEvent.getClick());

                ItemStack itemStack = clickEvent.getClickedInventory().getItem(clickEvent.getRawSlot());
                if (itemStack != null) {
                    bind.put("clickItemStack", itemStack);
                }
            }
        }

        script = TLocale.Translate.setPlaceholders(player, Strings.replaceWithOrder(script, args));

        System.out.println(script);

        try {
            return Scripts.compile(script).eval(new SimpleBindings(bind));
        } catch (ScriptException e) {
            TrMenu.getTLogger().error("&cJs 运算时发生异常: &6{Script" + script + "}&8; &6Error: &4" + e.getMessage());
            TrMenu.getTLogger().error("&8" + Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

}