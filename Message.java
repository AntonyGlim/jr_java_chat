package com.javarush.task.task30.task3008;

import java.io.Serializable;

/**
 * Класс, отвечающий за пересылаемые сообщения
 * Сообщение Message - это данные, которые одна сторона отправляет, а вторая принимает.
 * Каждое сообщение должно иметь тип MessageType, а некоторые и дополнительные
 * данные, например, текстовое сообщение должно содержать текст. Т.к. сообщения будут
 * создаваться в одной программе, а читаться в другой, удобно воспользоваться механизмом
 * сериализации для перевода класса в последовательность битов и наоборот.
 */
public class Message implements Serializable {

    private final MessageType type; //будет содержать тип сообщения

    private final String data; //будет содержать данные сообщения

    public Message(MessageType type) {
        this.type = type;
        data = null;
    }

    public Message(MessageType type, String data) {
        this.type = type;
        this.data = data;
    }

    public MessageType getType() {
        return type;
    }

    public String getData() {
        return data;
    }
}
