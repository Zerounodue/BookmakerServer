/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author bizki
 */
public class Bet {
    private int id;
    private double amount;
    //better use objects?
    private int userId;
    private int resultId;
    
    private Result result;
    
    /**
     * constructor used to when displaying bets
     * @param id id of bet
     * @param amount amount for bet
     * @param userId id of user who placed this bet
     * @param r result object to which this bet belongs
     */
    public Bet(int id, double amount, int userId, Result r){
        this.id = id;
        this.amount = amount;
        this.userId = userId;
        this.result  = r;
    }

    public Bet(int id, double amount, int userId, int resultId){
        this.id = id;
        this.amount = amount;
        this.userId = userId;
        this.resultId = resultId;
    }
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * @return the resultId
     */
    public int getResultId() {
        return resultId;
    }
    
    /**
     * @return the userId
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @return the result
     */
    public Result getResult() {
        return result;
    }
    
}
