package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
	public static Connection con = null;
	private static ConnectDB instance = new ConnectDB();
	private static final String URL = "jdbc:sqlserver://localhost:1433;" + "databaseName=PTUD-JOJO-Restaurant;"
			+ "encrypt=false;trustServerCertificate=true;";
	private static final String USER = "sa";
	private static final String PASSWORD = "sapassword";

	public static Connection getConnection() {
		try {

			Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
			System.out.println("Kết nối SQL Server thành công!");
			return conn;
		} catch (SQLException e) {
			System.err.println("Lỗi kết nối SQL Server!");
			e.printStackTrace();
			return null;
		}
	}

	public static ConnectDB getInstance() {
		return instance;
	}

	public void disconect() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
