package ru.loper.suncore.api.hook.antirelog;

import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.leymooo.antirelog.Antirelog;
import ru.leymooo.antirelog.config.Settings;
import ru.leymooo.antirelog.manager.BossbarManager;
import ru.leymooo.antirelog.manager.PowerUpsManager;
import ru.leymooo.antirelog.manager.PvPManager;

import java.lang.reflect.Method;

@UtilityClass
@SuppressWarnings("ALL")
public class AntiRelogHook {
    private boolean hook = false;

    private Antirelog antirelog;
    private PvPManager manager;
    private Settings settings;

    public void hook(JavaPlugin plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("AntiRelog") == null) {
            plugin.getLogger().warning("AntiRelog не установлен, некоторые функции могут не работать!");
            return;
        }

        antirelog = plugin.getPlugin(Antirelog.class);
        manager = antirelog.getPvpManager();
        settings = antirelog.getSettings();

        hook = true;
    }

    public void startPvp(Player attacker, Player attacked) {
        if (!hook) {
            return;
        }

        manager.playerDamagedByPlayer(attacker, attacked);
    }

    public void startPvp(Player player) {
        if (!hook) {
            return;
        }

        try {
            Class<? extends PvPManager> clazz = manager.getClass();
            Method method = clazz.getDeclaredMethod("startPvp", Player.class, boolean.class, boolean.class);
            method.setAccessible(true);
            method.invoke(manager, player, false, manager.isHasBypassPermission(player));
        } catch (Exception ignored) {
        }
    }

    public void safeStartPvp(Player player) {
        if (!hook) {
            return;
        }

        if (isPvp(player)) {
            updatePvpTime(player, getPvpTime());
            return;
        }

        startPvp(player);
    }

    public boolean isPvp(Player player) {
        if (!hook) {
            return false;
        }

        return manager.isInPvP(player);
    }

    public boolean isSilentPvp(Player player) {
        if (!hook) {
            return false;
        }

        return manager.isInSilentPvP(player);
    }

    public void stopPvp(Player player) {
        if (!hook) {
            return;
        }

        manager.stopPvP(player);
    }

    public void stopPvpSilent(Player player) {
        if (!hook) {
            return;
        }

        manager.stopPvPSilent(player);
    }

    public int getTimeRemainingInPvP(Player player) {
        if (!hook) {
            return 0;
        }

        return manager.getTimeRemainingInPvP(player);
    }

    public int getTimeRemainingInPvPSilent(Player player) {
        if (!hook) {
            return 0;
        }

        return manager.getTimeRemainingInPvPSilent(player);
    }

    public boolean isPvPModeEnabled() {
        if (!hook) {
            return false;
        }

        return manager.isPvPModeEnabled();
    }

    public boolean isBypassed(Player player) {
        if (!hook) {
            return false;
        }
        return manager.isBypassed(player);
    }

    public boolean hasBypassPermission(Player player) {
        if (!hook) {
            return false;
        }

        return manager.isHasBypassPermission(player);
    }

    public PowerUpsManager getPowerUpsManager() {
        if (!hook) {
            return null;
        }

        return manager.getPowerUpsManager();
    }

    public void disablePowerUps(Player player) {
        if (!hook) {
            return;
        }

        manager.getPowerUpsManager().disablePowerUps(player);
    }

    public void disablePowerUpsWithRunCommands(Player player) {
        if (!hook) {
            return;
        }

        manager.getPowerUpsManager().disablePowerUpsWithRunCommands(player);
    }

    public BossbarManager getBossbarManager() {
        if (!hook) {
            return null;
        }

        return manager.getBossbarManager();
    }

    public void setBossBar(Player player, int time) {
        if (!hook) {
            return;
        }

        manager.getBossbarManager().setBossBar(player, time);
    }

    public void clearBossbar(Player player) {
        if (!hook) {
            return;
        }

        manager.getBossbarManager().clearBossbar(player);
    }

    public boolean isCommandWhiteListed(String command) {
        if (!hook) {
            return false;
        }

        return manager.isCommandWhiteListed(command);
    }

    public int getPvpTime() {
        if (!hook) {
            return 0;
        }

        return settings.getPvpTime();
    }

    public boolean isDisableCommandsInPvp() {
        if (!hook) {
            return false;
        }

        return settings.isDisableCommandsInPvp();
    }

    public boolean isDisablePvpInIgnoredRegion() {
        if (!hook) {
            return false;
        }

        return settings.isDisablePvpInIgnoredRegion();
    }

    public void updatePvpTime(Player player, int newTime) {
        if (!hook) {
            return;
        }

        boolean isSilent = isSilentPvp(player);
        boolean bypassed = hasBypassPermission(player);

        try {
            Class<? extends PvPManager> clazz = manager.getClass();
            Method method = clazz.getDeclaredMethod("updatePvpMode", Player.class, boolean.class, int.class);
            method.setAccessible(true);
            method.invoke(manager, player, bypassed || isSilent, newTime);
        } catch (Exception ignored) {
        }
    }

    public void clearAllPvp() {
        if (!hook) {
            return;
        }

        try {
            Class<? extends PvPManager> clazz = manager.getClass();
            Method onPluginDisable = clazz.getDeclaredMethod("onPluginDisable");
            onPluginDisable.setAccessible(true);
            onPluginDisable.invoke(manager);
        } catch (Exception ignored) {
        }
    }

    public boolean isHooked() {
        return hook;
    }

    public Antirelog getAntiRelogInstance() {
        return antirelog;
    }
}