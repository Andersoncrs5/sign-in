package com.signin.signin.e2e;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.signin.signin.Repositories.UserRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://127.0.0.1";
    }

    @BeforeEach
    void clearRepository() {
        this.userRepository.deleteAll();
    }

    @Test
    void testCreateUser() {
        String user = """
                {
                    "name":"test of silva",
                    "email":"test@gmail.com",
                    "password":"12345678"
                }
                """;

        given()
                .port(port)
                .contentType("application/json; charset=UTF-8")
                .body(user)
                .when()
                .post("/users/register")
                .then()
                .statusCode(201)
                .body("email", equalTo("test@gmail.com"))
                .body("name", equalTo("test of silva"));


    }

    @Test
    public void testLoginUser() {
        String userDto = """
                {
                    "name": "test of silva",
                    "email":"test@gmail.com",
                    "password":"12345678"
                }
                """;

        given()
                .port(port)
                .contentType("application/json; charset=UTF-8")
                .body(userDto)
                .when()
                .post("/users/register")
                .then()
                .statusCode(201)
                .body("email", equalTo("test@gmail.com"))
                .body("name", equalTo("test of silva"));

        String loginDto = """
                {
                    "email":"test@gmail.com",
                    "password":"12345678"
                }
                """;

        given()
                .port(port)
                .contentType("application/json; charset=UTF-8")
                .body(loginDto)
                .when()
                .post("/users/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());

    }

    @Test
    public void testGetUser() {
        String userDto = """
            {
                "name": "test of silva",
                "email":"test@gmail.com",
                "password":"12345678"
            }
            """;

        given()
                .port(port)
                .contentType("application/json; charset=UTF-8")
                .body(userDto)
                .when()
                .post("/users/register")
                .then()
                .statusCode(201)
                .body("email", equalTo("test@gmail.com"))
                .body("name", equalTo("test of silva"));

        String loginDto = """
            {
                "email":"test@gmail.com",
                "password":"12345678"
            }
            """;

        String token = given()
                .port(port)
                .contentType("application/json; charset=UTF-8")
                .body(loginDto)
                .when()
                .post("/users/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .path("token").toString();

        int id = given()
                .port(port)
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/users/me")
                .then()
                .statusCode(200)
                .body("email", equalTo("test@gmail.com"))
                .extract()
                .path("id");
    }

    @Test
    public void testUpdateUser() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        String userDto = """
        {
            "name": "test of silva",
            "email":"test@gmail.com",
            "password":"12345678"
        }
        """;

        String responseCreated = given()
                .port(port)
                .contentType("application/json; charset=UTF-8")
                .body(userDto)
                .when()
                .post("/users/register")
                .then()
                .statusCode(201)
                .extract()
                .response()
                .asString(); // ✅ importante

        JsonNode nodeCreated = mapper.readTree(responseCreated);
        assertNotNull(responseCreated);
        assertEquals("test@gmail.com", nodeCreated.get("email").asText());
        assertEquals("test of silva", nodeCreated.get("name").asText());
        assertNotEquals("12345678", nodeCreated.get("password").asText());

        String loginDto = """
        {
            "email":"test@gmail.com",
            "password":"12345678"
        }
        """;

        String token = given()
                .port(port)
                .contentType("application/json; charset=UTF-8")
                .body(loginDto)
                .when()
                .post("/users/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .path("token").toString();

        assertNotNull(token);

        String userDtoToUpdate = """
        {
            "name": "test of silva update",
            "email":"test@gmail.com",
            "password":"12345678"
        }
        """;

        String responseBody = given()
                .port(port)
                .contentType("application/json; charset=UTF-8")
                .header("Authorization", "Bearer " + token)
                .body(userDtoToUpdate)
                .when()
                .put("/users")
                .then()
                .statusCode(200)
                .extract()
                .response()
                .asString(); // ✅ aqui também

        assertNotNull(responseBody);

        JsonNode node = mapper.readTree(responseBody);

        assertNotNull(node.get("email"));
        assertNotNull(node.get("name"));
        assertEquals("test@gmail.com", node.get("email").asText());
        assertEquals("test of silva update", node.get("name").asText());
        assertNotEquals("12345678", node.get("password").asText());
    }

    @Test
    public void testDeleteUser() {
        String userDto = """
            {
                "name": "test of silva",
                "email":"test@gmail.com",
                "password":"12345678"
            }
            """;

        given()
                .port(port)
                .contentType("application/json; charset=UTF-8")
                .body(userDto)
                .when()
                .post("/users/register")
                .then()
                .statusCode(201)
                .body("email", equalTo("test@gmail.com"))
                .body("name", equalTo("test of silva"));

        String loginDto = """
            {
                "email":"test@gmail.com",
                "password":"12345678"
            }
            """;

        String token = given()
                .port(port)
                .contentType("application/json; charset=UTF-8")
                .body(loginDto)
                .when()
                .post("/users/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .extract()
                .path("token").toString();

        given()
                .port(port)
                .header("Authorization", "Bearer " + token)
                .when()
                .delete("/users")
                .then()
                .statusCode(200);
    }

}
