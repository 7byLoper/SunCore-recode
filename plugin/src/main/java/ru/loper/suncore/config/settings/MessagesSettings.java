package ru.loper.suncore.config.settings;

import lombok.Getter;
import ru.loper.suncore.api.config.CustomConfig;

@Getter
public class MessagesSettings {
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

    private final String invSaveUsage;
    private final String invSaveOnlyPlayer;
    private final String invSaveUnknownAction;
    private final String invSaveInvalidName;
    private final String invSaveAlreadyExists;
    private final String invSaveNotFound;
    private final String invSaveCreateOpened;
    private final String invSaveEditOpened;
    private final String invSaveSaved;
    private final String invSaveTargetRequired;
    private final String invSavePlayerNotFound;
    private final String invSaveGiveSuccess;
    private final String invSaveDropped;
    private final String invSaveDeleteSuccess;
    private final String invSaveOperationError;
    private final String invSaveMenuTitle;

    private final String securityUsage;
    private final String securityAdd;
    private final String securityAlreadyAdded;

    public MessagesSettings(CustomConfig config) {
        this.noPermission = config.configMessage("no_permission", "&cУ вас нет прав");
        this.reloadSuccess = config.configMessage(
                "reload.success",
                "&#05A3FE▶ &fКонфигурация успешно перезагрузилась за {ms} мс"
        );
        this.saveUsage = config.configMessage(
                "save.usage",
                "&#FF0000▶ &fИспользование: /suncore save [название]"
        );
        this.saveAir = config.configMessage(
                "save.air",
                "&#FF0000▶ &fВы не можете сохранить воздух"
        );
        this.saveSuccess = config.configMessage(
                "save.success",
                "&#05A3FE▶ &fПредмет &e{name} &fуспешно сохранён"
        );
        this.giveUsage = config.configMessage(
                "give.usage",
                "&#FF0000▶ &fИспользование: /suncore give [custom item] [player] [amount]"
        );
        this.giveItemNotFound = config.configMessage(
                "give.item_not_found",
                "&#FF0000▶ &fДанного предмета не существует"
        );
        this.giveInvalidAmount = config.configMessage(
                "give.invalid_amount",
                "&#FF0000▶ &fНекорректное количество предметов"
        );
        this.givePlayerOnly = config.configMessage(
                "give.player_only",
                "&cДанная команда доступна только игрокам"
        );
        this.givePlayerNotFound = config.configMessage(
                "give.player_not_found",
                "&#FF0000▶ &fУказанный игрок не найден или не в сети"
        );
        this.giveSuccess = config.configMessage(
                "give.success",
                "&#05A3FE▶ &fВыдан предмет &e%s &fигроку &e%s &fв количестве &e%d"
        );

        this.invSaveUsage = config.configMessage(
                "inv_save.usage",
                "&#FF0000▶ &fИспользование: &7/suncore inv-save <create|edit|give> <название> [игрок]"
        );
        this.invSaveOnlyPlayer = config.configMessage(
                "inv_save.only_player",
                "&#FF0000▶ &fСоздавать и редактировать инвентари может только игрок"
        );
        this.invSaveUnknownAction = config.configMessage(
                "inv_save.unknown_action",
                "&#FF0000▶ &fНеизвестное действие. Используйте &7create&f, &7edit&f или &7give"
        );
        this.invSaveInvalidName = config.configMessage(
                "inv_save.invalid_name",
                "&#FF0000▶ &fНазвание должно содержать от 1 до 32 символов: &7a-z, 0-9, _, -"
        );
        this.invSaveAlreadyExists = config.configMessage(
                "inv_save.already_exists",
                "&#FF0000▶ &fИнвентарь &e{name} &fуже существует"
        );
        this.invSaveNotFound = config.configMessage(
                "inv_save.not_found",
                "&#FF0000▶ &fИнвентарь &e{name} &fне найден"
        );
        this.invSaveCreateOpened = config.configMessage(
                "inv_save.create_opened",
                "&#05A3FE▶ &fИнвентарь &e{name} &fсоздан, редактор открыт"
        );
        this.invSaveEditOpened = config.configMessage(
                "inv_save.edit_opened",
                "&#05A3FE▶ &fРедактор инвентаря &e{name} &fоткрыт"
        );
        this.invSaveSaved = config.configMessage(
                "inv_save.saved",
                "&#05A3FE▶ &fИнвентарь &e{name} &fуспешно сохранён"
        );
        this.invSaveTargetRequired = config.configMessage(
                "inv_save.target_required",
                "&#FF0000▶ &fПри выполнении из консоли необходимо указать игрока"
        );
        this.invSavePlayerNotFound = config.configMessage(
                "inv_save.player_not_found",
                "&#FF0000▶ &fИгрок &e{player} &fне найден или не в сети"
        );
        this.invSaveGiveSuccess = config.configMessage(
                "inv_save.give_success",
                "&#05A3FE▶ &fИнвентарь &e{name} &fвыдан игроку &e{player}"
        );
        this.invSaveDeleteSuccess = config.configMessage(
                "inv_save.delete_success",
                "&#05A3FE▶ &fИнвентарь &e{name} &fудален"
        );
        this.invSaveDropped = config.configMessage(
                "inv_save.dropped",
                "&#FFB800▶ &fУ игрока &e{player} &fне хватило места, выброшено предметов: &e{amount}"
        );
        this.invSaveOperationError = config.configMessage(
                "inv_save.operation_error",
                "&#FF0000▶ &fНе удалось обработать инвентарь &e{name}&f. Проверьте консоль"
        );
        this.invSaveMenuTitle = config.configMessage(
                "inv_save.menu_title",
                "&8Редактирование инвентаря: {name}"
        );

        this.securityUsage = config.configMessage(
                "security.usage",
                "&#FF0000▶ &fИспользование: &7/suncore security allow <plugin> <action>"
        );
        this.securityAdd = config.configMessage(
                "security.add",
                "&#05A3FE▶ &fПравило &7{action} &fдобавлено для плагина &7{plugin}"
        );
        this.securityAlreadyAdded = config.configMessage(
                "security.already_added",
                "&#FFB800▶ &fПравило уже активно"
        );
    }
}
