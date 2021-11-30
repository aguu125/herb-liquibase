package herb.modules.liquibase.handler;

import lombok.extern.slf4j.Slf4j;

/**
 * 升级处理类
 *
 * @auth wanghm
 **/
@Slf4j
public class TagHandler {

    /**
     * 升级到指定版本
     */
    private String assignVersion;

    /**
     * 是否升级到最新版本
     */
    private boolean updateToLatest;


    public TagHandler(String[] args) {
        if ("updateToVersion".equalsIgnoreCase(args[0])) {

        }
    }


    public void execute() {


    }

}
