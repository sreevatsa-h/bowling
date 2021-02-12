<h1>Assumptions made:</h1>

1. Players in a single game cannot have the same name
2. Multiple games will be running simultaneously
3. On 1 roll, everyone in the game will roll (There will be no situation where 1 player in the game has finished a frame and the other hasn't)
4. Number of lanes will be decided by the server (Number of people per lane can be changed)
5. Minimum number of players per lane will be 1 (This cannot be changed)
6. If a player is created in another game with the same name, it will be considered as a new player

<h1>Instructions for running:</h1>

1. Clone repo
2. Change application.properties file (database URLs/username/password)
3. Wait for maven dependencies to be installed
4. Run command `mvn spring-boot:run` (maven has to be installed)
   Server can also be run by importing the repo into Intellij Idea or equivalent IDE and running from it
5. Access from browser `http://localhost:8002` (The port number is `application.properties`)

<h1>Possible Issues:</h1>

1. The server is compiled in java version 11
2. `data.sql` file might not get executed at the beginning leading to rules table being empty, in this case the server won't start
    To overcome this, execute the lines in `data.sql` file manually in the database
