package com.bookit.step_definitions;

import com.bookit.pages.HomePage;
import com.bookit.pages.HuntPage;
import com.bookit.pages.LoginPage;
import com.bookit.pages.SpotsPage;
import com.bookit.utilities.BrowserUtils;
import com.bookit.utilities.Driver;
import com.bookit.utilities.Environment;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class UIStepsDefs {

    HomePage homePage = new HomePage();
    HuntPage huntPage = new HuntPage();
    SpotsPage spotsPage = new SpotsPage();
    static List<String> availableRooms;

    @Given("User logged in to Bookit app as teacher role")
    public void user_logged_in_to_Bookit_app_as_teacher_role() {
        Driver.getDriver().get(Environment.URL);
        LoginPage loginPage = new LoginPage();
        loginPage.login(Environment.TEACHER_EMAIL,Environment.TEACHER_PASSWORD);
    }

    @Given("User is on self page")
    public void user_is_on_self_page() {

        homePage.goToSelf();
    }

    @Given("User logged in to Bookit app as team lead role")
    public void user_logged_in_to_Bookit_app_as_team_lead_role() {
        Driver.getDriver().get(Environment.URL);
        LoginPage loginPage = new LoginPage();
        loginPage.login(Environment.LEADER_EMAIL,Environment.LEADER_PASSWORD);

        //explicitly wait for the url to change to /map
        WebDriverWait wait = new WebDriverWait(Driver.getDriver(), 30);
        wait.until(ExpectedConditions.urlContains("map"));
        assertTrue(Driver.getDriver().getCurrentUrl().endsWith("map"));
    }

    @When("User goes to room hunt page")
    public void user_goes_to_room_hunt_page() {
        homePage.hunt.click();
    }

    @When("User searches for room with date:")
    public void user_searches_for_room_with_date(Map<String,String > dateInfo) {
        huntPage.dateField.sendKeys(dateInfo.get("date"));
        BrowserUtils.waitFor(5);
        huntPage.selectStartTime(dateInfo.get("from"));
        BrowserUtils.waitFor(5);
        huntPage.selectFinishTime(dateInfo.get("to"));
        huntPage.submitBtn.click();
    }

    @Then("User should see available rooms")
    public void user_should_see_available_rooms() {

            WebDriverWait wait = new WebDriverWait(Driver.getDriver(),10);
            wait.until(ExpectedConditions.visibilityOfAllElements(spotsPage.roomNames));

            availableRooms = BrowserUtils.getElementsText(spotsPage.roomNames);

            System.out.println("availableRooms = " + availableRooms);

            Assert.assertEquals(7,availableRooms.size());
    }

}
