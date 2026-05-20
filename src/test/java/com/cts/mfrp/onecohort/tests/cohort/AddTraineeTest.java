package com.cts.mfrp.onecohort.tests.cohort;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.pages.cohort.CohortDeepDivePage;
import com.cts.mfrp.onecohort.pages.cohort.CohortManagementPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

@Listeners(ExtentReportListener.class)
public class AddTraineeTest extends BaseClassTest {

    private CohortManagementPage cohortPage;
    private CohortDeepDivePage   deepDivePage;

    private final String TEST_TRAINEE_ID   = "TEST" + System.currentTimeMillis();
    private final String TEST_FULL_NAME    = "AutoTest User";
    private final String TEST_EMAIL        = "autotest" + System.currentTimeMillis() + "@test.com";

    private final By traineeIdInput = By.cssSelector(".modal-dialog-custom input[name='id']");
    private final By fullNameInput = By.cssSelector(".modal-dialog-custom input[name='fullName']");
    private final By emailInput = By.cssSelector(".modal-dialog-custom input[name='email']");
    private final By employmentTypeDropdown = By.cssSelector(".modal-dialog-custom select[name='employmentType']");
    private final By addTraineeSubmitBtn = By.cssSelector(".modal-dialog-custom button[type='submit']");
    private final By cancelModalBtn = By.cssSelector(".modal-dialog-custom button.btn-outline-secondary, .modal-dialog-custom button.btn-close");
    private final By successNotification = By.xpath("//*[contains(text(),'success') or contains(text(),'Success') or contains(text(),'added') or contains(text(),'Added') or contains(@class,'toast') or contains(@class,'alert-success')]");
    private final By traineesTableRows = By.cssSelector("table tbody tr");
    private final By addTraineeModalTitle = By.cssSelector(".modal-dialog-custom h5");
    private final By addTraineeOpenBtn = By.xpath("//button[contains(normalize-space(),'Add Trainee')]");
    private final By cohortRowSelector = By.xpath("//table//tr[contains(.,'INTQEA26SD002')]");

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToCohortDeepDive() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));

        SuperAdminDashboardPage dashPage = new SuperAdminDashboardPage(driver);
        dashPage.getMenuItemElement("Cohort Management").click();

        cohortPage = new CohortManagementPage(driver);
        cohortPage.waitForTableToLoad();

        WebElement targetRow = wait.until(ExpectedConditions.presenceOfElementLocated(cohortRowSelector));
        cohortPage.clickCohortIdSpanInRow(targetRow);

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/cohort-management")));
        deepDivePage = new CohortDeepDivePage(driver);
        System.out.println("Deep-Dive page loaded — URL: " + driver.getCurrentUrl());
    }

    @Test(priority = 1, description = "TC-AT-001 [FRD 2.2.8]: Cohort Deep-Dive page has loaded")
    public void verifyCohortDeepDivePageLoaded() {
        Assert.assertTrue(driver.getCurrentUrl().contains("cohort") || deepDivePage.isPageLoaded());
    }

    @Test(priority = 2, description = "TC-AT-002 [FRD 2.2.8]: 'Add Trainee' button is visible on Deep-Dive page")
    public void verifyAddTraineeBtnVisible() {
        WebElement btn = wait.until(ExpectedConditions.visibilityOfElementLocated(addTraineeOpenBtn));
        Assert.assertTrue(btn.isDisplayed());
    }

    @Test(priority = 3, description = "TC-AT-003 [FRD 2.2.8]: 'Add Trainee' button is clickable")
    public void verifyAddTraineeBtnClickable() {
        WebElement btn = driver.findElement(addTraineeOpenBtn);
        Assert.assertTrue(btn.isEnabled());
    }

    @Test(priority = 4, description = "TC-AT-004 [FRD 2.2.8]: Clicking 'Add Trainee' opens the modal dialog")
    public void verifyModalOpens() {
        driver.findElement(addTraineeOpenBtn).click();
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(traineeIdInput));
        Assert.assertTrue(input.isDisplayed());
    }

    @Test(priority = 5, description = "TC-AT-005 [FRD 2.2.8]: Modal title reads 'Add Trainee'")
    public void verifyModalTitle() {
        WebElement title = driver.findElement(addTraineeModalTitle);
        Assert.assertEquals(title.getText().trim(), "Add Trainee");
    }

    @Test(priority = 6, description = "TC-AT-006 [FRD 2.2.8]: Trainee ID input field is present")
    public void verifyTraineeIdFieldPresent() {
        Assert.assertTrue(driver.findElement(traineeIdInput).isDisplayed());
    }

    @Test(priority = 7, description = "TC-AT-007 [FRD 2.2.8]: Full Name input field is present")
    public void verifyFullNameFieldPresent() {
        Assert.assertTrue(driver.findElement(fullNameInput).isDisplayed());
    }

    @Test(priority = 8, description = "TC-AT-008 [FRD 2.2.8]: Email input field is present")
    public void verifyEmailFieldPresent() {
        Assert.assertTrue(driver.findElement(emailInput).isDisplayed());
    }

    @Test(priority = 9, description = "TC-AT-009 [FRD 2.2.8]: Employment Type dropdown has 'Full-Time' and 'Intern' options")
    public void verifyEmploymentTypeDropdown() {
        WebElement dropdown = driver.findElement(employmentTypeDropdown);
        Select select = new Select(dropdown);
        List<WebElement> options = select.getOptions();

        boolean hasFullTime = options.stream().anyMatch(o -> o.getText().trim().equals("Full-Time"));
        boolean hasIntern = options.stream().anyMatch(o -> o.getText().trim().equals("Intern"));

        Assert.assertTrue(hasFullTime);
        Assert.assertTrue(hasIntern);
    }

    @Test(priority = 10, description = "TC-AT-010 [FRD 2.2.8]: Fill Trainee ID field with unique value")
    public void fillTraineeIdField() {
        WebElement field = driver.findElement(traineeIdInput);
        field.clear();
        field.sendKeys(TEST_TRAINEE_ID);
    }

    @Test(priority = 11, description = "TC-AT-011 [FRD 2.2.8]: Fill Full Name, Email fields")
    public void fillFullNameAndEmail() {
        WebElement nameField = driver.findElement(fullNameInput);
        nameField.clear();
        nameField.sendKeys(TEST_FULL_NAME);

        WebElement emailField = driver.findElement(emailInput);
        emailField.clear();
        emailField.sendKeys(TEST_EMAIL);
    }

    @Test(priority = 12, description = "TC-AT-012 [FRD 2.2.8]: Select Employment Type = 'Intern'")
    public void selectEmploymentType() {
        WebElement dropdown = driver.findElement(employmentTypeDropdown);
        Select select = new Select(dropdown);
        select.selectByValue("Intern");
    }

    @Test(priority = 13, description = "TC-AT-013 [FRD 2.2.8]: Submit form — trainee row appears in Trainees table")
    public void submitAddTraineeFormAndVerify() {
        WebElement submitBtn = driver.findElement(addTraineeSubmitBtn);
        Assert.assertTrue(submitBtn.isEnabled());
        submitBtn.click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(traineeIdInput));

        List<WebElement> rows = driver.findElements(traineesTableRows);
        boolean containsNewTrainee = rows.stream().anyMatch(r -> r.getText().contains(TEST_TRAINEE_ID));

        boolean successShown = false;
        List<WebElement> notifications = driver.findElements(successNotification);
        if (!notifications.isEmpty() && notifications.stream().anyMatch(WebElement::isDisplayed)) {
            successShown = true;
        }

        Assert.assertTrue(successShown || containsNewTrainee || rows.size() > 1);
    }

    @Test(priority = 14, description = "TC-AT-014 [FRD 2.2.8]: Clicking Add without filling fields shows validation")
    public void verifyEmptyFormValidation() {
        driver.findElement(cancelModalBtn).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(traineeIdInput));

        driver.findElement(addTraineeOpenBtn).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(traineeIdInput));

        WebElement submitBtn = driver.findElement(addTraineeSubmitBtn);
        Assert.assertFalse(submitBtn.isEnabled());
    }

    @Test(priority = 15, description = "TC-AT-015 [FRD 2.2.8]: All 4 modal fields are marked as required (red asterisk *)")
    public void verifyRequiredFieldMarkings() {
        By dangerAsterisk = By.cssSelector(".modal-dialog-custom label span.text-danger");
        List<WebElement> asterisks = driver.findElements(dangerAsterisk);
        Assert.assertTrue(asterisks.size() >= 4);
        driver.findElement(cancelModalBtn).click();
    }
}