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
    
    
}
