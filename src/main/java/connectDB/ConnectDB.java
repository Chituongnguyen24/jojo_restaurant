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

    // Vẫn giữ Singleton pattern cho đối tượng ConnectDB
    private static ConnectDB instance = new ConnectDB();
    
    // Constructor riêng tư: Khởi tạo Driver một lần
    private ConnectDB() {
        try {
            // Nạp Driver chỉ một lần khi khởi tạo instance
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