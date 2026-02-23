package ru.loper.suncore.config;

import lombok.Getter;
import ru.loper.suncore.api.config.CustomConfig;

@Getter
public class MessageConfig {
    private final String noPermission;
    private final String reloadSuccess;
    private final String saveUsage;
    private final String saveAir;
    private final String saveSuccess;
    private final String giveUsage;
    private final String giveItemNotFound;
    private final String giveInvalidAmount;
    private final String givePlayerOnly;
    private final String givePlayerNotFound;
    private final String giveSuccess;

    public MessageConfig(CustomConfig config) {
        this.noPermission = config.configMessage("no_permission", "&cУ вас нет прав");
        this.reloadSuccess = config.configMessage("reload.success", "&#05A3FE▶ &fКонфигурация успешно перезагрузилась за {ms} мс");
        this.saveUsage = config.configMessage("save.usage", "&#FF0000▶ &fИспользование: /suncore save [название]");
        this.saveAir = config.configMessage("save.air", "&#FF0000▶ &fВы не можете сохранить воздух");
        this.saveSuccess = config.configMessage("save.success", "&#05A3FE▶ &fПредмет &e{name} &fуспешно сохранён");
        this.giveUsage = config.configMessage("give.usage", "&#FF0000▶ &fИспользование: /suncore give [custom item] [player] [amount]");
        this.giveItemNotFound = config.configMessage("give.item_not_found", "&#FF0000▶ &fДанного предмета не существует");
        this.giveInvalidAmount = config.configMessage("give.invalid_amount", "&#FF0000▶ &fНекорректное количество предметов");
        this.givePlayerOnly = config.configMessage("give.player_only", "&cДанная команда доступна только игрокам");
        this.givePlayerNotFound = config.configMessage("give.player_not_found", "&#FF0000▶ &fУказанный игрок не найден или не в сети");
        this.giveSuccess = config.configMessage("give.success", "&#05A3FE▶ &fВыдан предмет &e%s &fигроку &e%s &fв количестве &e%d");
    }
}