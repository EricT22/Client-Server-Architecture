package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractSQLConnector implements DatabaseInterface {
    protected Connection connection = null;
    protected Statement statement = null;
    protected ResultSet resultSet = null;
    protected String url = "jdbc:mysql://127.0.0.1:3306/clientServerApp";

    public AbstractSQLConnector(String username, String pass) {

        try {
            connection = DriverManager.getConnection(url, username, pass);

            statement = connection.createStatement();

            resultSet = statement.executeQuery("SELECT VERSION()");
            if (resultSet.next()) {

                System.out.println("MySQL version: " + resultSet.getString(1) + "\n=====================\n");
            }

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

    }

    public abstract void updateDatabase();

    public abstract String getResultSet();
}