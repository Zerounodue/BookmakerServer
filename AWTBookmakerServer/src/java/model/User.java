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
    
}
