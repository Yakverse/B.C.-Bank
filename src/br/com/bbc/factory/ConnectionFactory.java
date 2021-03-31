package br.com.bbc.factory;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionFactory {

    private static final String USERNAME = "bbc";
    private static final String PASSWORD = "123";
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/bbc";

    public static Connection connectionToDB() throws Exception{
        Class.forName("com.mysql.jdbc.Driver");

        Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

        return connection;
    }

    public static void main(String[] args) throws Exception{
        Connection con = connectionToDB();

        if(con != null) con.close();
    }
}
