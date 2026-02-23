package ru.loper.suncore.api.economy;

import org.bukkit.OfflinePlayer;

public interface EconomyEditor {
    void setup();

    double getBalance(OfflinePlayer player);

    void withdrawBalance(OfflinePlayer player, double value);

    void investBalance(OfflinePlayer player, double value);

    boolean hasBalance(OfflinePlayer player, double value);

    boolean hasAndWithdrawBalance(OfflinePlayer player, double value);
}
