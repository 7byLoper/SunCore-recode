package ru.loper.suncore.api.hook.holograms;

import org.bukkit.Location;

import java.util.List;

public interface HologramHook {
    void setup();

    void createHologram(String name, Location location, List<String> lines);

    void updateHologram(String name, Location location, List<String> lines);

    void removeHologram(String name);
}
