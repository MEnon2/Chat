package chatjfx;

import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DbAuthService implements AuthService {

    static final String DATABASE_URL = "jdbc:sqlite:javadb.db";
    static final String TABLE_NAME = "chatUsers";


    private Connection connection;
    private List<Entry> entries = new ArrayList<>();
    private Logger logger;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public DbAuthService(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void start() {

        try {
            connection = DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        logger.info("Authentification service STARTED");

        createTable();
        selectUsers();

        for (int i = 0; i < 10; i++) {
            createUser("login" + i, "pass" + i, "nick" + i);
        }
    }

    @Override
    public void stop() {
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        logger.info("Authentification service STOPED");
    }


    private class Entry {
        private final String login;
        private String pass;
        private String nick;

        public Entry(String login, String pass, String nick) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;
        }
    }

  //создание таблицы
    private void createTable() {
        try {
            if (createTableSQL()) {
                 logger.info("Table <" + TABLE_NAME + "> created");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private boolean createTableSQL() throws SQLException {
        String querySQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " " +
                "(" +
                "userId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "login TEXT NOT NULL UNIQUE," +
                "pass TEXT NOT NULL," +
                "nick TEXT NOT NULL" +
                ")";
        PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
        return preparedStatement.execute();
    }

    //выборка пользователей
    private void selectUsers() {
        ResultSet rs = null;
        try {
            rs = selectUsersSQL();
            while (rs.next()) {
                entries.add(new Entry(rs.getString("login"), rs.getString("pass"), rs.getString("nick")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private ResultSet selectUsersSQL() throws SQLException {
        String querySQL = "SELECT * FROM " + TABLE_NAME;
        PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
        return preparedStatement.executeQuery();
    }

    //получение пользователя
    private Optional<Entry> selectUser(String login) {
        return entries.stream().filter(e -> e.login.equals(login)).findFirst();
    }

    private ResultSet selectUserSQL(String login) throws SQLException {
        String querySQL = "SELECT * FROM " + TABLE_NAME + " WHERE login = '?'";

        PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
        preparedStatement.setString(1, login);
        return preparedStatement.executeQuery();
    }


    @Override
    public Optional<String> getUserNick(String login, String pass) {
        if (login.isEmpty() || pass.isEmpty()) {
            return Optional.empty();
        }
        return entries.stream().filter(e -> e.login.equals(login)).filter(e -> e.pass.equals(pass)).map(e -> e.nick).findFirst();
    }

    //добавление пользователей
    @Override
    public boolean createUser(String login, String pass, String nick) {
        if (login.isEmpty() || pass.isEmpty() || nick.isEmpty()) {
            logger.error("The login, password and  nickname cannot be empty");
            return false;
        }

        if (!selectUser(login).isPresent()) {
            try {
                if (createUserSQL(login, pass, nick)) {
                    logger.info("User <" + login + "> crated successfully");

                    entries.add(new Entry(login, pass, nick));
                    return true;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }

    private boolean createUserSQL(String login, String pass, String nick) throws SQLException {
        String querySQL = "INSERT INTO " + TABLE_NAME + " (login, pass, nick) VALUES ('?', '?', '?')";

        PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, pass);
        preparedStatement.setString(3, nick);
        return preparedStatement.execute();
    }


    //удаление пользователей
    @Override
    public boolean deleteUser(String login) {
        if (login.isEmpty()) {
            return false;
        }

        Optional<Entry> foundUser = selectUser(login);

        if (foundUser.isPresent()) {
            try {
                if (deleteUserSQL(login)) {
                    logger.info("User <" + login + "> deleted successfully");

                    entries.remove(foundUser.get());
                    return true;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }

    public boolean deleteUserSQL(String login) throws SQLException {
        String querySQL = "DELETE FROM " + TABLE_NAME + " WHERE login = '?'";

        PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
        preparedStatement.setString(1, login);

        return preparedStatement.execute(querySQL);
    }

    @Override
    public boolean updateUserInfo(String login, String field, String newValue) {
        if (login.isEmpty()) {
            return false;
        }

        Optional<Entry> foundUser = selectUser(login);
        if (foundUser.isPresent()) {
            try {
                updateUserInfoSQL(login, field, newValue);
                logger.info("User <" + login + "> updated successfully");

                foundUser.get().nick = newValue;
                return true;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }

    public boolean updateUserInfoSQL(String login, String field, String newValue) throws SQLException {
        String querySQL = "UPDATE " + TABLE_NAME + " SET " + field + " = ? WHERE login = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
        preparedStatement.setString(1, newValue);
        preparedStatement.setString(2, login);

        return preparedStatement.execute();
    }
}

