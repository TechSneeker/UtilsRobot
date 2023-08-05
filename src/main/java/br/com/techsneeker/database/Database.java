package br.com.techsneeker.database;

import br.com.techsneeker.Main;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.net.URISyntaxException;
import java.sql.*;

public class Database {

    private static HikariDataSource dataSource;

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
           e.printStackTrace();
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
            e.printStackTrace();
        }

        return false;
    }

    public void insertConfig(long id, String value) {
        String query = "INSERT INTO perm_survey (id, perm) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, id);
            preparedStatement.setString(2, value);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
}
