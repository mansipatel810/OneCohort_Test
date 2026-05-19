package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object for the Super Admin System Configuration page.
 *
 * URL     : /super-admin/system-config
 * FRD ref : Section 2.6 — System Configuration
 *
 * Page layout (FRD 2.6):
 *   Header  : "System Configuration"
 *   Subtitle: "Manage cohorts, service lines, and learning paths"
 *   Body    : 2×2 grid of 4 configuration tiles:
 *     1. Cohort Management         → "+ Create Cohort" button
 *     2. Service Line Management   → "+ Create Service Line" button
 *     3. Learning Path Management  → "+ Create Learning Path" button
 *     4. POC Management            → "+ Create POC" button
 *
 * NOTE FOR BEGINNERS:
 *   The selectors below are based on common Angular card layouts.
 *   If a test fails with "element not found", right-click the element in
 *   Chrome → Inspect, then copy the actual class name and update the selector.
 */
public class SystemConfigPage extends BasePage {

    // ── Page header ───────────────────────────────────────────────────────────
    // FRD 2.6: Page heading = "System Configuration"
    private final By pageHeading = By.xpath(
            "//*[contains(@class,'heading') or contains(@class,'title') or self::h1 or self::h2]" +
                    "[contains(text(),'System Configuration')]"
    );

    // FRD 2.6: Subtitle = "Manage cohorts, service lines, and learning paths"
    private final By pageSubtitle = By.xpath(
            "//*[contains(text(),'Manage cohorts') or contains(text(),'service lines') " +
                    "or contains(text(),'learning paths')]"
    );

    // ── Configuration tiles (4 cards in 2×2 grid) ────────────────────────────
    // Angular apps commonly use class names like "config-card", "card", "tile"
    // for card-based layouts. We target all of them to find the right one.
    private final By allConfigTiles = By.cssSelector(
            "div.config-card, div.tile, div.card, div.config-tile"
    );

    // ── Individual tile headings (by exact text) ──────────────────────────────
    // FRD 2.6: Each tile has a visible heading
    private final By cohortMgmtTile     = By.xpath("//*[contains(text(),'Cohort Management')]");
    private final By serviceLineMgmtTile = By.xpath("//*[contains(text(),'Service Line Management')]");
    private final By learningPathMgmtTile = By.xpath("//*[contains(text(),'Learning Path Management')]");
    private final By pocMgmtTile         = By.xpath("//*[contains(text(),'POC Management')]");

    // ── Action buttons on each tile ───────────────────────────────────────────
    // FRD 2.6: Each tile has a "+ Create X" button
    private final By createCohortBtn      = By.xpath("//button[contains(text(),'Create Cohort')]");
    private final By createServiceLineBtn = By.xpath("//button[contains(text(),'Create Service Line')]");
    private final By createLearningPathBtn = By.xpath("//button[contains(text(),'Create Learning Path')]");
    private final By createPocBtn         = By.xpath("//button[contains(text(),'Create POC')]");

    // ── Modal containers (appear when a Create button is clicked) ────────────
    // FRD 2.6: Clicking a button opens a modal with form fields
    private final By anyModalOverlay = By.cssSelector(
            "div.modal, div.modal-overlay, div[role='dialog'], div.overlay"
    );
    private final By modalCancelBtn  = By.xpath("//button[normalize-space()='Cancel']");

    // ── Constructor ───────────────────────────────────────────────────────────
    public SystemConfigPage(WebDriver driver) {
        super(driver);
    }

    // ── Wait for page to load ─────────────────────────────────────────────────

    /**
     * Waits until the page heading is visible.
     * Call this right after navigating to /system-config.
     */
    public SystemConfigPage waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(pageHeading));
        return this;
    }

    // ── Header content ────────────────────────────────────────────────────────

    public boolean isPageHeadingVisible() {
        return isDisplayed(pageHeading);
    }

    public String getPageHeadingText() {
        return getText(pageHeading);
    }

    public boolean isPageSubtitleVisible() {
        return isDisplayed(pageSubtitle);
    }

    public String getPageSubtitleText() {
        return getText(pageSubtitle);
    }

    // ── Tile presence checks ──────────────────────────────────────────────────

    public int getTileCount() {
        return driver.findElements(allConfigTiles).size();
    }

    public boolean isCohortManagementTileVisible() {
        return isDisplayed(cohortMgmtTile);
    }

    public boolean isServiceLineMgmtTileVisible() {
        return isDisplayed(serviceLineMgmtTile);
    }

    public boolean isLearningPathMgmtTileVisible() {
        return isDisplayed(learningPathMgmtTile);
    }

    public boolean isPocMgmtTileVisible() {
        return isDisplayed(pocMgmtTile);
    }

    // ── Action button checks ──────────────────────────────────────────────────

    public boolean isCreateCohortButtonVisible() {
        return isDisplayed(createCohortBtn);
    }

    public boolean isCreateServiceLineButtonVisible() {
        return isDisplayed(createServiceLineBtn);
    }

    public boolean isCreateLearningPathButtonVisible() {
        return isDisplayed(createLearningPathBtn);
    }

    public boolean isCreatePocButtonVisible() {
        return isDisplayed(createPocBtn);
    }

    // ── Modal interaction ─────────────────────────────────────────────────────

    /** Opens the Create Cohort modal by clicking its button. */
    public SystemConfigPage clickCreateCohort() {
        click(createCohortBtn);
        return this;
    }

    /** Opens the Create Service Line modal. */
    public SystemConfigPage clickCreateServiceLine() {
        click(createServiceLineBtn);
        return this;
    }

    /** Opens the Create Learning Path modal. */
    public SystemConfigPage clickCreateLearningPath() {
        click(createLearningPathBtn);
        return this;
    }

    /** Opens the Create POC modal. */
    public SystemConfigPage clickCreatePoc() {
        click(createPocBtn);
        return this;
    }

    /** Returns true if any modal overlay is currently visible. */
    public boolean isModalVisible() {
        return isDisplayed(anyModalOverlay);
    }

    /** Closes the currently open modal by clicking Cancel. */
    public SystemConfigPage cancelModal() {
        click(modalCancelBtn);
        return this;
    }

    // ── URL check ─────────────────────────────────────────────────────────────

    public boolean isOnSystemConfigPage() {
        return driver.getCurrentUrl().contains("/system-config");
    }
}