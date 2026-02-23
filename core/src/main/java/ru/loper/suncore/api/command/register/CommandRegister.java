package ru.loper.suncore.api.command.register;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CommandRegister {
    String name();

    String permission();
}
