package br.com.testApiRest.core;

import io.restassured.http.ContentType;

public interface Constant {
	
	String APP_BASE_URL = "https://serverest.dev";
	Integer APP_PORT = 443; // Porta padrão para "https"
	String APP_BASE_PATH = "";

	ContentType APP_CONTENT_TYPE = ContentType.JSON;

	Long MAX_TIMEOUT = 8000L;
}
