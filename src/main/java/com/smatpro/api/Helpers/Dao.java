package com.smatpro.api.Helpers;

import java.sql.Connection;
import java.sql.DriverManager;

public class Dao {

    public static Connection connection() throws Exception {

        Connection con = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=SmatRentalPro;username=rentalad;password=1234;trustServerCertificate=true;encrypt=true");
        if (con != null) {
            System.out.println("connection success");
        } else {
            System.out.println("connection failed");
        }
        return con;
    }
}
