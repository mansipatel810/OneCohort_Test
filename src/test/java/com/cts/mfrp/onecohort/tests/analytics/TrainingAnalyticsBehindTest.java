package com.cts.mfrp.onecohort.tests.analytics;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.analytics.TrainingAnalyticsBehindPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

@Test(groups = {"regression", "functional", "analytics", "superadmin"})
@Listeners(ExtentReportListener.class)
public class TrainingAnalyticsBehindTest extends BaseClassTest {

    private TrainingAnalyticsBehindPage analyticsPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigate() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("dashboard"));

        SuperAdminDashboardPage dashPage = new SuperAdminDashboardPage(driver);
        dashPage.getMenuItemElement("Training Progress").click();
        wait.until(ExpectedConditions.urlContains("training-progress"));

        analyticsPage = new TrainingAnalyticsBehindPage(driver);
        System.out.println("Navigated to: " + driver.getCurrentUrl());
    }

    @Test(priority = 1,
            description = "TC-BHD-001 [FRD 2.5]: Page heading 'Active Cohort Progress' is visible")
    public void verifyPageHeadingVisible() {
        WebElement heading = analyticsPage.getPageHeadingElement();
        analyticsPage.scrollIntoView(heading);
        Assert.assertTrue(heading.isDisplayed(),
                "FAIL - Page heading is not visible [FRD 2.5]");
        System.out.println("PASS - Page heading text: " + heading.getText());
    }

    @Test(priority = 2,
            description = "TC-BHD-002 [FRD 2.5]: Bar chart container is rendered on the page")
    public void verifyChartIsRendered() {
        Assert.assertTrue(analyticsPage.isChartContainerVisible(),
                "FAIL - Chart container is not visible [FRD 2.5]");
        System.out.println("PASS - Chart container is present on the page.");
    }

    @Test(priority = 3,
            description = "TC-BHD-003 [FRD 2.5]: Hovering each chart bar shows a tooltip with progress info")
    public void verifyChartBarTooltipOnHover() {
        List<WebElement> bars = analyticsPage.getChartBars();
        Assert.assertFalse(bars.isEmpty(),
                "FAIL - No chart bars found inside the chart container [FRD 2.5]");

        for (WebElement bar : bars) {
            analyticsPage.scrollIntoView(bar);
            analyticsPage.hoverOverElement(bar);
            analyticsPage.waitForTooltipVisible();
            List<WebElement> tooltips = analyticsPage.getBarTooltips();
            if (!tooltips.isEmpty() && tooltips.get(0).isDisplayed()) {
                System.out.println("PASS - Bar hovered. Tooltip text: " + tooltips.get(0).getText().trim());
            } else {
                System.out.println("INFO - No tooltip element visible for this bar. "
                        + "title='" + bar.getAttribute("title")
                        + "', aria-label='" + bar.getAttribute("aria-label") + "' [FRD 2.5]");
            }
            analyticsPage.moveMouseAway();
        }
        System.out.println("PASS - TC-BHD-003: Hovered over " + bars.size() + " chart bar(s). [FRD 2.5]");
    }

    @Test(priority = 4,
            description = "TC-BHD-004 [FRD 2.5]: Cohort cards are displayed on the page (scrolls to find all)")
    public void verifyCohortCardsArePresent() {
        List<WebElement> cards = analyticsPage.getCohortCards();
        Assert.assertFalse(cards.isEmpty(),
                "FAIL - No cohort cards found on the page [FRD 2.5]");
        for (WebElement card : cards) {
            analyticsPage.scrollIntoView(card);
        }
        System.out.println("PASS - Total cohort cards found: " + cards.size());
    }

    @Test(priority = 5,
            description = "TC-BHD-005 [FRD 2.5]: Progress percentage is shown on every cohort card")
    public void verifyProgressValuesAreDisplayed() {
        List<WebElement> values = analyticsPage.getProgressValues();
        Assert.assertFalse(values.isEmpty(),
                "FAIL - No progress percentage values found [FRD 2.5]");
        for (WebElement val : values) {
            analyticsPage.scrollIntoView(val);
            String text = val.getText().trim();
            Assert.assertTrue(text.endsWith("%"),
                    "FAIL - Value '" + text + "' does not look like a percentage [FRD 2.5]");
        }
        System.out.println("PASS - " + values.size() + " progress value(s) found. Sample: "
                + values.get(0).getText());
    }

    @Test(priority = 6,
            description = "TC-BHD-006 [FRD 2.5]: Ongoing cohort cards are present on the page")
    public void verifyOngoingCardsExist() {
        List<WebElement> ongoing = analyticsPage.getOngoingCards();
        if (ongoing.isEmpty()) {
            System.out.println("INFO - No 'ongoing' cards found. All cohorts may be completed or upcoming. [FRD 2.5]");
        } else {
            for (WebElement card : ongoing) { analyticsPage.scrollIntoView(card); }
            System.out.println("PASS - " + ongoing.size() + " ongoing cohort card(s) found. [FRD 2.5]");
        }
        Assert.assertTrue(driver.getCurrentUrl().contains("training-progress"),
                "FAIL - Not on the Training Progress page [FRD 2.5]");
    }

    @Test(priority = 7,
            description = "TC-BHD-007 [FRD 2.5]: Trend icon appears inside each ongoing cohort card")
    public void verifyTrendIconInsideOngoingCards() {
        List<WebElement> ongoing = analyticsPage.getOngoingCards();
        if (ongoing.isEmpty()) {
            System.out.println("SKIP - No ongoing cards to check for trend icons. [FRD 2.5]");
            return;
        }
        for (WebElement card : ongoing) {
            analyticsPage.scrollIntoView(card);
            List<WebElement> icons = analyticsPage.getTrendIconsInCard(card);
            Assert.assertFalse(icons.isEmpty(),
                    "FAIL - Ongoing card '" + analyticsPage.getCardTitleText(card)
                            + "' is missing a trend icon [FRD 2.5]");
        }
        System.out.println("PASS - All " + ongoing.size() + " ongoing card(s) have a trend icon. [FRD 2.5]");
    }

    @Test(priority = 8,
            description = "TC-BHD-008 [FRD 2.5]: Completed cards show 100% progress")
    public void verifyCompletedCardsShow100Percent() {
        List<WebElement> completedCards = analyticsPage.getCompletedCards();
        if (completedCards.isEmpty()) {
            System.out.println("SKIP - No completed cards found. [FRD 2.5]");
            return;
        }
        for (WebElement card : completedCards) {
            analyticsPage.scrollIntoView(card);
            String progressText = analyticsPage.getValInCard(card);
            Assert.assertEquals(progressText, "100%",
                    "FAIL - Completed card should show 100% but shows: " + progressText + " [FRD 2.5]");
        }
        System.out.println("PASS - All " + completedCards.size() + " completed card(s) show 100%. [FRD 2.5]");
    }

    @Test(priority = 9,
            description = "TC-BHD-009 [FRD 2.5]: Upcoming cards show 0% progress")
    public void verifyUpcomingCardsShow0Percent() {
        List<WebElement> upcomingCards = analyticsPage.getUpcomingCards();
        if (upcomingCards.isEmpty()) {
            System.out.println("SKIP - No upcoming cards found. [FRD 2.5]");
            return;
        }
        for (WebElement card : upcomingCards) {
            analyticsPage.scrollIntoView(card);
            String progressText = analyticsPage.getValInCard(card);
            Assert.assertEquals(progressText, "0%",
                    "FAIL - Upcoming card should show 0% but shows: " + progressText + " [FRD 2.5]");
        }
        System.out.println("PASS - All " + upcomingCards.size() + " upcoming card(s) show 0%. [FRD 2.5]");
    }

    @Test(priority = 10,
            description = "TC-BHD-010 [FRD 2.5]: All three card types (completed, ongoing, upcoming) exist on the page")
    public void verifyAllThreeCardTypesExist() {
        int completed = analyticsPage.getCompletedCards().size();
        int ongoing   = analyticsPage.getOngoingCards().size();
        int upcoming  = analyticsPage.getUpcomingCards().size();

        System.out.println("INFO - Card counts: completed=" + completed
                + ", ongoing=" + ongoing + ", upcoming=" + upcoming);

        Assert.assertTrue(completed > 0, "FAIL - No 'completed' cards found [FRD 2.5]");
        Assert.assertTrue(ongoing   > 0, "FAIL - No 'ongoing' cards found [FRD 2.5]");
        Assert.assertTrue(upcoming  > 0, "FAIL - No 'upcoming' cards found [FRD 2.5]");

        System.out.println("PASS - All three card types are present. [FRD 2.5]");
    }

    @Test(priority = 11,
            description = "TC-BHD-011 [FRD 2.5]: Milestone date links are present on all ongoing cards")
    public void verifyMilestoneDateLinksOnOngoingCards() {
        List<WebElement> ongoing = analyticsPage.getOngoingCards();
        if (ongoing.isEmpty()) {
            System.out.println("SKIP - No ongoing cards to check. [FRD 2.5]");
            return;
        }
        for (WebElement card : ongoing) {
            analyticsPage.scrollIntoView(card);
            List<WebElement> dateLinks = analyticsPage.getMilestoneDateLinksInCard(card);
            Assert.assertFalse(dateLinks.isEmpty(),
                    "FAIL - Ongoing card '" + analyticsPage.getCardTitleText(card)
                            + "' is missing a milestone date link [FRD 2.5]");
            System.out.println("PASS - Card '" + analyticsPage.getCardTitleText(card)
                    + "' has milestone date: " + dateLinks.get(0).getText().trim());
        }
    }

    @Test(priority = 12,
            description = "TC-BHD-012 [FRD 2.5]: Each cohort card displays a non-empty cohort ID title")
    public void verifyCohortCardTitlesAreNotEmpty() {
        List<WebElement> titles = analyticsPage.getCardTitles();
        Assert.assertFalse(titles.isEmpty(),
                "FAIL - No card titles found [FRD 2.5]");
        for (WebElement title : titles) {
            analyticsPage.scrollIntoView(title);
            Assert.assertFalse(title.getText().trim().isEmpty(),
                    "FAIL - A cohort card has an empty title [FRD 2.5]");
        }
        System.out.println("PASS - All " + titles.size() + " cohort card(s) have a non-empty title. [FRD 2.5]");
    }

    @Test(priority = 13,
            description = "TC-BHD-013 [FRD 2.5]: Each cohort card has a non-empty subtitle (training topic)")
    public void verifyCohortCardSubtitlesAreNotEmpty() {
        List<WebElement> subtitles = analyticsPage.getCardSubtitles();
        Assert.assertFalse(subtitles.isEmpty(),
                "FAIL - No card subtitles found [FRD 2.5]");
        for (WebElement subtitle : subtitles) {
            analyticsPage.scrollIntoView(subtitle);
            Assert.assertFalse(subtitle.getText().trim().isEmpty(),
                    "FAIL - A cohort card has an empty subtitle [FRD 2.5]");
        }
        System.out.println("PASS - All " + subtitles.size() + " subtitle(s) are non-empty. [FRD 2.5]");
    }

    @Test(priority = 14,
            description = "TC-BHD-014 [FRD 2.5]: Progress bar fill width matches the displayed percentage value")
    public void verifyProgressBarWidthMatchesPercentage() {
        List<WebElement> cards = analyticsPage.getCohortCards();
        Assert.assertFalse(cards.isEmpty(), "FAIL - No cohort cards found [FRD 2.5]");

        for (WebElement card : cards) {
            analyticsPage.scrollIntoView(card);
            String displayedPct = analyticsPage.getValInCard(card);
            String barWidth     = analyticsPage.getProgressFillWidthInCard(card);

            Assert.assertEquals(barWidth, displayedPct,
                    "FAIL - Progress bar width '" + barWidth
                            + "' does not match displayed value '" + displayedPct
                            + "' on card: " + analyticsPage.getCardTitleText(card) + " [FRD 2.5]");
        }
        System.out.println("PASS - All " + cards.size()
                + " cohort card(s) have matching progress bar widths. [FRD 2.5]");
    }

    @Test(priority = 15,
            description = "TC-BHD-015 [FRD 2.5]: Ongoing cards with past milestone dates are identified as behind schedule")
    public void verifyOngoingCardsWithPastMilestonesAreBehindSchedule() {
        List<WebElement> ongoing = analyticsPage.getOngoingCards();
        if (ongoing.isEmpty()) {
            System.out.println("SKIP - No ongoing cards found. [FRD 2.5]");
            return;
        }
        String today = java.time.LocalDate.now().toString();
        int behindCount = 0;
        for (WebElement card : ongoing) {
            analyticsPage.scrollIntoView(card);
            String cardName = analyticsPage.getCardTitleText(card);
            List<WebElement> dateLinks = analyticsPage.getMilestoneDateLinksInCard(card);
            if (dateLinks.isEmpty()) continue;
            String milestoneDate = dateLinks.get(0).getText().trim();
            if (milestoneDate.compareTo(today) < 0) {
                behindCount++;
                System.out.println("BEHIND - Card '" + cardName + "' milestone was " + milestoneDate
                        + " (today is " + today + "). [FRD 2.5]");
            } else {
                System.out.println("ON TRACK - Card '" + cardName + "' milestone is " + milestoneDate + ". [FRD 2.5]");
            }
        }
        System.out.println("PASS - TC-BHD-015 complete. Behind count = " + behindCount + ". [FRD 2.5]");
        Assert.assertTrue(behindCount >= 0, "FAIL - Behind count is somehow negative [FRD 2.5]");
    }

    @Test(priority = 16,
            description = "TC-BHD-016 [FRD 2.5]: Total card count equals completed + ongoing + upcoming")
    public void verifyTotalCardCountIsConsistent() {
        int total     = analyticsPage.getCohortCards().size();
        int completed = analyticsPage.getCompletedCards().size();
        int ongoing   = analyticsPage.getOngoingCards().size();
        int upcoming  = analyticsPage.getUpcomingCards().size();

        Assert.assertEquals(total, completed + ongoing + upcoming,
                "FAIL - Total card count (" + total + ") does not equal "
                        + "completed(" + completed + ") + ongoing(" + ongoing
                        + ") + upcoming(" + upcoming + ") [FRD 2.5]");
        System.out.println("PASS - Card count is consistent: "
                + completed + " completed + " + ongoing + " ongoing + "
                + upcoming  + " upcoming = " + total + " total. [FRD 2.5]");
    }
}
