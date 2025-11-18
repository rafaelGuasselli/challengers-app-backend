package parsers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;

/**
 * Facade that delegates to the appropriate auth parser based on the execution environment.
 */
public final class AuthParser {
	private static final String AWS_SAM_LOCAL_FLAG = "AWS_SAM_LOCAL";

	private static final AbstractAuthParser HEADER = new HeaderAuthParser();
	private static final AbstractAuthParser COGNITO = new CognitoAuthParser();

	private AuthParser() {
	}

	public static AbstractAuthParser.AuthContext parse(APIGatewayV2HTTPEvent event) {
		if (shouldUseAuthorizer()) {
			return COGNITO.parse(event);
		}
		return HEADER.parse(event);
	}

	public static boolean shouldUseAuthorizer() {
		return !isEnvFlagTrue(System.getenv(AWS_SAM_LOCAL_FLAG));
	}

	private static boolean isEnvFlagTrue(String value) {
		return value != null && "true".equalsIgnoreCase(value.trim());
	}
}
