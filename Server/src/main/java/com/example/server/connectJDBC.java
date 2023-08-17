package com.example.server;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connectJDBC {
    private String hostName = "localhost:3306";
    private String db_name = "caseStudy";
    private String username = "root";
    private String password = "password";
    private String connectionURL = "jdbc:mysql://" + hostName + "/" + db_name;
    public Connection connection(){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(connectionURL,username, password);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }
}
