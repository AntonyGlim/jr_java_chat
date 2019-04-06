package com.javarush.task.task30.task3008.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Графический интерфейс согласно модели MVC
 */
public class ClientGuiModel {

    private final Set<String> allUserNames = new HashSet<>(); //В нем будет храниться список всех участников чата
    private String newMessage; //будет храниться новое сообщение, которое получил клиент

    /**
     * Должен добавлять имя участника во множество,
     * хранящее всех участников
     * @param newUserName
     */
    public void addUser(String newUserName){
        allUserNames.add(newUserName);
    }

    /**
     * Будет удалять имя участника из множества
     * @param userName
     */
    public void deleteUser(String userName){
        allUserNames.remove(userName);
    }

    public Set<String> getAllUserNames() {
        return Collections.unmodifiableSet(allUserNames);
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }
}
