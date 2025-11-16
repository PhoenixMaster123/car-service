package springboot.bg.harisauto.web.mapper;

import lombok.experimental.UtilityClass;
import springboot.bg.harisauto.user.model.User;
import springboot.bg.harisauto.web.dto.ChangeProfileInfoRequest;

/**
 * DtoMapper.java - Utility class for mapping between entities and DTOs.
 *
 * @author Kristian Popov
 */
@UtilityClass
public class DtoMapper {

  /**
   * Maps a User entity to an EditProfileRequest DTO.
   *
   * @param user User entity
   * @return EditProfileRequest DTO
   */
  public static ChangeProfileInfoRequest fromUser(User user) {
    return ChangeProfileInfoRequest.builder()
      .firstName(user.getFirstName())
      .lastName(user.getLastName())
      .email(user.getEmail())
      .phoneNumber(user.getPhoneNumber())
      .country(user.getCountry())
      .build();
  }
}