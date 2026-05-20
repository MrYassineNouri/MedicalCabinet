package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:medical.db";

    public static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(URL);
            System.out.println("Database connected successfully");
            return conn;
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC Driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database Connection Error");
            e.printStackTrace();
        }
        return null;
    }
}