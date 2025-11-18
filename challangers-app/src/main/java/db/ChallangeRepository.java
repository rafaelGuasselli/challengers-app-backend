package db;

import model.Challange;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
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
		return attributes;
	}

	public List<Challange> listByGroups(ArrayList<String> groupIds) {
		List<Challange> result = new ArrayList<Challange>();
		for (String id: groupIds) {
			List<Challange> temp = this.listByGroup(id);
			result.addAll(temp);
		}

		return result;
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
}
