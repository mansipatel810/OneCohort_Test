package com.cts.mfrp.onecohort.tests;

import com.cts.mfrp.onecohort.base.BaseClassTest;
import com.cts.mfrp.onecohort.pages.LoginPage;
import com.cts.mfrp.onecohort.pages.SuperAdminDashboardPage;
import com.cts.mfrp.onecohort.utils.ConfigReader;
import com.cts.mfrp.onecohort.utils.ExtentReportListener;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

@Listeners(ExtentReportListener.class)
public class HomePageTest extends BaseClassTest {

    private SuperAdminDashboardPage dashboardPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUpDriver")
    public void loginAndNavigate() {
        driver.get(ConfigReader.getBaseUrl());
        new LoginPage(driver).loginAsSuperAdmin(ConfigReader.getSuperAdminUserId());
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("dashboard"));
        dashboardPage = new SuperAdminDashboardPage(driver);
        System.out.println("Login successful - Dashboard loaded");
    }

    // -------------------------------------------------------
    // TEST 1 - Check Logo is visible
    // FRD 2.1.1 - Application logo displayed as OC
    // -------------------------------------------------------
    @Test(priority = 1)
    public void checkLogo() {
        WebElement logo = dashboardPage.getLogoElement();
        highlight(logo);
        Assert.assertTrue(dashboardPage.isLogoVisible(), "Logo is not visible");
        System.out.println("PASS - Logo is visible");
    }

    // -------------------------------------------------------
    // TEST 2 - Check App Name "One Cohort" is visible
    // FRD 2.1.1 - Application name One Cohort
    // -------------------------------------------------------
    @Test(priority = 2)
    public void checkAppName() {
        WebElement appName = dashboardPage.getAppNameElement();
        highlight(appName);
        Assert.assertTrue(dashboardPage.getAppName().contains("One Cohort"), "App name is wrong");
        System.out.println("PASS - App name is: " + dashboardPage.getAppName());
    }

    // -------------------------------------------------------
    // TEST 3 - Check Subtitle "Super User Command Center"
    // FRD 2.1.1 - Subtitle must be visible in header
    // -------------------------------------------------------
    @Test(priority = 3)
    public void checkSubtitle() {
        List<WebElement> subtitle = dashboardPage.getSubtitleElements();
        Assert.assertFalse(subtitle.isEmpty(),
                "FAIL - Subtitle 'Super User Command Center' is NOT present");
        Assert.assertTrue(subtitle.get(0).isDisplayed(),
                "FAIL - Subtitle found but NOT visible");
        System.out.println("PASS - Subtitle 'Super User Command Center' is visible");
    }

    // -------------------------------------------------------
    // TEST 4 - Check Super User label is visible (top right)
    // FRD 2.1.1 - Logged in role indicator
    // -------------------------------------------------------
    @Test(priority = 4)
    public void checkSuperUserLabel() {
        WebElement roleLabel = dashboardPage.getSuperUserLabelElement();
        highlight(roleLabel);
        Assert.assertTrue(dashboardPage.getSuperUserLabelText().contains("Super User"),
                "Role label is wrong");
        System.out.println("PASS - Role label shows: " + dashboardPage.getSuperUserLabelText());
    }

    // -------------------------------------------------------
    // TEST 5 - Check SU blue avatar is visible (top right)
    // FRD 2.1.1 - User profile icon
    // -------------------------------------------------------
    @Test(priority = 5)
    public void checkSUAvatar() {
        WebElement avatar = dashboardPage.getSUAvatarElement();
        highlight(avatar);
        Assert.assertTrue(dashboardPage.isSUAvatarVisible(), "SU avatar is not visible");
        System.out.println("PASS - SU avatar is visible");
    }

    // -------------------------------------------------------
    // TEST 6 - Check User profile icon has dropdown menu
    // FRD 2.1.1 - User profile icon with dropdown menu options
    // -------------------------------------------------------
    @Test(priority = 6)
    public void checkProfileDropdown() {
        dashboardPage.clickSUAvatar();
        Assert.assertTrue(dashboardPage.isProfileDropdownVisible(),
                "FAIL - Profile dropdown not present");
        System.out.println("PASS - Profile dropdown is visible");
    }

    // -------------------------------------------------------
    // TEST 7 - Check Welcome message is visible
    // FRD 2.1.5 - Dashboard greets the logged in user
    // -------------------------------------------------------
    @Test(priority = 7)
    public void checkWelcomeMessage() {
        WebElement welcome = dashboardPage.getWelcomeElement();
        Assert.assertTrue(dashboardPage.isWelcomeMessageVisible(), "Welcome message not visible");
        highlight(welcome);
        System.out.println("PASS - Welcome message: " + welcome.getText());
    }

    // -------------------------------------------------------
    // TEST 8 - Check Left Sidebar is visible
    // FRD 2.1.2 - Left Navigation Panel
    // -------------------------------------------------------
    @Test(priority = 8)
    public void checkSidebar() {
        WebElement sidebar = dashboardPage.getSidebarElement();
        highlight(sidebar);
        Assert.assertTrue(dashboardPage.isSidebarVisible(), "Sidebar is not visible");
        System.out.println("PASS - Sidebar is visible");
    }

    // -------------------------------------------------------
    // TEST 9 - Check all menu items in sidebar
    // FRD 2.1.2 - All navigation menu items must be present
    // -------------------------------------------------------
    @Test(priority = 9)
    public void checkMenuItems() {
        String[] menuItems = {
                "Dashboard", "Cohort Management", "Managers",
                "Batch Owners", "Training Progress", "System Configuration"
        };
        for (String item : menuItems) {
            WebElement menuEl = dashboardPage.getMenuItemElement(item);
            highlight(menuEl);
            Assert.assertTrue(dashboardPage.isMenuItemVisible(item), item + " not visible in menu");
            System.out.println("PASS - Menu item visible: " + item);
        }
    }

    // -------------------------------------------------------
    // TEST 10 - Check Super User in right panel
    // FRD 2.1.3 - Right Frame User Controls
    // -------------------------------------------------------
    @Test(priority = 10)
    public void checkRightUserPanel() {
        WebElement superUser = dashboardPage.getSuperUserLabelElement();
        highlight(superUser);
        Assert.assertTrue(dashboardPage.getSuperUserLabelText().contains("Super User"),
                "Super User not shown in header");
        System.out.println("PASS - Right panel shows: " + dashboardPage.getSuperUserLabelText());
    }

    // -------------------------------------------------------
    // TEST 11 - Check Search bar gap
    // FRD 2.1.4 - Search bar not present in current build
    // -------------------------------------------------------
    @Test(priority = 11)
    public void checkSearchBarGap() {
        Assert.assertFalse(!dashboardPage.isSearchBarPresent(),
                "FAIL - Search bar is NOT present. FRD 2.1.4 requires it.");
        System.out.println("PASS - Search bar is present");
    }

    // -------------------------------------------------------
    // TEST 12 - Check Total Cohorts card is visible
    // FRD 2.1.5.1 - Summary Metrics Cards
    // -------------------------------------------------------
    @Test(priority = 12)
    public void checkTotalCohortsCard() {
        WebElement card = dashboardPage.getTotalCohortsCardElement();
        highlight(card);
        Assert.assertTrue(card.isDisplayed(), "Total Cohorts card not visible");
        System.out.println("PASS - Total Cohorts card is visible");
    }

    // -------------------------------------------------------
    // TEST 13 - Check Active card is visible
    // FRD 2.1.5.1 - Active Cohorts card
    // -------------------------------------------------------
    @Test(priority = 13)
    public void checkActiveCard() {
        WebElement card = dashboardPage.getActiveCardElement();
        highlight(card);
        Assert.assertTrue(card.isDisplayed(), "Active card not visible");
        System.out.println("PASS - Active card is visible");
    }

    // -------------------------------------------------------
    // TEST 14 - Check Completed card is visible
    // FRD 2.1.5.1 - Completed Cohorts card
    // -------------------------------------------------------
    @Test(priority = 14)
    public void checkCompletedCard() {
        WebElement card = dashboardPage.getCompletedCardElement();
        highlight(card);
        Assert.assertTrue(card.isDisplayed(), "Completed card not visible");
        System.out.println("PASS - Completed card is visible");
    }

    // -------------------------------------------------------
    // TEST 15 - Check Upcoming card is visible
    // FRD 2.1.5.1 - Upcoming Cohorts card
    // -------------------------------------------------------
    @Test(priority = 15)
    public void checkUpcomingCard() {
        WebElement card = dashboardPage.getUpcomingCardElement();
        highlight(card);
        Assert.assertTrue(card.isDisplayed(), "Upcoming card not visible");
        System.out.println("PASS - Upcoming card is visible");
    }

    // -------------------------------------------------------
    // TEST 16 - Check all KPI cards show a number
    // FRD 2.1.5.1 - Each card must display a value
    // -------------------------------------------------------
    @Test(priority = 16)
    public void checkKpiNumbers() {
        List<WebElement> numbers = dashboardPage.getKpiNumberElements();
        Assert.assertTrue(numbers.size() >= 4, "Less than 4 KPI cards found");
        for (WebElement num : numbers) {
            Assert.assertFalse(num.getText().trim().isEmpty(), "A card has no number");
            System.out.println("PASS - KPI card shows: " + num.getText());
        }
    }
}
