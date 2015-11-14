/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.time.LocalTime;
import java.util.Date;

/**
 *
 * @author bizki
 */
public class Match {
    private int id;
    private Date date;
    private LocalTime time;
    private boolean finished;

    private Team homeTeam;
    private Team awayTeam;
    private Result result;
    
    
}
