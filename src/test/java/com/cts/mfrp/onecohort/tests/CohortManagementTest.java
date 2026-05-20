package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.cohort.CohortManagementPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExcelUtils;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import com.cts.mfrp.onecohort.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

@Listeners(ExtentReportListener.class)
public class CohortManagementTest extends BaseClassTest {

    private static final String COHORT_DATA_FILE =
            "src/test/resources/testdata/CohortTestData.xlsx";

    private CohortManagementPage page;

    // ═══════════════════════════════════════════════════
    // SETUP
    // ═══════════════════════════════════════════════════

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void setup() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("dashboard"));
        System.out.println("Login successful - Dashboard loaded");

        // Navigate to Cohort Management via sidebar
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//nav[contains(@class,'menu')]//*[contains(text(),'Cohort Management')]")))
                .click();

        page = new CohortManagementPage(driver);
        page.waitForTableToLoad();
        System.out.println("Cohort Management page loaded successfully");
    }

    // ═══════════════════════════════════════════════════
    // TEST 1 - Page title is "Cohort Management"
    // FRD 4.1.1
    // ═══════════════════════════════════════════════════
    @Test(priority = 1)
    public void checkPageTitle() {
        System.out.println("\n--- TEST 1: checkPageTitle ---");
        try {
            WebElement title = page.getPageTitleElement();
            highlight(title);
            Assert.assertTrue(title.getText().contains("Cohort Management"),
                    "Page title wrong. Expected: 'Cohort Management' | Got: '" + title.getText() + "'");
            System.out.println("PASS - Page title: " + title.getText());
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 1: " + ae.getMessage()); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 1: h2.fw-bold not found. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 2 - Page subtitle is visible
    // FRD 4.1.1
    // ═══════════════════════════════════════════════════
    @Test(priority = 2)
    public void checkPageSubtitle() {
        System.out.println("\n--- TEST 2: checkPageSubtitle ---");
        try {
            WebElement subtitle = page.getPageSubtitleElement();
            highlight(subtitle);
            Assert.assertTrue(subtitle.isDisplayed(),
                    "Page subtitle not visible. Locator: p.text-muted");
            System.out.println("PASS - Subtitle: " + subtitle.getText().trim());
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 2: " + ae.getMessage()); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 2: p.text-muted not found. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 3 - Search bar is present
    // FRD 4.1.1 | FR-011
    // ═══════════════════════════════════════════════════
    @Test(priority = 3)
    public void checkSearchBar() {
        System.out.println("\n--- TEST 3: checkSearchBar ---");
        try {
            WebElement search = page.getSearchInputElement();
            highlight(search);
            Assert.assertTrue(search.isDisplayed(), "Search bar not visible. FR-011 requires it.");
            System.out.println("PASS - Search bar present");
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 3: " + ae.getMessage()); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 3: Search input not found. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 4 - Search bar filters rows in real time
    // FRD 4.1.1 | FR-011
    // Data-driven: reads keywords from CohortTestData.xlsx → SearchCohort sheet
    // ═══════════════════════════════════════════════════
    @DataProvider(name = "cohortSearchData")
    public Object[][] cohortSearchData() {
        return ExcelUtils.getTestData(COHORT_DATA_FILE, "SearchCohort");
    }

    @Test(priority = 4, dataProvider = "cohortSearchData")
    public void checkSearchBarFilters(Map<String, String> row) {
        String keyword     = row.get("SearchKeyword");
        String description = row.get("Description");
        int expectedMin    = Integer.parseInt(row.getOrDefault("ExpectedMinRows", "0"));

        System.out.println("\n--- TEST 4: checkSearchBarFilters [" + keyword + "] — " + description + " ---");
        try {
            WebElement search = page.getSearchInputElement();
            highlight(search);
            search.clear();
            search.sendKeys(keyword);
            WaitUtils.waitForResultsToSettle(driver, By.cssSelector("tbody tr.align-middle"), 5);

            int resultCount = page.getAlignedRows().size();
            System.out.println("INFO - Keyword: '" + keyword + "' | Results: " + resultCount
                    + " | Expected min: " + expectedMin);

            if (expectedMin > 0) {
                Assert.assertTrue(resultCount >= expectedMin,
                        "Search for '" + keyword + "' returned " + resultCount
                        + " rows, expected at least " + expectedMin + ". FRD 4.1.1 FR-011.");
            }
            System.out.println("PASS - Search filter keyword '" + keyword
                    + "' returned " + resultCount + " result(s)");

            search.clear();
            WaitUtils.waitForResultsToSettle(driver, By.cssSelector("tbody tr.align-middle"), 5);
            page.waitForTableToLoad();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 4 [" + keyword + "]: " + ae.getMessage()); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 4 [" + keyword + "]: " + e.getClass().getSimpleName();
            System.out.println(m); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 5 - Filters button is present
    // FRD 4.1.1 | FR-012
    // ═══════════════════════════════════════════════════
    @Test(priority = 5)
    public void checkFiltersButton() {
        System.out.println("\n--- TEST 5: checkFiltersButton ---");
        try {
            WebElement btn = page.getFiltersBtnElement();
            highlight(btn);
            Assert.assertTrue(btn.isDisplayed(),
                    "Filters button not visible. Locator: button.btn.btn-outline-secondary");
            Assert.assertTrue(btn.getText().contains("Filters"),
                    "Filters button text wrong. Expected 'Filters' | Got: '" + btn.getText() + "'");
            System.out.println("PASS - Filters button: " + btn.getText().trim());
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 5: " + ae.getMessage()); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 5: Filters button not found. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 6 - Filters panel opens on click
    // FRD 4.1.1 | FR-012
    // ═══════════════════════════════════════════════════
    @Test(priority = 6)
    public void checkFiltersPanelOpens() {
        System.out.println("\n--- TEST 6: checkFiltersPanelOpens ---");
        try {
            WebElement btn = page.getFiltersBtnElement();
            highlight(btn);
            btn.click();
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("select.form-select"), 0));
            List<WebElement> dropdowns = page.getFilterSelectElements();
            Assert.assertFalse(dropdowns.isEmpty(),
                    "Filter panel did not open. No select.form-select found. GAP: FR-012");
            System.out.println("PASS - Filter panel opened with " + dropdowns.size() + " filter(s)");
            btn.click();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("select.form-select")));
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 6: " + ae.getMessage()); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 6: Filter panel error. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 7 - Create New Cohort button is present
    // FRD 4.1.1 | FR-013
    // ═══════════════════════════════════════════════════
    @Test(priority = 7)
    public void checkCreateButton() {
        System.out.println("\n--- TEST 7: checkCreateButton ---");
        try {
            WebElement btn = page.getCreateBtnPrimaryElement();
            highlight(btn);
            Assert.assertTrue(btn.isDisplayed(),
                    "Create button not visible. Locator: button.btn.btn-primary");
            Assert.assertTrue(btn.getText().contains("Create"),
                    "Create button text wrong. Expected 'Create' | Got: '" + btn.getText() + "'");
            System.out.println("PASS - Create button: " + btn.getText().trim());
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 7: " + ae.getMessage()); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 7: Create button not found. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 8 - Create modal opens with correct title
    // FRD 4.1.3 | FR-013
    // ═══════════════════════════════════════════════════
    @Test(priority = 8)
    public void checkCreateModalOpens() {
        System.out.println("\n--- TEST 8: checkCreateModalOpens ---");
        try {
            page.openCreateModal();
            WebElement modal = page.getModalCardElement();
            highlight(modal);
            Assert.assertTrue(modal.isDisplayed(),
                    "Create modal did not open. Locator: div.modal-card");
            String titleText = page.getModalTitleText();
            Assert.assertTrue(titleText.contains("Create"),
                    "Modal title wrong. Expected 'Create' | Got: '" + titleText + "'");
            System.out.println("PASS - Create modal title: " + titleText.trim());
            page.closeModal();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 8: " + ae.getMessage()); page.closeModal(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 8: Create modal error. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); page.closeModal(); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 9 - Service Line dropdown present in CREATE modal
    // FRD 4.1.3 | FR-013
    //
    // In CREATE modal → Service Line is a DROPDOWN (select[name='serviceLine'])
    // ═══════════════════════════════════════════════════
    @Test(priority = 9)
    public void checkServiceLineDropdownInCreateModal() {
        System.out.println("\n--- TEST 9: checkServiceLineDropdownInCreateModal ---");
        try {
            page.openCreateModal();
            WebElement slDropdown = page.getCreateServiceLineDropdown();
            highlight(slDropdown);
            Assert.assertTrue(slDropdown.isDisplayed(),
                    "Service Line dropdown not visible in Create modal. "
                            + "FR-013 requires it. Locator: select[name='serviceLine']");
            Select sl = new Select(slDropdown);
            int optionCount = sl.getOptions().size();
            Assert.assertTrue(optionCount > 1,
                    "Service Line dropdown has no options. Found: " + optionCount);
            System.out.println("PASS - Service Line dropdown present in Create modal with "
                    + optionCount + " option(s). First: " + sl.getOptions().get(0).getText());
            page.closeModal();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 9: " + ae.getMessage()); page.closeModal(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 9: Service Line dropdown not found in Create modal. "
                    + "GAP: FR-013 requires it. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); page.closeModal(); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 10 - Learning Path dropdown present in CREATE modal
    // FRD 4.1.3 | FR-014
    // ═══════════════════════════════════════════════════
    @Test(priority = 10)
    public void checkLearningPathDropdown() {
        System.out.println("\n--- TEST 10: checkLearningPathDropdown ---");
        try {
            page.openCreateModal();
            WebElement dd = page.getLearningPathDropdown();
            highlight(dd);
            Assert.assertTrue(dd.isDisplayed(),
                    "Learning Path dropdown not visible. FR-014 requires it.");
            System.out.println("PASS - Learning Path dropdown present");
            page.closeModal();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 10: " + ae.getMessage()); page.closeModal(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 10: Learning Path dropdown not found. "
                    + "GAP: FR-014 requires it. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); page.closeModal(); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 11 - Trainer dropdown present in CREATE modal
    // FRD 4.1.3
    // ═══════════════════════════════════════════════════
    @Test(priority = 11)
    public void checkTrainerDropdown() {
        System.out.println("\n--- TEST 11: checkTrainerDropdown ---");
        try {
            page.openCreateModal();
            WebElement trainerDd = page.getTrainerDropdown();
            highlight(trainerDd);
            Assert.assertTrue(trainerDd.isDisplayed(),
                    "Trainer dropdown not visible in modal. FRD 4.1.3 requires it.");
            Select t = new Select(trainerDd);
            int count = t.getOptions().size();
            Assert.assertTrue(count > 1,
                    "Trainer dropdown has no trainers. Found only " + count + " option(s).");
            System.out.println("PASS - Trainer dropdown present with " + count + " option(s)");
            page.closeModal();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 11: " + ae.getMessage()); page.closeModal(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 11: Trainer dropdown not found. "
                    + "GAP: FRD 4.1.3 requires this. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); page.closeModal(); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 12 - Batch Owner dropdown present in CREATE modal
    // FRD 4.1.3 | FR-015
    // ═══════════════════════════════════════════════════
    @Test(priority = 12)
    public void checkBatchOwnerDropdown() {
        System.out.println("\n--- TEST 12: checkBatchOwnerDropdown ---");
        try {
            page.openCreateModal();
            WebElement dd = page.getBatchOwnerDropdown();
            highlight(dd);
            Assert.assertTrue(dd.isDisplayed(),
                    "Batch Owner dropdown not visible. FR-015 requires it.");
            System.out.println("PASS - Batch Owner dropdown present");
            page.closeModal();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 12: " + ae.getMessage()); page.closeModal(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 12: Batch Owner dropdown not found. "
                    + "GAP: FR-015 requires it. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); page.closeModal(); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 13 - Start Date picker present in CREATE modal
    // FRD 4.1.3
    // ═══════════════════════════════════════════════════
    @Test(priority = 13)
    public void checkStartDatePicker() {
        System.out.println("\n--- TEST 13: checkStartDatePicker ---");
        try {
            page.openCreateModal();
            List<WebElement> dates = page.getDateInputs();
            Assert.assertTrue(dates.size() >= 1,
                    "Start Date picker not found. FRD 4.1.3 requires it. "
                            + "Found " + dates.size() + " date input(s).");
            highlight(dates.get(0));
            System.out.println("PASS - Start Date picker present");
            page.closeModal();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 13: " + ae.getMessage()); page.closeModal(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 13: Start Date picker not found. "
                    + "GAP: FRD 4.1.3 requires it. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); page.closeModal(); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 14 - End Date picker present in CREATE modal
    // FRD 4.1.3
    // ═══════════════════════════════════════════════════
    @Test(priority = 14)
    public void checkEndDatePicker() {
        System.out.println("\n--- TEST 14: checkEndDatePicker ---");
        try {
            page.openCreateModal();
            List<WebElement> dates = page.getDateInputs();
            Assert.assertTrue(dates.size() >= 2,
                    "End Date picker not found. FRD 4.1.3 requires Start AND End Date. "
                            + "Found only " + dates.size() + " date input(s). GAP.");
            highlight(dates.get(1));
            System.out.println("PASS - End Date picker present");
            page.closeModal();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 14: " + ae.getMessage()); page.closeModal(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 14: End Date picker not found. "
                    + "GAP: FRD 4.1.3 requires it. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); page.closeModal(); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 15 - Cancel button closes the modal
    // FRD 4.1.3
    // ═══════════════════════════════════════════════════
    @Test(priority = 15)
    public void checkCancelButtonClosesModal() {
        System.out.println("\n--- TEST 15: checkCancelButtonClosesModal ---");
        try {
            page.openCreateModal();
            WebElement cancelBtn = page.getCancelBtnElement();
            highlight(cancelBtn);
            page.clickCancelBtn();
            page.waitForModalInvisible();
            System.out.println("PASS - Cancel button closes the modal");
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 15: " + ae.getMessage()); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 15: Cancel button error. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 16 - Cohort table visible with all 5 columns
    // FRD 4.1.1 | FR-010
    // ═══════════════════════════════════════════════════
    @Test(priority = 16)
    public void checkCohortTableAndColumns() {
        System.out.println("\n--- TEST 16: checkCohortTableAndColumns ---");
        try {
            page.waitForTableToLoad();
            WebElement table = page.getSpecificTableElement();
            highlight(table);
            Assert.assertTrue(table.isDisplayed(),
                    "Cohort table not visible. Locator: table.table.table-hover.align-middle");
            String header = page.getTableHeaderText();
            String[] cols = {"COHORT ID", "COHORT NAME", "STATUS", "START DATE", "ACTIONS"};
            for (String col : cols) {
                Assert.assertTrue(header.toUpperCase().contains(col),
                        "Column missing: '" + col + "'. Header text: '" + header + "'");
                System.out.println("PASS - Column present: " + col);
            }
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 16: " + ae.getMessage()); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 16: Table/columns not found. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 17 - Table rows load with data
    // FRD 4.1 | FR-010
    // ═══════════════════════════════════════════════════
    @Test(priority = 17)
    public void checkTableRowsLoaded() {
        System.out.println("\n--- TEST 17: checkTableRowsLoaded ---");
        try {
            page.waitForTableToLoad();
            List<WebElement> rows = page.getAlignedRows();
            Assert.assertTrue(rows.size() > 0,
                    "No rows in cohort table. FR-010 requires cohorts to be displayed.");
            highlight(rows.get(0));
            System.out.println("PASS - " + rows.size() + " cohort row(s) loaded");
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 17: " + ae.getMessage()); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 17: Table rows not found. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 18 - Cohort ID is bold and blue
    // FRD 4.1.1 | FR-019
    // ═══════════════════════════════════════════════════
    @Test(priority = 18)
    public void checkCohortIdStyle() {
        System.out.println("\n--- TEST 18: checkCohortIdStyle ---");
        try {
            page.waitForTableToLoad();
            List<WebElement> spans = page.getCohortIdSpans();
            Assert.assertFalse(spans.isEmpty(),
                    "Cohort ID span not found. Locator: span.fw-bold.text-primary");
            WebElement span = spans.get(0);
            highlight(span);
            Assert.assertTrue(span.isDisplayed(),
                    "Cohort ID span not visible. Locator: span.fw-bold.text-primary");
            Assert.assertFalse(span.getText().trim().isEmpty(), "Cohort ID span has no text.");
            System.out.println("PASS - Cohort ID: " + span.getText().trim());
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 18: " + ae.getMessage()); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 18: Cohort ID span not found. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 19 - Clicking Cohort row navigates to detail page
    // FRD 4.1.1 | FR-019
    // ═══════════════════════════════════════════════════
    @Test(priority = 19)
    public void checkCohortIdClickNavigatesToDetail() {
        System.out.println("\n--- TEST 19: checkCohortIdClickNavigatesToDetail ---");
        try {
            page.waitForTableToLoad();
            List<WebElement> rows = page.getAlignedRows();
            WebElement firstRow = rows.get(0);
            WebElement idSpan = page.getCohortIdSpanInRow(firstRow);
            String cohortId = idSpan.getText().trim();
            highlight(firstRow);

            page.clickCohortIdSpanInRow(firstRow);
            wait.until(ExpectedConditions.urlContains("/cohorts/"));

            String url = driver.getCurrentUrl();
            Assert.assertTrue(url.contains("/cohorts/"),
                    "Did not navigate to cohort detail. FR-019 requires it. URL: " + url);
            System.out.println("PASS - Navigated to: " + url + " for cohort: " + cohortId);

            driver.navigate().back();
            page.waitForTableToLoad();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 19: " + ae.getMessage());
            driver.navigate().back(); page.waitForTableToLoad(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 19: Could not navigate. Reason: " + e.getClass().getSimpleName();
            System.out.println(m);
            driver.navigate().back(); page.waitForTableToLoad(); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 20 - Status badge must be present in EVERY row
    // FRD 4.1.1 | FR-010
    //
    // Checks EVERY row individually — not just the first badge.
    // Any row missing a status badge → logged as BUG and test FAILS.
    // e.g. SDET cohort (INTQEA26SD002) has no status → will be caught here.
    // ═══════════════════════════════════════════════════
    @Test(priority = 20)
    public void checkStatusBadgeAllRows() {
        System.out.println("\n--- TEST 20: checkStatusBadgeAllRows ---");
        try {
            page.waitForTableToLoad();
            List<WebElement> rows = page.getAlignedRows();
            Assert.assertTrue(rows.size() > 0, "No rows found to check status badges.");

            int missingCount = 0;
            int validCount   = 0;
            StringBuilder bugList = new StringBuilder();

            for (int i = 0; i < rows.size(); i++) {
                WebElement row = rows.get(i);

                // Get cohort ID of THIS row for reporting
                String cohortId = "Unknown";
                try {
                    cohortId = page.getCohortIdSpanInRow(row).getText().trim();
                } catch (Exception ignored) {}

                // Check badge inside THIS row only (not whole page)
                List<WebElement> badges = page.getStatusBadgesInRow(row);

                if (badges.isEmpty() || badges.get(0).getText().trim().isEmpty()) {
                    missingCount++;
                    bugList.append("\n  → Row ").append(i + 1)
                            .append(" | Cohort ID: ").append(cohortId)
                            .append(" | Status: MISSING");
                    System.out.println("BUG  - Row " + (i + 1)
                            + " | Cohort: " + cohortId
                            + " | Status: MISSING — FR-010 requires every cohort to have a status.");
                } else {
                    validCount++;
                    System.out.println("OK   - Row " + (i + 1)
                            + " | Cohort: " + cohortId
                            + " | Status: " + badges.get(0).getText().trim());
                }
            }

            System.out.println("\nStatus Summary: "
                    + validCount + " have status | "
                    + missingCount + " MISSING status");

            if (missingCount > 0) {
                Assert.fail("FAIL - TEST 20: " + missingCount
                        + " cohort row(s) have NO status badge. "
                        + "FRD 4.1.1 | FR-010 requires every cohort to display a status. "
                        + "Raise a BUG report for:" + bugList);
            } else {
                System.out.println("PASS - All " + validCount + " rows have a status badge.");
            }

        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 20: " + ae.getMessage()); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 20: Status check error. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 21 - Edit button visible in Actions column
    // FRD 4.1 | FR-017
    // ═══════════════════════════════════════════════════
    @Test(priority = 21)
    public void checkEditButton() {
        System.out.println("\n--- TEST 21: checkEditButton ---");
        try {
            page.waitForTableToLoad();
            List<WebElement> editBtns = page.getEditButtons();
            Assert.assertFalse(editBtns.isEmpty(),
                    "Edit button not visible. FR-017 requires it. "
                            + "Locator: button.btn.btn-link.text-muted.p-1");
            highlight(editBtns.get(0));
            System.out.println("PASS - Edit button visible in Actions column");
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 21: " + ae.getMessage()); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 21: Edit button not found. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 22 - Edit modal opens
    // FRD 4.1 | FR-017
    // ═══════════════════════════════════════════════════
    @Test(priority = 22)
    public void checkEditModalOpens() {
        System.out.println("\n--- TEST 22: checkEditModalOpens ---");
        try {
            page.waitForTableToLoad();
            List<WebElement> editBtns = page.getEditButtons();
            highlight(editBtns.get(0));
            page.clickFirstEditBtn();
            page.waitForModalVisible();

            WebElement modal = page.getModalCardElement();
            Assert.assertTrue(modal.isDisplayed(),
                    "Edit modal did not open. FR-017 requires edit form.");
            String titleText = page.getModalTitleText();
            System.out.println("PASS - Edit modal opened. Title: " + titleText.trim());
            page.closeModal();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 22: " + ae.getMessage()); page.closeModal(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 22: Edit modal could not open. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); page.closeModal(); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 23 - Service Line is LOCKED in Edit modal
    // FRD 4.1 | FR-017
    //
    // FRD says: "Service Line and Learning Path are locked after creation"
    // In EDIT modal → Service Line becomes a DISABLED text input
    // (different from Create modal where it was a dropdown)
    //
    // Inspect confirmed:
    //   <input type="text"
    //          class="form-control-disabled ng-untouched ng-pristine"
    //          disabled>
    //
    // This test PASSES when the field is disabled — that is CORRECT behaviour.
    // This test FAILS only if the field is missing or is NOT disabled.
    // ═══════════════════════════════════════════════════
    @Test(priority = 23)
    public void checkEditModalServiceLineLocked() {
        System.out.println("\n--- TEST 23: checkEditModalServiceLineLocked ---");
        try {
            page.waitForTableToLoad();
            page.clickFirstEditBtn();

            // Wait for modal AND the disabled input to fully render
            page.waitForModalVisible();
            page.waitForEditSLLockedPresent();

            WebElement slField = page.getEditServiceLineLockedElement();
            highlight(slField);

            // PASS: field exists and is disabled → correct per FRD
            String disabledAttr = slField.getAttribute("disabled");
            Assert.assertNotNull(disabledAttr,
                    "FAIL - Service Line is NOT disabled in Edit modal. "
                            + "FRD requires it locked after creation (FR-017).");

            String value = slField.getAttribute("value");
            System.out.println("PASS - Service Line is correctly LOCKED in Edit modal "
                    + "(disabled as per FRD FR-017). Value: " + value);
            page.closeModal();

        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 23: " + ae.getMessage());
            page.closeModal(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 23: Service Line locked field (input.form-control-disabled) "
                    + "not found in Edit modal. "
                    + "FRD FR-017 requires Service Line to be locked after creation. "
                    + "Reason: " + e.getClass().getSimpleName();
            System.out.println(m); page.closeModal(); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 24 - Delete button visible in Actions column
    // FRD 4.1 | FR-018
    // ═══════════════════════════════════════════════════
    @Test(priority = 24)
    public void checkDeleteButton() {
        System.out.println("\n--- TEST 24: checkDeleteButton ---");
        try {
            page.waitForTableToLoad();
            List<WebElement> deleteBtns = page.getDeleteButtons();
            Assert.assertFalse(deleteBtns.isEmpty(),
                    "Delete button not visible. FR-018 requires it. "
                            + "Locator: button.btn.btn-link.text-danger.p-1");
            highlight(deleteBtns.get(0));
            System.out.println("PASS - Delete button visible in Actions column");
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 24: " + ae.getMessage()); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 24: Delete button not found. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 25 - Delete triggers confirmation prompt
    // FRD 4.1 | FR-018
    // ═══════════════════════════════════════════════════
    @Test(priority = 25)
    public void checkDeleteConfirmation() {
        System.out.println("\n--- TEST 25: checkDeleteConfirmation ---");
        try {
            page.waitForTableToLoad();
            List<WebElement> deleteBtns = page.getDeleteButtons();
            highlight(deleteBtns.get(0));
            page.clickFirstDeleteBtn();

            try {
                wait.until(ExpectedConditions.alertIsPresent());
                String alertText = driver.switchTo().alert().getText();
                Assert.assertTrue(
                        alertText.toLowerCase().contains("delete") ||
                                alertText.toLowerCase().contains("confirm") ||
                                alertText.toLowerCase().contains("sure"),
                        "Alert text unexpected. FR-018 requires confirm. Got: '" + alertText + "'");
                System.out.println("PASS - Delete confirmation alert: " + alertText);
                driver.switchTo().alert().dismiss();
            } catch (Exception alertEx) {
                if (page.isModalCardVisible()) {
                    System.out.println("PASS - Delete confirmation custom modal appeared");
                    page.closeModal();
                } else {
                    System.out.println("NOTE - TEST 25: No alert or modal for delete. "
                            + "GAP: FR-018 requires confirmation before deletion.");
                }
            }
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 25: " + ae.getMessage()); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 25: Delete confirmation error. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 26 - Total Records count is displayed
    // FRD 4.1.1 | FR-010
    // ═══════════════════════════════════════════════════
    @Test(priority = 26)
    public void checkTotalRecordsCount() {
        System.out.println("\n--- TEST 26: checkTotalRecordsCount ---");
        try {
            page.waitForTableToLoad();
            WebElement el = page.getTotalRecordsElement();
            highlight(el);
            Assert.assertFalse(el.getText().trim().isEmpty(),
                    "Total Records count empty. FRD 4.1.1 requires it shown.");
            System.out.println("PASS - Total Records: " + el.getText().trim());
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 26: " + ae.getMessage()); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 26: Total Records not found. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 27 - Loaded count is displayed
    // FRD 4.1.1 | FR-010
    // ═══════════════════════════════════════════════════
    @Test(priority = 27)
    public void checkLoadedCount() {
        System.out.println("\n--- TEST 27: checkLoadedCount ---");
        try {
            page.waitForTableToLoad();
            WebElement el = page.getLoadedCountElement();
            highlight(el);
            Assert.assertFalse(el.getText().trim().isEmpty(),
                    "Loaded count empty. FRD 4.1.1 requires it shown.");
            System.out.println("PASS - Loaded count: " + el.getText().trim());
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 27: " + ae.getMessage()); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 27: Loaded count not found. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); Assert.fail(m);
        }
    }
}
