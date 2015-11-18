/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.faces.application.Application;
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

        //Application a = context.getApplication();
        //ResourceBundle b = ResourceBundle.getBundle(msgBundle, context.getViewRoot().getLocale());
        //ResourceBundle b = a.getResourceBundle(context, context.getViewRoot().getLocale());
        //String c = b.getString(msgKey);
        //String errorMessage = context.getApplication().getResourceBundle(context, msgBundle).getString(msgKey);
        String errorMessage = "__name already taken";

        // Add View Faces Message
        FacesMessage message = new FacesMessage(severity, errorMessage, errorMessage);

        // Add the message into context for a specific component
        context.addMessage(componentId, message);
    }

}
