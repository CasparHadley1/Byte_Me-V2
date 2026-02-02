package com.byteme.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrgOrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrgOrderRepository orderRepo;

    private Organisation sharedOrg;
    private BundlePosting sharedPosting;

    @BeforeEach
    void setUp() {
        UserAccount sellerUser = new UserAccount();
        sellerUser.setEmail("seller-" + UUID.randomUUID() + "@test.com");
        sellerUser.setPasswordHash("hash");
        sellerUser.setRole(UserAccount.Role.SELLER);
        entityManager.persist(sellerUser);

        Seller seller = new Seller();
        seller.setName("Test Vendor");
        seller.setUser(sellerUser);
        entityManager.persist(seller);

        sharedPosting = new BundlePosting();
        sharedPosting.setSeller(seller);
        sharedPosting.setTitle("Surplus Bread");
        sharedPosting.setPriceCents(300);
        sharedPosting.setPickupStartAt(Instant.now());
        sharedPosting.setPickupEndAt(Instant.now().plusSeconds(3600));
        sharedPosting.setStatus(BundlePosting.Status.ACTIVE);
        entityManager.persist(sharedPosting);

        UserAccount orgUser = new UserAccount();
        orgUser.setEmail("org-" + UUID.randomUUID() + "@test.com");
        orgUser.setPasswordHash("hash");
        orgUser.setRole(UserAccount.Role.ORG_ADMIN);
        entityManager.persist(orgUser);

        sharedOrg = new Organisation();
        sharedOrg.setName("Helping Hearts");
        sharedOrg.setUser(orgUser);
        entityManager.persist(sharedOrg);

        entityManager.flush();
    }

    @Test
    void testSaveAndFindOrder() {
        OrgOrder order = new OrgOrder();
        order.setOrganisation(sharedOrg);
        order.setPosting(sharedPosting);
        order.setQuantity(3);
        order.setTotalPriceCents(900);
        order.setStatus(OrgOrder.Status.RESERVED);

        OrgOrder saved = orderRepo.save(order);
        entityManager.flush();
        entityManager.clear();

        OrgOrder found = orderRepo.findById(saved.getOrderId()).orElse(null);

        assertNotNull(found);
        assertEquals(OrgOrder.Status.RESERVED, found.getStatus());
        assertEquals(3, found.getQuantity());
    }

    @Test
    void testUpdateStatusToCollected() {
        OrgOrder order = new OrgOrder();
        order.setOrganisation(sharedOrg);
        order.setPosting(sharedPosting);
        order.setQuantity(1);
        order.setTotalPriceCents(300);
        order.setStatus(OrgOrder.Status.RESERVED);
        entityManager.persist(order);
        entityManager.flush();
        
        order.setStatus(OrgOrder.Status.COLLECTED);
        order.setCollectedAt(Instant.now());
        orderRepo.save(order);
        entityManager.flush();

        OrgOrder updated = entityManager.find(OrgOrder.class, order.getOrderId());
        
        assertEquals(OrgOrder.Status.COLLECTED, updated.getStatus());
        assertNotNull(updated.getCollectedAt());
    }

    @Test
    void testFindOrdersByOrganisation() {
        OrgOrder order1 = new OrgOrder();
        order1.setOrganisation(sharedOrg);
        order1.setPosting(sharedPosting);
        order1.setQuantity(1);
        order1.setTotalPriceCents(300);
        order1.setStatus(OrgOrder.Status.RESERVED);
        entityManager.persist(order1);

        OrgOrder order2 = new OrgOrder();
        order2.setOrganisation(sharedOrg);
        order2.setPosting(sharedPosting);
        order2.setQuantity(1);
        order2.setTotalPriceCents(300);
        order2.setStatus(OrgOrder.Status.CANCELLED);
        entityManager.persist(order2);
        
        entityManager.flush();

        List<OrgOrder> results = orderRepo.findAll().stream()
                .filter(o -> o.getOrganisation().getOrgId().equals(sharedOrg.getOrgId()))
                .toList();

        assertEquals(2, results.size());
    }
}