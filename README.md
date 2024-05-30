# Survey Website

Website that allows users to create and respond to surveys. Created using Angular bootstrap for the frontend, and Spring Boot for the backend, with a MySQL database.
  
## Prerequisites
- Have MySQL installed on your machine
- Have Angular installed on your machine
- Run `setup.sql` in MySQL environment to set up the database
- Open the file `backend/src/main/java/surveys/Helper.java`
	- On line 20, replace `yourUsername` and `yourPassword` from  `jdbc:mysql://localhost:3306/surveysite?user=yourUsername&password=yourPassword`  with your MySQL username and password
## Starting the Application
- Navigate to `/frontend` and run `ng serve`
- Navigate to `/backend` and run `./mvnw spring-boot:run`
## Features
- Ability to create and edit surveys
- Ability to respond to surveys
- Ability to view responses to your surveys