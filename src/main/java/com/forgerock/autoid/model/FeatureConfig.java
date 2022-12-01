package com.forgerock.autoid.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.schibsted.spt.data.jslt.Expression;
import com.schibsted.spt.data.jslt.Parser;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
@Getter
public class FeatureConfig {

    @JsonIgnoreProperties(ignoreUnknown=true)
    @Getter
    public static class Transform {
        private String name;

        private boolean enabled;

        private String jsltExpression;

        public void toggleEnabled() {
            this.enabled = !this.enabled;
        }
    }
    private int id;

    private String name;

    private List<String> passThrough = Collections.emptyList();

    private List<Transform> transforms = Collections.emptyList();

    public Expression buildJSLTExpression() {
        var jsltObjectProperties = new ArrayList<String>();

        // build pass through properties
        for (var fieldName : this.passThrough) {
            var sb = new StringBuilder();
            sb.append('"').append(fieldName).append('"');
            sb.append(": ");
            sb.append('.').append(fieldName);
            jsltObjectProperties.add(sb.toString());
        }

        // build transformed properties
        for (var transform : this.transforms) {
            if (!transform.isEnabled()) {
                continue;
            }
            var sb = new StringBuilder();
            sb.append('"').append(transform.getName()).append('"');
            sb.append(": ");
            sb.append(transform.getJsltExpression());
            jsltObjectProperties.add(sb.toString());
        }

        // build object expression
        var oe = new StringBuilder();
        oe.append("{");
        oe.append(Strings.join(jsltObjectProperties, ','));
        oe.append("}");

        return Parser.compileString(oe.toString());
    }
}
