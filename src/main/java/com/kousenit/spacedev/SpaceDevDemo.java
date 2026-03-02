package com.kousenit.spacedev;

import java.util.List;
import java.util.Map;

import static com.kousenit.spacedev.SpaceDevRecords.*;

/**
 * Demonstrates data-oriented programming with the Launch Library 2 API.
 * <p>
 * The pattern-matching switch on Result handles five distinct cases,
 * each carrying different data. The compiler enforces exhaustiveness.
 */
public class SpaceDevDemo {

    public static void main(String[] args) {
        var service = new SpaceDevService();
        Result result = service.fetchExpeditions();

        // Pattern matching on the sealed Result hierarchy — all five cases handled
        switch (result) {
            case Result.Success(var expeditions) -> {
                // Nested record deconstruction in action
                System.out.println(ExpeditionOperations.describeResult(result));

                // Multiple operations over the same fixed set of types
                System.out.println("\n--- Crew by Agency ---");
                Map<String, List<String>> byAgency =
                        ExpeditionOperations.crewByAgency(expeditions);
                byAgency.forEach((agency, names) ->
                        System.out.printf("%s: %s%n", agency, names));

                System.out.println("\n--- Crew Count by Station ---");
                Map<String, Long> counts =
                        ExpeditionOperations.crewCountByStation(expeditions);
                counts.forEach((station, count) ->
                        System.out.printf("%s: %d crew members%n", station, count));

                System.out.println("\n--- Commanders ---");
                List<AstronautAssignment> commanders =
                        ExpeditionOperations.filterByRole(expeditions, "Commander");
                commanders.forEach(c ->
                        System.out.printf("%s (%s) on %s%n",
                                c.astronautName(), c.agency(), c.stationName()));
            }
            case Result.NetworkError(var message) ->
                    System.err.println("Could not reach the API: " + message);
            case Result.ClientError(var code, var body) ->
                    System.err.printf("Client error %d: %s%n", code, body);
            case Result.ServerError(var code) ->
                    System.err.printf("Server error %d — try again later%n", code);
            case Result.RateLimited(var retryAfter) ->
                    System.err.println("Rate limited. Retry after: " + retryAfter);
        }
    }
}
