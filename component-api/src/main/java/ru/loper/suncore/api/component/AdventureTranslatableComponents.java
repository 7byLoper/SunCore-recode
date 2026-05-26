package ru.loper.suncore.api.component;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@UtilityClass
public class AdventureTranslatableComponents {

    public TranslatableComponent of(@NotNull String translationKey) {
        return Component.translatable(requireTranslationKey(translationKey));
    }

    public TranslatableComponent of(@NotNull String translationKey, ComponentLike... arguments) {
        return Component.translatable(requireTranslationKey(translationKey), sanitize(arguments));
    }

    public TranslatableComponent of(@NotNull String translationKey, List<? extends ComponentLike> arguments) {
        return Component.translatable(requireTranslationKey(translationKey), sanitize(arguments));
    }

    public TranslatableComponent colored(@NotNull String translationKey, TextColor color, ComponentLike... arguments) {
        return Component.translatable(requireTranslationKey(translationKey), color, sanitize(arguments));
    }

    public TranslatableComponent styled(@NotNull String translationKey, @NotNull Style style, ComponentLike... arguments) {
        return Component.translatable(
                requireTranslationKey(translationKey),
                Objects.requireNonNull(style, "style"),
                sanitize(arguments)
        );
    }

    public TranslatableComponent decorated(@NotNull String translationKey,
                                           TextColor color,
                                           Set<TextDecoration> decorations,
                                           ComponentLike... arguments) {
        return Component.translatable(
                requireTranslationKey(translationKey),
                color,
                decorations == null ? Collections.emptySet() : Set.copyOf(decorations),
                sanitize(arguments)
        );
    }

    public TranslatableComponent material(@NotNull Material material) {
        Objects.requireNonNull(material, "material");
        return material.isBlock() ? block(material) : item(material);
    }

    public TranslatableComponent item(@NotNull Material material) {
        return keyed("item", Objects.requireNonNull(material, "material").getKey());
    }

    public TranslatableComponent block(@NotNull Material material) {
        return keyed("block", Objects.requireNonNull(material, "material").getKey());
    }

    public TranslatableComponent entity(@NotNull EntityType entityType) {
        return keyed("entity", Objects.requireNonNull(entityType, "entityType").getKey());
    }

    public TranslatableComponent enchantment(@NotNull Enchantment enchantment) {
        return keyed("enchantment", Objects.requireNonNull(enchantment, "enchantment").getKey());
    }

    public TranslatableComponent keyed(@NotNull String category, @NotNull NamespacedKey key) {
        return of(key(category, key));
    }

    public TranslatableComponent keyed(@NotNull String category,
                                       @NotNull NamespacedKey key,
                                       ComponentLike... arguments) {
        return of(key(category, key), arguments);
    }

    public String key(@NotNull String category, @NotNull NamespacedKey key) {
        String normalizedCategory = Objects.requireNonNull(category, "category").trim();
        NamespacedKey checkedKey = Objects.requireNonNull(key, "key");

        if (normalizedCategory.isEmpty()) {
            throw new IllegalArgumentException("Translation category cannot be empty");
        }

        return normalizedCategory + "." + checkedKey.getNamespace() + "." + checkedKey.getKey();
    }

    public Component argument(String value) {
        return Component.text(value == null ? "" : value);
    }

    public Component nonItalic(ComponentLike componentLike) {
        Component component = componentLike == null ? Component.empty() : componentLike.asComponent();
        return component.decoration(TextDecoration.ITALIC, false);
    }

    private String requireTranslationKey(String translationKey) {
        String checkedKey = Objects.requireNonNull(translationKey, "translationKey").trim();

        if (checkedKey.isEmpty()) {
            throw new IllegalArgumentException("Translation key cannot be empty");
        }

        return checkedKey;
    }

    private ComponentLike[] sanitize(ComponentLike... arguments) {
        if (arguments == null || arguments.length == 0) {
            return new ComponentLike[0];
        }

        return Arrays.stream(arguments)
                .filter(Objects::nonNull)
                .toArray(ComponentLike[]::new);
    }

    private List<? extends ComponentLike> sanitize(List<? extends ComponentLike> arguments) {
        if (arguments == null || arguments.isEmpty()) {
            return List.of();
        }

        return arguments.stream()
                .filter(Objects::nonNull)
                .toList();
    }
}