package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.Connection;

public class Client {

    protected Connection connection;
    private volatile boolean clientConnected = false; //оно будет устанавливаться в true, если клиент подсоединен к серверу или в false в противном случае

    /**
     * Будет отвечать за поток, устанавливающий сокетное соединение
     * и читающий сообщения сервера.
     */
    public class SocketThread extends Thread{

    }
}
