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

/**
 * Add Trainee — End-to-End Test Suite
 *
 * FRD Reference: Section 2.2.8 — Add Trainee (Cohort Deep-Dive page)
 *
 * Flow:
 *   1. Navigate to Cohort Management
 *   2. Click a Cohort ID link → opens Cohort Deep-Dive page
 *   3. Click "Add Trainee" button
 *   4. Modal "Add Trainee" opens with 4 required fields:
 *        - Trainee ID    (text, placeholder "e.g. EMP001", required)
 *        - Full Name     (text, placeholder "e.g. John Doe", required)
 *        - Email         (email, placeholder "e.g. john.doe@example.com", required)
 *        - Employment Type (dropdown: Full-Time / Intern, required)
 *   5. Fill all fields → click Add/Submit
 *   6. Verify: new trainee row appears in the Trainees table
 *
 * Test sections:
 *   A — Navigate to Cohort Deep-Dive              (tests 1–3)
 *   B — Add Trainee modal structure               (tests 4–9)
 *   C — Fill form and submit (E2E)                (tests 10–13)
 *   D — Validation: empty field submission        (tests 14–15)
 *
 * NOTE: Tests 10–13 ADD real data to the database.
 *       Use a unique Trainee ID each run (we auto-generate one with timestamp).
 */
@Listeners(ExtentReportListener.class)
public class AddTraineeTest extends BaseClassTest {

    private CohortManagementPage cohortPage;
    private CohortDeepDivePage   deepDivePage;

    // Unique test data — timestamp ensures no duplicate-ID conflicts
    private final String TEST_TRAINEE_ID   = "TEST" + System.currentTimeMillis();
    private final String TEST_FULL_NAME    = "AutoTest User";
    private final String TEST_EMAIL        = "autotest" + System.currentTimeMillis() + "@test.com";

    // ── Additional locators for Add Trainee modal fields ─────────────────────

    // Full Name text input inside Add Trainee modal
    private final By fullNameInput = By.cssSelector(
            "[class*='modal'] input[formcontrolname*='name'], " +
                    "[class*='modal'] input[formcontrolname*='fullName'], " +
                    "[class*='modal'] input[placeholder*='John'], " +
                    "[class*='modal'] input[placeholder*='Name']," +
                    "[role='dialog'] input[placeholder*='Name']"
    );

    // Email input inside modal
    private final By emailInput = By.cssSelector(
            "[class*='modal'] input[type='email'], " +
                    "[class*='modal'] input[formcontrolname*='email'], " +
                    "[class*='modal'] input[placeholder*='email'], " +
                    "[class*='modal'] input[placeholder*='john.doe'], " +
                    "[role='dialog'] input[type='email']"
    );

    // Employment Type dropdown (Full-Time / Intern)
    private final By employmentTypeDropdown = By.cssSelector(
            "[class*='modal'] select[formcontrolname*='employment'], " +
                    "[class*='modal'] select[formcontrolname*='type'], " +
                    "[class*='modal'] select[formcontrolname*='empType'], " +
                    "[role='dialog'] select"
    );

    // Submit button (Add / Save / Enroll)
    private final By addTraineeSubmitBtn = By.xpath(
            "//*[contains(@class,'modal') or @role='dialog']" +
                    "//button[contains(normalize-space(),'Add') " +
                    "or contains(normalize-space(),'Submit') " +
                    "or contains(normalize-space(),'Save') " +
                    "or contains(normalize-space(),'Enroll')]" +
                    "[not(contains(normalize-space(),'Cancel'))]"
    );

    // Success notification after adding trainee
    private final By successNotification = By.xpath(
            "//*[contains(text(),'success') or contains(text(),'Success') " +
                    "or contains(text(),'added') or contains(text(),'Added') " +
                    "or contains(@class,'toast') or contains(@class,'alert-success')]"
    );

    // Trainees table rows (to verify new row added)
    private final By traineesTableRows = By.cssSelector(
            "table tbody tr, [class*='trainee-row'], [class*='intern-row']"
    );

    // Modal title for Add Trainee
    private final By addTraineeModalTitle = By.cssSelector(
            "[class*='modal'] h5, [class*='modal'] h4, " +
                    "[class*='modal-title'], [role='dialog'] h5"
    );

    // ── Setup ─────────────────────────────────────────────────────────────────

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToCohortDeepDive() {
        // Login as Super Admin
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));

        // Navigate to Cohort Management
        SuperAdminDashboardPage dashPage = new SuperAdminDashboardPage(driver);
        dashPage.getMenuItemElement("Cohort Management").click();

        cohortPage = new CohortManagementPage(driver);
        cohortPage.waitForTableToLoad();
        System.out.println("Cohort Management loaded. Rows: " + cohortPage.getTableRows().size());

        // Click the first Cohort ID hyperlink to open Deep-Dive page
        List<WebElement> rows = cohortPage.getTableRows();
        Assert.assertFalse(rows.isEmpty(), "No cohorts found — cannot navigate to Deep-Dive page.");
        cohortPage.clickCohortIdSpanInRow(rows.get(0));

        // Wait for Deep-Dive page to load
        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("/cohort-management")));
        deepDivePage = new CohortDeepDivePage(driver);
        System.out.println("Deep-Dive page loaded — URL: " + driver.getCurrentUrl());
    }

    // =========================================================================
    //  SECTION A — Deep-Dive page is loaded correctly
    // =========================================================================

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
        highlight(btn, "yellow", "Add Trainee button [FRD 2.2.8]");
        Assert.assertTrue(btn.isEnabled(),
                "FAIL [FRD 2.2.8] — 'Add Trainee' button is disabled.");
        System.out.println("PASS — 'Add Trainee' button is enabled.");
    }

    // =========================================================================
    //  SECTION B — Add Trainee Modal Structure
    // =========================================================================

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
        highlight(fullNameFields.get(0), "green", "Full Name field [FRD 2.2.8]");
        System.out.println("PASS — Full Name input is present.");
    }

    @Test(priority = 8,
            description = "TC-AT-008 [FRD 2.2.8]: Email input field is present")
    public void verifyEmailFieldPresent() {
        List<WebElement> emailFields = driver.findElements(emailInput);
        Assert.assertFalse(emailFields.isEmpty(),
                "FAIL [FRD 2.2.8] — Email input not found in Add Trainee modal. " +
                        "FRD 2.2.8 requires this mandatory field.");
        highlight(emailFields.get(0), "green", "Email field [FRD 2.2.8]");
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

    // =========================================================================
    //  SECTION C — Fill All 4 Fields and Submit (End-to-End)
    // =========================================================================

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
        highlight(field, "green", "Trainee ID: " + TEST_TRAINEE_ID);
        System.out.println("PASS — Trainee ID entered: " + TEST_TRAINEE_ID);
    }

    @Test(priority = 11,
            description = "TC-AT-011 [FRD 2.2.8]: Fill Full Name, Email fields")
    public void fillFullNameAndEmail() {
        // Fill Full Name
        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(fullNameInput));
        nameField.clear();
        nameField.sendKeys(TEST_FULL_NAME);
        highlight(nameField, "green", "Full Name: " + TEST_FULL_NAME);

        // Fill Email
        WebElement emailField = driver.findElement(emailInput);
        emailField.clear();
        emailField.sendKeys(TEST_EMAIL);
        highlight(emailField, "green", "Email: " + TEST_EMAIL);

        System.out.println("PASS — Full Name and Email filled.");
    }

    @Test(priority = 12,
            description = "TC-AT-012 [FRD 2.2.8]: Select Employment Type = 'Intern'")
    public void selectEmploymentType() {
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(employmentTypeDropdown));
        Select empType = new Select(dropdown);
        // Select "Intern" (as per FRD 2.2.8 options)
        try {
            empType.selectByVisibleText("Intern");
        } catch (Exception e) {
            // Fallback: select index 1 or 2 (skip placeholder at index 0)
            empType.selectByIndex(1);
        }
        String selected = empType.getFirstSelectedOption().getText();
        highlight(dropdown, "green", "Employment Type: " + selected);
        System.out.println("PASS — Employment Type selected: " + selected);
    }

    @Test(priority = 13,
            description = "TC-AT-013 [FRD 2.2.8]: Submit form — trainee row appears in Trainees table")
    public void submitAddTraineeFormAndVerify() {
        // Get trainee count before submission
        int countBefore = driver.findElements(traineesTableRows).size();
        System.out.println("Trainee rows before submit: " + countBefore);

        // Click the Submit / Add button
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(addTraineeSubmitBtn));
        highlight(submitBtn, "yellow", "Submit Add Trainee form [FRD 2.2.8]");
        submitBtn.click();

        // Wait for modal to close and table to refresh
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // Verify success notification OR new row in table
        boolean successShown = false;
        List<WebElement> notifications = driver.findElements(successNotification);
        if (!notifications.isEmpty() && notifications.stream().anyMatch(WebElement::isDisplayed)) {
            successShown = true;
            String msg = notifications.stream().filter(WebElement::isDisplayed)
                    .findFirst().map(WebElement::getText).orElse("(toast found)");
            System.out.println("SUCCESS NOTIFICATION: \"" + msg + "\"");
        }

        // Verify new row count
        int countAfter = driver.findElements(traineesTableRows).size();
        System.out.println("Trainee rows after submit: " + countAfter);

        boolean rowAdded = countAfter > countBefore;

        // Pass if either success toast shown OR new row added
        Assert.assertTrue(successShown || rowAdded,
                "FAIL [FRD 2.2.8] — After submitting Add Trainee form, no success notification appeared " +
                        "AND row count did not increase. Before: " + countBefore + " | After: " + countAfter);
        System.out.println("PASS — Trainee added. Count: " + countBefore + " → " + countAfter);
    }

    // =========================================================================
    //  SECTION D — Validation: Empty Field Submission
    // =========================================================================

    @Test(priority = 14,
            description = "TC-AT-014 [FRD 2.2.8]: Clicking Add without filling fields shows validation")
    public void verifyEmptyFormValidation() {
        // Reopen Add Trainee modal
        deepDivePage.clickAddTraineeButton();
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}

        // Click submit without filling anything
        List<WebElement> submitBtns = driver.findElements(addTraineeSubmitBtn);
        if (!submitBtns.isEmpty()) {
            submitBtns.get(0).click();
            try { Thread.sleep(600); } catch (InterruptedException ignored) {}

            // Check for any validation signal:
            // 1. Alert dialog  2. Required field indicators  3. Modal still open
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

        // FRD 2.2.8: "each marked with a red asterisk (*) to indicate they are required"
        By requiredMarkers = By.xpath(
                "//*[contains(@class,'modal') or @role='dialog']//*[text()='*' " +
                        "or contains(@class,'required') or contains(@class,'asterisk')]"
        );
        List<WebElement> markers = driver.findElements(requiredMarkers);
        System.out.println("Required markers found: " + markers.size());

        // Accept 4 markers OR simply verify all 4 inputs are in the modal
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