package db;

import model.ChallangeValidationRequest;
import model.User;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChallangeValidationRequestRepository extends BaseCrudRepository<ChallangeValidationRequest> {

	public ChallangeValidationRequestRepository(DynamoDbClient dynamoDbClient, String tableName) {
		super(dynamoDbClient, tableName, ChallangeValidationRequest.class);
	}

	@Override
	protected String extractId(ChallangeValidationRequest entity) {
		return Objects.requireNonNull(entity.getId(), "ChallangeValidationRequest id must not be null");
	}

	@Override
	protected Map<String, AttributeValue> buildAdditionalAttributes(ChallangeValidationRequest entity) {
		Map<String, AttributeValue> attributes = new HashMap<>();
		if (entity.getChallangeId() != null) {
			attributes.put("challangeId", AttributeValue.builder().s(entity.getChallangeId()).build());
		}
		User user = entity.getUser();
		if (user != null && user.getId() != null) {
			attributes.put("userId", AttributeValue.builder().s(user.getId()).build());
		}
		return attributes;
	}

	public List<ChallangeValidationRequest> listByChallange(String challangeId) {
		if (challangeId == null || challangeId.isBlank()) {
			return Collections.emptyList();
		}
		Map<String, AttributeValue> values = Collections.singletonMap(
				":challangeId", AttributeValue.builder().s(challangeId).build()
		);
		return scanWithFilter("challangeId = :challangeId", values);
	}

	public List<ChallangeValidationRequest> listByUser(String userId) {
		if (userId == null || userId.isBlank()) {
			return Collections.emptyList();
		}
		Map<String, AttributeValue> values = Collections.singletonMap(
				":userId", AttributeValue.builder().s(userId).build()
		);
		return scanWithFilter("userId = :userId", values);
	}
}
