package db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Utility class to convert models to/from JSON for storage in DynamoDB.
 */
public final class DynamoDbJsonMapper {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
			.findAndRegisterModules()
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

	private DynamoDbJsonMapper() {
	}

	public static String toJson(Object payload) {
		try {
			return OBJECT_MAPPER.writeValueAsString(payload);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Failed to serialize payload to JSON", e);
		}
	}

	public static <T> T fromJson(String json, Class<T> targetClass) {
		try {
			return OBJECT_MAPPER.readValue(json, targetClass);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Failed to deserialize JSON to " + targetClass.getSimpleName(), e);
		}
	}
}
