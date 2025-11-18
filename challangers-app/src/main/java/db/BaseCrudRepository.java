package db;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public abstract class BaseCrudRepository<T> {
	protected static final String ID_ATTRIBUTE = "id";
	protected static final String PAYLOAD_ATTRIBUTE = "payload";

	private final DynamoDbClient dynamoDbClient;
	private final String tableName;
	private final Class<T> modelClass;

	protected BaseCrudRepository(DynamoDbClient dynamoDbClient, String tableName, Class<T> modelClass) {
		this.dynamoDbClient = Objects.requireNonNull(dynamoDbClient, "dynamoDbClient must not be null");
		this.tableName = Objects.requireNonNull(tableName, "tableName must not be null");
		this.modelClass = Objects.requireNonNull(modelClass, "modelClass must not be null");
	}

	public void save(T entity) {
		Objects.requireNonNull(entity, "entity must not be null");
		Map<String, AttributeValue> item = new HashMap<>();
		item.put(ID_ATTRIBUTE, AttributeValue.builder().s(extractId(entity)).build());
		item.put(PAYLOAD_ATTRIBUTE, AttributeValue.builder().s(DynamoDbJsonMapper.toJson(entity)).build());
		Map<String, AttributeValue> additional = buildAdditionalAttributes(entity);
		if (additional != null && !additional.isEmpty()) {
			item.putAll(additional);
		}
		dynamoDbClient.putItem(PutItemRequest.builder()
				.tableName(tableName)
				.item(item)
				.build());
	}

	public Optional<T> getById(String id) {
		if (id == null || id.isBlank()) {
			return Optional.empty();
		}
		Map<String, AttributeValue> key = Collections.singletonMap(ID_ATTRIBUTE, AttributeValue.builder().s(id).build());
		GetItemResponse response = dynamoDbClient.getItem(GetItemRequest.builder()
				.tableName(tableName)
				.key(key)
				.build());
		if (!response.hasItem()) {
			return Optional.empty();
		}
		return Optional.ofNullable(deserialize(response.item()));
	}

	public void delete(String id) {
		if (id == null || id.isBlank()) {
			return;
		}
		Map<String, AttributeValue> key = Collections.singletonMap(ID_ATTRIBUTE, AttributeValue.builder().s(id).build());
		dynamoDbClient.deleteItem(DeleteItemRequest.builder()
				.tableName(tableName)
				.key(key)
				.build());
	}

	public List<T> listAll() {
		return scanAndMap(ScanRequest.builder().tableName(tableName).build());
	}

	protected List<T> scanWithFilter(String filterExpression, Map<String, AttributeValue> expressionAttributeValues) {
		ScanRequest.Builder builder = ScanRequest.builder().tableName(tableName);
		if (filterExpression != null && !filterExpression.isBlank()) {
			builder = builder.filterExpression(filterExpression);
		}
		if (expressionAttributeValues != null && !expressionAttributeValues.isEmpty()) {
			builder = builder.expressionAttributeValues(expressionAttributeValues);
		}
		return scanAndMap(builder.build());
	}

	protected DynamoDbClient getDynamoDbClient() {
		return dynamoDbClient;
	}

	protected String getTableName() {
		return tableName;
	}

	protected Map<String, AttributeValue> buildAdditionalAttributes(T entity) {
		return Collections.emptyMap();
	}

	protected abstract String extractId(T entity);

	private List<T> scanAndMap(ScanRequest request) {
		List<T> results = new ArrayList<>();
		Map<String, AttributeValue> lastEvaluatedKey = null;
		ScanRequest currentRequest = request;
		do {
			if (lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty()) {
				currentRequest = request.toBuilder()
						.exclusiveStartKey(lastEvaluatedKey)
						.build();
			}
			ScanResponse response = dynamoDbClient.scan(currentRequest);
			if (response.hasItems()) {
				for (Map<String, AttributeValue> item : response.items()) {
					T model = deserialize(item);
					if (model != null) {
						results.add(model);
					}
				}
			}
			lastEvaluatedKey = response.lastEvaluatedKey();
		} while (lastEvaluatedKey != null && !lastEvaluatedKey.isEmpty());
		return results;
	}

	private T deserialize(Map<String, AttributeValue> item) {
		AttributeValue payload = item.get(PAYLOAD_ATTRIBUTE);
		if (payload == null || payload.s() == null) {
			return null;
		}
		return DynamoDbJsonMapper.fromJson(payload.s(), modelClass);
	}
}
