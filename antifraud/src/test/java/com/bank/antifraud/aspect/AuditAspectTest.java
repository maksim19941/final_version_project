package com.bank.antifraud.aspect;

import com.bank.antifraud.exception.AuditRecordNotFoundException;
import com.bank.antifraud.model.AuditModel;
import com.bank.antifraud.service.interface_service.AuditService;
import com.bank.antifraud.util.Identifiable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuditAspectTest {

    @Mock
    private AuditService auditService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MethodSignature methodSignature;

    @InjectMocks
    private AuditAspect auditAspect;

    private static class TestIdentifiable implements Identifiable<Long> {
        private final Long id;

        TestIdentifiable(Long id) {
            this.id = id;
        }

        @Override
        public Long getId() {
            return id;
        }
    }

    private TestIdentifiable testEntity;

    @BeforeEach
    public void setUp() {
        testEntity = new TestIdentifiable(1L);
    }

    @Test
    public void testAfterResultCreateAdvice_successfulCreation() throws JsonProcessingException {

        String jsonRepresentation = "{\"id\":1}";
        when(objectMapper.writeValueAsString(testEntity)).thenReturn(jsonRepresentation);
        auditAspect.afterResultCreateAdvice(mockJoinPointWithMethodSignature(), testEntity);
        verify(auditService, times(1)).createAudit(any(AuditModel.class));
        verify(objectMapper, times(1)).writeValueAsString(testEntity);
    }

    @Test
    public void testAfterResultUpdateAdvice_existingAuditRecord() throws JsonProcessingException {

        String jsonRepresentation = "{\"id\":1}";
        AuditModel existingAudit = new AuditModel();
        existingAudit.setEntityJson("{\"id\":1}");

        when(objectMapper.writeValueAsString(testEntity)).thenReturn(jsonRepresentation);
        when(auditService.getLatestAuditByJson(anyString(), anyLong())).thenReturn(existingAudit);

        auditAspect.afterResultUpdateAdvice(mockJoinPointWithMethodSignature(), testEntity);
        verify(auditService, times(1)).createAudit(any(AuditModel.class));
    }

    @Test
    public void testAfterResultUpdateAdvice_noExistingAuditRecord() throws JsonProcessingException {

        String jsonRepresentation = "{\"id\":1}";

        when(objectMapper.writeValueAsString(testEntity)).thenReturn(jsonRepresentation);
        when(auditService.getLatestAuditByJson(anyString(), anyLong())).thenReturn(null);

        auditAspect.afterResultUpdateAdvice(mockJoinPointWithMethodSignature(), testEntity);
        verify(auditService, times(1)).createAudit(any(AuditModel.class));
    }

    @Test
    public void testGetID_withIdentifiable() {

        Long id = auditAspect.getID(testEntity);
        assertEquals(id, 1L);
    }

    @Test
    public void testGetID_withNonIdentifiable_throwsException() {

        Object nonIdentifiableObject = new Object();
        assertThrows(AuditRecordNotFoundException.class, () -> auditAspect.getID(nonIdentifiableObject));
    }

    private JoinPoint mockJoinPointWithMethodSignature() {

        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(methodSignature);
        when(methodSignature.getParameterTypes()).thenReturn(new Class<?>[]{TestIdentifiable.class});
        when(methodSignature.getMethod()).thenReturn(AuditAspectTest.class.getDeclaredMethods()[0]);
        return joinPoint;
    }
}
