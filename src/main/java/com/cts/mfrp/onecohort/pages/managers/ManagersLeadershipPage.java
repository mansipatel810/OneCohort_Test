package com.cts.mfrp.onecohort.pages.managers;

import com.cts.mfrp.onecohort.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.Collections;
import java.util.List;



public class ManagersLeadershipPage extends BasePage {

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By pageHeading = By.xpath(
            "//*[self::h1 or self::h2 or self::h3]" +
            "[contains(normalize-space(),'Manager') " +
            "or contains(normalize-space(),'Leadership')]");

    private final By profileCards = By.cssSelector(
            "[class*='manager-card'], [class*='profile-card'], " +
            "div[class*='card']:not(.modal-content):not(.modal-card)");

    private final By cardNames = By.cssSelector(
            "[class*='card'] h3, [class*='card'] h4, " +
            "[class*='card'] [class*='name'], [class*='card'] p.font-bold");

    private final By viewDetailsButtons = By.xpath(
            "//button[contains(normalize-space(),'View Details') " +
            "or contains(normalize-space(),'View') " +
            "or contains(normalize-space(),'Details')]");

    private final By createManagerBtn = By.xpath(
            "//button[contains(normalize-space(),'Create Manager') " +
            "or contains(normalize-space(),'Add Manager') " +
            "or contains(normalize-space(),'New Manager')]");

    // Actual HTML: <button class="tab-btn active">Managers</button> <button class="tab-btn">Leaders</button>
    // Old selector [class*='tab'] was too broad — also matched div.tabs and span.tab-count.
    private final By filterTabs = By.cssSelector("button.tab-btn");

    // Actual HTML: <div class="modal-overlay"><div class="modal">...</div></div>
    // Old [class*='modal'] matched 6 parts of the modal (overlay, header, body, footer, close btn).
    private final By modalOverlay = By.cssSelector("div.modal-overlay");

    // ── Edit Manager modal locators (FRD 2.3) ─────────────────────────────────
    private final By managerCards        = By.cssSelector(".card-grid .card");
    private final By editButtons         = By.cssSelector(".card-grid .card button.btn-edit");
    private final By editModalOverlay    = By.cssSelector("div.modal-overlay");
    private final By editModalTitle      = By.cssSelector("div.modal-overlay div.modal h2");
    private final By editModalFullName   = By.cssSelector(
            "div.modal-overlay div.modal input[placeholder='Enter full name']");
    private final By editModalEmail      = By.cssSelector(
            "div.modal-overlay div.modal input[type='email']");
    private final By editModalUserIdDis  = By.cssSelector(
            "div.modal-overlay div.modal input[disabled]");
    private final By editModalCancelBtn  = By.cssSelector(
            "div.modal-overlay div.modal button.btn-cancel");
    private final By editModalSubmitBtn  = By.cssSelector(
            "div.modal-overlay div.modal button.btn-primary");
    private final By successNotif        = By.cssSelector(
            ".toast, .alert-success, [class*='success'], [class*='toast']");
    private final By deleteButton        = By.cssSelector(
            "div.card-grid div.card button.btn-delete, " +
            "div.card-grid div.card button.btn-danger");

    // ── Constructor ───────────────────────────────────────────────────────────
    public ManagersLeadershipPage(WebDriver driver) {
        super(driver);
    }

    // ── Page heading ──────────────────────────────────────────────────────────

    /**
     * Waits for the page heading to be visible before returning.
     * isDisplayed() alone relies only on implicit wait; explicit wait is safer for render.com.
     */
    public boolean isPageHeadingVisible() {
        try {
            waitForVisible(pageHeading);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public WebElement getPageHeadingElement() {
        return driver.findElement(pageHeading);
    }

    public boolean isCreateManagerBtnEnabled() {
        try { return driver.findElement(createManagerBtn).isEnabled(); }
        catch (Exception e) { return false; }
    }

    public WebElement getCreateManagerBtnElement() {
        return driver.findElement(createManagerBtn);
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

    public List<WebElement> getCardNames() {
        try {
            return driver.findElements(cardNames);
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

    // ── Create Manager ────────────────────────────────────────────────────────

    public boolean isCreateManagerBtnVisible() {
        return isDisplayed(createManagerBtn);
    }

    public CreateManagerModal clickCreateManager() {
        click(createManagerBtn);
        return new CreateManagerModal(driver);
    }

    // ── Role filter tabs ──────────────────────────────────────────────────────

    /**
     * Returns true if the filter tabs (button.tab-btn) are visible.
     * Uses explicit wait before findElements() — the tabs are rendered by Angular after
     * page load and driver.findElements() returns empty immediately without waiting.
     */
    public boolean areFilterTabsVisible() {
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(filterTabs));
            return !driver.findElements(filterTabs).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public List<WebElement> getFilterTabs() {
        try {
            return driver.findElements(filterTabs);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ── Modal ─────────────────────────────────────────────────────────────────

    /**
     * Returns true if the modal overlay (div.modal-overlay) is visible.
     * Uses explicit wait — modal is injected by Angular *ngIf after button click.
     */
    public boolean isModalVisible() {
        try {
            waitForVisible(modalOverlay);
            return true;
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

    // ── Manager cards & edit buttons (FRD 2.3) ───────────────────────────────

    public List<WebElement> getManagerCards() {
        try { return driver.findElements(managerCards); }
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

    // ── Edit modal (FRD 2.3) ─────────────────────────────────────────────────

    public boolean isEditModalOverlayVisible() {
        return isDisplayed(editModalOverlay);
    }

    public String getEditModalTitleText() {
        try { return getText(editModalTitle); } catch (Exception e) { return ""; }
    }

    public WebElement getEditModalFullNameInput() {
        return waitForVisible(editModalFullName);
    }

    public WebElement getEditModalEmailInput() {
        return waitForVisible(editModalEmail);
    }

    public WebElement getEditModalUserIdInput() {
        return waitForVisible(editModalUserIdDis);
    }

    public boolean isEditModalUserIdDisabled() {
        try {
            WebElement el = driver.findElement(editModalUserIdDis);
            return el.getAttribute("disabled") != null;
        } catch (Exception e) { return false; }
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
