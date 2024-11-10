package com.bank.antifraud.repository;

import com.bank.antifraud.model.SuspiciousAccountTransfers;
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
public class AccountTransfersRepositoryTest {

    @Autowired
    private SuspiciousAccountTransfersRepository repository;

    @Autowired
    private TestEntityManager entityManager;
    private SuspiciousAccountTransfers account;
    private SuspiciousAccountTransfers account2;
    private SuspiciousAccountTransfers account3;
    @BeforeEach
    void setUp() {

        account = SuspiciousAccountTransfers.builder()
                .accountTransferId(12345L)
                .blocked(false)
                .suspicious(true)
                .blockedReason("activity")
                .suspiciousReason("Suspicious activity detected")
                .build();

        account2 = SuspiciousAccountTransfers.builder()
                .accountTransferId(54321L)
                .blocked(true)
                .suspicious(false)
                .blockedReason("High value transfer")
                .suspiciousReason("No suspicious activity")
                .build();

        account3 = SuspiciousAccountTransfers.builder()
                .accountTransferId(67890L)
                .blocked(false)
                .suspicious(true)
                .blockedReason("frequent transfers")
                .suspiciousReason("Frequent small transfers")
                .build();

        entityManager.persist(account);
        entityManager.persist(account2);
        entityManager.persist(account3);
        entityManager.flush();
    }

    @Test
    public void testSaveAndFindById() {

        SuspiciousAccountTransfers savedTransfer = repository.save(account);
        Optional<SuspiciousAccountTransfers> foundTransfer = repository.findById(savedTransfer.getId());

        assertThat(foundTransfer).isPresent();
        assertThat(foundTransfer.get().getAccountTransferId()).isEqualTo(12345L);
        assertThat(foundTransfer.get().isBlocked()).isFalse();
        assertThat(foundTransfer.get().isSuspicious()).isTrue();
        assertThat(foundTransfer.get().getBlockedReason()).isEqualTo("activity");
        assertThat(foundTransfer.get().getSuspiciousReason()).isEqualTo("Suspicious activity detected");
    }

    @Test
    public void testUpdate() {

        SuspiciousAccountTransfers savedTransfer = repository.save(account);
        savedTransfer.setBlocked(true);
        savedTransfer.setBlockedReason("Manual block");

        SuspiciousAccountTransfers updatedTransfer = repository.save(savedTransfer);
        assertThat(updatedTransfer.isBlocked()).isTrue();
        assertThat(updatedTransfer.getBlockedReason()).isEqualTo("Manual block");
    }

    @Test
    public void testDelete() {

        SuspiciousAccountTransfers savedTransfer = repository.save(account);
        repository.deleteById(savedTransfer.getId());

        Optional<SuspiciousAccountTransfers> deletedTransfer = repository.findById(savedTransfer.getId());
        assertThat(deletedTransfer).isNotPresent();
    }
    @Test
    public void testFindAll() {

        List<SuspiciousAccountTransfers> allTransfers = repository.findAll();
        assertThat(allTransfers.size()).isEqualTo(3);

        List<Long> transferId = allTransfers.stream()
                .map(SuspiciousAccountTransfers::getAccountTransferId)
                .toList();

        assertThat(transferId)
                .asList()
                .contains(12345L, 54321L, 67890L);
    }
}
