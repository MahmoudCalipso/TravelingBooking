package com.travelingbooking.event;

import com.travelingbooking.domain.ApprovalStatus;
import com.travelingbooking.domain.event.Event;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class EventRepository implements PanacheRepository<Event> {

    public List<Event> findByOrganizer(UUID organizerId) {
        return list("organizer.id = ?1 and deleted = false", organizerId);
    }

    public List<Event> findPending() {
        return list("approvalStatus = ?1 and deleted = false", ApprovalStatus.PENDING);
    }

    public List<Event> findPublicActive(LocalDate from, LocalDate to) {
        String jpql = "from Event where deleted = false and approvalStatus = ?1";
        if (from != null) {
            jpql += " and startDate >= ?2";
        }
        if (to != null) {
            jpql += " and endDate <= ?3";
        }
        var query = getEntityManager().createQuery(jpql, Event.class)
                .setParameter(1, ApprovalStatus.APPROVED);
        if (from != null) query.setParameter(2, from);
        if (to != null) query.setParameter(3, to);
        return query.getResultList();
    }
}

