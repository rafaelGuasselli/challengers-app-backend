package db;

import model.GroupJoinSolicitation;
import model.User;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupJoinSolicitationRepository extends BaseCrudRepository<GroupJoinSolicitation> {

	public GroupJoinSolicitationRepository(DynamoDbClient dynamoDbClient, String tableName) {
		super(dynamoDbClient, tableName, GroupJoinSolicitation.class);
	}

	@Override
	protected String extractId(GroupJoinSolicitation entity) {
		return Objects.requireNonNull(entity.getId(), "GroupJoinSolicitation id must not be null");
	}

	@Override
	protected Map<String, AttributeValue> buildAdditionalAttributes(GroupJoinSolicitation entity) {
		Map<String, AttributeValue> attributes = new HashMap<>();
		if (entity.getGroupId() != null) {
			attributes.put("groupId", AttributeValue.builder().s(entity.getGroupId()).build());
		}
		User user = entity.getUser();
		if (user != null && user.getId() != null) {
			attributes.put("userId", AttributeValue.builder().s(user.getId()).build());
		}
		return attributes;
	}

	public List<GroupJoinSolicitation> listByGroup(String groupId) {
		if (groupId == null || groupId.isBlank()) {
			return Collections.emptyList();
		}
		Map<String, AttributeValue> values = Collections.singletonMap(
				":groupId", AttributeValue.builder().s(groupId).build()
		);
		return scanWithFilter("groupId = :groupId", values);
	}

	public List<GroupJoinSolicitation> listByUser(String userId) {
		if (userId == null || userId.isBlank()) {
			return Collections.emptyList();
		}
		Map<String, AttributeValue> values = Collections.singletonMap(
				":userId", AttributeValue.builder().s(userId).build()
		);
		return scanWithFilter("userId = :userId", values);
	}
}
