package org.yx.sumk.apollo.util;

import com.ctrip.framework.apollo.spring.property.PlaceholderHelper;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.Stack;

/**
 * @author : wjiajun
 * @description:
 */
public class PlaceholderUtil extends PlaceholderHelper {

    private static final String PLACEHOLDER_PREFIX = "${";
    private static final String VALUE_SEPARATOR = ":";
    private static final String PLACEHOLDER_SUFFIX = "}";

    /**
     * Extract keys from placeholder, e.g.
     * <ul>
     * <li>${some.key} => "some.key"</li>
     * <li>${some.key:${some.other.key:100}} => "some.key", "some.other.key"</li>
     * <li>${${some.key}} => "some.key"</li>
     * <li>${${some.key:other.key}} => "some.key"</li>
     * <li>${${some.key}:${another.key}} => "some.key", "another.key"</li>
     * <li>#{new java.text.SimpleDateFormat('${some.key}').parse('${another.key}')} => "some.key", "another.key"</li>
     * </ul>
     */
    @Override
    public Set<String> extractPlaceholderKeys(String propertyString) {
        Set<String> placeholderKeys = Sets.newHashSet();

        if (Strings.isNullOrEmpty(propertyString) || (!isNormalizedPlaceholder(propertyString) && !isExpressionWithPlaceholder(propertyString))) {
            return placeholderKeys;
        }

        Stack<String> stack = new Stack<>();
        stack.push(propertyString);

        while (!stack.isEmpty()) {
            String strVal = stack.pop();
            int startIndex = strVal.indexOf(PLACEHOLDER_PREFIX);
            if (startIndex == -1) {
                placeholderKeys.add(strVal);
                continue;
            }
            int endIndex = findPlaceholderEndIndex(strVal, startIndex);
            if (endIndex == -1) {
                // invalid placeholder?
                continue;
            }

            String placeholderCandidate = strVal.substring(startIndex + PLACEHOLDER_PREFIX.length(), endIndex);

            // ${some.key:other.key}
            if (placeholderCandidate.startsWith(PLACEHOLDER_PREFIX)) {
                stack.push(placeholderCandidate);
            } else {
                // some.key:${some.other.key:100}
                int separatorIndex = placeholderCandidate.indexOf(VALUE_SEPARATOR);

                if (separatorIndex == -1) {
                    stack.push(placeholderCandidate);
                } else {
                    stack.push(placeholderCandidate.substring(0, separatorIndex));
                    String defaultValuePart =
                            normalizeToPlaceholder(placeholderCandidate.substring(separatorIndex + VALUE_SEPARATOR.length()));
                    if (!Strings.isNullOrEmpty(defaultValuePart)) {
                        stack.push(defaultValuePart);
                    }
                }
            }

            // has remaining part, e.g. ${a}.${b}
            if (endIndex + PLACEHOLDER_SUFFIX.length() < strVal.length() - 1) {
                String remainingPart = normalizeToPlaceholder(strVal.substring(endIndex + PLACEHOLDER_SUFFIX.length()));
                if (!Strings.isNullOrEmpty(remainingPart)) {
                    stack.push(remainingPart);
                }
            }
        }

        return placeholderKeys;
    }

    private String normalizeToPlaceholder(String strVal) {
        int startIndex = strVal.indexOf("${");
        if (startIndex == -1) {
            return null;
        } else {
            int endIndex = strVal.lastIndexOf("}");
            return endIndex == -1 ? null : strVal.substring(startIndex, endIndex + "}".length());
        }
    }

    private boolean isNormalizedPlaceholder(String propertyString) {
        return propertyString.startsWith("${") && propertyString.contains("}");
    }

    private boolean isExpressionWithPlaceholder(String propertyString) {
        return propertyString.startsWith("#{") && propertyString.contains("}") && propertyString.contains("${") && propertyString.contains("}");
    }

    private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
        int index = startIndex + "${".length();
        int withinNestedPlaceholder = 0;

        while(index < buf.length()) {
            if (substringMatch(buf, index, "}")) {
                if (withinNestedPlaceholder <= 0) {
                    return index;
                }

                --withinNestedPlaceholder;
                index += "}".length();
            } else if (substringMatch(buf, index, "{")) {
                ++withinNestedPlaceholder;
                index += "{".length();
            } else {
                ++index;
            }
        }

        return -1;
    }

    public boolean substringMatch(CharSequence str, int index, CharSequence substring) {
        if (index + substring.length() > str.length()) {
            return false;
        }
        for (int i = 0; i < substring.length(); i++) {
            if (str.charAt(index + i) != substring.charAt(i)) {
                return false;
            }
        }
        return true;
    }
}
