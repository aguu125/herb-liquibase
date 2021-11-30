package herb.modules.liquibase.core;

import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @auth wanghm
 **/
public class HerbLiquibase extends SpringLiquibase {

    Liquibase liquibase;

    public synchronized Liquibase createLiquibase() throws LiquibaseException, SQLException {
        if (liquibase != null) {
            return liquibase;
        }
        Connection c = null;
        c = getDataSource().getConnection();
        liquibase = super.createLiquibase(c);
        return liquibase;
    }

    public void tagAndUpdate(String tagName) throws SQLException, LiquibaseException {
        Liquibase liquibase = createLiquibase();

        liquibase.tag(tagName);

        StringWriter stringWriter = new StringWriter();

        liquibase.update("", stringWriter);

        String msg = stringWriter.getBuffer().toString();
        log.info(msg);
    }
}
