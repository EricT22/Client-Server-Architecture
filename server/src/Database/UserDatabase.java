package Database;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class UserDatabase extends AbstractSQLConnector {

    public UserDatabase(String username, String pass) {
        super(username, pass);
    }

    public static void main(String[] args) {
        UserDatabase userDB = new UserDatabase("root", "password");
        System.out.println(userDB.getNumOfRegisteredUsers());
    }

    public void updatePassword(String username, String password) { // INSERTS PASSWORD AT USERNAME

        try {
            String query = "UPDATE usertable SET pass= ? WHERE username= ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, password);
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();


            // statement.executeUpdate("UPDATE usertable SET pass='" + password + "' WHERE username='" + username + "';");
            
            resultSet = statement.executeQuery("SELECT * FROM usertable;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getEmail(String username) { // GETS EMAIL FROM GIVEN USERNAME
        try {
            String query = "SELECT emailaddress FROM usertable WHERE username= ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            resultSet = preparedStatement.executeQuery();

            // resultSet = statement.executeQuery("SELECT emailaddress FROM usertable WHERE username='" + username + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.getResultSet();
    }

    public String getPassword(String username) {// GETS PASSWORD FROM GIVEN USERNAME
        try {
            String query = "SELECT pass FROM usertable WHERE username= ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            resultSet = preparedStatement.executeQuery();

            // resultSet = statement.executeQuery("SELECT pass FROM usertable WHERE username='" + username + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.getResultSet();
    }

    public void addNewData(String username, String password, String email) { // CREATES NEW ROW IN TABLE WITH USERNAME
                                                                             // PASSWORD EMAIL

        try {
            String query = "INSERT INTO usertable (username, pass, emailaddress) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, email);

            preparedStatement.executeUpdate();

            // statement.executeUpdate("INSERT INTO usertable (username, pass, emailaddress) VALUES ('" + username + "', '"
            //         + password + "', '" + email + "');");

            resultSet = statement.executeQuery("SELECT * FROM usertable;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printTable() { // JUST PRINTS ENTIRE TABLE -- USED FOR TESTING
        try {
            resultSet = statement.executeQuery("SELECT * FROM usertable;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(getResultSet());
    }

    public int getNumOfRegisteredUsers() {
        try {
            resultSet = statement.executeQuery("SELECT * FROM usertable;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getRowCount();
    }

    @Override
    public String getResultSet() { // RETURNS RESULTSET AS A STRING
        String returnString = "";
        try {
            // -- the metadata tells us how many columns in the data
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            // System.out.println("columns: " + numberOfColumns);
            // -- loop through the ResultSet one row at a time
            // Note that the ResultSet starts at index 1
            while (resultSet.next()) {
                // -- loop through the columns of the ResultSet
                for (int i = 1; i < numberOfColumns; ++i) {
                    returnString += (resultSet.getString(i) + "\t");
                }
                returnString += (resultSet.getString(numberOfColumns) + "\n");
            }
        } catch (SQLException ex) {
            // handle any errors
            returnString += ("SQLException: " + ex.getMessage());
            returnString += ("SQLState: " + ex.getSQLState());
            returnString += ("VendorError: " + ex.getErrorCode());
        }
        return returnString;
    }

    public int getRowCount(){
        int returnVal = 0;
        try {
            // -- the metadata tells us how many columns in the data
            while (resultSet.next()) {
                returnVal++;
            }
        } catch (SQLException ex) {
        }
        return returnVal;
    }
}
