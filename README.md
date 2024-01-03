# SpringProject-Note

The project is aimed at developing a secure and scalable RESTful API to facilitate note management. Leveraging Spring Boot, it ensures rapid API development with seamless integration capabilities. MySQL is selected as the primary database due to its reliability and strong support for structured data storage, crucial for managing user notes efficiently. To optimize performance, Redis, a caching tool, is utilized, effectively caching frequently accessed data and reducing database load.

One of the essential functionalities of the API is enabling users to perform CRUD (Create, Read, Update, Delete) operations on their notes. Moreover, the application allows users to share their notes with others and search for specific notes using keywords. To maintain a balanced system load and prevent misuse, the project integrates rate-limiting logic, ensuring controlled access to the API endpoints.

By combining Spring Boot's flexibility, MySQL's robust data handling, Redis caching capabilities, and effective rate-limiting measures, the project aims to deliver a secure, scalable, and efficient note management system through its RESTful API.

**As MYSQL DataBase has been used in this project, it has to be set up locally-**
1. Download MySQL community version from here- https://dev.mysql.com/downloads/mysql/
2. mysql -u root -p
3. CREATE DATABASE logdb;

**As Redis Cache is being used in this project, it has to be set up locally-**
1. brew install redis
2. redis-server
3. redis-cli

Go to **LogGuardApplication** Class, and run the application!

**API CURLS-**
1. **Sign Up-** curl --location 'http://localhost:8081/api/auth/signup' \
--header 'Content-Type: application/json' \
--data '{
    "firstName" : "Shaunak",
    "lastName" : "Ahluwalia",
    "age" : 18
}'
2. **Log In-**
curl --location 'http://localhost:8081/api/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "firstName" : "Shaunak",
    "lastName" : "Ahluwalia",
    "age" : 18
}'
3. **Create Note-**
curl --location 'http://localhost:8081/api/notes' \
--header 'accessToken: Shaunak5d7e445f-0b7a-41fe-a1c1-ce6c7dcfd93f' \
--header 'Content-Type: application/json' \
--data '{
    "id" : 1,
    "firstName" : "shaunak",
    "note" : "First Note from Shaunak"
}'
4. **Find Note By Id-**
curl --location 'http://localhost:8081/api/notes?id=1' \
--header 'accessToken: Shaunak4a15a9c4-ffc5-4b72-9e17-fcce06fa2edd' \
--data ''
/]'
6. **Delete Note By Id-**
curl --location 'http://localhost:8081/api/search?query=note' \
--header 'accessToken: Shaunak4a15a9c4-ffc5-4b72-9e17-fcce06fa2edd'
7. **Search Query-**
curl --location 'http://localhost:8081/api/search?query=note' \
--header 'accessToken: Shaunak4a15a9c4-ffc5-4b72-9e17-fcce06fa2edd'
   
