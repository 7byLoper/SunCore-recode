package ru.loper.suncore.api.menu.impl;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import ru.loper.suncore.api.menu.MenuItem;
import ru.loper.suncore.api.menu.MenuSettings;
import ru.loper.suncore.api.menu.button.Button;

import java.util.Objects;

@RequiredArgsConstructor
public abstract class ActionMenu extends AsyncMenu {
    protected final MenuSettings menuSettings;

    @Override
    public @Nullable String getTitle() {
        return menuSettings.getTitle();
    }

    @Override
    public int getSize() {
        return menuSettings.getSize();
    }

    @Override
    public void getItemsAndButtons() {
        menuSettings.getDecoration()
                .forEach(this::addDecorItems);

        onLoad();

        menuSettings.getItems().stream()
                .map(this::createButton)
                .filter(Objects::nonNull)
                .forEach(menuButtons::add);
    }

    protected abstract Button createButton(MenuItem menuItem);

    protected abstract void onLoad();
}
