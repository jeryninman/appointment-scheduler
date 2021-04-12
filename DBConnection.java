/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.sql.*;


/**
 *
 * @author Jeryn
 */
public class DBConnection {
    private static final String databaseName="U05tUZ";
    private static final String databaseURL="jdbc:mysql://52.206.157.109/"+databaseName;
    private static final String username="U05tUZ";
    private static final String password="53688605884"; 
    private static final String driver="com.mysql.jdbc.Driver";
    public static Connection connection;
    public static void makeConnection() throws ClassNotFoundException, SQLException, Exception{
        
        Class.forName(driver);
        connection = (Connection) DriverManager.getConnection(databaseURL, username,password);
        
    }
    
    public static void closeConnection() throws ClassNotFoundException, SQLException, Exception{
        connection.close();
    }
}
