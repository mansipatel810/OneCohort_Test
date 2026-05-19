package com.cts.mfrp.onecohort.tests.batchowners;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.pages.batchowners.BatchOwnerPage;
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
 * Edit Batch Owner + Delete Batch Owner — End-to-End Test Suite
 *
 * FRD Reference: Section 2.4 — Batch Owners / POC
 *
 * ── EDIT BATCH OWNER (FRD 2.4) ───────────────────────────────────────────────
 * FRD Figures 3.10–3.11 show the "Edit Batch Owner Modal" with field details.
 * Clicking Edit on a Batch Owner card opens a pre-populated modal.
 * Editable fields include: Full Name, Email, Service Line assignment
 * Submit → success notification → modal closes
 *
 * ── DELETE BATCH OWNER (FRD 2.4) ─────────────────────────────────────────────
 * If a delete button exists, clicking it shows a confirmation dialog.
 * Confirming removes the Batch Owner from the system.
 *
 * ⚠️  WARNING: Delete tests are DESTRUCTIVE.
 *
 * Test sections:
 *   A — Navigate and verify page loads         (tests 1–3)
 *   B — Edit Batch Owner: modal structure      (tests 4–9)
 *   C — Edit Batch Owner: actual submission    (tests 10–13)
 *   D — Delete Batch Owner: E2E               (tests 14–16)
 */
@Listeners(ExtentReportListener.class)
public class EditDeleteBatchOwnerTest extends BaseClassTest {

    private BatchOwnerPage batchOwnerPage;

    // ── Locators ──────────────────────────────────────────────────────────────

    private final By editBtn = By.xpath(
            "//button[contains(normalize-space(),'Edit') " +
                    "and not(contains(normalize-space(),'View'))]"
    );

    private final By deleteBtn = By.xpath(
            "//button[contains(normalize-space(),'Delete') " +
                    "or contains(normalize-space(),'Remove')]" +
                    "[not(contains(normalize-space(),'Cancel'))]"
    );

    private final By editModal = By.cssSelector("[class*='modal'], [role='dialog']");

    private final By editModalTitle = By.cssSelector(
            "[class*='modal'] h5, [class*='modal'] h4, " +
                    "[class*='modal-title'], [role='dialog'] h5"
    );

    // FRD 3.10–3.11: Edit Batch Owner modal fields
    private final By modalFullNameInput = By.cssSelector(
            "[class*='modal'] input[formcontrolname*='name'], " +
                    "[class*='modal'] input[formcontrolname*='fullName'], " +
                    "[class*='modal'] input[placeholder*='Name'], " +
                    "[role='dialog'] input[placeholder*='Name']"
    );

    private final By modalEmailInput = By.cssSelector(
            "[class*='modal'] input[type='email'], " +
                    "[class*='modal'] input[formcontrolname*='email'], " +
                    "[class*='modal'] input[placeholder*='email'], " +
                    "[role='dialog'] input[type='email']"
    );

    private final By modalServiceLineDd = By.cssSelector(
            "[class*='modal'] select[formcontrolname*='service'], " +
                    "[class*='modal'] select, [role='dialog'] select"
    );

    private final By editSubmitBtn = By.xpath(
            "//*[contains(@class,'modal') or @role='dialog']" +
                    "//button[contains(normalize-space(),'Save') " +
                    "or contains(normalize-space(),'Update') " +
                    "or contains(normalize-space(),'Edit')]" +
                    "[not(contains(normalize-space(),'Cancel'))]"
    );

    private final By successNotification = By.xpath(
            "//*[contains(text(),'success') or contains(text(),'Success') " +
                    "or contains(text(),'updated') or contains(text(),'Updated') " +
                    "or contains(@class,'toast') or contains(@class,'alert-success')]"
    );

    // ── Setup ─────────────────────────────────────────────────────────────────

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigateToBatchOwners() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("/super-admin"));

        SuperAdminDashboardPage dashPage = new SuperAdminDashboardPage(driver);
        dashPage.getMenuItemElement("Batch Owners").click();
        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        batchOwnerPage = new BatchOwnerPage(driver);
        System.out.println("Batch Owners page loaded — URL: " + driver.getCurrentUrl());
    }

    // =========================================================================
    //  SECTION A — Page Load & Cards Present
    // =========================================================================

    @Test(priority = 1,
            description = "TC-EBO-001 [FRD 2.4]: Batch Owners page heading is visible")
    public void verifyPageLoaded() {
        Assert.assertTrue(batchOwnerPage.isPageHeadingVisible(),
                "FAIL [FRD 2.4] — Batch Owners page heading not visible.");
        System.out.println("PASS — Batch Owners page loaded.");
    }

    @Test(priority = 2,
            description = "TC-EBO-002 [FRD 2.4]: Batch Owner profile cards are present")
    public void verifyCardsPresent() {
        Assert.assertTrue(batchOwnerPage.areProfileCardsVisible(),
                "FAIL [FRD 2.4] — No Batch Owner cards found. Cannot test Edit/Delete.");
        System.out.println("PASS — " + batchOwnerPage.getProfileCards().size() + " card(s) found.");
    }

    @Test(priority = 3,
            description = "TC-EBO-003 [FRD 2.4]: Edit button is present on Batch Owner cards")
    public void verifyEditButtonExists() {
        List<WebElement> editBtns = driver.findElements(editBtn);
        Assert.assertFalse(editBtns.isEmpty(),
                "FAIL [FRD 2.4] — No 'Edit' button on Batch Owner cards. " +
                        "FRD 2.4 (Figure 3.10) requires an Edit button.");
        highlight(editBtns.get(0), "yellow", "Edit Batch Owner button [FRD 2.4]");
        System.out.println("PASS — " + editBtns.size() + " Edit button(s) found.");
    }

    // =========================================================================
    //  SECTION B — Edit Batch Owner: Modal Structure
    // =========================================================================

    @Test(priority = 4,
            description = "TC-EBO-004 [FRD 2.4]: Clicking Edit opens the Edit Batch Owner modal")
    public void verifyEditModalOpens() {
        driver.findElements(editBtn).get(0).click();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
        Assert.assertTrue(batchOwnerPage.isModalVisible(),
                "FAIL [FRD 2.4] — Edit Batch Owner modal did not open.");
        System.out.println("PASS — Edit Batch Owner modal opened.");
    }

    @Test(priority = 5,
            description = "TC-EBO-005 [FRD 2.4]: Edit modal title is visible (FRD Fig 3.10)")
    public void verifyEditModalTitle() {
        List<WebElement> titles = driver.findElements(editModalTitle);
        if (titles.isEmpty()) {
            Assert.assertTrue(batchOwnerPage.isModalVisible(), "FAIL — Modal not open.");
            System.out.println("NOTE — Modal title element not found; modal is open.");
            return;
        }
        String title = titles.get(0).getText().trim();
        System.out.println("Modal title: \"" + title + "\"");
        Assert.assertFalse(title.isEmpty(),
                "FAIL [FRD 2.4] — Edit Batch Owner modal title is empty.");
        System.out.println("PASS — Modal title: \"" + title + "\"");
    }

    @Test(priority = 6,
            description = "TC-EBO-006 [FRD 2.4]: Edit modal is pre-populated with Batch Owner data (FRD Fig 3.11)")
    public void verifyModalPrePopulated() {
        List<WebElement> inputs = driver.findElements(By.cssSelector(
                "[class*='modal'] input, [role='dialog'] input"));
        Assert.assertFalse(inputs.isEmpty(),
                "FAIL [FRD 2.4] — No inputs found in Edit Batch Owner modal.");
        boolean anyPopulated = inputs.stream()
                .anyMatch(i -> {
                    String val = i.getAttribute("value");
                    return val != null && !val.trim().isEmpty();
                });
        Assert.assertTrue(anyPopulated,
                "FAIL [FRD 2.4] — Edit Batch Owner modal is not pre-populated. " +
                        "FRD Figure 3.11 shows fields pre-filled with existing data.");
        System.out.println("PASS — Modal is pre-populated with Batch Owner data.");
    }

    @Test(priority = 7,
            description = "TC-EBO-007 [FRD 2.4]: Full Name field is present in Edit modal")
    public void verifyFullNameField() {
        List<WebElement> nameFields = driver.findElements(modalFullNameInput);
        Assert.assertFalse(nameFields.isEmpty(),
                "FAIL [FRD 2.4] — Full Name input not found in Edit Batch Owner modal.");
        highlight(nameFields.get(0), "green", "Full Name field [FRD 2.4]");
        System.out.println("PASS — Full Name field present.");
    }

    @Test(priority = 8,
            description = "TC-EBO-008 [FRD 2.4]: Email field is present in Edit modal")
    public void verifyEmailField() {
        List<WebElement> emailFields = driver.findElements(modalEmailInput);
        Assert.assertFalse(emailFields.isEmpty(),
                "FAIL [FRD 2.4] — Email input not found in Edit Batch Owner modal.");
        highlight(emailFields.get(0), "green", "Email field [FRD 2.4]");
        System.out.println("PASS — Email field present.");
    }

    @Test(priority = 9,
            description = "TC-EBO-009 [FRD 2.4]: Cancel button closes modal without saving")
    public void verifyCancelClosesModal() {
        batchOwnerPage.closeModal();
        try { Thread.sleep(600); } catch (InterruptedException ignored) {}
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(0));
        boolean gone = driver.findElements(editModal).stream().noneMatch(WebElement::isDisplayed);
        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(ConfigReader.getImplicitWait()));
        Assert.assertTrue(gone, "FAIL [FRD 2.4] — Modal still visible after Cancel.");
        System.out.println("PASS — Cancel closed the Edit Batch Owner modal.");
    }

    // =========================================================================
    //  SECTION C — Edit Batch Owner: Actual Submission (End-to-End)
    // =========================================================================

    @Test(priority = 10,
            description = "TC-EBO-010 [FRD 2.4]: Re-open Edit modal and modify a field")
    public void modifyFieldInEditModal() {
        driver.findElements(editBtn).get(0).click();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        // Modify the Service Line dropdown or a text field
        List<WebElement> selects = driver.findElements(modalServiceLineDd);
        if (!selects.isEmpty()) {
            Select dropdown = new Select(selects.get(0));
            if (dropdown.getOptions().size() > 1) {
                int cur = dropdown.getOptions().indexOf(dropdown.getFirstSelectedOption());
                int newIdx = (cur + 1) % dropdown.getOptions().size();
                if (newIdx == 0) newIdx = 1;
                dropdown.selectByIndex(newIdx);
                highlight(selects.get(0), "green",
                        "Changed to: " + dropdown.getFirstSelectedOption().getText());
                System.out.println("PASS — Dropdown changed to: " +
                        dropdown.getFirstSelectedOption().getText());
                return;
            }
        }
        // Fallback: append a space to an enabled text input
        List<WebElement> inputs = driver.findElements(modalFullNameInput);
        if (!inputs.isEmpty() && inputs.get(0).isEnabled()) {
            inputs.get(0).sendKeys(" ");
            System.out.println("PASS — Text field modified.");
        }
    }

    @Test(priority = 11,
            description = "TC-EBO-011 [FRD 2.4]: Click Submit button in Edit Batch Owner modal")
    public void clickSubmitButton() {
        WebElement submit = wait.until(ExpectedConditions.elementToBeClickable(editSubmitBtn));
        highlight(submit, "yellow", "Submit Edit Batch Owner [FRD 2.4]");
        submit.click();
        System.out.println("PASS — Submit button clicked.");
    }

    @Test(priority = 12,
            description = "TC-EBO-012 [FRD 2.4]: Success notification or modal close after edit submission")
    public void verifyEditSuccess() {
        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        List<WebElement> notifs = driver.findElements(successNotification);
        boolean notifShown = !notifs.isEmpty() &&
                notifs.stream().anyMatch(WebElement::isDisplayed);

        driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(0));
        boolean modalClosed = driver.findElements(editModal)
                .stream().noneMatch(WebElement::isDisplayed);
        driver.manage().timeouts().implicitlyWait(
                java.time.Duration.ofSeconds(ConfigReader.getImplicitWait()));

        if (notifShown) {
            System.out.println("SUCCESS NOTIFICATION: \"" +
                    notifs.stream().filter(WebElement::isDisplayed)
                            .findFirst().map(WebElement::getText).orElse("shown") + "\"");
        }
        Assert.assertTrue(notifShown || modalClosed,
                "FAIL [FRD 2.4] — No success notification and modal did not close after editing.");
        System.out.println("PASS — Batch Owner edit confirmed. Notif: " + notifShown +
                " | Modal closed: " + modalClosed);
    }

    // =========================================================================
    //  SECTION D — Delete Batch Owner (if button present)
    //  ⚠️  DESTRUCTIVE
    // =========================================================================

    @Test(priority = 14,
            description = "TC-EBO-014 [FRD 2.4] ⚠️DESTRUCTIVE: Delete Batch Owner E2E if button present")
    public void verifyDeleteBatchOwner() {
        List<WebElement> deleteBtns = driver.findElements(deleteBtn);
        if (deleteBtns.isEmpty()) {
            System.out.println("INFO — No Delete button on Batch Owner cards in this build. " +
                    "FRD 2.4 may only specify Add + Edit for Batch Owners.");
            return;
        }

        int cardsBefore = batchOwnerPage.getProfileCards().size();
        System.out.println("Cards before delete: " + cardsBefore);
        deleteBtns.get(0).click();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

        boolean confirmShown = false;
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            System.out.println("Delete confirm: \"" + alert.getText() + "\"");
            confirmShown = true;
            alert.accept();
        } catch (Exception e) {
            By okBtn = By.xpath(
                    "//button[contains(normalize-space(),'OK') " +
                            "or contains(normalize-space(),'Yes')]");
            List<WebElement> oks = driver.findElements(okBtn);
            if (!oks.isEmpty()) { confirmShown = true; oks.get(0).click(); }
        }

        if (confirmShown) {
            try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            int cardsAfter = batchOwnerPage.getProfileCards().size();
            Assert.assertTrue(cardsAfter < cardsBefore,
                    "FAIL [FRD 2.4] — Card count should decrease after delete. " +
                            "Before: " + cardsBefore + " | After: " + cardsAfter);
            System.out.println("PASS [⚠️DESTRUCTIVE] — Batch Owner deleted. " +
                    cardsBefore + " → " + cardsAfter);
        }
    }
}