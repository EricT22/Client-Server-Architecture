package Database;

// SQL jdbc not added to library yet
public abstract class AbstractSQLConnector implements DatabaseInterface {

    private Connection connection;
    private Statement stmt;
    private ResultSet rset;

    private String url;

    public AbstractSQLConnector(String username, String password){

    }

    @Override
    public void updateDatabase(){

    }

    @Override
    public String getResultSet(){

    }
}