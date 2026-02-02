package com.byteme.app;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrganisationTest {

    @Test
    void testOrganisationStateAndGetters() {
        Organisation org = new Organisation();
        UserAccount user = new UserAccount();
        UUID orgId = UUID.randomUUID();
        LocalDate lastOrder = LocalDate.of(2026, 2, 1);

        org.setOrgId(orgId);
        org.setUser(user);
        org.setName("Exeter Food Bank");
        org.setLocationText("Exeter, UK");
        org.setBillingEmail("billing@exeterfood.org");
        org.setCurrentStreakWeeks(3);
        org.setBestStreakWeeks(10);
        org.setTotalOrders(25);
        org.setLastOrderWeekStart(lastOrder);

        assertEquals(orgId, org.getOrgId());
        assertEquals(user, org.getUser());
        assertEquals("Exeter Food Bank", org.getName());
        assertEquals(3, org.getCurrentStreakWeeks());
        assertEquals(10, org.getBestStreakWeeks());
        assertEquals(25, org.getTotalOrders());
        assertEquals(lastOrder, org.getLastOrderWeekStart());
    }

    @Test
    void testGamificationDefaults() {
        Organisation org = new Organisation();

        assertEquals(0, org.getCurrentStreakWeeks(), "Current streak should default to 0");
        assertEquals(0, org.getBestStreakWeeks(), "Best streak should default to 0");
        assertEquals(0, org.getTotalOrders(), "Total orders should default to 0");
        assertNotNull(org.getCreatedAt(), "CreatedAt should be auto-initialized");
    }

    @Test
    void testStreakUpdates() {
        Organisation org = new Organisation();
        
        org.setCurrentStreakWeeks(org.getCurrentStreakWeeks() + 1);
        if (org.getCurrentStreakWeeks() > org.getBestStreakWeeks()) {
            org.setBestStreakWeeks(org.getCurrentStreakWeeks());
        }

        assertEquals(1, org.getCurrentStreakWeeks());
        assertEquals(1, org.getBestStreakWeeks());
    }
}