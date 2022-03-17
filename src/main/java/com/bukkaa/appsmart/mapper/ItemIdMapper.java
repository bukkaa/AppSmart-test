package com.bukkaa.appsmart.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper
public interface ItemIdMapper {

    @Named("uuidToString")
    static String uuidToString(UUID id) {
        return id.toString();
    }

    @Named("stringToUuid")
    static UUID stringToUuid(String stringId) {
        return UUID.fromString(stringId);
    }
}
