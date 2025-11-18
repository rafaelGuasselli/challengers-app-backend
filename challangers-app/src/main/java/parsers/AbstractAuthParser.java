package parsers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractAuthParser {

	public abstract AuthContext parse(APIGatewayV2HTTPEvent event);

	protected Map<String, String> normalizeFields(Map<String, String> fields) {
		if (fields == null || fields.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, String> normalized = new HashMap<>(fields.size());
		for (Map.Entry<String, String> entry : fields.entrySet()) {
			if (entry.getKey() == null) continue;
			normalized.put(entry.getKey().toLowerCase(Locale.ROOT), entry.getValue());
		}
		return normalized;
	}

	protected AuthContext buildContext(String sub, String username, Map<String, String> claims, boolean synthesized) {
		Map<String, String> safeClaims;
		if (claims == null || claims.isEmpty()) {
			safeClaims = Collections.emptyMap();
		} else {
			safeClaims = Collections.unmodifiableMap(new HashMap<>(claims));
		}
		return new AuthContext(sub, username, safeClaims, synthesized);
	}

	public static class AuthContext {
		private final String sub;
		private final String username;
		private final Map<String, String> claims;
		private final boolean synthesized;

		public AuthContext(String sub, String username, Map<String, String> claims, boolean synthesized) {
			this.sub = sub;
			this.username = username;
			this.claims = Objects.requireNonNull(claims);
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
