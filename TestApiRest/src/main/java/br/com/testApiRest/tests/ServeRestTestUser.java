package br.com.testApiRest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Locale;

import org.junit.Test;

import com.github.javafaker.Faker;

import br.com.testApiRest.core.BaseTest;

public class ServeRestTestUser extends BaseTest {
	ServeRestTestLogin login = new ServeRestTestLogin();
	
	Faker faker = new Faker(Locale.ENGLISH);
	private String _id;
	
	public String registerUsersDefault() {
		
		// Inserindo um usuário e salvando o _id em uma string
		_id = given()
			.header("authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Vitor Oliveira\",\r\n"
					+ "  \"email\": \""+faker.name().username()+"@qa.com\",\r\n"
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
			.body("usuarios.nome[0]", containsString("Vitor Oliveira"));
		return _id;
	}
	
	/**
	 * Registering user and validating inclusion
	 */
	@Test
	public void registerUsers() {
		
		// Inserindo um usuário e salvando o _id em uma string
		String _id = given()
			.header("authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Vitor Tester Testador\",\r\n"
					+ "  \"email\": \""+faker.name().username()+"@qa.com\",\r\n"
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
		
		// Deletando o usuário
		given()
			.header("authorization", login.login())
		.when()
			.delete("/usuarios/" + _id)
		.then()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso"));		
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
			.header("authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Vitor Tester Testador\",\r\n"
					+ "  \"email\": \"fulano@qa.com\",\r\n"
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
		
		// Inserindo um usuário e salvando o _id em uma string
		String _id = given()
			.header("authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Vitor\",\r\n"
					+ "  \"email\": \""+faker.name().username()+"@qa.com\",\r\n"
					+ "  \"password\": \"teste@123\",\r\n"
					+ "  \"administrador\": \"true\"\r\n"
					+ "}")
		.when()
			.post("/usuarios")
		.then()
			.statusCode(201)
			.body("message", is("Cadastro realizado com sucesso"))
			.extract().path("_id");
		
		// Alterando os dados do usuário
		given()
			.header("authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Fulano Testador\",\r\n"
					+ "  \"email\": \"fulanotestador20@qa.com\",\r\n"
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
			.body("usuarios.nome", hasItem("Fulano Testador"));
		
		// Deletando o usuário
		given()
			.header("authorization", login.login())
		.when()
			.delete("/usuarios/" + _id)
		.then()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso"));
	}
	
	/**
	 * Validate user edit block with same email
	 */
	@Test
	public void validateUserEditBlockWithSameEmail() {
		String teste = this.registerUsersDefault();
				
		// Alterando os dados do usuário com um email que já está sendo utilizado
		given()
			.header("authorization", login.login())
			.body("{\r\n"
					+ "  \"nome\": \"Fulano Testador\",\r\n"
					+ "  \"email\": \"fulano@qa.com\",\r\n"
					+ "  \"password\": \"teste\",\r\n"
					+ "  \"administrador\": \"true\"\r\n"
					+ "}")
		.when()
			.put("/usuarios/" + teste)
		.then()
			.statusCode(400)
			.body("message", is("Este email já está sendo usado"));		
	}
	
	/**
	 * Delete user account
	 */
	@Test
	public void deleteUserAccount() {
		String teste = this.registerUsersDefault();
		
		// Deletando o usuário
		given()
			.header("authorization", login.login())
		.when()
			.delete("/usuarios/" + teste)
		.then()
			.statusCode(200)
			.body("message", is("Registro excluído com sucesso"));
		
		// Validando se a alteração foi realizada com sucesso e que o mesmo não encontra-se na lista		
		given()
		.when()
			.get("/usuarios?_id=" + teste)
		.then()
			.statusCode(200)
			.body("usuarios", empty());			
	}	
}
