package com.bank.antifraud.controller;

import com.bank.antifraud.dto.CardTransfersDTO;
import com.bank.antifraud.service.interface_service.SuspiciousCardTransfersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/card_transfers")
public class ControllerCardTransfers {

    private final SuspiciousCardTransfersService cardService;

    @GetMapping("/{id}")
    @Operation(summary = "Get card transfer by ID")
    @ApiResponse(responseCode = "200", description = "Successful retrieval of card transfer")
    @ApiResponse(responseCode = "404", description = "card transfer not found")
    public ResponseEntity<CardTransfersDTO> getCard(@PathVariable Long id) {

        CardTransfersDTO transfer = cardService.getCardTransfer(id);
        log.info("Get card transfer with ID: {}", id);
        return ResponseEntity.ok(transfer);
    }

    @GetMapping()
    @Operation(summary = "Get all card transfers")
    @ApiResponse(responseCode = "200", description = "List of all card transfers")
    public ResponseEntity<List<CardTransfersDTO>> getAllSAT() {

        log.info("Retrieving all card transfers");
        List<CardTransfersDTO> transfers = cardService.getListSCardTransfers();
        return ResponseEntity.ok(transfers);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete card transfer by ID")
    @ApiResponse(responseCode = "200", description = "card transfer deleted successfully")
    @ApiResponse(responseCode = "404", description = "card transfer not found")
    public ResponseEntity<String> deleteSAT(@PathVariable Long id) {

        log.info("Deleting card transfer with ID: {}", id);
        cardService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body("Объект удалён");
    }

    @PostMapping()
    @Operation(summary = "Save new card transfer")
    @ApiResponse(responseCode = "201", description = "New card transfer saved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<?> saveSAT(@RequestBody @Valid CardTransfersDTO transfersDTO) {

        log.info("Saving new card transfer: {}", transfersDTO);
        cardService.saveCard(transfersDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Объект сохранен");
    }

    @PutMapping()
    @Operation(summary = "Update existing card transfer")
    @ApiResponse(responseCode = "200", description = "card transfer updated successfully")
    @ApiResponse(responseCode = "404", description = "card transfer not found")
    @ApiResponse(responseCode = "400", description = "card transfer not found")
    public ResponseEntity<?> updateSAT(@RequestBody @Valid CardTransfersDTO transfersDTO) {

        log.info("Updating card transfer with ID: {}, Data: {}", transfersDTO.getId(), transfersDTO);
        cardService.updateCard(transfersDTO);
        return ResponseEntity.status(HttpStatus.OK).body("Объект обновлён");
    }
}
