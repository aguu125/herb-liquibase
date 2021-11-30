package herb.modules;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 非空库，执行基线版本，并执行升级到最新版本
 */
//@RunWith(SpringRunner.class)
@SpringBootTest(args = {"update"})
public class BaselineUpdateLatestTest {

    @Test
    public void testRun() {

    }

}
