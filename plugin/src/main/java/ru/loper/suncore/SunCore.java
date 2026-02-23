package ru.loper.suncore;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.loper.suncore.api.CoreService;
import ru.loper.suncore.api.command.CommandServices;
import ru.loper.suncore.api.database.DatabaseServices;
import ru.loper.suncore.api.economy.EconomyServices;
import ru.loper.suncore.api.itemstack.ItemStackServices;
import ru.loper.suncore.api.redis.RedisManager;
import ru.loper.suncore.api.scheduler.CoreScheduler;
import ru.loper.suncore.api.scheduler.SchedulerServices;
import ru.loper.suncore.api.utilities.VersionHelper;
import ru.loper.suncore.commands.core.CoreCommand;
import ru.loper.suncore.config.CoreConfigManager;
import ru.loper.suncore.economy.PlayerPointsEconomy;
import ru.loper.suncore.economy.VaultEconomy;
import ru.loper.suncore.listeners.ItemsListener;
import ru.loper.suncore.listeners.MenuListener;
import ru.loper.suncore.registrar.BukkitCommandRegistrar;
import ru.loper.suncore.scheduler.bukkit.BukkitScheduler;
import ru.loper.suncore.scheduler.folia.FoliaScheduler;

@Getter
public final class SunCore extends JavaPlugin {
    private CoreConfigManager configManager;

    @Override
    public void onEnable() {
        printWelcome();

        CoreService.setCoreInstance(this);
        CoreScheduler coreScheduler = VersionHelper.isFolia() ?
                new FoliaScheduler() :
                new BukkitScheduler();

        SchedulerServices.setCoreScheduler(coreScheduler);
        CommandServices.setRegistrar(new BukkitCommandRegistrar());

        ItemStackServices.setSkullHead(createSkullHead());
        ItemStackServices.setPlacedKey(new NamespacedKey(this, "place"));

        configManager = new CoreConfigManager(this);
        DatabaseServices.setReplaceAllDatabases(configManager.isReplaceAllDatabases());
        DatabaseServices.setDefaultDatabase(configManager.getDefaultDatabase());

        EconomyServices.setVaultEconomy(new VaultEconomy());
        EconomyServices.setPlayerPointsEconomy(new PlayerPointsEconomy());

        registerListeners(
                new ItemsListener(),
                new MenuListener()
        );

        new CoreCommand(this)
                .registerWrappers();
    }

    @Override
    public void onDisable() {
        SchedulerServices.clientScheduler()
                .shutdown();

        RedisManager.shutdownAll();
    }

    private void printWelcome(){
        Bukkit.getConsoleSender().sendMessage("§e ____               ____");
        Bukkit.getConsoleSender().sendMessage("§e/ ___| _   _ _ __  / ___|___  _ __ ___");
        Bukkit.getConsoleSender().sendMessage("§e\\___ \\| | | | '_ \\| |   / _ \\| '__/ _ \\");
        Bukkit.getConsoleSender().sendMessage("§e ___) | |_| | | | | |__| (_) | | |  __/");
        Bukkit.getConsoleSender().sendMessage("§e|____/ \\__,_|_| |_|\\____\\___/|_|  \\___|");
        Bukkit.getConsoleSender().sendMessage("§fПлагин сделан при поддержке §eSunDev");
        Bukkit.getConsoleSender().sendMessage("§fНовостной канал студии: §ahttps://t.me/bySunDev");
        Bukkit.getConsoleSender().sendMessage("§fВерсия плагина: §a" + getDescription().getVersion());
    }

    private ItemStack createSkullHead() {
        if (VersionHelper.IS_ITEM_LEGACY) {
            return new ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
        }

        return new ItemStack(Material.PLAYER_HEAD, 1);
    }

    private void registerListeners(Listener... listeners) {
        PluginManager manager = Bukkit.getPluginManager();
        for (Listener listener : listeners) {
            manager.registerEvents(listener, this);
        }
    }

}
