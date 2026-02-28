package ru.loper.suncore.api.hook.holograms;

import lombok.Setter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HologramsServices {
    @Setter
    private volatile HologramHook decentHolograms;
    @Setter
    private volatile HologramHook fancyHolograms;

    public HologramHook decentHolograms() {
        return decentHolograms;
    }

    public HologramHook fancyHolograms() {
        return fancyHolograms;
    }

    public HologramHook activeHook() {
        if (decentHolograms != null) {
            return decentHolograms;
        }

        return fancyHolograms;
    }
}
