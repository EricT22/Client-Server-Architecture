package Database;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class SystemDatabase extends AbstractSQLConnector {
    
    public static void main(String[] args) {
        
    }

    // Constructor
    public SystemDatabase(String username, String password) {
        super(username, password);
    }

    public String getDisplayData(String username) { //WILL SELECT DIDPLAY DATA FROM GIVEN USERNAME
        try {
            String query = "SELECT displayData FROM systemTable WHERE username= ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);

            resultSet = preparedStatement.executeQuery();

            // resultSet = statement.executeQuery("SELECT displayData FROM systemTable WHERE username='"+username+"';");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.getResultSet();
    }
        

    public void updateDisplayData(String username, String displayData) { //INSERTS DISPLAYDATA AT USERNAME

        try {
            String query = "UPDATE systemTable SET displayData= ? WHERE username= ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, displayData);
            preparedStatement.setString(2, username);

            preparedStatement.executeUpdate();

            // statement.executeUpdate("UPDATE systemTable SET displayData='" + displayData + "' WHERE username='"+username+"';");
            
            resultSet = statement.executeQuery("SELECT * FROM systemTable;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNewData(String username, String displayData) {  //CREATES NEW ROW IN TABLE WITH USERNAME AND DISPLAY DATA

        try {
            String query = "INSERT INTO systemTable (username, displayData) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, displayData);

            preparedStatement.executeUpdate();

            // statement.executeUpdate("INSERT INTO systemTable (username, displayData) VALUES ('"+username+"', '"+displayData+"');");
            resultSet = statement.executeQuery("SELECT * FROM systemTable;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void printTable(){ //JUST PRINTS ENTIRE TABLE -- USED FOR TESTING
        try {
            resultSet = statement.executeQuery("SELECT * FROM systemTable;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(getResultSet());
    }
   
    //getResultSet required by AbstractSQLConnector
    @Override
    public String getResultSet() {

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
