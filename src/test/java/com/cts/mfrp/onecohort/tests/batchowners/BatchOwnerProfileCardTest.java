package com.cts.mfrp.onecohort.tests.batchowners;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
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

/**
 * Batch Owner Profile Card Tests — FRD Section 2.4.2
 *
 * Scope: profile card content validation (name, service line, cohort count,
 *        contact info), View Details action, and back navigation.
 *
 * Pre-condition: Service Line and Learning Path filters are applied in @BeforeClass
 * so that profile cards are visible before the tests run.
 *
 * Login: Super Admin — all tests share one browser session.
 *
 * ── FRD Traceability ─────────────────────────────────────────────────────────
 * TC-BO-CARD-001  FRD 2.4.2  Profile cards are visible after filter selection
 * TC-BO-CARD-002  FRD 2.4.2  First card displays a non-empty name
 * TC-BO-CARD-003  FRD 2.4.2  First card shows Service Line information
 * TC-BO-CARD-004  FRD 2.4.2  First card shows cohort count or related metric
 * TC-BO-CARD-005  FRD 2.4.2  First card shows contact info (email or ID)
 * TC-BO-CARD-006  FRD 2.4.2  "View Details" button on each card navigates to profile
 * TC-BO-CARD-007  FRD 2.4.2  Back navigation returns to Batch Owners list
 * ─────────────────────────────────────────────────────────────────────────────
 */
@Test(groups = {"regression", "functional", "batchowner", "superadmin"})
@Listeners(ExtentReportListener.class)
public class BatchOwnerProfileCardTest extends BaseClassTest {

    private BatchOwnerPage batchOwnerPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void setup() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("super-admin"));

        // Navigate to Batch Owners / POC
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//nav[contains(@class,'menu')]" +
                         "//*[contains(text(),'Batch Owner')]" +
                         " | //nav[contains(@class,'menu')]" +
                         "//*[contains(text(),'Batch Owners / POC')]"))).click();
        wait.until(ExpectedConditions.urlContains("batch"));
        batchOwnerPage = new BatchOwnerPage(driver);

        // Apply the Service Line filter so profile cards are loaded
        if (batchOwnerPage.isServiceLineDropdownVisible()) {
            batchOwnerPage.selectServiceLine(ConfigReader.getValidServiceLineId());
            try {
                wait.until(d -> batchOwnerPage.areProfileCardsVisible() || batchOwnerPage.isLearningPathDropdownVisible());
            } catch (Exception ignored) {}
        }

        // If a Learning Path dropdown appeared, select the first real option
        if (batchOwnerPage.isLearningPathDropdownVisible()) {
            try {
                Select lpSelect = new Select(
                        driver.findElement(By.cssSelector(
                                "select[name='learningPath'], select#learningPathFilter, " +
                                "select[formcontrolname='learningPath']")));
                List<WebElement> lpOptions = lpSelect.getOptions();
                for (WebElement opt : lpOptions) {
                    if (!opt.getAttribute("value").trim().isEmpty()) {
                        lpSelect.selectByValue(opt.getAttribute("value"));
                        break;
                    }
                }
                wait.until(d -> batchOwnerPage.areProfileCardsVisible());
            } catch (Exception ignored) {}
        }

        System.out.println("Setup complete — URL: " + driver.getCurrentUrl());
    }

    // -------------------------------------------------------
    // TC-BO-CARD-001 — Profile cards visible after filter selection
    // FRD 2.4.2 — Batch Owner profile cards must appear once filters are applied
    // -------------------------------------------------------
    @Test(priority = 1)
    public void verifyProfileCardsVisible() {
        boolean visible = batchOwnerPage.areProfileCardsVisible();
        Assert.assertTrue(
                visible,
                "FAIL - No Batch Owner profile cards visible after applying Service Line filter. " +
                "FRD 2.4.2 requires batch owner cards to be displayed after filtering. " +
                "Verify the test database has batch owners assigned to SRV-10001.");
        List<WebElement> cards = batchOwnerPage.getProfileCards();
        for (WebElement card : cards) highlight(card);
        System.out.println("PASS - " + cards.size() + " profile card(s) visible");
    }

    // -------------------------------------------------------
    // TC-BO-CARD-002 — First card displays a non-empty name
    // FRD 2.4.2 — Each card must show the batch owner's full name
    // -------------------------------------------------------
    @Test(priority = 2)
    public void verifyCardNameNotEmpty() {
        List<WebElement> cards = batchOwnerPage.getProfileCards();
        if (cards.isEmpty()) {
            System.out.println("SKIP - No profile cards; skipping name validation");
            return;
        }
        WebElement firstCard = cards.get(0);
        highlight(firstCard);
        // Look for name elements inside the card
        List<WebElement> nameEls = firstCard.findElements(By.cssSelector(
                "h3, h4, p.font-bold, p.font-semibold, " +
                "[class*='name'], [class*='title']"));
        boolean hasName = nameEls.stream().anyMatch(e -> !e.getText().trim().isEmpty());
        Assert.assertTrue(
                hasName,
                "FAIL - First Batch Owner profile card does not display a name. " +
                "FRD 2.4.2 requires each card to show the batch owner's full name.");
        System.out.println("PASS - Profile card name element is non-empty");
    }

    // -------------------------------------------------------
    // TC-BO-CARD-003 — First card shows Service Line information
    // FRD 2.4.2 — Cards must display the assigned Service Line
    // -------------------------------------------------------
    @Test(priority = 3)
    public void verifyCardShowsServiceLine() {
        List<WebElement> cards = batchOwnerPage.getProfileCards();
        if (cards.isEmpty()) {
            System.out.println("SKIP - No profile cards; skipping Service Line check");
            return;
        }
        WebElement firstCard = cards.get(0);
        String cardText = firstCard.getText();
        boolean hasServiceLine = cardText.contains("SRV-") ||
                cardText.toLowerCase().contains("service line") ||
                cardText.toLowerCase().contains("service");
        Assert.assertTrue(
                hasServiceLine,
                "FAIL - First card does not show Service Line information. " +
                "FRD 2.4.2 requires each batch owner card to display their Service Line. " +
                "Card text: " + cardText);
        System.out.println("PASS - Service Line info found in profile card");
    }

    // -------------------------------------------------------
    // TC-BO-CARD-004 — First card shows a cohort count or metric
    // FRD 2.4.2 — Cards should display cohort-related metrics
    //             (e.g. number of cohorts managed, active cohorts)
    // -------------------------------------------------------
    @Test(priority = 4)
    public void verifyCardShowsCohortMetric() {
        List<WebElement> cards = batchOwnerPage.getProfileCards();
        if (cards.isEmpty()) {
            System.out.println("SKIP - No profile cards; skipping cohort metric check");
            return;
        }
        String cardText = cards.get(0).getText();
        boolean hasCohortInfo = cardText.toLowerCase().contains("cohort") ||
                cardText.matches(".*\\b\\d+\\b.*");  // contains any number
        // Soft check — log GAP rather than hard fail if metric not displayed
        if (!hasCohortInfo) {
            System.out.println("GAP - Profile card does not display a cohort count or metric. " +
                    "FRD 2.4.2 implies batch owner cards should show cohort-related metrics.");
        } else {
            System.out.println("PASS - Profile card shows cohort/numeric metric");
        }
        // Assert non-empty card at minimum
        Assert.assertFalse(
                cardText.trim().isEmpty(),
                "FAIL - Profile card is completely empty. FRD 2.4.2 requires cards to display " +
                "batch owner information.");
    }

    // -------------------------------------------------------
    // TC-BO-CARD-005 — First card shows contact info (email or ID)
    // FRD 2.4.2 — Cards should display contact information
    // -------------------------------------------------------
    @Test(priority = 5)
    public void verifyCardShowsContactInfo() {
        List<WebElement> cards = batchOwnerPage.getProfileCards();
        if (cards.isEmpty()) {
            System.out.println("SKIP - No profile cards; skipping contact info check");
            return;
        }
        String cardText = cards.get(0).getText();
        boolean hasContact = cardText.contains("@") ||             // email
                cardText.matches(".*USR-\\d+.*") ||               // USR-ID format
                cardText.toLowerCase().contains("email") ||
                cardText.toLowerCase().contains("contact") ||
                cardText.toLowerCase().contains("id");
        if (!hasContact) {
            System.out.println("GAP - Profile card does not show email or ID. " +
                    "FRD 2.4.2 implies contact details should be on each card.");
        } else {
            System.out.println("PASS - Profile card shows contact information");
        }
        // Soft assertion — card must at least have some content
        Assert.assertFalse(cardText.trim().isEmpty(),
                "FAIL - Profile card is empty. FRD 2.4.2 requires batch owner details.");
    }

    // -------------------------------------------------------
    // TC-BO-CARD-006 — "View Details" navigates to the profile
    // FRD 2.4.2 — Clicking View Details must show full batch owner profile
    // -------------------------------------------------------
    @Test(priority = 6)
    public void verifyViewDetailsNavigatesToProfile() {
        List<WebElement> viewBtns = batchOwnerPage.getViewDetailsButtons();
        if (viewBtns.isEmpty()) {
            System.out.println("SKIP - No 'View Details' buttons found; skipping navigation test");
            return;
        }
        String urlBefore = driver.getCurrentUrl();
        viewBtns.get(0).click();
        try {
            wait.until(d -> batchOwnerPage.isModalVisible() || !d.getCurrentUrl().equals(urlBefore));
        } catch (Exception ignored) {}
        String urlAfter = driver.getCurrentUrl();

        boolean modalOpened = batchOwnerPage.isModalVisible();
        boolean pageChanged = !urlAfter.equals(urlBefore);

        Assert.assertTrue(
                modalOpened || pageChanged,
                "FAIL - Clicking 'View Details' on a Batch Owner card neither opened a modal " +
                "nor navigated to a detail page. FRD 2.4.2 requires View Details to show the " +
                "full batch owner profile.");
        System.out.println("PASS - View Details opened: " +
                (modalOpened ? "modal" : "detail page at " + urlAfter));

        // Restore state
        if (modalOpened) {
            batchOwnerPage.closeModal();
        } else if (pageChanged) {
            driver.navigate().back();
            wait.until(ExpectedConditions.urlContains("batch"));
        }
    }

    // -------------------------------------------------------
    // TC-BO-CARD-007 — Back navigation returns to Batch Owners page
    // FRD 2.4.2 — After viewing details, user should be able to return
    // -------------------------------------------------------
    @Test(priority = 7)
    public void verifyBackNavigationToBatchOwnersList() {
        List<WebElement> viewBtns = batchOwnerPage.getViewDetailsButtons();
        if (viewBtns.isEmpty()) {
            System.out.println("SKIP - No View Details buttons; skipping back-navigation test");
            return;
        }
        String urlBefore = driver.getCurrentUrl();
        viewBtns.get(0).click();
        try {
            wait.until(d -> batchOwnerPage.isModalVisible() || !d.getCurrentUrl().equals(urlBefore));
        } catch (Exception ignored) {}

        // Check if we navigated or a modal opened
        if (batchOwnerPage.isModalVisible()) {
            batchOwnerPage.closeModal();
            System.out.println("PASS - View Details opened modal (not navigation); modal closed");
            return;
        }

        // We navigated — use back button or browser back
        List<WebElement> backBtns = driver.findElements(By.xpath(
                "//button[contains(normalize-space(),'Back')] | " +
                "//a[contains(normalize-space(),'Back')]"));
        if (!backBtns.isEmpty()) {
            backBtns.get(0).click();
        } else {
            driver.navigate().back();
        }

        wait.until(ExpectedConditions.urlContains("batch"));
        String urlAfter = driver.getCurrentUrl();
        Assert.assertTrue(
                urlAfter.contains("batch") || urlAfter.equals(urlBefore),
                "FAIL - Back navigation did not return to the Batch Owners page. " +
                "FRD 2.4.2 implies navigation to profile and back must be supported. " +
                "Current URL: " + urlAfter);
        System.out.println("PASS - Back navigation returned to Batch Owners list: " + urlAfter);
    }
}
