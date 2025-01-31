package com.bank.antifraud.dto;

import com.bank.antifraud.util.Identifiable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class CardTransfersDTO implements Identifiable<Long> {

    @Valid
    private Long id;

    @NotNull(message = "Card transfer ID must not be null")
    private Long cardTransferId;

    @NotNull(message = "Blocked status must not be null")
    private boolean blocked;

    @NotNull(message = "Suspicious status must not be null")
    private boolean suspicious;

    @NotNull(message = "Blocked reason must not be null")
    @NotBlank(message = "Blocked reason must not be blank")
    private String blockedReason;

    @Size(min = 3, message = "Suspicious reason must have at least 3 characters")
    @NotBlank(message = "Suspicious reason must not be blank")
    private String suspiciousReason;
}
