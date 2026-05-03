package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseTest;
import com.cts.mfrp.onecohort.pages.DashboardPage;
import com.cts.mfrp.onecohort.pages.EmployeePage;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentManager;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import com.cts.mfrp.onecohort.utils.RetryAnalyzer;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(ExtentReportListener.class)
public class EmployeeTest extends BaseTest {

    private EmployeePage employeePage;

    @BeforeMethod(alwaysRun = true)
    public void loginAndNavigate() {
        LoginPage loginPage = new LoginPage(getDriver());
        DashboardPage dashboard = loginPage.loginAs(
                ConfigReader.getUsername(), ConfigReader.getPassword());
        employeePage = dashboard.navigateToEmployee();
    }

    @Test(groups = {"smoke"}, retryAnalyzer = RetryAnalyzer.class,
          description = "Verify employee list page loads")
    public void employeeListLoadsTest() {
        ExtentManager.getTest().info("Verifying employee table is visible");
        Assert.assertTrue(employeePage.isEmployeeTableVisible(),
                "Employee table should be visible");
    }

    @Test(groups = {"regression"},
          description = "Verify add employee form opens")
    public void addEmployeeFormOpensTest() {
        employeePage.clickAddEmployee();
        ExtentManager.getTest().info("Clicked Add Employee button");
        Assert.assertTrue(employeePage.getCurrentUrl().contains("addEmployee"),
                "URL should navigate to add employee page");
    }

    @Test(groups = {"regression"},
          description = "Search for non-existent employee shows no records")
    public void searchNonExistentEmployeeTest() {
        employeePage.searchEmployee("ZZZ_NOT_EXIST_ZZZ");
        ExtentManager.getTest().info("Searched for non-existent employee");
        Assert.assertTrue(employeePage.isNoRecordsDisplayed(),
                "No Records Found message should appear");
    }
}
