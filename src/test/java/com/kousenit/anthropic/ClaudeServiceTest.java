package com.kousenit.anthropic;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

import static com.kousenit.anthropic.ClaudeRecords.*;
import static org.assertj.core.api.Assertions.assertThat;

class ClaudeServiceTest {
    private final ClaudeService claudeService = new ClaudeService();

    @Nested
    class CompositionTests {
        @Test
        void haikuTest_haiku() {
            String question = """
                    Write a haiku about Java development
                    with AI tools.
                    """;
            var response = claudeService.chat(
                    new ClaudeMessageRequest(
                            ClaudeService.CLAUDE_35_HAIKU,
                            "",
                            1024,
                            0.7,
                            List.of(new SimpleMessage("user", question))));
            System.out.println(response);
            assertThat(response.content().getFirst().text()).isNotBlank();
        }

        @Test
        void sonnetTest_sonnet() {
            String question = """
                    Write a sonnet about Java development
                    with AI tools.
                    """;
            var response = claudeService.chat(new ClaudeMessageRequest(
                    ClaudeService.CLAUDE_35_SONNET,
                    "",
                    1024,
                    0.7,
                    List.of(new SimpleMessage("user", question))));
            System.out.println(response);
            assertThat(response.content().getFirst().text()).isNotBlank();
        }

        @Test
        void opusTest_opus() {
            String question = """
                    Write an opus about Java development
                    with AI tools.
                    """;
            var response = claudeService.chat(new ClaudeMessageRequest(
                    ClaudeService.CLAUDE_3_OPUS,
                    "",
                    1024,
                    0.7,
                    List.of(new SimpleMessage("user", question))));
            System.out.println(response);
            assertThat(response.content().getFirst().text()).isNotBlank();
        }
    }


    @Test
    void mixedContentMessage() {
        String imageFileName = "cats_playing_cards.png";
        String encodedImage = encodeImage(
                "src/main/resources/%s".formatted(imageFileName));
        var request = new ClaudeMessageRequest(
                ClaudeService.CLAUDE_35_SONNET,
                "",
                1024,
                0.7,
                List.of(new MixedContent("user",
                        List.of(new ImageContent("image",
                                        new ImageContent.ImageSource(
                                                "base64", "image/png", encodedImage)),
                                new TextContent("text", "What is in this image?")))));
        System.out.println(claudeService.chat(request));
    }


    @Test
    void pirateCoverLetter_haiku() {
        String question = """
                Write a cover letter for a Java developer
                applying for an AI programming position,
                written in pirate speak.
                """;
        var response = claudeService.chat(new ClaudeMessageRequest(
                ClaudeService.CLAUDE_35_HAIKU,
                "",
                1024,
                0.3,
                List.of(new SimpleMessage("user", question))));
        System.out.println(response);
        assertThat(response.content().getFirst().text()).contains("Ahoy");
    }

    private static String encodeImage(String path) {
        try {
            byte[] imageBytes = Files.readAllBytes(Paths.get(path));
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
