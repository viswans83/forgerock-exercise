package com.forgerock.autoid.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forgerock.autoid.model.FeatureConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransformerServiceTest {

    @Autowired
    private TransformerService transformerService;

    private ObjectMapper om = new ObjectMapper();

    @Test public void testAbleToParseFeatureConfig() throws IOException {
        var featureConfig = om.readValue(new ClassPathResource("feature-config.json").getURL(), FeatureConfig.class);
        assertEquals("DeviceFeatures", featureConfig.getName());
        assertEquals(2, featureConfig.getTransforms().size());
        assertEquals(".device.osType", featureConfig.getTransforms().get(0).getJsltExpression());
    }

    @Test public void testFeatureConfigTransformsAsExpected() throws IOException {
        var featureConfig = om.readValue(new ClassPathResource("feature-config.json").getURL(), FeatureConfig.class);
        var input = om.readTree(new ClassPathResource("sample-input.json").getURL());

        var output = transformerService.transform(featureConfig, input);
        var expectedOutput = om.readTree(new ClassPathResource("sample-output.json").getURL());

        assertEquals(expectedOutput, output);
    }

    @Test public void testDisabledTransformsInFeatureConfigAreHonored() throws IOException {
        var featureConfig = om.readValue(new ClassPathResource("feature-config.json").getURL(), FeatureConfig.class);
        var input = om.readValue(new ClassPathResource("sample-input.json").getURL(), JsonNode.class);

        // modify the featureConfig to disable the "device_os" transformation
        featureConfig.getTransforms().get(0).toggleEnabled();

        var output = transformerService.transform(featureConfig, input);

        assertTrue(output.has("device_description"));
        assertFalse(output.has("device_os"));
    }
}