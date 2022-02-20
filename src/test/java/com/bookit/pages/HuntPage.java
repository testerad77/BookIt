package com.bookit.pages;

import com.bookit.utilities.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HuntPage extends  BasePage{

    @FindBy(id = "mat-input-0")
    public WebElement dateField;

    @FindBy(id = "mat-select-0")
    public WebElement from;

    @FindBy(id = "mat-select-1")
    public WebElement to;

    @FindBy(xpath = "//mat-icon[.='search']")
    public WebElement submitBtn;

    public void selectStartTime(String startTime) {
        from.click();
        Driver.getDriver().findElement(By.xpath("//span[contains(text(),'" + startTime + "')]")).click();
    }

    public void selectFinishTime(String endTime) {
        to.click();
        Driver.getDriver().findElement(By.xpath("//span[contains(text(),'" + endTime + "')]")).click();
    }

}
