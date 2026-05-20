package com.cts.mfrp.onecohort.pages.analytics;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Collections;
import java.util.List;

/**
 * Page Object for the Training Analytics / Active Cohort Progress page (FRD 2.5).
 * Covers all elements tested in TrainingAnalyticsBehindTest.
 */
public class TrainingAnalyticsBehindPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────

    private final By pageHeading     = By.cssSelector("h2.section-title");
    private final By chartContainer  = By.xpath(
            "//*[contains(@class,'bar-chart') or contains(@class,'chart-container') " +
            "or contains(@class,'chart-wrapper')]" +
            "[.//div[contains(@class,'bar-fill')]]");
    private final By chartBars       = By.cssSelector(".bar-fill");
    private final By barTooltip      = By.cssSelector(".bar-tooltip, .tooltip, [class*='tooltip']");
    private final By cohortCards     = By.cssSelector(".cohort-card");
    private final By completedCards  = By.cssSelector(".cohort-card.completed");
    private final By ongoingCards    = By.cssSelector(".cohort-card.ongoing");
    private final By upcomingCards   = By.cssSelector(".cohort-card.upcoming");
    private final By progressValues  = By.cssSelector(".card-progress-area .val");
    private final By trendIcons      = By.cssSelector(".trend-icon");
    private final By milestoneDateLinks = By.cssSelector(".f-val.link");
    private final By cardTitles      = By.cssSelector(".cohort-card h4");
    private final By cardSubtitles   = By.cssSelector(".cohort-card .subtitle");
    private final By progressFill    = By.cssSelector(".progress-fill");
    private final By valElement      = By.cssSelector(".val");

    public TrainingAnalyticsBehindPage(WebDriver driver) {
        super(driver);
    }

    // ── Scroll helper ─────────────────────────────────────────────────────────

    public void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", element);
    }

    // ── Page heading ──────────────────────────────────────────────────────────

    public WebElement getPageHeadingElement() {
        return driver.findElement(pageHeading);
    }

    public boolean isPageHeadingVisible() {
        return isDisplayed(pageHeading);
    }

    // ── Chart container ───────────────────────────────────────────────────────

    public boolean isChartContainerVisible() {
        // Try specific selector first; fall back to first element containing bar-fills
        if (isDisplayed(chartContainer)) return true;
        return !driver.findElements(chartBars).isEmpty();
    }

    public WebElement getChartContainerElement() {
        try { return driver.findElement(chartContainer); }
        catch (Exception e) { return driver.findElement(By.xpath("//*[.//div[contains(@class,'bar-fill')]]")); }
    }

    // ── Chart bars ────────────────────────────────────────────────────────────

    public List<WebElement> getChartBars() {
        try { return driver.findElements(chartBars); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    // ── Tooltip helpers ───────────────────────────────────────────────────────

    public List<WebElement> getBarTooltips() {
        try { return driver.findElements(barTooltip); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public void hoverOverElement(WebElement element) {
        new Actions(driver).moveToElement(element).perform();
    }

    public void waitForTooltipVisible() {
        try { wait.until(ExpectedConditions.visibilityOfElementLocated(barTooltip)); }
        catch (Exception ignored) {}
    }

    public void moveMouseAway() {
        new Actions(driver).moveByOffset(0, -50).perform();
    }

    // ── Cohort cards ──────────────────────────────────────────────────────────

    public List<WebElement> getCohortCards() {
        try { return driver.findElements(cohortCards); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<WebElement> getCompletedCards() {
        try { return driver.findElements(completedCards); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<WebElement> getOngoingCards() {
        try { return driver.findElements(ongoingCards); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<WebElement> getUpcomingCards() {
        try { return driver.findElements(upcomingCards); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    // ── Progress values ───────────────────────────────────────────────────────

    public List<WebElement> getProgressValues() {
        try { return driver.findElements(progressValues); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public String getValInCard(WebElement card) {
        return card.findElement(valElement).getText().trim();
    }

    public String getProgressFillWidthInCard(WebElement card) {
        String rawStyle = card.findElement(progressFill).getAttribute("style");
        return rawStyle.replace("width:", "").replace(";", "").trim();
    }

    // ── Trend icons ───────────────────────────────────────────────────────────

    public List<WebElement> getTrendIconsInCard(WebElement card) {
        return card.findElements(trendIcons);
    }

    // ── Milestone date links ──────────────────────────────────────────────────

    public List<WebElement> getMilestoneDateLinksInCard(WebElement card) {
        return card.findElements(milestoneDateLinks);
    }

    public String getCardTitleText(WebElement card) {
        return card.findElement(By.cssSelector("h4")).getText().trim();
    }

    // ── Card titles and subtitles ─────────────────────────────────────────────

    public List<WebElement> getCardTitles() {
        try { return driver.findElements(cardTitles); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<WebElement> getCardSubtitles() {
        try { return driver.findElements(cardSubtitles); }
        catch (Exception e) { return Collections.emptyList(); }
    }
}
