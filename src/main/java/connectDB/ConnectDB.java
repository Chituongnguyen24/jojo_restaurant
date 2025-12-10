package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    
    private static final String URL = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=PTUD-JOJO-Restaurant;"
            + "encrypt=false;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASSWORD = "sapassword";

    private static ConnectDB instance = new ConnectDB();
    
    private ConnectDB() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy SQL Server Driver.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        Connection newConnection = null;
        try {
            newConnection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối SQL Server!");
            e.printStackTrace();
        }
        return newConnection;
    }

    public static ConnectDB getInstance() {
        return instance;
    }
    
}