package br.com.testApiRest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import br.com.testApiRest.core.BaseTest;

public class ServeRestTestLogin extends BaseTest {
	
	public String login() {
		
		// Login com usuário válido!
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "fulano@qa.com");
		login.put("password", "teste");
		
		String token = given()
			.body(login)
		.when()
			.post("/login")
		.then()
			.statusCode(200)
			.body("message", is("Login realizado com sucesso"))
			.extract().path("authorization");
		return token;
	}
	
	/**
	 * Trying to login incorrect credential
	 */
	@Test
	public void tryingToLoginIncorrectCredential() {
		Map<String, String> login = new HashMap<String, String>();
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
		Map<String, String> login = new HashMap<String, String>();
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
}
