package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
    private static Connection con = null;
    private static final String URL = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=PTUD-JOJO-Restaurant;"
            + "encrypt=false;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASSWORD = "sapassword";

    private static ConnectDB instance = new ConnectDB();

 
    public static Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                con = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Kết nối SQL Server thành công!");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối SQL Server!");
            e.printStackTrace();
        }
        return con;
    }

    public static ConnectDB getInstance() {
        return instance;
    }

    public void disconnect() {
        if (con != null) {
            try {
                con.close();
                System.out.println("Đã ngắt kết nối SQL Server.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
