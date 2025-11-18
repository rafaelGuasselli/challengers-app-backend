package db;

import model.Group;
import model.User;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupRepository extends BaseCrudRepository<Group> {

	public GroupRepository(DynamoDbClient dynamoDbClient, String tableName) {
		super(dynamoDbClient, tableName, Group.class);
	}

	@Override
	protected String extractId(Group entity) {
		return Objects.requireNonNull(entity.getId(), "Group id must not be null");
	}

	@Override
	protected Map<String, AttributeValue> buildAdditionalAttributes(Group entity) {
		Map<String, AttributeValue> attributes = new HashMap<>();
		if (entity.getAdminId() != null) {
			attributes.put("adminId", AttributeValue.builder().s(entity.getAdminId()).build());
		}
		if (entity.getName() != null) {
			attributes.put("name", AttributeValue.builder().s(entity.getName()).build());
		}
		return attributes;
	}

	public List<Group> listByAdmin(String adminId) {
		if (adminId == null || adminId.isBlank()) {
			return Collections.emptyList();
		}
		Map<String, AttributeValue> values = Collections.singletonMap(
				":adminId", AttributeValue.builder().s(adminId).build()
		);
		return scanWithFilter("adminId = :adminId", values);
	}

	public List<Group> listByUser(String userId) {
		if (userId == null || userId.isBlank()) {
			return Collections.emptyList();
		}
		List<Group> filtered = new ArrayList<>();
		for (Group group : listAll()) {
			if (group == null) {
				continue;
			}
			if (userId.equals(group.getAdminId()) || isParticipant(group, userId)) {
				filtered.add(group);
			}
		}
		return filtered;
	}

	private boolean isParticipant(Group group, String userId) {
		if (group.getParticipants() == null || group.getParticipants().isEmpty()) {
			return false;
		}
		for (User participant : group.getParticipants().keySet()) {
			if (participant != null && userId.equals(participant.getId())) {
				return true;
			}
		}
		return false;
	}
}
