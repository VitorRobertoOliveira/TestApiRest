package br.com.testApiRest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

import br.com.testApiRest.core.BaseTest;

public class ServeRestTestCart extends BaseTest {
	ServeRestTestLogin login = new ServeRestTestLogin();
	
	/**
	 * List cart and check if it returns empty
	 */
	@Test
	public void listCartAndCheckIfItReturnsEmpty() {

		// Buscar a lista o carrinho e validar que a lista não está vazia
		given()
		.when()
			.get("/carrinhos")
		.then()
			.statusCode(200)
			.body("carrinhos", not(empty()));
		}
	
	/**
	 * Register Cart
	 */
	@Test
	public void registerCartAndCancelPurchase() {
		
		// Cadastrando produto 1
		String product_id_1 = given()
			.headers("Authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Notebook Dell E6420\",\r\n"
					+ "  \"preco\": \"10000\",\r\n"
					+ "  \"descricao\": \"Notebook\",\r\n"
					+ "  \"quantidade\": \"20\"\r\n"
					+ "}")
		.when()
			.post("/produtos")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");
		
		// Cadastrando produto 2
		String product_id_2 = given()
			.headers("Authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Cabo de Rede\",\r\n"
					+ "  \"preco\": \"5\",\r\n"
					+ "  \"descricao\": \"Cabo\",\r\n"
					+ "  \"quantidade\": \"50\"\r\n"
					+ "}")
		.when()
			.post("/produtos")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");
		
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
		
		// Validando a inclusão do carrinho na lista de carrinhos
		given()
		.when()
			.get("/carrinhos?_id=" + cart_id)
		.then()
			.statusCode(200);
		
		// Cancelando a compra
		given()
			.headers("Authorization", login.login())
		.when()
			.delete("/carrinhos/cancelar-compra")
		.then()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso. Estoque dos produtos reabastecido"));

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
	}
	
	/**
	 * Validate that it is not allowed to have more than one cart
	 */
	@Test
	public void validateThatItIsNotAllowedToHaveMoreThanOneCart() {
		
		// Cadastrando produto 1
		String product_id_1 = given()
			.headers("Authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Dell Inspiron\",\r\n"
					+ "  \"preco\": \"10000\",\r\n"
					+ "  \"descricao\": \"Notebook\",\r\n"
					+ "  \"quantidade\": \"20\"\r\n"
					+ "}")
		.when()
			.post("/produtos")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");
				
		// Cadastrando produto 2
		String product_id_2 = given()
			.headers("Authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"HD Seagate\",\r\n"
					+ "  \"preco\": \"759\",\r\n"
					+ "  \"descricao\": \"HD portatil\",\r\n"
					+ "  \"quantidade\": \"20\"\r\n"
					+ "}")
		.when()
			.post("/produtos")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");
				
		// Incluindo um carrinho
		given()
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
			.body("message", is("Cadastro realizado com sucesso"));
				
		//validar que não é permitido ter mais de um carrinho
		given()
			.headers("Authorization", login.login())
			.body("{\r\n"
				+ "  \"produtos\": [\r\n"
				+ "    {\r\n"
				+ "      \"idProduto\": \""+product_id_1+"\",\r\n"
				+ "      \"quantidade\": 1\r\n"
				+ "    },\r\n"
				+ "    {\r\n"
				+ "      \"idProduto\": \""+product_id_2+"\",\r\n"
				+ "      \"quantidade\": 3\r\n"
				+ "    }\r\n"
				+ "  ]\r\n"
				+ "}")
		.when()
			.post("/carrinhos")
		.then()
			.statusCode(400)
			.body("message", is("Não é permitido ter mais de 1 carrinho"));
				
		// Cancelando a compra
		given()
			.headers("Authorization", login.login())
		.when()
			.delete("/carrinhos/cancelar-compra")
		.then()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso. Estoque dos produtos reabastecido"));

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
		}
	
	/**
	 * Complete purchase
	 */
	@Test
	public void completePurchase() {
		
		// Cadastrando produto 1
		String product_id_1 = given()
			.headers("Authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Notebook Asus\",\r\n"
					+ "  \"preco\": \"10000\",\r\n"
					+ "  \"descricao\": \"Notebook\",\r\n"
					+ "  \"quantidade\": \"20\"\r\n"
					+ "}")
		.when()
			.post("/produtos")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");
						
		// Cadastrando produto 2
		String product_id_2 = given()
			.headers("Authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Fone JBL\",\r\n"
					+ "  \"preco\": \"759\",\r\n"
					+ "  \"descricao\": \"Fone\",\r\n"
					+ "  \"quantidade\": \"20\"\r\n"
					+ "}")
		.when()
			.post("/produtos")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");
						
		// Incluindo um carrinho
		given()
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
			.body("message", is("Cadastro realizado com sucesso"));
		
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
	}
	
	/**
	 * Validate a return message when there is no cart for the user
	 */
	@Test
	public void validateReturnMessageWhenThereIsNoCartForTheUser() {	
		
		// Validar retorno da msg "Não foi encontrado carrinho para esse usuário"
		given()
			.headers("Authorization", login.login())
		.when()
			.delete("/carrinhos/concluir-compra")
		.then()
			.statusCode(200)
			.body("message", is("Não foi encontrado carrinho para esse usuário"));
	}
	
	/**
	 * Validate the blocking of the completion of the sale of a cart without an authentication token
	 */
	@Test
	public void validateTheBlockingOfTheCompletionOfTheSaleOfCartWithoutAnAuthenticationToken() {	
		
		// Validar que não é possivel concluir a compra sem token de autenticação
		given()
		.when()
			.delete("/carrinhos/concluir-compra")
		.then()
			.statusCode(401)
			.body("message", is("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
	}
	
	/**
	 * validate return message when there is no cart to be canceled
	 */
	@Test
	public void validateReturnMessageWhenThereIsNoCartToBeCanceled() {	
		
		// Validar retorno da msg "Não foi encontrado carrinho para esse usuário"
		given()
			.headers("Authorization", login.login())
		.when()
			.delete("/carrinhos/cancelar-compra")
		.then()
			.statusCode(200)
			.body("message", is("Não foi encontrado carrinho para esse usuário"));
		}
	
	/**
	 * Validate the block for canceling the sale of a cart without an authentication token
	 */
	@Test
	public void validateTheBlockForCancelingTheSaleOfCartWithoutAnAuthenticationToken() {	
		
		// Validar que não é possivel cancelar a compra sem token de autenticação
		given()
		.when()
			.delete("/carrinhos/cancelar-compra")
		.then()
			.statusCode(401)
			.body("message", is("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
		}	
}
