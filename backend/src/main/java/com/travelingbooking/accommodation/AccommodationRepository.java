package com.travelingbooking.accommodation;

import com.travelingbooking.domain.ApprovalStatus;
import com.travelingbooking.domain.accommodation.Accommodation;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AccommodationRepository implements PanacheRepository<Accommodation> {

    public List<Accommodation> findBySupplier(UUID supplierId) {
        return list("supplier.id = ?1 and deleted = false", supplierId);
    }

    public List<Accommodation> findPending() {
        return list("approvalStatus = ?1 and deleted = false", ApprovalStatus.PENDING);
    }

    public List<Accommodation> searchPublic(String query, String city, String country) {
        Instant now = Instant.now();
        StringBuilder jpql = new StringBuilder("from Accommodation where deleted = false " +
                "and approvalStatus = ?1 " +
                "and (visibilityFrom is null or visibilityFrom <= ?2) " +
                "and (visibilityTo is null or visibilityTo >= ?2)");

        if (query != null && !query.isBlank()) {
            jpql.append(" and lower(title) like concat('%', ?3, '%')");
        }
        if (city != null && !city.isBlank()) {
            jpql.append(" and lower(city) = ?4");
        }
        if (country != null && !country.isBlank()) {
            jpql.append(" and lower(country) = ?5");
        }

        return getEntityManager().createQuery(jpql.toString(), Accommodation.class)
                .setParameter(1, ApprovalStatus.APPROVED)
                .setParameter(2, now)
                .setParameter(3, query == null ? null : query.toLowerCase())
                .setParameter(4, city == null ? null : city.toLowerCase())
                .setParameter(5, country == null ? null : country.toLowerCase())
                .getResultList();
    }
}

