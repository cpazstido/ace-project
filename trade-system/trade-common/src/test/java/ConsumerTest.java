import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:xml/spring-rocketmq-consumer.xml")
public class ConsumerTest {

    @Test
    public void testConsumer() throws InterruptedException {
        Thread.sleep(1000000000);
    }
}
