package model;

import lombok.Data;
import model.CloudMessage;
import model.CommandType;

@Data
public class FileRequest implements CloudMessage {

    private final String fileName;

    @Override
    public CommandType getType() {
        return CommandType.FILE_REQUEST;
    }
}
