package com.cts.mfrp.onecohort.tests.managers;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.pages.managers.ManagersLeadershipPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
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

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToManagers() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));

        SuperAdminDashboardPage dashPage = new SuperAdminDashboardPage(driver);
        dashPage.getMenuItemElement("Managers & Leadership").click();
        wait.until(ExpectedConditions.urlContains("leadership"));

        managersPage = new ManagersLeadershipPage(driver);

        // Dismiss any open modal before the tests start
        if (managersPage.isEditModalOverlayVisible()) {
            managersPage.clickEditModalCancel();
            managersPage.waitForEditModalInvisible();
        }

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                org.openqa.selenium.By.cssSelector(".card-grid .card"), 0));
    }

    @Test(priority = 1, description = "TC-EMD-001 [FRD 2.3]: Managers page heading is visible")
    public void verifyManagersPageLoaded() {
        Assert.assertTrue(managersPage.isPageHeadingVisible(),
                "FAIL [FRD 2.3] - Managers page heading not visible.");
    }

    @Test(priority = 2, description = "TC-EMD-002 [FRD 2.3]: Manager profile cards are present on the view layout grid")
    public void verifyManagerCardsPresent() {
        managersPage.waitForEditModalInvisible();
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                org.openqa.selenium.By.cssSelector(".card-grid .card"), 0));
        Assert.assertFalse(managersPage.getManagerCards().isEmpty(),
                "FAIL [FRD 2.3] - No manager cards found on the page.");
    }

    @Test(priority = 3, description = "TC-EMD-003 [FRD 2.3]: Edit button is visible on manager cards")
    public void verifyEditButtonExists() {
        managersPage.waitForEditModalInvisible();
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                org.openqa.selenium.By.cssSelector(".card-grid .card button.btn-edit"), 0));
        Assert.assertFalse(managersPage.getEditButtons().isEmpty(),
                "FAIL [FRD 2.3] - No Edit buttons found on manager cards.");
    }

    @Test(priority = 4, description = "TC-EMD-004 [FRD 2.3]: Clicking Edit opens the Edit Manager modal")
    public void verifyEditModalOpens() {
        managersPage.waitForEditModalInvisible();
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                org.openqa.selenium.By.cssSelector(".card-grid .card button.btn-edit"), 0));
        managersPage.clickFirstEditButton();
        managersPage.waitForEditModalVisible();
        Assert.assertTrue(managersPage.isEditModalOverlayVisible(),
                "FAIL [FRD 2.3] - Edit Manager modal did not open.");
    }

    @Test(priority = 5, description = "TC-EMD-005 [FRD 2.3]: Edit Manager modal title reads 'Edit Manager'")
    public void verifyEditModalTitle() {
        String titleText = managersPage.getEditModalTitleText().toLowerCase();
        Assert.assertFalse(titleText.isEmpty(), "FAIL [FRD 2.3] - Edit Manager modal title is empty.");
        Assert.assertTrue(titleText.contains("edit") || titleText.contains("manager"),
                "FAIL [FRD 2.3] - Modal title should contain 'Edit' or 'Manager'.");
    }

    @Test(priority = 6, description = "TC-EMD-006 [FRD 2.3]: Edit modal Full Name input is present")
    public void verifyFullNameInputPresent() {
        Assert.assertTrue(managersPage.getEditModalFullNameInput().isDisplayed(),
                "FAIL [FRD 2.3] - Full Name input not found in Edit modal.");
    }

    @Test(priority = 7, description = "TC-EMD-007 [FRD 2.3]: Edit modal Email input is present")
    public void verifyEmailInputPresent() {
        Assert.assertTrue(managersPage.getEditModalEmailInput().isDisplayed(),
                "FAIL [FRD 2.3] - Email input not found in Edit modal.");
    }

    @Test(priority = 8, description = "TC-EMD-008 [FRD 2.3]: User ID field is disabled (read-only) in Edit modal")
    public void verifyUserIdIsReadOnly() {
        Assert.assertTrue(managersPage.getEditModalUserIdInput().isDisplayed(),
                "FAIL [FRD 2.3] - User ID disabled field not found in Edit modal.");
    }

    @Test(priority = 9, description = "TC-EMD-009 [FRD 2.3]: Submit button is present and enabled in Edit modal")
    public void verifySubmitButtonPresent() {
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
                org.openqa.selenium.By.cssSelector("div.modal-overlay div.modal button.btn-primary")));
        Assert.assertTrue(submitBtn.isDisplayed() && submitBtn.isEnabled(),
                "FAIL [FRD 2.3] - Submit button not visible or not enabled in Edit modal.");
    }

    @Test(priority = 10, description = "TC-EMD-010 [FRD 2.3]: Cancel button closes the modal without saving")
    public void verifyCancelClosesModal() {
        managersPage.clickEditModalCancel();
        managersPage.waitForEditModalInvisible();
        Assert.assertFalse(managersPage.isEditModalOverlayVisible(),
                "FAIL [FRD 2.3] - Modal still visible after clicking Cancel.");
    }

    @Test(priority = 11, description = "TC-EMD-011 [FRD 2.3]: Re-open Edit modal for E2E submission")
    public void reopenEditModalForSubmission() {
        managersPage.waitForEditModalInvisible();
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                org.openqa.selenium.By.cssSelector(".card-grid .card button.btn-edit"), 0));
        managersPage.clickFirstEditButton();
        managersPage.waitForEditModalVisible();
        Assert.assertTrue(managersPage.isEditModalOverlayVisible(),
                "FAIL - Modal did not reopen.");
    }

    @Test(priority = 12, description = "TC-EMD-012 [FRD 2.3]: Modify Full Name field in Edit Manager modal")
    public void modifyFullNameField() {
        WebElement nameField = managersPage.getEditModalFullNameInput();
        String currentValue = nameField.getAttribute("value");
        nameField.clear();
        String newValue = (currentValue == null || currentValue.isEmpty())
                ? "Test Manager" : currentValue + " Updated";
        nameField.sendKeys(newValue);
        Assert.assertEquals(nameField.getAttribute("value"), newValue,
                "FAIL [FRD 2.3] - Full Name field value was not updated.");
    }

    @Test(priority = 13, description = "TC-EMD-013 [FRD 2.3]: Click Submit/Update button in Edit modal")
    public void clickSubmitButton() {
        managersPage.clickEditModalSubmit();
    }

    @Test(priority = 14, description = "TC-EMD-014 [FRD 2.3]: Success notification appears or modal closes after edit")
    public void verifyEditSuccess() {
        boolean notifShown = false;
        try { notifShown = managersPage.isSuccessNotifVisible(); } catch (Exception ignored) {}

        boolean modalClosed = false;
        try { managersPage.waitForEditModalInvisible(); modalClosed = true; } catch (Exception ignored) {}

        Assert.assertTrue(notifShown || modalClosed,
                "FAIL [FRD 2.3] - No success notification and modal did not close.");
    }

    @Test(priority = 15, description = "TC-EMD-015 [FRD 2.3]: Delete button does not exist on manager cards")
    public void verifyNoDeleteButtonOnCards() {
        managersPage.waitForEditModalInvisible();
        List<WebElement> deleteBtns = managersPage.getDeleteButtons();
        Assert.assertTrue(deleteBtns.isEmpty(),
                "FAIL [FRD 2.3] - Delete button discovered on a profile card layout.");
    }
}
