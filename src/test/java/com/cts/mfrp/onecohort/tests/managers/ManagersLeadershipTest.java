package com.cts.mfrp.onecohort.tests.managers;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
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

/**
 * Managers & Leadership Page Tests — FRD Section 2.3
 *
 * Scope: page navigation, heading, profile card listing, role-based filtering,
 *        View Details modal, Create Manager button presence.
 *
 * Login: Super Admin — all tests share one browser session.
 *
 * ── FRD Traceability ─────────────────────────────────────────────────────────
 * TC-MGR-001  FRD 2.3    Page navigable via "Managers & Leadership" sidebar link
 * TC-MGR-002  FRD 2.3    "Managers & Leadership" heading is visible
 * TC-MGR-003  FRD 2.3.1  Profile cards are displayed for managers/leaders
 * TC-MGR-004  FRD 2.3.1  Each visible profile card shows a name
 * TC-MGR-005  FRD 2.3.1  "View Details" button present on profile cards
 * TC-MGR-006  FRD 2.3.1  View Details opens a detail modal or navigates to profile
 * TC-MGR-007  FRD 2.3.2  "Create Manager" button is visible and enabled
 * TC-MGR-008  FRD 2.3    Role filter tabs (Managers / Leaders) are present
 * ─────────────────────────────────────────────────────────────────────────────
 */
@Test(groups = {"smoke", "regression", "functional", "manager", "superadmin"})
@Listeners(ExtentReportListener.class)
public class ManagersLeadershipTest extends BaseClassTest {

    private ManagersLeadershipPage managersPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void setup() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(ExpectedConditions.urlContains("super-admin"));

        // Navigate to "Managers & Leadership" via sidebar
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//nav[contains(@class,'menu')]" +
                         "//*[contains(text(),'Managers') and contains(text(),'Leadership')]" +
                         " | //nav[contains(@class,'menu')]" +
                         "//*[contains(text(),'Managers & Leadership')]"))).click();

        wait.until(ExpectedConditions.urlContains("manager"));
        managersPage = new ManagersLeadershipPage(driver);
        System.out.println("Setup complete — URL: " + driver.getCurrentUrl());
    }

    // -------------------------------------------------------
    // TC-MGR-001 — URL contains manager/leadership segment
    // FRD 2.3 — Module must be reachable from the sidebar
    // -------------------------------------------------------
    @Test(priority = 1)
    public void verifyManagersPageUrl() {
        String url = driver.getCurrentUrl();
        Assert.assertTrue(
                url.contains("manager") || url.contains("leadership"),
                "FAIL - URL does not contain a managers/leadership path segment. " +
                "FRD 2.3 requires this module to be accessible via the sidebar. " +
                "Actual URL: " + url);
        System.out.println("PASS - Managers & Leadership URL: " + url);
    }

    // -------------------------------------------------------
    // TC-MGR-002 — "Managers & Leadership" heading is visible
    // FRD 2.3 — Module heading must identify the page
    // -------------------------------------------------------
    @Test(priority = 2)
    public void verifyPageHeadingVisible() {
        Assert.assertTrue(
                managersPage.isPageHeadingVisible(),
                "FAIL - 'Managers & Leadership' (or similar) heading is NOT visible. " +
                "FRD 2.3 requires a clear page heading identifying this module.");
        WebElement heading = driver.findElement(By.xpath(
                "//*[self::h1 or self::h2 or self::h3]" +
                "[contains(normalize-space(),'Managers') " +
                "or contains(normalize-space(),'Leadership')]"));
        highlight(heading);
        System.out.println("PASS - Page heading visible: " + heading.getText().trim());
    }

    // -------------------------------------------------------
    // TC-MGR-003 — Profile cards are displayed
    // FRD 2.3.1 — Managers and Leaders appear as profile cards
    // -------------------------------------------------------
    @Test(priority = 3)
    public void verifyProfileCardsPresent() {
        List<WebElement> cards = managersPage.getProfileCards();
        Assert.assertFalse(
                cards.isEmpty(),
                "FAIL - No Manager/Leader profile cards found. " +
                "FRD 2.3.1 requires each manager and leader to be displayed as a profile card. " +
                "Verify the database has at least one Manager or Leader record.");
        for (WebElement card : cards) {
            highlight(card);
        }
        System.out.println("PASS - " + cards.size() + " profile card(s) visible");
    }

    // -------------------------------------------------------
    // TC-MGR-004 — Each profile card shows a name
    // FRD 2.3.1 — Profile cards must display the person's name
    // -------------------------------------------------------
    @Test(priority = 4)
    public void verifyProfileCardNamesNotEmpty() {
        List<WebElement> cards = managersPage.getProfileCards();
        if (cards.isEmpty()) {
            System.out.println("SKIP - No cards; skipping name validation");
            return;
        }
        // Check at least the first card
        WebElement firstCard = cards.get(0);
        highlight(firstCard);
        // Look for any heading/name element inside the card
        List<WebElement> nameEls = firstCard.findElements(By.cssSelector(
                "h3, h4, [class*='name'], [class*='title'], p.font-bold, p.font-semibold"));
        boolean hasName = nameEls.stream()
                .anyMatch(el -> !el.getText().trim().isEmpty());
        Assert.assertTrue(
                hasName,
                "FAIL - First Manager/Leader profile card does not display a name. " +
                "FRD 2.3.1 requires each profile card to show the person's full name.");
        System.out.println("PASS - Profile card name element is non-empty");
    }

    // -------------------------------------------------------
    // TC-MGR-005 — "View Details" button present on profile cards
    // FRD 2.3.1 — Each profile card must have a View Details action
    // -------------------------------------------------------
    @Test(priority = 5)
    public void verifyViewDetailsButtonPresent() {
        List<WebElement> viewBtns = managersPage.getViewDetailsButtons();
        Assert.assertFalse(
                viewBtns.isEmpty(),
                "FAIL - No 'View Details' (or equivalent) buttons found on any profile card. " +
                "FRD 2.3.1 requires a View Details action on each manager/leader card.");
        highlight(viewBtns.get(0));
        System.out.println("PASS - 'View Details' button(s) found: " + viewBtns.size());
    }

    // -------------------------------------------------------
    // TC-MGR-006 — View Details opens a detail modal or profile page
    // FRD 2.3.1 — Clicking View Details must show the full manager profile
    // -------------------------------------------------------
    @Test(priority = 6)
    public void verifyViewDetailsOpensDetail() {
        List<WebElement> viewBtns = managersPage.getViewDetailsButtons();
        if (viewBtns.isEmpty()) {
            System.out.println("SKIP - No View Details button; skipping modal test");
            return;
        }
        String urlBefore = driver.getCurrentUrl();
        viewBtns.get(0).click();

        try {
            wait.until(d -> managersPage.isModalVisible() || !d.getCurrentUrl().equals(urlBefore));
        } catch (Exception ignored) {}
        String urlAfter = driver.getCurrentUrl();

        boolean modalOpened  = managersPage.isModalVisible();
        boolean pageChanged  = !urlAfter.equals(urlBefore);

        Assert.assertTrue(
                modalOpened || pageChanged,
                "FAIL - Clicking 'View Details' neither opened a modal nor navigated to a detail page. " +
                "FRD 2.3.1 requires View Details to show the full manager/leader profile.");
        System.out.println("PASS - View Details opened: " +
                (modalOpened ? "modal" : "detail page at " + urlAfter));

        // Close modal or navigate back if page changed
        if (modalOpened) {
            managersPage.closeModal();
        } else if (pageChanged) {
            driver.navigate().back();
            wait.until(ExpectedConditions.urlContains("manager"));
        }
    }

    // -------------------------------------------------------
    // TC-MGR-007 — "Create Manager" button is visible and enabled
    // FRD 2.3.2 — Super Admin must be able to create a new Manager
    // -------------------------------------------------------
    @Test(priority = 7)
    public void verifyCreateManagerButtonVisible() {
        Assert.assertTrue(
                managersPage.isCreateManagerBtnVisible(),
                "FAIL - 'Create Manager' (or 'Add Manager') button is NOT visible. " +
                "FRD 2.3.2 requires a Create Manager button on the Managers & Leadership page.");

        WebElement btn = driver.findElement(By.xpath(
                "//button[contains(normalize-space(),'Create Manager') " +
                "or contains(normalize-space(),'Add Manager')]"));
        highlight(btn);
        Assert.assertTrue(
                btn.isEnabled(),
                "FAIL - 'Create Manager' button is present but disabled. " +
                "FRD 2.3.2 requires Super Admin to be able to open the Create Manager form.");
        System.out.println("PASS - 'Create Manager' button visible and enabled: " + btn.getText().trim());
    }

    // -------------------------------------------------------
    // TC-MGR-008 — Role filter tabs (Managers / Leaders) are present
    // FRD 2.3 — Page should allow filtering between Managers and Leaders
    // NOTE: If tabs are not present (no filter UI), this is a GAP against
    //       FRD 2.3 which describes both roles on the same page.
    // -------------------------------------------------------
    @Test(priority = 8)
    public void verifyRoleFilterTabsPresent() {
        List<WebElement> tabs = managersPage.getFilterTabs();

        // Also try text-based lookup if component locator finds nothing
        if (tabs.isEmpty()) {
            tabs = driver.findElements(By.xpath(
                    "//button[contains(normalize-space(),'Manager') " +
                    "or contains(normalize-space(),'Leader')]" +
                    "[not(contains(normalize-space(),'Create'))" +
                    " and not(contains(normalize-space(),'Add'))]"));
        }

        Assert.assertFalse(
                tabs.isEmpty(),
                "FAIL - No role-filter tabs found (Manager / Leader). " +
                "GAP: FRD 2.3 displays both Managers and Leaders in the same module; " +
                "a filter or tab to switch between roles is expected. " +
                "Verify if filtering is implemented.");
        System.out.println("PASS - Role filter tabs found: " + tabs.size());
        tabs.forEach(t -> System.out.println("  Tab: " + t.getText().trim()));
    }
}
