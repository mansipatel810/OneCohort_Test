package com.cts.mfrp.onecohort.pages.batchowners;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.Collections;
import java.util.List;

public class BatchOwnerPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By pageHeading = By.xpath(
            "//*[self::h1 or self::h2 or self::h3]" +
            "[contains(normalize-space(),'Batch Owner') " +
            "or contains(normalize-space(),'POC')]");

    private final By serviceLineDropdown = By.cssSelector(
            "select[name='serviceLine'], select#serviceLineFilter, " +
            "select[formcontrolname='serviceLine'], select[formcontrolname='serviceLineId']");

    private final By learningPathDropdown = By.cssSelector(
            "select[name='learningPath'], select#learningPathFilter, " +
            "select[formcontrolname='learningPath'], select[formcontrolname='learningPathId']");

    private final By profileCards = By.cssSelector(
            "[class*='poc-card'], [class*='batch-owner-card'], [class*='profile-card'], " +
            "div[class*='card']:not(.modal-content):not(.modal-card)");

    private final By addBatchOwnerBtn = By.xpath(
            "//button[contains(normalize-space(),'Add Batch Owner') " +
            "or contains(normalize-space(),'Add POC') " +
            "or contains(normalize-space(),'New Batch Owner')]");

    private final By viewDetailsButtons = By.xpath(
            "//button[contains(normalize-space(),'View Details') " +
            "or contains(normalize-space(),'View') " +
            "or contains(normalize-space(),'Details')]");

    private final By modalOverlay = By.cssSelector("[class*='modal'], [role='dialog']");

    // ── Edit Batch Owner modal locators (FRD 2.4) ─────────────────────────────
    private final By pageTitleH1         = By.cssSelector("h1.page-title");
    private final By batchOwnerCards     = By.cssSelector("div.card");
    private final By editButtons         = By.cssSelector("button.btn-edit");
    private final By editModalOverlay    = By.cssSelector("div.modal-overlay, .modal-backdrop, app-modal");
    private final By editModalTitle      = By.cssSelector(".modal-overlay h2, .modal-header h2");
    private final By editModalInputs     = By.cssSelector(".modal-overlay input, form input");
    private final By editModalCancelBtn  = By.cssSelector(".modal-overlay button.btn-cancel");
    private final By editModalSubmitBtn  = By.cssSelector(".modal-overlay button.btn-primary");
    private final By successNotif        = By.cssSelector(
            ".toast, .alert-success, [class*='success'], [class*='toast']");
    private final By deleteButton        = By.cssSelector(
            "div.card button.btn-delete, div.card button.btn-danger");

    // ── Constructor ───────────────────────────────────────────────────────────
    public BatchOwnerPage(WebDriver driver) {
        super(driver);
    }

    // ── Page heading ──────────────────────────────────────────────────────────

    public boolean isPageHeadingVisible() {
        return isDisplayed(pageHeading);
    }

    // ── Service Line filter ───────────────────────────────────────────────────

    public boolean isServiceLineDropdownVisible() {
        return isDisplayed(serviceLineDropdown);
    }

    public void selectServiceLine(String serviceLineId) {
        try {
            Select select = new Select(driver.findElement(serviceLineDropdown));
            try {
                select.selectByValue(serviceLineId);
            } catch (Exception e1) {
                try {
                    select.selectByVisibleText(serviceLineId);
                } catch (Exception e2) {
                    // Partial match
                    select.getOptions().stream()
                            .filter(o -> o.getText().contains(serviceLineId)
                                      && !o.getAttribute("value").trim().isEmpty())
                            .findFirst()
                            .ifPresent(o -> select.selectByVisibleText(o.getText()));
                }
            }
        } catch (Exception ignored) {}
    }

    // ── Service Line filter (element access) ─────────────────────────────────

    public WebElement getServiceLineDropdownElement() {
        return driver.findElement(serviceLineDropdown);
    }

    public List<WebElement> getServiceLineOptions() {
        try {
            return new org.openqa.selenium.support.ui.Select(
                    driver.findElement(serviceLineDropdown)).getOptions();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ── Learning Path filter ──────────────────────────────────────────────────

    public boolean isLearningPathDropdownVisible() {
        return isDisplayed(learningPathDropdown);
    }

    public WebElement getLearningPathDropdownElement() {
        return driver.findElement(learningPathDropdown);
    }

    public void selectLearningPathByVisibleText(String visibleText) {
        try {
            new org.openqa.selenium.support.ui.Select(
                    driver.findElement(learningPathDropdown))
                    .selectByVisibleText(visibleText);
        } catch (Exception ignored) {}
    }

    public List<WebElement> getLearningPathOptions() {
        try {
            return new org.openqa.selenium.support.ui.Select(
                    driver.findElement(learningPathDropdown)).getOptions();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ── Profile cards ─────────────────────────────────────────────────────────

    public boolean areProfileCardsVisible() {
        try {
            return !driver.findElements(profileCards).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getProfileCards() {
        try {
            return driver.findElements(profileCards);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ── View Details ──────────────────────────────────────────────────────────

    public List<WebElement> getViewDetailsButtons() {
        try {
            return driver.findElements(viewDetailsButtons);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ── Add Batch Owner ───────────────────────────────────────────────────────

    public boolean isAddBatchOwnerBtnVisible() {
        return isDisplayed(addBatchOwnerBtn);
    }

    public AddBatchOwnerModal clickAddBatchOwner() {
        click(addBatchOwnerBtn);
        return new AddBatchOwnerModal(driver);
    }

    // ── Modal ─────────────────────────────────────────────────────────────────

    public boolean isModalVisible() {
        try {
            List<WebElement> modals = driver.findElements(modalOverlay);
            return modals.stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            return false;
        }
    }

    public void closeModal() {
        try {
            List<WebElement> closeBtns = driver.findElements(By.xpath(
                    "//*[contains(@class,'modal') or @role='dialog']" +
                    "//button[contains(normalize-space(),'Cancel') " +
                    "or contains(normalize-space(),'Close') " +
                    "or contains(normalize-space(),'×')]"));
            if (!closeBtns.isEmpty()) {
                closeBtns.get(0).click();
                return;
            }
            ((JavascriptExecutor) driver).executeScript(
                    "document.dispatchEvent(new KeyboardEvent('keydown',{'key':'Escape','bubbles':true}))");
        } catch (Exception ignored) {}
    }

    // ── Page title (FRD 2.4) ─────────────────────────────────────────────────

    public WebElement getPageTitleElement() {
        return driver.findElement(pageTitleH1);
    }

    // ── Batch Owner cards & edit buttons (FRD 2.4) ───────────────────────────

    public List<WebElement> getBatchOwnerCards() {
        try { return driver.findElements(batchOwnerCards); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<WebElement> getEditButtons() {
        try { return driver.findElements(editButtons); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public void clickFirstEditButton() {
        List<WebElement> btns = getEditButtons();
        if (!btns.isEmpty()) btns.get(0).click();
    }

    // ── Edit modal (FRD 2.4) ─────────────────────────────────────────────────

    public boolean isEditModalVisible() {
        try {
            List<WebElement> modals = driver.findElements(editModalOverlay);
            return modals.stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) { return false; }
    }

    public String getEditModalTitleText() {
        try {
            List<WebElement> titles = driver.findElements(editModalTitle);
            return titles.isEmpty() ? "" : titles.get(0).getText().trim();
        } catch (Exception e) { return ""; }
    }

    public List<WebElement> getEditModalInputs() {
        try { return driver.findElements(editModalInputs); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public void clickEditModalCancel() {
        waitForClickable(editModalCancelBtn).click();
    }

    public void clickEditModalSubmit() {
        waitForClickable(editModalSubmitBtn).click();
    }

    public boolean isSuccessNotifVisible() {
        try {
            List<WebElement> notifs = driver.findElements(successNotif);
            return notifs.stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) { return false; }
    }

    public List<WebElement> getDeleteButtons() {
        try { return driver.findElements(deleteButton); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public void waitForEditModalVisible() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(editModalOverlay));
    }

    public void waitForEditModalInvisible() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(editModalOverlay));
    }
}
