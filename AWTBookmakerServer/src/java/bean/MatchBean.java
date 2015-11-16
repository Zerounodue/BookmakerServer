/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.sql.Connection;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Team;
import util.DBHelper;

/**
 *
 * @author bizki
 */
@ManagedBean
@SessionScoped
public class MatchBean {

    private final static String SELECT_ALL_FROM_MATCHES
            = "SELECT m.id, m.homeTeamFK, m.awayTeamFK, m.time FROM matches m "
            + "INNER JOIN teams ht ON m.homeTeamFK = ht.id "
            + "INNER JOIN teams at ON m.awayTeamFK = at.id ";

    /**
     * Gets all matches with a start time > than now
     * @return list of Match objects or null
     */
    public List<Match> getUpcomingMatches() {
        List<Match> matches = null;
        //get connection to DB
        Connection conn = DBHelper.getDBConnection();
        //error connecting to db
        if (conn != null) {
            ResultSet rs = null;
            PreparedStatement s = null;

            try {
                Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());

                String sql = SELECT_ALL_FROM_MATCHES
                        + "WHERE m.time > '" + currentTimestamp.toString() + "'"
                        + "ORDER BY m.time asc ";

                s = conn.prepareStatement(sql);
                rs = s.executeQuery();
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
                }

            } catch (SQLException e) {
                Logger.getLogger(MatchBean.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                DBHelper.closeConnection(rs, s, conn);
            }
        }

        return matches;
    }

}
