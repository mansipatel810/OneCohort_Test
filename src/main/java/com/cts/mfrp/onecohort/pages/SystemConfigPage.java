package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

public class SystemConfigPage extends BasePage {

    private final By pageHeading = By.cssSelector("div.page-header h2");
    private final By pageSubtitle = By.cssSelector("div.page-header p");
    private final By allConfigCards = By.cssSelector(".config-card");
    private final By cohortCardTitle       = By.xpath("//h3[normalize-space()='Cohort Management']");
    private final By serviceLineCardTitle  = By.xpath("//h3[normalize-space()='Service Line Management']");
    private final By learningPathCardTitle = By.xpath("//h3[normalize-space()='Learning Path Management']");
    private final By pocCardTitle          = By.xpath("//h3[normalize-space()='POC Management']");
    private final By allCreateButtons = By.cssSelector("button.btn-create");
    private final By createCohortBtn       = By.xpath("//button[normalize-space()='+ Create Cohort']");
    private final By createServiceLineBtn  = By.xpath("//button[normalize-space()='+ Create Service Line']");
    private final By createLearningPathBtn = By.xpath("//button[normalize-space()='+ Create Learning Path']");
    private final By createPocBtn          = By.xpath("//button[normalize-space()='+ Create POC']");
    private final By modalOverlay  = By.cssSelector("div.modal-overlay, div[class*='modal'], div[class*='overlay']");
    private final By modalCancelBtn = By.xpath("//button[normalize-space()='Cancel']");

    public SystemConfigPage(WebDriver driver) {
        super(driver);
    }

    public void waitForPageLoad() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(pageHeading));
    }

    public boolean isOnSystemConfigPage() {
        return driver.getCurrentUrl().contains("/system-config");
    }

    public boolean isPageHeadingVisible() {
        return isDisplayed(pageHeading);
    }

    public String getPageHeadingText() {
        return driver.findElement(pageHeading).getText().trim();
    }

    public boolean isPageSubtitleVisible() {
        return isDisplayed(pageSubtitle);
    }

    public String getPageSubtitleText() {
        return driver.findElement(pageSubtitle).getText().trim();
    }

    public int getConfigCardCount() {
        return driver.findElements(allConfigCards).size();
    }

    public boolean isCohortCardVisible() {
        return isDisplayed(cohortCardTitle);
    }

    public boolean isServiceLineCardVisible() {
        return isDisplayed(serviceLineCardTitle);
    }

    public boolean isLearningPathCardVisible() {
        return isDisplayed(learningPathCardTitle);
    }

    public boolean isPocCardVisible() {
        return isDisplayed(pocCardTitle);
    }

    public int getCreateButtonCount() {
        return driver.findElements(allCreateButtons).size();
    }

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

    public void clickCreateCohort() {
        driver.findElement(createCohortBtn).click();
    }

    public void clickCreateServiceLine() {
        driver.findElement(createServiceLineBtn).click();
    }

    public void clickCreateLearningPath() {
        driver.findElement(createLearningPathBtn).click();
    }

    public void clickCreatePoc() {
        driver.findElement(createPocBtn).click();
    }

    public boolean isModalVisible() {
        List<WebElement> modals = driver.findElements(modalOverlay);
        return modals.stream().anyMatch(WebElement::isDisplayed);
    }

    public void cancelModal() {
        driver.findElement(modalCancelBtn).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(modalOverlay));
    }

}