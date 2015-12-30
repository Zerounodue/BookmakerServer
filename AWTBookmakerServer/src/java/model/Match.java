
package model;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 *
 * @author Philippe Lüthi and Elia Kocher
 */
public class Match {
    private int id;
    private Timestamp time;
    private boolean finished;
    private int resultId;
    
    private Team homeTeam;
    private Team awayTeam;
    private Result result;
    
    private double totalGain;

   
    private double totalLoss;
    
    /**
     * default constructor, used by "newMatch.xhtml"
     */
    public Match(){
        
    }
    /**
     * constructor used when displaying a match
     * @param id id of match
     * @param t timestamp when match starts
     * @param ht Team homeTeam
     * @param at Team awayTeam
     * @param r resultId
     * @param finished finished flag
     */
    public Match(int id, Timestamp t, Team ht, Team at, int r, boolean finished){
        this.id = id;
        this.time = t;
        this.homeTeam = ht;
        this.awayTeam = at;
        this.resultId = r;
        this.finished = finished;
    }
    /**
     * constructor used when displaying a match
     * @param id id of match
     * @param t timestamp when match starts
     * @param ht Team homeTeam
     * @param at Team awayTeam
     * @param r Result
     * @param finished finished flag
     */
    public Match(int id, Timestamp t, Team ht, Team at, Result r, boolean finished){
        this.id = id;
        this.time = t;
        this.homeTeam = ht;
        this.awayTeam = at;
        this.result = r;
        this.finished = finished;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the time
     */
    public Timestamp getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(Timestamp time) {
        this.time = time;
    }

    /**
     * @return the homeTeam
     */
    public Team getHomeTeam() {
        return homeTeam;
    }

    /**
     * @param homeTeam the homeTeam to set
     */
    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    /**
     * @return the awayTeam
     */
    public Team getAwayTeam() {
        return awayTeam;
    }

    /**
     * @param awayTeam the awayTeam to set
     */
    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    /**
     * @return the result
     */
    public Result getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * @return the finished
     */
    public boolean isFinished() {
        return finished;
    }
    /**
     * @return if match is not finished and time is past
     */
    public boolean startedAndFinished() {
        Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
        return ((!finished) && (currentTimestamp.after(time)));
    }

    /**
     * @return the resultId
     */
    public int getResultId() {
        return resultId;
    }
     /**
     * @return the totslGain
     */
    public double getTotalGain() {
        return totalGain;
    }
     /**
     * @return the totalLoss
     */
    public double getTotalLoss() {
        return totalLoss;
    }
    /**
     * @param totalGain the totalGain to set
     */
    public void setTotalGain(double totalGain) {
        this.totalGain = totalGain;
    }
    /**
     * @param totalLoss the totalLoss to set
     */
    public void setTotalLoss(double totalLoss) {
        this.totalLoss = totalLoss;
    }
    
}
