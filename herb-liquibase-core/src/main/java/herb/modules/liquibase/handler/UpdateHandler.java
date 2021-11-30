package herb.modules.liquibase.handler;

import herb.modules.liquibase.config.ProjectVersionsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;

/**
 * 升级处理类
 *
 * @auth wanghm
 **/
@Slf4j
public class UpdateHandler {

    /**
     * 升级到指定版本
     */
    private String assignVersion;

    /**
     * 是否升级到最新版本
     */
    private boolean updateToLatest;


    public UpdateHandler(String[] args) {
        if ("updateToVersion".equalsIgnoreCase(args[0])) {

        }
    }


    public void execute() {


    }

}
