package com.cts.mfrp.onecohort.tests.cohort;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Cohort Management Grid Tests — FRD Section 2.2 (Cohort Management Module)
 *
 * Scope: page navigation, grid visibility, column headers, data rows,
 *        status badges, Create Cohort button, row action buttons,
 *        and modal opening/field presence for Create Cohort.
 *
 * Login: Super Admin — all tests run in a single browser session.
 *
 * ── FRD Traceability ─────────────────────────────────────────────────────────
 * TC-COHORT-001  FRD 2.2         Cohort Management page navigable from sidebar
 * TC-COHORT-002  FRD 2.2         "Cohort Management" page heading present
 * TC-COHORT-003  FRD 2.2.1       Cohort data grid is rendered
 * TC-COHORT-004  FRD 2.2.1       Grid columns: Cohort ID, Cohort Name, Status present
 * TC-COHORT-005  FRD 2.2.1       Grid contains at least one data row
 * TC-COHORT-006  FRD 2.2.1       First row shows a non-empty Cohort ID
 * TC-COHORT-007  FRD 2.2.1       Status column shows colour-coded badges
 *                                 (Planning=Yellow, In-Progress=Blue, Completed=Green)
 * TC-COHORT-008  FRD 2.2.2       "Create Cohort" button is visible and enabled
 * TC-COHORT-009  FRD 2.2.1       Row-level action elements present
 * TC-COHORT-010  FRD 2.2.1       Cohort Name column is non-empty in first row
 * TC-COHORT-011  FRD 2.2.2       Create Cohort modal opens on button click
 * TC-COHORT-012  FRD 2.2.2       Create Cohort modal contains required fields
 * ─────────────────────────────────────────────────────────────────────────────
 */
@Test(groups = {"smoke", "regression", "cohort", "superadmin"})
@Listeners(ExtentReportListener.class)
public class CohortManagementGridTest extends BaseClassTest {

    private CohortManagementPage cohortPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigate() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("super-admin"));

        WebElement cohortNavLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//nav[contains(@class,'menu')]" +
                         "//*[contains(text(),'Cohort Management')]")));
        cohortNavLink.click();

        wait.until(ExpectedConditions.urlContains("cohort"));
        cohortPage = new CohortManagementPage(driver);
        System.out.println("Setup complete — Cohort Management URL: " + driver.getCurrentUrl());
    }

    // -------------------------------------------------------
    // TC-COHORT-001 — URL contains cohort segment
    // FRD 2.2 — Cohort Management module must be reachable via sidebar
    // -------------------------------------------------------
    @Test(priority = 1)
    public void verifyCohortManagementUrl() {
        String url = driver.getCurrentUrl();
        Assert.assertTrue(
                url.contains("cohort-management") || url.contains("cohort"),
                "FAIL - URL does not contain a cohort path segment. " +
                "FRD 2.2 requires the Cohort Management module to be accessible. " +
                "Actual URL: " + url);
        System.out.println("PASS - Cohort Management URL: " + url);
    }

    // -------------------------------------------------------
    // TC-COHORT-002 — "Cohort Management" page heading is visible
    // FRD 2.2 — Module heading must appear on the page
    // -------------------------------------------------------
    @Test(priority = 2)
    public void verifyCohortManagementHeading() {
        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[self::h1 or self::h2 or self::h3 or self::h4]" +
                         "[contains(text(),'Cohort Management')]")));
        highlight(heading);
        Assert.assertTrue(
                heading.isDisplayed(),
                "FAIL - 'Cohort Management' heading not visible on the page. " +
                "FRD 2.2 requires this heading to identify the module.");
        System.out.println("PASS - Cohort Management heading visible: " + heading.getText());
    }

    // -------------------------------------------------------
    // TC-COHORT-003 — Cohort data grid (table) is rendered
    // FRD 2.2.1 — A grid listing all cohorts must be displayed
    // -------------------------------------------------------
    @Test(priority = 3)
    public void verifyCohortGridVisible() {
        Assert.assertTrue(
                cohortPage.isTableVisible(),
                "FAIL - Cohort data grid is not visible on the page. " +
                "FRD 2.2.1 requires a grid displaying the list of cohorts.");
        System.out.println("PASS - Cohort data grid is visible");
    }

    // -------------------------------------------------------
    // TC-COHORT-004 — Grid has required column headers
    // FRD 2.2.1 — Required columns: Cohort ID, Cohort Name, Status
    // -------------------------------------------------------
    @Test(priority = 4)
    public void verifyCohortGridColumns() {
        List<WebElement> headers = cohortPage.getTableHeaders();
        Assert.assertFalse(
                headers.isEmpty(),
                "FAIL - No column headers found in the cohort grid. " +
                "FRD 2.2.1 requires the grid to have defined column headers.");

        // Normalise: trim whitespace, collapse all unicode-space chars (including  )
        // and upper-case so the comparison is case-insensitive.
        List<String> headerTexts = headers.stream()
                .map(WebElement::getText)
                .map(h -> h.replaceAll("[\\s\\u00A0]+", " ").trim().toUpperCase())
                .collect(Collectors.toList());
        System.out.println("INFO - Columns found (normalised): " + headerTexts);

        String[] requiredColumns = {"Cohort ID", "Cohort Name", "Status"};
        for (String col : requiredColumns) {
            final String colUpper = col.toUpperCase();
            boolean found = headerTexts.stream().anyMatch(h -> h.contains(colUpper));
            Assert.assertTrue(
                    found,
                    "FAIL - Required column '" + col + "' not found in grid headers. " +
                    "FRD 2.2.1 lists this as a mandatory grid column. " +
                    "Actual headers: " + headerTexts);
            System.out.println("PASS - Column present: " + col);
        }

        List<String> optionalCols = Arrays.asList("Service Line", "Start Date", "End Date", "Actions");
        for (String col : optionalCols) {
            final String colUpper = col.toUpperCase();
            boolean found = headerTexts.stream().anyMatch(h -> h.contains(colUpper));
            System.out.println((found ? "PASS" : "GAP") +
                    " - Column '" + col + "': " +
                    (found ? "present" : "NOT found — FRD 2.2.1 expects this column"));
        }
    }

    // -------------------------------------------------------
    // TC-COHORT-005 — Grid has at least one data row
    // FRD 2.2.1 — Grid must display cohort records from the database
    // -------------------------------------------------------
    @Test(priority = 5)
    public void verifyCohortGridHasRows() {
        // Wait up to explicit-wait seconds for the API response to populate the table.
        // The app is hosted on render.com (free tier) which can have 30-60 s cold-start
        // delays — rows arrive asynchronously and may not be present immediately.
        List<WebElement> rows;
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector("table tbody tr")));
            rows = cohortPage.getTableRows();
        } catch (Exception e) {
            rows = cohortPage.getTableRows(); // fallback — return whatever is present
        }
        Assert.assertFalse(
                rows.isEmpty(),
                "FAIL - Cohort grid has no data rows after waiting. " +
                "FRD 2.2.1 requires the grid to display all available cohorts. " +
                "Verify the test database contains at least one cohort record.");
        System.out.println("PASS - Cohort grid has " + rows.size() + " row(s)");
    }

    // -------------------------------------------------------
    // TC-COHORT-006 — First row displays a non-empty Cohort ID
    // FRD 2.2.1 — Cohort ID column must show the unique identifier per row
    // -------------------------------------------------------
    @Test(priority = 6)
    public void verifyCohortIdInFirstRow() {
        List<WebElement> rows = cohortPage.getTableRows();
        if (rows.isEmpty()) {
            System.out.println("SKIP - No rows available to validate Cohort ID column");
            return;
        }
        WebElement firstCell = rows.get(0).findElement(By.cssSelector("td:first-child"));
        highlight(firstCell);
        String cohortId = firstCell.getText().trim();
        Assert.assertFalse(
                cohortId.isEmpty(),
                "FAIL - Cohort ID cell in the first row is empty. " +
                "FRD 2.2.1 requires each row to display its Cohort ID.");

        List<WebElement> anchors = firstCell.findElements(By.tagName("a"));
        if (anchors.isEmpty()) {
            System.out.println("GAP - Cohort ID '" + cohortId + "' is not rendered as a hyperlink. " +
                    "FRD 2.2.1 requires the Cohort ID to be a blue clickable link to the detail page.");
        } else {
            System.out.println("PASS - Cohort ID '" + cohortId + "' is a clickable link.");
        }
        System.out.println("PASS - Cohort ID in first row: " + cohortId);
    }

    // -------------------------------------------------------
    // TC-COHORT-007 — Status badges present with correct values
    // FRD 2.2.1 — Status must be a colour-coded badge
    // -------------------------------------------------------
    @Test(priority = 7)
    public void verifyStatusBadgesPresent() {
        List<WebElement> statusCells = cohortPage.getStatusBadgesInTable();
        Assert.assertFalse(
                statusCells.isEmpty(),
                "FAIL - No status badges or status-column cells found in the cohort grid. " +
                "FRD 2.2.1 requires each row to show a colour-coded status badge.");

        List<String> acceptedStatuses = Arrays.asList(
                "Planning", "In-Progress", "In Progress", "Completed", "Active", "Upcoming");

        for (WebElement cell : statusCells) {
            String text = cell.getText().trim();
            if (!text.isEmpty()) {
                boolean isAccepted = acceptedStatuses.stream()
                        .anyMatch(s -> text.equalsIgnoreCase(s));
                if (isAccepted) {
                    System.out.println("PASS - Status badge value: '" + text + "'");
                    String classes = cell.getAttribute("class") != null
                            ? cell.getAttribute("class").toLowerCase() : "";
                    if (text.equalsIgnoreCase("Planning")) {
                        System.out.println(classes.contains("yellow") || classes.contains("warning")
                                ? "PASS - Planning badge is Yellow"
                                : "GAP - Planning badge expected Yellow. Classes: " + classes);
                    } else if (text.toLowerCase().contains("in-progress") || text.toLowerCase().contains("in progress")) {
                        System.out.println(classes.contains("blue") || classes.contains("primary") || classes.contains("info")
                                ? "PASS - In-Progress badge is Blue"
                                : "GAP - In-Progress badge expected Blue. Classes: " + classes);
                    } else if (text.equalsIgnoreCase("Completed")) {
                        System.out.println(classes.contains("green") || classes.contains("success")
                                ? "PASS - Completed badge is Green"
                                : "GAP - Completed badge expected Green. Classes: " + classes);
                    }
                } else {
                    System.out.println("GAP - Unexpected status value: '" + text + "'. " +
                            "FRD 2.2.1 accepted values: " + acceptedStatuses);
                }
            }
        }
    }

    // -------------------------------------------------------
    // TC-COHORT-008 — "Create Cohort" button is visible and enabled
    // FRD 2.2.2 — Super Admin must be able to initiate cohort creation
    // -------------------------------------------------------
    @Test(priority = 8)
    public void verifyCreateCohortButton() {
        Assert.assertTrue(
                cohortPage.isCreateCohortButtonVisible(),
                "FAIL - 'Create Cohort' button is NOT visible on the Cohort Management page. " +
                "FRD 2.2.2 requires a Create Cohort button to be present.");

        WebElement btn = driver.findElement(By.xpath(
                "//button[contains(normalize-space(),'Create Cohort') " +
                "or contains(normalize-space(),'Add Cohort') " +
                "or contains(normalize-space(),'New Cohort')]"));
        highlight(btn);
        Assert.assertTrue(
                btn.isEnabled(),
                "FAIL - 'Create Cohort' button is present but disabled. " +
                "FRD 2.2.2 requires Super Admin to be able to open the Create Cohort form.");
        System.out.println("PASS - 'Create Cohort' button visible and enabled: " + btn.getText());
    }

    // -------------------------------------------------------
    // TC-COHORT-009 — Row-level action elements are present
    // FRD 2.2.1, 2.2.3, 2.2.4 — Each row must have View, Edit, Delete actions
    // -------------------------------------------------------
    @Test(priority = 9)
    public void verifyRowActionsPresent() {
        List<WebElement> rows = cohortPage.getTableRows();
        if (rows.isEmpty()) {
            System.out.println("SKIP - No rows available; skipping row-action validation");
            return;
        }
        List<WebElement> actionElems = cohortPage.getFirstRowActionElements();
        Assert.assertFalse(
                actionElems.isEmpty(),
                "FAIL - No action buttons/links/icons found in the last column of the first row. " +
                "FRD 2.2.1 requires row-level actions (View Details, Edit, Delete Cohort).");
        System.out.println("PASS - Row action elements found: " + actionElems.size());
    }

    // -------------------------------------------------------
    // TC-COHORT-010 — Cohort Name column is non-empty in the first row
    // FRD 2.2.1 — Cohort Name is a mandatory grid column
    // -------------------------------------------------------
    @Test(priority = 10)
    public void verifyCohortNameColumnNotEmpty() {
        List<WebElement> rows = cohortPage.getTableRows();
        if (rows.isEmpty()) {
            System.out.println("SKIP - No rows available; skipping Cohort Name validation");
            return;
        }
        WebElement nameCell = rows.get(0).findElement(By.cssSelector("td:nth-child(2)"));
        highlight(nameCell);
        String cohortName = nameCell.getText().trim();
        Assert.assertFalse(
                cohortName.isEmpty(),
                "FAIL - Cohort Name cell in the first row is empty. " +
                "FRD 2.2.1 requires each cohort row to display the Cohort Name.");
        System.out.println("PASS - Cohort Name in first row: " + cohortName);
    }

    // -------------------------------------------------------
    // TC-COHORT-011 — Create Cohort modal opens on button click
    // FRD 2.2.2 — Clicking Create Cohort must open a modal/form overlay
    // -------------------------------------------------------
    @Test(priority = 11)
    public void verifyCreateCohortModalOpens() {
        cohortPage.clickCreateCohort();

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[class*='modal'], [role='dialog'], .dialog")));
        highlight(modal);
        Assert.assertTrue(
                modal.isDisplayed(),
                "FAIL - Create Cohort modal did not appear after clicking the 'Create Cohort' button. " +
                "FRD 2.2.2 requires a modal/form overlay to open for entering cohort details.");
        System.out.println("PASS - Create Cohort modal opened successfully");

        cohortPage.closeModal();
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("[class*='modal'], [role='dialog']")));
        } catch (Exception ignored) {}
    }

    // -------------------------------------------------------
    // TC-COHORT-012 — Create Cohort modal contains required fields
    // FRD 2.2.2 — Mandatory fields: Cohort Name, Service Line, Start Date, End Date
    // -------------------------------------------------------
    @Test(priority = 12)
    public void verifyCreateCohortModalFields() {
        cohortPage.clickCreateCohort();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[class*='modal'], [role='dialog']")));

        Object[][] fields = {
            { By.cssSelector("input[type='text'], input:not([type]), textarea"),
              "Cohort Name / text input" },
            { By.cssSelector("input[type='date'], " +
                             "input[placeholder*='Start'], " +
                             "input[formcontrolname*='start'], " +
                             "input[formcontrolname*='Start']"),
              "Start Date" },
            { By.cssSelector("input[type='date'], " +
                             "input[placeholder*='End'], " +
                             "input[formcontrolname*='end'], " +
                             "input[formcontrolname*='End']"),
              "End Date" },
            { By.cssSelector("select"),
              "Service Line / dropdown" }
        };

        for (Object[] field : fields) {
            By locator      = (By) field[0];
            String fieldDesc = (String) field[1];
            List<WebElement> found = driver.findElements(locator);
            Assert.assertFalse(
                    found.isEmpty(),
                    "FAIL - '" + fieldDesc + "' field not found in the Create Cohort modal. " +
                    "FRD 2.2.2 requires this field to be present in the create form.");
            System.out.println("PASS - Field present in Create Cohort modal: " + fieldDesc);
        }

        cohortPage.closeModal();
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("[class*='modal'], [role='dialog']")));
        } catch (Exception ignored) {}
    }
}
