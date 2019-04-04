package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.Connection;
import com.javarush.task.task30.task3008.ConsoleHelper;
import com.javarush.task.task30.task3008.Message;
import com.javarush.task.task30.task3008.MessageType;

import java.io.IOException;

public class Client {

    protected Connection connection;
    private volatile boolean clientConnected = false; //оно будет устанавливаться в true, если клиент подсоединен к серверу или в false в противном случае

    /**
     * Должен запросить ввод адреса сервера у пользователя и вернуть введенное значение.
     * Адрес может быть строкой, содержащей ip, если клиент и сервер запущен на разных машинах
     * или 'localhost', если клиент и сервер работают на одной машине.
     * @return
     */
    protected String getServerAddress(){
        ConsoleHelper.writeMessage("Введите адрес сервера");
        String address = ConsoleHelper.readString();
        return address;
    }

    /**
     * Должен запрашивать ввод порта сервера и возвращать его
     * @return
     */
    protected int getServerPort(){
        ConsoleHelper.writeMessage("Введите адрес порта");
        int port = ConsoleHelper.readInt();
        return port;
    }

    /**
     * Должен запрашивать и возвращать имя пользователя
     * @return
     */
    protected String getUserName(){
        ConsoleHelper.writeMessage("Введите имя пользователя");
        String userName = ConsoleHelper.readString();
        return userName;
    }

    /**
     *  В данной реализации клиента всегда должен возвращать true
     *  (мы всегда отправляем текст введенный в консоль).
     *  Этот метод может быть переопределен, если мы будем писать какой-нибудь другой клиент,
     *  унаследованный от нашего, который не должен отправлять введенный в консоль текст.
     * @return
     */
    protected boolean shouldSendTextFromConsole(){
        return true;
    }

    /**
     * Должен создавать и возвращать новый объект класса SocketThread
     * @return
     */
    protected SocketThread getSocketThread(){
        return new SocketThread();
    }

    /**
     * Создает новое текстовое сообщение, используя переданный текст
     * и отправляет его серверу через соединение connection.
     * @param text
     */
    protected void sendTextMessage(String text){
        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Произошло исключение IOException");
            clientConnected = false;
        }
    }



    /**
     * Будет отвечать за поток, устанавливающий сокетное соединение
     * и читающий сообщения сервера.
     */
    public class SocketThread extends Thread{

    }
}
