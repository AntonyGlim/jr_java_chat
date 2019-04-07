import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * класс соединения между клиентом и сервером
 * Класс Connection будет выполнять роль обертки над классом
 * java.net.Socket, которая должна будет уметь сериализовать и десериализовать объекты
 * типа Message в сокет. Методы этого класса должны быть готовы к вызову из разных
 * потоков.
 */
public class Connection implements Closeable {

    private final Socket socket;

    private final ObjectOutputStream out;

    private final ObjectInputStream in;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());    //Создать объект класса ObjectOutputStream нужно до того, как будет создаваться объект класса ObjectInputStream,
        in = new ObjectInputStream(socket.getInputStream());       //иначе может возникнуть взаимная блокировка потоков,
    }

    /**
     * Должен записывать (сериализовать) сообщение в ObjectOutputStream.
     * Этот метод будет вызываться из нескольких потоков.
     * @param message
     */
    public void send(Message message) throws IOException {
        synchronized (out){
            out.writeObject(message);
        }
    }

    /**
     * Должен читать (десериализовать) данные из ObjectInputStream.
     * Операция чтения не может быть одновременно вызвана несколькими потоками.
     * @return
     */
    public Message receive() throws IOException, ClassNotFoundException {
        Message msg = null;
        synchronized (in){
            msg = (Message) in.readObject();
        }
        return msg;
    }

    /**
     * Возвращает удаленный адрес сокетного соединения.
     * @return
     */
    public SocketAddress getRemoteSocketAddress(){
        return socket.getRemoteSocketAddress();
    }

    /**
     * Должен закрывать все ресурсы класса
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
