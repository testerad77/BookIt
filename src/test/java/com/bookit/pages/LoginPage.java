package com.bookit.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends BasePage{

    @FindBy(name = "email")
    public WebElement emailField;

    @FindBy(name = "password")
    public WebElement passwordField;

    @FindBy(xpath = "//button[.='sign in']")
    public WebElement signInBtn;

    public void login(String email, String password){
        emailField.sendKeys(email);
        passwordField.sendKeys(password);
        signInBtn.click();
    }
}
