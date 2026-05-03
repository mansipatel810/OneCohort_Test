package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LeavePage extends BasePage {

    private final By leaveTypeDropdown = By.cssSelector(".oxd-select-text--active");
    private final By fromDateField     = By.xpath("(//input[@placeholder='yyyy-dd-mm'])[1]");
    private final By toDateField       = By.xpath("(//input[@placeholder='yyyy-dd-mm'])[2]");
    private final By applyBtn          = By.xpath("//button[normalize-space()='Apply']");
    private final By searchBtn         = By.xpath("//button[normalize-space()='Search']");
    private final By leaveTable        = By.cssSelector(".oxd-table-body");
    private final By noRecordsLabel    = By.xpath("//*[contains(text(),'No Records Found')]");

    public LeavePage(WebDriver driver) {
        super(driver);
    }

    public LeavePage selectLeaveType(String leaveType) {
        click(leaveTypeDropdown);
        By option = By.xpath("//span[normalize-space()='" + leaveType + "']");
        click(option);
        return this;
    }

    public LeavePage enterFromDate(String date) {
        type(fromDateField, date);
        return this;
    }

    public LeavePage enterToDate(String date) {
        type(toDateField, date);
        return this;
    }

    public LeavePage clickApply() {
        click(applyBtn);
        return this;
    }

    public LeavePage clickSearch() {
        click(searchBtn);
        return this;
    }

    public boolean isLeaveTableVisible() {
        return isDisplayed(leaveTable);
    }

    public boolean isNoRecordsDisplayed() {
        return isDisplayed(noRecordsLabel);
    }
}
