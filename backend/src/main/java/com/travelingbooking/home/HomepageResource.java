package com.travelingbooking.home;

import com.travelingbooking.accommodation.AccommodationRepository;
import com.travelingbooking.domain.accommodation.Accommodation;
import com.travelingbooking.domain.event.Event;
import com.travelingbooking.domain.program.TravelProgram;
import com.travelingbooking.event.EventRepository;
import com.travelingbooking.program.TravelProgramRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/api/home")
@Produces(MediaType.APPLICATION_JSON)
public class HomepageResource {

    @Inject
    AccommodationRepository accommodationRepository;

    @Inject
    EventRepository eventRepository;

    @Inject
    TravelProgramRepository travelProgramRepository;

    @GET
    public Response getHomepage() {
        LocalDate today = LocalDate.now();

        List<Accommodation> allApprovedAccommodations =
                accommodationRepository.searchPublic(null, null, null);

        List<Accommodation> premiumAccommodations = allApprovedAccommodations.stream()
                .filter(Accommodation::isPremiumVisible)
                .sorted(Comparator.comparing(a -> a.premiumUntil, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(20)
                .collect(Collectors.toList());

        List<Event> upcomingEvents = eventRepository.findPublicActive(today, today.plusMonths(3));
        List<TravelProgram> featuredPrograms = travelProgramRepository.findPublic(today, today.plusMonths(6));

        Map<String, Object> payload = Map.of(
                "premiumAccommodations", premiumAccommodations,
                "featuredEvents", upcomingEvents,
                "travelPrograms", featuredPrograms,
                "generatedAt", Instant.now()
        );

        return Response.ok(payload).build();
    }
}

