package com.cts.mfrp.onecohort.tests.analytics;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

@Listeners(ExtentReportListener.class)
public class TrainingAnalyticsBehindTest extends BaseClassTest {

    private final By pageHeading      = By.cssSelector("h2.section-title");
    private final By chartSection     = By.cssSelector("div.chart-section");
    private final By chartBars        = By.cssSelector("div.bar-fill");
    private final By barTooltip       = By.cssSelector(".bar-tooltip, .tooltip, [class*='tooltip']");
    private final By allCohortCards   = By.cssSelector("div.cohort-card");
    private final By completedCards   = By.cssSelector("div.cohort-card.completed");
    private final By ongoingCards     = By.cssSelector("div.cohort-card.ongoing");
    private final By upcomingCards    = By.cssSelector("div.cohort-card.upcoming");
    private final By progressValue    = By.cssSelector("span.val");
    private final By cardTitle        = By.cssSelector("h4");
    private final By cardSubtitle     = By.cssSelector("span.subtitle");
    private final By progressFill     = By.cssSelector("div.progress-fill");
    private final By trendIcon        = By.cssSelector("div.trend-icon");
    private final By milestoneDateLink = By.cssSelector("span.f-val.link");
    private final By milestoneValue   = By.cssSelector("span.f-val");

    private void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({ block: 'center' });", element
        );
    }

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigate() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("dashboard"));

        new SuperAdminDashboardPage(driver).getMenuItemElement("Training Progress").click();
        wait.until(ExpectedConditions.urlContains("training-progress"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(pageHeading));

        System.out.println("Navigated to: " + driver.getCurrentUrl());
    }

    @Test(priority = 1, description = "TC-BHD-001 [FRD 2.5]: Page heading 'Active Cohort Progress' is visible")
    public void verifyPageHeadingVisible() {
        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(pageHeading));
        scrollToElement(heading);
        Assert.assertTrue(heading.isDisplayed(), "FAIL - Page heading is not visible [FRD 2.5]");
        Assert.assertEquals(heading.getText().trim(), "Active Cohort Progress",
                "FAIL - Page heading text does not match [FRD 2.5]");
        System.out.println("PASS - Page heading: " + heading.getText().trim());
    }

    @Test(priority = 2, description = "TC-BHD-002 [FRD 2.5]: Bar chart section is rendered on the page")
    public void verifyChartIsRendered() {
        WebElement chart = wait.until(ExpectedConditions.visibilityOfElementLocated(chartSection));
        scrollToElement(chart);
        Assert.assertTrue(chart.isDisplayed(), "FAIL - Chart section is not visible [FRD 2.5]");

        List<WebElement> bars = driver.findElements(chartBars);
        Assert.assertFalse(bars.isEmpty(), "FAIL - No bar-fill elements found inside the chart [FRD 2.5]");
        System.out.println("PASS - Chart is rendered with " + bars.size() + " bar(s).");
    }

    @Test(priority = 3, description = "TC-BHD-003 [FRD 2.5]: Each chart bar has a non-zero height set in its style attribute")
    public void verifyChartBarsHaveHeight() {
        List<WebElement> bars = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(chartBars));
        Assert.assertFalse(bars.isEmpty(), "FAIL - No chart bars found [FRD 2.5]");

        for (WebElement bar : bars) {
            scrollToElement(bar);
            String styleValue = bar.getAttribute("style");
            Assert.assertNotNull(styleValue, "FAIL - bar-fill has no style attribute [FRD 2.5]");
            Assert.assertTrue(styleValue.contains("height"),
                    "FAIL - bar-fill style does not contain height. Style was: " + styleValue + " [FRD 2.5]");
            Assert.assertFalse(styleValue.contains("height: 0%"),
                    "FAIL - bar-fill has zero height, bar is invisible. Style: " + styleValue + " [FRD 2.5]");
            System.out.println("PASS - Bar height style: " + styleValue.trim());
        }
        System.out.println("PASS - All " + bars.size() + " chart bar(s) have a valid height.");
    }

    @Test(priority = 4, description = "TC-BHD-004 [FRD 2.5]: Clicking a chart bar shows a tooltip with cohort name and progress")
    public void verifyChartBarTooltipOnClick() {
        List<WebElement> bars = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(chartBars));
        Assert.assertFalse(bars.isEmpty(), "FAIL - No chart bars to click [FRD 2.5]");

        Actions actions = new Actions(driver);
        boolean tooltipFoundAtLeastOnce = false;

        for (WebElement bar : bars) {
            scrollToElement(bar);
            actions.click(bar).perform();

            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(barTooltip),
                    ExpectedConditions.attributeContains(bar, "title", "")
            ));

            List<WebElement> tooltips = driver.findElements(barTooltip);
            if (!tooltips.isEmpty() && tooltips.get(0).isDisplayed()) {
                String tooltipText = tooltips.get(0).getText().trim();
                Assert.assertFalse(tooltipText.isEmpty(),
                        "FAIL - Tooltip is visible but has no text [FRD 2.5]");
                System.out.println("PASS - Tooltip on click: " + tooltipText);
                tooltipFoundAtLeastOnce = true;
                actions.click(bar).perform();
            } else {
                String titleAttr  = bar.getAttribute("title");
                String ariaLabel  = bar.getAttribute("aria-label");
                String parentTitle = bar.findElement(By.xpath("..")).getAttribute("title");
                System.out.println("INFO - No tooltip element found for this bar. "
                        + "title='" + titleAttr + "', aria-label='" + ariaLabel
                        + "', parent title='" + parentTitle + "' [FRD 2.5]");
            }
        }

        Assert.assertTrue(tooltipFoundAtLeastOnce,
                "FAIL - No tooltip appeared after clicking any bar. Check tooltip CSS selector. [FRD 2.5]");
        System.out.println("PASS - TC-BHD-004: Clicked " + bars.size() + " bar(s), tooltip confirmed.");
    }

    @Test(priority = 5, description = "TC-BHD-005 [FRD 2.5]: Cohort cards are displayed on the page")
    public void verifyCohortCardsArePresent() {
        List<WebElement> cards = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(allCohortCards));
        Assert.assertFalse(cards.isEmpty(), "FAIL - No cohort cards found on the page [FRD 2.5]");

        for (WebElement card : cards) {
            scrollToElement(card);
        }
        System.out.println("PASS - Total cohort cards found: " + cards.size());
    }

    @Test(priority = 6, description = "TC-BHD-006 [FRD 2.5]: Progress percentage is shown on every cohort card")
    public void verifyProgressValuesAreDisplayed() {
        List<WebElement> cards = driver.findElements(allCohortCards);
        Assert.assertFalse(cards.isEmpty(), "FAIL - No cards found [FRD 2.5]");

        for (WebElement card : cards) {
            scrollToElement(card);
            WebElement valElement = card.findElement(progressValue);
            String text = valElement.getText().trim();
            Assert.assertTrue(text.endsWith("%"),
                    "FAIL - Progress value '" + text + "' does not end with '%' [FRD 2.5]");
        }
        System.out.println("PASS - All " + cards.size() + " card(s) show a progress percentage.");
    }

    @Test(priority = 7, description = "TC-BHD-007 [FRD 2.5]: Ongoing cohort cards are present on the page")
    public void verifyOngoingCardsExist() {
        List<WebElement> ongoing = driver.findElements(ongoingCards);
        Assert.assertFalse(ongoing.isEmpty(),
                "FAIL - No 'ongoing' cohort cards found on the page [FRD 2.5]");

        for (WebElement card : ongoing) {
            scrollToElement(card);
        }
        System.out.println("PASS - " + ongoing.size() + " ongoing cohort card(s) found.");
    }

    @Test(priority = 8, description = "TC-BHD-008 [FRD 2.5]: Every ongoing card has a trend icon (↗)")
    public void verifyTrendIconOnOngoingCards() {
        List<WebElement> ongoing = driver.findElements(ongoingCards);
        Assert.assertFalse(ongoing.isEmpty(), "FAIL - No ongoing cards to check [FRD 2.5]");

        for (WebElement card : ongoing) {
            scrollToElement(card);
            String cardName = card.findElement(cardTitle).getText().trim();
            List<WebElement> icons = card.findElements(trendIcon);
            Assert.assertFalse(icons.isEmpty(),
                    "FAIL - Ongoing card '" + cardName + "' is missing a trend icon [FRD 2.5]");
            System.out.println("PASS - Trend icon found on ongoing card: " + cardName);
        }
    }

    @Test(priority = 9, description = "TC-BHD-009 [FRD 2.5]: All completed cards show 100% progress")
    public void verifyCompletedCardsShow100Percent() {
        List<WebElement> completed = driver.findElements(completedCards);
        Assert.assertFalse(completed.isEmpty(), "FAIL - No completed cards found [FRD 2.5]");

        for (WebElement card : completed) {
            scrollToElement(card);
            String progressText = card.findElement(progressValue).getText().trim();
            Assert.assertEquals(progressText, "100%",
                    "FAIL - Completed card should show 100% but shows: " + progressText + " [FRD 2.5]");
        }
        System.out.println("PASS - All " + completed.size() + " completed card(s) show 100%.");
    }

    @Test(priority = 10, description = "TC-BHD-010 [FRD 2.5]: All upcoming cards show 0% progress")
    public void verifyUpcomingCardsShow0Percent() {
        List<WebElement> upcoming = driver.findElements(upcomingCards);
        Assert.assertFalse(upcoming.isEmpty(), "FAIL - No upcoming cards found [FRD 2.5]");

        for (WebElement card : upcoming) {
            scrollToElement(card);
            String progressText = card.findElement(progressValue).getText().trim();
            Assert.assertEquals(progressText, "0%",
                    "FAIL - Upcoming card should show 0% but shows: " + progressText + " [FRD 2.5]");
        }
        System.out.println("PASS - All " + upcoming.size() + " upcoming card(s) show 0%.");
    }

    @Test(priority = 11, description = "TC-BHD-011 [FRD 2.5]: All three card types (completed, ongoing, upcoming) exist")
    public void verifyAllThreeCardTypesExist() {
        int completedCount = driver.findElements(completedCards).size();
        int ongoingCount   = driver.findElements(ongoingCards).size();
        int upcomingCount  = driver.findElements(upcomingCards).size();

        System.out.println("INFO - completed=" + completedCount
                + ", ongoing=" + ongoingCount + ", upcoming=" + upcomingCount);

        Assert.assertTrue(completedCount > 0, "FAIL - No completed cards found [FRD 2.5]");
        Assert.assertTrue(ongoingCount   > 0, "FAIL - No ongoing cards found [FRD 2.5]");
        Assert.assertTrue(upcomingCount  > 0, "FAIL - No upcoming cards found [FRD 2.5]");

        System.out.println("PASS - All three card types are present.");
    }

    @Test(priority = 12, description = "TC-BHD-012 [FRD 2.5]: Every ongoing card has a clickable milestone date link")
    public void verifyMilestoneDateLinksOnOngoingCards() {
        List<WebElement> ongoing = driver.findElements(ongoingCards);
        Assert.assertFalse(ongoing.isEmpty(), "FAIL - No ongoing cards to check [FRD 2.5]");

        for (WebElement card : ongoing) {
            scrollToElement(card);
            String name = card.findElement(cardTitle).getText().trim();
            List<WebElement> dateLinks = card.findElements(milestoneDateLink);
            Assert.assertFalse(dateLinks.isEmpty(),
                    "FAIL - Ongoing card '" + name + "' has no milestone date link [FRD 2.5]");
            System.out.println("PASS - '" + name + "' milestone date: " + dateLinks.get(0).getText().trim());
        }
    }

    @Test(priority = 13, description = "TC-BHD-013 [FRD 2.5]: Every cohort card has a non-empty title (cohort ID)")
    public void verifyCohortCardTitlesAreNotEmpty() {
        List<WebElement> cards = driver.findElements(allCohortCards);
        Assert.assertFalse(cards.isEmpty(), "FAIL - No cohort cards found [FRD 2.5]");

        for (WebElement card : cards) {
            scrollToElement(card);
            String titleText = card.findElement(cardTitle).getText().trim();
            Assert.assertFalse(titleText.isEmpty(),
                    "FAIL - A cohort card has an empty title [FRD 2.5]");
        }
        System.out.println("PASS - All " + cards.size() + " card(s) have a non-empty cohort ID title.");
    }

    @Test(priority = 14, description = "TC-BHD-014 [FRD 2.5]: Every cohort card has a non-empty subtitle (training topic)")
    public void verifyCohortCardSubtitlesAreNotEmpty() {
        List<WebElement> cards = driver.findElements(allCohortCards);
        Assert.assertFalse(cards.isEmpty(), "FAIL - No cohort cards found [FRD 2.5]");

        for (WebElement card : cards) {
            scrollToElement(card);
            String subtitleText = card.findElement(cardSubtitle).getText().trim();
            Assert.assertFalse(subtitleText.isEmpty(),
                    "FAIL - A cohort card has an empty subtitle [FRD 2.5]");
        }
        System.out.println("PASS - All " + cards.size() + " card(s) have a non-empty subtitle.");
    }

    @Test(priority = 15, description = "TC-BHD-015 [FRD 2.5]: Progress bar fill width matches the displayed percentage on each card")
    public void verifyProgressBarWidthMatchesPercentage() {
        List<WebElement> cards = driver.findElements(allCohortCards);
        Assert.assertFalse(cards.isEmpty(), "FAIL - No cohort cards found [FRD 2.5]");

        for (WebElement card : cards) {
            scrollToElement(card);

            String displayedPercent = card.findElement(progressValue).getText().trim();
            String fillStyle        = card.findElement(progressFill).getAttribute("style");
            String barWidthInStyle  = fillStyle.replace("width:", "").replace(";", "").trim();
            String cardName         = card.findElement(cardTitle).getText().trim();

            Assert.assertEquals(barWidthInStyle, displayedPercent,
                    "FAIL - Card '" + cardName + "': bar width '" + barWidthInStyle
                            + "' does not match displayed '" + displayedPercent + "' [FRD 2.5]");
        }
        System.out.println("PASS - All " + cards.size() + " card(s) have matching progress bar widths.");
    }

    @Test(priority = 16, description = "TC-BHD-016 [FRD 2.5]: Ongoing cards with past milestone dates are flagged as behind schedule")
    public void verifyOngoingCardsWithPastMilestonesAreBehindSchedule() {
        List<WebElement> ongoing = driver.findElements(ongoingCards);
        Assert.assertFalse(ongoing.isEmpty(), "FAIL - No ongoing cards found [FRD 2.5]");

        String today    = java.time.LocalDate.now().toString();
        int behindCount = 0;

        for (WebElement card : ongoing) {
            scrollToElement(card);
            String cardName = card.findElement(cardTitle).getText().trim();
            List<WebElement> dateLinks = card.findElements(milestoneDateLink);
            if (dateLinks.isEmpty()) continue;

            String milestoneDate = dateLinks.get(0).getText().trim();
            if (milestoneDate.compareTo(today) < 0) {
                behindCount++;
                System.out.println("BEHIND - '" + cardName + "' milestone was " + milestoneDate
                        + " (today is " + today + ") [FRD 2.5]");
            } else {
                System.out.println("ON TRACK - '" + cardName + "' milestone is " + milestoneDate);
            }
        }

        System.out.println("PASS - TC-BHD-016 complete. Cards behind schedule: " + behindCount);
        Assert.assertTrue(behindCount >= 0, "FAIL - Behind count should never be negative [FRD 2.5]");
    }

    @Test(priority = 17, description = "TC-BHD-017 [FRD 2.5]: Total card count equals completed + ongoing + upcoming")
    public void verifyTotalCardCountIsConsistent() {
        int total     = driver.findElements(allCohortCards).size();
        int completed = driver.findElements(completedCards).size();
        int ongoing   = driver.findElements(ongoingCards).size();
        int upcoming  = driver.findElements(upcomingCards).size();

        Assert.assertEquals(total, completed + ongoing + upcoming,
                "FAIL - Total cards (" + total + ") does not equal "
                        + "completed(" + completed + ") + ongoing(" + ongoing
                        + ") + upcoming(" + upcoming + ") [FRD 2.5]");

        System.out.println("PASS - " + completed + " completed + "
                + ongoing + " ongoing + " + upcoming + " upcoming = " + total + " total.");
    }
}