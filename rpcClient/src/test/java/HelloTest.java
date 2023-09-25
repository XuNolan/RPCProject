import api.ServiceApi;
import org.junit.Test;

public class HelloTest {
    @Test
    public void testHello(){
        ServiceApi service = new ServiceFactory().getService();
        System.out.println(service.hello("hhh",0));
    }
}
