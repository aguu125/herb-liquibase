package herb.modules;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 指定升级到最新版本
 */
@RunWith(SpringRunner.class)
@SpringBootTest(args = {"updateToVersion","v1.1"})
public class UpdateToVersionTest {

    @Test
    public void testRun() {

    }

}
