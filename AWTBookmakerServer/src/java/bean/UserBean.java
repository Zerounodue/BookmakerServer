/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import model.Bet;
import model.Match;
import model.Result;
import model.Team;
import util.DBHelper;
import util.MessageHelper;
import java.sql.Timestamp;

/**
 *
 * @author bizki
 */
@ManagedBean
@SessionScoped
public class UserBean {

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean lbean;

    //websites
    private final static String HOME_SITE = "/home.xhtml?faces-redirect=true";
    private final static String CHARGE_BALANCE_SITE = "/gambler/chargeBalance.xhtml?faces-redirect=true";
    private final static String BET_SITE = "/gambler/bet.xhtml?faces-redirect=true";

    //queries
    private final static String SELECT_BETS_WITH_RESULT_TEAMS_MATCH_AND_CALCULATED_WIN_LOSS = "SELECT b.id as betId, b.amount, b.userFK, b.resultFK, "
                        +   "r.name as rName, r.occured as rOccured, r.oddNumerator as rOddN, r.oddDenominator as rOddD, "
                        +   "ht.id as htId, ht.name as htName, at.id as atId, at.name as atName, "
                        +   "m.id as mId, m.time as mTime, m.finished as mFinished, "
                            //calculates the gain
                        +   "IF(r.occured = 1, ROUND(((r.oddNumerator / r.oddDenominator) * b.amount),2), 0) as gain "
                        + "FROM bets b "
                        + "INNER JOIN results r ON b.resultFK=r.id "
                        + "INNER JOIN matches m ON r.matchFK = m.id "
                        + "INNER JOIN teams ht ON m.homeTeamFK=ht.id "
                        + "INNER JOIN teams at ON m.awayTeamFK=at.id ";
    
    private final static String FORM_CHARGE_BALANCE = "frm_chargeBalance";
    private final static String FORM_BET = "frm_bet";
    private final static String MESSAGES_BUNDLE = "messages";
    private final static BigDecimal MIN_AMOUNT = new BigDecimal(0.05).setScale(2, BigDecimal.ROUND_HALF_UP);
    private final static String VALID_CRETID_CARD_NUMBER = "1234 1234 1234 1234";
    private final static String VALID_VALIDATION_CODE = "234";

    //form charge balance values
    private BigDecimal amount;
    private String creditCardNumber;
    private String validationCode;

    //form bet values
    private int resultId;
    private String resultName;
    private BigDecimal betAmount;

    private List<Bet> bets;

    //make sure redirect from bet to charge balance to bet workd
    private String betParams = null;
    //if redirected from bet to chargeBalance, show a message
    private boolean showNeedToChargeBalanceToBetMessage = false;

    /**
     * charges the user's balance
     * @return String Website to redirect to or null
     * if null is returned, an error message is displayed on the website
     */
    public String chargeBalance() {
        //check if user is logged in, else redirect to home
        if (lbean.getUser() == null) {
            //redirect to home
            return HOME_SITE;
        }

        //get userId, will be needed later
        int userId = lbean.getUser().getId();

        //check if fields are empty
        if (amount == null || creditCardNumber == null || creditCardNumber.length() == 0 || validationCode == null || validationCode.length() == 0) {
            MessageHelper.addMessageToComponent(FORM_CHARGE_BALANCE, MESSAGES_BUNDLE, "chargeBalanceFieldsEmpty", FacesMessage.SEVERITY_WARN);
            return null;
        }
        //check if min amount BigDecimal compareTo -1 -> second value is bigger
        amount.setScale(2, BigDecimal.ROUND_HALF_UP);
        if (amount.compareTo(MIN_AMOUNT) < 0) {
            MessageHelper.addMessageToComponent(FORM_CHARGE_BALANCE, MESSAGES_BUNDLE, "chargeBalanceMinAmount", FacesMessage.SEVERITY_WARN);
            return null;
        }

        //check if credit card and validation number match
        if (!creditCardNumber.equalsIgnoreCase(VALID_CRETID_CARD_NUMBER) || !validationCode.equalsIgnoreCase(VALID_VALIDATION_CODE)) {
            MessageHelper.addMessageToComponent(FORM_CHARGE_BALANCE, MESSAGES_BUNDLE, "chargeBalanceCreditCardNumberOrValidationCodeWrong", FacesMessage.SEVERITY_WARN);
            return null;
        }

        //presumably correct data from correct user, try to change balance
        Connection conn = DBHelper.getDBConnection();
        if (conn != null) {

            PreparedStatement s = null;
            int res;

            try {
                String sql = "UPDATE users "
                        + "SET balance = balance + ? "
                        + "WHERE id = ?";

                s = conn.prepareStatement(sql);
                s.setBigDecimal(1, amount);
                s.setInt(2, userId);

                res = s.executeUpdate();

                //error
                if (res < 1) {
                    MessageHelper.addMessageToComponent(FORM_CHARGE_BALANCE, MESSAGES_BUNDLE, "chargeBalanceErrChargeBalance", FacesMessage.SEVERITY_WARN);
                    return null;
                }
            } catch (SQLException e) {
                MessageHelper.addMessageToComponent(FORM_CHARGE_BALANCE, MESSAGES_BUNDLE, "chargeBalanceErrDB", FacesMessage.SEVERITY_WARN);
                return null;
            } finally {
                DBHelper.closeConnection(s, conn);
            }

            //everything went ok, update user balance
            lbean.getUser().setBalance(lbean.getUser().getBalance().add(amount.setScale(2, BigDecimal.ROUND_HALF_UP)));

            String path;
            //check if redirect to bet
            if (betParams != null) {
                path = BET_SITE + betParams;
            } else {
                path = HOME_SITE;
            }
            //reset variables
            resetVariables();
            return path;
        } else {
            MessageHelper.addMessageToComponent(FORM_CHARGE_BALANCE, MESSAGES_BUNDLE, "chargeBalanceErrDB", FacesMessage.SEVERITY_ERROR);
            return null;
        }
    }

    /**
     * lets the user place a bet on a result
     * @return String Website to redirect to or null
     * if null is returned, an error message is displayed on the website
     */
    public String bet() {
        //check if user is logged in
        //check if user is logged in, else redirect to home
        if (lbean.getUser() == null) {
            //redirect to home
            return HOME_SITE;
        }

        //get userId, will be needed later
        int userId = lbean.getUser().getId();

        //check if fields are empty
        if (betAmount == null) {
            MessageHelper.addMessageToComponent(FORM_BET, MESSAGES_BUNDLE, "betFieldsEmpty", FacesMessage.SEVERITY_WARN);
            return null;
        }
        //check if min amount is bigger. BigDecimal compareTo -1 -> second value is bigger
        betAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
        if (betAmount.compareTo(MIN_AMOUNT) < 0) {
            MessageHelper.addMessageToComponent(FORM_BET, MESSAGES_BUNDLE, "betMinAmount", FacesMessage.SEVERITY_WARN);
            return null;
        }

        //presumably correct data from correct user
        //check if there is enough money, send to charge balance otherwise
        BigDecimal newBalance = lbean.getUser().getBalance().subtract(betAmount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            //store parameters and make sure user gets redirected here
            betParams = "&resultId=" + resultId + "&resultName=" + resultName + "&betAmount=" + betAmount;
            //store message to display when user cannot bet because the balance is too low
            setShowNeedToChargeBalanceToBetMessage(true);
            return CHARGE_BALANCE_SITE + "&amount=" + newBalance.negate();
        }

        //everything seems to be ok, start betting
        Connection conn = DBHelper.getDBConnection();
        if (conn != null) {

            PreparedStatement s = null;

            try {
                //will change table bets and users, therefore make a transaction
                conn.setAutoCommit(false);

                int res;

                //create new bet
                String sql = "INSERT INTO bets (amount, userFK, resultFK) "
                        + "VALUES (?, ?, ?)";

                s = conn.prepareStatement(sql);
                s.setBigDecimal(1, betAmount);
                s.setInt(2, lbean.getUser().getId());
                //might not be correct id, taken from get params...
                s.setInt(3, resultId);

                res = s.executeUpdate();

                if (res < 1) {
                    conn.rollback();
                    MessageHelper.addMessageToComponent(FORM_BET, MESSAGES_BUNDLE, "betErrBet", FacesMessage.SEVERITY_ERROR);
                    return null;
                }

                //update user balance
                sql = "UPDATE users "
                        + "SET balance = ? "
                        + "WHERE id = ?";

                s = conn.prepareStatement(sql);
                s.setBigDecimal(1, newBalance);
                s.setInt(2, userId);

                res = s.executeUpdate();

                if (res < 1) {
                    conn.rollback();
                    MessageHelper.addMessageToComponent(FORM_BET, MESSAGES_BUNDLE, "betErrBet", FacesMessage.SEVERITY_ERROR);
                    return null;
                }

                conn.commit();
            } catch (SQLException e) {
                MessageHelper.addMessageToComponent(FORM_BET, MESSAGES_BUNDLE, "betErrBet", FacesMessage.SEVERITY_ERROR);
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    return null;
                }
                return null;
            } finally {
                DBHelper.closeConnection(s, conn);
            }

            //everything went ok, update user balance
            lbean.getUser().setBalance(newBalance);

            resetVariables();
            //TODO CHANGE to results of match
            return HOME_SITE;
        } else {
            MessageHelper.addMessageToComponent(FORM_BET, MESSAGES_BUNDLE, "betErrDB", FacesMessage.SEVERITY_ERROR);
            return null;
        }
    }

    /**
     * gets all bets for the currently logged in user
     * @return List of Bet objects or null
     */
    public List<Bet> getBetsForUser() {
        bets = null;
        //check if user is logged in, if not no results will be returned
        if (lbean.getUser() == null) {
            return null;
        }

        int userId = lbean.getUser().getId();

        //get connection to DB
        Connection conn = DBHelper.getDBConnection();
        if (conn != null) {
            ResultSet rs = null;
            PreparedStatement s = null;

            try {
                String sql = SELECT_BETS_WITH_RESULT_TEAMS_MATCH_AND_CALCULATED_WIN_LOSS
                        + "WHERE b.userFK = ? "
                        + "ORDER BY m.finished, r.occured ";
                
                s = conn.prepareStatement(sql);
                s.setInt(1, userId);
                rs = s.executeQuery();
                if (rs != null) {
                    bets = new ArrayList<>();

                    while (rs.next()) {
                        //bet
                        int betId = rs.getInt("betId");
                        double betA = rs.getDouble("amount");
                        int uId = rs.getInt("userFK");
                        int rId = rs.getInt("resultFK");
                        double gain = rs.getDouble("gain");
                        //result
                        String rName = rs.getString("rName");
                        boolean rOccured = rs.getBoolean("rOccured");
                        double rOddN = rs.getDouble("rOddN");
                        double rOddD = rs.getDouble("rOddD");
                        //match
                        int mId = rs.getInt("mId");
                        Timestamp ts = rs.getTimestamp("mTime");
                        boolean mFinished = rs.getBoolean("mFinished");
                        
                        //home team
                        int htId = rs.getInt("htId");
                        String htName = rs.getString("htName");
                        //away Team
                        int atId = rs.getInt("atId");
                        String atName = rs.getString("atName");
                        
                        Team ht = new Team(htId, htName);
                        Team at = new Team(atId, atName);
                        
                        Match m = new Match(mId, ts, ht, at, rId, mFinished);
                        
                        Result r = new Result(rId, rName, rOddN, rOddD, rOccured, m);
                        Bet b = new Bet(betId, betA, uId, r, gain);

                        bets.add(b);
                    }
                }

            } catch (SQLException e) {
                Logger.getLogger(MatchBean.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                DBHelper.closeConnection(rs, s, conn);
            }
        }

        return getBets();
    }

    /**
     * calculates the total gain for the list of bets currently used
     * @return double summed gain
     */
    public double getTotalUserGainForBets(){
        if(bets == null) return 0;
        //sums userGain of all bets in list
        return bets.stream().mapToDouble(b -> b.getUserGain()).sum();
    }
    
    /**
     * calculates the total amount for the list of bets currently used
     * @return double summed amount
     */
    public double getTotalUserAmountForBets(){
        if(bets == null) return 0;
        //sums amount of all bets in list
        return bets.stream().mapToDouble(b -> b.getAmount()).sum();
    }
    
    /**
     * resets all variables back to their default values
     */
    private void resetVariables() {
        amount = betAmount = null;
        creditCardNumber = validationCode = resultName = betParams = null;
        resultId = 0;
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

    /**
     * @return the resultId
     */
    public int getResultId() {
        return resultId;
    }

    /**
     * @param resultId the resultId to set
     */
    public void setResultId(int resultId) {
        this.resultId = resultId;
    }

    /**
     * @return the resultName
     */
    public String getResultName() {
        return resultName;
    }

    /**
     * @param resultName the resultName to set
     */
    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    /**
     * @return the betAmount
     */
    public BigDecimal getBetAmount() {
        return betAmount;
    }

    /**
     * @param betAmount the betAmount to set
     */
    public void setBetAmount(BigDecimal betAmount) {
        this.betAmount = betAmount;
    }

    /**
     * @return the showNeedToChargeBalanceToBetMessage
     */
    public boolean isShowNeedToChargeBalanceToBetMessage() {
        return showNeedToChargeBalanceToBetMessage;
    }

    /**
     * @param showNeedToChargeBalanceToBetMessage the
     * showNeedToChargeBalanceToBetMessage to set
     */
    public void setShowNeedToChargeBalanceToBetMessage(boolean showNeedToChargeBalanceToBetMessage) {
        this.showNeedToChargeBalanceToBetMessage = showNeedToChargeBalanceToBetMessage;
    }

    /**
     * @return the bets
     */
    public List<Bet> getBets() {
        return bets;
    }

}
