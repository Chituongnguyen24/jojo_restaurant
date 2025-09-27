import java.sql.Connection;

import connectDB.ConnectDB;

public class Main {
    public static void main(String[] args) {
        Connection con = ConnectDB.getConnection();
        
    }
}
