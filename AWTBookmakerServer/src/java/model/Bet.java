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
    
}
