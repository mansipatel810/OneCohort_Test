package com.cts.mfrp.onecohort.tests.cr;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.constants.AppConstants;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.cr.CRDashboardPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CR (Class Representative) Role Test Suite
 *
 * Tests covered:
 *   1. CR dashboard loads at the correct /cr/{cohortId} URL
 *   2. Cohort metadata fields are visible (Batch Owner, Start Date, Total Interns)
 *   3. Trainees table is visible and has required columns
 *   4. CR has no Create/Edit/Delete buttons (read-only access)
 *
 * Login: CR role with a valid user ID and cohort ID.
 * All tests share one browser session.
 */
@Listeners(ExtentReportListener.class)
@Test(groups = {"smoke", "regression", "cr"})
public class CRTest extends BaseClassTest {

    private static final String CR_COHORT_ID = ConfigReader.getValidCohortId();
    private CRDashboardPage crPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAsCR() {
        // Log in as CR with a valid user ID and cohort ID
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsCR(ConfigReader.getSuperAdminUserId(), CR_COHORT_ID);

        // Wait for the CR dashboard URL to load
        wait.until(ExpectedConditions.urlContains("/cr/"));

        crPage = new CRDashboardPage(driver, CR_COHORT_ID);
        System.out.println("CR login complete. URL: " + driver.getCurrentUrl());
    }

    // ── TC-CR-001 ─────────────────────────────────────────────────────────────
    @Test(priority = 1, description = "TC-CR-001: CR dashboard loads at the correct /cr/{cohortId} URL")
    public void testCRDashboardLoads() {
        // Verify the URL contains the CR route with the assigned cohort ID
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/cr/"),
                "URL should contain /cr/ after CR login. Got: " + url);
        Assert.assertTrue(url.contains(CR_COHORT_ID),
                "URL should contain cohort ID " + CR_COHORT_ID + ". Got: " + url);

        System.out.println("PASS - CR dashboard loaded at URL: " + url);
    }

    // ── TC-CR-002 ─────────────────────────────────────────────────────────────
    @Test(priority = 2, description = "TC-CR-002: Cohort metadata fields are visible (Batch Owner, Start Date, Total Interns)")
    public void testCohortMetadataFieldsVisible() {
        // These fields give the CR important information about their assigned cohort
        Assert.assertTrue(crPage.isBatchOwnerFieldVisible(),
                "Batch Owner field should be visible in the metadata panel");
        Assert.assertTrue(crPage.isStartDateFieldVisible(),
                "Start Date field should be visible in the metadata panel");
        Assert.assertTrue(crPage.isTotalInternsFieldVisible(),
                "Total Interns field should be visible in the metadata panel");

        System.out.println("PASS - Cohort metadata fields (Batch Owner, Start Date, Total Interns) are visible.");
    }

    // ── TC-CR-003 ─────────────────────────────────────────────────────────────
    @Test(priority = 3, description = "TC-CR-003: Trainees table is visible and has required columns")
    public void testTraineesTableHasRequiredColumns() {
        // Verify the trainees table is visible
        Assert.assertTrue(crPage.isTraineesTableVisible(),
                "Trainees table should be visible on the CR dashboard");

        // Get the column headers from the trainees table
        List<WebElement> headerElements = crPage.getTraineesTableHeaders();
        String allHeaders = headerElements.stream()
                .map(h -> h.getText().trim())
                .collect(Collectors.joining(" "))
                .toLowerCase();

        // Verify the required columns exist
        Assert.assertTrue(allHeaders.contains("id"),
                "Trainees table should have an 'ID' column. Found: " + allHeaders);
        Assert.assertTrue(allHeaders.contains("name"),
                "Trainees table should have a 'Name' column. Found: " + allHeaders);
        Assert.assertTrue(allHeaders.contains("email"),
                "Trainees table should have an 'Email' column. Found: " + allHeaders);
        Assert.assertTrue(allHeaders.contains("employment") || allHeaders.contains("type"),
                "Trainees table should have an 'Employment Type' column. Found: " + allHeaders);

        System.out.println("PASS - Trainees table has all required columns: " + allHeaders);
    }

    // ── TC-CR-004 ─────────────────────────────────────────────────────────────
    @Test(priority = 4, description = "TC-CR-004: CR role has no Create/Edit/Delete buttons (read-only access)")
    public void testCRHasNoCreateEditDeleteButtons() {
        // Temporarily disable implicit wait to quickly check for the absence of buttons
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(0));
        List<WebElement> crudButtons = crPage.getCrudButtonElements();
        driver.manage().timeouts().implicitlyWait(
                java.time.Duration.ofSeconds(ConfigReader.getImplicitWait()));

        // CR is a read-only role — no Create, Edit, or Delete buttons should be present
        if (!crudButtons.isEmpty()) {
            String foundButtons = crudButtons.stream()
                    .map(btn -> "[" + btn.getText() + "]")
                    .collect(Collectors.joining(" "));
            Assert.fail("CR role should be read-only. Found CRUD buttons: " + foundButtons);
        }

        System.out.println("PASS - No Create/Edit/Delete buttons found. CR access is correctly read-only.");
    }

    // ── TC-CR-005 ─────────────────────────────────────────────────────────────
    @Test(priority = 5, description = "TC-CR-005: Training timeline section is visible on the CR dashboard")
    public void testTrainingTimelineSectionVisible() {
        // The CR dashboard must show the week-by-week training schedule for the cohort
        Assert.assertTrue(crPage.isTrainingTimelineSectionVisible(),
                "Training timeline section should be visible on the CR dashboard");
        System.out.println("PASS - Training timeline section is visible on the CR dashboard.");
    }

    // ── TC-CR-006 ─────────────────────────────────────────────────────────────
    @Test(priority = 6, description = "TC-CR-006: CR dashboard shows Qualifier, Interim, and Final evaluation sections")
    public void testEvaluationsVisible() {
        // All 3 evaluation checkpoints must be displayed on the CR dashboard
        Assert.assertTrue(crPage.isQualifierExamVisible(),
                "Qualifier Exam section should be visible on the CR dashboard");
        Assert.assertTrue(crPage.isInterimEvaluationVisible(),
                "Interim Evaluation section should be visible on the CR dashboard");
        Assert.assertTrue(crPage.isFinalEvaluationVisible(),
                "Final Evaluation section should be visible on the CR dashboard");
        System.out.println("PASS - All 3 evaluation sections visible on CR dashboard (Qualifier, Interim, Final).");
    }

    // ── TC-CR-007 ─────────────────────────────────────────────────────────────
    @Test(priority = 7, description = "TC-CR-007: Overall progress card is visible on the CR dashboard")
    public void testOverallProgressCardVisible() {
        // The overall progress card shows the cohort's combined completion percentage
        Assert.assertTrue(crPage.isOverallProgressCardVisible(),
                "Overall progress card should be visible on the CR dashboard");
        System.out.println("PASS - Overall progress card is visible on the CR dashboard.");
    }
}
