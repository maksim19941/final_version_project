package com.bank.antifraud.mapper;

import com.bank.antifraud.dto.PhoneTransfersDTO;
import com.bank.antifraud.model.SuspiciousPhoneTransfers;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PhoneTransferMapper {

    PhoneTransferMapper INSTANCE = Mappers.getMapper(PhoneTransferMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "phoneTransferId", source = "phoneTransferDTO.phoneTransferId")
    @Mapping(target = "blocked", source = "phoneTransferDTO.blocked")
    @Mapping(target = "suspicious", source = "phoneTransferDTO.suspicious")
    SuspiciousPhoneTransfers toEntity(PhoneTransfersDTO phoneTransferDTO);

    @Mapping(target = "blocked", source = "phoneTransfer.blocked")
    @Mapping(target = "suspicious", source = "phoneTransfer.suspicious")
    PhoneTransfersDTO toDTO(SuspiciousPhoneTransfers phoneTransfer);

    List<PhoneTransfersDTO> toDTOList(List<SuspiciousPhoneTransfers> phoneTransferEntityList);

    void updateEntityFromDTO(PhoneTransfersDTO phoneTransferDTO,
                             @MappingTarget SuspiciousPhoneTransfers phoneTransferEntity);
}
