package com.bank.antifraud.repository;

import com.bank.antifraud.model.SuspiciousPhoneTransfers;
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
public class PhoneTransfersRepositoryTest {

    @Autowired
    private SuspiciousPhoneTransfersRepository repository;

    @Autowired
    private TestEntityManager entityManager;
    private SuspiciousPhoneTransfers phone;
    private SuspiciousPhoneTransfers phone2;
    private SuspiciousPhoneTransfers phone3;

    @BeforeEach
    void setUp() {

        phone = SuspiciousPhoneTransfers.builder()
                .phoneTransferId(12345L)
                .blocked(false)
                .suspicious(true)
                .blockedReason("activity")
                .suspiciousReason("Suspicious activity detected")
                .build();

        phone2 = SuspiciousPhoneTransfers.builder()
                .phoneTransferId(54321L)
                .blocked(true)
                .suspicious(false)
                .blockedReason("High value transfer")
                .suspiciousReason("No suspicious activity")
                .build();

        phone3 = SuspiciousPhoneTransfers.builder()
                .phoneTransferId(67890L)
                .blocked(false)
                .suspicious(true)
                .blockedReason("frequent transfers")
                .suspiciousReason("Frequent small transfers")
                .build();

        entityManager.persist(phone);
        entityManager.persist(phone2);
        entityManager.persist(phone3);
        entityManager.flush();
    }

    @Test
    public void testSaveAndFindById() {

        SuspiciousPhoneTransfers savedTransfer = repository.save(phone);
        Optional<SuspiciousPhoneTransfers> foundTransfer = repository.findById(savedTransfer.getId());

        assertThat(foundTransfer).isPresent();
        assertThat(foundTransfer.get().getPhoneTransferId()).isEqualTo(12345L);
        assertThat(foundTransfer.get().isBlocked()).isFalse();
        assertThat(foundTransfer.get().isSuspicious()).isTrue();
        assertThat(foundTransfer.get().getBlockedReason()).isEqualTo("activity");
        assertThat(foundTransfer.get().getSuspiciousReason()).isEqualTo("Suspicious activity detected");
    }

    @Test
    public void testUpdate() {

        SuspiciousPhoneTransfers savedTransfer = repository.save(phone);
        savedTransfer.setBlocked(true);
        savedTransfer.setBlockedReason("Manual block");

        SuspiciousPhoneTransfers updatedTransfer = repository.save(savedTransfer);
        assertThat(updatedTransfer.isBlocked()).isTrue();
        assertThat(updatedTransfer.getBlockedReason()).isEqualTo("Manual block");
    }

    @Test
    public void testDelete() {

        SuspiciousPhoneTransfers savedTransfer = repository.save(phone);
        repository.deleteById(savedTransfer.getId());

        Optional<SuspiciousPhoneTransfers> deletedTransfer = repository.findById(savedTransfer.getId());
        assertThat(deletedTransfer).isNotPresent();
    }

    @Test
    public void testFindAll() {

        List<SuspiciousPhoneTransfers> allTransfers = repository.findAll();
        assertThat(allTransfers.size()).isEqualTo(3);

        List<Long> transferId = allTransfers.stream()
                .map(SuspiciousPhoneTransfers::getPhoneTransferId)
                .toList();

        assertThat(transferId)
                .asList()
                .contains(12345L, 54321L, 67890L);
    }
}
