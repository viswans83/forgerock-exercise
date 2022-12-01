package com.forgerock.autoid.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.forgerock.autoid.model.FeatureConfig;
import com.forgerock.autoid.service.TransformerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;

@RestController
public class TransformerServiceRestController {

    @Autowired
    private TransformerService transformerService;

    @PostMapping(
            path = "/test-transform",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public JsonNode testTransform(
            @RequestParam("featureConfig") MultipartFile featureConfigFile,
            @RequestParam("input") MultipartFile inputFile) throws IOException {
        // temporary files to store multipart uploads
        var tempFeatureConfigFilePath = Files.createTempFile("featureConfig", "json");
        var tempInputFilePath = Files.createTempFile("input", "json");

        try {
            featureConfigFile.transferTo(tempFeatureConfigFilePath);
            inputFile.transferTo(tempInputFilePath);

            ObjectMapper om = new ObjectMapper();
            var featureConfig = om.readValue(tempFeatureConfigFilePath.toFile(), FeatureConfig.class);
            var input = om.readTree(tempInputFilePath.toFile());

            return transformerService.transform(featureConfig, input);
        } finally {
            // clean up temporary files
            tempFeatureConfigFilePath.toFile().delete();
            tempInputFilePath.toFile().delete();
        }
    }

}
