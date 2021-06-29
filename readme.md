# Calorie Tracker API

## Configuration

Define the following properties before running the application: 

```properties
jwt.secret=your-secret
nutritionix.app.id=your-app-id
nutritionix.app.key=your-app-key
```

`jwt.secret` is the secret that will be used for jwt token signing, this can be any password string

`nutritionix.app.id` and `nutritionix.app.key` are the app id and app key provided at 
https://developer.nutritionix.com/ 

## Build

You can build the application by running: 

```shell script
./gradlew clean build
```

End to end tests will nu run as part of the build, to run them separately use the following command: 

```shell script
./gradlew integrationTest
```

## Running 

The app uses the H2 in-memory database, each time the app run it will run with a new database

The database is preloaded with the following user: 

```json
{
  "username": "zoltan",
  "password": "zoltan"
}
```
This user has admin credentials, it can modify other users and add permissions to them
It also has some entries and a default setting preloaded.

To run the app, use the following command: 

```shell script
./gradlew bootRun
``` 

There is an insomnia export file in the project: `insomnia_workspace.json` 
This can be used to test the functionality using: https://insomnia.rest/
