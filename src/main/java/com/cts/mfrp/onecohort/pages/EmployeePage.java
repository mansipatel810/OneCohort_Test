package com.cts.mfrp.onecohort.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EmployeePage extends BasePage {

    private final By addEmployeeBtn  = By.xpath("//button[normalize-space()='Add']");
    private final By firstNameField  = By.id("firstName");
    private final By lastNameField   = By.id("lastName");
    private final By saveBtn         = By.xpath("//button[@type='submit']");
    private final By searchInput     = By.cssSelector(".oxd-input--active");
    private final By searchBtn       = By.xpath("//button[normalize-space()='Search']");
    private final By employeeTable   = By.cssSelector(".oxd-table-body");
    private final By noRecordsLabel  = By.xpath("//*[contains(text(),'No Records Found')]");

    public EmployeePage(WebDriver driver) {
        super(driver);
    }

    public EmployeePage clickAddEmployee() {
        click(addEmployeeBtn);
        return this;
    }

    public EmployeePage enterFirstName(String firstName) {
        type(firstNameField, firstName);
        return this;
    }

    public EmployeePage enterLastName(String lastName) {
        type(lastNameField, lastName);
        return this;
    }

    public EmployeePage clickSave() {
        click(saveBtn);
        return this;
    }

    public EmployeePage searchEmployee(String name) {
        type(searchInput, name);
        click(searchBtn);
        return this;
    }

    public boolean isEmployeeTableVisible() {
        return isDisplayed(employeeTable);
    }

    public boolean isNoRecordsDisplayed() {
        return isDisplayed(noRecordsLabel);
    }
}
