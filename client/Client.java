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
     * Должен создавать вспомогательный поток SocketThread,
     * ожидать пока тот установит соединение с сервером, а после этого в цикле
     * считывать сообщения с консоли и отправлять их серверу.
     */
    public void run(){
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();

        synchronized (this){
            try {
                wait();
            } catch (InterruptedException e) {
                ConsoleHelper.writeMessage("Возникла ошибка при ожидаии главного потока!");
                return;
            }
        }

        if (clientConnected){
            ConsoleHelper.writeMessage("Соединение установлено. Для выхода наберите команду 'exit'.");
            while (clientConnected){
                String msg = ConsoleHelper.readString();
                if (msg.equals("exit")) break;
                if (shouldSendTextFromConsole()) {
                    sendTextMessage(msg);
                }
            }
        } else {
            ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }





    /**
     * Будет отвечать за поток, устанавливающий сокетное соединение
     * и читающий сообщения сервера.
     */
    public class SocketThread extends Thread{

        /**
         * Должен выводить текст message в консоль
         * @param message
         */
        protected void processIncomingMessage(String message){
            ConsoleHelper.writeMessage(message);
        }

        /**
         * Должен выводить в консоль информацию о том, что участник с именем userName присоединился к чату.
         * @param userName
         */
        protected void informAboutAddingNewUser(String userName){
            ConsoleHelper.writeMessage("Участник с именем " + userName + " присоединился к чату");
        }

        /**
         * Должен выводить в консоль, что участник с именем userName покинул чат.
         * @param userName
         */
        protected void informAboutDeletingNewUser(String userName){
            ConsoleHelper.writeMessage("Участник с именем " + userName + " покинул чат");
        }

        /**
         * Должен:
         * а) Устанавливать значение поля clientConnected внешнего объекта Client в соответствии с переданным параметром.
         * б) Оповещать (пробуждать ожидающий) основной поток класса Client.
         * @param clientConnected
         */
        protected void notifyConnectionStatusChanged(boolean clientConnected){
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this){
                Client.this.notify();
            }
        }

        /**
         * Этот метод будет представлять клиента серверу.
         * @throws IOException
         * @throws ClassNotFoundException
         */
        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true){
                Message msg = connection.receive();
                if (msg.getType() == MessageType.NAME_REQUEST){
                    String userName = getUserName();
                    connection.send(new Message(MessageType.USER_NAME, userName));
                    continue;
                }
                if (msg.getType() == MessageType.NAME_ACCEPTED){
                    notifyConnectionStatusChanged(true);
                    return;
                }
                throw new IOException("Unexpected MessageType");
            }
        }

        /**
         * Этот метод будет реализовывать главный цикл обработки сообщений сервера.
         * @throws IOException
         * @throws ClassNotFoundException
         */
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            while (true) {
                Message msg = connection.receive();
                if (msg.getType() == MessageType.TEXT) {
                    processIncomingMessage(msg.getData());
                    continue;
                }
                if (msg.getType() == MessageType.USER_ADDED) {
                    informAboutAddingNewUser(msg.getData());
                    continue;
                }
                if (msg.getType() == MessageType.USER_REMOVED) {
                    informAboutDeletingNewUser(msg.getData());
                    continue;
                }
                throw new IOException("Unexpected MessageType");
            }
        }

    }

}
