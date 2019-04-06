package com.javarush.task.task30.task3008.client;

public class ClientGuiController extends Client{

    private ClientGuiModel model = new ClientGuiModel();
    private ClientGuiView view = new ClientGuiView(this);


    /**
     * должен создавать и возвращать объект типа GuiSocketThread
     * @return
     */
    @Override
    protected SocketThread getSocketThread() {
        return new GuiSocketThread();
    }

    /**
     * TODO Разберись, почему нет необходимости вызывать метод run()
     * TODO в отдельном потоке, как мы это делали для консольного клиента.
     */
    @Override
    public void run() {
        SocketThread socketThread = getSocketThread();
        socketThread.run();
    }

    @Override
    protected String getServerAddress() {
        return view.getServerAddress();
    }

    @Override
    protected int getServerPort() {
        return view.getServerPort();
    }

    @Override
    protected String getUserName() {
        return view.getUserName();
    }

    public ClientGuiModel getModel(){
        return model;
    }

    public static void main(String[] args) {
        ClientGuiController clientGuiController = new ClientGuiController();
        clientGuiController.run();
    }



    public class GuiSocketThread extends Client.SocketThread{
        /**
         * Должен устанавливать новое сообщение у модели
         * и вызывать обновление вывода сообщений у представления.
         * @param message
         */
        @Override
        protected void processIncomingMessage(String message) {
            model.setNewMessage(message);
            view.refreshMessages();
        }

        /**
         * Должен добавлять нового пользователя в модель
         * и вызывать обновление вывода пользователей у отображения.
         * @param userName
         */
        @Override
        protected void informAboutAddingNewUser(String userName) {
            model.addUser(userName);
            view.refreshUsers();
        }

        /**
         * должен удалять пользователя из модели
         * и вызывать обновление вывода пользователей у отображения.
         * @param userName
         */
        @Override
        protected void informAboutDeletingNewUser(String userName) {
            model.deleteUser(userName);
            view.refreshUsers();
        }

        /**
         * Должен вызывать аналогичный метод у представления
         * @param clientConnected
         */
        @Override
        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            view.notifyConnectionStatusChanged(clientConnected);
        }
    }
}
