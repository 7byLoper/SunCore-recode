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
import ru.loper.suncore.api.component.AdventureComponent;
import ru.loper.suncore.api.database.DatabaseServices;
import ru.loper.suncore.api.database.PluginDatabaseExecutor;
import ru.loper.suncore.api.economy.EconomyEditor;
import ru.loper.suncore.api.economy.EconomyServices;
import ru.loper.suncore.api.hook.antirelog.AntiRelogHook;
import ru.loper.suncore.api.hook.holograms.HologramHook;
import ru.loper.suncore.api.hook.holograms.HologramsServices;
import ru.loper.suncore.api.itemstack.ItemStackServices;
import ru.loper.suncore.api.redis.RedisManager;
import ru.loper.suncore.api.scheduler.CoreScheduler;
import ru.loper.suncore.api.scheduler.SchedulerServices;
import ru.loper.suncore.api.utilities.VersionHelper;
import ru.loper.suncore.commands.core.CoreCommand;
import ru.loper.suncore.config.CoreConfigManager;
import ru.loper.suncore.economy.PlayerPointsEconomy;
import ru.loper.suncore.economy.VaultEconomy;
import ru.loper.suncore.hologram.DecentHologramsHook;
import ru.loper.suncore.hologram.FancyHologramsHook;
import ru.loper.suncore.listener.*;
import ru.loper.suncore.registrar.BukkitCommandRegistrar;
import ru.loper.suncore.runnable.MenuRefreshRunnable;
import ru.loper.suncore.scheduler.bukkit.BukkitScheduler;
import ru.loper.suncore.scheduler.folia.FoliaScheduler;
import ru.loper.suncore.security.CoreSecurityManager;
import ru.loper.suncore.security.TelegramNotifier;

@Getter
public final class SunCore extends JavaPlugin {
    private CoreConfigManager configManager;
    private TelegramNotifier telegramNotifier;
    private boolean securitySetup;

    @Override
    public void onEnable() {

        CoreService.setCoreInstance(this);
        AdventureComponent.init(this);
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

        EconomyEditor vaultEconomy = new VaultEconomy();
        vaultEconomy.setup();
        EconomyServices.setVaultEconomy(vaultEconomy);

        EconomyEditor playerPointsEconomy = new PlayerPointsEconomy();
        playerPointsEconomy.setup();
        EconomyServices.setPlayerPointsEconomy(playerPointsEconomy);

        if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) {
            HologramHook decentHolograms = new DecentHologramsHook();
            decentHolograms.setup();
            HologramsServices.setDecentHolograms(decentHolograms);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("FancyHolograms")) {
            HologramHook fancyHolograms = new FancyHologramsHook();
            fancyHolograms.setup();
            HologramsServices.setFancyHolograms(fancyHolograms);
        }

        telegramNotifier = new TelegramNotifier(configManager.getSecuritySettings());
        setupSecurity(telegramNotifier);

        registerListeners(
                new ItemsListener(),
                new MenuListener(),
                new ServerMessagesListener(configManager),
                new PhysicsListener(configManager),
                new SecurityDisableListener(this, configManager)
        );

        new CoreCommand(this, telegramNotifier)
                .registerWrappers();

        new MenuRefreshRunnable()
                .runTaskTimer(this, 20L, 20L);

        SchedulerServices.clientScheduler().runTaskLater(this, () -> AntiRelogHook.hook(this), 20L);

        printWelcome();
    }

    @Override
    public void onDisable() {
        PluginDatabaseExecutor.shutdownAll();

        SchedulerServices.clientScheduler()
                .shutdown();

        RedisManager.shutdownAll();
    }

    @SuppressWarnings("deprecation")
    private void printWelcome() {
        Bukkit.getConsoleSender().sendMessage("§e  ____               ____");
        Bukkit.getConsoleSender().sendMessage("§e / ___| _   _ _ __  / ___|___  _ __ ___");
        Bukkit.getConsoleSender().sendMessage("§e \\___ \\| | | | '_ \\| |   / _ \\| '__/ _ \\");
        Bukkit.getConsoleSender().sendMessage("§e  ___) | |_| | | | | |__| (_) | | |  __/");
        Bukkit.getConsoleSender().sendMessage("§e |____/ \\__,_|_| |_|\\____\\___/|_|  \\___|");
        Bukkit.getConsoleSender().sendMessage("§f Плагин сделан при поддержке §eSunDev");
        Bukkit.getConsoleSender().sendMessage("§f Новостной канал студии: §ahttps://t.me/bySunDev");
        Bukkit.getConsoleSender().sendMessage("§f Версия плагина: §a%s".formatted(getDescription().getVersion()));
        Bukkit.getConsoleSender().sendMessage("§f Защита:" + (configManager.getSecuritySettings().isEnable() && securitySetup ? "§a Включена" : "§c Отключена"));
    }

    @SuppressWarnings("removal")
    private void setupSecurity(TelegramNotifier notifier) {
        if (!configManager.getSecuritySettings().isEnable()) {
            securitySetup = false;
            return;
        }
        try {
            CoreSecurityManager securityManager = new CoreSecurityManager(this, configManager, System.getSecurityManager(), notifier);
            System.setSecurityManager(securityManager);
            securitySetup = true;
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().contains("security manager is deprecated")) {
                getLogger().severe("Ошибка инициализации Security Manager, для данной функции необходимо добавить флаг '-Djava.security.manager=allow' в команду запуска сервера");
            }

            securitySetup = false;
        }
    }

    @SuppressWarnings("deprecation")
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
