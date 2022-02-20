package com.bookit.step_definitions;

import com.bookit.pages.SelfPage;
import com.bookit.utilities.BookItApiUtil;
import com.bookit.utilities.DBUtils;
import com.bookit.utilities.Environment;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ApiStepDefs {

    Response response;
    String accessToken;
    Map<String,String> newRecordMap;
    List<String> apiAvailableRooms;

    @Given("User logged in to Bookit api as teacher role")
    public void user_logged_in_to_Bookit_api_as_teacher_role() {
        accessToken = BookItApiUtil.getAccessToken(Environment.TEACHER_EMAIL, Environment.TEACHER_PASSWORD);
        System.out.println("teacher email = " + Environment.TEACHER_EMAIL);
        System.out.println("teacher password = " + Environment.TEACHER_PASSWORD);
    }

    @Given("User sends GET request to {string}")
    public void user_sends_GET_request_to(String path) {
        response = given().accept(ContentType.JSON)
                .and().header("Authorization",accessToken)
                .when().get(Environment.BASE_URL + path);
        System.out.println("API endpoint = " + Environment.BASE_URL + path);
        //          https://cybertek-reservation-api-qa.herokuapp.com/api/users/me
        response.prettyPrint();
    }

    @Then("status code should be {int}")
    public void status_code_should_be(int expStatusCode) {
        assertThat(response.statusCode(),is(expStatusCode));
    }

    @Then("content type is {string}")
    public void content_type_is(String expContentType) {
        assertThat(response.contentType(),equalTo(expContentType));
    }


    @And("role is {string}")
    public void roleIs(String expRole) {
        JsonPath json = response.jsonPath();
        System.out.println("role= " + json.getString("role"));
        assertThat(json.getString("role"), is(expRole));
    }

    /**
     "id": 11516,
     "firstName": "Barbabas",
     "lastName": "Lyst",
     "role": "teacher"
     */
    @Then("User should see same info on UI and API")
    public void user_should_see_same_info_on_UI_and_API() {
        //read values into a map from api  /deserializing
        Map<String,Object> apiUserMap = response.body().as(Map.class);
        String apiFullName = apiUserMap.get("firstName") + " " + apiUserMap.get("lastName");
        System.out.println("apiFullName = " + apiFullName);

        //read values  from UI using POM
        SelfPage selfPage = new SelfPage();
        String uiFullName = selfPage.fullName.getText();
        String uiRole = selfPage.role.getText();

        System.out.println("uiFullName = " + uiFullName);

        //assertions
        assertThat(uiFullName,equalTo(apiFullName));
        assertThat(uiRole,equalTo(apiUserMap.get("role")));
    }

    @And("User sends POST request to {string} with following info:")
    public void userSendsPOSTRequestToWithFollowingInfo(String endPoint,Map<String,String> newEntryInfo) {
        newRecordMap = newEntryInfo;//assign the query map to newRecordMap
        response = given().accept(ContentType.JSON)
                .and().header("Authorization",accessToken)
                .and().params(newEntryInfo).log().all()
                .when().post(Environment.BASE_URL + endPoint);
        response.prettyPrint();

    }

    @And("user deletes previously created student")
    public void userDeletesPreviouslyCreatedStudent() {
        int studentId = response.path("entryiId");
        given().accept(ContentType.JSON)
                .and().header("Authorization",accessToken)
                .when().delete(Environment.BASE_URL + "/api/students/" + studentId)
                .then().assertThat().statusCode(204);
    }

    //documentation => GET /api/teams/{id} => it retrieves a team by id
    @And("User sends GET request to {string} with {string}")
    public void userSendsGETRequestToWith(String endPoint, String teamId) {
        response = given().accept(ContentType.JSON)
                .and().header("Authorization", accessToken)
                .and().pathParam("id", teamId).log().all()
                .when().get(Environment.BASE_URL + endPoint);

    }

    @And("Team name should match {string} in response")
    public void teamNameShouldMatchInResponse(String expTeamName) {
        response.prettyPrint();
        assertThat(response.path("name"),is(expTeamName));
    }

    @And("Database query should have same {string} and {string}")
    public void databaseQueryShouldHaveSameAnd(String teamId, String teamName) {
        String sql = "select id, name from team where id = " + teamId;
        Map<String,Object> dbTeamInfo = DBUtils.getRowMap(sql);
        System.out.println("dbTeamInfo = " + dbTeamInfo);
        assertThat(dbTeamInfo.get("id"),equalTo(Long.parseLong(teamId)));
        assertThat(dbTeamInfo.get("name"),equalTo(teamName));
    }

    @And("Database should persist same team info")
    public void databaseShouldPersistSameTeamInfo() {
        int newTeamId = response.path("entryiId");
        String sql = "SELECT * FROM team WHERE id = " + newTeamId;
        Map<String,Object> dbNewTeamMap = DBUtils.getRowMap(sql);
        System.out.println("dbNewTeam = " + dbNewTeamMap);

        assertThat(dbNewTeamMap.get("id"),equalTo((long)newTeamId));
        assertThat(dbNewTeamMap.get("name"),equalTo(newRecordMap.get("team-name")));
        assertThat(dbNewTeamMap.get("batch_number").toString(),equalTo(newRecordMap.get("batch-number")));
    }

    @And("User logged in to Bookit api as team lead role")
    public void userLoggedInToBookitApiAsTeamLeadRole() {
        accessToken = BookItApiUtil.getAccessToken(Environment.LEADER_EMAIL, Environment.LEADER_PASSWORD);
        System.out.println("Team lead email = " + Environment.LEADER_EMAIL);
        System.out.println("Team lead password = " + Environment.LEADER_PASSWORD);
    }

    @And("User sends GET request to {string} with:")
    public void userSendsGETRequestToWith(String endpoint, Map<String,String> queryParams) {
        response = given().accept(ContentType.JSON)
                .and().header("Authorization",accessToken)
                .and().queryParams(queryParams)
                .when().get(Environment.BASE_URL + endpoint);
    }

    @And("available rooms in response should match UI results")
    public void availableRoomsInResponseShouldMatchUIResults() {
        response.prettyPrint();
        //availableRooms = [mit, harvard, yale, princeton, stanford, duke, berkeley]
        JsonPath json = response.jsonPath();
        apiAvailableRooms = json.getList("name");
        System.out.println("roomsList = " + apiAvailableRooms);
        System.out.println("UIStepsDefs.availableRooms = " + UIStepsDefs.availableRooms);
        assertThat(UIStepsDefs.availableRooms,equalTo(apiAvailableRooms));
        assertThat(UIStepsDefs.availableRooms,equalTo(response.jsonPath().getList("name")));//same as above
    }

    @And("available rooms in database should match UI and API results")
    public void availableRoomsInDatabaseShouldMatchUIAndAPIResults() {
        String query = "select room.name from room inner join cluster on room.cluster_id = cluster.id where cluster.name='light-side'";
        List<Object> dbAvailableRooms =  DBUtils.getColumnData(query,"name");
        System.out.println("dbAvailableRooms = " + dbAvailableRooms);

        //available rooms in database should match UI and API
        assertThat(dbAvailableRooms,allOf(equalTo(apiAvailableRooms),equalTo(UIStepsDefs.availableRooms)));
    }

    @And("User deletes previously created team")
    public void userDeletesPreviouslyCreatedTeam() {
        //read ID of newly created team
        int teamId = response.path("entryiId");
        given().accept(ContentType.JSON).log().all()
                .and().header("Authorization", accessToken)
                .when().delete(Environment.BASE_URL+"/api/teams/"+teamId)
                .then().assertThat().statusCode(200);
    }
}
