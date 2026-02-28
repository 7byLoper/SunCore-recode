package ru.loper.suncore.hologram;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import ru.loper.suncore.api.hook.holograms.HologramHook;

import java.util.List;

public class FancyHologramsHook implements HologramHook {
    private HologramManager hologramManager;

    @Override
    public void setup() {
        if (Bukkit.getPluginManager().getPlugin("FancyHolograms") == null) {
            Bukkit.getLogger().warning("FancyHolograms detect failed!");
            return;
        }

        hologramManager = FancyHologramsPlugin.get().getHologramManager();
        Bukkit.getLogger().info("FancyHolograms hook successfully initialized!");
    }

    @Override
    public void createHologram(String name, Location location, List<String> lines) {
        if (hologramManager == null) {
            Bukkit.getLogger().warning("Cannot create hologram: HologramManager is null!");
            return;
        }

        var optionalHologram = hologramManager.getHologram(name);

        if (optionalHologram.isEmpty()) {
            TextHologramData hologramData = new TextHologramData(name, location.clone());
            hologramData.setText(lines);

            Hologram hologram = hologramManager.create(hologramData);
            hologramManager.addHologram(hologram);
            hologram.createHologram();

            return;
        }

        updateHologram(name, location, lines);
    }

    @Override
    public void updateHologram(String name, Location location, List<String> lines) {
        if (hologramManager == null) {
            Bukkit.getLogger().warning("Cannot update hologram: HologramManager is null!");
            return;
        }

        var optionalHologram = hologramManager.getHologram(name);

        if (optionalHologram.isEmpty()) {
            return;
        }

        Hologram hologram = optionalHologram.get();
        TextHologramData data = (TextHologramData) hologram.getData();

        if (lines != null && !lines.isEmpty()) {
            data.setText(lines);
        }

        if (location != null && !location.equals(hologram.getData().getLocation())) {
            data.setLocation(location.clone());
        }

        hologram.refreshForViewers();
    }

    @Override
    public void removeHologram(String name) {
        if (hologramManager == null) {
            Bukkit.getLogger().warning("Cannot remove hologram: HologramManager is null!");
            return;
        }

        var optionalHologram = hologramManager.getHologram(name);

        if (optionalHologram.isEmpty()) {
            return;
        }

        Hologram hologram = optionalHologram.get();
        hologram.deleteHologram();
        hologramManager.removeHologram(hologram);
    }
}