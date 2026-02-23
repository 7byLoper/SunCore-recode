package ru.loper.suncore.api.economy;

import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EconomyServices {
    @Setter
    private volatile EconomyEditor vaultEconomy;
    @Setter
    private volatile EconomyEditor playerPointsEconomy;

    public EconomyEditor vaultEconomy() {
        return vaultEconomy;
    }

    public EconomyEditor playerPointsEconomy() {
        return playerPointsEconomy;
    }
}
