package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage {

    private final By dashboardHeader  = By.cssSelector(".oxd-topbar-header-title");
    private final By employeeMenuItem = By.xpath("//a[contains(@href,'viewEmployeeList')]");
    private final By leaveMenuItem    = By.xpath("//a[contains(@href,'viewLeaveList')]");
    private final By logoutMenu       = By.cssSelector(".oxd-userdropdown-tab");
    private final By logoutOption     = By.xpath("//a[text()='Logout']");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public boolean isDashboardLoaded() {
        return isDisplayed(dashboardHeader);
    }

    public String getDashboardTitle() {
        return getPageTitle();
    }

    public EmployeePage navigateToEmployee() {
        click(employeeMenuItem);
        return new EmployeePage(driver);
    }

    public LeavePage navigateToLeave() {
        click(leaveMenuItem);
        return new LeavePage(driver);
    }

    public LoginPage logout() {
        click(logoutMenu);
        click(logoutOption);
        return new LoginPage(driver);
    }
}
