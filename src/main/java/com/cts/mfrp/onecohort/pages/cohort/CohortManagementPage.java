package com.cts.mfrp.onecohort.pages.cohort;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CohortManagementPage extends BasePage {

    private final By dataGrid      = By.cssSelector("table, [class*='grid'], [class*='data-grid']");
    private final By emptyState    = By.xpath("//*[contains(text(),'No data found')]");
    private final By searchInput   = By.cssSelector("input[type='text'][placeholder*='Search'], input[type='search']");
    private final By filterButton  = By.xpath("//button[contains(text(),'Filter') or contains(text(),'filter')]");

    public CohortManagementPage(WebDriver driver) {
        super(driver);
    }

    public boolean isGridLoaded() {
        return isDisplayed(dataGrid);
    }

    public boolean isEmptyStateDisplayed() {
        return isDisplayed(emptyState);
    }

    public CohortManagementPage searchByKeyword(String keyword) {
        type(searchInput, keyword);
        return this;
    }

    public CohortManagementPage clickFilterButton() {
        click(filterButton);
        return this;
    }
}