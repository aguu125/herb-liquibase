package herb.modules;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 比较少用
 * 升级到指定版本，而不是最新版本
 */
@SpringBootTest(args = {"updateToVersion", "v1.1"})
public class CreateAndUpdateToVersionTest {

    @Test
    public void testRun() {

    }

}
