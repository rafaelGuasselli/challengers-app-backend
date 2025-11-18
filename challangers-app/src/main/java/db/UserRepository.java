package db;

import model.User;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Objects;

public class UserRepository extends BaseCrudRepository<User> {

	public UserRepository(DynamoDbClient dynamoDbClient, String tableName) {
		super(dynamoDbClient, tableName, User.class);
	}

	@Override
	protected String extractId(User entity) {
		return Objects.requireNonNull(entity.getId(), "User id must not be null");
	}
}
