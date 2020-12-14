# Game-of-Three
Game of Three - Coding Task for interview

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
docker build -t game-of-three:0.0.1 . && docker run -p 80:8080 game-of-three:0.0.1
```

# API
## Swagger location
``` 
http://localhost:8080/swagger-ui.html
``` 