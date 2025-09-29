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

    public static Connection getConnection() {
        try {
            // JDBC 4.0 trở lên không cần Class.forName nữa
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Kết nối SQL Server thành công!");
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ Lỗi kết nối SQL Server!");
            e.printStackTrace();
            return null;
        }
    }
}
