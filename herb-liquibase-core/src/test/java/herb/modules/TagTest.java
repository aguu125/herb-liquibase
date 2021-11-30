package herb.modules;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 创建号空库，后执行初始化脚本
 * 然后执行updateTest ，更新到最新版本
 */
@SpringBootTest(args = {"tag", "v0.9"})
public class TagTest {

    @Test
    public void testRun() {

    }

}
