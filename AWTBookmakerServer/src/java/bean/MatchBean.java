/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import model.Match;

/**
 *
 * @author bizki
 */
@ManagedBean
@SessionScoped
public class MatchBean {

    public List<Match> getUpcomingMatches() {
        List<Match> matches = new ArrayList<>();

        //query to get all matches
        //"select m.id, m.homeTeam, m.awayTeam, m.date, m.time from Matches m
        //inner join Teams ht on m.homeTeam = ht.id
        //inner join Teams at on m.awayTeam = at.id
        //order by m.date desc
        //where m.time > [timestamp now]
        
        //put some place that is more useful
        Connection conn = null;
        try {
            String userName = "bookmaker";
            String password = "b00kM4k3r";
            String url = "jdbc:mysql://localhost/bookmaker";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, userName, password);
            System.out.println("Database connection established");
        } catch (Exception e) {
            System.err.println("Cannot connect to database server" + e.toString());
        }

        
        
        try {

            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());

            PreparedStatement s;

            String sql = "SELECT `m.id`,` m.homeTeam`, `m.awayTeam`, `m.time`, FROM 'matches' m"
                    + "INNER JOIN 'teams' ht ON m.homeTeam = ht.id"
                    + "INNER JOIN 'teams' at ON m.awayTeam = at.id"
                    + "ORDER BY m.time desc"
                    + "WHERE m.time > ";

            //String sql = "INSERT INTO `user` (`userID` ,`username` ,`password` )VALUES (NULL , ?, ?)";
            s = conn.prepareStatement(sql);
            s.executeQuery();
            
            ResultSet rs = s.getResultSet();
            
            s.close();
            
            
        } catch (SQLException e) {
            System.out.println(e);
        }

        return matches;
    }

}
