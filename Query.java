/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import static DAO.DBConnection.connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Jeryn
 */
public class Query {
    private static String query;
    private static Statement toExecute;
    private static ResultSet result;
    
    
    public static void makeQuery(String sqlStatement) throws SQLException, Exception{
        
        query = sqlStatement;
        try{
            toExecute = connection.createStatement();
            //determine type of query
            if(query.toLowerCase().startsWith("select")){ //execute select query
                
                result = toExecute.executeQuery(sqlStatement);
            }
            if (query.toLowerCase().startsWith("delete")||query.toLowerCase().startsWith("insert")||query.toLowerCase().startsWith("update")){  //perform update
                
                toExecute.executeUpdate(sqlStatement);
            }
        }catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        
    }
    
    public static ResultSet getResult(){
        return result;
    }

    
}
