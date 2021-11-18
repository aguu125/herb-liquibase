package herb.modules;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 这个 执行 update 自动根据数据库脚本版本，升级到最新版本
 */
@RunWith(SpringRunner.class)
@SpringBootTest(args = {"update"})
public class UpdateTest {

    @Test
    public void testRun() {

    }

}
