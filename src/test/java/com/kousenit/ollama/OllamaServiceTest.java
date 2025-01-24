package com.kousenit.ollama;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.kousenit.ollama.OllamaRecords.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OllamaServiceTest {
    private final OllamaService service = new OllamaService();

    @Test
    void generate_with_text_request() {
        var ollamaRequest = new OllamaTextRequest(
                "phi4",
                "Write a haiku about Java development", false);
        OllamaResponse ollamaResponse = service.generate(ollamaRequest);
        System.out.println(ollamaResponse);
        String answer = ollamaResponse.response();
        System.out.println(answer);
    }

    @Test
    void generate_with_vision_request() {
        var request = new OllamaVisionRequest("llama3.2-vision",
                """
                What company is building the robot shown in the image?
                Can you tell me anything about it?
                """,
                false,
                List.of("src/main/resources/skynet.jpg"));
        OllamaResponse response = service.generateVision(request);
        assertNotNull(response);
        System.out.println(response);
    }

    @Test
    void generate_with_model_and_name() {
        var ollamaResponse = service.generate(
                "phi4", "Why is the sky blue?");
        String answer = ollamaResponse.response();
        System.out.println(answer);
        assertTrue(answer.contains("scattering"));
    }

    @Test
    public void streaming_request() {
        var request = new OllamaTextRequest(
                "phi4", "Why is the sky blue?", true);
        String response = service.generateStreaming(request);
        System.out.println(response);
    }

    @Test
    void test_vision_request() {
        var request = new OllamaVisionRequest("llava-llama3",
                """
                My company (logo in the image) is building
                the robot shown and wants me to embed an
                AI model into it. What's the worst that
                could happen?
                """,
                false,
                List.of("src/main/resources/skynet.jpg"));
        OllamaResponse response = service.generateVision(request);
        assertNotNull(response);
        System.out.println(response);
    }

    @Test
    void test_conversation() {
        var request = new OllamaChatRequest("gemma",
                List.of(new OllamaMessage("user", "why is the sky blue?"),
                        new OllamaMessage("assistant", "due to rayleigh scattering."),
                        new OllamaMessage("user", "how is that different than mie scattering?")),
                false);
        OllamaChatResponse response = service.chat(request);
        assertNotNull(response);
        System.out.println(response);
    }
}
