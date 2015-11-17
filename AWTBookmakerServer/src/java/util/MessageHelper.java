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

        // Bring the error message using the Faces Context
        FacesContext context = FacesContext.getCurrentInstance();

        String errorMessage = context.getApplication().getResourceBundle(context, msgBundle).getString(msgKey);

        // Add View Faces Message
        FacesMessage message = new FacesMessage(severity, errorMessage, errorMessage);

        // Add the message into context for a specific component
        context.addMessage(componentId, message);
    }

}
