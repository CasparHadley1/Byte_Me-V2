package com.byteme.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrganisationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrganisationRepository organisationRepo;

    private UserAccount sharedUser;

    @BeforeEach
    void setUp() {
        sharedUser = new UserAccount();
        sharedUser.setEmail("admin@charity.org");
        sharedUser.setPasswordHash("hashed_password");
        sharedUser.setRole(UserAccount.Role.ORG_ADMIN);
        entityManager.persist(sharedUser);
        entityManager.flush();
    }

    @Test
    void testSaveAndRetrieveOrganisationWithGamification() {
        Organisation org = new Organisation();
        org.setName("Global Food Share");
        org.setUser(sharedUser);
        org.setLocationText("123 Giving Way");
        org.setBillingEmail("finance@charity.org");
        
        org.setCurrentStreakWeeks(5);
        org.setBestStreakWeeks(12);
        org.setTotalOrders(50);
        org.setLastOrderWeekStart(LocalDate.now().minusWeeks(1));

        Organisation savedOrg = organisationRepo.save(org);
        entityManager.flush();
        entityManager.clear(); // Force fetch from DB

        Optional<Organisation> retrieved = organisationRepo.findById(savedOrg.getOrgId());

        assertTrue(retrieved.isPresent());
        Organisation found = retrieved.get();
        assertEquals("Global Food Share", found.getName());
        assertEquals(5, found.getCurrentStreakWeeks());
        assertEquals(12, found.getBestStreakWeeks());
        assertEquals(50, found.getTotalOrders());
        assertEquals(sharedUser.getUserId(), found.getUser().getUserId());
    }

    @Test
    void testFindByUserEmail() {
        Organisation org = new Organisation();
        org.setName("Test Org");
        org.setUser(sharedUser);
        entityManager.persist(org);
        entityManager.flush();

        Organisation found = organisationRepo.findAll().stream()
                .filter(o -> o.getUser().getEmail().equals("admin@charity.org"))
                .findFirst()
                .orElse(null);

        assertNotNull(found);
        assertEquals("Test Org", found.getName());
    }

    @Test
    void testDefaultValues() {
        Organisation org = new Organisation();
        org.setName("New Org");
        org.setUser(sharedUser);

        Organisation saved = organisationRepo.save(org);
        entityManager.flush();

        assertNotNull(saved.getCreatedAt());
        assertEquals(0, saved.getCurrentStreakWeeks());
        assertEquals(0, saved.getBestStreakWeeks());
        assertEquals(0, saved.getTotalOrders());
    }
}