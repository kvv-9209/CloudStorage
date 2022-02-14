package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.Data;
import model.CloudMessage;
import model.CommandType;

@Data
public class FileMessage implements CloudMessage {

    private final String fileName;
    private final byte[] bytes;

    public FileMessage(Path path) throws IOException {
        fileName = path.getFileName().toString();
        bytes = Files.readAllBytes(path);
    }

    @Override
    public CommandType getType() {
        return CommandType.FILE;
    }
}