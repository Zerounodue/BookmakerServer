
package model;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Philippe Lüthi and Elia Kocher
 */
public class User {
    private int id;
    private String name;
    private BigDecimal balance;
    private Role role;
    private boolean isAdmin = false;
    private String password;
    
    private List<Bet> bets;
    
    /**
     *
     * @param id id of the user
     * @param n name of the user
     * @param b balance of the user
     * @param r role of the user
     */
    public User(int id, String n, BigDecimal b, Role r){
        this.id = id;
        this.name = n;
        this.balance = b;
        this.role = r;
        // admin role id is 1, do not change it in the database
        this.isAdmin= (r.getId()==1);
    }
    
    /**
     *
     * @param id id of the user
     * @param n name of the user
     * @param b balance of the user
     * @param r role of the user
     * @param bets list of bets
     */
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

    /**
     *
     * @return true if user is admin
     */
    public boolean isAdmin() {
        return isAdmin;
    }

    /**
     *
     * @return the user password
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    
    
}
