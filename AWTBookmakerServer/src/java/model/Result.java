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
    
    //ids might be enough
    //private Match match;
    //private List<Bet> bets;
    
    
    public Result(int id, String name, double oddN, double oddD, boolean occ, int mId){
        this.id = id;
        this.name = name;
        this.oddNumerator = oddN;
        this.oddDenominator = oddD;
        this.occured = occ;
        this.matchId = mId;
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

    
    
    
    
    
    
    
}
