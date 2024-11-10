package com.bank.antifraud.repository;

import com.bank.antifraud.model.SuspiciousCardTransfers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@ActiveProfiles("test")
public class CardTransfersRepositoryTest {

    @Autowired
    private SuspiciousCardTransfersRepository repository;

    @Autowired
    private TestEntityManager entityManager;
    private SuspiciousCardTransfers card;
    private SuspiciousCardTransfers card2;
    private SuspiciousCardTransfers card3;
    @BeforeEach
    void setUp() {

        card = SuspiciousCardTransfers.builder()
                .cardTransferId(12345L)
                .blocked(false)
                .suspicious(true)
                .blockedReason("activity")
                .suspiciousReason("Suspicious activity detected")
                .build();

        card2 = SuspiciousCardTransfers.builder()
                .cardTransferId(54321L)
                .blocked(true)
                .suspicious(false)
                .blockedReason("High value transfer")
                .suspiciousReason("No suspicious activity")
                .build();

        card3 = SuspiciousCardTransfers.builder()
                .cardTransferId(67890L)
                .blocked(false)
                .suspicious(true)
                .blockedReason("frequent transfers")
                .suspiciousReason("Frequent small transfers")
                .build();

        entityManager.persist(card);
        entityManager.persist(card2);
        entityManager.persist(card3);
        entityManager.flush();
    }

    @Test
    public void testSaveAndFindById() {

        SuspiciousCardTransfers savedTransfer = repository.save(card);
        Optional<SuspiciousCardTransfers> foundTransfer = repository.findById(savedTransfer.getId());

        assertThat(foundTransfer).isPresent();
        assertThat(foundTransfer.get().getCardTransferId()).isEqualTo(12345L);
        assertThat(foundTransfer.get().isBlocked()).isFalse();
        assertThat(foundTransfer.get().isSuspicious()).isTrue();
        assertThat(foundTransfer.get().getBlockedReason()).isEqualTo("activity");
        assertThat(foundTransfer.get().getSuspiciousReason()).isEqualTo("Suspicious activity detected");
    }

    @Test
    public void testUpdate() {

        SuspiciousCardTransfers savedTransfer = repository.save(card);
        savedTransfer.setBlocked(true);
        savedTransfer.setBlockedReason("Manual block");

        SuspiciousCardTransfers updatedTransfer = repository.save(savedTransfer);
        assertThat(updatedTransfer.isBlocked()).isTrue();
        assertThat(updatedTransfer.getBlockedReason()).isEqualTo("Manual block");
    }

    @Test
    public void testDelete() {

        SuspiciousCardTransfers savedTransfer = repository.save(card);
        repository.deleteById(savedTransfer.getId());

        Optional<SuspiciousCardTransfers> deletedTransfer = repository.findById(savedTransfer.getId());
        assertThat(deletedTransfer).isNotPresent();
    }
    @Test
    public void testFindAll() {

        List<SuspiciousCardTransfers> allTransfers = repository.findAll();
        assertThat(allTransfers.size()).isEqualTo(3);

        List<Long> transferId = allTransfers.stream()
                .map(SuspiciousCardTransfers::getCardTransferId)
                .toList();

        assertThat(transferId)
                .asList()
                .contains(12345L, 54321L, 67890L);
    }
}
