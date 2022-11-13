import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {
    private String color;
    public CreateOrderTest(String color) {
        this.color = color;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][] {
                { "BLACK" },
                { "GREY" },
                { "BLACK,GREY" },
                { "" },
        };
    }

    @Test
    public void createOrderWithColorAndCheckResponse() {
        Order order = new Order(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                4,
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                color.split(","));
        given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post("/api/v1/orders")
                .then()
                .assertThat().body("track", notNullValue())
                .and()
                .statusCode(201);
    }
}
