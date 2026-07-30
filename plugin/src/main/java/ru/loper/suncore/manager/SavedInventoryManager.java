package ru.loper.suncore.manager;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class SavedInventoryManager {
    public static final int INVENTORY_SIZE = 9 * 4;

    private static final Pattern INVENTORY_NAME_PATTERN = Pattern.compile("[a-z0-9_-]{1,32}");

    private final Plugin plugin;
    private final File inventoriesDirectory;

    public SavedInventoryManager(Plugin plugin) {
        this.plugin = plugin;
        this.inventoriesDirectory = new File(plugin.getDataFolder(), "inventories");
        createInventoriesDirectory();
    }

    public String normalizeName(String name) {
        return name == null ? "" : name.toLowerCase(Locale.ROOT);
    }

    public boolean isValidName(String name) {
        return name != null && INVENTORY_NAME_PATTERN.matcher(normalizeName(name)).matches();
    }

    public synchronized boolean exists(String name) {
        return isValidName(name) && getInventoryFile(normalizeName(name)).isFile();
    }

    public synchronized boolean create(String name) {
        String normalizedName = normalizeName(name);

        if (!isValidName(normalizedName) || exists(normalizedName)) {
            return false;
        }

        return save(normalizedName, new ItemStack[INVENTORY_SIZE]);
    }

    public synchronized Optional<ItemStack[]> load(String name) {
        String normalizedName = normalizeName(name);

        if (!isValidName(normalizedName)) {
            return Optional.empty();
        }

        File file = getInventoryFile(normalizedName);

        if (!file.isFile()) {
            return Optional.empty();
        }

        try {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection itemsSection = config.getConfigurationSection("items");
            ItemStack[] contents = new ItemStack[INVENTORY_SIZE];

            if (itemsSection == null) {
                return Optional.of(contents);
            }

            for (String key : itemsSection.getKeys(false)) {
                int slot;

                try {
                    slot = Integer.parseInt(key);
                } catch (NumberFormatException exception) {
                    continue;
                }

                if (slot < 0 || slot >= INVENTORY_SIZE) {
                    continue;
                }

                ItemStack itemStack = itemsSection.getItemStack(key);

                if (itemStack != null && !itemStack.getType().isAir()) {
                    contents[slot] = itemStack.clone();
                }
            }

            return Optional.of(contents);
        } catch (RuntimeException exception) {
            plugin.getLogger().log(
                    Level.SEVERE,
                    "Failed to load saved inventory " + normalizedName,
                    exception
            );
            return Optional.empty();
        }
    }

    public synchronized boolean save(String name, ItemStack[] contents) {
        String normalizedName = normalizeName(name);

        if (!isValidName(normalizedName)) {
            return false;
        }

        if (!createInventoriesDirectory()) {
            return false;
        }

        File targetFile = getInventoryFile(normalizedName);
        File temporaryFile = new File(inventoriesDirectory, normalizedName + ".yml.tmp");
        YamlConfiguration config = new YamlConfiguration();

        config.set("name", normalizedName);
        config.set("size", INVENTORY_SIZE);

        ItemStack[] safeContents = contents == null
                ? new ItemStack[INVENTORY_SIZE]
                : Arrays.copyOf(contents, INVENTORY_SIZE);

        for (int slot = 0; slot < safeContents.length; slot++) {
            ItemStack itemStack = safeContents[slot];

            if (itemStack == null || itemStack.getType().isAir()) {
                continue;
            }

            config.set("items." + slot, itemStack.clone());
        }

        try {
            config.save(temporaryFile);
            moveReplacing(temporaryFile, targetFile);
            return true;
        } catch (IOException | RuntimeException exception) {
            plugin.getLogger().log(
                    Level.SEVERE,
                    "Failed to save inventory " + normalizedName,
                    exception
            );
            return false;
        } finally {
            if (temporaryFile.exists() && !temporaryFile.equals(targetFile)) {
                try {
                    Files.deleteIfExists(temporaryFile.toPath());
                } catch (IOException ignored) {
                }
            }
        }
    }

    public synchronized boolean delete(String name) {
        String normalizedName = normalizeName(name);

        if (!isValidName(normalizedName)) {
            return false;
        }

        File inventoryFile = getInventoryFile(normalizedName);

        if (!inventoryFile.isFile()) {
            return false;
        }

        try {
            return Files.deleteIfExists(inventoryFile.toPath());
        } catch (IOException exception) {
            plugin.getLogger().log(
                    Level.SEVERE,
                    "Failed to delete inventory " + normalizedName,
                    exception
            );
            return false;
        }
    }

    public int give(Player target, ItemStack[] contents) {
        if (contents == null || contents.length == 0) {
            return 0;
        }

        ItemStack[] items = Arrays.stream(contents)
                .filter(itemStack -> itemStack != null && !itemStack.getType().isAir())
                .map(ItemStack::clone)
                .toArray(ItemStack[]::new);

        if (items.length == 0) {
            return 0;
        }

        Map<Integer, ItemStack> leftovers = target.getInventory().addItem(items);
        int droppedAmount = 0;

        for (ItemStack leftover : leftovers.values()) {
            droppedAmount += leftover.getAmount();
            target.getWorld().dropItemNaturally(target.getLocation(), leftover);
        }

        return droppedAmount;
    }

    public synchronized List<String> getInventoryNames() {
        if (!createInventoriesDirectory()) {
            return Collections.emptyList();
        }

        File[] files = inventoriesDirectory.listFiles(
                file -> file.isFile() && file.getName().endsWith(".yml")
        );

        if (files == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(files)
                .map(File::getName)
                .map(fileName -> fileName.substring(0, fileName.length() - 4))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    private File getInventoryFile(String normalizedName) {
        return new File(inventoriesDirectory, normalizedName + ".yml");
    }

    private boolean createInventoriesDirectory() {
        if (inventoriesDirectory.isDirectory()) {
            return true;
        }

        if (inventoriesDirectory.mkdirs()) {
            return true;
        }

        plugin.getLogger().severe(
                "Failed to create inventories directory: " + inventoriesDirectory.getAbsolutePath()
        );
        return false;
    }

    private void moveReplacing(File source, File target) throws IOException {
        try {
            Files.move(
                    source.toPath(),
                    target.toPath(),
                    StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(
                    source.toPath(),
                    target.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
    }
}
