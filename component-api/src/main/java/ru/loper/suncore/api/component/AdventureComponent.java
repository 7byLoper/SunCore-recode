package ru.loper.suncore.api.component;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class AdventureComponent {

    private final Pattern HEX_PATTERN = Pattern.compile("([&§]#[A-Fa-f0-9]{6})|([&§]x([&§][A-Fa-f0-9]){6})");
    private final Pattern PLACEHOLDER_KEY_PATTERN = Pattern.compile("[a-z0-9_-]+");
    private final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private BukkitAudiences adventure;

    public void init(Plugin plugin) {
        init(BukkitAudiences.create(plugin));
    }

    public void init(BukkitAudiences audiences) {
        adventure = audiences;
    }

    public void sendMessage(CommandSender sender, Component component) {
        if (adventure == null || component == null || sender == null) {
            return;
        }

        adventure.sender(sender).sendMessage(component);
    }

    public void sendMessage(CommandSender sender, String text) {
        if (adventure == null || sender == null || text == null || text.isEmpty()) {
            return;
        }

        adventure.sender(sender).sendMessage(toComponent(text));
    }

    public void sendMessage(CommandSender sender, String text, Map<String, ? extends ComponentLike> replacements) {
        if (adventure == null || sender == null || text == null || text.isEmpty()) {
            return;
        }

        adventure.sender(sender).sendMessage(toComponent(text, replacements));
    }

    public Component toComponent(String text) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }

        return MINI_MESSAGE.deserialize(prepareText(text)).compact();
    }

    public Component toComponent(String text, String placeholder, ComponentLike replacement) {
        if (replacement == null) {
            return toComponent(text);
        }

        return toComponent(text, Map.of(placeholder, replacement));
    }

    public Component toComponent(String text, Map<String, ? extends ComponentLike> replacements) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }

        Map<String, ComponentLike> validReplacements = sanitizeReplacements(replacements);
        if (validReplacements.isEmpty()) {
            return toComponent(text);
        }

        String preparedText = prepareText(text);
        TagResolver.Builder resolverBuilder = TagResolver.builder();

        for (var entry : validReplacements.entrySet()) {
            String key = entry.getKey();
            preparedText = preparedText.replace("{" + key + "}", "<" + key + ">");
            resolverBuilder.resolver(Placeholder.component(key, entry.getValue()));
        }

        return MINI_MESSAGE.deserialize(preparedText, resolverBuilder.build()).compact();
    }

    public Component replace(String text, String placeholder, ComponentLike replacement) {
        return toComponent(text, placeholder, replacement);
    }

    public Component replace(String text, Map<String, ? extends ComponentLike> replacements) {
        return toComponent(text, replacements);
    }

    public String toMiniMessage(Component component) {
        if (component == null) {
            return "";
        }

        return MINI_MESSAGE.serialize(component);
    }

    public String toString(Component component) {
        if (component == null) {
            return "";
        }

        return LegacyComponentSerializer.legacySection().serialize(component);
    }

    private String prepareText(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuilder builder = new StringBuilder(text.length());

        while (matcher.find()) {
            matcher.appendReplacement(builder, "<#" + matcher.group(0).replaceAll("[&§#x]", "") + ">");
        }

        return translateAlternateColorCodes('&', matcher.appendTail(builder).toString())
                .replace("§0", "<!b><!i><!u><!st><!obf><black>")
                .replace("§1", "<!b><!i><!u><!st><!obf><dark_blue>")
                .replace("§2", "<!b><!i><!u><!st><!obf><dark_green>")
                .replace("§3", "<!b><!i><!u><!st><!obf><dark_aqua>")
                .replace("§4", "<!b><!i><!u><!st><!obf><dark_red>")
                .replace("§5", "<!b><!i><!u><!st><!obf><dark_purple>")
                .replace("§6", "<!b><!i><!u><!st><!obf><gold>")
                .replace("§7", "<!b><!i><!u><!st><!obf><gray>")
                .replace("§8", "<!b><!i><!u><!st><!obf><dark_gray>")
                .replace("§9", "<!b><!i><!u><!st><!obf><blue>")
                .replace("§a", "<!b><!i><!u><!st><!obf><green>")
                .replace("§b", "<!b><!i><!u><!st><!obf><aqua>")
                .replace("§c", "<!b><!i><!u><!st><!obf><red>")
                .replace("§d", "<!b><!i><!u><!st><!obf><light_purple>")
                .replace("§e", "<!b><!i><!u><!st><!obf><yellow>")
                .replace("§f", "<!b><!i><!u><!st><!obf><white>")
                .replace("§r", "<reset>")
                .replace("§k", "<obfuscated>")
                .replace("§l", "<bold>")
                .replace("§m", "<strikethrough>")
                .replace("§n", "<underlined>")
                .replace("§o", "<italic>");
    }

    private Map<String, ComponentLike> sanitizeReplacements(Map<String, ? extends ComponentLike> replacements) {
        Map<String, ComponentLike> validReplacements = new LinkedHashMap<>();

        if (replacements == null || replacements.isEmpty()) {
            return validReplacements;
        }

        for (Map.Entry<String, ? extends ComponentLike> entry : replacements.entrySet()) {
            String key = normalizePlaceholder(entry.getKey());
            ComponentLike component = entry.getValue();

            if (key != null && component != null) {
                validReplacements.put(key, component);
            }
        }

        return validReplacements;
    }

    private String normalizePlaceholder(String placeholder) {
        if (placeholder == null) {
            return null;
        }

        String key = placeholder.trim()
                .replaceFirst("^\\{", "")
                .replaceFirst("}$", "")
                .toLowerCase(Locale.ROOT);

        if (!PLACEHOLDER_KEY_PATTERN.matcher(key).matches()) {
            throw new IllegalArgumentException("Invalid placeholder key: " + placeholder);
        }

        return key;
    }

    @Contract("_, _ -> new")
    private @NotNull String translateAlternateColorCodes(char altColorChar, @NotNull String textToTranslate) {
        char[] characters = textToTranslate.toCharArray();

        for (int i = 0; i < characters.length - 1; i++) {
            if (characters[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(characters[i + 1]) > -1) {
                characters[i] = 167;
                characters[i + 1] = Character.toLowerCase(characters[i + 1]);
            }
        }

        return new String(characters);
    }

    private Audience adventure(Player player) {
        return adventure.player(player);
    }

    private Audience adventure(CommandSender sender) {
        return adventure.sender(sender);
    }
}
