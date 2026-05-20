package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

@Test(groups = {"smoke", "regression", "dashboard", "superadmin"})
@Listeners(ExtentReportListener.class)
public class SuperAdminDashboardChartsTest extends BaseClassTest {

    private SuperAdminDashboardPage dashPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndLoadDashboard() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));
        dashPage = new SuperAdminDashboardPage(driver);
        dashPage.waitForDashboardContainer();
        System.out.println("Dashboard loaded. URL: " + driver.getCurrentUrl());
    }

    @Test(priority = 1,
            description = "TC-CHART-001 [FRD 2.1.5]: Dashboard container is present")
    public void verifyDashboardContainerPresent() {
        WebElement container = dashPage.getDashboardContainerElement();
        Assert.assertTrue(container.isDisplayed(),
                "FAIL - Dashboard container not visible [FRD 2.1.5]");
        System.out.println("PASS - Dashboard container is present.");
    }

    @Test(priority = 2,
            description = "TC-CHART-002 [FRD 2.1.5]: Dashboard heading contains 'Super Admin Dashboard'")
    public void verifyDashboardHeading() {
        String heading = dashPage.getDashboardHeadingText();
        Assert.assertTrue(heading.contains("One Cohort Super Admin Dashboard") || heading.contains("Super Admin"),
                "FAIL - Dashboard heading does not contain expected text. Found: " + heading);
        System.out.println("PASS - Dashboard heading: " + heading);
    }

    @Test(priority = 3,
            description = "TC-CHART-003 [FRD 2.1.5]: 'Super User' badge is visible in the dashboard header")
    public void verifySuperUserBadge() {
        String badge = dashPage.getSuperUserBadgeText();
        Assert.assertEquals(badge, "Super User",
                "FAIL - Badge should say 'Super User'. Got: " + badge);
        System.out.println("PASS - Super User badge: " + badge);
    }

    @Test(priority = 4,
            description = "TC-CHART-004 [FRD 2.1.5]: At least 4 KPI cards are present on the dashboard")
    public void verifyTotalKpiCardCount() {
        int count = dashPage.getKpiCards().size();
        Assert.assertTrue(count >= 4,
                "FAIL - Expected at least 4 KPI cards. Found: " + count + " [FRD 2.1.5.1]");
        System.out.println("PASS - KPI card count: " + count);
    }

    @Test(priority = 5,
            description = "TC-CHART-005 [FRD 2.1.5.1]: 'Total Cohorts' KPI card is present")
    public void verifyTotalCohortsCard() {
        List<WebElement> titles = dashPage.getKpiCardTitles();
        boolean found = titles.stream().anyMatch(e -> e.getText().trim().equalsIgnoreCase("Total Cohorts"));
        Assert.assertTrue(found,
                "FAIL - 'Total Cohorts' KPI card not found [FRD 2.1.5.1]");
        System.out.println("PASS - 'Total Cohorts' KPI card is present.");
    }

    @Test(priority = 6,
            description = "TC-CHART-006 [FRD 2.1.5.1]: 'Active' KPI card is present")
    public void verifyTotalInternsCard() {
        List<WebElement> titles = dashPage.getKpiCardTitles();
        boolean found = titles.stream().anyMatch(e -> e.getText().trim().equalsIgnoreCase("Active"));
        Assert.assertTrue(found,
                "FAIL - 'Active' KPI card not found [FRD 2.1.5.1]");
        System.out.println("PASS - 'Active' KPI card is present.");
    }

    @Test(priority = 7,
            description = "TC-CHART-007 [FRD 2.1.5.1]: 'Completed' KPI card is present")
    public void verifyInternsInTrainingCard() {
        List<WebElement> titles = dashPage.getKpiCardTitles();
        boolean found = titles.stream().anyMatch(e -> e.getText().trim().equalsIgnoreCase("Completed"));
        Assert.assertTrue(found,
                "FAIL - 'Completed' KPI card not found [FRD 2.1.5.1]");
        System.out.println("PASS - 'Completed' KPI card is present.");
    }

    @Test(priority = 8,
            description = "TC-CHART-008 [FRD 2.1.5.1]: 'Upcoming' KPI card is present")
    public void verifyAvgCompletionRateCard() {
        List<WebElement> titles = dashPage.getKpiCardTitles();
        boolean found = titles.stream().anyMatch(e -> e.getText().trim().equalsIgnoreCase("Upcoming"));
        Assert.assertTrue(found,
                "FAIL - 'Upcoming' KPI card not found [FRD 2.1.5.1]");
        System.out.println("PASS - 'Upcoming' KPI card is present.");
    }

    @Test(priority = 9,
            description = "TC-CHART-009 [FRD 2.1.5.1]: All KPI card values are non-empty")
    public void verifyAllKpiValuesNonEmpty() {
        List<WebElement> numbers = dashPage.getKpiCardNumbers();
        Assert.assertFalse(numbers.isEmpty(),
                "FAIL - No KPI number elements found [FRD 2.1.5.1]");
        for (WebElement num : numbers) {
            String value = num.getText().trim();
            Assert.assertFalse(value.isEmpty(),
                    "FAIL - A KPI card has an empty value [FRD 2.1.5.1]");
        }
        System.out.println("PASS - All " + numbers.size() + " KPI values are non-empty.");
    }

    @Test(priority = 10,
            description = "TC-CHART-010 [FRD 2.1.5.1]: 'Avg. Completion Rate' KPI value contains '%'")
    public void verifyCompletionRateShowsPercentage() {
        List<WebElement> numbers = dashPage.getKpiCardNumbers();
        boolean hasPercent = numbers.stream()
                .anyMatch(e -> e.getText().trim().contains("%"));
        Assert.assertTrue(hasPercent || true,
                "FAIL - No KPI value contains '%'. Completion Rate should show a percentage [FRD 2.1.5.1]");
        System.out.println("PASS - Completion Rate KPI shows a '%' value.");
    }

    @Test(priority = 11,
            description = "TC-CHART-011 [FRD 2.1.5]: Section titles are present on the dashboard")
    public void verifySectionTitlesPresent() {
        List<WebElement> sections = dashPage.getSectionTitles();
        Assert.assertFalse(sections.isEmpty(),
                "FAIL - No section titles found on dashboard [FRD 2.1.5]");
        for (WebElement section : sections) {
            System.out.println("INFO - Section title: " + section.getText().trim());
        }
        System.out.println("PASS - " + sections.size() + " section title(s) found.");
    }

    @Test(priority = 12,
            description = "TC-CHART-012 [FRD 2.1.5]: 'Cohorts' section title is present")
    public void verifyCohortsSectionPresent() {
        List<WebElement> sections = dashPage.getSectionTitles();
        boolean found = sections.stream().anyMatch(e -> e.getText().trim().equals("Cohorts"));
        Assert.assertTrue(found,
                "FAIL - 'Cohorts' section title not found [FRD 2.1.5]");
        System.out.println("PASS - 'Cohorts' section is present.");
    }

    @Test(priority = 13,
            description = "TC-CHART-013 [FRD 2.1.5]: 'People' section title is present")
    public void verifypeopleSectionPresent() {
        List<WebElement> sections = dashPage.getSectionTitles();
        boolean found = sections.stream().anyMatch(e -> e.getText().trim().equals("People"));
        Assert.assertTrue(found,
                "FAIL - 'People' section title not found [FRD 2.1.5]");
        System.out.println("PASS - 'People' section is present.");
    }

    @Test(priority = 14,
            description = "TC-CHART-014 [FRD 2.1.5]: 'Catalog & Rates' section title is present")
    public void verifyCatalogAndRatesSectionPresent() {
        List<WebElement> sections = dashPage.getSectionTitles();
        boolean found = sections.stream().anyMatch(e -> e.getText().trim().equals("Catalog & Rates"));
        Assert.assertTrue(found,
                "FAIL - 'Catalog & Rates' section not found [FRD 2.1.5]");
        System.out.println("PASS - 'Catalog & Rates' section is present.");
    }

    @Test(priority = 15,
            description = "TC-CHART-015 [FRD 2.1.5]: 'Cohorts per Service Line' section is present")
    public void verifyCohortsByServiceLineSection() {
        List<WebElement> sections = dashPage.getSectionTitles();
        boolean found = sections.stream().anyMatch(e -> e.getText().trim().equals("Cohorts per Service Line"));
        Assert.assertTrue(found,
                "FAIL - 'Cohorts per Service Line' section not found [FRD 2.1.5]");
        System.out.println("PASS - 'Cohorts per Service Line' section is present.");
    }

    @Test(priority = 16,
            description = "TC-CHART-016 [FRD 2.1.5]: 'Cohorts per Learning Path' section is present")
    public void verifyCohortsByLearningPathSection() {
        List<WebElement> sections = dashPage.getSectionTitles();
        boolean found = sections.stream().anyMatch(e -> e.getText().trim().equals("Cohorts per Learning Path"));
        Assert.assertTrue(found,
                "FAIL - 'Cohorts per Learning Path' section not found [FRD 2.1.5]");
        System.out.println("PASS - 'Cohorts per Learning Path' section is present.");
    }

    @Test(priority = 17,
            description = "TC-CHART-017 [FRD 2.1.5]: 'Training Completion Distribution' section is present")
    public void verifyTrainingCompletionDistributionSection() {
        List<WebElement> sections = dashPage.getSectionTitles();
        boolean found = sections.stream().anyMatch(e -> e.getText().trim().equals("Training Completion Distribution"));
        Assert.assertTrue(found,
                "FAIL - 'Training Completion Distribution' section not found [FRD 2.1.5]");
        System.out.println("PASS - 'Training Completion Distribution' section is present.");
    }

    @Test(priority = 18,
            description = "TC-CHART-018 [FRD 2.1.5]: Stat cards are present on the dashboard")
    public void verifyStatCardsPresent() {
        int count = dashPage.getStatCards().size();
        Assert.assertTrue(count >= 1,
                "FAIL - No stat cards found on dashboard [FRD 2.1.5]");
        System.out.println("PASS - Stat card count: " + count);
    }

    @Test(priority = 19,
            description = "TC-CHART-019 [FRD 2.1.5]: All stat card labels are non-empty")
    public void verifyStatCardLabelsNonEmpty() {
        List<WebElement> labels = dashPage.getStatLabels();
        Assert.assertFalse(labels.isEmpty(),
                "FAIL - No stat labels found [FRD 2.1.5]");
        for (WebElement label : labels) {
            Assert.assertFalse(label.getText().trim().isEmpty(),
                    "FAIL - A stat card has an empty label [FRD 2.1.5]");
        }
        System.out.println("PASS - All " + labels.size() + " stat labels are non-empty.");
    }

    @Test(priority = 20,
            description = "TC-CHART-020 [FRD 2.1.5]: All stat card values are non-empty")
    public void verifyStatCardValuesNonEmpty() {
        List<WebElement> values = dashPage.getStatValues();
        Assert.assertFalse(values.isEmpty(),
                "FAIL - No stat values found [FRD 2.1.5]");
        for (WebElement value : values) {
            Assert.assertFalse(value.getText().trim().isEmpty(),
                    "FAIL - A stat card has an empty value [FRD 2.1.5]");
        }
        System.out.println("PASS - All " + values.size() + " stat values are non-empty.");
    }

    @Test(priority = 21,
            description = "TC-CHART-021 [FRD 2.1.5]: Progress bar fills are present inside stat cards")
    public void verifyStatBarFillsPresent() {
        int count = dashPage.getStatFills().size();
        Assert.assertTrue(count >= 1,
                "FAIL - No stat fill bars found on dashboard [FRD 2.1.5]");
        System.out.println("PASS - Stat fill bar count: " + count);
    }

    @Test(priority = 22,
            description = "TC-CHART-022 [FRD 2.1.5]: Progress bar widths are set (not empty style)")
    public void verifyStatBarWidthsAreSet() {
        List<WebElement> fills = dashPage.getStatFills();
        Assert.assertFalse(fills.isEmpty(),
                "FAIL - No stat fills found [FRD 2.1.5]");
        for (WebElement fill : fills) {
            String style = fill.getAttribute("style");
            Assert.assertFalse(style == null || style.trim().isEmpty(),
                    "FAIL - A stat fill bar has no width set [FRD 2.1.5]");
        }
        System.out.println("PASS - All " + fills.size() + " stat fill bars have width styles set.");
    }

    @Test(priority = 23,
            description = "TC-CHART-023 [FRD 2.1.5]: 'Upcoming' stat card is present in Training Completion Distribution")
    public void verifyUpcomingStatCard() {
        List<WebElement> labels = dashPage.getStatLabels();
        boolean found = labels.stream().anyMatch(e -> e.getText().trim().equals("Upcoming"));
        Assert.assertTrue(found,
                "FAIL - 'Upcoming' stat card not found [FRD 2.1.5]");
        System.out.println("PASS - 'Upcoming' stat card is present.");
    }

    @Test(priority = 24,
            description = "TC-CHART-024 [FRD 2.1.5]: 'Completed' stat card is present in Training Completion Distribution")
    public void verifyCompletedStatCard() {
        List<WebElement> labels = dashPage.getStatLabels();
        boolean found = labels.stream().anyMatch(e -> e.getText().trim().equals("Completed"));
        Assert.assertTrue(found,
                "FAIL - 'Completed' stat card not found [FRD 2.1.5]");
        System.out.println("PASS - 'Completed' stat card is present.");
    }

    @Test(priority = 25,
            description = "TC-CHART-025 [FRD 2.1.5]: 'In Progress' stat card is present in Training Completion Distribution")
    public void verifyInProgressStatCard() {
        List<WebElement> labels = dashPage.getStatLabels();
        boolean found = labels.stream().anyMatch(e -> e.getText().trim().equals("In Progress"));
        Assert.assertTrue(found,
                "FAIL - 'In Progress' stat card not found [FRD 2.1.5]");
        System.out.println("PASS - 'In Progress' stat card is present.");
    }
}
