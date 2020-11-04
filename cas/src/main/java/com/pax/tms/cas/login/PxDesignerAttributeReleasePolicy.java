package com.pax.tms.cas.login;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hjson.JsonArray;
import org.jasig.cas.services.AbstractRegisteredServiceAttributeReleasePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PxDesignerAttributeReleasePolicy extends AbstractRegisteredServiceAttributeReleasePolicy {

	private static final long serialVersionUID = 4594057930004951843L;

	private static final Logger LOGGER = LoggerFactory.getLogger(PxDesignerAttributeReleasePolicy.class);

	private Map<String, String> allowedAttributes;

	/**
	 * Instantiates a new Return mapped attribute release policy.
	 */
	public PxDesignerAttributeReleasePolicy() {
		this(new TreeMap<String, String>());
	}

	/**
	 * Instantiates a new Return mapped attribute release policy.
	 *
	 * @param allowedAttributes
	 *            the allowed attributes
	 */
	public PxDesignerAttributeReleasePolicy(final Map<String, String> allowedAttributes) {
		this.allowedAttributes = allowedAttributes;
	}

	/**
	 * Sets the allowed attributes.
	 *
	 * @param allowed
	 *            the allowed attributes.
	 */
	public void setAllowedAttributes(final Map<String, String> allowed) {
		this.allowedAttributes = allowed;
		LOGGER.debug("Set allowed attributes {}", allowed);
	}

	/**
	 * Gets the allowed attributes.
	 *
	 * @return the allowed attributes
	 */
	public Map<String, String> getAllowedAttributes() {
		return new TreeMap<>(this.allowedAttributes);
	}

	@Override
	protected Map<String, Object> getAttributesInternal(final Map<String, Object> resolvedAttributes) {
		LOGGER.debug("Resolved attributes {}", resolvedAttributes);
		final Map<String, Object> attributesToRelease = new HashMap<>(resolvedAttributes.size());
		for (final Map.Entry<String, String> entry : this.allowedAttributes.entrySet()) {
			final String key = entry.getKey();
			final Object value = resolvedAttributes.get(key);

			final String mappedAttributeName = entry.getValue();
			LOGGER.debug("Found attribute [{}] in the list of allowed attributes, mapped to the name [{}]", key,
					mappedAttributeName);

			if ("rolelist".equalsIgnoreCase(mappedAttributeName)) {
				attributesToRelease.put(mappedAttributeName, resolvesRoles(value));
				continue;
			}

			if (value != null) {
				attributesToRelease.put(mappedAttributeName, value instanceof Date ? ((Date) value).getTime() : value);
			} else {
				attributesToRelease.put(entry.getValue(), "");
			}
		}
		attributesToRelease.put("isEnable", "Y");
		return attributesToRelease;
	}

	@SuppressWarnings("unchecked")
	private String resolvesRoles(Object value) {
		if (value instanceof Collection) {
			return resolvesRoles((Collection<String>) value);
		} else if (value != null) {
			String[] roles = value.toString().split(",");
			return resolvesRoles(Arrays.asList(roles));
		} else {
			return "[]";
		}
	}

	private String resolvesRoles(Collection<String> rolelist) {
		JsonArray resolvedRoles = new JsonArray();
		for (String roleName : rolelist) {
			String lowerCaseRoleName = roleName.toLowerCase();
			if (lowerCaseRoleName.contains("admin")) {
				resolvedRoles.add("PXDesigner_Administrator");
			} else if (lowerCaseRoleName.contains("develop")) {
				resolvedRoles.add("PXDesigner_Developer");
			} else if (lowerCaseRoleName.contains("deploy")) {
				resolvedRoles.add("PXDesigner_Deployer");
			} else if (lowerCaseRoleName.contains("sign")) {
				resolvedRoles.add("PXDesigner_Signer");
			} else {
				resolvedRoles.add("PXDesigner_" + roleName);
			}
		}
		return resolvedRoles.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		final PxDesignerAttributeReleasePolicy rhs = (PxDesignerAttributeReleasePolicy) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj)).append(this.allowedAttributes, rhs.allowedAttributes)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().appendSuper(super.hashCode()).append(allowedAttributes).toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).appendSuper(super.toString()).append("allowedAttributes", allowedAttributes)
				.toString();
	}

}
