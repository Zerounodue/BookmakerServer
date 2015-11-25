/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.sql.Timestamp;

/**
 *
 * @author bizki
 */
public class Match {
    private int id;
    private Timestamp time;
    private boolean finished;
    private int resultId;
    
    private Team homeTeam;
    private Team awayTeam;
    private Result result;
    
    /**
     * default constructor, used by "newMatch.xhtml"
     */
    public Match(){
        
    }
    
    public Match(int id, Timestamp t, Team ht, Team at, int r, boolean finished){
        this.id = id;
        this.time = t;
        this.homeTeam = ht;
        this.awayTeam = at;
        this.resultId = r;
        this.finished = finished;
    }
    
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
     * @return the resultId
     */
    public int getResultId() {
        return resultId;
    }
    
}
