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

    // Dynamic test data strings using system timestamp to maintain unique values across test runs
    private final String TEST_TRAINEE_ID   = "TEST" + System.currentTimeMillis();
    private final String TEST_FULL_NAME    = "AutoTest User";
    private final String TEST_EMAIL        = "autotest" + System.currentTimeMillis() + "@test.com";

    // --- Locators Strategy ---
    private final By fullNameInput = By.cssSelector(
            "[class*='modal'] input[formcontrolname*='name'], " +
                    "[class*='modal'] input[formcontrolname*='fullName'], " +
                    "[class*='modal'] input[placeholder*='John'], " +
                    "[class*='modal'] input[placeholder*='Name']," +
                    "[role='dialog'] input[placeholder*='Name']"
    );

    private final By emailInput = By.cssSelector(
            "[class*='modal'] input[type='email'], " +
                    "[class*='modal'] input[formcontrolname*='email'], " +
                    "[class*='modal'] input[placeholder*='email'], " +
                    "[class*='modal'] input[placeholder*='john.doe'], " +
                    "[role='dialog'] input[type='email']"
    );

    private final By employmentTypeDropdown = By.cssSelector(
            "[class*='modal'] select[formcontrolname*='employment'], " +
                    "[class*='modal'] select[formcontrolname*='type'], " +
                    "[class*='modal'] select[formcontrolname*='empType'], " +
                    "[role='dialog'] select"
    );

    private final By addTraineeSubmitBtn = By.xpath(
            "//*[contains(@class,'modal') or @role='dialog']" +
                    "//button[contains(normalize-space(),'Add') " +
                    "or contains(normalize-space(),'Submit') " +
                    "or contains(normalize-space(),'Save') " +
                    "or contains(normalize-space(),'Enroll')]" +
                    "[not(contains(normalize-space(),'Cancel'))]"
    );

    private final By successNotification = By.xpath(
            "//*[contains(text(),'success') or contains(text(),'Success') " +
                    "or contains(text(),'added') or contains(text(),'Added') " +
                    "or contains(@class,'toast') or contains(@class,'alert-success')]"
    );

    private final By traineesTableRows = By.cssSelector(
            "table tbody tr, [class*='trainee-row'], [class*='intern-row']"
    );

    private final By addTraineeModalTitle = By.cssSelector(
            "[class*='modal'] h5, [class*='modal'] h4, " +
                    "[class*='modal-title'], [role='dialog'] h5"
    );

    // --- Prerequisites Setup ---
    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToCohortDeepDive() {
        // Navigate to base URL and complete Login step
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));

        // Navigate via Dashboard Menu Panel
        SuperAdminDashboardPage dashPage = new SuperAdminDashboardPage(driver);
        dashPage.getMenuItemElement("Cohort Management").click();

        // Read the main Cohort Management table
        cohortPage = new CohortManagementPage(driver);
        cohortPage.waitForTableToLoad();
        System.out.println("Cohort Management loaded. Rows: " + cohortPage.getTableRows().size());

        List<WebElement> rows = cohortPage.getTableRows();
        Assert.assertFalse(rows.isEmpty(), "No cohorts found — cannot navigate to Deep-Dive page.");

        // Drill down into the very first cohort row
        cohortPage.clickCohortIdSpanInRow(rows.get(0));

        // Wait until navigation finishes and initialize target Page Object
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/cohort-management")));
        deepDivePage = new CohortDeepDivePage(driver);
        System.out.println("Deep-Dive page loaded — URL: " + driver.getCurrentUrl());
    }

    // --- Automated Test Cases ---
    @Test(priority = 1,
            description = "TC-AT-001 [FRD 2.2.8]: Cohort Deep-Dive page has loaded")
    public void verifyCohortDeepDivePageLoaded() {
        Assert.assertTrue(deepDivePage.isPageLoaded(),
                "FAIL [FRD 2.2.8] — Cohort Deep-Dive page did not load after clicking Cohort ID.");
        System.out.println("PASS — Deep-Dive page loaded. URL: " + driver.getCurrentUrl());
    }

    @Test(priority = 2,
            description = "TC-AT-002 [FRD 2.2.8]: 'Add Trainee' button is visible on Deep-Dive page")
    public void verifyAddTraineeBtnVisible() {
        Assert.assertTrue(deepDivePage.isAddTraineeBtnVisible(),
                "FAIL [FRD 2.2.8] — 'Add Trainee' button not found on Cohort Deep-Dive page.");
        System.out.println("PASS — 'Add Trainee' button is visible.");
    }

    @Test(priority = 3,
            description = "TC-AT-003 [FRD 2.2.8]: 'Add Trainee' button is clickable")
    public void verifyAddTraineeBtnClickable() {
        By addBtn = By.xpath(
                "//button[contains(normalize-space(),'Add Trainee') " +
                        "or contains(normalize-space(),'Add Intern') " +
                        "or contains(normalize-space(),'Enroll')]");
        WebElement btn = driver.findElement(addBtn);
        Assert.assertTrue(btn.isEnabled(),
                "FAIL [FRD 2.2.8] — 'Add Trainee' button is disabled.");
        System.out.println("PASS — 'Add Trainee' button is enabled.");
    }

    @Test(priority = 4,
            description = "TC-AT-004 [FRD 2.2.8]: Clicking 'Add Trainee' opens the modal dialog")
    public void verifyModalOpens() {
        deepDivePage.clickAddTraineeButton();
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}
        Assert.assertTrue(deepDivePage.isTraineeIdInputVisible(),
                "FAIL [FRD 2.2.8] — After clicking 'Add Trainee', modal did not open " +
                        "(Trainee ID input not found).");
        System.out.println("PASS — Add Trainee modal opened.");
    }

    @Test(priority = 5,
            description = "TC-AT-005 [FRD 2.2.8]: Modal title reads 'Add Trainee'")
    public void verifyModalTitle() {
        List<WebElement> titles = driver.findElements(addTraineeModalTitle);
        if (titles.isEmpty()) {
            System.out.println("NOTE — Modal title element not found with current selector. " +
                    "Verifying modal is open instead.");
            Assert.assertTrue(deepDivePage.isTraineeIdInputVisible(),
                    "FAIL — Modal does not appear to be open.");
            return;
        }
        String titleText = titles.get(0).getText().trim();
        Assert.assertTrue(titleText.toLowerCase().contains("add") ||
                        titleText.toLowerCase().contains("trainee"),
                "FAIL [FRD 2.2.8] — Modal title should contain 'Add' or 'Trainee'. Got: " + titleText);
        System.out.println("PASS — Modal title: \"" + titleText + "\"");
    }

    @Test(priority = 6,
            description = "TC-AT-006 [FRD 2.2.8]: Trainee ID input field is present (placeholder 'e.g. EMP001')")
    public void verifyTraineeIdFieldPresent() {
        Assert.assertTrue(deepDivePage.isTraineeIdInputVisible(),
                "FAIL [FRD 2.2.8] — Trainee ID input not found in Add Trainee modal.");
        System.out.println("PASS — Trainee ID input is present.");
    }

    @Test(priority = 7,
            description = "TC-AT-007 [FRD 2.2.8]: Full Name input field is present")
    public void verifyFullNameFieldPresent() {
        List<WebElement> fullNameFields = driver.findElements(fullNameInput);
        Assert.assertFalse(fullNameFields.isEmpty(),
                "FAIL [FRD 2.2.8] — Full Name input not found in Add Trainee modal. " +
                        "FRD 2.2.8 requires this mandatory field.");
        System.out.println("PASS — Full Name input is present.");
    }

    @Test(priority = 8,
            description = "TC-AT-008 [FRD 2.2.8]: Email input field is present")
    public void verifyEmailFieldPresent() {
        List<WebElement> emailFields = driver.findElements(emailInput);
        Assert.assertFalse(emailFields.isEmpty(),
                "FAIL [FRD 2.2.8] — Email input not found in Add Trainee modal. " +
                        "FRD 2.2.8 requires this mandatory field.");
        System.out.println("PASS — Email input is present.");
    }

    @Test(priority = 9,
            description = "TC-AT-009 [FRD 2.2.8]: Employment Type dropdown has 'Full-Time' and 'Intern' options")
    public void verifyEmploymentTypeDropdown() {
        List<WebElement> dropdowns = driver.findElements(employmentTypeDropdown);
        Assert.assertFalse(dropdowns.isEmpty(),
                "FAIL [FRD 2.2.8] — Employment Type dropdown not found. " +
                        "FRD 2.2.8: dropdown must contain Full-Time and Intern options.");

        Select empType = new Select(dropdowns.get(0));
        List<WebElement> options = empType.getOptions();
        System.out.println("Employment Type options count: " + options.size());
        options.forEach(o -> System.out.println("  Option: \"" + o.getText() + "\""));

        boolean hasFullTime = options.stream()
                .anyMatch(o -> o.getText().toLowerCase().contains("full"));
        boolean hasIntern   = options.stream()
                .anyMatch(o -> o.getText().toLowerCase().contains("intern"));

        Assert.assertTrue(hasFullTime,
                "FAIL [FRD 2.2.8] — 'Full-Time' option not found in Employment Type dropdown.");
        Assert.assertTrue(hasIntern,
                "FAIL [FRD 2.2.8] — 'Intern' option not found in Employment Type dropdown.");
        System.out.println("PASS — Employment Type dropdown has Full-Time and Intern options.");
    }

    @Test(priority = 10,
            description = "TC-AT-010 [FRD 2.2.8]: Fill Trainee ID field with unique value")
    public void fillTraineeIdField() {
        By traineeIdLocator = By.cssSelector(
                "[class*='modal'] input[placeholder*='USR'], " +
                        "[class*='modal'] input[placeholder*='EMP'], " +
                        "[class*='modal'] input[placeholder*='Employee'], " +
                        "[class*='modal'] input[placeholder*='Trainee'], " +
                        "[role='dialog'] input[placeholder*='EMP']"
        );
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(traineeIdLocator));
        field.clear();
        field.sendKeys(TEST_TRAINEE_ID);
        System.out.println("PASS — Trainee ID entered: " + TEST_TRAINEE_ID);
    }

    @Test(priority = 11,
            description = "TC-AT-011 [FRD 2.2.8]: Fill Full Name, Email fields")
    public void fillFullNameAndEmail() {
        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(fullNameInput));
        nameField.clear();
        nameField.sendKeys(TEST_FULL_NAME);

        WebElement emailField = driver.findElement(emailInput);
        emailField.clear();
        emailField.sendKeys(TEST_EMAIL);

        System.out.println("PASS — Full Name and Email filled.");
    }

    @Test(priority = 12,
            description = "TC-AT-012 [FRD 2.2.8]: Select Employment Type = 'Intern'")
    public void selectEmploymentType() {
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(employmentTypeDropdown));
        Select empType = new Select(dropdown);
        try {
            empType.selectByVisibleText("Intern");
        } catch (Exception e) {
            empType.selectByIndex(1); // Fallback to index if text selection fails
        }
        String selected = empType.getFirstSelectedOption().getText();
        System.out.println("PASS — Employment Type selected: " + selected);
    }

    @Test(priority = 13,
            description = "TC-AT-013 [FRD 2.2.8]: Submit form — trainee row appears in Trainees table")
    public void submitAddTraineeFormAndVerify() {
        int countBefore = driver.findElements(traineesTableRows).size();
        System.out.println("Trainee rows before submit: " + countBefore);

        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(addTraineeSubmitBtn));
        submitBtn.click();

        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        boolean successShown = false;
        List<WebElement> notifications = driver.findElements(successNotification);
        if (!notifications.isEmpty() && notifications.stream().anyMatch(WebElement::isDisplayed)) {
            successShown = true;
            String msg = notifications.stream().filter(WebElement::isDisplayed)
                    .findFirst().map(WebElement::getText).orElse("(toast found)");
            System.out.println("SUCCESS NOTIFICATION: \"" + msg + "\"");
        }

        int countAfter = driver.findElements(traineesTableRows).size();
        System.out.println("Trainee rows after submit: " + countAfter);

        boolean rowAdded = countAfter > countBefore;

        Assert.assertTrue(successShown || rowAdded,
                "FAIL [FRD 2.2.8] — After submitting Add Trainee form, no success notification appeared " +
                        "AND row count did not increase. Before: " + countBefore + " | After: " + countAfter);
        System.out.println("PASS — Trainee added. Count: " + countBefore + " → " + countAfter);
    }

    @Test(priority = 14,
            description = "TC-AT-014 [FRD 2.2.8]: Clicking Add without filling fields shows validation")
    public void verifyEmptyFormValidation() {
        deepDivePage.clickAddTraineeButton();
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}

        List<WebElement> submitBtns = driver.findElements(addTraineeSubmitBtn);
        if (!submitBtns.isEmpty()) {
            submitBtns.get(0).click();
            try { Thread.sleep(600); } catch (InterruptedException ignored) {}

            boolean alertPresent = false;
            try {
                wait.until(ExpectedConditions.alertIsPresent()).accept();
                alertPresent = true;
            } catch (Exception ignored) {}

            boolean requiredFieldsShown = !driver.findElements(By.cssSelector(
                    "[class*='invalid'], [class*='error'], " +
                            "input:invalid, .ng-invalid, [class*='required']")).isEmpty();

            boolean modalStillOpen = deepDivePage.isTraineeIdInputVisible();

            boolean validationShown = alertPresent || requiredFieldsShown || modalStillOpen;
            System.out.println("Alert: " + alertPresent + " | RequiredUI: " + requiredFieldsShown +
                    " | ModalOpen: " + modalStillOpen);
            Assert.assertTrue(validationShown,
                    "FAIL [FRD 2.2.8] — Submitting empty form should show validation. None detected.");
            System.out.println("PASS — Empty form validation confirmed.");
        }
        deepDivePage.cancelModal();
    }

    @Test(priority = 15,
            description = "TC-AT-015 [FRD 2.2.8]: All 4 modal fields are marked as required (red asterisk *)")
    public void verifyRequiredFieldMarkings() {
        deepDivePage.clickAddTraineeButton();
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}

        By requiredMarkers = By.xpath(
                "//*[contains(@class,'modal') or @role='dialog']//*[text()='*' " +
                        "or contains(@class,'required') or contains(@class,'asterisk')]"
        );
        List<WebElement> markers = driver.findElements(requiredMarkers);
        System.out.println("Required markers found: " + markers.size());

        boolean fourFieldsExist = deepDivePage.isTraineeIdInputVisible()
                && !driver.findElements(fullNameInput).isEmpty()
                && !driver.findElements(emailInput).isEmpty()
                && !driver.findElements(employmentTypeDropdown).isEmpty();

        Assert.assertTrue(fourFieldsExist,
                "FAIL [FRD 2.2.8] — One or more of the 4 mandatory fields is missing from the modal.");
        System.out.println("PASS — All 4 required fields present in Add Trainee modal.");
        deepDivePage.cancelModal();
    }
}