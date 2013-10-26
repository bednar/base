package com.github.bednar.base.event;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import com.github.bednar.base.utils.xml.FluentXml;
import com.mycila.event.Event;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jakub Bednář (03/09/2013 6:12 PM)
 */
public class UpdateApplicationSubscriber extends AbstractSubscriber<UpdateApplicationEvent>
{
    private static final Logger LOG = LoggerFactory.getLogger(UpdateApplicationSubscriber.class);

    @Nonnull
    @Override
    public Class<UpdateApplicationEvent> eventType()
    {
        return UpdateApplicationEvent.class;
    }

    @Override
    public void onEvent(final @Nonnull Event<UpdateApplicationEvent> event) throws Exception
    {
        LOG.info("UpdateApplicationSubscriber.onEvent[start]");

        FluentXml hibernateXML = FluentXml.byResource("/hibernate.cfg.xml");
        if (hibernateXML.notExists())
        {
            LOG.info("UpdateApplicationSubscriber.onEvent[not found hibernate.cfg.xml configuration]");

            return;
        }

        FluentXml changeLogXML = FluentXml.byResource("/initialize.update.xml");
        if (changeLogXML.notExists())
        {
            LOG.info("UpdateApplicationSubscriber.onEvent[not found initialize.update.xml configuration]");

            return;
        }

        Properties settings = hibernateXML.xpathsToProperties(
                "driver",   "//property[@name='hibernate.connection.driver_class']/text()",
                "url",      "//property[@name='hibernate.connection.url']/text()",
                "user",     "//property[@name='hibernate.connection.username']/text()",
                "password", "//property[@name='hibernate.connection.password']/text()");

        //Init driver
        Class.forName(settings.getProperty("driver"));

        //Init connection to DB
        Connection connection = DriverManager.getConnection(settings.getProperty("url"), settings);

        Database database = DatabaseFactory
                .getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));

        Liquibase liquibase = new Liquibase("initialize.update.xml", new ClassLoaderResourceAccessor(), database);
        liquibase.update("development");

        connection.close();

        LOG.info("UpdateApplicationSubscriber.onEvent[done]");
    }
}
