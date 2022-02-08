import model.CloudMessage;

public interface MessageProcessor {

    void processMessage(CloudMessage msg);

}