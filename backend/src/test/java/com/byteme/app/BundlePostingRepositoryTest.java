package com.byteme.app;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class BundlePostingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BundlePostingRepository bundleRepo;

    private Seller sharedSeller;

    @BeforeEach
    void setUp() {
        UserAccount user = new UserAccount();
        user.setEmail("test-" + UUID.randomUUID() + "@byteme.com");
        user.setPasswordHash("hash");
        user.setRole(UserAccount.Role.SELLER);
        entityManager.persist(user);

        sharedSeller = new Seller();
        sharedSeller.setName("Test Seller");
        sharedSeller.setUser(user);
        entityManager.persist(sharedSeller);

        entityManager.flush();
    }

    @Test
    void testFindBySeller_SellerId() {
        BundlePosting b1 = new BundlePosting();
        b1.setSeller(sharedSeller);
        b1.setTitle("Item 1");
        b1.setPriceCents(100);
        b1.setPickupStartAt(Instant.now());
        b1.setPickupEndAt(Instant.now().plus(1, ChronoUnit.HOURS));
        entityManager.persist(b1);

        entityManager.flush();

        List<List<BundlePosting>> results = List.of(bundleRepo.findBySeller_SellerId(sharedSeller.getSellerId()));
        assertEquals(1, results.get(0).size());
        assertEquals("Item 1", results.get(0).get(0).getTitle());
    }

    @Test
    void testFindAvailable_FiltersCorrectly() {
        Instant now = Instant.now();

        BundlePosting available = new BundlePosting();
        available.setSeller(sharedSeller);
        available.setTitle("Available");
        available.setStatus(BundlePosting.Status.ACTIVE);
        available.setQuantityTotal(5);
        available.setQuantityReserved(0);
        available.setPickupStartAt(now.minus(1, ChronoUnit.HOURS));
        available.setPickupEndAt(now.plus(1, ChronoUnit.HOURS));
        available.setPriceCents(100);
        entityManager.persist(available);

        BundlePosting soldOut = new BundlePosting();
        soldOut.setSeller(sharedSeller);
        soldOut.setTitle("Sold Out");
        soldOut.setStatus(BundlePosting.Status.ACTIVE);
        soldOut.setQuantityTotal(5);
        soldOut.setQuantityReserved(5);
        soldOut.setPickupStartAt(now.minus(1, ChronoUnit.HOURS));
        soldOut.setPickupEndAt(now.plus(1, ChronoUnit.HOURS));
        soldOut.setPriceCents(100);
        entityManager.persist(soldOut);

        BundlePosting draft = new BundlePosting();
        draft.setSeller(sharedSeller);
        draft.setTitle("Draft");
        draft.setStatus(BundlePosting.Status.DRAFT);
        draft.setQuantityTotal(5);
        draft.setQuantityReserved(0);
        draft.setPickupStartAt(now.minus(1, ChronoUnit.HOURS));
        draft.setPickupEndAt(now.plus(1, ChronoUnit.HOURS));
        draft.setPriceCents(100);
        entityManager.persist(draft);

        BundlePosting past = new BundlePosting();
        past.setSeller(sharedSeller);
        past.setTitle("Past");
        past.setStatus(BundlePosting.Status.ACTIVE);
        past.setQuantityTotal(5);
        past.setQuantityReserved(0);
        past.setPickupStartAt(now.minus(5, ChronoUnit.HOURS));
        past.setPickupEndAt(now.minus(1, ChronoUnit.HOURS));
        past.setPriceCents(100);
        entityManager.persist(past);

        entityManager.flush();

        Page<BundlePosting> page = bundleRepo.findAvailable(now, PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
        assertEquals("Available", page.getContent().get(0).getTitle());
    }

    @Test
    void testFindExpired() {
        Instant now = Instant.now();

        BundlePosting expired = new BundlePosting();
        expired.setSeller(sharedSeller);
        expired.setTitle("Expired");
        expired.setStatus(BundlePosting.Status.ACTIVE);
        expired.setPickupStartAt(now.minus(10, ChronoUnit.HOURS));
        expired.setPickupEndAt(now.minus(2, ChronoUnit.HOURS)); // Is in the past
        expired.setPriceCents(100);
        entityManager.persist(expired);

        BundlePosting notExpired = new BundlePosting();
        notExpired.setSeller(sharedSeller);
        notExpired.setTitle("Not Expired");
        notExpired.setStatus(BundlePosting.Status.ACTIVE);
        notExpired.setPickupStartAt(now.minus(1, ChronoUnit.HOURS));
        notExpired.setPickupEndAt(now.plus(1, ChronoUnit.HOURS)); // Is in the future
        notExpired.setPriceCents(100);
        entityManager.persist(notExpired);

        entityManager.flush();

        List<BundlePosting> expiredList = bundleRepo.findExpired(now);
        assertEquals(1, expiredList.size());
        assertEquals("Expired", expiredList.get(0).getTitle());
    }

    @Test
    void testCountBySeller() {
        BundlePosting b1 = new BundlePosting();
        b1.setSeller(sharedSeller);
        b1.setTitle("B1");
        b1.setPriceCents(100);
        b1.setPickupStartAt(Instant.now());
        b1.setPickupEndAt(Instant.now().plus(1, ChronoUnit.HOURS));
        entityManager.persist(b1);

        entityManager.flush();

        long count = bundleRepo.countBySeller(sharedSeller.getSellerId());
        assertEquals(1, count);

        long countWrongId = bundleRepo.countBySeller(UUID.randomUUID());
        assertEquals(0, countWrongId);
    }
}