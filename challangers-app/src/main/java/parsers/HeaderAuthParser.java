package parsers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Extracts authentication context from custom headers when running locally.
 */
public class HeaderAuthParser extends AbstractAuthParser {
	public static final String LOCAL_SUB_HEADER = "x-user-sub";
	public static final String LOCAL_USERNAME_HEADER = "x-user-username";

	@Override
	public AuthContext parse(APIGatewayV2HTTPEvent event) {
		Map<String, String> headers = normalizeFields(event != null ? event.getHeaders() : null);
		String sub = headers.get(LOCAL_SUB_HEADER);
		String username = headers.get(LOCAL_USERNAME_HEADER);

		Map<String, String> synthesizedClaims = new HashMap<>(2);
		if (sub != null) {
			synthesizedClaims.put("sub", sub);
		}
		if (username != null) {
			synthesizedClaims.put("cognito:username", username);
		}

		return buildContext(sub, username, synthesizedClaims, true);
	}
}
