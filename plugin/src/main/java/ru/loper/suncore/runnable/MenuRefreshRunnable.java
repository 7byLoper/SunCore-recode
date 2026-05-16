package ru.loper.suncore.runnable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.loper.suncore.api.menu.impl.AbstractMenu;
import ru.loper.suncore.api.scheduler.CoreRunnable;

public class MenuRefreshRunnable extends CoreRunnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!(player.getOpenInventory().getTopInventory().getHolder() instanceof AbstractMenu menu)) {
                return;
            }

            menu.refreshMenu();
        }
    }
}
