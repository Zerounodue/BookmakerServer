/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author bizki
 */
public class DBHelper {

    private static final String URL = "jdbc:mysql://localhost/bookmaker";
    private static final String DRIVER_PATH = "com.mysql.jdbc.Driver";
    //TODO make more secure
    private static final String USER_NAME = "bookmaker";
    private static final String PASSWORD = "b00kM4k3r";
    
    /**
     * Establishes a connection to a sql DB
     * @return A sql DB connection or null
     */
    public static Connection getDBConnection(){
        Connection conn = null;
        try {
            Class.forName(DRIVER_PATH).newInstance();
            conn = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
        return conn;
    }
    
    /**
     * Tries to close the db connection
     * @param c Connection to close
     */
    public static void closeConnection(Connection c){
        try { if (c != null) c.close(); } catch (Exception e) {}
    }
    
    /**
     * Tries to close the db connection
     * @param r Result set to close
     * @param s Statements to close
     */
    public static void closeConnection(ResultSet r, PreparedStatement s){
        try { if (r != null) r.close(); } catch (Exception e) {}
        try { if (s != null) s.close(); } catch (Exception e) {}
    }
    
    /**
     * Tries to close the db connection
     * @param s Statements to close
     * @param c Connection to close
     */
    public static void closeConnection(PreparedStatement s, Connection c){
        try { if (s != null) s.close(); } catch (Exception e) {}
        try { if (c != null) c.close(); } catch (Exception e) {}
    }
    
    /**
     * Tries to close the db connection
     * @param r Result set to close
     * @param s Statements to close
     * @param c Connection to close
     */
    public static void closeConnection(ResultSet r, PreparedStatement s, Connection c){
        try { if (r != null) r.close(); } catch (Exception e) {}
        try { if (s != null) s.close(); } catch (Exception e) {}
        try { if (c != null) c.close(); } catch (Exception e) {}
    }
    
    
    
    
}
