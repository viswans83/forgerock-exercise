package com.forgerock.autoid.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.forgerock.autoid.model.FeatureConfig;
import org.springframework.stereotype.Service;

@Service
public class TransformerService {

    public JsonNode transform(FeatureConfig featureConfig, JsonNode inputJson) {
        var jsltExpression = featureConfig.buildJSLTExpression();
        return jsltExpression.apply(inputJson);
    }

}
