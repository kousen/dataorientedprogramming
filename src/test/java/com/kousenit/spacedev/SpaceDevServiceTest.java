package com.kousenit.spacedev;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static com.kousenit.spacedev.SpaceDevRecords.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class SpaceDevServiceTest {

    private final SpaceDevService service = new SpaceDevService();

    @Test
    void fetch_returns_success_with_active_expeditions() {
        Result result = service.fetchExpeditions();

        assertInstanceOf(Result.Success.class, result);

        // Deconstruct the Success to get expeditions
        if (result instanceof Result.Success(var expeditions)) {
            assertThat(expeditions).isNotEmpty();
            assertThat(expeditions).allSatisfy(expedition -> {
                assertThat(expedition.spacestation()).isNotNull();
                assertThat(expedition.spacestation().name()).isNotBlank();
                assertThat(expedition.crew()).isNotEmpty();
            });
        }
    }

    @Test
    void astronaut_assignments_have_all_fields() {
        var result = service.fetchExpeditions();
        if (result instanceof Result.Success(var expeditions)) {
            List<AstronautAssignment> assignments =
                    ExpeditionOperations.toAssignments(expeditions);

            assertThat(assignments).isNotEmpty();
            assertThat(assignments).allSatisfy(a -> assertAll(
                    () -> assertThat(a.astronautName()).isNotBlank(),
                    () -> assertThat(a.role()).isNotBlank(),
                    () -> assertThat(a.agency()).isNotBlank(),
                    () -> assertThat(a.stationName()).isNotBlank()
            ));

            // Print for visual inspection during demos
            assignments.forEach(a ->
                    System.out.printf("%s (%s, %s) aboard %s%n",
                            a.astronautName(), a.role(), a.agency(), a.stationName()));
        }
    }

    @Test
    void crew_by_station_groups_correctly() {
        var result = service.fetchExpeditions();
        if (result instanceof Result.Success(var expeditions)) {
            Map<String, List<String>> crewByStation =
                    ExpeditionOperations.crewByStation(expeditions);

            assertThat(crewByStation).isNotEmpty();
            crewByStation.forEach((station, crew) -> {
                assertThat(station).isNotBlank();
                assertThat(crew).isNotEmpty();
                System.out.printf("%s: %s%n", station, crew);
            });
        }
    }

    @Test
    void crew_by_agency_groups_correctly() {
        var result = service.fetchExpeditions();
        if (result instanceof Result.Success(var expeditions)) {
            Map<String, List<String>> crewByAgency =
                    ExpeditionOperations.crewByAgency(expeditions);

            assertThat(crewByAgency).isNotEmpty();
            crewByAgency.forEach((agency, crew) -> {
                assertThat(agency).isNotBlank();
                assertThat(crew).isNotEmpty();
                System.out.printf("%s: %s%n", agency, crew);
            });
        }
    }

    @Test
    void describe_result_formats_all_cases() {
        // Test with real data
        var result = service.fetchExpeditions();
        String description = ExpeditionOperations.describeResult(result);
        assertThat(description).isNotBlank();
        System.out.println(description);

        // Test error case formatting
        assertAll(
                () -> assertThat(ExpeditionOperations.describeResult(
                        new Result.NetworkError("connection refused")))
                        .contains("Network error", "connection refused"),
                () -> assertThat(ExpeditionOperations.describeResult(
                        new Result.ClientError(404, "not found")))
                        .contains("404", "not found"),
                () -> assertThat(ExpeditionOperations.describeResult(
                        new Result.ServerError(503)))
                        .contains("503"),
                () -> assertThat(ExpeditionOperations.describeResult(
                        new Result.RateLimited("60")))
                        .contains("Rate limited", "60")
        );
    }
}
