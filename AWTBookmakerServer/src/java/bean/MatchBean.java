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
import java.sql.Timestamp;
import model.Team;

/**
 *
 * @author bizki
 */
@ManagedBean
@SessionScoped
public class MatchBean {

    public List<Match> getUpcomingMatches() {
        List<Match> matches = null;

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
        //TODO find better way
        if(conn == null) return null;
        
        try {

            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());

            PreparedStatement s;

            String sql = "SELECT m.id, m.homeTeamFK, m.awayTeamFK, m.time FROM matches m "
                    + "INNER JOIN teams ht ON m.homeTeamFK = ht.id "
                    + "INNER JOIN teams at ON m.awayTeamFK = at.id "
                    + "WHERE m.time > '" + currentTimestamp.toString() + "'"
                    + "ORDER BY m.time asc ";

            //String sql = "INSERT INTO `user` (`userID` ,`username` ,`password` )VALUES (NULL , ?, ?)";
            s = conn.prepareStatement(sql);
            ResultSet rs = s.executeQuery();
            if (rs != null) {
                matches = new ArrayList<>();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    int htId = rs.getInt("homeTeamFK");
                    int atId = rs.getInt("awayTeamFK");
                    Timestamp ts = rs.getTimestamp("time");
                    //result will be null by upcoming matches
                    Match m = new Match(id, ts, new Team(htId), new Team(atId), null);

                    matches.add(m);
                }
                rs.close();
            }

            s.close();

        } catch (SQLException e) {
            System.out.println(e);
        }

        return matches;
    }

}
