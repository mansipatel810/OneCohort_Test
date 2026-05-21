package com.cts.mfrp.onecohort.tests.cohort;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
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
import java.util.stream.Collectors;

/**
 * Cohort Management Test Suite
 *
 * Tests covered:
 *   1. Cohort Management page loads with the correct URL and heading
 *   2. Table has required column headers (Cohort ID, Name, Status)
 *   3. Table has at least one data row with content
 *   4. Search bar filters the cohort list when a keyword is typed
 *   5. Create Cohort button opens a modal dialog
 *   6. Create Cohort modal contains all required form fields
 *   7. Edit button on a cohort row opens the edit modal
 *   8. Clicking a Cohort ID cell opens the cohort detail (deep dive) page
 *
 * Login: Super Admin (one browser session shared by all tests)
 */
@Listeners(ExtentReportListener.class)
@Test(groups = {"smoke", "regression", "cohort", "superadmin"})
public class CohortManagementTest extends BaseClassTest {

    private CohortManagementPage cohortPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToCohortManagement() {
        // Log in as Super Admin
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));

        // Click "Cohort Management" in the sidebar
        SuperAdminDashboardPage dashPage = new SuperAdminDashboardPage(driver);
        dashPage.getMenuItemElement("Cohort Management").click();

        // Wait for the cohort management page to load
        wait.until(ExpectedConditions.urlContains("cohort"));
        cohortPage = new CohortManagementPage(driver);
        cohortPage.waitForTableToLoad();
        System.out.println("Setup complete. Cohort Management URL: " + driver.getCurrentUrl());
    }

    // ── TC-COHORT-001 ─────────────────────────────────────────────────────────
    @Test(priority = 1, description = "TC-COHORT-001: Cohort Management page loads with correct URL and heading")
    public void testCohortPageLoads() {
        // Verify the URL contains a cohort segment
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("cohort"),
                "URL should contain 'cohort'. Got: " + url);

        // Verify the page heading is visible
        Assert.assertTrue(cohortPage.isPageHeadingVisible(),
                "Cohort Management heading should be visible on the page");
        System.out.println("PASS - Cohort page heading: " + cohortPage.getPageHeadingElement().getText());
    }

    // ── TC-COHORT-002 ─────────────────────────────────────────────────────────
    @Test(priority = 2, description = "TC-COHORT-002: Cohort table has required column headers")
    public void testTableHasRequiredColumns() {
        // Get all column header elements from the table
        List<WebElement> headers = cohortPage.getTableHeaders();
        Assert.assertFalse(headers.isEmpty(), "Table should have column headers");

        // Combine all header text into one string for easy searching
        String allHeaderText = headers.stream()
                .map(WebElement::getText)
                .collect(Collectors.joining(" "))
                .toLowerCase();

        // These 3 columns are required by the specification
        Assert.assertTrue(allHeaderText.contains("id"),
                "Table should have a 'Cohort ID' column. Found: " + allHeaderText);
        Assert.assertTrue(allHeaderText.contains("name"),
                "Table should have a 'Cohort Name' column. Found: " + allHeaderText);
        Assert.assertTrue(allHeaderText.contains("status"),
                "Table should have a 'Status' column. Found: " + allHeaderText);

        System.out.println("PASS - Required columns found: " + allHeaderText);
    }

    // ── TC-COHORT-003 ─────────────────────────────────────────────────────────
    @Test(priority = 3, description = "TC-COHORT-003: Cohort table has at least one data row")
    public void testTableHasDataRows() {
        List<WebElement> rows = cohortPage.getTableRows();
        Assert.assertFalse(rows.isEmpty(),
                "Cohort table should have at least one row of data");
        System.out.println("PASS - Cohort table has " + rows.size() + " row(s).");
    }

    // ── TC-COHORT-004 ─────────────────────────────────────────────────────────
    @Test(priority = 4, description = "TC-COHORT-004: Search bar filters the cohort list")
    public void testSearchFiltersCohortList() {
        // Pick a short search term from the first cohort ID in the table
        List<WebElement> rows = cohortPage.getTableRows();
        String searchTerm = "INT"; // Default search term
        if (!rows.isEmpty()) {
            String firstCellText = rows.get(0).findElement(By.cssSelector("td:first-child")).getText().trim();
            if (firstCellText.length() >= 3) {
                searchTerm = firstCellText.substring(0, 3);
            }
        }
        int rowsBefore = rows.size();

        // Type the search term — this filters the list
        cohortPage.searchByKeyword(searchTerm);

        // Wait a moment for Angular to update the table (no Thread.sleep needed here
        // because the next getTableRows() call will return the current DOM state)
        wait.until(d -> !cohortPage.getTableRows().isEmpty());
        List<WebElement> rowsAfterSearch = cohortPage.getTableRows();
        Assert.assertFalse(rowsAfterSearch.isEmpty(),
                "Search with term '" + searchTerm + "' should return at least one result");
        System.out.println("PASS - Search with '" + searchTerm + "' returned " + rowsAfterSearch.size() + " rows.");

        // Clear the search by typing an empty string to restore the full list
        cohortPage.searchByKeyword("");
        wait.until(d -> cohortPage.getTableRows().size() >= rowsBefore);
        System.out.println("PASS - Search cleared. Rows restored to " + cohortPage.getTableRows().size() + ".");
    }

    // ── TC-COHORT-005 ─────────────────────────────────────────────────────────
    @Test(priority = 5, description = "TC-COHORT-005: Create Cohort button opens a modal dialog")
    public void testCreateCohortModalOpens() {
        // Click the "Create Cohort" button
        cohortPage.clickCreateCohort();

        // Wait for the modal to appear
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[class*='modal'], [role='dialog'], .modal-card")));
        Assert.assertTrue(modal.isDisplayed(),
                "A modal dialog should appear after clicking Create Cohort");
        System.out.println("PASS - Create Cohort modal opened.");

        // Close the modal to leave the page in a clean state
        cohortPage.closeModal();
    }

    // ── TC-COHORT-006 ─────────────────────────────────────────────────────────
    @Test(priority = 6, description = "TC-COHORT-006: Create Cohort modal has all required form fields")
    public void testCreateCohortModalHasRequiredFields() {
        // Open the Create Cohort modal
        cohortPage.clickCreateCohort();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[class*='modal'], [role='dialog'], .modal-card")));

        // Check for a text input (for the Cohort Name field)
        List<WebElement> textInputs = driver.findElements(
                By.cssSelector("[class*='modal'] input[type='text'], .modal-card input[type='text']"));
        Assert.assertFalse(textInputs.isEmpty(),
                "Create Cohort modal should have a text input for Cohort Name");

        // Check for a dropdown (for the Service Line selection)
        List<WebElement> dropdowns = driver.findElements(
                By.cssSelector("[class*='modal'] select, .modal-card select"));
        Assert.assertFalse(dropdowns.isEmpty(),
                "Create Cohort modal should have a dropdown for Service Line");

        // Check for date inputs (Start Date and End Date)
        List<WebElement> dateInputs = driver.findElements(
                By.cssSelector("[class*='modal'] input[type='date'], .modal-card input[type='date']"));
        Assert.assertFalse(dateInputs.isEmpty(),
                "Create Cohort modal should have date input fields");

        System.out.println("PASS - All required form fields are present in the Create Cohort modal.");
        cohortPage.closeModal();
    }

    // ── TC-COHORT-007 ─────────────────────────────────────────────────────────
    @Test(priority = 7, description = "TC-COHORT-007: Edit button on a cohort row opens the edit modal")
    public void testEditButtonOpensModal() {
        // Find the edit buttons provided by the page object
        List<WebElement> editButtons = cohortPage.getEditButtons();
        Assert.assertFalse(editButtons.isEmpty(),
                "Each cohort row should have an Edit button");

        // Click the first edit button
        editButtons.get(0).click();

        // Verify the edit modal opened
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[class*='modal'], [role='dialog'], .modal-card")));
        Assert.assertTrue(modal.isDisplayed(),
                "Edit modal should appear after clicking the Edit button");
        System.out.println("PASS - Edit modal opened successfully.");

        // Close the modal
        cohortPage.closeModal();
    }

    // ── TC-COHORT-008 ─────────────────────────────────────────────────────────
    @Test(priority = 8, description = "TC-COHORT-008: Clicking a Cohort ID opens the cohort detail (deep dive) page")
    public void testCohortIdLinkOpensDeepDivePage() {
        // Get the rows from the table
        List<WebElement> rows = cohortPage.getTableRows();
        Assert.assertFalse(rows.isEmpty(), "Table must have rows to test navigation");

        String urlBefore = driver.getCurrentUrl();

        // Click the first cell (Cohort ID) of the first row to open the detail page
        WebElement firstCell = rows.get(0).findElement(By.cssSelector("td:first-child"));
        firstCell.click();

        // Wait for the URL to change, confirming navigation to the detail page
        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(urlBefore)));

        String urlAfter = driver.getCurrentUrl();
        Assert.assertNotEquals(urlAfter, urlBefore,
                "Clicking the Cohort ID should navigate to the cohort detail page");
        System.out.println("PASS - Navigated to cohort detail page. URL: " + urlAfter);

        // Navigate back to cohort management for any tests that follow
        driver.navigate().back();
        wait.until(ExpectedConditions.urlContains("cohort"));
        cohortPage.waitForTableToLoad();
    }

    // ── TC-COHORT-009 ─────────────────────────────────────────────────────────
    @Test(priority = 9, description = "TC-COHORT-009: A Filters button is visible on the Cohort Management page")
    public void testFiltersButtonVisible() {
        // A filter button lets users narrow down the cohort list by status or service line
        Assert.assertTrue(cohortPage.isFiltersBtnVisible(),
                "A Filters button should be visible on the Cohort Management page");
        System.out.println("PASS - Filters button is visible on the Cohort Management page.");
    }

    // ── TC-COHORT-010 ─────────────────────────────────────────────────────────
    @Test(priority = 10, description = "TC-COHORT-010: Status badges are visible in the cohort table rows")
    public void testStatusBadgesVisible() {
        // Each cohort row should have a status badge (e.g. Active, Completed, Upcoming)
        List<WebElement> statusBadges = cohortPage.getStatusBadgesInTable();
        Assert.assertFalse(statusBadges.isEmpty(),
                "Cohort table should have at least one status badge (Active, Completed, or Upcoming)");
        System.out.println("PASS - Found " + statusBadges.size() + " status badge(s) in the cohort table.");
    }

    // ── TC-COHORT-011 ─────────────────────────────────────────────────────────
    @Test(priority = 11, description = "TC-COHORT-011: Create Cohort button is enabled and ready to click")
    public void testCreateCohortButtonEnabled() {
        // The Create Cohort button should be enabled (not grayed out or disabled)
        WebElement createBtn = cohortPage.getCreateBtnPrimaryElement();
        Assert.assertTrue(createBtn.isEnabled(),
                "Create Cohort button should be enabled (not disabled)");
        System.out.println("PASS - Create Cohort button is enabled and ready to click.");
    }

    // ── TC-COHORT-012 ─────────────────────────────────────────────────────────
    @Test(priority = 12, description = "TC-COHORT-012: Clicking Cancel in the Create Cohort modal closes the modal")
    public void testCancelButtonClosesCreateModal() {
        // Open the Create Cohort modal
        cohortPage.clickCreateCohort();
        cohortPage.waitForModalVisible();
        Assert.assertTrue(cohortPage.isModalCardVisible(),
                "Create Cohort modal should be open after clicking Create Cohort");

        // Click the Cancel button — this should dismiss the modal without any action
        cohortPage.clickCancelBtn();
        cohortPage.waitForModalInvisible();

        Assert.assertFalse(cohortPage.isModalCardVisible(),
                "Create Cohort modal should be closed after clicking Cancel");
        System.out.println("PASS - Create Cohort modal closed correctly when Cancel was clicked.");
    }
}
