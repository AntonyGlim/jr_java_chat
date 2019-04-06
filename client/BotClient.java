package com.javarush.task.task30.task3008.client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BotClient extends Client {

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false; // Мы не хотим, чтобы бот отправлял текст введенный в консоль
    }

    @Override
    protected String getUserName() {
        return "date_bot_" + (int) (Math.random() * 100);
    }

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }

    public class BotSocketThread extends Client.SocketThread{

        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            super.processIncomingMessage(message);
            if (message.indexOf(": ") < 0) return;
            String[] tokens = message.split(": ");
            String userName;
            String userRequest;
            if (tokens.length < 2) return;
            userName = tokens[0];
            userRequest = tokens[1];
            Calendar calendar = new GregorianCalendar();
            Date date = calendar.getTime();
            SimpleDateFormat formatForDate = new SimpleDateFormat();
            Map<String, String> formats = new HashMap<>();
            {
                formats.put("дата", "d.MM.YYYY");
                formats.put("день", "d");
                formats.put("месяц", "MMMM");
                formats.put("год", "YYYY");
                formats.put("время", "H:mm:ss");
                formats.put("час", "H");
                formats.put("минуты", "m");
                formats.put("секунды", "s");
            }
            if (formats.containsKey(userRequest)){
                formatForDate = new SimpleDateFormat(formats.get(userRequest));
                sendTextMessage("Информация для " + userName + ": " + formatForDate.format(date));
            }
        }

    }
}
