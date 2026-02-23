package ru.loper.suncore.economy;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import ru.loper.suncore.api.economy.EconomyEditor;

public class PlayerPointsEconomy implements EconomyEditor {
    private PlayerPointsAPI economy;

    @Override
    public void setup() {
        if (Bukkit.getPluginManager().getPlugin("PlayerPoints") == null) {
            return;
        }

        economy = PlayerPoints.getInstance().getAPI();
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return economy != null ? economy.look(player.getUniqueId()) : 0;
    }

    @Override
    public void withdrawBalance(OfflinePlayer player, double value) {
        if (economy == null) {
            return;
        }

        economy.take(player.getUniqueId(), (int) value);
    }

    @Override
    public void investBalance(OfflinePlayer player, double value) {
        if (economy == null) {
            return;
        }

        economy.give(player.getUniqueId(), (int) value);
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
