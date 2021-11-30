package herb.modules;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 创建空库，并执行到最新版本
 * 这个 执行 update 自动根据数据库脚本版本，升级到最新版本
 */
//@RunWith(SpringRunner.class)
@SpringBootTest(args = {"update"})
public class CreateUpdateLatestTest {

    @Test
    public void testRun() {

    }

}
