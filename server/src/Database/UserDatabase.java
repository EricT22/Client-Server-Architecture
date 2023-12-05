package Database;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class UserDatabase extends AbstractSQLConnector{

    public UserDatabase(String username, String pass) {
        super(username, pass);
    }

    public void updatePassword(String username, String password) { //INSERTS DISPLAYDATA AT USERNAME

        try {
            statement.executeUpdate("UPDATE usertable SET pass='" + password + "' WHERE username='"+username+"';");
            resultSet = statement.executeQuery("SELECT * FROM usertable;");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getEmail(String username){ //GETS EMAIL FROM GIVEN USERNAME
        try {
            resultSet = statement.executeQuery("SELECT emailaddress FROM usertable WHERE username='"+username+"';");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return this.getResultSet();
    }


    public void addNewData(String username, String password, String email) {  //CREATES NEW ROW IN TABLE WITH USERNAME AND DISPLAY DATA

        try {
            statement.executeUpdate("INSERT INTO usertable (username, pass, emailaddress) VALUES ('"+username+"', '"+password+"', '"+email+"');");
            resultSet = statement.executeQuery("SELECT * FROM usertable;");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void printTable(){ //JUST PRINTS ENTIRE TABLE -- USED FOR TESTING
        try {
            resultSet = statement.executeQuery("SELECT * FROM usertable;");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(getResultSet());
    }
    @Override
    public String getResultSet() { //RETURNS RESULTSET AS A STRING
       String returnString = "";
        try {
            // -- the metadata tells us how many columns in the data
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            //System.out.println("columns: " + numberOfColumns);
            // -- loop through the ResultSet one row at a time
            // Note that the ResultSet starts at index 1
            while (resultSet.next()) {
            // -- loop through the columns of the ResultSet
                for (int i = 1; i < numberOfColumns; ++i) {
                    returnString += (resultSet.getString(i) + "\t");
                }
                returnString += (resultSet.getString(numberOfColumns) + "\n");
            }
        }
        catch (SQLException ex) {
            // handle any errors
            returnString +=("SQLException: " + ex.getMessage());
            returnString +=("SQLState: " + ex.getSQLState());
            returnString +=("VendorError: " + ex.getErrorCode());
        }
        return returnString;
    }
}

