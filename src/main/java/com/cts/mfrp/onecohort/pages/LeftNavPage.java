package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object for the Super Admin left navigation panel.
 * Covers all sidebar link clicks tested in LeftNavPanelTest (FRD 2.1.2).
 *
 * Uses PageFactory via BasePage constructor.
 */
public class LeftNavPage extends BasePage {

    // ── @FindBy locators use By directly — all CSS href-based ─────────────────

    private final By dashboardLink        = By.cssSelector("a[href='/super-admin/dashboard']");
    private final By cohortManagementLink = By.cssSelector("a[href='/super-admin/cohorts']");
    private final By managersLink         = By.cssSelector("a[href='/super-admin/leadership']");
    private final By batchOwnersLinkExact = By.cssSelector("a[href='/super-admin/batch-owners']");
    private final By batchOwnersLinkWild  = By.cssSelector("a[href*='batch']");
    private final By trainingProgressLink = By.cssSelector("a[href='/super-admin/training-progress']");
    private final By systemConfigLink     = By.cssSelector("a[href='/super-admin/system-config']");

    public LeftNavPage(WebDriver driver) {
        super(driver);
    }

    // ── Navigation actions ────────────────────────────────────────────────────

    public void clickDashboard() {
        wait.until(ExpectedConditions.elementToBeClickable(dashboardLink)).click();
        wait.until(ExpectedConditions.urlContains("dashboard"));
    }

    public void clickCohortManagement() {
        wait.until(ExpectedConditions.elementToBeClickable(cohortManagementLink)).click();
        wait.until(ExpectedConditions.urlContains("cohort"));
    }

    public void clickManagersLeadership() {
        wait.until(ExpectedConditions.elementToBeClickable(managersLink)).click();
        wait.until(ExpectedConditions.urlContains("leadership"));
    }

    public void clickBatchOwners() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(batchOwnersLinkExact)).click();
        } catch (Exception e) {
            wait.until(ExpectedConditions.elementToBeClickable(batchOwnersLinkWild)).click();
        }
        wait.until(ExpectedConditions.urlContains("batch"));
    }

    public void clickTrainingProgress() {
        wait.until(ExpectedConditions.elementToBeClickable(trainingProgressLink)).click();
        wait.until(ExpectedConditions.urlContains("training"));
    }

    public void clickSystemConfig() {
        wait.until(ExpectedConditions.elementToBeClickable(systemConfigLink)).click();
        wait.until(ExpectedConditions.urlContains("system-config"));
    }
}
