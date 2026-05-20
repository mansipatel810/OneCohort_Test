package com.cts.mfrp.onecohort.tests.managers;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.managers.ManagerDashboardPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

@Test(groups = {"smoke", "regression", "dashboard", "manager"})
@Listeners(ExtentReportListener.class)
public class ManagerDashboardTest extends BaseClassTest {

    private ManagerDashboardPage dashPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void setup() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsManager(
                ConfigReader.getManagerUserId(),
                ConfigReader.getValidServiceLineId()
        );
        wait.until(ExpectedConditions.urlContains("/manager/"));
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        dashPage = new ManagerDashboardPage(driver);
        dashPage.waitForDashboardLoad();
        System.out.println("Manager dashboard loaded. URL: " + driver.getCurrentUrl());
    }

    @Test(priority = 1, description = "TC-MGR-DASH-001: URL contains /manager/ path [FRD 3.4]")
    public void verifyManagerUrlPath() {
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/manager/"),
                "FAIL - URL does not contain /manager/. Got: " + url);
        System.out.println("PASS - URL: " + url);
    }

    @Test(priority = 2, description = "TC-MGR-DASH-002: URL contains /dashboard [FRD 3.4]")
    public void verifyUrlContainsDashboard() {
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/dashboard"),
                "FAIL - URL does not contain /dashboard. Got: " + url);
        System.out.println("PASS - URL contains /dashboard: " + url);
    }

    @Test(priority = 3, description = "TC-MGR-DASH-003: Dashboard container is present [FRD 3.4]")
    public void verifyDashboardContainerPresent() {
        Assert.assertTrue(dashPage.isDashboardContainerPresent(),
                "FAIL - dashboard-container not found.");
        System.out.println("PASS - Dashboard container is present.");
    }

    @Test(priority = 4, description = "TC-MGR-DASH-004: Welcome heading is visible [FRD 3.4]")
    public void verifyWelcomeHeadingVisible() {
        String text = dashPage.getWelcomeText();
        Assert.assertFalse(text.isEmpty(),
                "FAIL - Welcome heading is empty.");
        System.out.println("PASS - Welcome heading: " + text);
    }

    @Test(priority = 5, description = "TC-MGR-DASH-005: Welcome heading contains 'Manager' [FRD 3.4]")
    public void verifyWelcomeHeadingText() {
        String text = dashPage.getWelcomeText();
        Assert.assertTrue(text.contains("Manager"),
                "FAIL - Expected 'Manager' in welcome heading. Got: " + text);
        System.out.println("PASS - Welcome heading: " + text);
    }

    @Test(priority = 6, description = "TC-MGR-DASH-006: Role label shows 'Manager' [FRD 3.4]")
    public void verifyRoleTextIsManager() {
        String role = dashPage.getRoleText();
        Assert.assertEquals(role, "Manager",
                "FAIL - Role text expected 'Manager'. Got: " + role);
        System.out.println("PASS - Role text: " + role);
    }

    @Test(priority = 7, description = "TC-MGR-DASH-007: 'MG' avatar is visible [FRD 3.4]")
    public void verifyAvatarVisible() {
        Assert.assertTrue(dashPage.isAvatarVisible(),
                "FAIL - Manager avatar not visible.");
        System.out.println("PASS - Avatar visible. Text: " + dashPage.getAvatarText());
    }

    @Test(priority = 8, description = "TC-MGR-DASH-008: Sidebar is visible [FRD 3.4]")
    public void verifySidebarVisible() {
        Assert.assertTrue(dashPage.isSidebarVisible(),
                "FAIL - Sidebar not visible.");
        System.out.println("PASS - Sidebar is visible.");
    }

    @Test(priority = 9, description = "TC-MGR-DASH-009: Logo image is visible in sidebar [FRD 3.4]")
    public void verifyLogoVisible() {
        Assert.assertTrue(dashPage.isLogoVisible(),
                "FAIL - Logo image not visible.");
        System.out.println("PASS - Logo image visible.");
    }

    @Test(priority = 10, description = "TC-MGR-DASH-010: App name 'One Cohort' shown in sidebar [FRD 3.4]")
    public void verifyAppName() {
        String appName = dashPage.getAppName();
        Assert.assertTrue(appName.contains("One Cohort") || appName.contains("OneCohort"),
                "FAIL - App name not found. Got: " + appName);
        System.out.println("PASS - App name: " + appName);
    }

    @Test(priority = 11, description = "TC-MGR-DASH-011: Sidebar has at least 2 nav links [FRD 3.4]")
    public void verifyNavLinkCount() {
        int count = dashPage.getNavLinkCount();
        Assert.assertTrue(count >= 2,
                "FAIL - Expected at least 2 nav links. Found: " + count);
        System.out.println("PASS - Nav link count: " + count);
    }

    @Test(priority = 12, description = "TC-MGR-DASH-012: Sidebar contains 'Dashboard' nav link [FRD 3.4]")
    public void verifyDashboardNavLinkPresent() {
        List<String> links = dashPage.getNavLinkTexts();
        boolean found = links.stream().anyMatch(l -> l.equalsIgnoreCase("Dashboard"));
        Assert.assertTrue(found,
                "FAIL - 'Dashboard' nav link not found. Links: " + links);
        System.out.println("PASS - Dashboard nav link found. All links: " + links);
    }

    @Test(priority = 13, description = "TC-MGR-DASH-013: Sidebar contains 'Manage Cohorts' nav link [FRD 4]")
    public void verifyManageCohortsNavLinkPresent() {
        List<String> links = dashPage.getNavLinkTexts();
        boolean found = links.stream().anyMatch(l -> l.toLowerCase().contains("cohort"));
        Assert.assertTrue(found,
                "FAIL - No cohort nav link found. Links: " + links);
        System.out.println("PASS - Cohort nav link found. All links: " + links);
    }

    @Test(priority = 14, description = "TC-MGR-DASH-014: Dashboard heading contains 'Manager' [FRD 3.4]")
    public void verifyDashboardHeadingText() {
        String heading = dashPage.getDashboardHeading();
        Assert.assertTrue(heading.toLowerCase().contains("manager"),
                "FAIL - Dashboard heading should contain 'Manager'. Got: " + heading);
        System.out.println("PASS - Dashboard heading: " + heading);
    }

    @Test(priority = 15, description = "TC-MGR-DASH-015: Role badge shows 'Manager' [FRD 3.4]")
    public void verifyManagerBadge() {
        String badge = dashPage.getManagerBadgeText();
        Assert.assertEquals(badge, "Manager",
                "FAIL - Badge expected 'Manager'. Got: " + badge);
        System.out.println("PASS - Manager badge: " + badge);
    }

    @Test(priority = 16, description = "TC-MGR-DASH-016: 'Catalog & Rates' section title present [FRD 3.4]")
    public void verifyCatalogAndRatesSection() {
        Assert.assertTrue(dashPage.isSectionTitlePresent("Catalog & Rates"),
                "FAIL - 'Catalog & Rates' section not found. Titles: " + dashPage.getAllSectionTitles());
        System.out.println("PASS - 'Catalog & Rates' section is present.");
    }

    @Test(priority = 17, description = "TC-MGR-DASH-017: 'Cohorts per Service Line' section present [FRD 3.4]")
    public void verifyCohortsByServiceLineSection() {
        Assert.assertTrue(dashPage.isSectionTitlePresent("Cohorts per Service Line"),
                "FAIL - 'Cohorts per Service Line' not found. Titles: " + dashPage.getAllSectionTitles());
        System.out.println("PASS - 'Cohorts per Service Line' section is present.");
    }

    @Test(priority = 18, description = "TC-MGR-DASH-018: 'Cohorts per Learning Path' section present [FRD 3.4]")
    public void verifyCohortsByLearningPathSection() {
        Assert.assertTrue(dashPage.isSectionTitlePresent("Cohorts per Learning Path"),
                "FAIL - 'Cohorts per Learning Path' not found. Titles: " + dashPage.getAllSectionTitles());
        System.out.println("PASS - 'Cohorts per Learning Path' section is present.");
    }

    @Test(priority = 19, description = "TC-MGR-DASH-019: 'Training Completion Distribution' section present [FRD 3.4]")
    public void verifyCompletionDistributionSection() {
        Assert.assertTrue(dashPage.isSectionTitlePresent("Training Completion Distribution"),
                "FAIL - 'Training Completion Distribution' not found. Titles: " + dashPage.getAllSectionTitles());
        System.out.println("PASS - 'Training Completion Distribution' section is present.");
    }

    @Test(priority = 20, description = "TC-MGR-DASH-020: 'Service Lines' KPI card present [FRD 3.4]")
    public void verifyServiceLinesKpiCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Service Lines"),
                "FAIL - 'Service Lines' KPI card not found. Cards: " + dashPage.getKpiCardTitles());
        System.out.println("PASS - 'Service Lines' KPI card present.");
    }

    @Test(priority = 21, description = "TC-MGR-DASH-021: 'Learning Paths' KPI card present [FRD 3.4]")
    public void verifyLearningPathsKpiCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Learning Paths"),
                "FAIL - 'Learning Paths' KPI card not found. Cards: " + dashPage.getKpiCardTitles());
        System.out.println("PASS - 'Learning Paths' KPI card present.");
    }

    @Test(priority = 22, description = "TC-MGR-DASH-022: 'Avg. Completion Rate' KPI card present [FRD 3.4]")
    public void verifyAvgCompletionRateKpiCard() {
        Assert.assertTrue(dashPage.isKpiCardPresent("Avg. Completion Rate"),
                "FAIL - 'Avg. Completion Rate' KPI card not found. Cards: " + dashPage.getKpiCardTitles());
        System.out.println("PASS - 'Avg. Completion Rate' KPI card present.");
    }

    @Test(priority = 23, description = "TC-MGR-DASH-023: Service Lines KPI value is '1' [FRD 3.4]")
    public void verifyManagerSeesOnlyOneServiceLine() {
        List<String> numbers = dashPage.getKpiNumbers();
        boolean hasOne = numbers.stream().anyMatch(n -> n.trim().equals("1"));
        Assert.assertTrue(hasOne,
                "FAIL - Expected a KPI showing '1' for Service Lines. All values: " + numbers);
        System.out.println("PASS - KPI shows '1' for Service Lines. Values: " + numbers);
    }

    @Test(priority = 24, description = "TC-MGR-DASH-024: 'Upcoming' stat card present [FRD 3.4]")
    public void verifyUpcomingStatCard() {
        List<String> labels = dashPage.getStatLabels();
        boolean found = labels.stream().anyMatch(l -> l.equalsIgnoreCase("Upcoming"));
        Assert.assertTrue(found,
                "FAIL - 'Upcoming' stat card not found. Labels: " + labels);
        System.out.println("PASS - 'Upcoming' stat card present.");
    }

    @Test(priority = 25, description = "TC-MGR-DASH-025: 'In Progress' stat card present [FRD 3.4]")
    public void verifyInProgressStatCard() {
        List<String> labels = dashPage.getStatLabels();
        boolean found = labels.stream().anyMatch(l -> l.equalsIgnoreCase("In Progress"));
        Assert.assertTrue(found,
                "FAIL - 'In Progress' stat card not found. Labels: " + labels);
        System.out.println("PASS - 'In Progress' stat card present.");
    }

    @Test(priority = 26, description = "TC-MGR-DASH-026: 'Completed' stat card present [FRD 3.4]")
    public void verifyCompletedStatCard() {
        List<String> labels = dashPage.getStatLabels();
        boolean found = labels.stream().anyMatch(l -> l.equalsIgnoreCase("Completed"));
        Assert.assertTrue(found,
                "FAIL - 'Completed' stat card not found. Labels: " + labels);
        System.out.println("PASS - 'Completed' stat card present.");
    }

    @Test(priority = 27, description = "TC-MGR-DASH-027: At least 1 stat card exists [FRD 3.4]")
    public void verifyStatCardsPresent() {
        int count = dashPage.getTotalStatCardCount();
        Assert.assertTrue(count >= 1,
                "FAIL - No stat cards found. Expected at least 1.");
        System.out.println("PASS - Stat card count: " + count);
    }

    @Test(priority = 28, description = "TC-MGR-DASH-028: Exactly 1 stat card under 'Cohorts per Service Line' [FRD 3.4]")
    public void verifyOneStatCardForServiceLine() {
        int count = dashPage.getStatCardCountForSection("Cohorts per Service Line");
        Assert.assertEquals(count, 1,
                "FAIL - Expected 1 stat card for 'Cohorts per Service Line'. Found: " + count);
        System.out.println("PASS - 1 stat card in 'Cohorts per Service Line'.");
    }

    @Test(priority = 29, description = "TC-MGR-DASH-029: Stat cards in 'Cohorts per Learning Path' are present [FRD 3.4]")
    public void verifyLearningPathStatCardsPresent() {
        int count = dashPage.getStatCardCountForSection("Cohorts per Learning Path");
        Assert.assertTrue(count >= 1,
                "FAIL - No stat cards for 'Cohorts per Learning Path'. Found: " + count);
        System.out.println("PASS - Learning Path stat card count: " + count);
    }

    @Test(priority = 30, description = "TC-MGR-DASH-030: All stat card labels are non-empty [FRD 3.4]")
    public void verifyStatCardLabelsNonEmpty() {
        List<String> labels = dashPage.getStatLabels();
        Assert.assertTrue(labels.size() >= 1, "FAIL - No stat labels found.");
        boolean anyEmpty = labels.stream().anyMatch(String::isEmpty);
        Assert.assertFalse(anyEmpty,
                "FAIL - One or more stat labels are empty. Labels: " + labels);
        System.out.println("PASS - All stat labels non-empty: " + labels);
    }

    @Test(priority = 31, description = "TC-MGR-DASH-031: All stat card values are non-empty [FRD 3.4]")
    public void verifyStatCardValuesNonEmpty() {
        List<String> values = dashPage.getStatValues();
        Assert.assertTrue(values.size() >= 1, "FAIL - No stat values found.");
        boolean anyEmpty = values.stream().anyMatch(String::isEmpty);
        Assert.assertFalse(anyEmpty,
                "FAIL - One or more stat values are empty. Values: " + values);
        System.out.println("PASS - All stat values non-empty: " + values);
    }

    @Test(priority = 32, description = "TC-MGR-DASH-032: Progress bar fills are present in stat cards [FRD 3.4]")
    public void verifyStatBarFillsPresent() {
        int count = dashPage.getStatFillCount();
        Assert.assertTrue(count >= 1,
                "FAIL - No progress bar fills found. Expected at least 1.");
        System.out.println("PASS - Stat bar fill count: " + count);
    }

    @Test(priority = 33, description = "TC-MGR-DASH-033: All KPI numbers are non-empty [FRD 3.4]")
    public void verifyKpiNumbersNotEmpty() {
        List<String> numbers = dashPage.getKpiNumbers();
        Assert.assertTrue(numbers.size() >= 1, "FAIL - No KPI numbers found.");
        boolean anyEmpty = numbers.stream().anyMatch(n -> n.trim().isEmpty());
        Assert.assertFalse(anyEmpty,
                "FAIL - One or more KPI values are empty. Values: " + numbers);
        System.out.println("PASS - All KPI numbers populated: " + numbers);
    }

    @Test(priority = 34, description = "TC-MGR-DASH-034: Avg. Completion Rate KPI value contains '%' [FRD 3.4]")
    public void verifyCompletionRateHasPercentSign() {
        List<String> numbers = dashPage.getKpiNumbers();
        boolean hasPercent = numbers.stream().anyMatch(n -> n.contains("%"));
        Assert.assertTrue(hasPercent,
                "FAIL - Avg. Completion Rate should show a % value. Values: " + numbers);
        System.out.println("PASS - Completion rate KPI verified. Values: " + numbers);
    }

    @Test(priority = 35, description = "TC-MGR-DASH-035: Clicking 'Manage Cohorts' nav navigates away from dashboard [FRD 4]")
    public void verifyManageCohortsNavigation() {
        dashPage.clickManageCohortsNav();
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/dashboard")));
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/manager/"),
                "FAIL - After clicking Manage Cohorts, URL should be under /manager/. URL: " + url);
        System.out.println("PASS - Navigated to: " + url);
    }

    @Test(priority = 36, description = "TC-MGR-DASH-036: Clicking 'Dashboard' nav returns to dashboard [FRD 3.4]")
    public void verifyDashboardNavReturns() {
        dashPage.clickDashboardNav();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        String url = driver.getCurrentUrl();
        Assert.assertTrue(url.contains("/dashboard"),
                "FAIL - Clicking Dashboard nav should go to /dashboard. URL: " + url);
        System.out.println("PASS - Returned to dashboard: " + url);
        dashPage = new ManagerDashboardPage(driver);
        dashPage.waitForDashboardLoad();
    }

    @Test(priority = 37, description = "TC-MGR-DASH-037: No error message visible on dashboard [FRD 3.4]")
    public void verifyNoErrorMessageVisible() {
        Assert.assertFalse(dashPage.isErrorMessageVisible(),
                "FAIL - An error message is visible on the Manager Dashboard.");
        System.out.println("PASS - No error message visible.");
    }
}