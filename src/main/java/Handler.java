
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


public class Handler implements Runnable {
    private static final int SIZE = 256;

    private Path clientDir;
    private DataInputStream is;
    private DataOutputStream os;
    private final byte[] buf;

    public Handler(Socket socket) throws IOException {
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        clientDir = Paths.get("data");
        buf = new byte[SIZE];
        sendServerFiles();
    }

    public void sendServerFiles() throws IOException {
        List<String> files = Files.list(clientDir)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        os.writeUTF("#list#");
        os.writeInt(files.size());
        for (String file : files) {
            os.writeUTF(file);
        }
        os.flush();
    }

    @Override
    public void run() {
        try {
            while (true) {
                String command = is.readUTF();
                System.out.println("received: " + command);
                if (command.equals("#file#")) {
                    Sender.getFile(is, clientDir, SIZE, buf);
                    sendServerFiles();
                } else if (command.equals("#get_file#")) {
                    String fileName = is.readUTF();
                    Sender.sendFile(fileName, os, clientDir);
                    /**
                     * Если приходит команда ниже, тогда возвращает на директорию выше в сервере и в клиенте соответственно
                     */
                } else if (command.equals("#above_server#")) {
    //                clientDir = Paths.get("..");

                } else if (command.equals("#above_client#")) {
//пока не понимаю как это реализовать
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

