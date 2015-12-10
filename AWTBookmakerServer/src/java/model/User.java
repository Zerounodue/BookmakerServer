/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author bizki
 */
public class User {
    private int id;
    private String name;
    private BigDecimal balance;
    private Role role;
    private boolean isAdmin = false;
    private String password;
    
    private List<Bet> bets;
    
    
    public User(int id, String n, BigDecimal b, Role r){
        this.id = id;
        this.name = n;
        this.balance = b;
        this.role = r;
        // admin role id is 1, do not change it in the database
        this.isAdmin= (r.getId()==1);
    }
    
    public User(int id, String n, BigDecimal b, Role r, List<Bet> bets){
        this.id = id;
        this.name = n;
        this.balance = b;
        this.role = r;
        this.bets = bets;
        // admin role id is 1, do not change it in the database
        this.isAdmin= (r.getId()==1);
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
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * @return the role
     */
    public Role getRole() {
        return role;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    
    
}
