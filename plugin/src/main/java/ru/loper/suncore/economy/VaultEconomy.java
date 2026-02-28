package ru.loper.suncore.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import ru.loper.suncore.api.economy.EconomyEditor;

public class VaultEconomy implements EconomyEditor {
    private Economy economy;

    @Override
    public void setup() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            Bukkit.getLogger().warning("Vault economy detect failed!");
            return;
        }

        RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (provider == null) {
            Bukkit.getLogger().warning("Vault economy detect failed!");
            return;
        }

        this.economy = provider.getProvider();
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return economy != null ? economy.getBalance(player) : 0;
    }

    @Override
    public void withdrawBalance(OfflinePlayer player, double value) {
        if (economy == null) {
            return;
        }

        economy.withdrawPlayer(player, value);
    }

    @Override
    public void investBalance(OfflinePlayer player, double value) {
        if (economy == null) {
            return;
        }

        economy.depositPlayer(player, value);
    }

    @Override
    public boolean hasBalance(OfflinePlayer player, double value) {
        return getBalance(player) >= value;
    }

    @Override
    public boolean hasAndWithdrawBalance(OfflinePlayer player, double value) {
        boolean hasBalance = hasBalance(player, value);
        if (hasBalance) {
            withdrawBalance(player, value);
        }

        return hasBalance;
    }
}
