import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import lombok.extern.slf4j.Slf4j;
import model.CloudMessage;
import model.FileMessage;
import model.FileRequest;
import model.ListMessage;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

@Slf4j
public class CloudInterfaceController implements Initializable {

    public ListView<String> clientView;
    public ListView<String> serverView;
    private Path clientDir;
    private Path serverDir;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private CloudMessageProcessor processor;

    // read from network
    private void readLoop() {
        try {
            while (true) {
                CloudMessage message = (CloudMessage) is.readObject();
                log.info("received: {}", message);
                processor.processMessage(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateClientView() {
        try {
            clientView.getItems().clear();
            Files.list(clientDir)
                    .map(p -> p.getFileName().toString())
                    .forEach(f -> clientView.getItems().add(f));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateServerView() {
        try {
            serverView.getItems().clear();
            Files.list(serverDir)
                    .map(p -> p.getFileName().toString())
                    .forEach(f -> serverView.getItems().add(f));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            clientDir = Paths.get(System.getProperty("user.home"));
            updateClientView();
            initMouseListeners();
            processor = new CloudMessageProcessor(clientDir, clientView, serverView);
            Socket socket = new Socket("localhost", 8189);
            System.out.println("Network created...");
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMouseListeners() {

        clientView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Path current = clientDir.resolve(getItem());
                if (Files.isDirectory(current)) {
                    clientDir = current;
                    Platform.runLater(this::updateClientView);
                }
            }
        });

        serverView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                // todo Home Work
                Path current = serverDir.resolve(getItem());
                if (Files.isDirectory(current)) {
                    serverDir = current;
                    Platform.runLater(this::updateServerView);
                }
            }
        });

    }

    private String getItem() {
        return clientView.getSelectionModel().getSelectedItem();
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem();
        os.writeObject(new FileMessage(clientDir.resolve(fileName)));
    }


    public void download(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        os.writeObject(new FileRequest(fileName));
    }

    /**
     * Здесь должен быть реализован обработчик кнопки перемещение вверх по директории,
     * но пока не разобрался
     *
     * @param actionEvent
     * @throws IOException
     */
    public void directoryAboveServer(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        os.writeUTF("#above_server#");
//        os.writeUTF(fileName);
        os.flush();
    }

    public void directoryAboveClient(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        os.writeUTF("#above_client#");
//        os.writeUTF(fileName);
        os.flush();
    }
}

