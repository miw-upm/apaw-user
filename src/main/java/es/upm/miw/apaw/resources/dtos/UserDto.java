package es.upm.miw.apaw.resources.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import es.upm.miw.apaw.infrastructure.data.models.Province;
import es.upm.miw.apaw.infrastructure.data.models.Role;
import es.upm.miw.apaw.infrastructure.data.models.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.UUID;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @JsonProperty(access = Access.READ_ONLY)
    private UUID id;
    @NotNull
    @NotBlank
    @Pattern(regexp = Validations.MOBILE_RX)
    private String mobile;
    @NotNull
    @NotBlank
    private String firstName;
    private String familyName;
    private String email;
    private String identity;
    private String address;
    private String city;
    private Province province;
    private Integer postalCode;
    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;
    private Role role;
    @JsonProperty(access = Access.READ_ONLY)
    private LocalDate registrationDate;
    private Boolean active;

    public UserDto(User user) {
        BeanUtils.copyProperties(user, this);
    }

    public UserDto toSummary() {
        return UserDto.builder()
                .id(this.getId())
                .mobile(this.getMobile())
                .firstName(this.getFirstName())
                .familyName(this.getFamilyName())
                .email(this.getEmail())
                .build();
    }

    public User toDomain() {
        User user = new User();
        BeanUtils.copyProperties(this, user);
        return user;
    }

}
