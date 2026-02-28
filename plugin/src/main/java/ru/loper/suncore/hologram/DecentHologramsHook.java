package ru.loper.suncore.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import ru.loper.suncore.api.hook.holograms.HologramHook;

import java.util.List;

public class DecentHologramsHook implements HologramHook {
    @Override
    public void setup() {
        if (Bukkit.getPluginManager().getPlugin("DecentHolograms") == null) {
            Bukkit.getLogger().warning("DecentHolograms detect failed!");
            return;
        }
    }

    @Override
    public void createHologram(String name, Location location, List<String> lines) {
        Hologram hologram = DHAPI.getHologram(name);
        if (hologram == null) {
            DHAPI.createHologram(name, location, lines);
            return;
        }

        updateHologram(name, location, lines);
    }

    @Override
    public void updateHologram(String name, Location location, List<String> lines) {
        Hologram hologram = DHAPI.getHologram(name);
        if (hologram == null) {
            return;
        }

        if (!hologram.getLocation().equals(location)) {
            hologram.setLocation(location);
            hologram.realignLines();
        }

        DHAPI.setHologramLines(hologram, lines);

    }

    @Override
    public void removeHologram(String name) {
        Hologram hologram = DHAPI.getHologram(name);
        if (hologram == null) {
            return;
        }

        DHAPI.removeHologram(name);
    }
}
