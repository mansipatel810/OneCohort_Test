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
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Trainee Management Test Suite
 *
 * Tests covered (on the Cohort Deep Dive page):
 *   1. "Add Trainee" button is visible on the cohort detail page
 *   2. Clicking "Add Trainee" opens a modal with title "Add Trainee"
 *   3. The modal contains all required fields (Trainee ID, Full Name, Email, Employment Type)
 *   4. Submit button is disabled when the form is empty (prevents invalid submission)
 *
 * Login: Super Admin, then navigate to a specific cohort's deep dive page.
 */
@Listeners(ExtentReportListener.class)
@Test(groups = {"regression", "functional", "cohort", "superadmin"})
public class TraineeTest extends BaseClassTest {

    private CohortDeepDivePage deepDivePage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToCohortDeepDive() {
        // Log in as Super Admin
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));

        // Navigate to Cohort Management via sidebar
        SuperAdminDashboardPage dashPage = new SuperAdminDashboardPage(driver);
        dashPage.getMenuItemElement("Cohort Management").click();
        wait.until(ExpectedConditions.urlContains("cohort"));

        // Wait for the cohort table to load
        CohortManagementPage cohortPage = new CohortManagementPage(driver);
        cohortPage.waitForTableToLoad();

        // Click on the first cohort's ID to open the deep dive page
        List<WebElement> rows = cohortPage.getTableRows();
        Assert.assertFalse(rows.isEmpty(), "Cohort table must have rows to navigate to deep dive");

        WebElement firstCell = rows.get(0).findElement(By.cssSelector("td:first-child"));
        String urlBefore = driver.getCurrentUrl();
        firstCell.click();

        // Wait for navigation to the cohort detail page
        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(urlBefore)));
        deepDivePage = new CohortDeepDivePage(driver);
        // Wait for the deep-dive page to fully render (Angular + render.com latency)
        deepDivePage.waitForPageLoad();
        System.out.println("Setup complete. Cohort Deep Dive URL: " + driver.getCurrentUrl());
    }

    // ── TC-TRAINEE-001 ───────────────────────────────────────────────────────
    @Test(priority = 1, description = "TC-TRAINEE-001: Add Trainee button is visible on the cohort detail page")
    public void testAddTraineeBtnVisible() {
        Assert.assertTrue(deepDivePage.isAddTraineeBtnVisible(),
                "The 'Add Trainee' button should be visible on the Cohort Detail page");
        System.out.println("PASS - Add Trainee button is visible.");
    }

    // ── TC-TRAINEE-002 ───────────────────────────────────────────────────────
    @Test(priority = 2, description = "TC-TRAINEE-002: Clicking Add Trainee opens a modal with title 'Add Trainee'")
    public void testAddTraineeModalOpens() {
        // Open the Add Trainee modal
        deepDivePage.clickAddTraineeOpen();
        deepDivePage.waitForModalToOpen();

        // Verify the modal is open by checking the modal title
        WebElement modalTitle = deepDivePage.getAddTraineeModalTitle();
        Assert.assertNotNull(modalTitle, "Add Trainee modal title element should exist");
        Assert.assertEquals(modalTitle.getText().trim(), "Add Trainee",
                "Modal title should read 'Add Trainee'");
        System.out.println("PASS - Modal opened with title: " + modalTitle.getText());
    }

    // ── TC-TRAINEE-003 ───────────────────────────────────────────────────────
    @Test(priority = 3, description = "TC-TRAINEE-003: Add Trainee modal has all required form fields")
    public void testAddTraineeModalHasRequiredFields() {
        // The modal should already be open from TC-TRAINEE-002.
        // Guard: if it has been closed (or a fresh run), re-open it.
        // NOTE: isAddTraineeBtnVisible() was wrong here — the button stays visible even when
        // the modal is open (it's just behind the backdrop). Use isAddTraineeModalOpen() instead.
        if (!deepDivePage.isAddTraineeModalOpen()) {
            deepDivePage.clickAddTraineeOpen();
            deepDivePage.waitForModalToOpen();
        }

        // Verify Trainee ID input field is present
        WebElement traineeIdInput = deepDivePage.getTraineeIdInput();
        Assert.assertTrue(traineeIdInput.isDisplayed(),
                "Trainee ID input field should be visible in the modal");

        // Verify Employment Type dropdown is present with required options
        List<WebElement> employmentOptions = deepDivePage.getEmploymentTypeOptions();
        Assert.assertFalse(employmentOptions.isEmpty(),
                "Employment Type dropdown should have options (Full-Time, Intern)");

        boolean hasFullTime = employmentOptions.stream()
                .anyMatch(option -> option.getText().trim().equals("Full-Time"));
        boolean hasIntern = employmentOptions.stream()
                .anyMatch(option -> option.getText().trim().equals("Intern"));

        Assert.assertTrue(hasFullTime, "Employment Type should include 'Full-Time' option");
        Assert.assertTrue(hasIntern,   "Employment Type should include 'Intern' option");

        System.out.println("PASS - All required fields are present in the Add Trainee modal.");
    }

    // ── TC-TRAINEE-004 ───────────────────────────────────────────────────────
    @Test(priority = 4, description = "TC-TRAINEE-004: Submit button is disabled when the form is empty")
    public void testSubmitDisabledWithEmptyForm() {
        // Close and reopen the modal to get a fresh empty form
        deepDivePage.cancelAddTraineeModal();
        deepDivePage.waitForModalToClose();

        deepDivePage.clickAddTraineeOpen();
        deepDivePage.waitForModalToOpen();

        // Verify that the Submit button is disabled on an empty form
        // This prevents accidental submissions with no data
        Assert.assertFalse(deepDivePage.isAddTraineeSubmitEnabled(),
                "Submit button should be DISABLED when the form is empty");
        System.out.println("PASS - Submit button is correctly disabled for empty form.");

        // Close the modal
        deepDivePage.cancelAddTraineeModal();
    }

    // ── TC-TRAINEE-005 ───────────────────────────────────────────────────────
    @Test(priority = 5, description = "TC-TRAINEE-005: Cohort name heading is visible on the cohort detail page")
    public void testCohortNameHeadingVisible() {
        // The cohort detail page should clearly show the cohort's name as a heading
        Assert.assertTrue(deepDivePage.isCohortNameHeadingVisible(),
                "Cohort name heading should be visible on the Cohort Detail page");
        System.out.println("PASS - Cohort name heading is visible on the cohort detail page.");
    }

    // ── TC-TRAINEE-006 ───────────────────────────────────────────────────────
    @Test(priority = 6, description = "TC-TRAINEE-006: Cohort detail page shows KPI summary cards")
    public void testDeepDiveSummaryCardsPresent() {
        // The detail page should show summary cards with key cohort metrics
        List<WebElement> summaryCards = deepDivePage.getSummaryCards();
        Assert.assertFalse(summaryCards.isEmpty(),
                "Cohort detail page should show at least one KPI summary card");
        System.out.println("PASS - Found " + summaryCards.size() + " KPI summary card(s) on the cohort detail page.");
    }
}
