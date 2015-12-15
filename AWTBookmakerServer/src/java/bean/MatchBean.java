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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import model.Match;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;
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

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean lbean;

    //websites
    private final static String HOME_SITE = "/home.xhtml?faces-redirect=true";
    private final static String BOOKMAKER_RESULTS_SITE = "/bookmaker/results.xhtml?faces-redirect=true";
    private final static String BOOKMAKER_MATCHES_SITE = "/bookmaker/matches.xhtml?faces-redirect=true";

    //components to display error messages
    private final static String TABLE_BOOKMAKER_RESULTS = "tbl_bResults";
    private final static String TABLE_BOOKMAKER_MATCHES = "tbl_bMatches";
    
    private final static String MESSAGES_BUNDLE = "messages";
    
    //sql queries
    private final static String SELECT_ALL_FROM_MATCHES_AND_TEAM_NAME
            = "SELECT m.id, m.homeTeamFK, m.awayTeamFK, m.time, m.finished, ht.name as homeTeamName, at.name as awayTeamName, "
                + "(SELECT totalGain from totalgainlossview WHERE mid = m.id) as totalGain, "
                + "(SELECT totalLoss from totalgainlossview WHERE mid = m.id) as totalLoss FROM matches m "
            + "INNER JOIN teams ht ON m.homeTeamFK=ht.id "
            + "INNER JOIN teams at ON m.awayTeamFK=at.id ";
    /*
    private final static String SELECT_ALL_FROM_MATCHES_AND_TEAM_NAME
            = "SELECT m.id, m.homeTeamFK, m.awayTeamFK, m.time, m.finished, ht.name as homeTeamName, at.name as awayTeamName, "
                + "(SELECT totalGain from totalgainlossview WHERE mid = m.id) as totalGain, "
                + "(SELECT totalLoss from totalgainlossview WHERE mid = m.id) as totalLoss FROM matches m "
            + "INNER JOIN teams ht ON m.homeTeamFK=ht.id "
            + "INNER JOIN teams at ON m.awayTeamFK=at.id ";
    */
   // private final static String SELECT_ALL_FROM_BETS
   //         = "SELECT b.id, b.amount, b.userFK, b.resultFK FROM bets b ";
    private final static String SELECT_ALL_FROM_BETS
            = "SELECT b.id, b.amount, b.userFK, b.resultFK, users.name AS userName, results.name AS resultName FROM bets b INNER JOIN users ON b.userFK = users.id INNER JOIN results ON b.resultFK = results.id ";
    private final static String SELECT_ALL_FROM_RESULTS_AND_AMOUNT_SUM
            = "SELECT r.id, r.name, r.oddNumerator, r.oddDenominator, r.occured, r.matchFK, m.finished, (SELECT sum(bets.amount) FROM bets INNER JOIN results ON bets.resultFK = results.id where results.id =r.id ) as amountSum FROM results r ";
    private final static String SELECT_ALL_FROM_TEAMS
            = "Select t.id, t.name from teams t ";
    private final static String UPDATE_USER_BALANCE_FOR_RESULT_ID =
            "UPDATE users u "
            //join table that contains total gain for all users who placed a bet for this result
            + "INNER JOIN "
            + "("
                + "Select DISTINCT "
                    + "("
                        + "SELECT SUM(ROUND(((res.oddNumerator / res.oddDenominator) * bet.amount),2)) as gain "
                        + "FROM results res "
                        + "INNER JOIN bets bet ON res.id = bet.resultFK "
                        + "INNER JOIN users usr ON bet.userFK = usr.id "
                        + "WHERE res.id = r.id AND usr.id = u.id "
                    + ") AS gain, "
                    + "u.id as uID "
                + "FROM results r "
                + "INNER JOIN bets b ON r.id = b.resultFK "
                + "INNER JOIN users u ON b.userFK = u.id "
                + "WHERE r.id = ? "
            + ") userGain "
            + "SET u.balance = (u.balance + userGain.gain) "
            + "WHERE u.id = userGain.uID ";
    private final static String SELECT_COUNT_IF_MATCH_STARTED_BY_RESULT_ID = "SELECT COUNT(m.id) as started "
            + "FROM results r "
            + "INNER JOIN matches m ON r.matchFK = m.id " 
            + "WHERE r.id = ? "
            + "AND m.time < ? ";
    private final static String SELECT_COUNT_IF_MATCH_STARTED_BY_MATCH_ID = "SELECT COUNT(m.id) as started "
            + "FROM matches m "
            + "WHERE m.id = ? "
            + "AND m.time < ? ";
    
    //forms
    private final static String FORM_NEW_MATCH = "frm_newMatch";
    private final static String FORM_NEW_RESULT = "frm_newResult";

    private int matchId;
    private int resultId;

    //values for newMatch form
    private int newMatchHomeTeamId;
    private int newMatchAwayTeamId;
    private Timestamp newMatchTime;
    private Date newMatchDate;
    private int newMatchHours =12;
    private int newMatchMinutes=30;
    private Result newMatchResult = new Result();
    private List<Result> newMatchResults;

    private List<Result> results = null;
    private List<Bet> bets = null;
    private List<Match> matches = null;
    private List<Team> teams = null;

    private final DecimalFormat df = new DecimalFormat("#.00");
    
    //used to display the title in the matches website
    private String bookmakerMatchesTitle = MessageHelper.getMessage(MESSAGES_BUNDLE, "matchUpcomingMatches");
    
    

    private enum bookmakerMatchesEnum { ALL, UPCOMING, STARTED, FINISHED}
    private bookmakerMatchesEnum bookmakerMatchesId;
    
    

    /**
     * Returns all matches, including the teams for a match
     * @return List of Match objects or null
     */
    public List<Match> getAllMatches() {
        matches = null;

        //check if user is bookmaker
        if (lbean.getUser() == null || !lbean.getUser().isAdmin()) {
            return null;
        }

        //get connection to DB
        Connection conn = DBHelper.getDBConnection();
        if (conn != null) {
            ResultSet rs = null;
            PreparedStatement s = null;

            try {
                String sql = SELECT_ALL_FROM_MATCHES_AND_TEAM_NAME
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
                        double totalGain = rs.getDouble("totalGain");
                        double totalLoss = rs.getDouble("totalLoss");
                        
                        //result will be null by upcoming matches -> set id to 0
                        Match m = new Match(id, ts, new Team(htId, htName), new Team(atId, atName), 0, finished);
                        List<Result> matchResults =getResultsWithTotalOddsByMatchId(id);
                        m.setTotalGain(matchResults.stream().mapToDouble(r -> r.getTotalGain()).sum());
                        m.setTotalLoss(matchResults.stream().mapToDouble(r -> r.getTotalLoss()).sum());
                        getMatches().add(m);
                    }
                }

            } catch (SQLException e) {
                matches = null;
                Logger.getLogger(MatchBean.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                DBHelper.closeConnection(rs, s, conn);
            }
        }
        //set title for matches website for bookmaker
        bookmakerMatchesTitle = MessageHelper.getMessage(MESSAGES_BUNDLE, "matchAllMatches");
        bookmakerMatchesId=bookmakerMatchesEnum.ALL;
        return getMatches();
    }

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
                        double totalGain = rs.getDouble("totalGain");
                        double totalLoss = rs.getDouble("totalLoss");
                        //result will be null by upcoming matches -> set id to 0
                        Match m = new Match(id, ts, new Team(htId, htName), new Team(atId, atName), 0, finished);
                        m.setTotalGain(totalGain);
                        m.setTotalLoss(totalLoss);
                        getMatches().add(m);
                    }
                }

            } catch (SQLException e) {
                matches = null;
                Logger.getLogger(MatchBean.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                DBHelper.closeConnection(rs, s, conn);
            }
        }
        //set title for matches website for bookmaker
        bookmakerMatchesTitle = MessageHelper.getMessage(MESSAGES_BUNDLE, "matchUpcomingMatches");
        bookmakerMatchesId=bookmakerMatchesEnum.UPCOMING;
        return getMatches();
    }

    /**
     * Gets all matches where finished is set to 0
     * @return List or Match object or null
     */
    public List<Match> getMatchStartedButNotFinished() {
        matches = null;
        //get connection to DB
        Connection conn = DBHelper.getDBConnection();
        if (conn != null) {
            ResultSet rs = null;
            PreparedStatement s = null;
            
            try {
                Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
                String sql = SELECT_ALL_FROM_MATCHES_AND_TEAM_NAME
                        + "WHERE m.finished = 0 AND m.time < ? "
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
                        double totalGain = rs.getDouble("totalGain");
                        double totalLoss = rs.getDouble("totalLoss");
                        //result will be null by upcoming matches
                        Match m = new Match(id, ts, new Team(htId, htName), new Team(atId, atName), 0, finished);
                        m.setTotalGain(totalGain);
                        m.setTotalLoss(totalLoss);
                        getMatches().add(m);
                    }

                }

            } catch (SQLException e) {
                matches = null;
            } finally {
                DBHelper.closeConnection(rs, s, conn);
            }

        }
        //set title for matches website for bookmaker
        bookmakerMatchesTitle = MessageHelper.getMessage(MESSAGES_BUNDLE, "matchStartedButNotFinished");
        bookmakerMatchesId=bookmakerMatchesEnum.STARTED;
        return getMatches();
    }

    /**
     * Sets the result by a given result id.
     * Will set the match to finished = 1
     * Will set the result to occured = 1 
     * Will update all the user's balance who placed a bet on this result
     * @param id id of the result to set
     * @return String Website to redirect to or null
     * if null is returned, an error message is displayed on the website
     */
    public String setResultForMatchByResultId(int id) {
        //check if id smaller 1 
        if (id < 1) {
            return HOME_SITE;
        }

        //check if user is admin
        if (lbean.getUser() == null || !lbean.getUser().isAdmin()) {
            return HOME_SITE;
        }

        Connection conn = DBHelper.getDBConnection();
        if (conn != null) {
            
            int res;
            PreparedStatement s = null;
            ResultSet rs = null;

            try {
                //TODO check if match already started...
                String sql = SELECT_COUNT_IF_MATCH_STARTED_BY_RESULT_ID;
                Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
                
                s = conn.prepareStatement(sql);
                s.setInt(1, id);
                s.setTimestamp(2, currentTimestamp);
                rs = s.executeQuery();
                
                int started = 0;
                
                if (rs != null) {
                    while(rs.next()){
                        started = rs.getInt("started");
                    }
                }
                
                if(started < 1){
                    MessageHelper.addMessageToComponent(TABLE_BOOKMAKER_RESULTS, MESSAGES_BUNDLE, "resultMatchDidNotYetStart", FacesMessage.SEVERITY_WARN);
                    return null;
                }                
                
                //will change more than one table
                conn.setAutoCommit(false);

                //-----update match
                sql = "UPDATE matches m "
                        + "INNER JOIN results r ON m.id=r.matchFK "
                        + "SET m.finished = 1 "
                        + "WHERE r.id = ?";

                s = conn.prepareStatement(sql);
                s.setInt(1, id);
                res = s.executeUpdate();
                
                if (res < 1) {
                    conn.rollback();
                    MessageHelper.addMessageToComponent(TABLE_BOOKMAKER_RESULTS, MESSAGES_BUNDLE, "resultErrDB", FacesMessage.SEVERITY_ERROR);
                    return null;
                }
                
                //-----update result                
                sql = "UPDATE results r SET r.occured = 1 WHERE r.id = ?";
                       
                s = conn.prepareStatement(sql);
                s.setInt(1, id);
                res = s.executeUpdate();
                
                if (res < 1) {
                    conn.rollback();
                    MessageHelper.addMessageToComponent(TABLE_BOOKMAKER_RESULTS, MESSAGES_BUNDLE, "resultErrDB", FacesMessage.SEVERITY_ERROR);
                    return null;
                }
                
                //-----update users                
                sql = UPDATE_USER_BALANCE_FOR_RESULT_ID;
                
                s = conn.prepareStatement(sql);
                s.setInt(1, id);
                res = s.executeUpdate();
                
                if (res < 1) {
                    conn.rollback();
                    MessageHelper.addMessageToComponent(TABLE_BOOKMAKER_RESULTS, MESSAGES_BUNDLE, "resultErrDB", FacesMessage.SEVERITY_ERROR);
                    return null;
                }
                
                //everything ok
                conn.commit();
            } catch (SQLException e) {
                MessageHelper.addMessageToComponent(TABLE_BOOKMAKER_RESULTS, MESSAGES_BUNDLE, "resultErrDB", FacesMessage.SEVERITY_ERROR);
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    Logger.getLogger(MatchBean.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
                return null;
            } finally {
                DBHelper.closeConnection(rs, s, conn);
            }

            //everything was ok, display the same page again
            return BOOKMAKER_RESULTS_SITE + "&matchId=" + matchId;

        } else {
            MessageHelper.addMessageToComponent(TABLE_BOOKMAKER_RESULTS, MESSAGES_BUNDLE, "resultErrDB", FacesMessage.SEVERITY_ERROR);
            return null;
        }

    }

    /**
     * Gets all bets placed on the specified result
     * @param id id for which the bets should be loaded
     * @return List of Bet objects or null
     */
    public List<Bet> getBetsByResultId(int id) {
        bets = null;

        Connection conn = DBHelper.getDBConnection();

        if (conn != null) {
            ResultSet rs = null;
            PreparedStatement s = null;

            try {
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
                        String userName = rs.getString("userName");
                        String resultName = rs.getString("resultName");

                        Bet b = new Bet(betId, amount, userId, resultFK, userName, resultName);
                        getBets().add(b);
                    }
                }

            } catch (SQLException e) {
                Logger.getLogger(MatchBean.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                DBHelper.closeConnection(rs, s, conn);
            }
        }

        return getBets();
    }

    /**
     * calculates the total gain for the list of bets currently used
     * @return double summed gain
     */
    public double getTotalAmountOfBets(){
        if(bets == null) return 0;
        //sums userGain of all bets in list
        return bets.stream().mapToDouble(b -> b.getAmount()).sum();
    }
    
    /**
     * Gets all results for the specified match id, including the total gain and loss
     * @param id id of the match for which the results should be loaded
     * @return List of Result objects
     */
    public List<Result> getResultsWithTotalOddsByMatchId(int id) {
        results = null;
        
        Connection conn = DBHelper.getDBConnection();

        if (conn != null) {
            ResultSet rs = null;
            PreparedStatement s = null;
            try {
                //Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
                String sql = SELECT_ALL_FROM_RESULTS_AND_AMOUNT_SUM
                        + "INNER JOIN matches m ON r.matchFK = m.id "
                        //if user is logged in get his bets too
                        //+ "INNER JOIN bets b ON r.id=b.resultFK"
                        //make sure only matches that are not already finished will be displayed
                        + "WHERE r.matchFK = ?";// AND m.time > ?";

                s = conn.prepareStatement(sql);
                s.setInt(1, id);
                //s.setTimestamp(2, currentTimestamp);
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
                        double amountSum = rs.getDouble("amountSum");
                        boolean finished= rs.getBoolean("finished");

                        Result r = new Result(rId, name, oddN, oddD, occured, mId);
                        r.setFinished(finished);
                        //if the match is not finished, the total gain and loss cant be
                        //calculated so its displayed the maximum loss and gani
                        if(!finished){
                            r.setTotalGain(amountSum);
                            r.setTotalLoss((amountSum*oddN )/oddD);
                        }
                        else{//the match is finisched, chech if the result is occoured
                            if(!occured){//bookmaker wins
                                r.setTotalGain(amountSum);
                                r.setTotalLoss(0);
                            }
                            else{//gamble wins
                                r.setTotalGain(0);
                                r.setTotalLoss((amountSum*oddN )/oddD);
                            }
                        }  
                        getResults().add(r);
                    }
                }
            } catch (SQLException e) {
                Logger.getLogger(MatchBean.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                DBHelper.closeConnection(rs, s, conn);
            }
        }
        return getResults();
    }
    /**
     * checks if the current list of results contains a result that occured
     *
     * @return true if a result occured, false otherwise
     */
    public boolean anyResultOccured() {
        //not sure that to do...
        if (results == null) {
            return false;
        }
        boolean a = results.stream().anyMatch(r -> r.isOccured());
        return results.stream().anyMatch(r -> r.isOccured());
    }


    /**
     * selects all teams from the database and returns a list of Team objects
     * will only query the teams once, then store them in a variable and return
     * the variable
     *
     * @return List<Team> a list of Team objects from the database
     */
    public List<Team> getAllTeams() {
        //load teams only once
        if (teams != null) {
            return teams;
        }

        Connection conn = DBHelper.getDBConnection();

        if (conn != null) {

            ResultSet rs = null;
            PreparedStatement s = null;

            try {
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
            } catch (SQLException e) {
                Logger.getLogger(MatchBean.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                DBHelper.closeConnection(rs, s, conn);
            }
        }
        return teams;
    }

    /**
     * adds a new match
     *
     * @return String to redirect to or null (error message will be set)
     */
    public String newMatch() {
        //parameters can be found in variables starting with newMatch

        //if user is not logged in, redirect to home
        if (lbean.getUser() == null) {
            return "../home.xhtml?faces-redirect=true";
        }

        //check if fields are empty
        if (newMatchDate == null) {
            MessageHelper.addMessageToComponent(FORM_NEW_MATCH, MESSAGES_BUNDLE, "newMatchFieldsNotEmpty", FacesMessage.SEVERITY_WARN);
            return null;
        }

        //must not select the same teams
        if (newMatchHomeTeamId == newMatchAwayTeamId) {
            MessageHelper.addMessageToComponent(FORM_NEW_MATCH, MESSAGES_BUNDLE, "newMatchHomeAwayTeamMustBeDifferent", FacesMessage.SEVERITY_WARN);
            return null;
        }

        //store user's date
        Calendar c = Calendar.getInstance();
        c.setTime(newMatchDate);
        c.set(Calendar.HOUR_OF_DAY, newMatchHours);
        c.set(Calendar.MINUTE, newMatchMinutes);
        c.set(Calendar.SECOND, 0);
        newMatchTime = new Timestamp(c.getTimeInMillis());

        //get a time that is 1 day in the future
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, 1);
        Timestamp currentTimestamp = new Timestamp(now.getTime().getTime());

        //check if selected date is at least 1 day in the future
        if (newMatchTime.compareTo(currentTimestamp) < 0) {
            MessageHelper.addMessageToComponent(FORM_NEW_MATCH, MESSAGES_BUNDLE, "newMatchDateMustBeOneDayAhead", FacesMessage.SEVERITY_WARN);
            return null;
        }

        //check if at least 1 result has been added
        if (newMatchResults == null || newMatchResults.isEmpty()) {
            MessageHelper.addMessageToComponent(FORM_NEW_MATCH, MESSAGES_BUNDLE, "newMatchNeedToAddAtLeastOneResult", FacesMessage.SEVERITY_WARN);
            return null;
        }

        //presumably correct data from correct user, try to change balance
        Connection conn = DBHelper.getDBConnection();
        if (conn != null) {

            PreparedStatement s = null;
            int res;

            try {
                //will update more than one table -> transaction needed
                conn.setAutoCommit(false);

                //first create a new match
                String sql = "INSERT INTO matches "
                        + "(time, homeTeamFK, awayTeamFK) "
                        + "VALUES(?, ?, ?)";

                s = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                s.setTimestamp(1, newMatchTime);
                s.setInt(2, newMatchHomeTeamId);
                s.setInt(3, newMatchAwayTeamId);
                res = s.executeUpdate();

                if (res < 1) {
                    conn.rollback();
                    MessageHelper.addMessageToComponent(FORM_NEW_MATCH, MESSAGES_BUNDLE, "newMatchErrDB", FacesMessage.SEVERITY_ERROR);
                    return null;
                }

                //store the id of the created match
                int nMatchId;

                //try to get the id of the created match
                try (ResultSet generatedKeys = s.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        nMatchId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Creating failed, no ID obtained.");
                    }
                } catch (SQLException e) {
                    conn.rollback();
                    MessageHelper.addMessageToComponent(FORM_NEW_MATCH, MESSAGES_BUNDLE, "newMatchErrDB", FacesMessage.SEVERITY_ERROR);
                    return null;
                }

                sql = "INSERT INTO results "
                        + "(name, oddNumerator, oddDenominator, matchFK) "
                        + "VALUES(?, ?, ?, ?)";

                //insert each result
                for (Result r : newMatchResults) {
                    s = conn.prepareStatement(sql);
                    s.setString(1, r.getName());
                    s.setDouble(2, r.getOddNumerator());
                    s.setDouble(3, r.getOddDenominator());
                    s.setInt(4, nMatchId);

                    res = s.executeUpdate();

                    if (res < 1) {
                        conn.rollback();
                        MessageHelper.addMessageToComponent(FORM_NEW_MATCH, MESSAGES_BUNDLE, "newMatchErrDB", FacesMessage.SEVERITY_ERROR);
                        return null;
                    }
                }

                //everything ok -> commit
                conn.commit();

            } catch (SQLException e) {
                MessageHelper.addMessageToComponent(FORM_NEW_MATCH, MESSAGES_BUNDLE, "newMatchErrDB", FacesMessage.SEVERITY_ERROR);
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    Logger.getLogger(MatchBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            } finally {
                DBHelper.closeConnection(s, conn);
            }

            //everything was fine, reset form
            resetVariables();
            return HOME_SITE;
        } else {
            MessageHelper.addMessageToComponent(FORM_NEW_MATCH, MESSAGES_BUNDLE, "newMatchErrDB", FacesMessage.SEVERITY_ERROR);
            return null;
        }
    }

    /**
     * adds a new result object to the new match uses the parameters of the
     * newMatchResult object method is only called via ajax
     */
        public void addResultToNewMatch() {
        if (newMatchResults == null) {
            newMatchResults = new ArrayList<>();
        }

        //check if fields are empty
        if (newMatchResult.getName().length() == 0) {
            MessageHelper.addMessageToComponent(FORM_NEW_RESULT, MESSAGES_BUNDLE, "newMatchFieldsNotEmpty", FacesMessage.SEVERITY_WARN);
            return;
        }

        //check if odds are greater than zero           
        if (!(newMatchResult.getOddNumerator() > 0) || !(newMatchResult.getOddDenominator() > 0)) {
            MessageHelper.addMessageToComponent(FORM_NEW_RESULT, MESSAGES_BUNDLE, "newMatchNumbersGreaterThanZero", FacesMessage.SEVERITY_WARN);
            return;
        }

        //check if oddn and oddd are the same number, necessary?
        Result nr = new Result(0, newMatchResult.getName(), newMatchResult.getOddNumerator(), newMatchResult.getOddDenominator(), false, 0);

        String newOddN = df.format(newMatchResult.getOddNumerator());
        String newOddD = df.format(newMatchResult.getOddDenominator());

        //check if same the item has already been added
        if (newMatchResults.stream().filter(r -> r.getName().equals(nr.getName()) && df.format(r.getOddNumerator()).equals(newOddN) && df.format(r.getOddDenominator()).equals(newOddD)).findFirst().isPresent()) {
            MessageHelper.addMessageToComponent(FORM_NEW_RESULT, MESSAGES_BUNDLE, "newMatchSameResultAlreadyAdded", FacesMessage.SEVERITY_WARN);
            return;
        }

        nr.setOddNumerator(Double.parseDouble(newOddN));
        nr.setOddDenominator(Double.parseDouble(newOddD));

        newMatchResults.add(nr);
    }
        
       
    /**
     * Gets all matches where finished is set to 1
     * @return List or Match object or null
     */
    public List<Match> getFinishedMatches() {
        matches = null;
        //get connection to DB
        Connection conn = DBHelper.getDBConnection();
        if (conn != null) {
            ResultSet rs = null;
            PreparedStatement s = null;
            
            try {
               
                String sql = SELECT_ALL_FROM_MATCHES_AND_TEAM_NAME
                        + "WHERE m.finished = 1 "
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
                        double totalGain = rs.getDouble("totalGain");
                        double totalLoss = rs.getDouble("totalLoss");
                        //result will be null by upcoming matches
                        Match m = new Match(id, ts, new Team(htId, htName), new Team(atId, atName), 0, finished);
                        List<Result> matchResults =getResultsWithTotalOddsByMatchId(id);
                        m.setTotalGain(matchResults.stream().mapToDouble(r -> r.getTotalGain()).sum());
                        m.setTotalLoss(matchResults.stream().mapToDouble(r -> r.getTotalLoss()).sum());
                        getMatches().add(m);
                    }
                }
            } catch (SQLException e) {
                matches = null;
            } finally {
                DBHelper.closeConnection(rs, s, conn);
            }

        }
        //set title for matches website for bookmaker
        bookmakerMatchesTitle = MessageHelper.getMessage(MESSAGES_BUNDLE, "matchFinishedMatches");
        bookmakerMatchesId=bookmakerMatchesEnum.FINISHED;
        return getMatches();
    }
    
    
    
    /**
     * Sets the match to finished by a given match id.
     * Will set the match to finished = 1 
     * @param id id of the match to set
     * @return String Website to redirect to or null
     * if null is returned, an error message is displayed on the website
     */
    public String setMatchFinishedByMatchId(int id) {
        //check if id smaller 1 
        if (id < 1) {
            return HOME_SITE;
        }

        //check if user is admin
        if (lbean.getUser() == null || !lbean.getUser().isAdmin()) {
            return HOME_SITE;
        }

        Connection conn = DBHelper.getDBConnection();
        if (conn != null) {
            
            int res;
            PreparedStatement s = null;
            ResultSet rs = null;

            try {
                //TODO check if match already started...
                String sql = SELECT_COUNT_IF_MATCH_STARTED_BY_MATCH_ID;
                Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
                
                s = conn.prepareStatement(sql);
                s.setInt(1, id);
                s.setTimestamp(2, currentTimestamp);
                rs = s.executeQuery();
                
                int started = 0;
                
                if (rs != null) {
                    while(rs.next()){
                        started = rs.getInt("started");
                    }
                }
                
                if(started < 1){
                    MessageHelper.addMessageToComponent(TABLE_BOOKMAKER_MATCHES, MESSAGES_BUNDLE, "matchMatchDidNotYetStart", FacesMessage.SEVERITY_WARN);
                    return null;
                }

                //-----update match
                sql = "UPDATE matches m "
                        + "SET m.finished = 1 "
                        + "WHERE m.id = ?";

                s = conn.prepareStatement(sql);
                s.setInt(1, id);
                res = s.executeUpdate();
                
                if (res < 1) {
                    
                    MessageHelper.addMessageToComponent(TABLE_BOOKMAKER_MATCHES, MESSAGES_BUNDLE, "matchErrDB", FacesMessage.SEVERITY_ERROR);
                    return null;
                }
                
            } catch (SQLException e) {
                MessageHelper.addMessageToComponent(TABLE_BOOKMAKER_MATCHES, MESSAGES_BUNDLE, "matchErrDB", FacesMessage.SEVERITY_ERROR);
               
                return null;
            } finally {
                DBHelper.closeConnection(rs, s, conn);
            }
            // depending on witch site the user currenty is it will 
            // redirect (reload) the same site, so the datas are
            // reloaded from the DB and the table is updated 
            switch (bookmakerMatchesId){
                case ALL:
                    getAllMatches();
                    break;                 
                case STARTED:
                    getMatchStartedButNotFinished();
                    break;
                default:
                    throw new AssertionError(bookmakerMatchesId.name());
            }

            //everything was ok, display the same page again
            return BOOKMAKER_MATCHES_SITE;

        } else {
            MessageHelper.addMessageToComponent(TABLE_BOOKMAKER_MATCHES, MESSAGES_BUNDLE, "matchErrDB", FacesMessage.SEVERITY_ERROR);
            return null;
        }

    }
    

    /**
     * removes a match item from the list which is displayed on the client this
     * method is only called via ajax
     *
     * @param r Result to remove
     */
    public void removeResultFromNewMatch(Result r) {
        if (newMatchResults != null) {
            newMatchResults.remove(r);
        }
    }

    /**
     * resets all variables to their default values
     */
    private void resetVariables() {
        newMatchResults = null;
        newMatchResult = null;
        newMatchAwayTeamId = newMatchHomeTeamId = newMatchHours = newMatchMinutes = 0;
        newMatchDate = null;
        newMatchTime = null;
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

    /**
     * @return the newMatchHours
     */
    public int getNewMatchHours() {
        return newMatchHours;
    }

    /**
     * @param newMatchHours the newMatchHours to set
     */
    public void setNewMatchHours(int newMatchHours) {
        this.newMatchHours = newMatchHours;
    }

    /**
     * @return the newMatchMinutes
     */
    public int getNewMatchMinutes() {
        return newMatchMinutes;
    }

    /**
     * @param newMatchMinutes the newMatchMinutes to set
     */
    public void setNewMatchMinutes(int newMatchMinutes) {
        this.newMatchMinutes = newMatchMinutes;
    }

    /**
     * @return the newMatchDate
     */
    public Date getNewMatchDate() {
        return newMatchDate;
    }

    /**
     * @param newMatchDate the newMatchDate to set
     */
    public void setNewMatchDate(Date newMatchDate) {
        this.newMatchDate = newMatchDate;
    }

    /**
     * @return the newMatchResults
     */
    public List<Result> getNewMatchResults() {
        return newMatchResults;
    }

    /**
     * @param newMatchResults the newMatchResults to set
     */
    public void setNewMatchResults(List<Result> newMatchResults) {
        this.newMatchResults = newMatchResults;
    }

    /**
     * @return the newMatchResult
     */
    public Result getNewMatchResult() {
        return newMatchResult;
    }

    /**
     * @param newMatchResult the newMatchResult to set
     */
    public void setNewMatchResult(Result newMatchResult) {
        this.newMatchResult = newMatchResult;
    }
    

    /**
     * calculates the possible gain if a given result happen
     * called from results.xhtml
     * 
     * @param matchID the match id to get the gains
     * @param resultID the result id to get the gains
     * @return  the gain for this result
     */
    /*
    public double getBetAmount(int matchID, int resultID) {
        
        double gain = 0.0;

        //get connection to DB
        Connection conn = DBHelper.getDBConnection();
        if (conn != null) {
            ResultSet rs = null;
            PreparedStatement s = null;

            try {
               
                String sql = "SELECT amount) as gain FROM gainview WHERE matchID = "+matchID+" AND resultID = "+resultID;

                s = conn.prepareStatement(sql);
                rs = s.executeQuery();
                if (rs != null) {
                    while (rs.next()) {
                         gain = rs.getDouble("gain"); 
                    }
                }
            } catch (SQLException e) {
                Logger.getLogger(MatchBean.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                DBHelper.closeConnection(rs, s, conn);
            }
        }
        return gain;
    }
    /**
     * calculates the possible loss if a given result happen
     * called from results.xhtml
     * 
     * @param matchID the match id to get the gains
     * @param resultID the result id to get the gains
     * @return  the loss for this result
     */
    /*
    public double getLoss(int matchID, int resultID) {
        double loss = 0.0;
        
        //get connection to DB
        Connection conn = DBHelper.getDBConnection();
        if (conn != null) {
            ResultSet rs = null;
            PreparedStatement s = null;

            try {
                String sql = "SELECT SUM(((amount * oddNumerator) / oddDenominator)) AS loss FROM lossview WHERE matchID = "+matchID+" AND resultID = "+resultID;

                s = conn.prepareStatement(sql);
                rs = s.executeQuery();
                if (rs != null) {
                    while (rs.next()) {
                         loss = rs.getDouble("loss");   
                    }
                }

            } catch (SQLException e) {
                matches = null;
                Logger.getLogger(MatchBean.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                DBHelper.closeConnection(rs, s, conn);
            }
        }
        return loss;
    }
    */
    
    
    /**
     * calculates the total gain for the list of match currently used
     * @return double summed gain
     */
    public double getTotalGainForMatches(){
        if(matches == null) return 0;
        //sums userGain of all bets in list
        return matches.stream().mapToDouble(m -> m.getTotalGain()).sum();
    }
    /**
     * calculates the total loss for the list of match currently used
     * @return double summed loss
     */
    public double getTotalLossForMatches(){
        if(matches == null) return 0;
        //sums userGain of all bets in list
        return matches.stream().mapToDouble(m -> m.getTotalLoss()).sum();
    }
    
    /**
     * calculates the total gain for the list of match currently used
     * @return double summed gain
     */
    public double getTotalGainForResults(){
        if(results == null) return 0;
        //sums userGain of all bets in list
        return results.stream().mapToDouble(r -> r.getTotalGain()).sum();
    }
    /**
     * calculates the total loss for the list of match currently used
     * @return double summed loss
     */
    public double getTotalLossForResults(){
        if(results == null) return 0;
        //sums userGain of all bets in list
        return results.stream().mapToDouble(r -> r.getTotalLoss()).sum();
    }

    /**
     * @return the bookmakerMatchesTitle
     */
    public String getBookmakerMatchesTitle() {
        return bookmakerMatchesTitle;
    }
}
