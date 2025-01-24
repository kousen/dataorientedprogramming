package com.kousenit.astro;

import java.util.List;
import java.util.Map;

public class AstroRecords {
    // Record to represent an astronaut
    public record Astronaut(String name, String craft) { }

    // Record to represent the API response
    public record AstroResponse(List<Astronaut> people, int number, String message) { }

    // Sealed interface for processing results
    public sealed interface Result {
        record Success(Map<String, List<String>> astronautsByCraft) implements Result { }
        record Failure(String error) implements Result { }
    }
}
