package com.bank.antifraud.mapper;

import com.bank.antifraud.dto.CardTransfersDTO;
import com.bank.antifraud.model.SuspiciousCardTransfers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CardTransferMapper {

    CardTransferMapper INSTANCE = Mappers.getMapper(CardTransferMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cardTransferId", source = "cardTransferDTO.cardTransferId")
    @Mapping(target = "blocked", source = "cardTransferDTO.blocked")
    @Mapping(target = "suspicious", source = "cardTransferDTO.suspicious")
    SuspiciousCardTransfers toEntity(CardTransfersDTO cardTransferDTO);

    @Mapping(target = "blocked", source = "cardTransfer.blocked")
    @Mapping(target = "suspicious", source = "cardTransfer.suspicious")
    CardTransfersDTO toDTO(SuspiciousCardTransfers cardTransfer);

    List<CardTransfersDTO> toDTOList(List<SuspiciousCardTransfers> cardTransferEntityList);

    void updateEntityFromDTO(CardTransfersDTO cardTransferDTO, @MappingTarget SuspiciousCardTransfers cardTransferEntity);
}
