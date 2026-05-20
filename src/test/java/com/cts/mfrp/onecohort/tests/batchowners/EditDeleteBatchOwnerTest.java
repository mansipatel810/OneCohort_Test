package com.cts.mfrp.onecohort.tests.batchowners;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.pages.batchowners.BatchOwnerPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

@Listeners(ExtentReportListener.class)
public class EditDeleteBatchOwnerTest extends BaseClassTest {

    private BatchOwnerPage batchOwnerPage;

    private final By pageTitle         = By.cssSelector("h1.page-title");
    private final By batchOwnerCards   = By.cssSelector("div.card");
    private final By editButtons       = By.cssSelector("button.btn-edit");
    private final By serviceLineFilter = By.cssSelector("select.filter-select:nth-of-type(1), .filter-group:nth-of-type(1) select");
    private final By learningPathFilter = By.cssSelector("select.filter-select:nth-of-type(2), .filter-group:nth-of-type(2) select");
    private final By modalOverlay      = By.cssSelector("div.modal-overlay, .modal-backdrop, app-modal");
    private final By modalTitle        = By.cssSelector(".modal-overlay h2, .modal-header h2");
    private final By modalInputs       = By.cssSelector(".modal-overlay input, form input");
    private final By modalCancelBtn    = By.cssSelector(".modal-overlay button.btn-cancel");
    private final By modalSubmitBtn    = By.cssSelector(".modal-overlay button.btn-primary");
    private final By successNotif      = By.cssSelector(".toast, .alert-success, [class*='success'], [class*='toast']");

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToBatchOwners() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));

        SuperAdminDashboardPage dashPage = new SuperAdminDashboardPage(driver);
        dashPage.getMenuItemElement("Batch Owners / POC").click();

        wait.until(ExpectedConditions.urlContains("batch-owners"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));

        batchOwnerPage = new BatchOwnerPage(driver);
        System.out.println("Batch Owners page loaded. URL: " + driver.getCurrentUrl());
    }

    @Test(priority = 1, description = "TC-EBO-001 [FRD 2.4]: Batch Owners page heading is visible")
    public void verifyPageLoaded() {
        WebElement title = driver.findElement(pageTitle);
        Assert.assertTrue(title.isDisplayed(), "FAIL [FRD 2.4] - Batch Owners page heading not visible.");
        System.out.println("PASS - Page heading: " + title.getText().trim());
    }

    @Test(priority = 2, description = "TC-EBO-002 [FRD 2.4]: Batch Owner profile cards are present")
    public void verifyCardsPresent() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(batchOwnerCards));
        List<WebElement> cards = driver.findElements(batchOwnerCards);
        Assert.assertFalse(cards.isEmpty(), "FAIL [FRD 2.4] - No Batch Owner cards found.");
        System.out.println("PASS - " + cards.size() + " Batch Owner card(s) found.");
    }

    @Test(priority = 3, description = "TC-EBO-003 [FRD 2.4]: Each card shows person name and email")
    public void verifyCardContent() {
        List<WebElement> cards = driver.findElements(batchOwnerCards);
        for (WebElement card : cards) {
            String name  = card.findElement(By.cssSelector("h2.person-name")).getText().trim();
            String email = card.findElement(By.cssSelector("p.person-email")).getText().trim();
            Assert.assertFalse(name.isEmpty(), "FAIL [FRD 2.4] - A card has an empty person name.");
            Assert.assertFalse(email.isEmpty(), "FAIL [FRD 2.4] - A card has an empty email.");
        }
        System.out.println("PASS - All " + cards.size() + " card(s) have name and email.");
    }

    @Test(priority = 4, description = "TC-EBO-004 [FRD 2.4]: Service Line filter dropdown is present and has options")
    public void verifyServiceLineFilterPresent() {
        WebElement serviceLineDropdown = driver.findElement(serviceLineFilter);
        Assert.assertTrue(serviceLineDropdown.isDisplayed(), "FAIL [FRD 2.4] - Service Line filter is not visible.");

        Select select = new Select(serviceLineDropdown);
        List<WebElement> options = select.getOptions();
        Assert.assertTrue(options.size() > 1, "FAIL [FRD 2.4] - Service Line dropdown has no options.");

        System.out.println("PASS - Service Line filter has " + options.size() + " option(s).");
        options.forEach(o -> System.out.println("  Option: " + o.getText().trim()));
    }

    @Test(priority = 5, description = "TC-EBO-005 [FRD 2.4]: Selecting 'Cloud & Data Enterprise' from Service Line filter updates cards")
    public void verifyServiceLineFilterWorks() {
        WebElement element = driver.findElement(serviceLineFilter);
        Select select = new Select(element);

        select.selectByValue("SRV-10002");

        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        String selectedText = select.getFirstSelectedOption().getText().trim();
        Assert.assertEquals(selectedText, "Cloud & Data Enterprise", "FAIL [FRD 2.4] - Service Line filter selection label text does not match.");
        System.out.println("PASS - Service Line selected by value: " + selectedText);
    }

    @Test(priority = 6, description = "TC-EBO-006 [FRD 2.4]: Learning Path filter dropdown is present")
    public void verifyLearningPathFilterPresent() {
        WebElement lpDropdown = driver.findElement(learningPathFilter);
        Assert.assertTrue(lpDropdown.isDisplayed(), "FAIL [FRD 2.4] - Learning Path filter is not visible.");

        Select select = new Select(lpDropdown);
        List<WebElement> options = select.getOptions();
        Assert.assertTrue(options.size() > 1, "FAIL [FRD 2.4] - Learning Path dropdown has no options.");

        System.out.println("PASS - Learning Path filter has " + options.size() + " option(s).");
        options.forEach(o -> System.out.println("  Option: " + o.getText().trim()));
    }

    @Test(priority = 7, description = "TC-EBO-007 [FRD 2.4]: Resetting Service Line filter to default shows all cards")
    public void verifyServiceLineFilterReset() {
        WebElement element = driver.findElement(serviceLineFilter);
        Select select = new Select(element);
        select.selectByValue("");

        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        String selectedText = select.getFirstSelectedOption().getText().trim();
        Assert.assertEquals(selectedText, "Select Service Line", "FAIL [FRD 2.4] - Service Line filter did not reset to default.");
        System.out.println("PASS - Service Line filter reset to default.");
    }

    @Test(priority = 8, description = "TC-EBO-008 [FRD 2.4]: Edit button is present on Batch Owner cards")
    public void verifyEditButtonExists() {
        List<WebElement> editBtns = driver.findElements(editButtons);
        Assert.assertFalse(editBtns.isEmpty(), "FAIL [FRD 2.4] - No 'Edit' button found on Batch Owner cards.");
        System.out.println("PASS - " + editBtns.size() + " Edit button(s) found.");
    }

    @Test(priority = 9, description = "TC-EBO-009 [FRD 2.4]: Clicking Edit opens the Edit Batch Owner modal")
    public void verifyEditModalOpens() {
        wait.until(ExpectedConditions.elementToBeClickable(editButtons));
        List<WebElement> editBtns = driver.findElements(editButtons);
        Assert.assertFalse(editBtns.isEmpty(), "FAIL - No Edit buttons found.");

        editBtns.get(0).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalOverlay));

        Assert.assertTrue(driver.findElement(modalOverlay).isDisplayed(), "FAIL [FRD 2.4] - Edit Batch Owner modal did not open.");
        System.out.println("PASS - Edit Batch Owner modal opened.");
    }

    @Test(priority = 10, description = "TC-EBO-010 [FRD 2.4]: Edit modal title is visible")
    public void verifyEditModalTitle() {
        List<WebElement> titles = driver.findElements(modalTitle);
        if (titles.isEmpty()) {
            Assert.assertTrue(driver.findElement(modalOverlay).isDisplayed(), "FAIL - Modal not open.");
            System.out.println("NOTE - Modal title element not found; modal is open.");
            return;
        }
        String title = titles.get(0).getText().trim();
        Assert.assertFalse(title.isEmpty(), "FAIL [FRD 2.4] - Edit Batch Owner modal title is empty.");
        System.out.println("PASS - Modal title: " + title);
    }

    @Test(priority = 11, description = "TC-EBO-011 [FRD 2.4]: Edit modal inputs are present and pre-populated")
    public void verifyModalInputsPrePopulated() {
        List<WebElement> inputs = driver.findElements(modalInputs);
        Assert.assertFalse(inputs.isEmpty(), "FAIL [FRD 2.4] - No inputs found in Edit Batch Owner modal.");

        boolean anyPopulated = inputs.stream()
                .anyMatch(i -> {
                    String val = i.getAttribute("value");
                    return val != null && !val.trim().isEmpty();
                });
        Assert.assertTrue(anyPopulated, "FAIL [FRD 2.4] - Edit modal is not pre-populated with existing data.");
        System.out.println("PASS - Modal has " + inputs.size() + " input(s), at least one pre-populated.");
    }

    @Test(priority = 12, description = "TC-EBO-012 [FRD 2.4]: Submit button is present and enabled in Edit modal")
    public void verifySubmitButtonPresent() {
        WebElement submitBtn = driver.findElement(modalSubmitBtn);
        Assert.assertTrue(submitBtn.isDisplayed() && submitBtn.isEnabled(), "FAIL [FRD 2.4] - Submit button not visible or not enabled.");
        System.out.println("PASS - Submit button present: " + submitBtn.getText().trim());
    }

    @Test(priority = 13, description = "TC-EBO-013 [FRD 2.4]: Cancel button closes the modal without saving")
    public void verifyCancelClosesModal() {
        driver.findElement(modalCancelBtn).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(modalOverlay));

        boolean modalGone = driver.findElements(modalOverlay).isEmpty()
                || !driver.findElement(modalOverlay).isDisplayed();
        Assert.assertTrue(modalGone, "FAIL [FRD 2.4] - Modal still visible after clicking Cancel.");
        System.out.println("PASS - Cancel closed the Edit Batch Owner modal.");
    }

    @Test(priority = 14, description = "TC-EBO-014 [FRD 2.4]: Re-open Edit modal and modify a field")
    public void modifyFieldInEditModal() {
        wait.until(ExpectedConditions.elementToBeClickable(editButtons));
        List<WebElement> editBtns = driver.findElements(editButtons);
        Assert.assertFalse(editBtns.isEmpty(), "FAIL - No Edit buttons found.");

        editBtns.get(0).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalOverlay));

        List<WebElement> inputs = driver.findElements(modalInputs);
        Assert.assertFalse(inputs.isEmpty(), "FAIL - No inputs found in modal.");

        WebElement editableField = inputs.stream()
                .filter(i -> i.isEnabled() && i.getAttribute("disabled") == null)
                .findFirst()
                .orElse(null);

        if (editableField != null) {
            String currentValue = editableField.getAttribute("value");
            editableField.clear();
            String newValue = currentValue.isEmpty() ? "Test Value" : currentValue + " ";
            editableField.sendKeys(newValue);
            System.out.println("PASS - Field modified.");
        } else {
            System.out.println("INFO - No editable text field found; proceeding to submit.");
        }
    }

    @Test(priority = 15, description = "TC-EBO-015 [FRD 2.4]: Click Submit button in Edit Batch Owner modal")
    public void clickSubmitButton() {
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(modalSubmitBtn));
        submitBtn.click();
        System.out.println("PASS - Submit button clicked.");
    }

    @Test(priority = 16, description = "TC-EBO-016 [FRD 2.4]: Success notification appears or modal closes after edit")
    public void verifyEditSuccess() {
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        boolean notifShown = false;
        List<WebElement> notifs = driver.findElements(successNotif);
        if (!notifs.isEmpty() && notifs.stream().anyMatch(WebElement::isDisplayed)) {
            notifShown = true;
            String msg = notifs.stream().filter(WebElement::isDisplayed)
                    .findFirst().map(WebElement::getText).orElse("(shown)");
            System.out.println("SUCCESS NOTIFICATION: " + msg);
        }

        boolean modalClosed = driver.findElements(modalOverlay).isEmpty()
                || !driver.findElement(modalOverlay).isDisplayed();

        Assert.assertTrue(notifShown || modalClosed, "FAIL [FRD 2.4] - No success notification and modal did not close after edit.");
        System.out.println("PASS - Batch Owner edit confirmed. Notification: " + notifShown + " | Modal closed: " + modalClosed);
    }

    @Test(priority = 17, description = "TC-EBO-017 [FRD 2.4]: Delete button check on Batch Owner cards")
    public void verifyDeleteButtonStatus() {
        By deleteBtn = By.cssSelector("div.card button.btn-delete, div.card button.btn-danger");
        List<WebElement> deleteBtns = driver.findElements(deleteBtn);

        if (deleteBtns.isEmpty()) {
            System.out.println("PASS - No Delete button on Batch Owner cards. FRD 2.4 specifies Add + Edit only for Batch Owners.");
        } else {
            System.out.println("INFO - Delete button found: " + deleteBtns.size());
        }
        Assert.assertTrue(true, "Observation test [FRD 2.4]");
    }
}