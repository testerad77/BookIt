@ui @rooms @db
  #FULL STACK TESTING  -  can take 4-5 days to work on
  Feature: Verify room reservation functionality

    Scenario: Team lead should be able to see the available rooms
      #UI
      Given User logged in to Bookit app as team lead role
      When User goes to room hunt page
      And User searches for room with date:
        |date |February 21, 2022|
        |from |9:00am           |
        |to   |9:30am           |
      Then User should see available rooms
      #API
      And User logged in to Bookit api as team lead role
      And User sends GET request to "/api/rooms/available" with:
        | year | 2022 |
        | month | 2 |
        | day | 21 |
        | conference-type | SOLID |
        | cluster-name | light-side |
        | timeline-id | 11237 |
      Then status code should be 200
      And available rooms in response should match UI results
      #Database
      And available rooms in database should match UI and API results


#    Scenario:
#    API: Send GET request to :api/rooms/available
#    With access token and
#    With query params:
#                year: 2022
#                month: 2
#                day: 21
#                conference-type: SOLID
#                cluster-name: light-side
#                timeline-id: 11237
#
#      Then json response room names must match the UI result room names.

#    select room.name from room
#    inner join cluster c on room.cluster_id = c.id
#    where c.name='light-side';
