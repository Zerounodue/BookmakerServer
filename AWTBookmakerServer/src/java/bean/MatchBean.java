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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import model.Bet;
import model.Result;
import model.Team;
import util.DBHelper;
import util.MessageHelper;

/**
 *
 * @author bizki
 */
@ManagedBean
@SessionScoped
public class MatchBean {

    @ManagedProperty(value="#{loginBean}")
    private LoginBean lbean;
    
    //sql queries
    private final static String SELECT_ALL_FROM_MATCHES_AND_TEAM_NAME
            = "SELECT m.id, m.homeTeamFK, m.awayTeamFK, m.time, m.finished, ht.name as homeTeamName, at.name as awayTeamName FROM matches m ";
    private final static String SELECT_ALL_FROM_BETS
            = "SELECT b.id, b.amount, b.userFK, b.resultFK FROM bets b ";
    private final static String SELECT_ALL_FROM_RESULTS
            = "SELECT r.id, r.name, r.oddNumerator, r.oddDenominator, r.occured, r.matchFK FROM results r ";
    private final static String SELECT_ALL_FROM_TEAMS
            = "Select t.id, t.name from teams t ";
    
    //forms
    private final static String FORM_NEW_MATCH = "frm_newMatch";
    
    private final static String MESSAGES_BUNDLE = "messages";
    
    private int matchId;
    private int resultId;
    private String homeTeam;
    private String awayTeam;
    private Match match;
    
    //values for newMatch form
    private int newMatchHomeTeamId;
    private int newMatchAwayTeamId;
    private Timestamp newMatchTime;
    
    private List<Result> results = null;
    private List<Bet> bets = null;
    private List<Match> matches = null;
    private List<Team> teams = null;
    
    
    /**
     * Gets all matches with a start time > than now
     * @return list of Match objects or null
     */
    public List<Match> getUpcomingMatches() {
        matches = null;
        //get connection to DB
        Connection conn = DBHelper.getDBConnection();
        if (conn != null) {
            ResultSet rs = null;
            PreparedStatement s = null;

            try {
                Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());

                String sql = SELECT_ALL_FROM_MATCHES_AND_TEAM_NAME
                        + "INNER JOIN teams ht ON m.homeTeamFK=ht.id "
                        + "INNER JOIN teams at ON m.awayTeamFK=at.id "
                        + "WHERE m.time > ? AND m.finished = 0 "
                        + "ORDER BY m.time asc ";

                s = conn.prepareStatement(sql);
                s.setTimestamp(1, currentTimestamp);
                rs = s.executeQuery();
                if (rs != null) {
                    matches = new ArrayList<>();

                    while (rs.next()) {
                        int id = rs.getInt("id");
                        int htId = rs.getInt("homeTeamFK");
                        String htName = rs.getString("homeTeamName");
                        int atId = rs.getInt("awayTeamFK");
                        String atName = rs.getString("awayTeamName");
                        Timestamp ts = rs.getTimestamp("time");
                        boolean finished = rs.getBoolean("finished");
                        //result will be null by upcoming matches -> set id to 0
                        Match m = new Match(id, ts, new Team(htId, htName), new Team(atId, atName), 0, finished);
                        getMatches().add(m);
                    }
                }

            } catch (SQLException e) {
                Logger.getLogger(MatchBean.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                DBHelper.closeConnection(rs, s, conn);
            }
        }

        return getMatches();
    }

    public List<Match> getMatchesWithoutResult(){
        matches = null;
        //get connection to DB
        Connection conn = DBHelper.getDBConnection();
        if (conn != null) {
            ResultSet rs = null;
            PreparedStatement s = null;
            
            try {
                String sql = SELECT_ALL_FROM_MATCHES_AND_TEAM_NAME
                        + "INNER JOIN teams ht ON m.homeTeamFK=ht.id "
                        + "INNER JOIN teams at ON m.awayTeamFK=at.id "
                        + "WHERE m.finished = 0 "
                        + "ORDER BY m.time asc ";

                s = conn.prepareStatement(sql);
                rs = s.executeQuery();
                if (rs != null) {
                    matches = new ArrayList<>();
                    
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        int htId = rs.getInt("homeTeamFK");
                        String htName = rs.getString("homeTeamName");
                        int atId = rs.getInt("awayTeamFK");
                        String atName = rs.getString("awayTeamName");
                        Timestamp ts = rs.getTimestamp("time");
                        boolean finished = rs.getBoolean("finished");
                        //result will be null by upcoming matches
                        Match m = new Match(id, ts, new Team(htId, htName), new Team(atId, atName), 0, finished);
                        getMatches().add(m);
                    }
                    
                }

            } catch (SQLException e) {
                
            }finally{
                DBHelper.closeConnection(rs, s, conn);
            }
            
        }
        
        
        return getMatches();
    }
    
    public String setResultForMatchByResultId(int id){
        
        
        
        return null;
    }
    
    public List<Bet> getBetsByResultId(int id){
        bets = null;
        
        Connection conn = DBHelper.getDBConnection();
        
        if (conn != null) {
            ResultSet rs = null;
            PreparedStatement s = null;
        
            try{
                String sql = SELECT_ALL_FROM_BETS
                        + "WHERE b.resultFK = ? "
                        + "ORDER BY b.userFK";
                
                s = conn.prepareStatement(sql);
                s.setInt(1, id);
                rs = s.executeQuery();
                if (rs != null) {
                    bets = new ArrayList<>();

                    while (rs.next()) {
                        int betId = rs.getInt("id");
                        double amount = rs.getDouble("amount");
                        int userId = rs.getInt("userFK");
                        int resultFK = rs.getInt("resultFK");
                        
                        Bet b = new Bet(betId, amount, userId, resultFK);
                        getBets().add(b);
                    }
                }
                
            }catch(SQLException e){
                Logger.getLogger(MatchBean.class.getName()).log(Level.SEVERE, null, e);
            }finally{
                DBHelper.closeConnection(rs, s, conn);
            }
        }

        return getBets();
    }
    
    
    
    public String betForResultByResultId(int resultId){
        
        
        
        
        
        return null;
    }
    
    public List<Result> getResultsByMatchId(int id){
        results = null;
        
        Connection conn = DBHelper.getDBConnection();
        
        if (conn != null) {
            ResultSet rs = null;
            PreparedStatement s = null;
            
            try{
                Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
                
                String sql = SELECT_ALL_FROM_RESULTS
                        + "INNER JOIN matches m ON r.matchFK = m.id "
                        //if user is logged in get his bets too
                        //+ "INNER JOIN bets b ON r.id=b.resultFK"
                        //make sure only matches that are not already finished will be displayed
                        + "WHERE r.matchFK = ? AND m.time > ?";
                
                s = conn.prepareStatement(sql);
                s.setInt(1, id);
                s.setTimestamp(2, currentTimestamp);
                rs = s.executeQuery();
                if (rs != null) {
                    results = new ArrayList<>();

                    while (rs.next()) {
                        int rId = rs.getInt("id");
                        String name = rs.getString("name");
                        double oddN = rs.getDouble("oddNumerator");
                        double oddD = rs.getDouble("oddDenominator");
                        boolean occured = rs.getBoolean("occured");
                        int mId = rs.getInt("matchFK");
                        
                        Result r = new Result(rId, name, oddN, oddD, occured, mId);
                        getResults().add(r);
                    }
                }
            }catch(SQLException e){
                Logger.getLogger(MatchBean.class.getName()).log(Level.SEVERE, null, e);
            }finally{
                DBHelper.closeConnection(rs, s, conn);
            }
        }

        return getResults();
    }
    
    /**
     * selects all teams from the database and returns a list of Team objects
     * will only query the teams once, then store them in a variable and return the variable
     * @return List<Team> a list of Team objects from the database
     */
    public List<Team> getAllTeams(){
        //load teams only once
        if(teams != null) return teams;
        
        Connection conn = DBHelper.getDBConnection();
        
        if(conn != null){
            
            ResultSet rs = null;
            PreparedStatement s = null;
            
            try{
                String sql = SELECT_ALL_FROM_TEAMS;
                s = conn.prepareStatement(sql);
                
                rs = s.executeQuery();
                if (rs != null) {
                    teams = new ArrayList<>();
                    
                    while (rs.next()) {
                        int tId = rs.getInt("id");
                        String tName = rs.getString("name");
                        
                        Team t = new Team(tId, tName);
                        
                        teams.add(t);
                    }
                }
            }catch(SQLException e){
                Logger.getLogger(MatchBean.class.getName()).log(Level.SEVERE, null, e);
            }finally{
                DBHelper.closeConnection(rs, s, conn);
            } 
        }

        return teams;
    }
    
    /**
     * adds a new match
     * @return String to redirect to or null (error message will be set)
     */
    public String newMatch(){
        //parameters can be found in variables starting with newMatch

        //if user is not logged in, redirect to home
        if(lbean.getUser() == null) return "../home.xhtml?faces-redirect=true";
        
        
        //must not select the same teams
        if(newMatchHomeTeamId == newMatchAwayTeamId){
            MessageHelper.addMessageToComponent(FORM_NEW_MATCH, MESSAGES_BUNDLE, "newMatchHomeAwayTeamMustBeDifferent", FacesMessage.SEVERITY_WARN);
            return null;
        }
        
        
        return null;
    }
    
    /**
     * @return the matchId
     */
    public int getMatchId() {
        return matchId;
    }

    /**
     * @return the resultId
     */
    public int getResultId() {
        return resultId;
    }
    
    /**
     * @param matchId the matchId to set
     */
    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    /**
     * @return the results
     */
    public List<Result> getResults() {
        return results;
    }

    /**
     * @return the bets
     */
    public List<Bet> getBets() {
        return bets;
    }

    /**
     * @return the matches
     */
    public List<Match> getMatches() {
        return matches;
    }

    /**
     * @param resultId the resultId to set
     */
    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    /**
     * @return the newMatchHomeTeamId
     */
    public int getNewMatchHomeTeamId() {
        return newMatchHomeTeamId;
    }

    /**
     * @param newMatchHomeTeamId the newMatchHomeTeamId to set
     */
    public void setNewMatchHomeTeamId(int newMatchHomeTeamId) {
        this.newMatchHomeTeamId = newMatchHomeTeamId;
    }

    /**
     * @return the newMatchAwayTeamId
     */
    public int getNewMatchAwayTeamId() {
        return newMatchAwayTeamId;
    }

    /**
     * @param newMatchAwayTeamId the newMatchAwayTeamId to set
     */
    public void setNewMatchAwayTeamId(int newMatchAwayTeamId) {
        this.newMatchAwayTeamId = newMatchAwayTeamId;
    }

    /**
     * @return the newMatchTime
     */
    public Timestamp getNewMatchTime() {
        return newMatchTime;
    }

    /**
     * @param newMatchTime the newMatchTime to set
     */
    public void setNewMatchTime(Timestamp newMatchTime) {
        this.newMatchTime = newMatchTime;
    }

    /**
     * @return the lbean
     */
    public LoginBean getLbean() {
        return lbean;
    }

    /**
     * @param lbean the lbean to set
     */
    public void setLbean(LoginBean lbean) {
        this.lbean = lbean;
    }

}
