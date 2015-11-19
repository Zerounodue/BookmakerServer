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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import model.Role;
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
    private String username;
    private String password;
    private String password2;

    private final static String LOGIN_SITE = "/login.xhtml?faces-redirect=true";
    private final static String REGISTER_SITE = "/register.xhtml?faces-redirect=true";
    private final static String HOME_SITE = "/home.xhtml?faces-redirect=true";

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        if (locale == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            return context.getViewRoot().getLocale().toString();
        }
        return locale;
    }

    public String login() {
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
                if (getUser() == null) {
                    //log the user in
                    MessageHelper.addMessageToComponent("frm_login", "messages", "loginUsernameAndPasswordNotFound", FacesMessage.SEVERITY_WARN);
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

        }
        //login successful
        this.username = this.password = "";
        return HOME_SITE;
    }

    public String logout(){
        this.user = null;
        this.username = this.password = "";
        return HOME_SITE;
    }
    
    public String register() {
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

                s.setString(1, username);
                s.setString(2, password);

                res = s.executeUpdate();

                //error
                if (res < 0) {
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

    //TODO add to db helper?
    private User getUserFromDB(Connection conn, boolean withPW) throws SQLException {
        User u = null;

        ResultSet rs = null;
        PreparedStatement s = null;

        String sql = "SELECT id, name, balance, roleFK FROM users "
                + "WHERE name = ?";
        if (withPW) {
            sql += " AND password = ?";
        }

        s = conn.prepareStatement(sql);
        s.setString(1, username);
        if (withPW) {
            s.setString(2, password);
        }

        rs = s.executeQuery();

        if (rs != null) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                Double balance = rs.getDouble("balance");
                int roleId = rs.getInt("roleFK");

                u = new User(id, name, balance, new Role(roleId));
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
