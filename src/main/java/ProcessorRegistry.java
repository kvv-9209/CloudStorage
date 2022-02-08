import io.netty.channel.ChannelOutboundBuffer;
import model.CloudMessage;
import model.CommandType;

import java.util.HashMap;
import java.util.Map;


public class ProcessorRegistry {

    private Map<CommandType, ChannelOutboundBuffer.MessageProcessor> map;

    public ProcessorRegistry() {
        map = new HashMap<>();
/*        map.put(CommandType.FILE, msg -> {

        });*/
    }

    public void process(CloudMessage msg) throws Exception {
        map.get(msg.getType()).processMessage(msg);
    }

}
