package bean;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import model.Role;
import model.User;
import util.DBHelper;
import util.MessageHelper;
import util.PasswordHelper;
import static util.PasswordHelper.validatePassword;

/**
 *
 * @author Philippe Lüthi and Elia Kocher
 */
@ManagedBean(name="loginBean", eager=true)
@SessionScoped
public class LoginBean {

    private User user;
    private String locale;
    private String username;
    private String password;
    private String password2;

    private final static String LOGIN_SITE = "/login.xhtml?faces-redirect=true";
    private final static String REGISTER_SITE = "/register.xhtml?faces-redirect=true";
    private final static String HOME_SITE = "/home.xhtml?faces-redirect=true";

    /**
     * sets the locale
     * @param locale locale to set
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * gets the current locale
     * @return locale
     */
    public String getLocale() {
        if (locale == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            return context.getViewRoot().getLocale().toString();
        }
        return locale;
    }

    /**
     * Tries to log the user in
     * @return String Website to redirect to or null
     * if null is returned, an error message is displayed on the website
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.spec.InvalidKeySpecException
     */
    public String login() throws NoSuchAlgorithmException, InvalidKeySpecException {
        //check if user is already logged in

        //check user data to make sure they contain values and passwords are equal
        if (username == null || password == null || username.length() == 0 || password.length() == 0) {
            MessageHelper.addMessageToComponent("frm_login", "messages", "loginErrNameOrPasswordEmpty", FacesMessage.SEVERITY_WARN);
            return null;
        }

        Connection conn = DBHelper.getDBConnection();
        if (conn != null) {

            try {
                this.user = getUserFromDB(conn, true);
                if (getUser() == null || !(validatePassword(password, user.getPassword()))) {
                    //log the user in
                    MessageHelper.addMessageToComponent("frm_login", "messages", "loginUsernameAndPasswordNotFound", FacesMessage.SEVERITY_WARN);
                    this.user=null;
                    return null;
                //might want to add the user's bets?
                }else{
                    
                }
            } catch (SQLException e) {
                MessageHelper.addMessageToComponent("frm_login", "messages", "loginErrDB", FacesMessage.SEVERITY_ERROR);
                return null;
            } finally {
                DBHelper.closeConnection(conn);
            }
            
            //login successful
            this.username = this.password = "";
            return HOME_SITE;
        //could not connect to db, user can't be logged in
        }else{
            MessageHelper.addMessageToComponent("frm_login", "messages", "loginErrDB", FacesMessage.SEVERITY_ERROR);
            return null;
        } 
    }

    /**
     * logs the user out
     * @return String Website to redirect to or null
     * if null is returned, an error message is displayed on the website
     */
    public String logout(){
        this.user = null;
        this.username = this.password = "";
        return HOME_SITE;
    }
    
    /**
     * Tries to create a new user
     * @return String Website to redirect to or null
     * if null is returned, an error message is displayed on the website
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.spec.InvalidKeySpecException
     */
    public String register() throws NoSuchAlgorithmException, InvalidKeySpecException {
        //check user data to make sure they contain values and passwords are equal
        if (username == null || password == null || username.length() == 0 || password.length() == 0) {
            MessageHelper.addMessageToComponent("frm_register", "messages", "registerErrNameOrPasswordEmpty", FacesMessage.SEVERITY_WARN);
            return null;
        } else if (!password.equals(password2)) {
            MessageHelper.addMessageToComponent("frm_register", "messages", "registerErrPasswordsDoNotMatch", FacesMessage.SEVERITY_WARN);
            return null;
        }

        Connection conn = DBHelper.getDBConnection();
        if (conn != null) {

            PreparedStatement s = null;

            try {
                //check if username already exists
                if (getUserFromDB(conn, false) != null) {
                    MessageHelper.addMessageToComponent("frm_register", "messages", "registerUserNameExists", FacesMessage.SEVERITY_WARN);
                    return null;
                }

                int res;

                String sql = "INSERT INTO users (name, password) "
                        + "VALUES (?, ?)";
                s = conn.prepareStatement(sql);
                //PasswordHash ph = new PasswordHelper();
                String hashedPassword= PasswordHelper.createHash(password);
                s.setString(1, username);
                s.setString(2, hashedPassword);

                res = s.executeUpdate();

                //error
                if (res < 1) {
                    MessageHelper.addMessageToComponent("frm_register", "messages", "registerErrCreateUser", FacesMessage.SEVERITY_ERROR);
                    return null;
                }
            } catch (SQLException e) {
                MessageHelper.addMessageToComponent("frm_register", "messages", "registerErrDB", FacesMessage.SEVERITY_ERROR);
                return null;
            } finally {
                DBHelper.closeConnection(s, conn);
            }

            return LOGIN_SITE;
        }

        return REGISTER_SITE;
    }

    /**
     * loads the specified user from the db (and checks if the entered password is correct)
     * @param conn connection to the database
     * @param withPW true if a password check should be checked as well
     * @return User object or null
     * @throws SQLException 
     */
    private User getUserFromDB(Connection conn, boolean withPW) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        User u = null;

        ResultSet rs = null;
        PreparedStatement s = null;

        String sql = "SELECT id, name, balance, roleFK, password FROM users "
                + "WHERE name = ?";
        if (withPW) {
            //sql += " AND password = ?";
        }

        s = conn.prepareStatement(sql);
        s.setString(1, username);
        if (withPW) {
            //s.setString(2, PasswordHelper.createHash(password));
        }

        rs = s.executeQuery();

        if (rs != null) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                BigDecimal balance = rs.getBigDecimal("balance");
                int roleId = rs.getInt("roleFK");
                String hashedPassword= rs.getString("password");
                u = new User(id, name, balance, new Role(roleId));
                if (withPW) {
                    u.setPassword(hashedPassword);
                }
            }
        }

        DBHelper.closeConnection(rs, s);

        return u;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
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

    /**
     * @return the password2
     */
    public String getPassword2() {
        return password2;
    }

    /**
     * @param password the password2 to set
     */
    public void setPassword2(String password) {
        this.password2 = password;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

}
