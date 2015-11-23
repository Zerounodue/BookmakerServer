/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import util.DBHelper;
import util.MessageHelper;

/**
 *
 * @author bizki
 */
@ManagedBean
@SessionScoped
public class UserBean {
    
    @ManagedProperty(value="#{loginBean}")
    private LoginBean lbean;
    
    private final static String HOME_SITE = "/home.xhtml?faces-redirect=true";
    private final static String FORM_CHARGE_BALANCE = "frm_chargeBalance";
    private final static String MESSAGES_BUNDLE = "messages";
    private final static BigDecimal MIN_AMOUNT = new BigDecimal(0.05).setScale(2, BigDecimal.ROUND_HALF_UP);
    private final static String VALID_CRETID_CARD_NUMBER = "1234 1234 1234 1234";
    private final static String VALID_VALIDATION_CODE = "234";
    
    //form values
    private BigDecimal amount;
    private String creditCardNumber;
    private String validationCode;
    
    public String chargeBalance(){        
        //check if user is logged in, else redirect to home
        if(lbean.getUser() == null){
            //redirect to home
            return HOME_SITE;
        }
        
        //get userId, will be needed later
        int userId = lbean.getUser().getId();
        
        //check if fields are empty
        if(amount == null || creditCardNumber == null || creditCardNumber.length() == 0 || validationCode == null || validationCode.length() == 0){
            MessageHelper.addMessageToComponent(FORM_CHARGE_BALANCE, MESSAGES_BUNDLE, "chargeBalanceFieldsEmpty", FacesMessage.SEVERITY_WARN);
            return null;
        }
        //check if min amount BigDecimal compareTo -1 -> second value is bigger
        amount.setScale(2, BigDecimal.ROUND_HALF_UP);
        if(amount.compareTo(MIN_AMOUNT) < 0){
            MessageHelper.addMessageToComponent(FORM_CHARGE_BALANCE, MESSAGES_BUNDLE, "chargeBalanceMinAmount", FacesMessage.SEVERITY_WARN);
            return null;
        }
        
        //check if credit card and validation number
        if(!creditCardNumber.equalsIgnoreCase(VALID_CRETID_CARD_NUMBER) || !validationCode.equalsIgnoreCase(VALID_VALIDATION_CODE)){
            MessageHelper.addMessageToComponent(FORM_CHARGE_BALANCE, MESSAGES_BUNDLE, "chargeBalanceCreditCardNumberOrValidationCodeWrong", FacesMessage.SEVERITY_WARN);
            return null;
        }
        
        //presumably correct data from correct user, try to change balance
        Connection conn = DBHelper.getDBConnection();
        if (conn != null) {
            
            PreparedStatement s = null;
            int res;
            
            try{
                String sql = "UPDATE users "
                        + "SET balance = balance + ? "
                        + "WHERE id = ?";
                
                s = conn.prepareStatement(sql);
                s.setBigDecimal(1, amount);
                s.setInt(2, userId);
                
                res = s.executeUpdate();
                
                //error
                if (res < 0) {
                    MessageHelper.addMessageToComponent(FORM_CHARGE_BALANCE, MESSAGES_BUNDLE, "chargeBalanceErrChargeBalance", FacesMessage.SEVERITY_WARN);
                    return null;
                }
            }catch(SQLException e){
                MessageHelper.addMessageToComponent(FORM_CHARGE_BALANCE, MESSAGES_BUNDLE, "chargeBalanceErrDB", FacesMessage.SEVERITY_WARN);
                    return null;
            }finally{
                DBHelper.closeConnection(s, conn);
            }
            
            //everything went ok, update user balance
            lbean.getUser().setBalance(lbean.getUser().getBalance().add(amount.setScale(2, BigDecimal.ROUND_HALF_UP)));
            //reset variables
            resetVariables();
            return HOME_SITE;
        }else{
            MessageHelper.addMessageToComponent(FORM_CHARGE_BALANCE, MESSAGES_BUNDLE, "chargeBalanceErrDB", FacesMessage.SEVERITY_ERROR);
            return null;
        }
    }

    private void resetVariables(){
        amount = null;
        creditCardNumber = validationCode = null;
    }
    
    /**
     * @return the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * @return the creditCardNumber
     */
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    /**
     * @param creditCardNumber the creditCardNumber to set
     */
    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    /**
     * @return the validationCode
     */
    public String getValidationCode() {
        return validationCode;
    }

    /**
     * @param validationCode the validationCode to set
     */
    public void setValidationCode(String validationCode) {
        this.validationCode = validationCode;
    }
    
    /**
     * @return the lbean
     */
    public LoginBean getLbean() {
        return lbean;
    }

    /**
     * @param lbean the lbean to set
     */
    public void setLbean(LoginBean lbean) {
        this.lbean = lbean;
    }
    
}
