import com.ace.trade.common.api.IUserApi;
import com.ace.trade.common.protocol.user.QueryUserReq;
import com.ace.trade.common.protocol.user.QueryUserRes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "classpath:xml/spring-rest-client.xml")
public class SpringRestClientTest {
    @Autowired
    private IUserApi userApi;

    @Test
    public void test(){
        QueryUserReq queryUserReq = new QueryUserReq();
        queryUserReq.setUserId(5);
        QueryUserRes queryUserRes = userApi.queryUserById(queryUserReq);
        System.out.println(queryUserRes.toString());
    }
}
