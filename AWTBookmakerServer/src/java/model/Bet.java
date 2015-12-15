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
    private String userName;
    private int resultId;
    private String resultName;
    private double userGain;
    
    private Result result;
    
    /**
     * constructor used to when displaying bets for a user
     * @param id id of bet
     * @param amount amount for bet
     * @param userId id of user who placed this bet
     * @param r result object to which this bet belongs
     * @param gain how much money the user won with this bet
     */
    public Bet(int id, double amount, int userId, Result r, double gain){
        this.id = id;
        this.amount = amount;
        this.userId = userId;
        this.result  = r;
        this.userGain = gain;        
    }

    public Bet(int id, double amount, int userId, int resultId, String userName, String resultName){
        this.id = id;
        this.amount = amount;
        this.userId = userId;
        this.resultId = resultId;
        this.userName = userName;
        this.resultName = resultName;
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

    /**
     * @return the userGain
     */
    public double getUserGain() {
        return userGain;
    }
    
    /**
     * @return the result name
     */
    public String getResultName() {
        return resultName;
    }
    /**
     * @return the name of the user
     */
    public String getUserName() {
        return userName;
    }
    
    
    
}
