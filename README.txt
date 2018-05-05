Project assignment:

Microblog

Specifications:
Micro blog will have simple text as news posts.
We want to create a RESTful JSON API to handle the post resource (create, update, read, etc). The service accepts json requests and responds with a json response.
This system will also support upvoting and downvoting a post.

Please note the following:
The system may be implemented in any programming language.
The posts and votes should be saved in a storage engine of your choice.
The task should be committed to GitHub or Bitbucket.
Bonus points are given for dockerizing the service (REST API and the storage engine) by formatting into a docker-compose YAML file. Otherwise, provide convenient way to run the service.
Bonus points are given for unit and/or integration tests for the system.

Web service (RESTFul API)
This service will allow the following actions:
1.  A user can create a new post providing its text.
2.  A user can update an existing post’s text.
3.  A user can upvote or downvote a post, but only once.
4.  A user can request for a list of “Top Posts”.
    Top posts should be determined by a combination of upvotes and creation time of a post.
    “Top Posts” list is being asked thousands of time per second and the number of posts in the database may be very high.





Implementation report:

Implemented as Spring-boot application with spring-data and swagger2 enabled.
Elasticsearch as persistent.

Runtime dependencies:
    Elasticsearch cluster (application will create it's index automatically).
    Apache Maven 3.
    Java 1.8

Application configuration:
    application.properties contains ElasticsearchTemplate client configuration
    (Elasticsearch cluster.name, host and port)

How to run application:
    mvn test - to execute integration tests
    mvn package - to compile, test and package application
    mvn spring-boot:run  - to run application

Where to find REST API specification and try:
    when application is up
    visit swagger page at http://localhost:8080/swagger-ui.html

