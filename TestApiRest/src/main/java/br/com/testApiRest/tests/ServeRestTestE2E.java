package br.com.testApiRest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import br.com.testApiRest.core.BaseTest;

public class ServeRestTestE2E extends BaseTest {
	ServeRestTestLogin login = new ServeRestTestLogin();
	
	/**
	 * end to end sales flow
	 */
	@Test
	public void endToEndSalesFlow() {	
		
		// Cadastrando produto 1
		String product_id_1 = given()
			.headers("Authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Tablet Alpha Gamer\",\r\n"
					+ "  \"preco\": \"1188\",\r\n"
					+ "  \"descricao\": \"Tablet Gamer\",\r\n"
					+ "  \"quantidade\": \"10\"\r\n"
					+ "}")
		.when()
			.post("/produtos")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");
		System.out.println("O Id do produto 1 é = " + product_id_1);

		// Cadastrando produto 2
		String product_id_2 = given()
			.headers("Authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Smartphone Gamer Razor\",\r\n"
					+ "  \"preco\": \"7500\",\r\n"
					+ "  \"descricao\": \"Smartphone Gamer\",\r\n"
					+ "  \"quantidade\": \"12\"\r\n"
					+ "}")
		.when()
			.post("/produtos")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");
		System.out.println("O Id do produto 2 é = " + product_id_2);
		
		// Incluindo um carrinho
		String cart_id = given()
			.headers("Authorization", login.login())
			.body("{\r\n"
					+ "  \"produtos\": [\r\n"
					+ "    {\r\n"
					+ "      \"idProduto\": \""+product_id_1+"\",\r\n"
					+ "      \"quantidade\": 1\r\n"
					+ "    },\r\n"
					+ "    {\r\n"
					+ "      \"idProduto\": \""+product_id_2+"\",\r\n"
					+ "      \"quantidade\": 1\r\n"
					+ "    }\r\n"
					+ "  ]\r\n"
					+ "}")
		.when()
			.post("/carrinhos")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");
		System.out.println("O Id do carrinho é = " + cart_id);
		
		// Validando a inclusão do carrinho na lista de carrinhos
		given()
		.when()
			.get("/carrinhos?_id=" + cart_id)
		.then()
			.statusCode(200);
		
		// Concluir compra
		given()
			.headers("Authorization", login.login())
		.when()
			.delete("/carrinhos/concluir-compra")
		.then()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso"));

		// Deletando produto 1
		given()
			.headers("Authorization", login.login())
		.when()
			.delete("/produtos/" + product_id_1)
		.then()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso"));
				
		// Deletando produto 1
		given()
			.headers("Authorization", login.login())
		.when()
			.delete("/produtos/" + product_id_2)
		.then()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso"));
		System.out.println("Compra concluida com sucesso!");
	}	
}
