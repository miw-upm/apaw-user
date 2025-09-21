package es.upm.miw.apaw.resources.dtos;

import es.upm.miw.apaw.data.entities.DocumentType;
import es.upm.miw.apaw.data.entities.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
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
    private DocumentType documentType;
    private String identity;
    private String address;
    private String city;
    private String province;
    private Integer postalCode;
    private LocalDate registrationDate;
    private Boolean active;

    public UserDto(User user) {
        BeanUtils.copyProperties(user, this);
    }

    public void doDefault() {
        if (Objects.isNull(active)) {
            this.active = true;
        }
    }

    public UserDto ofMobileFirstNameFamilyName() {
        return UserDto.builder()
                .id(this.getId())
                .mobile(this.getMobile())
                .firstName(this.getFirstName())
                .familyName(this.getFamilyName())
                .build();
    }

    public User toUser() {
        User user = new User();
        BeanUtils.copyProperties(this, user);
        return user;
    }
}
