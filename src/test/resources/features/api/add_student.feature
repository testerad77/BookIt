Feature: Add new student
  @add_student
  Scenario: Add new student and verify status code is 200
    Given User logged in to Bookit api as teacher role
    And User sends POST request to "/api/students/student" with following info:
      | first-name      | tester12345              |
      | last-name       | finch               |
      | email           | tester12345@gmail.com  |
      | password        | abc123              |
      | role            | student-team-leader |
      | campus-location | VA                  |
      | batch-number    | 8                   |
      | team-name       | Nukes               |
    Then status code should be 201
    And user deletes previously created student