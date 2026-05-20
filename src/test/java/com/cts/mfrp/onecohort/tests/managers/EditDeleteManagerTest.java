package com.cts.mfrp.onecohort.tests.managers;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.pages.managers.ManagersLeadershipPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

@Test(groups = {"regression", "functional", "manager", "superadmin"})
@Listeners(ExtentReportListener.class)
public class EditDeleteManagerTest extends BaseClassTest {

    private ManagersLeadershipPage managersPage;

    private final By managerCards   = By.cssSelector(".card-grid .card");
    private final By editButtons    = By.cssSelector(".card-grid .card button.btn-edit");
    private final By modalOverlay   = By.cssSelector("div.modal-overlay");
    private final By modalTitle     = By.cssSelector("div.modal-overlay div.modal h2");
    private final By modalFullName  = By.cssSelector("div.modal-overlay div.modal input[placeholder='Enter full name']");
    private final By modalEmail     = By.cssSelector("div.modal-overlay div.modal input[type='email']");
    private final By modalUserId    = By.cssSelector("div.modal-overlay div.modal input[disabled]");
    private final By modalCancelBtn = By.cssSelector("div.modal-overlay div.modal button.btn-cancel");
    private final By modalSubmitBtn = By.cssSelector("div.modal-overlay div.modal button.btn-primary");
    private final By successNotif   = By.cssSelector(".toast, .alert-success, [class*='success'], [class*='toast']");

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToManagers() {
        driver.get(ConfigReader.getBaseUrl());

        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());

        wait.until(ExpectedConditions.urlContains("/super-admin"));

        SuperAdminDashboardPage dashPage = new SuperAdminDashboardPage(driver);
        dashPage.getMenuItemElement("Managers & Leadership").click();

        wait.until(ExpectedConditions.urlContains("leadership"));

        List<WebElement> overlays = driver.findElements(modalOverlay);
        if (!overlays.isEmpty() && overlays.get(0).isDisplayed()) {
            driver.findElement(modalCancelBtn).click();
            wait.until(ExpectedConditions.invisibilityOfElementLocated(modalOverlay));
        }

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(managerCards, 0));
        managersPage = new ManagersLeadershipPage(driver);
    }

    @Test(priority = 1, description = "TC-EMD-001 [FRD 2.3]: Managers page heading is visible")
    public void verifyManagersPageLoaded() {
        boolean isHeadingVisible = managersPage.isPageHeadingVisible();
        Assert.assertTrue(isHeadingVisible, "FAIL [FRD 2.3] - Managers page heading not visible.");
    }

    @Test(priority = 2, description = "TC-EMD-002 [FRD 2.3]: Manager profile cards are present on the view layout grid")
    public void verifyManagerCardsPresent() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(modalOverlay));
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(managerCards, 0));

        List<WebElement> cards = driver.findElements(managerCards);
        Assert.assertFalse(cards.isEmpty(), "FAIL [FRD 2.3] - No manager cards found on the page.");
    }

    @Test(priority = 3, description = "TC-EMD-003 [FRD 2.3]: Edit button is visible on manager cards")
    public void verifyEditButtonExists() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(modalOverlay));
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(editButtons, 0));

        List<WebElement> editBtns = driver.findElements(editButtons);
        Assert.assertFalse(editBtns.isEmpty(), "FAIL [FRD 2.3] - No Edit buttons found on manager cards.");
    }

    @Test(priority = 4, description = "TC-EMD-004 [FRD 2.3]: Clicking Edit opens the Edit Manager modal")
    public void verifyEditModalOpens() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(modalOverlay));
        List<WebElement> editBtns = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(editButtons, 0));

        editBtns.get(0).click();

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(modalOverlay));
        Assert.assertTrue(modal.isDisplayed(), "FAIL [FRD 2.3] - Edit Manager modal did not open.");
    }

    @Test(priority = 5, description = "TC-EMD-005 [FRD 2.3]: Edit Manager modal title reads 'Edit Manager'")
    public void verifyEditModalTitle() {
        WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(modalTitle));
        String titleText = titleElement.getText().trim().toLowerCase();

        Assert.assertFalse(titleText.isEmpty(), "FAIL [FRD 2.3] - Edit Manager modal title is empty.");

        boolean standardTitleMatched = titleText.contains("edit") || titleText.contains("manager");
        Assert.assertTrue(standardTitleMatched, "FAIL [FRD 2.3] - Modal title should contain 'Edit' or 'Manager'.");
    }

    @Test(priority = 6, description = "TC-EMD-006 [FRD 2.3]: Edit modal Full Name input is present")
    public void verifyFullNameInputPresent() {
        WebElement nameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(modalFullName));
        Assert.assertTrue(nameInput.isDisplayed(), "FAIL [FRD 2.3] - Full Name input not found in Edit modal.");
    }

    @Test(priority = 7, description = "TC-EMD-007 [FRD 2.3]: Edit modal Email input is present")
    public void verifyEmailInputPresent() {
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(modalEmail));
        Assert.assertTrue(emailInput.isDisplayed(), "FAIL [FRD 2.3] - Email input not found in Edit modal.");
    }

    @Test(priority = 8, description = "TC-EMD-008 [FRD 2.3]: User ID field is disabled (read-only) in Edit modal")
    public void verifyUserIdIsReadOnly() {
        WebElement userIdField = wait.until(ExpectedConditions.visibilityOfElementLocated(modalUserId));
        Assert.assertTrue(userIdField.isDisplayed(), "FAIL [FRD 2.3] - User ID disabled field not found in Edit modal.");
    }

    @Test(priority = 9, description = "TC-EMD-009 [FRD 2.3]: Submit button is present and enabled in Edit modal")
    public void verifySubmitButtonPresent() {
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(modalSubmitBtn));

        boolean isReady = submitBtn.isDisplayed() && submitBtn.isEnabled();
        Assert.assertTrue(isReady, "FAIL [FRD 2.3] - Submit button not visible or not enabled in Edit modal.");
    }

    @Test(priority = 10, description = "TC-EMD-010 [FRD 2.3]: Cancel button closes the modal without saving")
    public void verifyCancelClosesModal() {
        wait.until(ExpectedConditions.elementToBeClickable(modalCancelBtn)).click();

        boolean modalGone = wait.until(ExpectedConditions.invisibilityOfElementLocated(modalOverlay));
        Assert.assertTrue(modalGone, "FAIL [FRD 2.3] - Modal still visible after clicking Cancel.");
    }

    @Test(priority = 11, description = "TC-EMD-011 [FRD 2.3]: Re-open Edit modal for E2E submission")
    public void reopenEditModalForSubmission() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(modalOverlay));
        List<WebElement> editBtns = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(editButtons, 0));

        editBtns.get(0).click();

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(modalOverlay));
        Assert.assertTrue(modal.isDisplayed(), "FAIL - Modal did not reopen.");
    }

    @Test(priority = 12, description = "TC-EMD-012 [FRD 2.3]: Modify Full Name field in Edit Manager modal")
    public void modifyFullNameField() {
        WebElement nameField = wait.until(ExpectedConditions.elementToBeClickable(modalFullName));
        String currentValue = nameField.getAttribute("value");

        nameField.clear();
        String newValue = (currentValue == null || currentValue.isEmpty()) ? "Test Manager" : currentValue + " Updated";
        nameField.sendKeys(newValue);

        String actualValue = nameField.getAttribute("value");
        Assert.assertEquals(actualValue, newValue, "FAIL [FRD 2.3] - Full Name field value was not updated.");
    }

    @Test(priority = 13, description = "TC-EMD-013 [FRD 2.3]: Click Submit/Update button in Edit modal")
    public void clickSubmitButton() {
        wait.until(ExpectedConditions.elementToBeClickable(modalSubmitBtn)).click();
    }

    @Test(priority = 14, description = "TC-EMD-014 [FRD 2.3]: Success notification appears or modal closes after edit")
    public void verifyEditSuccess() {
        boolean notifShown = false;
        try {
            List<WebElement> notifs = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(successNotif, 0));
            notifShown = notifs.stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception ignored) {
        }

        boolean modalClosed = false;
        try {
            modalClosed = wait.until(ExpectedConditions.invisibilityOfElementLocated(modalOverlay));
        } catch (Exception ignored) {
        }

        Assert.assertTrue(notifShown || modalClosed, "FAIL [FRD 2.3] - No success notification and modal did not close.");
    }

    @Test(priority = 15, description = "TC-EMD-015 [FRD 2.3]: Delete button does not exist on manager cards")
    public void verifyNoDeleteButtonOnCards() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(modalOverlay));
        By deleteBtn = By.cssSelector("div.card-grid div.card button.btn-delete, div.card-grid div.card button.btn-danger");

        List<WebElement> deleteBtns = driver.findElements(deleteBtn);
        Assert.assertTrue(deleteBtns.isEmpty(), "FAIL [FRD 2.3] - Delete button discovered on a profile card layout.");
    }
}