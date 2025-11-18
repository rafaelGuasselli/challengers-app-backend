package builder;

import model.User;
import parsers.AbstractAuthParser;

import java.util.ArrayList;
import java.util.Map;

public final class CurrentUserBuilder {
	private AbstractAuthParser.AuthContext authContext;

	private CurrentUserBuilder() {
	}

	public static CurrentUserBuilder newBuilder() {
		return new CurrentUserBuilder();
	}

	public static User buildFromAuthContext(AbstractAuthParser.AuthContext authContext) {
		return newBuilder()
				.withAuthContext(authContext)
				.build();
	}

	public CurrentUserBuilder withAuthContext(AbstractAuthParser.AuthContext authContext) {
		this.authContext = authContext;
		return this;
	}

	public User build() {
		if (authContext == null) {
			throw new IllegalStateException("Auth context is required to build the current user");
		}

		User user = new User();
		user.setId(authContext.getSub());
		user.setName(resolveName(authContext));
		user.setEmail(resolveClaim(authContext, "email"));
		user.setProfilePicture(resolveClaim(authContext, "picture"));
		user.setChallanges(new ArrayList<>());
		user.setAmountOfBronzeMedals(0);
		user.setAmountOfSilverMedals(0);
		user.setAmountOfGoldMedals(0);
		return user;
	}

	private static String resolveName(AbstractAuthParser.AuthContext context) {
		String name = resolveClaim(context, "name");
		if (isPresent(name)) {
			return name;
		}
		String username = context.getUsername();
		if (isPresent(username)) {
			return username;
		}
		String cognitoUsername = resolveClaim(context, "cognito:username");
		if (isPresent(cognitoUsername)) {
			return cognitoUsername;
		}
		return context.getSub();
	}

	private static String resolveClaim(AbstractAuthParser.AuthContext context, String key) {
		if (context == null) {
			return null;
		}
		Map<String, String> claims = context.getClaims();
		if (claims == null) {
			return null;
		}
		return claims.get(key);
	}

	private static boolean isPresent(String value) {
		return value != null && !value.isBlank();
	}
}
