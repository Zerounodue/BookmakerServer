/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import model.Match;

/**
 *
 * @author bizki
 */

@ManagedBean
@SessionScoped
public class MatchBean {
    
    
    
    
    
    public List<Match> getUpcomingMatches(){
        List<Match> matches = new ArrayList<>();
        
        //query to get all matches
        //"select m.id, m.homeTeam, m.awayTeam, m.date, m.time from Matches m
        //inner join Teams ht on m.homeTeam = ht.id
        //inner join Teams at on m.awayTeam = at.id
        //order by m.date desc
        //where m.time > [timestamp now]
        
        
        
        
        return matches;
    }
    
    
    
}
