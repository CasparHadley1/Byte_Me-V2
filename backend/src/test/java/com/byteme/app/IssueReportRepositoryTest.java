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
class IssueReportRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IssueReportRepository issueRepo;

    private Organisation sharedOrg;
    private Seller sharedSeller;
    private OrgOrder sharedOrder;

    @BeforeEach
    void setUp() {
        UserAccount sellerUser = new UserAccount();
        sellerUser.setEmail("seller-" + UUID.randomUUID() + "@byteme.com");
        sellerUser.setPasswordHash("secure_hash");
        sellerUser.setRole(UserAccount.Role.SELLER);
        entityManager.persist(sellerUser);

        sharedSeller = new Seller();
        sharedSeller.setName("The Salty Grocer");
        sharedSeller.setUser(sellerUser);
        entityManager.persist(sharedSeller);

        UserAccount orgUser = new UserAccount();
        orgUser.setEmail("org-" + UUID.randomUUID() + "@byteme.com");
        orgUser.setPasswordHash("secure_hash");
        orgUser.setRole(UserAccount.Role.ORG_ADMIN);
        entityManager.persist(orgUser);

        sharedOrg = new Organisation();
        sharedOrg.setName("Helping Hands NGO");
        sharedOrg.setUser(orgUser);
        entityManager.persist(sharedOrg);

        BundlePosting posting = new BundlePosting();
        posting.setSeller(sharedSeller);
        posting.setTitle("Leftover Pastries");
        posting.setPriceCents(500);
        posting.setPickupStartAt(Instant.now());
        posting.setPickupEndAt(Instant.now().plusSeconds(3600));
        posting.setStatus(BundlePosting.Status.ACTIVE);
        entityManager.persist(posting);

        sharedOrder = new OrgOrder();
        sharedOrder.setOrganisation(sharedOrg);
        sharedOrder.setPosting(posting);
        sharedOrder.setTotalPriceCents(500);
        sharedOrder.setStatus(OrgOrder.Status.RESERVED);
        entityManager.persist(sharedOrder);

        entityManager.flush();
    }

    @Test
    void testFindByOrganisationOrgId() {
        IssueReport issue = new IssueReport();
        issue.setOrder(sharedOrder);
        issue.setOrganisation(sharedOrg);
        issue.setType(IssueReport.Type.QUALITY); 
        issue.setDescription("Order was incomplete");
        issue.setStatus(IssueReport.Status.OPEN);
        entityManager.persist(issue);
        entityManager.flush();
        
        List<IssueReport> results = issueRepo.findByOrganisationOrgId(sharedOrg.getOrgId());
        
        assertEquals(1, results.size());
        assertEquals(sharedOrg.getOrgId(), results.get(0).getOrganisation().getOrgId());
    }

    @Test
    void testFindBySeller() {
        IssueReport issue = new IssueReport();
        issue.setOrder(sharedOrder);
        issue.setOrganisation(sharedOrg);
        issue.setType(IssueReport.Type.QUALITY); 
        issue.setDescription("Quality concern");
        issue.setStatus(IssueReport.Status.RESPONDED);
        entityManager.persist(issue);
        entityManager.flush();
        
        List<IssueReport> results = issueRepo.findBySeller(sharedSeller.getSellerId());
        
        assertFalse(results.isEmpty());
        assertEquals(sharedSeller.getSellerId(), 
            results.get(0).getOrder().getPosting().getSeller().getSellerId());
    }

    @Test
    void testFindOpenBySeller() {
        IssueReport openIssue = new IssueReport();
        openIssue.setOrder(sharedOrder);
        openIssue.setOrganisation(sharedOrg);
        openIssue.setType(IssueReport.Type.QUALITY); 
        openIssue.setDescription("Critical Issue");
        openIssue.setStatus(IssueReport.Status.OPEN);
        entityManager.persist(openIssue);

        IssueReport resolvedIssue = new IssueReport();
        resolvedIssue.setOrder(sharedOrder);
        resolvedIssue.setOrganisation(sharedOrg);
        resolvedIssue.setType(IssueReport.Type.QUALITY); 
        resolvedIssue.setDescription("Fixed Issue");
        resolvedIssue.setStatus(IssueReport.Status.RESOLVED);
        entityManager.persist(resolvedIssue);

        entityManager.flush();

        List<IssueReport> openIssues = issueRepo.findOpenBySeller(sharedSeller.getSellerId());
        
        assertEquals(1, openIssues.size());
        assertEquals(IssueReport.Status.OPEN, openIssues.get(0).getStatus());
    }
}