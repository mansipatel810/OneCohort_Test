package com.cts.mfrp.onecohort.pages.analytics;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TrainingProgressAnalyticsPage extends BasePage {

    private final By progressChart      = By.cssSelector("canvas, [class*='chart'], [class*='bar-chart']");
    private final By cohortDetailCards  = By.cssSelector("[class*='card'][class*='cohort'], [class*='cohort-card']");
    private final By onTrackIndicator   = By.xpath("//*[contains(text(),'On Track')]");

    public TrainingProgressAnalyticsPage(WebDriver driver) {
        super(driver);
    }

    public boolean isProgressChartVisible() {
        return isDisplayed(progressChart);
    }

    public boolean areCohortDetailCardsVisible() {
        return isDisplayed(cohortDetailCards);
    }

    public boolean isOnTrackIndicatorVisible() {
        return isDisplayed(onTrackIndicator);
    }
}