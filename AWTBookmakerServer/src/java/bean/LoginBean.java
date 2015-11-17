/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import model.User;
import util.DBHelper;
import util.MessageHelper;

/**
 *
 * @author bizki
 */
@ManagedBean
@SessionScoped
public class LoginBean {
    private User user;
    private String locale;
    private String userName;
    private String password;
    
    private final static String LOGIN_SITE = "login.xhtml?faces-redirect=true";
    private final static String REGISTER_SITE = "register.xhtml?faces-redirect=true";
    private final static String HOME_SITE = "/user/home.xhtml?faces-redirect=true";

    public void setLocale(String locale){
	this.locale = locale;
    }
    
    public String getLocale(){
	if(locale == null){
            FacesContext context = FacesContext.getCurrentInstance();
            return  context.getViewRoot().getLocale().toString();
	}
	return locale;
    }
    
    
    public String login(){
        
        
        
        
        
        return HOME_SITE;	
    }

    public String register(){
        Connection conn = DBHelper.getDBConnection();
        if(conn != null){
            //check if username already exists
            if(userNameAlreadyExists(conn)){
                MessageHelper.addMessageToComponent("frm_Login:message", "Messages", "registerUserNameExists", FacesMessage.SEVERITY_WARN);
                return LOGIN_SITE;
            }
            
            int res;
            PreparedStatement s = null;
            
            try {
                String sql = "INSERT INTO users (name, password) "
                           + "VALUES (?, ?)";
                s = conn.prepareStatement(sql);
                
                s.setString(1, userName);
                s.setString(2, password);
                
                res = s.executeUpdate(sql);
                //error
                if (res < 0) {
                    MessageHelper.addMessageToComponent("frm_Login:message", "Messages", "registerErrCreateUser", FacesMessage.SEVERITY_ERROR);
                    return REGISTER_SITE;
                }
            } catch (SQLException e) {
                Logger.getLogger(MatchBean.class.getName()).log(Level.SEVERE, null, e);
            }finally{
                DBHelper.closeConnection(s, conn);
            }

            return LOGIN_SITE;
        }
        
        return REGISTER_SITE;
    }
    
    private boolean userNameAlreadyExists(Connection conn){
        boolean exists = true;
        
        ResultSet rs = null;
        PreparedStatement s = null;
        
        String sql = "SELECT count(name) as number FROM users "
                   + "WHERE name = ?";
        
        try {
            s = conn.prepareStatement(sql);
            s.setString(1, userName);
            
            rs = s.executeQuery();
            
            if (rs != null) {
                rs.next();
                int num = rs.getInt("number");
                exists = num > 0;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(LoginBean.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            DBHelper.closeConnection(rs, s);
        }
	
        return exists;
    }
    
    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    
    
    
}
