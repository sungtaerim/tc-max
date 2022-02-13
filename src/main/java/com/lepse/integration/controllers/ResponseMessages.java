package com.lepse.integration.controllers;

public enum ResponseMessages {
    ITEM_NOT_EXIST("Изделие, для которого вы пытаетесь"),
    INCORRECT_DATE("Уже имеется конструкторское изменение"),
    ITEM_NOT_CONTROL("Это изделие не контролируется на внесение"),
    ITEM_ID_NOT_FOUND("Введите код принимающего изделия"),
    DATE_NOT_FOUND("Дата выдачи самой поздней версии изменения"),
    CORRECT_OPERATION("База данных обновлена"),

    //im40
    ERROR_IM40("Не удалось добвить изделие (ошибка im40)"),
    //em42
    ERROR_EM42("Не удалось добавить строки в изменение (ошибка em42)"),
    MAX10_COMPLETE("mx10: end_process COMPLETE"),

    // for logs
    RESPONSE_ITEM_NOT_EXIST("Изделие не определено в системе"),
    RESPONSE_INCORRECT_DATE("Извещение с датой ввода "),
    RESPONSE_CORRECT_EM41("Извещение EC"),
    RESPONSE_CORRECT_IM40("База данных обновлена");

    private final String message;

    ResponseMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
