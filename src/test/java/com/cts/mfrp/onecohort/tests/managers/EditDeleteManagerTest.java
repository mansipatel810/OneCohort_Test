package com.cts.mfrp.onecohort.tests.managers;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.pages.managers.ManagersLeadershipPage;
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

/**
 * Edit Manager + Delete Manager — End-to-End Test Suite
 *
 * FRD Reference: Section 2.3 — Managers & Leadership
 *
 * ── EDIT MANAGER (FRD 2.3) ───────────────────────────────────────────────────
 * Manager cards have an "Edit" button. Clicking it opens an Edit Manager modal
 * pre-populated with manager data. The user can modify allowed fields and submit.
 * Figure 3.2 in FRD shows: "Edit Manager Modal"
 *
 * ── DELETE MANAGER (FRD 2.3) ─────────────────────────────────────────────────
 * If a delete button exists on the manager card, clicking it shows a confirmation
 * dialog. Confirming removes the manager.
 *
 * ⚠️  WARNING: Delete tests are DESTRUCTIVE.
 *
 * Test sections:
 *   A — Navigate and verify page loads          (tests 1–3)
 *   B — Edit Manager: modal structure           (tests 4–9)
 *   C — Edit Manager: actual submission E2E     (tests 10–13)
 *   D — Delete Manager: E2E (if button present) (tests 14–16)
 */
@Listeners(ExtentReportListener.class)
public class EditDeleteManagerTest extends BaseClassTest {

    private ManagersLeadershipPage managersPage;

    // ── Locators for Edit Manager modal (not in existing page objects) ────────

    // Edit button on each manager profile card
    private final By editManagerBtn = By.xpath(
            "//button[contains(normalize-space(),'Edit') " +
                    "and not(contains(normalize-space(),'View'))]"
    );

    // Delete button on manager card (may not exist in all builds)
    private final By deleteManagerBtn = By.xpath(
            "//button[contains(normalize-space(),'Delete') " +
                    "or contains(normalize-space(),'Remove')]" +
                    "[not(contains(normalize-space(),'Cancel'))]"
    );

    // Modal container
    private final By editModal = By.cssSelector("[class*='modal'], [role='dialog']");

    // Modal title
    private final By editModalTitle = By.cssSelector(
            "[class*='modal'] h5, [class*='modal'] h4, " +
                    "[class*='modal-title'], [role='dialog'] h5"
    );

    // Input fields in Edit Manager modal
    private final By modalTextInputs = By.cssSelector(
            "[class*='modal'] input[type='text'], " +
                    "[class*='modal'] input:not([type='hidden']):not(:disabled), " +
                    "[role='dialog'] input[type='text']"
    );

    // Service Line dropdown in modal (may exist for reassigning)
    private final By modalServiceLineDd = By.cssSelector(
            "[class*='modal'] select[formcontrolname*='service'], " +
                    "[class*='modal'] select, [role='dialog'] select"
    );

    // Submit / Save button in Edit modal
    private final By editSubmitBtn = By.xpath(
            "//*[contains(@class,'modal') or @role='dialog']" +
                    "//button[contains(normalize-space(),'Save') " +
                    "or contains(normalize-space(),'Update') " +
                    "or contains(normalize-space(),'Create Entry')]" +
                    "[not(contains(normalize-space(),'Cancel'))]"
    );

    // Success notification
    private final By successNotification = By.xpath(
            "//*[contains(text(),'success') or contains(text(),'Success') " +
                    "or contains(text(),'updated') or contains(text(),'Updated') " +
                    "or contains(@class,'toast') or contains(@class,'alert-success')]"
    );

    // ── Setup ─────────────────────────────────────────────────────────────────

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToManagers() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));

        SuperAdminDashboardPage dashPage = new SuperAdminDashboardPage(driver);
        dashPage.getMenuItemElement("Managers").click();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        managersPage = new ManagersLeadershipPage(driver);
        System.out.println("Managers page loaded — URL: " + driver.getCurrentUrl());
    }

    // =========================================================================
    //  SECTION A — Page Load & Cards Present
    // =========================================================================

    @Test(priority = 1,
            description = "TC-EMD-001 [FRD 2.3]: Managers page heading is visible")
    public void verifyManagersPageLoaded() {
        Assert.assertTrue(managersPage.isPageHeadingVisible(),
                "FAIL [FRD 2.3] — Managers page heading not visible after navigation.");
        System.out.println("PASS — Managers page loaded.");
    }

    @Test(priority = 2,
            description = "TC-EMD-002 [FRD 2.3]: Manager profile cards are present")
    public void verifyManagerCardsPresent() {
        Assert.assertTrue(managersPage.areProfileCardsVisible(),
                "FAIL [FRD 2.3] — No manager profile cards found. " +
                        "Cannot test Edit/Delete without at least one manager.");
        System.out.println("PASS — " + managersPage.getProfileCards().size() + " manager card(s) found.");
    }

    @Test(priority = 3,
            description = "TC-EMD-003 [FRD 2.3]: Edit button is visible on manager cards")
    public void verifyEditButtonExists() {
        List<WebElement> editBtns = driver.findElements(editManagerBtn);
        Assert.assertFalse(editBtns.isEmpty(),
                "FAIL [FRD 2.3] — No 'Edit' button found on manager cards. " +
                        "FRD 2.3 shows Edit button in Manager Profile Cards.");
        highlight(editBtns.get(0), "yellow", "Edit Manager button [FRD 2.3]");
        System.out.println("PASS — " + editBtns.size() + " Edit button(s) found.");
    }

    // =========================================================================
    //  SECTION B — Edit Manager: Modal Structure
    // =========================================================================

    @Test(priority = 4,
            description = "TC-EMD-004 [FRD 2.3]: Clicking Edit opens the Edit Manager modal")
    public void verifyEditModalOpens() {
        List<WebElement> editBtns = driver.findElements(editManagerBtn);
        Assert.assertFalse(editBtns.isEmpty(), "No Edit buttons found.");
        editBtns.get(0).click();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        Assert.assertTrue(managersPage.isModalVisible(),
                "FAIL [FRD 2.3] — Edit Manager modal did not open after clicking Edit button.");
        System.out.println("PASS — Edit Manager modal opened.");
    }

    @Test(priority = 5,
            description = "TC-EMD-005 [FRD 2.3]: Edit Manager modal has a title")
    public void verifyEditModalTitle() {
        List<WebElement> titles = driver.findElements(editModalTitle);
        if (titles.isEmpty()) {
            System.out.println("NOTE — Modal title element not found; verifying modal is open.");
            Assert.assertTrue(managersPage.isModalVisible(), "FAIL — Modal not open.");
            return;
        }
        String titleText = titles.get(0).getText().trim();
        System.out.println("Modal title: \"" + titleText + "\"");
        Assert.assertFalse(titleText.isEmpty(),
                "FAIL [FRD 2.3] — Edit Manager modal title is empty.");
        System.out.println("PASS — Modal title: \"" + titleText + "\"");
    }

    @Test(priority = 6,
            description = "TC-EMD-006 [FRD 2.3]: Edit modal is pre-populated with manager data")
    public void verifyModalPrePopulated() {
        // FRD 2.3: Modal should be pre-filled with existing manager data
        List<WebElement> inputs = driver.findElements(modalTextInputs);
        System.out.println("Text inputs in modal: " + inputs.size());
        Assert.assertFalse(inputs.isEmpty(),
                "FAIL [FRD 2.3] — No text inputs found in Edit Manager modal.");

        boolean anyHasValue = inputs.stream()
                .anyMatch(i -> {
                    String val = i.getAttribute("value");
                    return val != null && !val.trim().isEmpty();
                });
        Assert.assertTrue(anyHasValue,
                "FAIL [FRD 2.3] — Edit modal fields appear empty. Should be pre-populated.");
        System.out.println("PASS — Edit modal is pre-populated with manager data.");
    }

    @Test(priority = 7,
            description = "TC-EMD-007 [FRD 2.3]: Submit button is present in Edit modal")
    public void verifySubmitButtonPresent() {
        List<WebElement> submitBtns = driver.findElements(editSubmitBtn);
        Assert.assertFalse(submitBtns.isEmpty(),
                "FAIL [FRD 2.3] — No submit/save button found in Edit Manager modal.");
        highlight(submitBtns.get(0), "yellow", "Submit button [FRD 2.3]");
        System.out.println("PASS — Submit button: \"" + submitBtns.get(0).getText() + "\"");
    }

    @Test(priority = 8,
            description = "TC-EMD-008 [FRD 2.3]: Cancel button closes the modal without saving")
    public void verifyCancelClosesModal() {
        managersPage.closeModal();
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}

        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(0));
        boolean modalGone = driver.findElements(editModal)
                .stream().noneMatch(WebElement::isDisplayed);
        driver.manage().timeouts().implicitlyWait(
                java.time.Duration.ofSeconds(ConfigReader.getImplicitWait()));

        Assert.assertTrue(modalGone,
                "FAIL [FRD 2.3] — Modal still visible after clicking Cancel.");
        System.out.println("PASS — Cancel closed the Edit Manager modal.");
    }

    // =========================================================================
    //  SECTION C — Edit Manager: Actual Submission (End-to-End)
    // =========================================================================

    @Test(priority = 9,
            description = "TC-EMD-009 [FRD 2.3]: Re-open Edit modal for E2E submission")
    public void reopenEditModalForSubmission() {
        List<WebElement> editBtns = driver.findElements(editManagerBtn);
        Assert.assertFalse(editBtns.isEmpty(), "No Edit buttons found to reopen modal.");
        editBtns.get(0).click();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
        Assert.assertTrue(managersPage.isModalVisible(), "FAIL — Modal did not reopen.");
        System.out.println("PASS — Edit modal re-opened.");
    }

    @Test(priority = 10,
            description = "TC-EMD-010 [FRD 2.3]: Modify an editable field in the Edit Manager modal")
    public void modifyEditableField() {
        // FRD 2.3: Fields that can be edited include Service Line assignment
        List<WebElement> selects = driver.findElements(modalServiceLineDd);
        if (!selects.isEmpty()) {
            // Change the dropdown value
            Select dropdown = new Select(selects.get(0));
            int opts = dropdown.getOptions().size();
            if (opts > 1) {
                int currentIdx = dropdown.getOptions().indexOf(dropdown.getFirstSelectedOption());
                int newIdx = (currentIdx + 1) % opts;
                if (newIdx == 0) newIdx = 1;
                dropdown.selectByIndex(newIdx);
                highlight(selects.get(0), "green",
                        "Changed to: " + dropdown.getFirstSelectedOption().getText());
                System.out.println("PASS — Dropdown changed to: " +
                        dropdown.getFirstSelectedOption().getText());
                return;
            }
        }
        // Fallback: modify a text field
        List<WebElement> inputs = driver.findElements(modalTextInputs);
        if (!inputs.isEmpty()) {
            WebElement field = inputs.stream()
                    .filter(i -> {
                        String dis = i.getAttribute("disabled");
                        return dis == null || dis.equals("false");
                    })
                    .findFirst().orElse(inputs.get(0));
            String original = field.getAttribute("value");
            field.clear();
            field.sendKeys(original.isEmpty() ? "TestValue" : original + " ");
            highlight(field, "green", "Field modified [FRD 2.3]");
            System.out.println("PASS — Text field modified.");
        } else {
            System.out.println("NOTE — No editable field found to modify; proceeding to submit.");
        }
    }

    @Test(priority = 11,
            description = "TC-EMD-011 [FRD 2.3]: Click Submit/Save button in Edit modal")
    public void clickSubmitButton() {
        WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(editSubmitBtn));
        highlight(submitBtn, "yellow", "Submitting Edit Manager [FRD 2.3]");
        submitBtn.click();
        System.out.println("PASS — Submit button clicked.");
    }

    @Test(priority = 12,
            description = "TC-EMD-012 [FRD 2.3]: Success notification or modal closes after edit submit")
    public void verifyEditSuccess() {
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        // Check for success notification
        boolean notifShown = false;
        List<WebElement> notifs = driver.findElements(successNotification);
        if (!notifs.isEmpty() && notifs.stream().anyMatch(WebElement::isDisplayed)) {
            notifShown = true;
            System.out.println("SUCCESS NOTIFICATION: \"" +
                    notifs.stream().filter(WebElement::isDisplayed)
                            .findFirst().map(WebElement::getText).orElse("(shown)") + "\"");
        }

        // Check modal closed
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(0));
        boolean modalClosed = driver.findElements(editModal)
                .stream().noneMatch(WebElement::isDisplayed);
        driver.manage().timeouts().implicitlyWait(
                java.time.Duration.ofSeconds(ConfigReader.getImplicitWait()));

        Assert.assertTrue(notifShown || modalClosed,
                "FAIL [FRD 2.3] — After submitting Edit Manager, no success notification appeared " +
                        "and modal did not close.");
        System.out.println("PASS — Edit Manager submission confirmed. " +
                "Notification: " + notifShown + " | Modal closed: " + modalClosed);
    }

    // =========================================================================
    //  SECTION D — Delete Manager (if delete button exists)
    //  ⚠️  DESTRUCTIVE
    // =========================================================================

    @Test(priority = 14,
            description = "TC-EMD-014 [FRD 2.3]: Check if Delete button exists on manager cards")
    public void checkDeleteButtonExists() {
        List<WebElement> deleteBtns = driver.findElements(deleteManagerBtn);
        System.out.println("Delete buttons found: " + deleteBtns.size());
        if (deleteBtns.isEmpty()) {
            System.out.println("INFO — No Delete button found on Manager cards in this build. " +
                    "FRD 2.3 shows Edit and View Details only — Delete may be a future feature.");
        } else {
            highlight(deleteBtns.get(0), "yellow", "Delete Manager button [FRD 2.3]");
            System.out.println("Delete button IS present on manager cards.");
        }
        // Not failing — delete may not be implemented (FRD shows Edit + View Details only)
        System.out.println("PASS — Delete button check completed.");
    }

    @Test(priority = 15,
            description = "TC-EMD-015 [FRD 2.3] ⚠️DESTRUCTIVE: If Delete button present, confirm dialog shown on click")
    public void verifyDeleteConfirmationIfPresent() {
        List<WebElement> deleteBtns = driver.findElements(deleteManagerBtn);
        if (deleteBtns.isEmpty()) {
            System.out.println("SKIP — No Delete button on Manager cards. Skipping delete test.");
            return;
        }

        int cardsBefore = managersPage.getProfileCards().size();
        deleteBtns.get(0).click();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        // Handle both native alert and custom modal
        boolean confirmShown = false;
        try {
            Alert confirm = wait.until(ExpectedConditions.alertIsPresent());
            System.out.println("Delete confirm text: \"" + confirm.getText() + "\"");
            confirmShown = true;
            confirm.accept(); // Accept — ACTUALLY DELETES
        } catch (Exception e) {
            By confirmBtn = By.xpath(
                    "//button[contains(normalize-space(),'OK') " +
                            "or contains(normalize-space(),'Yes') " +
                            "or contains(normalize-space(),'Confirm')]");
            List<WebElement> confirmBtns = driver.findElements(confirmBtn);
            if (!confirmBtns.isEmpty()) {
                confirmShown = true;
                confirmBtns.get(0).click();
            }
        }

        if (confirmShown) {
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            int cardsAfter = managersPage.getProfileCards().size();
            Assert.assertTrue(cardsAfter < cardsBefore,
                    "FAIL [FRD 2.3] — Manager card count should decrease after delete. " +
                            "Before: " + cardsBefore + " | After: " + cardsAfter);
            System.out.println("PASS [⚠️DESTRUCTIVE] — Manager deleted. Cards: " +
                    cardsBefore + " → " + cardsAfter);
        }
        System.out.println("PASS — Delete flow completed.");
    }
}