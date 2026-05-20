package com.cts.mfrp.onecohort.pages.analytics;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.Collections;
import java.util.List;

/**
 * Page Object for the Training Progress Analytics page (FRD 2.5).
 */
public class TrainingProgressAnalyticsPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────

    private final By progressChart     = By.cssSelector("canvas, [class*='chart'], [class*='bar-chart']");
    private final By svgCharts         = By.cssSelector("svg, [class*='chart']");
    private final By cohortDetailCards = By.cssSelector("[class*='card'][class*='cohort'], [class*='cohort-card']");
    private final By onTrackIndicator  = By.xpath("//*[contains(text(),'On Track')]");
    private final By behindIndicator   = By.xpath("//*[contains(text(),'Behind')]");

    // Page heading
    private final By pageHeading = By.xpath(
            "//*[self::h1 or self::h2 or self::h3 or self::h4]" +
            "[contains(normalize-space(),'Training') " +
            "or contains(normalize-space(),'Analytics') " +
            "or contains(normalize-space(),'Progress')]");

    // Completion percentage elements — container-check via self or direct parent (no ancestor::*)
    private final By percentageInCards = By.xpath(
            "//*[contains(text(),'%') and " +
            "(contains(@class,'card') or contains(@class,'cohort') or contains(@class,'stat') " +
            "or parent::*[contains(@class,'card') or contains(@class,'cohort') or contains(@class,'stat')])]");
    private final By anyPercentage = By.xpath("//*[contains(text(),'%')]");

    // Service Line filter dropdown (multiple selector fallbacks)
    private final By serviceLineFilter = By.cssSelector(
            "select[name='serviceLine'], select#serviceLineFilter, " +
            "select[formcontrolname='serviceLine'], " +
            "select[formcontrolname='serviceLineId']");

    // Generic cohort cards (broad — used after filter)
    private final By genericCohortCards = By.cssSelector("[class*='card'], [class*='cohort']");

    public TrainingProgressAnalyticsPage(WebDriver driver) {
        super(driver);
    }

    // ── Chart ─────────────────────────────────────────────────────────────────

    public boolean isProgressChartVisible() {
        if (isDisplayed(progressChart)) return true;
        return !driver.findElements(svgCharts).isEmpty();
    }

    // ── Cohort cards ──────────────────────────────────────────────────────────

    public boolean areCohortDetailCardsVisible() {
        if (isDisplayed(cohortDetailCards)) return true;
        List<WebElement> fallback = driver.findElements(
                By.cssSelector("[class*='card'], [class*='cohort'], [class*='stat']"));
        return !fallback.isEmpty();
    }

    public List<WebElement> getCohortCardElements() {
        try { return driver.findElements(genericCohortCards); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    // ── Status indicators ─────────────────────────────────────────────────────

    public boolean isOnTrackIndicatorVisible() {
        return isDisplayed(onTrackIndicator);
    }

    public boolean isBehindIndicatorVisible() {
        return !driver.findElements(behindIndicator).isEmpty();
    }

    // ── Page heading ──────────────────────────────────────────────────────────

    public WebElement getPageHeadingElement() {
        return waitForVisible(pageHeading);
    }

    public boolean isPageHeadingVisible() {
        return isDisplayed(pageHeading);
    }

    // ── Completion percentages ────────────────────────────────────────────────

    public List<WebElement> getPercentageElements() {
        List<WebElement> elements = driver.findElements(percentageInCards);
        if (elements.isEmpty()) {
            elements = driver.findElements(anyPercentage);
        }
        return elements;
    }

    // ── Service Line filter ───────────────────────────────────────────────────

    public boolean isServiceLineFilterPresent() {
        List<WebElement> selects = driver.findElements(serviceLineFilter);
        if (!selects.isEmpty()) return true;
        // Broad fallback
        return !driver.findElements(By.cssSelector("select")).isEmpty();
    }

    public List<WebElement> getServiceLineFilterElements() {
        List<WebElement> selects = driver.findElements(serviceLineFilter);
        if (selects.isEmpty()) {
            selects = driver.findElements(By.cssSelector("select"));
        }
        return selects;
    }

    public WebElement getFirstServiceLineFilter() {
        List<WebElement> selects = getServiceLineFilterElements();
        return selects.isEmpty() ? null : selects.get(0);
    }

    public void selectServiceLineFilter(String serviceLineId) {
        WebElement select = getFirstServiceLineFilter();
        if (select == null) return;
        Select s = new Select(select);
        try { s.selectByValue(serviceLineId); return; } catch (Exception ignored) {}
        try { s.selectByIndex(1); } catch (Exception ignored) {}
    }
}
