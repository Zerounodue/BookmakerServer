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
public class Team {
    private int id;
    //could be deleted... maybe
    private String name;
    
    
    public Team(int id){
        this.id = id;
        //TODO get name from ressources or do in matches.xhtml
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    
}
