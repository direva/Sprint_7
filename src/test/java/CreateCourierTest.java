import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class CreateCourierTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void createNewCourierAndCheckResponse() {
        Courier courier = new Courier("qa_login", "qa_pass", "qa_firstName");
        sendCreateRequest(courier)
                .then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(201);
    }

    @Test
    public void createNewCourierWithRequiredFieldsAndCheckResponse() {
        Courier courier = new Courier("qa_login", "qa_pass");
        sendCreateRequest(courier)
                .then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(201);
    }

    @Test
    public void cannotCreateTwoSameCouriers() {
        Courier courier = new Courier("qa_login", "qa_pass", "qa_firstName");
        sendCreateRequest(courier);
        sendCreateRequest(courier)
                .then().assertThat().body("message", equalTo("Ётот логин уже используетс€. ѕопробуйте другой."))
                .and()
                .statusCode(409);;
    }

    @Test
    public void cannotCreateCourierWithLoginOnly() {
        Courier courier = new Courier();
        courier.setLogin("qa_login");
        sendCreateRequest(courier)
                .then().assertThat().body("message", equalTo("Ќедостаточно данных дл€ создани€ учетной записи"))
                .and()
                .statusCode(400);
    }

    @Test
    public void cannotCreateCourierWithPasswordOnly() {
        Courier courier = new Courier();
        courier.setPassword("qa_pass");
        sendCreateRequest(courier)
                .then().assertThat().body("message", equalTo("Ќедостаточно данных дл€ создани€ учетной записи"))
                .and()
                .statusCode(400);
    }

    @Test
    public void cannotCreateCourierWithFirstNameOnly() {
        Courier courier = new Courier();
        courier.setFirstName("qa_firstName");
        sendCreateRequest(courier)
                .then().assertThat().body("message", equalTo("Ќедостаточно данных дл€ создани€ учетной записи"))
                .and()
                .statusCode(400);
    }

    @Test
    public void cannotCreateCourierWithoutInfo() {
        Courier courier = new Courier();
        sendCreateRequest(courier)
                .then().assertThat().body("message", equalTo("Ќедостаточно данных дл€ создани€ учетной записи"))
                .and()
                .statusCode(400);
    }

    @After
    public void tearDown() {
        Courier courier = new Courier("qa_login", "qa_pass");
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
