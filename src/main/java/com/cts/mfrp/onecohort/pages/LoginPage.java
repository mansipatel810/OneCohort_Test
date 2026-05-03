package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private final By usernameField = By.id("txtUsername");
    private final By passwordField = By.id("txtPassword");
    private final By loginButton   = By.id("btnLogin");
    private final By errorMessage  = By.id("spanMessage");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage enterUsername(String username) {
        type(usernameField, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordField, password);
        return this;
    }

    public DashboardPage clickLogin() {
        click(loginButton);
        return new DashboardPage(driver);
    }

    /** Convenience method: fills credentials and submits in one call. */
    public DashboardPage loginAs(String username, String password) {
        return enterUsername(username)
                .enterPassword(password)
                .clickLogin();
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }
}
