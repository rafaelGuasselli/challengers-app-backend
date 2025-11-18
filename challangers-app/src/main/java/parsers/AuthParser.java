package parsers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public final class AuthParser {
	private static final String AWS_SAM_LOCAL_FLAG = "AWS_SAM_LOCAL";
	private static final String LOCAL_SUB_HEADER = "x-user-sub";
	private static final String LOCAL_USERNAME_HEADER = "x-user-username";

	private AuthParser() {
	}

	public static AuthContext parse(APIGatewayV2HTTPEvent event) {
		if (shouldUseAuthorizer()) {
			Map<String, String> claims = extractAuthorizerClaims(event);
			return new AuthContext(
					claims.get("sub"),
					claims.get("cognito:username"),
					Collections.unmodifiableMap(new HashMap<>(claims)),
					false
			);
		}

		Map<String, String> headers = normalizeHeaders(event != null ? event.getHeaders() : null);
		String sub = headers.get(LOCAL_SUB_HEADER);
		String username = headers.get(LOCAL_USERNAME_HEADER);

		Map<String, String> synthesizedClaims = new HashMap<>(2);
		if (sub != null) {
			synthesizedClaims.put("sub", sub);
		}
		if (username != null) {
			synthesizedClaims.put("cognito:username", username);
		}

		return new AuthContext(
				sub,
				username,
				Collections.unmodifiableMap(synthesizedClaims),
				true
		);
	}

	private static Map<String, String> extractAuthorizerClaims(APIGatewayV2HTTPEvent event) {
		if (event == null ||
				event.getRequestContext() == null ||
				event.getRequestContext().getAuthorizer() == null ||
				event.getRequestContext().getAuthorizer().getJwt() == null ||
				event.getRequestContext().getAuthorizer().getJwt().getClaims() == null) {
			return Collections.emptyMap();
		}
		return event.getRequestContext().getAuthorizer().getJwt().getClaims();
	}

	private static Map<String, String> normalizeHeaders(Map<String, String> headers) {
		if (headers == null || headers.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, String> normalized = new HashMap<>(headers.size());
		for (Map.Entry<String, String> entry : headers.entrySet()) {
			if (entry.getKey() == null) continue;
			normalized.put(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue());
		}
		return normalized;
	}

	private static boolean shouldUseAuthorizer() {
		return !isEnvFlagTrue(System.getenv(AWS_SAM_LOCAL_FLAG));
	}

	private static boolean isEnvFlagTrue(String value) {
		return value != null && "true".equalsIgnoreCase(value.trim());
	}

	public static final class AuthContext {
		private final String sub;
		private final String username;
		private final Map<String, String> claims;
		private final boolean synthesized;

		private AuthContext(String sub, String username, Map<String, String> claims, boolean synthesized) {
			this.sub = sub;
			this.username = username;
			this.claims = claims;
			this.synthesized = synthesized;
		}

		public String getSub() {
			return sub;
		}

		public String getUsername() {
			return username;
		}

		public Map<String, String> getClaims() {
			return claims;
		}

		public boolean isSynthesized() {
			return synthesized;
		}
	}
}
