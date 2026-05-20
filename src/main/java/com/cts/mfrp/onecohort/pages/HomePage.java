package com.cts.mfrp.onecohort.pages;

import com.cts.mfrp.onecohort.pages.analytics.TrainingProgressAnalyticsPage;
import com.cts.mfrp.onecohort.pages.cohort.CohortManagementPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

    private final By pageHeading        = By.xpath("//*[contains(text(),'Super User Command Center') or contains(text(),'OneCOHORT')]");
    private final By summaryCards       = By.cssSelector("[class*='card'], [class*='metric'], [class*='summary']");
    private final By cohortManagementNav = By.xpath("//a[contains(@href,'cohort') or contains(text(),'Cohort Management')]");
    // The sidebar link is labelled "Training Progress" in the Angular app — the old locator
    // only matched "Analytics" / "Reports" and never clicked anything, causing LeaveTest to fail.
    private final By analyticsNav        = By.xpath(
            "//a[contains(@href,'analytics') or contains(@href,'training')" +
            " or contains(normalize-space(),'Training Progress')" +
            " or contains(normalize-space(),'Analytics')" +
            " or contains(normalize-space(),'Training')" +
            " or contains(normalize-space(),'Reports')]");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public boolean isDashboardLoaded() {
        return isDisplayed(pageHeading) || getCurrentUrl().contains("/super-admin");
    }

    public boolean areSummaryCardsVisible() {
        return isDisplayed(summaryCards);
    }

    public CohortManagementPage navigateToCohortManagement() {
        click(cohortManagementNav);
        return new CohortManagementPage(driver);
    }

    public TrainingProgressAnalyticsPage navigateToTrainingAnalytics() {
        click(analyticsNav);
        return new TrainingProgressAnalyticsPage(driver);
    }
}