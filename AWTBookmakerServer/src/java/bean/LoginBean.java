/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import model.User;

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
    private final static String HOME_SITE = "../home.xhtml?faces-redirect=true";

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
        
        
        int a = 3;
        
        
        return HOME_SITE;	
    }

    public String register(){
        
        
        
        return LOGIN_SITE;
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
