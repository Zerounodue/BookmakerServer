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
public class User {
    private int id;
    private String name;
    private double balance;
    private Role role;
    
    private List<Bet> bets;
    
    public User(int id, String n, double b, Role r){
        this.id = id;
        this.name = n;
        this.balance = b;
        this.role = r;
    }
    
    public User(int id, String n, double b, Role r, List<Bet> bets){
        this.id = id;
        this.name = n;
        this.balance = b;
        this.role = r;
        this.bets = bets;
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * @return the role
     */
    public Role getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(Role role) {
        this.role = role;
    }
    
}
