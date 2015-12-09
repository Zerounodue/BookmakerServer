/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author bizki
 */
public class MessageHelper {

    public static void addMessageToComponent(String componentId, String msgBundle, String msgKey, FacesMessage.Severity severity) {
        //http://www.javabeat.net/jsf-2-message-messages/

        // Bring the error message using the Faces Context
        FacesContext context = FacesContext.getCurrentInstance();
        String errorMsg = msgKey;
        String errorDetail;
        
        try{
            errorMsg = context.getApplication().getResourceBundle(context, msgBundle).getString(msgKey);
            errorDetail = errorMsg;
        }catch(Exception e){
            //do nothing
            errorDetail = "Could not load error details in your language. Please try again and don't input mistakes.";
        }

        // Add the message into context for a specific component
        context.addMessage(componentId, new FacesMessage(severity, errorMsg, errorDetail));
    }

    public static String getMessage(String msgBundle, String msgKey){
        String m = "";
        
        FacesContext context = FacesContext.getCurrentInstance();
        
        try{
            m = context.getApplication().getResourceBundle(context, msgBundle).getString(msgKey);
        }catch(Exception e){
            //do nothing
        }
        
        return m;
    }
    
}
