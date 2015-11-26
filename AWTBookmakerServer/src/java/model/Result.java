/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.List;

/**
 *
 * @author bizki
 */
public class Result {
    private int id;
    private String name;
    private double oddNumerator;
    private double oddDenominator;
    private boolean occured;
    private int matchId;
    //TODO implement
    private double totalGain;
    private double totalLoss;
    
    private Match match;
    //private List<Bet> bets;
    
    public Result(){
        
    }
    
    /**
     * Constructor 
     * @param id id of result
     * @param name name of result
     * @param oddN odd numerator of result
     * @param oddD odd denumerator of result
     * @param occ true if this particulaar result occured
     * @param mId id of match this result belongs to
     */
    public Result(int id, String name, double oddN, double oddD, boolean occ, int mId){
        this.id = id;
        this.name = name;
        this.oddNumerator = oddN;
        this.oddDenominator = oddD;
        this.occured = occ;
        this.matchId = mId;
    }

    /**
     * constructor used to display all matches
     * @param id id of result
     * @param name name of result
     * @param oddN odd numerator of result
     * @param oddD odd denumerator of result
     * @param occ true if this particulaar result occured
     * @param m match the result belongs to
     */
    public Result(int id, String name, double oddN, double oddD, boolean occ, Match m){
        this.id = id;
        this.name = name;
        this.oddNumerator = oddN;
        this.oddDenominator = oddD;
        this.occured = occ;
        this.match = m;
    }
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the occured
     */
    public boolean isOccured() {
        return occured;
    }

    /**
     * @param occured the occured to set
     */
    public void setOccured(boolean occured) {
        this.occured = occured;
    }
    
    /**
     * @return the oddNumerator
     */
    public double getOddNumerator() {
        return oddNumerator;
    }

    /**
     * @return the oddDenominator
     */
    public double getOddDenominator() {
        return oddDenominator;
    }

    /**
     * @return the matchId
     */
    public int getMatchId() {
        return matchId;
    }

    /**
     * @return the totalGain
     */
    public double getTotalGain() {
        return totalGain;
    }

    /**
     * @param totalGain the totalGain to set
     */
    public void setTotalGain(double totalGain) {
        this.totalGain = totalGain;
    }

    /**
     * @return the totalLoss
     */
    public double getTotalLoss() {
        return totalLoss;
    }

    /**
     * @param totalLoss the totalLoss to set
     */
    public void setTotalLoss(double totalLoss) {
        this.totalLoss = totalLoss;
    }

    /**
     * @return the match
     */
    public Match getMatch() {
        return match;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param oddNumerator the oddNumerator to set
     */
    public void setOddNumerator(double oddNumerator) {
        this.oddNumerator = oddNumerator;
    }

    /**
     * @param oddDenominator the oddDenominator to set
     */
    public void setOddDenominator(double oddDenominator) {
        this.oddDenominator = oddDenominator;
    }

    /**
     * @param matchId the matchId to set
     */
    public void setMatchId(int matchId) {
        this.matchId = matchId;
    }

    /**
     * @param match the match to set
     */
    public void setMatch(Match match) {
        this.match = match;
    }

    
    
    
    
    
    
    
}
