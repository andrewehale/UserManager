package com.andrewhale.usermanager;

import com.andrewhale.usermanager.api.Status;
import com.andrewhale.usermanager.db.UsersDAO;
import com.andrewhale.usermanager.resources.ShutdownResource;
import com.andrewhale.usermanager.resources.UserResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.channels.FileLock;
import java.nio.channels.ServerSocketChannel;
import java.sql.Connection;

/**
 * Created by andrew on 6/20/2016.
 */
public class UserManagerApplication extends Application<UserManagerConfiguration> {

    private static final Logger log = LoggerFactory.getLogger("com.andrewhale.usermanager");
    private static final String APPLICATION_CONNECTOR = "UserManagerApplication";
    private Server jettyServer;
    private final Runnable stopJettyFunction;


    public static void main(String[] args) throws Exception {
        // Check if the application is running before startup.
        if (lockApplicationInstance()) {
            new UserManagerApplication().run(args);
        } else {
            System.out.println("Unable to start the server. It appears to already be running.");
            System.exit(1);
        }
    }

    /**
     * The constructor just registers a lambda that is going to get executed when a stop is requested. This gives
     * us access to stop the dropwizard server from the web server itself.
     */
    public UserManagerApplication() {
        // set up the lambda for stop jetty function
        this.stopJettyFunction = () -> {
            // and do it in a background thread so it doesn't hang the http request
            Thread t = new Thread(() -> {
                try {
                    jettyServer.stop();
                } catch (Exception ex) {
                    log.error("Unable to stop jetty", ex);
                }
            });
            t.start();
        };
    }

    /**
     * Use file locking to ensure there is only one instance of the
     * server running.
     *
     * @return true if we were able to lock an isolated instance. We should
     * start up. False we weren't able to get an exclusive lock, don't start.
     */
    private static boolean lockApplicationInstance() {
        try {
            final File runningFile = new File("running");
            final RandomAccessFile randomAccessFile = new RandomAccessFile(runningFile, "rw");
            final FileLock fileLock = randomAccessFile.getChannel().tryLock();

            if (fileLock != null) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        try {
                            fileLock.release();
                            randomAccessFile.close();
                            runningFile.delete();
                        } catch (Exception ex) {
                            log.error("Unable to remove running file.", ex);
                        }
                    }
                });
                return true;
            }
        } catch (IOException ex) {
            System.out.println("Error locking instance: " + ex.toString());
        }
        return false;
    }

    @Override
    public void initialize(Bootstrap<UserManagerConfiguration> bootstrap) {
        log.info("Initialize");
        bootstrap.addBundle(new MigrationsBundle<UserManagerConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(UserManagerConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(UserManagerConfiguration config, Environment environment) throws Exception {
        log.info("Run");
        Status status = new Status();

        // Save off a reference to the jetty server object. This has to be done via a callback
        environment.lifecycle().addServerLifecycleListener((Server server) -> {
            jettyServer = server;

            // get the port - this is the best way I know how since it is random
            for (final Connector connector : server.getConnectors()) {
                if (APPLICATION_CONNECTOR.equals(connector.getName())) {
                    final ServerSocketChannel channel = (ServerSocketChannel) connector.getTransport();
                    try {
                        final InetSocketAddress socket = (InetSocketAddress) channel.getLocalAddress();
                        File propertiesFile = new File(Types.PROPERTIES_FILE);
                        FileUtils.write(propertiesFile, String.format("port=%s%n", Integer.toString(socket.getPort()),
                                "UTF-8", false));
                        propertiesFile.deleteOnExit();
                        System.out.println("Starting server on port " + socket.getPort());
                    } catch (IOException ex) {
                        System.out.println("Unable to determine port information: " + ex.getLocalizedMessage());
                        status.setErrorCode(1);
                        status.setErrorMessage(ex.getLocalizedMessage());
                        System.exit(1);
                    }
                }
            }
        });

        // force liquibase migrations on startup
        migrateSchema(config, environment);

        // This is where we would register database stuff
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, config.getDataSourceFactory(), "derby");
        UsersDAO users = jdbi.onDemand(UsersDAO.class);

        // This is where we would register REST endpoints
        environment.jersey().register(new ShutdownResource(this.stopJettyFunction));
        environment.jersey().register(new UserResource(users));

        // This is where we would register scheduled tasks
    }

    /**
     * This method executes our liquibase migrations on demand. Right now it is called for every start up. Probably
     * should add a way to skip it from the commandline.
     *
     * @param config
     * @param environment
     * @throws IllegalStateException
     */
    private void migrateSchema(UserManagerConfiguration config, Environment environment) throws IllegalStateException {
        log.info("migrateSchema");
        DataSourceFactory dataSourceFactory = config.getDataSourceFactory();

        ManagedDataSource dataSource = dataSourceFactory.build(environment.metrics(), "migrations-ds");

        try (Connection connection = dataSource.getConnection()) {
            JdbcConnection conn = new JdbcConnection(connection);

            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(conn);
            Liquibase liquibase = new Liquibase("migrations.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("");
            log.info("Schema migrations complete.");
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to migrate database", ex);
        } finally {
            try {
                dataSource.stop();
            } catch (Exception ex) {
                log.error("Unable to stop data source used to execute schema migration", ex);
            }
        }
    }

    /**
     * Created by andrew on 6/23/2016.
     */
    public static class Err {
        public static final int NONE = 0;
        public static final int INVALID_EMAIL = 100;

    }
}
