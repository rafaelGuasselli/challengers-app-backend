package db;

import model.Challange;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ChallangeRepository extends BaseCrudRepository<Challange> {

	public ChallangeRepository(DynamoDbClient dynamoDbClient, String tableName) {
		super(dynamoDbClient, tableName, Challange.class);
	}

	@Override
	protected String extractId(Challange entity) {
		return Objects.requireNonNull(entity.getId(), "Challange id must not be null");
	}

	@Override
	protected Map<String, AttributeValue> buildAdditionalAttributes(Challange entity) {
		Map<String, AttributeValue> attributes = new HashMap<>();
		if (entity.getGroupId() != null) {
			attributes.put("groupId", AttributeValue.builder().s(entity.getGroupId()).build());
		}
		if (entity.getOwnerUserId() != null) {
			attributes.put("ownerUserId", AttributeValue.builder().s(entity.getOwnerUserId()).build());
		}
		return attributes;
	}

	public List<Challange> listByGroup(String groupId) {
		if (groupId == null || groupId.isBlank()) {
			return Collections.emptyList();
		}
		Map<String, AttributeValue> values = Collections.singletonMap(
				":groupId", AttributeValue.builder().s(groupId).build()
		);
		return scanWithFilter("groupId = :groupId", values);
	}

	public List<Challange> listByUser(String ownerUserId) {
		if (ownerUserId == null || ownerUserId.isBlank()) {
			return Collections.emptyList();
		}
		Map<String, AttributeValue> values = Collections.singletonMap(
				":ownerUserId", AttributeValue.builder().s(ownerUserId).build()
		);
		return scanWithFilter("ownerUserId = :ownerUserId", values);
	}
}
