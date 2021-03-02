package br.com.testApiRest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import br.com.testApiRest.core.BaseTest;

public class ServeRestTest extends BaseTest {
	
	private String TOKEN;
	
	@Before
	public void login() {
		// Login com usuário válido!
		Map<String, String> login = new HashMap<>();
		login.put("email", "fulano@qa.com");
		login.put("password", "teste");
		
		TOKEN = given()
			.body(login)
		.when()
			.post("/login")
		.then()
			.statusCode(200)
			.body("message", is("Login realizado com sucesso"))
			.extract().path("token");
	}
	
	/**
	 * Trying to login incorrect credential
	 */
	@Test
	public void tryingToLoginIncorrectCredential() {
		Map<String, String> login = new HashMap<>();
		login.put("email", "testeTestando@qa.com");
		login.put("password", "teste123");
		given()
			.body(login)
		.when()
			.post("/login")
		.then()
			.statusCode(401)
			.body("message", is("Email e/ou senha inválidos"));
	}
	
	/**
	 * Trying to login correct credential
	 */
	@Test
	public void tryingToLoginCorrectCredential() {
		Map<String, String> login = new HashMap<>();
		login.put("email", "fulano@qa.com");
		login.put("password", "teste");
		given()
			.body(login)
		.when()
			.post("/login")
		.then()
			.statusCode(200)
			.body("message", is("Login realizado com sucesso"))
		;			
	}
	
	/**
	 * Shouldn't access API without token
	 */
	@Test
	public void shouldntAccessApiWithoutToken() {
		given()
		.when()
			.get("/login")
		.then()
			.statusCode(405)
			.body("message", is("Não é possível realizar GET em /login. Acesse https://serverest.dev para ver as rotas disponíveis e como utilizá-las."))
		;			
	}
	
	/**
	 * Registering user and validating inclusion
	 */
	@Test
	public void registerUsers() {
		
		// Inserindo um usuário e salvando o _id em uma string
		String _id = given()
			.header("authorization", "Bearer " + TOKEN)
			.body("{\r\n"
					+ "  \"nome\": \"Vitor Tester Testador\",\r\n"
					+ "  \"email\": \"vitorotesterksjfhksjdf@qa.com\",\r\n"
					+ "  \"password\": \"teste@123\",\r\n"
					+ "  \"administrador\": \"true\"\r\n"
					+ "}")
		.when()
			.post("/usuarios")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");	
		
		// Buscando o usuário cadastrado e validando que o mesmo está na lista de usuários
		given()
		.when()
			.get("/usuarios?_id=" + _id)
		.then()
			.statusCode(200)
			.body("usuarios.nome[0]", containsString("Vitor Tester Testador"));
	}
	
	/**
	 * Validating Whether the user list returns empty
	 */
	@Test
	public void validatingWhetherTheUserListReturnsEmpty() {
			given()
			.when()
				.get("/usuarios")
			.then()
				.statusCode(200)
				.body("usuarios", not(empty()));
		
	}
	
	/**
	 * Validate the user registration block with the same email
	 */
	@Test
	public void validateTheUserRegistrationBlockWithTheSameEmail() {
				
		// Inserindo um usuário com um email já cadastrado
		given()
			.header("authorization", "Bearer " + TOKEN)
			.body("{\r\n"
					+ "  \"nome\": \"Vitor Tester Testador\",\r\n"
					+ "  \"email\": \"vitorotesterABCDEF@qa.com\",\r\n"
					+ "  \"password\": \"teste@123\",\r\n"
					+ "  \"administrador\": \"true\"\r\n"
					+ "}")
		.when()
			.post("/usuarios")
		.then()
			.statusCode(400)
			.body("message", is("Este email já está sendo usado"));
			
	}
	
	/**
	 * Edit user account
	 */
	@Test
	public void editUserAccount() {
		// Buscando um usuário na lista
		String _id = 
		given()
		.when()
			.get("/usuarios")
		.then()
			.statusCode(200)
			.extract().path("usuarios._id[1]");
				
		// Alterando os dados do usuário
		given()
			.header("authorization", "Bearer " + TOKEN)
			.body("{\r\n"
					+ "  \"nome\": \"Fulano do Teste123456\",\r\n"
					+ "  \"email\": \"fulanotesterQA12345@qa.com\",\r\n"
					+ "  \"password\": \"teste\",\r\n"
					+ "  \"administrador\": \"true\"\r\n"
					+ "}")
		.when()
			.put("/usuarios/" + _id)
		.then()
			.statusCode(200)
			.body("message", is("Registro alterado com sucesso"));
		
		// Validando se a alteração foi realizada com sucesso e que o mesmo encontra-se na lista
		given()
		.when()
			.get("/usuarios?_id=" + _id)
		.then()
			.statusCode(200)
			.body("usuarios.nome", hasItem("Fulano do Teste123456"));
			
	}
	
}
