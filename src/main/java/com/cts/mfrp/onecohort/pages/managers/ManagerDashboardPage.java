package com.cts.mfrp.onecohort.pages.managers;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerDashboardPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By dashboardContainer = By.cssSelector("div.dashboard-container");
    private final By roleLabel          = By.cssSelector("span.text-sm.font-bold.text-gray-700");
    private final By navLinks           = By.cssSelector("aside.sidebar nav.menu a");
    private final By kpiCardTitles      = By.cssSelector(".kpi-card .kpi-info h3");
    private final By kpiNumbers         = By.cssSelector(".kpi-card .kpi-info .kpi-number");
    private final By errorMessage       = By.cssSelector(".error, .alert-danger, [class*='error']");

    public ManagerDashboardPage(WebDriver driver) {
        super(driver);
    }

    // ── Page load ─────────────────────────────────────────────────────────────

    /**
     * Waits for the dashboard container AND at least one KPI card title to appear.
     * KPI data loads asynchronously on render.com, so a second wait is needed.
     */
    public void waitForDashboardLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(dashboardContainer));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(kpiCardTitles));
    }

    // ── Header ────────────────────────────────────────────────────────────────

    /**
     * Returns the role badge text (e.g. "Manager").
     * Uses explicit wait — header renders slightly after dashboardContainer on render.com.
     */
    public String getRoleText() {
        return waitForVisible(roleLabel).getText().trim();
    }

    // ── Sidebar navigation ────────────────────────────────────────────────────

    /**
     * Returns sidebar nav link texts.
     * Waits for links to be present — driver.findElements() returns empty immediately
     * without waiting, which fails on render.com before Angular finishes rendering.
     */
    public List<String> getNavLinkTexts() {
        try {
            waitForNavLinksReady();
            return driver.findElements(navLinks)
                    .stream()
                    .map(e -> e.getText().trim())
                    .filter(t -> !t.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Clicks "Manage Cohorts" in the sidebar.
     * Waits for nav links to be present before searching.
     */
    public void clickManageCohortsNav() {
        try {
            waitForNavLinksReady();
            driver.findElements(navLinks)
                    .stream()
                    .filter(e -> e.getText().trim().equalsIgnoreCase("Manage Cohorts"))
                    .findFirst()
                    .ifPresent(WebElement::click);
        } catch (Exception ignored) {}
    }

    /**
     * Clicks "Dashboard" in the sidebar.
     * Waits for nav links to be present before searching.
     */
    public void clickDashboardNav() {
        try {
            waitForNavLinksReady();
            driver.findElements(navLinks)
                    .stream()
                    .filter(e -> e.getText().trim().equalsIgnoreCase("Dashboard"))
                    .findFirst()
                    .ifPresent(WebElement::click);
        } catch (Exception ignored) {}
    }

    /** Waits until at least one sidebar nav link is present in the DOM. */
    private void waitForNavLinksReady() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(navLinks));
    }

    // ── KPI cards ─────────────────────────────────────────────────────────────

    /**
     * Returns KPI card title strings.
     * Uses FluentWait — KPI data loads asynchronously on render.com.
     */
    public List<String> getKpiCardTitles() {
        try {
            new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(20))
                    .pollingEvery(Duration.ofMillis(500))
                    .ignoring(Exception.class)
                    .until(d -> !d.findElements(kpiCardTitles).isEmpty());
            return driver.findElements(kpiCardTitles)
                    .stream()
                    .map(e -> e.getText().trim())
                    .filter(t -> !t.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /** Returns true if a KPI card with the given title is visible. */
    public boolean isKpiCardPresent(String title) {
        return getKpiCardTitles()
                .stream()
                .anyMatch(t -> t.equalsIgnoreCase(title));
    }

    /**
     * Returns KPI number value strings.
     * Uses FluentWait — numeric values arrive after the API call resolves.
     */
    public List<String> getKpiNumbers() {
        try {
            new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(20))
                    .pollingEvery(Duration.ofMillis(500))
                    .ignoring(Exception.class)
                    .until(d -> !d.findElements(kpiNumbers).isEmpty());
            return driver.findElements(kpiNumbers)
                    .stream()
                    .map(e -> e.getText().trim())
                    .filter(t -> !t.isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ── Error check ───────────────────────────────────────────────────────────

    /**
     * Returns true only if a visible error element exists on the page.
     * driver.findElements() returning empty = no error = false (happy path).
     */
    public boolean isErrorMessageVisible() {
        try {
            List<WebElement> errors = driver.findElements(errorMessage);
            return errors.stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            return false;
        }
    }
}
