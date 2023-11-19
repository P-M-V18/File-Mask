package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    static Connection connection = null;
    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/file_hider?allowPublicKeyRetrieval=true&useSSL=false","root", "Patimohit*18");

        }
        catch(ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Connected Successfully");
        return connection;
    }
    public static void closeConnection() {
        if(connection != null) {
            try {
                connection.close();
            }
            catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
