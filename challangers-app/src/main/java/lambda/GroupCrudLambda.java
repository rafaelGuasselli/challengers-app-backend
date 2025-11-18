package lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import db.GroupRepository;
import model.Group;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.HttpURLConnection;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


public class GroupCrudLambda implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
			.findAndRegisterModules()
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

	private static final Map<String, String> DEFAULT_HEADERS = Map.of(
			"Content-Type", "application/json",
			"Access-Control-Allow-Origin", "*"
	);
	private static final String GROUPS_TABLE_ENV = "GROUPS_TABLE";

	private final GroupRepository groupRepository;

	public GroupCrudLambda() {
		this(DynamoDbClient.create(), System.getenv(GROUPS_TABLE_ENV));
	}

	GroupCrudLambda(DynamoDbClient dynamoDbClient, String tableName) {
		if (tableName == null || tableName.isBlank()) {
			throw new IllegalStateException("Environment variable " + GROUPS_TABLE_ENV + " is required");
		}
		this.groupRepository = new GroupRepository(Objects.requireNonNull(dynamoDbClient), tableName);
	}

	@Override
	public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
		try {
			String httpMethod = Optional.ofNullable(event)
					.map(APIGatewayV2HTTPEvent::getRequestContext)
					.map(APIGatewayV2HTTPEvent.RequestContext::getHttp)
					.map(APIGatewayV2HTTPEvent.RequestContext.Http::getMethod)
					.orElse("");

			return switch (httpMethod) {
				case "GET" -> handleGet(event);
				case "POST" -> handlePost(event);
				case "PUT" -> handlePut(event);
				case "DELETE" -> handleDelete(event);
				default -> buildResponse(HttpURLConnection.HTTP_BAD_REQUEST,
						new ErrorResponse("Unsupported method: " + httpMethod));
			};
		} catch (IllegalArgumentException badInput) {
			return buildResponse(HttpURLConnection.HTTP_BAD_REQUEST, new ErrorResponse(badInput.getMessage()));
		} catch (Exception e) {
			if (context != null && context.getLogger() != null) {
				context.getLogger().log("Error handling request: " + e.getMessage());
			}
			return buildResponse(HttpURLConnection.HTTP_INTERNAL_ERROR, new ErrorResponse("Internal server error"));
		}
	}

	private APIGatewayV2HTTPResponse handleGet(APIGatewayV2HTTPEvent event) {
		String groupId = getPathParameter(event, "id");
		if (groupId != null) {
			return groupRepository.getById(groupId)
					.map(group -> buildResponse(HttpURLConnection.HTTP_OK, group))
					.orElseGet(() -> buildResponse(HttpURLConnection.HTTP_NOT_FOUND,
							new ErrorResponse("Group not found: " + groupId)));
		}

		Map<String, String> queryParams = event != null ? event.getQueryStringParameters() : null;
		String adminId = queryParams != null ? queryParams.get("adminId") : null;
		if (adminId != null && !adminId.isBlank()) {
			List<Group> groups = groupRepository.listByAdmin(adminId);
			return buildResponse(HttpURLConnection.HTTP_OK, groups);
		}
		String userId = queryParams != null ? queryParams.get("userId") : null;
		if (userId != null && !userId.isBlank()) {
			List<Group> groups = groupRepository.listByUser(userId);
			return buildResponse(HttpURLConnection.HTTP_OK, groups);
		}

		return buildResponse(HttpURLConnection.HTTP_OK, groupRepository.listAll());
	}

	private APIGatewayV2HTTPResponse handlePost(APIGatewayV2HTTPEvent event) {
		Group payload = readBody(event);
		if (payload.getId() == null || payload.getId().isBlank()) {
			payload.setId(UUID.randomUUID().toString());
		}
		validateGroup(payload);
		groupRepository.save(payload);
		return buildResponse(HttpURLConnection.HTTP_CREATED, payload);
	}

	private APIGatewayV2HTTPResponse handlePut(APIGatewayV2HTTPEvent event) {
		String groupId = getPathParameter(event, "id");
		if (groupId == null || groupId.isBlank()) {
			throw new IllegalArgumentException("Group id path parameter is required for update");
		}
		Group payload = readBody(event);
		if (payload.getId() == null || payload.getId().isBlank()) {
			payload.setId(groupId);
		}
		if (!groupId.equals(payload.getId())) {
			throw new IllegalArgumentException("Group id in path and payload must match");
		}
		validateGroup(payload);
		groupRepository.save(payload);
		return buildResponse(HttpURLConnection.HTTP_OK, payload);
	}

	private APIGatewayV2HTTPResponse handleDelete(APIGatewayV2HTTPEvent event) {
		String groupId = getPathParameter(event, "id");
		if (groupId == null || groupId.isBlank()) {
			throw new IllegalArgumentException("Group id path parameter is required for delete");
		}
		groupRepository.delete(groupId);
		return APIGatewayV2HTTPResponse.builder()
				.withStatusCode(HttpURLConnection.HTTP_NO_CONTENT)
				.withHeaders(DEFAULT_HEADERS)
				.build();
	}

	private Group readBody(APIGatewayV2HTTPEvent event) {
		try {
			String body = event != null ? event.getBody() : null;
			if (body == null || body.isBlank()) {
				throw new IllegalArgumentException("Request body is required");
			}
			return OBJECT_MAPPER.readValue(body, Group.class);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Invalid JSON payload", e);
		}
	}

	private String getPathParameter(APIGatewayV2HTTPEvent event, String key) {
		if (event == null || event.getPathParameters() == null) {
			return null;
		}
		return event.getPathParameters().get(key);
	}

	private void validateGroup(Group group) {
		if (group.getAdminId() == null || group.getAdminId().isBlank()) {
			throw new IllegalArgumentException("adminId is required");
		}
		if (group.getName() == null || group.getName().isBlank()) {
			throw new IllegalArgumentException("name is required");
		}
	}

	private APIGatewayV2HTTPResponse buildResponse(int statusCode, Object body) {
		String serializedBody = "";
		if (body != null && statusCode != HttpURLConnection.HTTP_NO_CONTENT) {
			try {
				serializedBody = OBJECT_MAPPER.writeValueAsString(body);
			} catch (JsonProcessingException e) {
				throw new IllegalStateException("Failed to serialize response body", e);
			}
		}
		return APIGatewayV2HTTPResponse.builder()
				.withStatusCode(statusCode)
				.withHeaders(DEFAULT_HEADERS)
				.withBody(serializedBody)
				.build();
	}

	private record ErrorResponse(String message, Instant timestamp) {
		ErrorResponse(String message) {
			this(message, Instant.now());
		}
	}
}
