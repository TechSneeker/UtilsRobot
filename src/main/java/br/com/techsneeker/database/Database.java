package br.com.techsneeker.database;

import br.com.techsneeker.Main;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class Database {

    private static HikariDataSource dataSource;
    private static final ExecutorService executor = Executors.newFixedThreadPool(5);
    private static final Logger logger = Logger.getLogger(Database.class.getName());

    public Database() throws URISyntaxException {
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        String directory = new File(path).getParent();

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:sqlite:" + directory + File.separator + "config.db");
        config.setMaximumPoolSize(1);

        dataSource = new HikariDataSource(config);
    }

    public void createTables() {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();

            String query = "CREATE TABLE IF NOT EXISTS perm_survey ("
                    + "id INTEGER PRIMARY KEY, "
                    + "perm VARCHAR(60) NOT NULL DEFAULT 'all' )";

            statement.execute(query);
            statement.close();

        } catch (SQLException e) {
           logger.severe("DATABASE - Exception thrown trying to create table: " + e + ";");
        }
    }

    public void addPermConfiguration(long id, String value) {
        try {
            Future<Boolean> futureCheck = executor.submit(() -> this.checkIfExists(id));
            boolean configExists = futureCheck.get();

            if (!configExists) executor.submit(() -> this.insertPerm(id, value));
            else executor.submit(() -> this.updatePermById(id, value));

        } catch (ExecutionException | InterruptedException e) {
            logger.severe("DATABASE - Exception thrown trying to add/update survey configuration: " + e + ";");
        }
    }

    public boolean checkIfExists(long id) {
        String query = "SELECT * FROM perm_survey WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            logger.severe("DATABASE - Exception thrown trying to check survey configuration: " + e + ";");
        }

        return false;
    }

    public void insertPerm(long id, String value) {
        String query = "INSERT INTO perm_survey (id, perm) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, value);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.severe("DATABASE - Exception thrown trying to insert survey configuration: " + e + ";");
        }
    }

    public void updatePermById(long id, String value) {
        String query = "UPDATE perm_survey SET perm = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, value);
            preparedStatement.setLong(2, id);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            logger.severe("DATABASE - Exception thrown trying to update survey configuration: " + e + ";");
        }
    }

    public String getPermByid(long id) {
        String query = "SELECT perm FROM perm_survey WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) return resultSet.getString("perm");
            }

        } catch (SQLException e) {
            logger.severe("DATABASE - Exception thrown trying to select survey configuration: " + e + ";");
        }

        return "all";
    }
}
