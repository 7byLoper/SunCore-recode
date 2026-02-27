package ru.loper.suncore.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.loper.suncore.config.CoreConfigManager;

@RequiredArgsConstructor
public class ServerMessagesListener implements Listener {
    private final CoreConfigManager configManager;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var joinMessage = configManager.getJoinMessage();

        if (!joinMessage.enable()) {
            event.joinMessage(null);
            return;
        }

        if (joinMessage.message() == null) {
            return;
        }

        event.joinMessage(joinMessage.message());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        var quitMessage = configManager.getQuitMessage();

        if (!quitMessage.enable()) {
            event.quitMessage(null);
            return;
        }

        if (quitMessage.message() == null) {
            return;
        }

        event.quitMessage(quitMessage.message());

    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        var deathMessage = configManager.getDeathMessage();

        if (!deathMessage.enable()) {
            event.deathMessage(null);
            return;
        }

        if (deathMessage.message() == null) {
            return;
        }

        event.deathMessage(deathMessage.message());

    }

    @EventHandler
    public void onDeath(PlayerAdvancementDoneEvent event) {
        var advancementMessage = configManager.getAdvancementMessage();

        if (!advancementMessage.enable()) {
            event.message(null);
            return;
        }

        if (advancementMessage.message() == null) {
            return;
        }

        event.message(advancementMessage.message());
    }

}
