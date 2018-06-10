package it.techn.gumeh.service.mapper;

import it.techn.gumeh.domain.*;
import it.techn.gumeh.service.dto.ResurceDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Resurce and its DTO ResurceDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ResurceMapper extends EntityMapper<ResurceDTO, Resurce> {



    default Resurce fromId(Long id) {
        if (id == null) {
            return null;
        }
        Resurce resurce = new Resurce();
        resurce.setId(id);
        return resurce;
    }
}
