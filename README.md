# Game-of-Three
Game of Three - Coding Task for interview

## Gameplay
* create
* join 
* start
* play the game
### Create a party.

```
   curl -X POST "http://localhost:8080/api/parties?nickName=player1" -H  "accept: */*" -d ""
```                               
Extract `id` from response as `partyID` for further use. 
``` 
{
  "id": "958ded7b-20f2-409a-99ae-cb70a5e68325",
  "players": [
    {
      "nickName": "player1"
    }
  ],
  "turns": [],
  "currentValue": 0,
  "currentPlayerId": null,
  "started": false,
  "over": false
}

```
### Join the game.
use `partyID`
define `playerNickName`
```
 curl -X POST "http://localhost:8080/api/parties/958ded7b-20f2-409a-99ae-cb70a5e68325/join?playerNickName=player2" -H  "accept: */*" -d ""
```               
### Start the game.
use `partyID`
```
   curl -X POST "http://localhost:8080/api/parties/958ded7b-20f2-409a-99ae-cb70a5e68325/start" -H  "accept: */*" -d ""
``` 
```
   {
     "id": "958ded7b-20f2-409a-99ae-cb70a5e68325",
     "players": [
       {
         "nickName": "player1"
       },
       {
         "nickName": "player2"
       }
     ],
     "turns": [],
     "currentValue": 46346,
     "currentPlayerId": "player2",
     "started": true,
     "over": false
   }
```
### Make a manual move 
use `partyID`
```
   curl -X POST "http://localhost:8080/api/parties/958ded7b-20f2-409a-99ae-cb70a5e68325/move" -H  "accept: */*" -H  "Content-Type: application/json" -d "{\"playerId\":\"player2\",\"addition\":1}"
```                   
### Make an auto move 
use `partyID`
```
    curl -X POST "http://localhost:8080/api/parties/958ded7b-20f2-409a-99ae-cb70a5e68325/move/auto?playerNickName=player1" -H  "accept: */*" -d ""
``` 

### Observe game status
use `partyID`
```
  curl -X GET "http://localhost:8080/api/parties/958ded7b-20f2-409a-99ae-cb70a5e68325" -H  "accept: */*"
```
The winner of the `party` is `currentPlayerId` when it is `over`.
# Run from maven
```
mvn spring-boot:run
```
# Build docker image
```
docker build -t game-of-three:0.0.1 .
```                                   
or build and run
``` 
docker build -t game-of-three:0.0.1 . && docker run -p 8080:8080 game-of-three:0.0.1
```

# API
## Swagger location
``` 
http://localhost:8080/swagger-ui.html
```      

# Configuration
`game.topNumber` - top number for name generation.