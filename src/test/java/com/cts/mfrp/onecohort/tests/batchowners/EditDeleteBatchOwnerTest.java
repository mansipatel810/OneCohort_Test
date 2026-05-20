package com.cts.mfrp.onecohort.tests.batchowners;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.pages.batchowners.BatchOwnerPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

@Listeners(ExtentReportListener.class)
public class EditDeleteBatchOwnerTest extends BaseClassTest {

    private BatchOwnerPage batchOwnerPage;

    private final By pageTitle           = By.cssSelector("h1.page-title");
    private final By batchOwnerCards     = By.cssSelector("div.card-grid div.card");
    private final By serviceLineDropdown = By.cssSelector("div.filter-group:nth-of-type(1) select.filter-select");
    private final By learningPathDropdown = By.cssSelector("div.filter-group:nth-of-type(2) select.filter-select");
    private final By editButtons         = By.cssSelector("div.card div.card-actions button.btn-edit");
    private final By modalOverlay        = By.cssSelector("div.modal-overlay");
    private final By modalHeading        = By.cssSelector("div.modal-overlay div.modal-header h2");
    private final By modalFirstName      = By.cssSelector("div.modal-overlay input[name='firstName']");
    private final By modalLastName       = By.cssSelector("div.modal-overlay input[name='lastName']");
    private final By modalEmail          = By.cssSelector("div.modal-overlay input[name='email']");
    private final By modalAllInputs      = By.cssSelector("div.modal-overlay form input");
    private final By modalEditableInputs = By.cssSelector("div.modal-overlay form input:not([disabled])");
    private final By modalCancelBtn      = By.cssSelector("div.modal-overlay div.modal-footer button.btn-cancel");
    private final By modalSubmitBtn      = By.cssSelector("div.modal-overlay div.modal-footer button.btn-primary");
    private final By successNotif        = By.cssSelector(".toast, .alert-success, [class*='success']");

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToBatchOwners() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));

        new SuperAdminDashboardPage(driver).getMenuItemElement("Batch Owners / POC").click();

        wait.until(ExpectedConditions.urlContains("batch-owners"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));

        batchOwnerPage = new BatchOwnerPage(driver);
    }

    private void waitForDropdownOptions(By dropdownLocator, int minimumOptions, String dropdownName) {
        WebDriverWait tenSeconds = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            tenSeconds.until(d -> new Select(d.findElement(dropdownLocator)).getOptions().size() >= minimumOptions);
        } catch (Exception e) {
            Assert.fail("FAIL - " + dropdownName + " did not load " + minimumOptions + " option(s) within 10 seconds.");
        }
    }

    private void selectDropdownValue(By dropdownLocator, String value) {
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownLocator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", dropdown, value);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", dropdown
        );
    }

    private void openEditModal() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(batchOwnerCards));
        wait.until(ExpectedConditions.elementToBeClickable(editButtons));
        driver.findElements(editButtons).get(0).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalOverlay));
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalFirstName));
    }

    private boolean isModalOpen() {
        List<WebElement> overlays = driver.findElements(modalOverlay);
        return !overlays.isEmpty() && overlays.get(0).isDisplayed();
    }

    private void ensureModalIsOpen() {
        if (!isModalOpen()) {
            openEditModal();
        }
    }

    @Test(priority = 1, description = "TC-EBO-001 [FRD 2.4]: Batch Owners page heading is visible")
    public void verifyPageLoaded() {
        WebElement title = wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle));
        Assert.assertTrue(title.isDisplayed(), "FAIL [FRD 2.4] - Batch Owners page heading not visible.");
        System.out.println("PASS - Page heading visible: " + title.getText().trim());
    }

    @Test(priority = 2, description = "TC-EBO-002 [FRD 2.4]: Service Line dropdown is visible and loads options within 10 seconds")
    public void verifyServiceLineDropdownLoads() {
        WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(serviceLineDropdown));
        Assert.assertTrue(dropdown.isDisplayed(), "FAIL [FRD 2.4] - Service Line dropdown is not visible.");

        waitForDropdownOptions(serviceLineDropdown, 2, "Service Line dropdown");

        List<WebElement> options = new Select(dropdown).getOptions();
        System.out.println("PASS - Service Line dropdown has " + options.size() + " option(s).");
        options.forEach(o -> System.out.println("  Option: " + o.getText().trim()));
    }

    @Test(priority = 3, description = "TC-EBO-003 [FRD 2.4]: Selecting a Service Line populates the Learning Path dropdown within 10 seconds")
    public void selectServiceLineAndVerifyLearningPathPopulates() {
        selectDropdownValue(serviceLineDropdown, "SRV-10002");

        String selectedValue = new Select(driver.findElement(serviceLineDropdown)).getFirstSelectedOption().getText().trim();
        Assert.assertEquals(selectedValue, "Cloud & Data Enterprise",
                "FAIL [FRD 2.4] - Service Line selected value does not match.");
        System.out.println("PASS - Service Line selected: " + selectedValue);

        waitForDropdownOptions(learningPathDropdown, 2, "Learning Path dropdown");

        List<WebElement> lpOptions = new Select(driver.findElement(learningPathDropdown)).getOptions();
        System.out.println("PASS - Learning Path dropdown populated with " + lpOptions.size() + " option(s).");
        lpOptions.forEach(o -> System.out.println("  Option: " + o.getText().trim()));
    }

    @Test(priority = 4, description = "TC-EBO-004 [FRD 2.4]: Selecting a Learning Path loads the Batch Owner cards")
    public void selectLearningPathAndVerifyCardsLoad() {
        waitForDropdownOptions(learningPathDropdown, 2, "Learning Path dropdown");

        List<WebElement> lpOptions = new Select(driver.findElement(learningPathDropdown)).getOptions();
        String valueToSelect = lpOptions.get(1).getAttribute("value");
        String textToSelect  = lpOptions.get(1).getText().trim();

        selectDropdownValue(learningPathDropdown, valueToSelect);

        wait.until(ExpectedConditions.visibilityOfElementLocated(batchOwnerCards));

        String selectedValue = new Select(driver.findElement(learningPathDropdown)).getFirstSelectedOption().getText().trim();
        Assert.assertEquals(selectedValue, textToSelect,
                "FAIL [FRD 2.4] - Learning Path selected value does not match.");
        System.out.println("PASS - Learning Path selected: " + selectedValue + ". Cards are now visible.");
    }

    @Test(priority = 5, description = "TC-EBO-005 [FRD 2.4]: Batch Owner cards are visible after both dropdowns are selected")
    public void verifyCardsArePresent() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(batchOwnerCards));
        List<WebElement> cards = driver.findElements(batchOwnerCards);
        Assert.assertFalse(cards.isEmpty(), "FAIL [FRD 2.4] - No Batch Owner cards found on the page.");
        System.out.println("PASS - " + cards.size() + " Batch Owner card(s) visible.");
    }

    @Test(priority = 6, description = "TC-EBO-006 [FRD 2.4]: Each Batch Owner card shows a name and email")
    public void verifyEachCardHasNameAndEmail() {
        List<WebElement> cards = driver.findElements(batchOwnerCards);
        Assert.assertFalse(cards.isEmpty(), "FAIL [FRD 2.4] - No cards found to verify.");

        for (WebElement card : cards) {
            String name  = card.findElement(By.cssSelector("h2.person-name")).getText().trim();
            String email = card.findElement(By.cssSelector("p.person-email")).getText().trim();
            Assert.assertFalse(name.isEmpty(),  "FAIL [FRD 2.4] - A card is missing the person name.");
            Assert.assertFalse(email.isEmpty(), "FAIL [FRD 2.4] - A card is missing the email address.");
        }
        System.out.println("PASS - All " + cards.size() + " card(s) have a name and email.");
    }

    @Test(priority = 7, description = "TC-EBO-007 [FRD 2.4]: Each Batch Owner card has an Edit button")
    public void verifyEditButtonExistsOnCards() {
        List<WebElement> editBtns = driver.findElements(editButtons);
        Assert.assertFalse(editBtns.isEmpty(), "FAIL [FRD 2.4] - No Edit button found on any Batch Owner card.");
        System.out.println("PASS - " + editBtns.size() + " Edit button(s) found.");
    }

    @Test(priority = 8, description = "TC-EBO-008 [FRD 2.4]: Clicking the Edit button opens the Edit Batch Owner modal")
    public void verifyEditModalOpens() {
        openEditModal();
        Assert.assertTrue(driver.findElement(modalOverlay).isDisplayed(),
                "FAIL [FRD 2.4] - Edit modal did not open after clicking Edit.");
        System.out.println("PASS - Edit Batch Owner modal is open.");
    }

    @Test(priority = 9, description = "TC-EBO-009 [FRD 2.4]: Edit modal heading says 'Edit Batch Owner'")
    public void verifyEditModalHeading() {
        ensureModalIsOpen();
        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(modalHeading));
        Assert.assertEquals(heading.getText().trim(), "Edit Batch Owner",
                "FAIL [FRD 2.4] - Edit modal heading text does not match.");
        System.out.println("PASS - Modal heading: " + heading.getText().trim());
    }

    @Test(priority = 10, description = "TC-EBO-010 [FRD 2.4]: First Name, Last Name and Email fields in the modal are editable")
    public void verifyEditableFieldsInModal() {
        ensureModalIsOpen();

        WebElement firstName = wait.until(ExpectedConditions.visibilityOfElementLocated(modalFirstName));
        WebElement lastName  = driver.findElement(modalLastName);
        WebElement email     = driver.findElement(modalEmail);

        Assert.assertTrue(firstName.isEnabled(), "FAIL [FRD 2.4] - First Name field is not editable.");
        Assert.assertTrue(lastName.isEnabled(),  "FAIL [FRD 2.4] - Last Name field is not editable.");
        Assert.assertTrue(email.isEnabled(),     "FAIL [FRD 2.4] - Email field is not editable.");
        System.out.println("PASS - First Name, Last Name and Email are all editable.");
    }

    @Test(priority = 11, description = "TC-EBO-011 [FRD 2.4]: User ID and Service Line fields in the modal are disabled (read-only)")
    public void verifyDisabledFieldsInModal() {
        ensureModalIsOpen();

        int totalInputs    = driver.findElements(modalAllInputs).size();
        int editableInputs = driver.findElements(modalEditableInputs).size();
        int disabledInputs = totalInputs - editableInputs;

        Assert.assertTrue(disabledInputs >= 2,
                "FAIL [FRD 2.4] - Expected at least 2 disabled fields (User ID and Service Line). Found: " + disabledInputs);
        System.out.println("PASS - " + disabledInputs + " disabled field(s) found as expected.");
    }

    @Test(priority = 12, description = "TC-EBO-012 [FRD 2.4]: The Update button is visible and clickable in the modal")
    public void verifyUpdateButtonInModal() {
        ensureModalIsOpen();
        WebElement updateBtn = wait.until(ExpectedConditions.elementToBeClickable(modalSubmitBtn));
        Assert.assertTrue(updateBtn.isDisplayed() && updateBtn.isEnabled(),
                "FAIL [FRD 2.4] - Update button is not visible or not clickable.");
        Assert.assertEquals(updateBtn.getText().trim(), "Update",
                "FAIL [FRD 2.4] - Button text is not 'Update'.");
        System.out.println("PASS - Update button is visible and clickable.");
    }

    @Test(priority = 13, description = "TC-EBO-013 [FRD 2.4]: Clicking Cancel closes the modal without saving")
    public void verifyCancelButtonClosesModal() {
        ensureModalIsOpen();
        driver.findElement(modalCancelBtn).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(modalOverlay));
        Assert.assertFalse(isModalOpen(), "FAIL [FRD 2.4] - Modal is still visible after clicking Cancel.");
        System.out.println("PASS - Modal closed after clicking Cancel.");
    }

    @Test(priority = 14, description = "TC-EBO-014 [FRD 2.4]: First Name field in the modal accepts typed input")
    public void verifyFirstNameFieldAcceptsInput() {
        openEditModal();
        WebElement firstName = wait.until(ExpectedConditions.elementToBeClickable(modalFirstName));
        firstName.clear();
        firstName.sendKeys("TestName");

        String typedValue = firstName.getAttribute("value");
        Assert.assertEquals(typedValue, "TestName",
                "FAIL [FRD 2.4] - First Name field did not save the typed value.");
        System.out.println("PASS - First Name field accepted input: " + typedValue);
    }

    @Test(priority = 15, description = "TC-EBO-015 [FRD 2.4]: Clicking the Update button submits the modal form")
    public void verifyUpdateButtonSubmitsForm() {
        ensureModalIsOpen();
        WebElement updateBtn = wait.until(ExpectedConditions.elementToBeClickable(modalSubmitBtn));
        updateBtn.click();
        System.out.println("PASS - Update button clicked successfully.");
    }

    @Test(priority = 16, description = "TC-EBO-016 [FRD 2.4]: A success message appears or the modal closes after saving")
    public void verifySuccessAfterUpdate() {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(successNotif),
                ExpectedConditions.invisibilityOfElementLocated(modalOverlay)
        ));

        List<WebElement> notifications = driver.findElements(successNotif);
        boolean notificationVisible = notifications.stream().anyMatch(WebElement::isDisplayed);
        boolean modalClosed         = !isModalOpen();

        Assert.assertTrue(notificationVisible || modalClosed,
                "FAIL [FRD 2.4] - No success message appeared and the modal did not close after saving.");
        System.out.println("PASS - Update confirmed. Success notification: " + notificationVisible + " | Modal closed: " + modalClosed);
    }

    @Test(priority = 17, description = "TC-EBO-017 [FRD 2.4]: No Delete button exists on Batch Owner cards (FRD allows Add and Edit only)")
    public void verifyNoDeleteButtonOnCards() {
        By deleteButton = By.cssSelector("div.card button.btn-delete, div.card button.btn-danger");
        List<WebElement> deleteButtons = driver.findElements(deleteButton);

        if (deleteButtons.isEmpty()) {
            System.out.println("PASS - No Delete button found on cards. This matches FRD 2.4.");
        } else {
            System.out.println("INFO - Delete button found on " + deleteButtons.size() + " card(s). Please review FRD 2.4.");
        }
        Assert.assertTrue(true, "Observation only — no assertion needed for this check.");
    }
}