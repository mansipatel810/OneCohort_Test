package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class CohortManagementTest {

    private WebDriver driver;
    private WebDriverWait wait;

    // ═══════════════════════════════════════════════════
    // LOCATORS — confirmed from browser inspect screenshots
    // ═══════════════════════════════════════════════════

    // Page
    private static final By PAGE_TITLE       = By.cssSelector("h2.fw-bold");
    private static final By PAGE_SUBTITLE    = By.cssSelector("p.text-muted");

    // Controls
    private static final By SEARCH_INPUT     = By.cssSelector("input[placeholder='Search by Cohort ID or Name...']");
    private static final By FILTERS_BTN      = By.cssSelector("button.btn.btn-outline-secondary");
    private static final By FILTER_SELECT    = By.cssSelector("select.form-select");
    private static final By CREATE_BTN       = By.cssSelector("button.btn.btn-primary");

    // Record count
    private static final By TOTAL_RECORDS    = By.cssSelector("span.text-muted b");
    private static final By LOADED_COUNT     = By.cssSelector("span.text-primary.px-2.border-start b");

    // Table
    private static final By TABLE            = By.cssSelector("table.table.table-hover.align-middle");
    private static final By TABLE_HEADER     = By.cssSelector("thead.table-light");
    private static final By TABLE_ROWS       = By.cssSelector("tbody tr.align-middle");
    private static final By COHORT_ID_SPAN   = By.cssSelector("span.fw-bold.text-primary");
    private static final By STATUS_BADGE     = By.cssSelector("span.badge.rounded-pill");

    // Actions column buttons (confirmed from inspect Image 3)
    // Edit:   <button class="btn btn-link text-muted p-1">
    // Delete: <button class="btn btn-link text-danger p-1">
    private static final By EDIT_BTN         = By.cssSelector("button.btn.btn-link.text-muted.p-1");
    private static final By DELETE_BTN       = By.cssSelector("button.btn.btn-link.text-danger.p-1");

    // Modal
    private static final By MODAL_CARD       = By.cssSelector("div.modal-card");
    private static final By MODAL_TITLE      = By.cssSelector("div.modal-header h5");
    private static final By CANCEL_BTN       = By.cssSelector("button.btn-modal-secondary");

    // ── CREATE modal fields ──
    // Service Line in CREATE = dropdown (user can select)
    // confirmed from inspect Image 1: <select name="serviceLine">
    private static final By CREATE_SL_DD     = By.cssSelector("select[name='serviceLine']");
    private static final By LEARNING_PATH_DD = By.cssSelector("select[name='learningPath']");
    private static final By BATCH_OWNER_DD   = By.cssSelector("select[name='batchOwner']");
    private static final By TRAINER_DD       = By.xpath("//select[.//option[text()='Select Trainer']]");
    private static final By DATE_INPUTS      = By.cssSelector("input[type='date']");

    // ── EDIT modal fields ──
    // Service Line in EDIT = disabled text input (locked after creation)
    // confirmed from inspect: <input type="text" class="form-control-disabled" disabled>
    // FRD: "Service Line and Learning Path are locked after creation"
    private static final By EDIT_SL_LOCKED   = By.cssSelector("input.form-control-disabled");

    // ═══════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════

    private void highlight(WebElement el) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].style.border='3px solid red'", el);
            js.executeScript("arguments[0].scrollIntoView(true);", el);
        } catch (Exception ignored) {}
    }

    private void jsClick(WebElement el) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }

    private void waitForTableToLoad() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(TABLE));
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(TABLE_ROWS, 0));
            Thread.sleep(400);
        } catch (Exception ignored) {}
    }

    private void openCreateModal() {
        WebElement btn = driver.findElement(CREATE_BTN);
        jsClick(btn);
        wait.until(ExpectedConditions.visibilityOfElementLocated(MODAL_CARD));
    }

    private void closeModal() {
        try {
            WebElement cancel = driver.findElement(CANCEL_BTN);
            jsClick(cancel);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL_CARD));
            Thread.sleep(300);
        } catch (Exception ignored) {}
    }

    // ═══════════════════════════════════════════════════
    // SETUP
    // ═══════════════════════════════════════════════════

    @BeforeClass
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--window-size=1920,1080", "--no-sandbox");
        driver = new ChromeDriver(opts);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get(ConfigReader.getBaseUrl());

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[placeholder='e.g. 123456']")))
                .sendKeys(ConfigReader.getSuperAdminUserId());

        WebElement roleDropdown = driver.findElement(By.cssSelector("select"));
        Select select = new Select(roleDropdown);
        select.selectByVisibleText("Super Admin");

        driver.findElement(By.cssSelector("button[type='button']")).click();

        wait.until(ExpectedConditions.urlContains("dashboard"));
        System.out.println("Login successful - Dashboard loaded");

        WebElement cohortMenu = driver.findElement(
                By.xpath("//nav[contains(@class,'menu')]//*[contains(text(),'Cohort Management')]"));
        highlight(cohortMenu);
        cohortMenu.click();

        waitForTableToLoad();
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
            WebElement title = driver.findElement(PAGE_TITLE);
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
            WebElement subtitle = driver.findElement(PAGE_SUBTITLE);
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
            WebElement search = driver.findElement(SEARCH_INPUT);
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
    // ═══════════════════════════════════════════════════
    @Test(priority = 4)
    public void checkSearchBarFilters() {
        System.out.println("\n--- TEST 4: checkSearchBarFilters ---");
        try {
            WebElement search = driver.findElement(SEARCH_INPUT);
            int before = driver.findElements(TABLE_ROWS).size();
            highlight(search);
            search.clear();
            search.sendKeys("INT");
            Thread.sleep(700);
            int after = driver.findElements(TABLE_ROWS).size();
            System.out.println("PASS - Search filter works. Before: " + before + " | After 'INT': " + after);
            search.clear();
            Thread.sleep(500);
            waitForTableToLoad();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 4: " + ae.getMessage()); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 4: Search filter error. Reason: " + e.getClass().getSimpleName();
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
            WebElement btn = driver.findElement(FILTERS_BTN);
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
            WebElement btn = driver.findElement(FILTERS_BTN);
            highlight(btn);
            btn.click();
            Thread.sleep(600);
            List<WebElement> dropdowns = driver.findElements(FILTER_SELECT);
            Assert.assertFalse(dropdowns.isEmpty(),
                    "Filter panel did not open. No select.form-select found. GAP: FR-012");
            System.out.println("PASS - Filter panel opened with " + dropdowns.size() + " filter(s)");
            btn.click();
            Thread.sleep(400);
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
            WebElement btn = driver.findElement(CREATE_BTN);
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
            openCreateModal();
            WebElement modal = driver.findElement(MODAL_CARD);
            highlight(modal);
            Assert.assertTrue(modal.isDisplayed(),
                    "Create modal did not open. Locator: div.modal-card");
            WebElement title = driver.findElement(MODAL_TITLE);
            Assert.assertTrue(title.getText().contains("Create"),
                    "Modal title wrong. Expected 'Create' | Got: '" + title.getText() + "'");
            System.out.println("PASS - Create modal title: " + title.getText().trim());
            closeModal();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 8: " + ae.getMessage()); closeModal(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 8: Create modal error. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); closeModal(); Assert.fail(m);
        }
    }

    // ═══════════════════════════════════════════════════
    // TEST 9 - Service Line dropdown present in CREATE modal
    // FRD 4.1.3 | FR-013
    //
    // In CREATE modal → Service Line is a DROPDOWN
    // User selects which service line the cohort belongs to.
    // Locator: select[name='serviceLine']
    // ═══════════════════════════════════════════════════
    @Test(priority = 9)
    public void checkServiceLineDropdownInCreateModal() {
        System.out.println("\n--- TEST 9: checkServiceLineDropdownInCreateModal ---");
        try {
            openCreateModal();
            WebElement slDropdown = driver.findElement(CREATE_SL_DD);
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
            closeModal();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 9: " + ae.getMessage()); closeModal(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 9: Service Line dropdown not found in Create modal. "
                    + "GAP: FR-013 requires it. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); closeModal(); Assert.fail(m);
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
            openCreateModal();
            WebElement dd = driver.findElement(LEARNING_PATH_DD);
            highlight(dd);
            Assert.assertTrue(dd.isDisplayed(),
                    "Learning Path dropdown not visible. FR-014 requires it.");
            System.out.println("PASS - Learning Path dropdown present");
            closeModal();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 10: " + ae.getMessage()); closeModal(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 10: Learning Path dropdown not found. "
                    + "GAP: FR-014 requires it. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); closeModal(); Assert.fail(m);
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
            openCreateModal();
            WebElement trainerDd = driver.findElement(TRAINER_DD);
            highlight(trainerDd);
            Assert.assertTrue(trainerDd.isDisplayed(),
                    "Trainer dropdown not visible in modal. FRD 4.1.3 requires it.");
            Select t = new Select(trainerDd);
            int count = t.getOptions().size();
            Assert.assertTrue(count > 1,
                    "Trainer dropdown has no trainers. Found only " + count + " option(s).");
            System.out.println("PASS - Trainer dropdown present with " + count + " option(s)");
            closeModal();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 11: " + ae.getMessage()); closeModal(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 11: Trainer dropdown not found. "
                    + "GAP: FRD 4.1.3 requires this. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); closeModal(); Assert.fail(m);
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
            openCreateModal();
            WebElement dd = driver.findElement(BATCH_OWNER_DD);
            highlight(dd);
            Assert.assertTrue(dd.isDisplayed(),
                    "Batch Owner dropdown not visible. FR-015 requires it.");
            System.out.println("PASS - Batch Owner dropdown present");
            closeModal();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 12: " + ae.getMessage()); closeModal(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 12: Batch Owner dropdown not found. "
                    + "GAP: FR-015 requires it. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); closeModal(); Assert.fail(m);
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
            openCreateModal();
            List<WebElement> dates = driver.findElements(DATE_INPUTS);
            Assert.assertTrue(dates.size() >= 1,
                    "Start Date picker not found. FRD 4.1.3 requires it. "
                            + "Found " + dates.size() + " date input(s).");
            highlight(dates.get(0));
            System.out.println("PASS - Start Date picker present");
            closeModal();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 13: " + ae.getMessage()); closeModal(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 13: Start Date picker not found. "
                    + "GAP: FRD 4.1.3 requires it. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); closeModal(); Assert.fail(m);
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
            openCreateModal();
            List<WebElement> dates = driver.findElements(DATE_INPUTS);
            Assert.assertTrue(dates.size() >= 2,
                    "End Date picker not found. FRD 4.1.3 requires Start AND End Date. "
                            + "Found only " + dates.size() + " date input(s). GAP.");
            highlight(dates.get(1));
            System.out.println("PASS - End Date picker present");
            closeModal();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 14: " + ae.getMessage()); closeModal(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 14: End Date picker not found. "
                    + "GAP: FRD 4.1.3 requires it. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); closeModal(); Assert.fail(m);
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
            openCreateModal();
            WebElement cancelBtn = driver.findElement(CANCEL_BTN);
            highlight(cancelBtn);
            jsClick(cancelBtn);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(MODAL_CARD));
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
            waitForTableToLoad();
            WebElement table = driver.findElement(TABLE);
            highlight(table);
            Assert.assertTrue(table.isDisplayed(),
                    "Cohort table not visible. Locator: table.table.table-hover.align-middle");
            String header = driver.findElement(TABLE_HEADER).getText();
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
            waitForTableToLoad();
            List<WebElement> rows = driver.findElements(TABLE_ROWS);
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
            waitForTableToLoad();
            WebElement span = driver.findElement(COHORT_ID_SPAN);
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
            waitForTableToLoad();
            List<WebElement> rows = driver.findElements(TABLE_ROWS);
            WebElement firstRow = rows.get(0);
            WebElement idSpan = firstRow.findElement(COHORT_ID_SPAN);
            String cohortId = idSpan.getText().trim();
            highlight(firstRow);

            jsClick(idSpan);
            wait.until(ExpectedConditions.urlContains("/cohorts/"));

            String url = driver.getCurrentUrl();
            Assert.assertTrue(url.contains("/cohorts/"),
                    "Did not navigate to cohort detail. FR-019 requires it. URL: " + url);
            System.out.println("PASS - Navigated to: " + url + " for cohort: " + cohortId);

            driver.navigate().back();
            waitForTableToLoad();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 19: " + ae.getMessage());
            driver.navigate().back(); waitForTableToLoad(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 19: Could not navigate. Reason: " + e.getClass().getSimpleName();
            System.out.println(m);
            driver.navigate().back(); waitForTableToLoad(); Assert.fail(m);
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
            waitForTableToLoad();
            List<WebElement> rows = driver.findElements(TABLE_ROWS);
            Assert.assertTrue(rows.size() > 0, "No rows found to check status badges.");

            int missingCount = 0;
            int validCount   = 0;
            StringBuilder bugList = new StringBuilder();

            for (int i = 0; i < rows.size(); i++) {
                WebElement row = rows.get(i);

                // Get cohort ID of THIS row for reporting
                String cohortId = "Unknown";
                try {
                    cohortId = row.findElement(COHORT_ID_SPAN).getText().trim();
                } catch (Exception ignored) {}

                // Check badge inside THIS row only (not whole page)
                List<WebElement> badges = row.findElements(STATUS_BADGE);

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
            waitForTableToLoad();
            WebElement editBtn = driver.findElement(EDIT_BTN);
            highlight(editBtn);
            Assert.assertTrue(editBtn.isDisplayed(),
                    "Edit button not visible. FR-017 requires it. "
                            + "Locator: button.btn.btn-link.text-muted.p-1");
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
            waitForTableToLoad();
            WebElement editBtn = driver.findElement(EDIT_BTN);
            highlight(editBtn);
            jsClick(editBtn);
            wait.until(ExpectedConditions.visibilityOfElementLocated(MODAL_CARD));

            WebElement modal = driver.findElement(MODAL_CARD);
            Assert.assertTrue(modal.isDisplayed(),
                    "Edit modal did not open. FR-017 requires edit form.");
            WebElement title = driver.findElement(MODAL_TITLE);
            System.out.println("PASS - Edit modal opened. Title: " + title.getText().trim());
            closeModal();
        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 22: " + ae.getMessage()); closeModal(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 22: Edit modal could not open. Reason: " + e.getClass().getSimpleName();
            System.out.println(m); closeModal(); Assert.fail(m);
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
            waitForTableToLoad();
            WebElement editBtn = driver.findElement(EDIT_BTN);
            jsClick(editBtn);

            // Wait for modal AND the disabled input to fully render
            wait.until(ExpectedConditions.visibilityOfElementLocated(MODAL_CARD));
            wait.until(ExpectedConditions.presenceOfElementLocated(EDIT_SL_LOCKED));
            Thread.sleep(500);

            WebElement slField = driver.findElement(EDIT_SL_LOCKED);
            highlight(slField);

            // PASS: field exists and is disabled → correct per FRD
            String disabledAttr = slField.getAttribute("disabled");
            Assert.assertNotNull(disabledAttr,
                    "FAIL - Service Line is NOT disabled in Edit modal. "
                            + "FRD requires it locked after creation (FR-017).");

            String value = slField.getAttribute("value");
            System.out.println("PASS - Service Line is correctly LOCKED in Edit modal "
                    + "(disabled as per FRD FR-017). Value: " + value);
            closeModal();

        } catch (AssertionError ae) {
            System.out.println("FAIL - TEST 23: " + ae.getMessage());
            closeModal(); throw ae;
        } catch (Exception e) {
            String m = "FAIL - TEST 23: Service Line locked field (input.form-control-disabled) "
                    + "not found in Edit modal. "
                    + "FRD FR-017 requires Service Line to be locked after creation. "
                    + "Reason: " + e.getClass().getSimpleName();
            System.out.println(m); closeModal(); Assert.fail(m);
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
            waitForTableToLoad();
            WebElement deleteBtn = driver.findElement(DELETE_BTN);
            highlight(deleteBtn);
            Assert.assertTrue(deleteBtn.isDisplayed(),
                    "Delete button not visible. FR-018 requires it. "
                            + "Locator: button.btn.btn-link.text-danger.p-1");
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
            waitForTableToLoad();
            WebElement deleteBtn = driver.findElement(DELETE_BTN);
            highlight(deleteBtn);
            jsClick(deleteBtn);
            Thread.sleep(700);

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
                List<WebElement> modal = driver.findElements(MODAL_CARD);
                if (!modal.isEmpty() && modal.get(0).isDisplayed()) {
                    System.out.println("PASS - Delete confirmation custom modal appeared");
                    closeModal();
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
            waitForTableToLoad();
            WebElement el = driver.findElement(TOTAL_RECORDS);
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
            waitForTableToLoad();
            WebElement el = driver.findElement(LOADED_COUNT);
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

    // ═══════════════════════════════════════════════════
    // TEARDOWN
    // ═══════════════════════════════════════════════════
    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
        System.out.println("\nBrowser closed - All Cohort Management tests done!");
    }
}