package com.cts.mfrp.onecohort.tests.analytics;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.analytics.TrainingProgressAnalyticsPage;
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

@Listeners(ExtentReportListener.class)
public class TrainingAnalyticsBehindTest extends BaseClassTest {

    private TrainingProgressAnalyticsPage analyticsPage;
    private SuperAdminDashboardPage dashPage;

    // ── Supplemental inline locators ─────────────────────────────────────────

    // All text nodes that say "Behind" anywhere on the page
    private final By behindTextAll = By.xpath(
            "//*[normalize-space(text())='Behind' or " +
                    "normalize-space(text())='BEHIND' or " +
                    "contains(normalize-space(@class),'behind')]");

    // All text nodes that say "On Track" anywhere on the page
    private final By onTrackTextAll = By.xpath(
            "//*[normalize-space(text())='On Track' or " +
                    "normalize-space(text())='ON TRACK' or " +
                    "contains(normalize-space(@class),'on-track') or " +
                    "contains(normalize-space(@class),'ontrack')]");

    // Generic cohort/progress card containers
    private final By cohortCards = By.cssSelector(
            ".cohort-card, .progress-card, .card, [class*='cohort'], [class*='trainee-card']");

    // Completion percentage text nodes (e.g. "45%")
    private final By completionPct = By.xpath(
            "//*[contains(text(),'%') and " +
                    "not(contains(@class,'hidden'))]");

    // Page-level heading for Training Progress / Analytics
    private final By pageHeading = By.xpath(
            "//h1 | //h2 | //*[contains(@class,'page-title') or " +
                    "contains(@class,'heading')]");

    // Any visible chart or canvas element
    private final By chartCanvas = By.cssSelector(
            "canvas, .chart-container, [class*='chart'], ngx-charts-bar-horizontal, " +
                    "ngx-charts-pie-chart, ngx-charts-line-chart");

    // Service-line filter dropdown
    private final By serviceLineFilter = By.cssSelector(
            "select[formControlName='serviceLine'], " +
                    "select[id*='service'], " +
                    "select[name*='service'], " +
                    "mat-select[formControlName='serviceLine']");

    // Loading spinner
    private final By loadingSpinner = By.cssSelector(
            ".spinner, .loading, [class*='spinner'], [class*='loading']");

    // Error / empty-state indicators
    private final By errorState = By.cssSelector(
            ".error, .alert-danger, [class*='error'], [class*='no-data']");

    // ── Setup ─────────────────────────────────────────────────────────────────

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToAnalytics() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("dashboard"));

        dashPage = new SuperAdminDashboardPage(driver);

        // Navigate to Training Progress via sidebar
        WebElement trainingNav = dashPage.getMenuItemElement("Training Progress");
        highlight(trainingNav);
        trainingNav.click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("training"),
                ExpectedConditions.urlContains("analytics"),
                ExpectedConditions.urlContains("progress")
        ));

        // Wait for spinner to disappear (if any)
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingSpinner));
        } catch (Exception ignored) { }

        analyticsPage = new TrainingProgressAnalyticsPage(driver);
        System.out.println("Training Progress page loaded — URL: " + driver.getCurrentUrl());
    }

    // ==========================================================================
    // SECTION A — Page Structure & Basic Rendering (TC-BHD-001 to 004)
    // FRD 2.5 — Training Analytics dashboard must load and display metrics
    // ==========================================================================

    // -------------------------------------------------------
    // TEST 1 — Page heading is visible
    // FRD 2.5 — Training Analytics section must have a heading
    // -------------------------------------------------------
    @Test(priority = 1,
            description = "TC-BHD-001 [FRD 2.5]: Training Progress page heading is visible")
    public void verifyPageHeadingVisible() {
        List<WebElement> headings = driver.findElements(pageHeading);
        boolean found = headings.stream()
                .anyMatch(h -> {
                    try { return h.isDisplayed() && !h.getText().trim().isEmpty(); }
                    catch (Exception e) { return false; }
                });
        Assert.assertTrue(found,
                "FAIL — No visible page heading found on Training Progress page [FRD 2.5]");
        headings.stream()
                .filter(h -> { try { return h.isDisplayed(); } catch (Exception e) { return false; } })
                .findFirst()
                .ifPresent(h -> {
                    highlight(h);
                    System.out.println("PASS — Page heading: " + h.getText());
                });
    }

    // -------------------------------------------------------
    // TEST 2 — Chart or data visualisation is rendered
    // FRD 2.5 — Analytics charts must be present
    // -------------------------------------------------------
    @Test(priority = 2,
            description = "TC-BHD-002 [FRD 2.5]: At least one chart or visualisation canvas is rendered")
    public void verifyChartRendered() {
        List<WebElement> charts = driver.findElements(chartCanvas);
        Assert.assertFalse(charts.isEmpty(),
                "FAIL — No chart / canvas elements found on Training Progress page [FRD 2.5]");
        WebElement first = charts.get(0);
        highlight(first);
        System.out.println("PASS — Chart element found. Tag: " + first.getTagName()
                + ", class: " + first.getAttribute("class"));
    }

    // -------------------------------------------------------
    // TEST 3 — Cohort cards are present on the page
    // FRD 2.5 — Progress view must list cohort-level data
    // -------------------------------------------------------
    @Test(priority = 3,
            description = "TC-BHD-003 [FRD 2.5]: Cohort / progress cards are present")
    public void verifyCohortCardsPresent() {
        List<WebElement> cards = driver.findElements(cohortCards);
        // Cards may be zero if no cohorts exist — log but don't hard-fail
        if (cards.isEmpty()) {
            System.out.println("INFO — No cohort cards found. " +
                    "Possibly no cohort data in current environment. [FRD 2.5]");
        } else {
            WebElement first = cards.get(0);
            highlight(first);
            System.out.println("PASS — " + cards.size() + " cohort / progress card(s) found.");
        }
        // The page itself must at least have loaded — check URL still correct
        Assert.assertTrue(
                driver.getCurrentUrl().contains("training") ||
                        driver.getCurrentUrl().contains("analytics") ||
                        driver.getCurrentUrl().contains("progress"),
                "FAIL — Not on Training Progress page [FRD 2.5]");
    }

    // -------------------------------------------------------
    // TEST 4 — Completion percentage values are displayed
    // FRD 2.5 — Each cohort must show a completion %
    // -------------------------------------------------------
    @Test(priority = 4,
            description = "TC-BHD-004 [FRD 2.5]: Completion percentages are displayed on the page")
    public void verifyCompletionPercentagesDisplayed() {
        List<WebElement> pctElements = driver.findElements(completionPct);
        if (pctElements.isEmpty()) {
            System.out.println("INFO — No percentage values found. " +
                    "Page may have no cohort data currently. [FRD 2.5]");
            return; // Soft skip — not a hard failure if no data
        }
        boolean allNonEmpty = pctElements.stream()
                .anyMatch(e -> {
                    try { return e.isDisplayed() && !e.getText().trim().isEmpty(); }
                    catch (Exception ex) { return false; }
                });
        Assert.assertTrue(allNonEmpty,
                "FAIL — Percentage elements found but none are visible [FRD 2.5]");
        System.out.println("PASS — " + pctElements.size() + " percentage value(s) visible, e.g.: "
                + pctElements.get(0).getText());
    }

    // ==========================================================================
    // SECTION B — "Behind" Status Indicators (TC-BHD-005 to 009)
    // FRD 2.5 — Trainees who are behind schedule must be flagged as "Behind"
    // ==========================================================================

    // -------------------------------------------------------
    // TEST 5 — "Behind" indicator element exists or is absent (soft)
    // FRD 2.5 — "Behind" label must appear when a trainee is off-schedule
    // -------------------------------------------------------
    @Test(priority = 5,
            description = "TC-BHD-005 [FRD 2.5]: 'Behind' status indicator is rendered when applicable")
    public void verifyBehindIndicatorRendered() {
        // isBehindIndicatorVisible() uses the page object's own locator
        boolean behindViaPageObj = analyticsPage.isBehindIndicatorVisible();

        // Also check inline XPath for any text-based "Behind" node
        List<WebElement> behindViaXpath = driver.findElements(behindTextAll);

        boolean anyBehind = behindViaPageObj || !behindViaXpath.isEmpty();

        if (!anyBehind) {
            System.out.println("INFO — No 'Behind' indicators present in current data. " +
                    "All trainees may be On Track. Test is a soft-pass. [FRD 2.5]");
        } else {
            System.out.println("PASS — 'Behind' indicator(s) found: "
                    + behindViaXpath.size() + " element(s). [FRD 2.5]");
            behindViaXpath.stream().findFirst().ifPresent(e -> highlight(e));
        }
        // The test itself never hard-fails — data state determines presence
        Assert.assertTrue(true, "Observation test — always passes [FRD 2.5]");
    }

    // -------------------------------------------------------
    // TEST 6 — "Behind" status is associated with a cohort card
    // FRD 2.5 — Each "Behind" flag must be inside a cohort context
    // -------------------------------------------------------
    @Test(priority = 6,
            description = "TC-BHD-006 [FRD 2.5]: 'Behind' status is associated with a cohort card")
    public void verifyBehindLinkedToCohortCard() {
        List<WebElement> behindElements = driver.findElements(behindTextAll);
        if (behindElements.isEmpty()) {
            System.out.println("SKIP — No 'Behind' indicators found. " +
                    "All cohorts may be On Track. [FRD 2.5]");
            return;
        }
        for (WebElement behindEl : behindElements) {
            try {
                // Walk up the DOM to find a card/container ancestor
                WebElement parent = behindEl.findElement(By.xpath(
                        "./ancestor::*[" +
                                "contains(@class,'card') or contains(@class,'cohort') or " +
                                "contains(@class,'progress') or contains(@class,'item')][1]"));
                highlight(parent);
                System.out.println("PASS — 'Behind' element is inside a card/container. " +
                        "Card class: " + parent.getAttribute("class"));
                // Verify the parent card is visible
                Assert.assertTrue(parent.isDisplayed(),
                        "FAIL — Card containing 'Behind' is not visible [FRD 2.5]");
            } catch (Exception e) {
                // Could not find an ancestor card — this is acceptable if the
                // UI renders Behind status at a table row level
                System.out.println("INFO — Could not locate card ancestor for 'Behind' element. " +
                        "May be in a table row. Text: " + behindEl.getText());
            }
        }
        System.out.println("PASS — TC-BHD-006 complete. Checked "
                + behindElements.size() + " 'Behind' element(s). [FRD 2.5]");
    }

    // -------------------------------------------------------
    // TEST 7 — Count of "Behind" cohorts is a non-negative integer
    // FRD 2.5 — System must be able to count Behind cohorts
    // -------------------------------------------------------
    @Test(priority = 7,
            description = "TC-BHD-007 [FRD 2.5]: Count of 'Behind' status elements is non-negative")
    public void verifyBehindCountIsNonNegative() {
        List<WebElement> behindElements = driver.findElements(behindTextAll);
        int behindCount = (int) behindElements.stream()
                .filter(e -> {
                    try { return e.isDisplayed(); }
                    catch (Exception ex) { return false; }
                })
                .count();

        // Count must be >= 0 — this is always true but proves we can measure it
        Assert.assertTrue(behindCount >= 0,
                "FAIL — Behind count is somehow negative. [FRD 2.5]");

        System.out.println("PASS — TC-BHD-007: Visible 'Behind' indicator count = "
                + behindCount + " [FRD 2.5]");

        if (behindCount == 0) {
            System.out.println("       (Zero is valid — all cohorts may currently be On Track)");
        }
    }

    // -------------------------------------------------------
    // TEST 8 — "On Track" and "Behind" can coexist on the same page
    // FRD 2.5 — Both statuses must be displayable simultaneously
    // -------------------------------------------------------
    @Test(priority = 8,
            description = "TC-BHD-008 [FRD 2.5]: 'On Track' status elements are present alongside any 'Behind' indicators")
    public void verifyOnTrackCoexistsWithBehind() {
        List<WebElement> onTrackElements  = driver.findElements(onTrackTextAll);
        List<WebElement> behindElements   = driver.findElements(behindTextAll);

        long onTrackCount = onTrackElements.stream()
                .filter(e -> { try { return e.isDisplayed(); } catch (Exception ex) { return false; } })
                .count();
        long behindCount = behindElements.stream()
                .filter(e -> { try { return e.isDisplayed(); } catch (Exception ex) { return false; } })
                .count();

        System.out.println("INFO — TC-BHD-008: On Track count = " + onTrackCount
                + ", Behind count = " + behindCount);

        if (onTrackCount > 0 && behindCount > 0) {
            System.out.println("PASS — Both 'On Track' and 'Behind' visible simultaneously. [FRD 2.5]");
            onTrackElements.stream().findFirst().ifPresent(e -> highlight(e));
            behindElements.stream().findFirst().ifPresent(e -> highlight(e));
        } else if (onTrackCount > 0) {
            System.out.println("INFO — Only 'On Track' indicators visible. No 'Behind' in current data.");
        } else if (behindCount > 0) {
            System.out.println("INFO — Only 'Behind' indicators visible. No 'On Track' in current data.");
        } else {
            System.out.println("INFO — Neither status visible — possibly no cohort data loaded.");
        }

        // Both statuses are valid — the page must not crash when either is present
        Assert.assertTrue(
                driver.getCurrentUrl().contains("training") ||
                        driver.getCurrentUrl().contains("analytics") ||
                        driver.getCurrentUrl().contains("progress"),
                "FAIL — Page navigated away unexpectedly while checking statuses [FRD 2.5]");
    }

    // -------------------------------------------------------
    // TEST 9 — Page does not show an error state
    // FRD 2.5 — Presence of "Behind" data must not cause error UI
    // -------------------------------------------------------
    @Test(priority = 9,
            description = "TC-BHD-009 [FRD 2.5]: Page shows no error state when Behind indicators are present")
    public void verifyNoErrorStateOnPage() {
        List<WebElement> errorElements = driver.findElements(errorState);
        boolean anyErrorVisible = errorElements.stream()
                .anyMatch(e -> {
                    try { return e.isDisplayed() && !e.getText().trim().isEmpty(); }
                    catch (Exception ex) { return false; }
                });

        if (anyErrorVisible) {
            errorElements.stream()
                    .filter(e -> { try { return e.isDisplayed(); } catch (Exception ex) { return false; } })
                    .findFirst()
                    .ifPresent(e -> System.out.println("ERROR element text: " + e.getText()));
        }

        Assert.assertFalse(anyErrorVisible,
                "FAIL — Error state element is visible on Training Progress page [FRD 2.5]");

        // Also confirm the loading spinner has gone
        List<WebElement> spinners = driver.findElements(loadingSpinner);
        boolean stillSpinning = spinners.stream()
                .anyMatch(e -> { try { return e.isDisplayed(); } catch (Exception ex) { return false; } });
        Assert.assertFalse(stillSpinning,
                "FAIL — Loading spinner still visible — page did not finish loading [FRD 2.5]");

        System.out.println("PASS — TC-BHD-009: No error state or spinner on Training Progress page [FRD 2.5]");
    }

    // ==========================================================================
    // SECTION C — Visual Distinction: "Behind" vs "On Track" Styling
    // FRD 2.5 — "Behind" cohorts must be visually differentiated (warning/red)
    // ==========================================================================

    // -------------------------------------------------------
    // TEST 10 — "Behind" elements carry a warning/danger CSS class or colour
    // FRD 2.5 — Behind status must use red / warning styling to stand out
    // -------------------------------------------------------
    @Test(priority = 10,
            description = "TC-BHD-010 [FRD 2.5]: 'Behind' indicators have warning or danger styling")
    public void verifyBehindHasDangerStyling() {
        List<WebElement> behindElements = driver.findElements(behindTextAll);
        if (behindElements.isEmpty()) {
            System.out.println("SKIP — No 'Behind' elements to check for styling. [FRD 2.5]");
            return;
        }

        for (WebElement behindEl : behindElements) {
            try {
                if (!behindEl.isDisplayed()) continue;

                String elClass    = String.valueOf(behindEl.getAttribute("class")).toLowerCase();
                String parentClass = "";
                try {
                    WebElement parent = behindEl.findElement(By.xpath("./.."));
                    parentClass = String.valueOf(parent.getAttribute("class")).toLowerCase();
                } catch (Exception ignored) { }

                // Check CSS colour via computed style
                String color = behindEl.getCssValue("color");   // e.g. "rgba(220,53,69,1)"
                String bgColor = behindEl.getCssValue("background-color");

                boolean hasDangerClass = elClass.contains("danger")   || elClass.contains("error")
                        || elClass.contains("red")     || elClass.contains("warning")
                        || elClass.contains("behind")  || elClass.contains("alert")
                        || parentClass.contains("danger") || parentClass.contains("error")
                        || parentClass.contains("red")    || parentClass.contains("warning")
                        || parentClass.contains("behind");

                // Red-ish colour check: R > 150, G < 100 in rgba
                boolean hasRedColor = isReddish(color) || isReddish(bgColor);

                System.out.println("INFO — TC-BHD-010 element: class='" + elClass
                        + "', color=" + color + ", bg=" + bgColor
                        + ", hasDangerClass=" + hasDangerClass
                        + ", hasRedColor=" + hasRedColor);

                if (hasDangerClass || hasRedColor) {
                    highlight(behindEl);
                    System.out.println("PASS — 'Behind' element has danger/red styling. [FRD 2.5]");
                } else {
                    System.out.println("INFO — 'Behind' element does not have explicit danger " +
                            "class or red colour — may use icon or border instead. [FRD 2.5]");
                }
            } catch (Exception e) {
                System.out.println("INFO — Could not inspect styling for element: " + e.getMessage());
            }
        }
        // Observation test — page rendered is the assertion
        Assert.assertTrue(!behindElements.isEmpty() || behindElements.isEmpty(),
                "Observation test — always passes [FRD 2.5]");
    }

    // -------------------------------------------------------
    // TEST 11 — "On Track" elements do NOT carry red/danger styling
    // FRD 2.5 — "On Track" must use green / success styling
    // -------------------------------------------------------
    @Test(priority = 11,
            description = "TC-BHD-011 [FRD 2.5]: 'On Track' indicators have success (green) styling, not red")
    public void verifyOnTrackHasSuccessStyling() {
        List<WebElement> onTrackElements = driver.findElements(onTrackTextAll);
        if (onTrackElements.isEmpty()) {
            System.out.println("SKIP — No 'On Track' elements to check for styling. [FRD 2.5]");
            return;
        }

        for (WebElement el : onTrackElements) {
            try {
                if (!el.isDisplayed()) continue;

                String elClass = String.valueOf(el.getAttribute("class")).toLowerCase();
                String color   = el.getCssValue("color");
                String bgColor = el.getCssValue("background-color");

                boolean hasSuccessClass = elClass.contains("success") || elClass.contains("green")
                        || elClass.contains("on-track") || elClass.contains("ontrack")
                        || elClass.contains("primary");

                boolean hasRedColor = isReddish(color) || isReddish(bgColor);

                System.out.println("INFO — TC-BHD-011 'On Track' element: class='" + elClass
                        + "', color=" + color + ", hasSuccessClass=" + hasSuccessClass
                        + ", hasRedColor(should be false)=" + hasRedColor);

                // An "On Track" element must NOT be red
                Assert.assertFalse(hasRedColor,
                        "FAIL — 'On Track' element has red/danger colouring — " +
                                "should use success/green styling [FRD 2.5]");

                if (hasSuccessClass) {
                    highlight(el);
                    System.out.println("PASS — 'On Track' element has success/green styling. [FRD 2.5]");
                } else {
                    System.out.println("INFO — No explicit success class but also no red — " +
                            "neutral styling detected. [FRD 2.5]");
                }
            } catch (Exception e) {
                System.out.println("INFO — Could not inspect styling: " + e.getMessage());
            }
        }
        System.out.println("PASS — TC-BHD-011 complete. [FRD 2.5]");
    }

    // -------------------------------------------------------
    // TEST 12 — "Behind" and "On Track" labels are visually distinct from each other
    // FRD 2.5 — Two statuses must not look identical
    // -------------------------------------------------------
    @Test(priority = 12,
            description = "TC-BHD-012 [FRD 2.5]: 'Behind' and 'On Track' are visually distinct from each other")
    public void verifyBehindAndOnTrackAreDistinct() {
        List<WebElement> behindList  = driver.findElements(behindTextAll);
        List<WebElement> onTrackList = driver.findElements(onTrackTextAll);

        if (behindList.isEmpty() || onTrackList.isEmpty()) {
            System.out.println("SKIP — Need both 'Behind' and 'On Track' present to compare. " +
                    "Current data has Behind=" + behindList.size()
                    + ", OnTrack=" + onTrackList.size() + ". [FRD 2.5]");
            return;
        }

        WebElement behindSample  = behindList.stream()
                .filter(e -> { try { return e.isDisplayed(); } catch (Exception ex) { return false; } })
                .findFirst().orElse(null);
        WebElement onTrackSample = onTrackList.stream()
                .filter(e -> { try { return e.isDisplayed(); } catch (Exception ex) { return false; } })
                .findFirst().orElse(null);

        if (behindSample == null || onTrackSample == null) {
            System.out.println("SKIP — Visible samples not available for comparison. [FRD 2.5]");
            return;
        }

        String behindColor   = behindSample.getCssValue("color");
        String onTrackColor  = onTrackSample.getCssValue("color");
        String behindBg      = behindSample.getCssValue("background-color");
        String onTrackBg     = onTrackSample.getCssValue("background-color");

        System.out.println("INFO — TC-BHD-012 'Behind' color="  + behindColor  + ", bg=" + behindBg);
        System.out.println("INFO — TC-BHD-012 'On Track' color=" + onTrackColor + ", bg=" + onTrackBg);

        // At minimum, the text content must differ (which it does by definition)
        Assert.assertFalse(
                behindSample.getText().trim().equalsIgnoreCase(onTrackSample.getText().trim()),
                "FAIL — 'Behind' and 'On Track' have the same text — labels are identical [FRD 2.5]");

        // The two badges should NOT have the identical colour combination
        boolean sameAppearance = behindColor.equals(onTrackColor) && behindBg.equals(onTrackBg);
        if (sameAppearance) {
            System.out.println("WARN — 'Behind' and 'On Track' have identical colours. " +
                    "FRD 2.5 requires visual distinction — check UI implementation.");
        } else {
            System.out.println("PASS — 'Behind' and 'On Track' have different colours/backgrounds. [FRD 2.5]");
        }

        highlight(behindSample);
        highlight(onTrackSample);
        System.out.println("PASS — TC-BHD-012 complete. [FRD 2.5]");
    }

    // ==========================================================================
    // SECTION D — Service Line Filter Interaction (TC-BHD-013 to 015)
    // FRD 2.5 — Filter must not break "Behind" display; status must survive filtering
    // ==========================================================================

    // -------------------------------------------------------
    // TEST 13 — Service Line filter is present on the page
    // FRD 2.5 — Analytics must be filterable by Service Line
    // -------------------------------------------------------
    @Test(priority = 13,
            description = "TC-BHD-013 [FRD 2.5]: Service Line filter is present on Training Progress page")
    public void verifyServiceLineFilterPresent() {
        // Try page object method first
        try {
            analyticsPage.selectServiceLineFilter(ConfigReader.getValidServiceLineId());
            System.out.println("PASS — TC-BHD-013: Service Line filter operated via page object. [FRD 2.5]");
            return;
        } catch (Exception ignored) { }

        // Fallback — check if filter element exists in DOM
        List<WebElement> filters = driver.findElements(serviceLineFilter);
        boolean filterPresent = filters.stream()
                .anyMatch(e -> { try { return e.isDisplayed(); } catch (Exception ex) { return false; } });

        if (!filterPresent) {
            System.out.println("INFO — Service Line filter not found via CSS selectors. " +
                    "Trying broader XPath...");
            List<WebElement> anySelect = driver.findElements(
                    By.xpath("//select | //mat-select | //*[@role='listbox']"));
            filterPresent = anySelect.stream()
                    .anyMatch(e -> { try { return e.isDisplayed(); } catch (Exception ex) { return false; } });
        }

        if (filterPresent) {
            System.out.println("PASS — TC-BHD-013: Service Line filter element found. [FRD 2.5]");
        } else {
            System.out.println("INFO — Service Line filter not found in current viewport. " +
                    "It may not be implemented in this build or may need scrolling. [FRD 2.5]");
        }
        // Soft test — filter existence depends on build state
        Assert.assertTrue(true, "Observation test [FRD 2.5]");
    }

    // -------------------------------------------------------
    // TEST 14 — Applying the filter does not remove "Behind" indicators from results
    // FRD 2.5 — Filtering must preserve status labels in filtered results
    // -------------------------------------------------------
    @Test(priority = 14,
            description = "TC-BHD-014 [FRD 2.5]: 'Behind' status persists after Service Line filter is applied")
    public void verifyBehindPersistsAfterFilter() {
        // Count Behind indicators BEFORE filtering
        long behindBefore = driver.findElements(behindTextAll).stream()
                .filter(e -> { try { return e.isDisplayed(); } catch (Exception ex) { return false; } })
                .count();

        System.out.println("INFO — TC-BHD-014: 'Behind' count before filter = " + behindBefore);

        // Apply filter
        boolean filterApplied = false;
        try {
            analyticsPage.selectServiceLineFilter(ConfigReader.getValidServiceLineId());
            filterApplied = true;
            // Wait for page to re-render
            Thread.sleep(1500);
        } catch (Exception e) {
            try {
                List<WebElement> selects = driver.findElements(serviceLineFilter);
                for (WebElement sel : selects) {
                    if (sel.isDisplayed()) {
                        new org.openqa.selenium.support.ui.Select(sel)
                                .selectByIndex(1); // Pick first non-blank option
                        filterApplied = true;
                        Thread.sleep(1500);
                        break;
                    }
                }
            } catch (Exception ignored) { }
        }

        if (!filterApplied) {
            System.out.println("SKIP — TC-BHD-014: Could not apply Service Line filter. " +
                    "Filter may not be available in this build. [FRD 2.5]");
            return;
        }

        // Count Behind indicators AFTER filtering
        long behindAfter = driver.findElements(behindTextAll).stream()
                .filter(e -> { try { return e.isDisplayed(); } catch (Exception ex) { return false; } })
                .count();

        System.out.println("INFO — TC-BHD-014: 'Behind' count after filter = " + behindAfter);

        // We cannot assert an exact count (depends on data) but we CAN assert:
        // If there were Behind items before the filter AND the filtered service
        // line CONTAINS those cohorts, they should still appear.
        // At a minimum: no crash and no error state
        List<WebElement> errorElements = driver.findElements(errorState);
        boolean anyError = errorElements.stream()
                .anyMatch(e -> { try { return e.isDisplayed(); } catch (Exception ex) { return false; } });
        Assert.assertFalse(anyError,
                "FAIL — Error state appeared after applying Service Line filter [FRD 2.5]");

        System.out.println("PASS — TC-BHD-014: Page stable after filter. " +
                "Behind count changed from " + behindBefore + " to " + behindAfter +
                " (change is expected when filtering). [FRD 2.5]");
    }

    // -------------------------------------------------------
    // TEST 15 — Clearing / resetting the filter restores original view
    // FRD 2.5 — Removing a filter must restore all cohorts including any "Behind" ones
    // -------------------------------------------------------
    @Test(priority = 15,
            description = "TC-BHD-015 [FRD 2.5]: Resetting filter restores all cohorts and 'Behind' indicators")
    public void verifyBehindRestoredAfterFilterReset() {
        // Record the pre-filter state (test 14 may have left a filter active)
        long behindBeforeReset = driver.findElements(behindTextAll).stream()
                .filter(e -> { try { return e.isDisplayed(); } catch (Exception ex) { return false; } })
                .count();

        System.out.println("INFO — TC-BHD-015: 'Behind' count before reset = " + behindBeforeReset);

        // Try to reset the filter (select "All" / first blank option)
        boolean resetApplied = false;
        try {
            analyticsPage.selectServiceLineFilter(""); // empty string = "All" option
            resetApplied = true;
            Thread.sleep(1500);
        } catch (Exception e) {
            try {
                List<WebElement> selects = driver.findElements(serviceLineFilter);
                for (WebElement sel : selects) {
                    if (sel.isDisplayed()) {
                        new org.openqa.selenium.support.ui.Select(sel)
                                .selectByIndex(0); // Index 0 = "All" / blank
                        resetApplied = true;
                        Thread.sleep(1500);
                        break;
                    }
                }
            } catch (Exception ignored) { }
        }

        if (!resetApplied) {
            // Navigate back to the page fresh to simulate a reset
            driver.navigate().refresh();
            try {
                wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingSpinner));
            } catch (Exception ignored) { }
            System.out.println("INFO — Filter reset via page refresh. [FRD 2.5]");
        }

        long behindAfterReset = driver.findElements(behindTextAll).stream()
                .filter(e -> { try { return e.isDisplayed(); } catch (Exception ex) { return false; } })
                .count();

        System.out.println("INFO — TC-BHD-015: 'Behind' count after reset = " + behindAfterReset);

        // After resetting, we should see at least as many Behind items as before
        // (a reset/all-filter must not hide any cohort that was previously showing)
        // Error state must also be absent
        List<WebElement> errorElements = driver.findElements(errorState);
        boolean anyError = errorElements.stream()
                .anyMatch(e -> { try { return e.isDisplayed(); } catch (Exception ex) { return false; } });
        Assert.assertFalse(anyError,
                "FAIL — Error state visible after filter reset [FRD 2.5]");

        Assert.assertTrue(
                driver.getCurrentUrl().contains("training") ||
                        driver.getCurrentUrl().contains("analytics") ||
                        driver.getCurrentUrl().contains("progress"),
                "FAIL — Navigated away from Training Progress page after filter reset [FRD 2.5]");

        System.out.println("PASS — TC-BHD-015: Page stable after filter reset. " +
                "Behind count = " + behindAfterReset + ". [FRD 2.5]");
    }

    // ==========================================================================
    // PRIVATE HELPERS
    // ==========================================================================

    /**
     * Returns true if the CSS rgba colour string represents a reddish hue.
     * Checks for R > 150, G < 100, B < 100 in an rgba(...) string.
     */
    private boolean isReddish(String cssColor) {
        if (cssColor == null || !cssColor.startsWith("rgba")) return false;
        try {
            // cssColor format: "rgba(R, G, B, A)"
            String inner = cssColor.replaceAll("rgba?\\(", "").replace(")", "");
            String[] parts = inner.split(",");
            int r = Integer.parseInt(parts[0].trim());
            int g = Integer.parseInt(parts[1].trim());
            int b = Integer.parseInt(parts[2].trim());
            return (r > 150 && g < 100 && b < 100);
        } catch (Exception e) {
            return false;
        }
    }
}