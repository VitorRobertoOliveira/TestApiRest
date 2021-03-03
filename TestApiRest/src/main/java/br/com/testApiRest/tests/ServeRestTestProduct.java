package br.com.testApiRest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import br.com.testApiRest.core.BaseTest;

public class ServeRestTestProduct extends BaseTest {
	ServeRestTestLogin login = new ServeRestTestLogin();
	
	/**
	 * Register product and check if it was inserted
	 */
	@Test
	public void registerProductAndCheckIfItWasInserted() {
		
		// Cadastrando um novo produto
		String product_id = given()
			.headers("Authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Mouse Multilaser\",\r\n"
					+ "  \"preco\": \"100\",\r\n"
					+ "  \"descricao\": \"Mouse\",\r\n"
					+ "  \"quantidade\": \"5\"\r\n"
					+ "}")
		.when()
			.post("/produtos")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");
		
		// Validando se a inclusão foi realizada com sucesso e que o mesmo encontra-se na lista
		given()
		.when()
			.get("/produtos?_id=" + product_id)
		.then()
			.statusCode(200)
			.body("produtos.nome", hasItem("Mouse Multilaser"));

		// Deletando um produto
		given()
			.headers("Authorization", login.login())
		.when()
			.delete("/produtos/" + product_id)
		.then()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso"));		
	}
	
	/**
	 * Check if the product list returns empty
	 */
	@Test
	public void checkIfTheProductListReturnsEmpty() {
		
		// Validando que a lista de produtos não está vazia
		given()
		.when()
			.get("/produtos")
		.then()
			.statusCode(200)
			.body("produtos", not(empty()));	
	}
	
	/**
	 * Validate the product registration block with the same name
	 */
	@Test
	public void validateTheProductRegistrationBlockWithTheSameName() {
		
		// Cadastrando um novo produto
		String product_id = given()
			.headers("Authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Chromebook\",\r\n"
					+ "  \"preco\": \"2789\",\r\n"
					+ "  \"descricao\": \"Notebook\",\r\n"
					+ "  \"quantidade\": \"5\"\r\n"
					+ "}")
		.when()
			.post("/produtos")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");
				
		// Validando se a inclusão foi realizada com sucesso e que o mesmo encontra-se na lista
		given()
		.when()
			.get("/produtos?_id=" + product_id)
		.then()
			.statusCode(200)
			.body("produtos.nome", hasItem("Chromebook"));
		
		// Cadastrando um produto de mesmo nome
		given()
			.headers("Authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Chromebook\",\r\n"
					+ "  \"preco\": \"2789\",\r\n"
					+ "  \"descricao\": \"Notebook\",\r\n"
					+ "  \"quantidade\": \"5\"\r\n"
					+ "}")
		.when()
			.post("/produtos")
		.then()
			.statusCode(400)
			.body("message", is("Já existe produto com esse nome"));
		
		// Deletando um produto
		given()
			.headers("Authorization", login.login())
		.when()
			.delete("/produtos/" + product_id)
		.then()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso"));		
	}
	
	/**
	 * Validate product registration block without authentication token
	 */
	@Test
	public void validateProductRegistrationBlockWithoutAuthenticationToken() {
		
		// Cadastrando um produto sem token
		given()
			.body("{\r\n"
					+ "  \"nome\": \"Gabinete Fortrek ATX\",\r\n"
					+ "  \"preco\": \"100\",\r\n"
					+ "  \"descricao\": \"Gabinete\",\r\n"
					+ "  \"quantidade\": \"5\"\r\n"
					+ "}")
		.when()
			.post("/produtos")
		.then()
			.statusCode(401)
			.body("message", is("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
		
	}
	
	/**
	 * Validate product registration block with user without admin permission
	 */
	@Test
	public void validateProductRegistrationBlockWithUserWithoutAdminPermission() {	
		
		// Inserindo um usuário sem permissão de Admin e salvando o _id em uma string
		String _id = given()
			.header("authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Tester Testando\",\r\n"
					+ "  \"email\": \"fakeremail@qa.com\",\r\n"
					+ "  \"password\": \"teste\",\r\n"
					+ "  \"administrador\": \"false\"\r\n"
					+ "}")
		.when()
			.post("/usuarios")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");
				
		// Login com usuário sem permissão de Admin
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "fakeremail@qa.com");
		login.put("password", "teste");
				
		String newToken = given()
			.body(login)
		.when()
			.post("/login")
		.then()
			.statusCode(200)
			.body("message", is("Login realizado com sucesso"))
			.extract().path("authorization");		
		
		// Validando o bloqueio de cadastro de produto com usuario sem permissão de admin
		given()
			.headers("Authorization", newToken)
			.body("{\r\n"
					+ "  \"nome\": \"Mesa Digitalizadora One\",\r\n"
					+ "  \"preco\": \"375\",\r\n"
					+ "  \"descricao\": \"Mesa Digitalizadora\",\r\n"
					+ "  \"quantidade\": \"2\"\r\n"
					+ "}")
		.when()
			.post("/produtos")
		.then()
			.statusCode(403)
			.body("message", is("Rota exclusiva para administradores"));
		
		// Deletando o usuário
		given()
			.header("authorization", newToken)
		.when()
			.delete("/usuarios/" + _id)
		.then()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso"));
	}
	
	/**
	 * Edit product
	 */
	@Test
	public void editProduct() {	

		// Cadastrando um novo produto
		String product_id = given()
			.headers("Authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Dell Latitude\",\r\n"
					+ "  \"preco\": \"8958\",\r\n"
					+ "  \"descricao\": \"Notebook\",\r\n"
					+ "  \"quantidade\": \"15\"\r\n"
					+ "}")
		.when()
			.post("/produtos")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");

		// Editando o produto 
		given()
			.header("authorization", login.login())
			.body("{\r\n"
					+ "\"nome\": \"Notebook Positivo Q232B\",\r\n"
					+ "\"preco\": 1736,\r\n"
					+ "\"descricao\": \"Notebook\",\r\n"
					+ "\"quantidade\":5\r\n"
					+ "}")
		.when()
			.put("/produtos/" + product_id)
		.then()
			.statusCode(200)
			.body("message", is("Registro alterado com sucesso"));

		// Deletando um produto
		given()
			.headers("Authorization", login.login())
		.when()
			.delete("/produtos/" + product_id)
		.then()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso"));		
	}
	
	/**
	 * Validate the blocking of the edition of a product with the same name
	 */
	@Test
	public void validateTheBlockingOfTheEditionOfAProductWithTheSameName() {	

		// Cadastrando produto 1
		String product_id_1 = given()
			.headers("Authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"MacBook\",\r\n"
					+ "  \"preco\": \"8958\",\r\n"
					+ "  \"descricao\": \"MacBook\",\r\n"
					+ "  \"quantidade\": \"15\"\r\n"
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
					+ "  \"nome\": \"MacBook Pro\",\r\n"
					+ "  \"preco\": \"8958\",\r\n"
					+ "  \"descricao\": \"MacBook\",\r\n"
					+ "  \"quantidade\": \"15\"\r\n"
					+ "}")
		.when()
			.post("/produtos")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");

		// Tentando alterar o produto utilizando o mesmo nome
		given()
			.header("authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"MacBook\",\r\n"
					+ "  \"preco\": \"8958\",\r\n"
					+ "  \"descricao\": \"MacBook\",\r\n"
					+ "  \"quantidade\": \"15\"\r\n"
					+ "}")
		.when()
			.put("/produtos/" + product_id_2)
		.then()
			.statusCode(400)
			.body("message", is("Já existe produto com esse nome"));

		// Deletando produto 1
		given()
			.headers("Authorization", login.login())
		.when()
			.delete("/produtos/" + product_id_1)
		.then()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso"));
		
		// Deletando produto 2
		given()
			.headers("Authorization", login.login())
		.when()
			.delete("/produtos/" + product_id_2)
		.then()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso"));
	}
	
	/**
	 * Validate a product's edit block without an authentication token
	 */
	@Test
	public void validateProductsEditBlockWithoutAnAuthenticationToken() {
		
		// Cadastrando um novo produto
		String product_id = given()
			.headers("Authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Mesa Gamer DT3Sports Infinity\",\r\n"
					+ "  \"preco\": \"8958\",\r\n"
					+ "  \"descricao\": \"Mesa\",\r\n"
					+ "  \"quantidade\": \"15\"\r\n"
					+ "}")
		.when()
			.post("/produtos")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");

		// Tentando alterar o produto sem token de autenticação
		given()
			.body("{\r\n"
					+ "\"nome\": \"Samsung 55 polegadas\",\r\n"
					+ "\"preco\": 1300,\r\n"
					+ "\"descricao\": \"TV\",\r\n"
					+ "\"quantidade\":15\r\n"
					+ "}")
		.when()
			.put("/produtos/" + product_id)
		.then()
			.statusCode(401)
			.body("message", is("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
		
		// Deletando um produto
		given()
			.headers("Authorization", login.login())
		.when()
			.delete("/produtos/" + product_id)
		.then()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso"));	
	}
	
	/**
	 * Validate product edit block with user without admin permission
	 */
	@Test
	public void validateProductEditBlockWithUserWithoutAdminPermission() {

		// Inserindo um usuário sem permissão de Admin e salvando o _id em uma string
		String _id = given()
			.header("authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Tester Testando\",\r\n"
					+ "  \"email\": \"fakeremail@qa.com\",\r\n"
					+ "  \"password\": \"teste\",\r\n"
					+ "  \"administrador\": \"false\"\r\n"
					+ "}")
		.when()
			.post("/usuarios")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");
						
		// Login com usuário sem permissão de Admin
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "fakeremail@qa.com");
		login.put("password", "teste");
						
		String newToken = given()
			.body(login)
		.when()
			.post("/login")
		.then()
			.statusCode(200)
			.body("message", is("Login realizado com sucesso"))
			.extract().path("authorization");
		
		// Buscando um produto na lista
		String product_id = given()
		.when()
			.get("/produtos")
		.then()
			.statusCode(200)
			.extract().path("produtos._id[0]");

		// Validando o bloqueio de edição do produto com usuario sem permissão de admin
		given()
			.headers("Authorization", newToken)
			.body("{\r\n"
					+ "\"nome\": \"Samsung 55 polegadas\",\r\n"
					+ "\"preco\": 1300,\r\n"
					+ "\"descricao\": \"TV\",\r\n"
					+ "\"quantidade\": 15\r\n"
					+ "}")
		.when()
			.put("/produtos/" + product_id)
		.then()
			.statusCode(403)
			.body("message", is("Rota exclusiva para administradores"));
		
		// Deletando o usuário
		given()
			.header("authorization", newToken)
		.when()
			.delete("/usuarios/" + _id)
		.then()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso"));
	}
	
	/**
	 * Delete product
	 */
	@Test
	public void deleteProduct() {
		
		// Cadastrando um novo produto
		String product_id = given()
			.headers("Authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Notebook Lenovo Ideapad\",\r\n"
					+ "  \"preco\": \"3899\",\r\n"
					+ "  \"descricao\": \"Notebook Lenovo\",\r\n"
					+ "  \"quantidade\": \"8\"\r\n"
					+ "}")
		.when()
			.post("/produtos")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");

		// Deletando um produto
		given()
			.headers("Authorization", login.login())
		.when()
			.delete("/produtos/" + product_id)
		.then()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso"));
	}
	
	/**
	 * Validate a product exclusion block without an authentication token
	 */
	@Test
	public void validateAProductExclusionBlockWithoutAnAuthenticationToken() {

		// Buscando um produto na lista
		String product_id = given()
		.when()
			.get("/produtos")
		.then()
			.statusCode(200)
			.extract().path("produtos._id[0]")
		;

		// Deletando um produto sem token
		given()
		.when()
			.delete("/produtos/" + product_id)
		.then()
			.statusCode(401)
			.body("message", is("Token de acesso ausente, inválido, expirado ou usuário do token não existe mais"));
	}
	
	/**
	 * Validate product delete block with user without admin permission
	 */
	@Test
	public void validateProductDeleteBlockWithUserWithoutAdminPermission() {

		// Inserindo um usuário sem permissão de Admin e salvando o _id em uma string
		String _id = given()
			.header("authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Tester Testando\",\r\n"
					+ "  \"email\": \"fakeremail@qa.com\",\r\n"
					+ "  \"password\": \"teste\",\r\n"
					+ "  \"administrador\": \"false\"\r\n"
					+ "}")
		.when()
			.post("/usuarios")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");
								
		// Login com usuário sem permissão de Admin
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "fakeremail@qa.com");
		login.put("password", "teste");
								
		String newToken = given()
			.body(login)
		.when()
			.post("/login")
		.then()
			.statusCode(200)
			.body("message", is("Login realizado com sucesso"))
			.extract().path("authorization");
		
		// Buscando um produto na lista
		String product_id = given()
		.when()
			.get("/produtos")
		.then()
			.statusCode(200)
			.extract().path("produtos._id[0]");
		
		// Deletando um produto com usuário sem permissão de Admin
		given()
			.headers("Authorization", newToken)
		.when()
			.delete("/produtos/" + product_id)
		.then()
			.statusCode(403)
			.body("message", is("Rota exclusiva para administradores"));
		
		// Deletando o usuário
		given()
			.header("authorization", newToken)
		.when()
			.delete("/usuarios/" + _id)
		.then()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso"));
	}
}
