import com.ace.trade.common.rocketmq.IMessageProcessor;
import org.apache.rocketmq.common.message.MessageExt;

public class TestProcessor implements IMessageProcessor {
    public boolean handleMessage(MessageExt messageExt) {
        System.out.println("收到消息："+messageExt.toString());
        return true;
    }
}
