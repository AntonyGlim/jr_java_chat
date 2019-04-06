package com.javarush.task.task30.task3008;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * основной класс сервера
 */
public class Server {

    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>(); //ключом будет имя клиента, а значением - соединение с ним

    /**
     * Должен отправлять сообщение message
     * всем соединениям из connectionMap.
     * @param message
     */
    public static void sendBroadcastMessage(Message message) {
        for (Map.Entry<String, Connection> entry: connectionMap.entrySet()) {
            try {
                entry.getValue().send(message);
            } catch (IOException e){
                System.out.println("Не смогли отправить сообщение");
            }
        }

    }

    public static void main(String[] args) throws IOException {
        
        try (ServerSocket serverSocket = new ServerSocket(ConsoleHelper.readInt());) {
            ConsoleHelper.writeMessage("Сервер запущен");
            while (true){
                Socket socket = serverSocket.accept();
                Thread thread = new Handler(socket);
                thread.start();
            }
        } catch (Exception e){
            ConsoleHelper.writeMessage("Ошибка!");
        }
    }

    /**
     * Новый поток обработчик Handler, в котором будет происходить обмен
     * сообщениями с клиентом.
     * Должен реализовывать протокол общения с клиентом.
     */
    private static class Handler extends Thread{

        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        /**
         * Главный метод класса Handler, который будет вызывать все
         * вспомогательные методы
         */
        @Override
        public void run() {

            ConsoleHelper.writeMessage(socket.getRemoteSocketAddress() + "");

            String userName = "";
            try (Connection  connection = new Connection(socket);) {
                userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                notifyUsers(connection, userName);
                serverMainLoop(connection, userName);
            } catch (IOException e) {
                ConsoleHelper.writeMessage("Ошибка ввода-вывода. Произошла ошибка при обмене данными с удаленным адресом.");
            } catch (ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Ошибка класс не найден. Произошла ошибка при обмене данными с удаленным адресом.");
            }
            if (!userName.equals("")) {
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
                ConsoleHelper.writeMessage("Соединение с удаленным адресом закрыто!");
            }

        }

        /**
         * Знакомства сервера с клиентом
         * @param connection - принимает соединение
         * @return - возвращает имя нового клиента
         * @throws IOException
         * @throws ClassNotFoundException
         */
        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException{
            connection.send(new Message(MessageType.NAME_REQUEST));
            Message message;
            while (true){
                message = connection.receive();
                if (message.getData() != null && !message.getData().equals("")){
                    if (message.getType().equals(MessageType.USER_NAME)){
                        if (!connectionMap.containsKey(message.getData())){
                            connectionMap.put(message.getData(), connection);
                            connection.send(new Message(MessageType.NAME_ACCEPTED));
                            break;
                        }
                    }
                }
                connection.send(new Message(MessageType.NAME_REQUEST));
            }
            return message.getData();
        }

        /**
         * Отправка клиенту (новому участнику) информации об остальных клиентах (участниках) чата.
         * Метод предназначен для нового пользователя, а значит
         * именно ему будет выведен результат работы метода - список всех участников чата.
         * @param connection - соединение с участником, которому будем слать информацию
         * @param userName - его имя
         */
        private void notifyUsers(Connection connection, String userName) throws IOException {
            for (Map.Entry<String, Connection> entry: connectionMap.entrySet()) {
                if (entry.getKey().equals(userName)) continue;
                connection.send(new Message(MessageType.USER_ADDED, entry.getKey()));
            }
        }

        /**
         * Метод принимает сообщение от пользователя,
         * обрабатывает его, и рассылает всем участникам чата.
         * @param connection - соединение с участником, которому будем слать информацию
         * @param userName - его имя
         */
        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message msg = connection.receive();
                if (msg.getType() == (MessageType.TEXT)) {
                    String newMsg = userName + ": " + msg.getData();
                    sendBroadcastMessage(new Message(MessageType.TEXT, newMsg));
                } else {
                    ConsoleHelper.writeMessage("Сообщение не является текстом!");
                }
            }
        }

    }

}
