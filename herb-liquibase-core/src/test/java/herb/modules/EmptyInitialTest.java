package herb.modules;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 创建号空库，后执行初始化脚本
 * 然后执行updateTest ，更新到最新版本
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(args = {"tag","v1.0"})
public class EmptyInitialTest {

    @Test
    public void testRun() {

    }

}
