package com.maksymfedosov.Util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBInitHelper {

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection != null)
            return connection;
        else {
            try {
                String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
                String SERVER_PATH = "localhost:3306";
                String DB_NAME = "programmers";
                String DB_LOGIN = "root";
                String DB_PASSWORD = "root";

                Class.forName(DB_DRIVER);

                String connectionUrl = "jdbc:mysql://" + SERVER_PATH + "/" + DB_NAME +
                            "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
                connection = DriverManager.getConnection(connectionUrl, DB_LOGIN, DB_PASSWORD);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return connection;
        }

    }

}
