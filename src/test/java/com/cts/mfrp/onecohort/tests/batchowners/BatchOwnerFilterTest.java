package com.cts.mfrp.onecohort.tests.batchowners;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.batchowners.BatchOwnerPage;
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
 * Batch Owners / POC Filter Tests — FRD Section 2.4.1
 *
 * Scope: page navigation, Service Line dropdown presence, dependent
 *        Learning Path dropdown chain, and profile card count response
 *        to filter selection.
 *
 * FRD 2.4.1 defines a dependent dropdown chain:
 *   1. Select Service Line → Learning Path dropdown becomes available
 *   2. Select Learning Path → Batch Owner profile cards are filtered
 *
 * Login: Super Admin — all tests share one browser session.
 *
 * ── FRD Traceability ─────────────────────────────────────────────────────────
 * TC-BO-FILTER-001  FRD 2.4    Page navigable via "Batch Owners / POC" sidebar link
 * TC-BO-FILTER-002  FRD 2.4    "Batch Owners" heading is visible
 * TC-BO-FILTER-003  FRD 2.4.1  Service Line dropdown is present
 * TC-BO-FILTER-004  FRD 2.4.1  Service Line dropdown has ≥ 1 option (SRV-10001 etc.)
 * TC-BO-FILTER-005  FRD 2.4.1  Selecting a Service Line shows cards / unlocks next filter
 * TC-BO-FILTER-006  FRD 2.4.1  Learning Path dropdown appears after Service Line selection
 * TC-BO-FILTER-007  FRD 2.4.1  Selecting Learning Path filters profile cards
 * ─────────────────────────────────────────────────────────────────────────────
 */
@Listeners(ExtentReportListener.class)
public class BatchOwnerFilterTest extends BaseClassTest {

    private BatchOwnerPage batchOwnerPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void setup() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("super-admin"));

        // Navigate to "Batch Owners / POC" via sidebar
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//nav[contains(@class,'menu')]" +
                         "//*[contains(text(),'Batch Owner')]" +
                         " | //nav[contains(@class,'menu')]" +
                         "//*[contains(text(),'Batch Owners / POC')]"))).click();

        wait.until(ExpectedConditions.urlContains("batch"));
        batchOwnerPage = new BatchOwnerPage(driver);
        System.out.println("Setup complete — URL: " + driver.getCurrentUrl());
    }

    // -------------------------------------------------------
    // TC-BO-FILTER-001 — URL contains batch-owners segment
    // FRD 2.4 — Module must be accessible via sidebar link
    // -------------------------------------------------------
    @Test(priority = 1)
    public void verifyBatchOwnersPageUrl() {
        String url = driver.getCurrentUrl();
        Assert.assertTrue(
                url.contains("batch") || url.contains("poc"),
                "FAIL - URL does not contain a batch-owners/poc path segment. " +
                "FRD 2.4 requires this module to be accessible via the sidebar. " +
                "Actual URL: " + url);
        System.out.println("PASS - Batch Owners URL: " + url);
    }

    // -------------------------------------------------------
    // TC-BO-FILTER-002 — "Batch Owners / POC" heading is visible
    // FRD 2.4 — Module heading must identify the page
    // -------------------------------------------------------
    @Test(priority = 2)
    public void verifyPageHeadingVisible() {
        Assert.assertTrue(
                batchOwnerPage.isPageHeadingVisible(),
                "FAIL - 'Batch Owners' (or 'POC') heading NOT visible on the page. " +
                "FRD 2.4 requires a clear heading identifying this module.");
        WebElement heading = driver.findElement(By.xpath(
                "//*[self::h1 or self::h2 or self::h3]" +
                "[contains(normalize-space(),'Batch Owner') " +
                "or contains(normalize-space(),'POC')]"));
        highlight(heading);
        System.out.println("PASS - Page heading: " + heading.getText().trim());
    }

    // -------------------------------------------------------
    // TC-BO-FILTER-003 — Service Line dropdown is present
    // FRD 2.4.1 — First filter: Service Line (required before others)
    // -------------------------------------------------------
    @Test(priority = 3)
    public void verifyServiceLineDropdownPresent() {
        Assert.assertTrue(
                batchOwnerPage.isServiceLineDropdownVisible(),
                "FAIL - Service Line filter dropdown is NOT present. " +
                "FRD 2.4.1 requires a Service Line dropdown as the primary filter " +
                "on the Batch Owners / POC page.");
        WebElement sl = driver.findElement(By.cssSelector(
                "select[name='serviceLine'], select#serviceLineFilter, " +
                "select[formcontrolname='serviceLine'], " +
                "select[formcontrolname='serviceLineId']"));
        highlight(sl);
        System.out.println("PASS - Service Line dropdown is visible");
    }

    // -------------------------------------------------------
    // TC-BO-FILTER-004 — Service Line dropdown has ≥ 1 option
    // FRD 2.4.1 — Dropdown must be populated from the API
    //             (SRV-10001, SRV-10002, etc.)
    // -------------------------------------------------------
    @Test(priority = 4)
    public void verifyServiceLineDropdownOptions() {
        WebElement slDropdown = driver.findElement(By.cssSelector(
                "select[name='serviceLine'], select#serviceLineFilter, " +
                "select[formcontrolname='serviceLine'], " +
                "select[formcontrolname='serviceLineId']"));
        Select slSelect = new Select(slDropdown);
        List<WebElement> options = slSelect.getOptions();
        // Filter out placeholder "-- Select --" type entries
        long realOptions = options.stream()
                .filter(o -> !o.getText().trim().isEmpty()
                        && !o.getAttribute("value").trim().isEmpty())
                .count();
        Assert.assertTrue(
                realOptions >= 1,
                "FAIL - Service Line dropdown has no selectable options. " +
                "FRD 2.4.1 requires at least one Service Line (e.g. SRV-10001) to be available. " +
                "Check that the API is returning service line data.");
        System.out.println("PASS - Service Line dropdown has " + realOptions + " selectable option(s)");
        options.forEach(o -> {
            if (!o.getAttribute("value").trim().isEmpty())
                System.out.println("  Option: " + o.getText() + " [" + o.getAttribute("value") + "]");
        });
    }

    // -------------------------------------------------------
    // TC-BO-FILTER-005 — Selecting a Service Line loads/filters profile cards
    // FRD 2.4.1 — After selecting a Service Line, the batch owner list
    //             must update (show cards or unlock the next dropdown)
    // -------------------------------------------------------
    @Test(priority = 5)
    public void verifyServiceLineSelectionFiltersCards() {
        batchOwnerPage.selectServiceLine(ConfigReader.getValidServiceLineId());
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        // After selecting a service line, either profile cards appear OR
        // a dependent Learning Path dropdown appears (both are valid)
        boolean cardsAppeared = batchOwnerPage.areProfileCardsVisible();
        boolean nextDropdown  = batchOwnerPage.isLearningPathDropdownVisible();

        Assert.assertTrue(
                cardsAppeared || nextDropdown,
                "FAIL - Selecting Service Line '" + ConfigReader.getValidServiceLineId() +
                "' produced no response: no profile cards and no dependent dropdown appeared. " +
                "FRD 2.4.1 requires the page to respond to Service Line selection.");
        System.out.println("PASS - Service Line selection response: " +
                (cardsAppeared ? "profile cards visible" : "dependent dropdown appeared"));
    }

    // -------------------------------------------------------
    // TC-BO-FILTER-006 — Learning Path dropdown appears after Service Line
    // FRD 2.4.1 — Dependent dropdown chain: Service Line → Learning Path
    // NOTE: If the Learning Path dropdown is not implemented as a dependent
    //       dropdown (i.e. it appears at page load), this test passes regardless.
    //       If absent entirely after Service Line selection → FRD 2.4.1 GAP.
    // -------------------------------------------------------
    @Test(priority = 6)
    public void verifyLearningPathDropdownAfterServiceLine() {
        // Service Line was already selected in TC-BO-FILTER-005
        boolean visible = batchOwnerPage.isLearningPathDropdownVisible();
        Assert.assertTrue(
                visible,
                "FAIL - Learning Path dropdown did NOT appear after selecting a Service Line. " +
                "GAP: FRD 2.4.1 defines a dependent dropdown chain where Learning Path " +
                "options load after a Service Line is chosen. " +
                "Verify the implementation uses this dependent chain.");
        WebElement lpDropdown = driver.findElement(By.cssSelector(
                "select[name='learningPath'], select#learningPathFilter, " +
                "select[formcontrolname='learningPath']"));
        highlight(lpDropdown);
        System.out.println("PASS - Learning Path dropdown visible after Service Line selection");
    }

    // -------------------------------------------------------
    // TC-BO-FILTER-007 — Selecting Learning Path filters profile cards
    // FRD 2.4.1 — Final filter step: Learning Path → shows filtered batch owners
    // -------------------------------------------------------
    @Test(priority = 7)
    public void verifyLearningPathSelectionFiltersCards() {
        if (!batchOwnerPage.isLearningPathDropdownVisible()) {
            System.out.println("SKIP - Learning Path dropdown not available; skipping filter test");
            return;
        }

        WebElement lpDropdown = driver.findElement(By.cssSelector(
                "select[name='learningPath'], select#learningPathFilter, " +
                "select[formcontrolname='learningPath']"));
        Select lpSelect = new Select(lpDropdown);
        List<WebElement> options = lpSelect.getOptions();

        // Select the first non-placeholder option
        WebElement firstRealOption = options.stream()
                .filter(o -> !o.getAttribute("value").trim().isEmpty())
                .findFirst().orElse(null);

        if (firstRealOption == null) {
            System.out.println("SKIP - No selectable Learning Path options available");
            return;
        }

        String lpValue = firstRealOption.getText().trim();
        lpSelect.selectByVisibleText(lpValue);
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        boolean cardsVisible = batchOwnerPage.areProfileCardsVisible();
        Assert.assertTrue(
                cardsVisible,
                "FAIL - After selecting Learning Path '" + lpValue + "', no Batch Owner " +
                "profile cards appeared. FRD 2.4.1 requires profile cards to display " +
                "after the full filter chain (Service Line → Learning Path) is applied.");
        List<WebElement> cards = batchOwnerPage.getProfileCards();
        System.out.println("PASS - Learning Path filter '" + lpValue + "' returned " +
                cards.size() + " profile card(s)");
    }
}
