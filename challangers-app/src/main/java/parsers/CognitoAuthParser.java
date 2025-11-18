package parsers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Extracts authentication context from the request's Cognito JWT authorizer claims.
 */
public class CognitoAuthParser extends AbstractAuthParser {

	@Override
	public AuthContext parse(APIGatewayV2HTTPEvent event) {
		Map<String, String> claims = extractAuthorizerClaims(event);
		return buildContext(
				claims.get("sub"),
				claims.get("cognito:username"),
				claims,
				false
		);
	}

	private Map<String, String> extractAuthorizerClaims(APIGatewayV2HTTPEvent event) {
		if (event == null ||
				event.getRequestContext() == null ||
				event.getRequestContext().getAuthorizer() == null ||
				event.getRequestContext().getAuthorizer().getJwt() == null ||
				event.getRequestContext().getAuthorizer().getJwt().getClaims() == null) {
			return Collections.emptyMap();
		}
		return Collections.unmodifiableMap(new HashMap<>(event.getRequestContext().getAuthorizer().getJwt().getClaims()));
	}
}
