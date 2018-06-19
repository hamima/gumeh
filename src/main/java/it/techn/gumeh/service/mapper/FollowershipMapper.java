package it.techn.gumeh.service.mapper;

import it.techn.gumeh.domain.*;
import it.techn.gumeh.service.dto.FollowershipDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Followership and its DTO FollowershipDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, TagMapper.class})
public interface FollowershipMapper extends EntityMapper<FollowershipDTO, Followership> {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.login", target = "userLogin")
    @Mapping(source = "tag.id", target = "tagId")
    @Mapping(source = "tag.title", target = "tagTitle")
    FollowershipDTO toDto(Followership followership);

    @Mapping(source = "userId", target = "user")
    @Mapping(source = "tagId", target = "tag")
    Followership toEntity(FollowershipDTO followershipDTO);

    default Followership fromId(Long id) {
        if (id == null) {
            return null;
        }
        Followership followership = new Followership();
        followership.setId(id);
        return followership;
    }
}
