package com.forgerock.autoid.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransformerServiceRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private ObjectMapper om = new ObjectMapper();

    @Test
    public void testTransformAPI() throws IOException {
        String path = "http://localhost:" + port + "/test-transform";

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        var multiPart = new LinkedMultiValueMap();
        multiPart.add("featureConfig", new ClassPathResource("feature-config.json"));
        multiPart.add("input", new ClassPathResource("sample-input.json"));

        var httpEntity = new HttpEntity<>(multiPart, headers);
        var responseJsonNode = restTemplate.postForEntity(path, httpEntity, JsonNode.class).getBody();

        var expectedJsonNode = om.readTree(new ClassPathResource("sample-output.json").getURL());

        assertEquals(expectedJsonNode, responseJsonNode);
    }
}