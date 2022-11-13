import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginCourierTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
        Courier courier = new Courier("qa_new_login", "qa_new_pass", "qa_firstName");
        sendCreateRequest(courier);
    }

    @Test
    public void courierCanLogin() {
        Courier courier = new Courier("qa_new_login", "qa_new_pass");
        sendLoginRequest(courier)
                .then().assertThat().body("id", notNullValue())
                .and().statusCode(200);
    }

    @Test
    public void courierCannotLoginWithoutPassword() {
        Courier courier = new Courier();
        courier.setLogin("qa_new_login");
        sendLoginRequest(courier)
                .then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and().statusCode(400);
    }

    @Test
    public void courierCannotLoginWithoutLogin() {
        Courier courier = new Courier();
        courier.setPassword("qa_new_pass");
        sendLoginRequest(courier)
                .then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and().statusCode(400);
    }

    @Test
    public void courierCannotLoginWithIncorrectLogin() {
        Courier courier = new Courier("qa_inc_login", "qa_new_pass");
        sendLoginRequest(courier)
                .then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and().statusCode(404);
    }

    @Test
    public void courierCannotLoginWithIncorrectPassword() {
        Courier courier = new Courier("qa_new_login", "qa_inc_pass");
        sendLoginRequest(courier)
                .then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and().statusCode(404);
    }

    @After
    public void tearDown() {
        Courier courier = new Courier("qa_new_login", "qa_new_pass");
        CourierId response = sendLoginRequest(courier).body().as(CourierId.class);
        sendDeleteRequest(response);
    }

    private Response sendCreateRequest(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    private Response sendLoginRequest(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
    }

    private Response sendDeleteRequest(CourierId courierId) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body("{\"id\": \"" + courierId.getId() + "\"}")
                .when()
                .delete("/api/v1/courier/" + courierId.getId());
    }
}
