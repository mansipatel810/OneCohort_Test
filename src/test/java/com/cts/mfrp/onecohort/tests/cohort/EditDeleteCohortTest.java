package com.cts.mfrp.onecohort.tests.cohort;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.pages.cohort.CohortManagementPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

@Test(groups = {"regression", "functional", "cohort", "superadmin"})
@Listeners(ExtentReportListener.class)
public class EditDeleteCohortTest extends BaseClassTest {

    private CohortManagementPage cohortPage;
    private SuperAdminDashboardPage dashPage;

    private final By editBatchOwnerDd = By.name("batchOwner");
    private final By editTrainerDd    = By.name("trainer");
    private final By updateCohortBtn  = By.className("btn-modal-primary");
    private final By modalTitle       = By.cssSelector(".modal-header h5");
    private final By disabledInputs   = By.cssSelector(".modal-body input[disabled]");

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToCohortManagement() {
        driver.get(ConfigReader.getBaseUrl());

        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));
        dashPage = new SuperAdminDashboardPage(driver);
        WebElement cohortNavItem = dashPage.getMenuItemElement("Cohort Management");
        cohortNavItem.click();

        cohortPage = new CohortManagementPage(driver);
        try {
            cohortPage.waitForTableToLoad();
        } catch (Exception ignored) {
        }
    }

    @Test(priority = 1, description = "Verify that the Cohort management table is visible on the screen")
    public void verifyTableVisible() {
        Assert.assertTrue(cohortPage.isTableVisible(), "Cohort data management table is not visible.");
    }

    @Test(priority = 2, description = "Verify that the dashboard table contains active cohort rows")
    public void verifyTableHasRows() {
        List<WebElement> rows = cohortPage.getTableRows();
        Assert.assertFalse(rows.isEmpty(), "The cohort table is empty. Cannot verify edit/delete actions.");
    }

    @Test(priority = 3, description = "Verify that the row item edit buttons are rendering properly")
    public void verifyEditButtonsExist() {
        List<WebElement> editBtns = cohortPage.getEditButtons();
        Assert.assertFalse(editBtns.isEmpty(), "No edit action buttons found inside the table records.");
    }

    @Test(priority = 4, description = "Verify that clicking the Edit button activates the modal view overlay")
    public void verifyEditModalOpens() {
        cohortPage.clickFirstEditBtn();
        cohortPage.waitForModalVisible();
        Assert.assertTrue(cohortPage.isModalCardVisible(), "The Edit Cohort popup window did not open.");
    }

    @Test(priority = 5, description = "Verify that the header label displays 'Edit Cohort'")
    public void verifyEditModalTitle() {
        WebElement headerTitle = driver.findElement(modalTitle);
        String titleText = headerTitle.getText().trim();
        Assert.assertEquals(titleText, "Edit Cohort", "The modal window header title does not match.");
    }

    @Test(priority = 6, description = "Verify that locked properties are configured as read-only/disabled fields")
    public void verifyReadOnlyFieldsAreDisabled() {
        List<WebElement> disabledFields = driver.findElements(disabledInputs);
        Assert.assertTrue(disabledFields.size() >= 3, "Expected system read-only input elements are not disabled.");
    }

    @Test(priority = 7, description = "Verify that the Batch Owner selection dropdown is enabled and interactive")
    public void verifyBatchOwnerDropdownEditable() {
        WebElement batchOwnerSelect = driver.findElement(editBatchOwnerDd);
        Assert.assertTrue(batchOwnerSelect.isEnabled(), "The Batch Owner dropdown is unexpectedly locked/disabled.");
    }

    @Test(priority = 8, description = "Verify that the Trainer selection dropdown is enabled and interactive")
    public void verifyTrainerDropdownEditable() {
        WebElement trainerSelect = driver.findElement(editTrainerDd);
        Assert.assertTrue(trainerSelect.isEnabled(), "The Trainer dropdown is unexpectedly locked/disabled.");
    }

    @Test(priority = 9, description = "Verify that the 'Update Cohort' action execution button is present")
    public void verifyUpdateCohortButtonPresent() {
        WebElement updateBtn = driver.findElement(updateCohortBtn);
        Assert.assertTrue(updateBtn.isDisplayed() && updateBtn.isEnabled(), "The Update Cohort submission button is missing or disabled.");
    }

    @Test(priority = 10, description = "Verify that clicking the Cancel button safely closes the active modal view")
    public void verifyCancelButtonClosesModal() {
        cohortPage.closeModal();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("modal-card")));

        boolean isModalPresent = cohortPage.isModalCardVisible();
        Assert.assertFalse(isModalPresent, "The edit modal overlay remained open after clicking Cancel.");
    }

    @Test(priority = 11, description = "Re-open the configuration modal to run subsequent functional tests")
    public void openEditModalForSubmission() {
        cohortPage.clickFirstEditBtn();
        cohortPage.waitForModalVisible();
        Assert.assertTrue(cohortPage.isModalCardVisible(), "Failed to open edit modal configuration window context.");
    }

    @Test(priority = 12, description = "Change the selection index on the Trainer dropdown menu input target")
    public void changeTrainerDropdownValue() {
        WebElement trainerElement = driver.findElement(editTrainerDd);
        Select trainerSelect = new Select(trainerElement);

        trainerSelect.selectByIndex(1);
        String selectedValue = trainerSelect.getFirstSelectedOption().getText();

        Assert.assertFalse(selectedValue.isEmpty(), "Trainer element drop-down selection failed to populate.");
    }

    @Test(priority = 13, description = "Execute form changes via the submit button element target click")
    public void clickUpdateCohortButton() {
        WebElement updateBtn = driver.findElement(updateCohortBtn);
        updateBtn.click();
    }

    @Test(priority = 14, description = "Verify that the popup window automatically dismisses post submission processing")
    public void verifyEditSuccessToast() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("modal-card")));
        boolean isModalOpen = cohortPage.isModalCardVisible();
        Assert.assertFalse(isModalOpen, "The configuration edit modal did not automatically close after form submission.");
    }

    @Test(priority = 15, description = "Verify that the delete tool element incorporates standard visual warning alert styles")
    public void verifyDeleteButtonIsRed() {
        List<WebElement> deleteBtns = cohortPage.getDeleteButtons();
        WebElement firstDeleteBtn = deleteBtns.get(0);

        String styleClass = firstDeleteBtn.getAttribute("class");
        Assert.assertTrue(styleClass.contains("text-danger"), "The table row delete execution option does not feature red danger warning styles.");
    }

    @Test(priority = 16, description = "Verify that clicking the delete option pops open a browser window alert confirmation prompt")
    public void verifyDeleteConfirmationAppears() {
        cohortPage.clickFirstDeleteBtn();
        Alert navigationAlert = wait.until(ExpectedConditions.alertIsPresent());
        Assert.assertNotNull(navigationAlert, "No confirmation prompt message alert layout box showed up upon selecting data row delete options.");

        navigationAlert.dismiss();
    }
}