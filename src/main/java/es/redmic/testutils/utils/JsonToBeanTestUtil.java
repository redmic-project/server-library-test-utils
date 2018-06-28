package es.redmic.testutils.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bedatadriven.jackson.datatype.jts.JtsModule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonToBeanTestUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(JsonToBeanTestUtil.class);

	private static ObjectMapper jacksonMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JtsModule());

	private JsonToBeanTestUtil() {

	}

	public static Object getBean(String filePath, JavaType type) throws IOException {

		return jacksonMapper.readValue(getResource(filePath), type);
	}

	public static Object getBean(String filePath, Class<?> clazz) throws IOException {

		String json = getResource(filePath);

		return jacksonMapper.readValue(json, clazz);
	}

	public static <T> T getBean(String filePath, final TypeReference<T> type) throws IOException {

		String json = getResource(filePath);

		return jacksonMapper.readValue(json, type);
	}

	public static String getJsonString(String filePath) throws IOException {

		return getResource(filePath);
	}

	public static String writeValueAsString(Object obj) throws JsonProcessingException {
		return jacksonMapper.writeValueAsString(obj);
	}

	public static JavaType getParametizedType(Class<?> parametizedClass, Class<?> parameterClass) {
		return jacksonMapper.getTypeFactory().constructParametricType(parametizedClass, parameterClass);
	}

	private static String getResource(String filePath) throws IOException {
		InputStream resource = null;

		try {
			resource = JsonToBeanTestUtil.class.getResource(filePath).openStream();

		} catch (IOException e) {
			LOGGER.error("No existe el recurso " + filePath);
			e.printStackTrace();
		}

		StringBuilder textBuilder = new StringBuilder();
		try (Reader reader = new BufferedReader(
				new InputStreamReader(resource, Charset.forName(StandardCharsets.UTF_8.name())))) {
			int c = 0;
			while ((c = reader.read()) != -1) {
				textBuilder.append((char) c);
			}
		}

		String json = textBuilder.toString();
		// LOGGER.debug("Recurso \"" + filePath + "\":\n" + json);

		resource.close();

		return json;

	}
}