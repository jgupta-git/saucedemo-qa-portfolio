package com.saucedemo.automation.dto;

/**
 * Plain DTO carrying the login form's inputs. Populated from
 * saucedemo-credentials.properties for the "real" user, or built inline
 * with one of SauceDemo's other named test accounts
 * (locked_out_user, problem_user, performance_glitch_user, error_user,
 * visual_user) for negative/quirk-specific scenarios.
 */
public class LoginCredentials {

    private String username;
    private String password;

    public LoginCredentials() {
    }

    public LoginCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginCredentials{username='" + username + "'}";
    }
}
