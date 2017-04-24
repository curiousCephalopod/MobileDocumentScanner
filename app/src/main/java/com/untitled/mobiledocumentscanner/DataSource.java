package com.untitled.mobiledocumentscanner;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by J on 24-Apr-17.
 */

public class DataSource {
    private static final String url = "jdbc:mysql://localhost:3306/documentscanner";
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
