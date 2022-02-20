package com.bookit.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class USPUZipcodePage extends BasePage{

    @FindBy(id = "tZip")
    private WebElement zipCodeField;


    @FindBy(linkText = "Find")
    private WebElement findBtn;

    @FindBy(xpath = "//p[.='RECOMMENDED CITY NAME']/following-sibling::p")
    public WebElement cityNameElem;

    public void searchZipcode(String zipCode){
        zipCodeField.sendKeys(zipCode);
        findBtn.click();
    }

}
