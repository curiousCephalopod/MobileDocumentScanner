package com.untitled.mobiledocumentscanner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by J on 24-Apr-17.
 */

public class DataSource {
    private static final String url = "jdbc:mysql://10.0.0.2:3306/documentscanner";
    private static final String username = "docscanner";
    private static final String password = "password";

    /**
     * @throws java.sql.SQLException * Gets a connection to the database.
     *
     * @return the database connection
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
