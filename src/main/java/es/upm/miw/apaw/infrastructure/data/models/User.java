package es.upm.miw.apaw.infrastructure.data.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "miwUser")
public class User {
    @Id
    private UUID id;
    @Column(unique = true, nullable = false)
    private String mobile;
    private String firstName;
    private String familyName;
    private String email;
    private String identity;
    private String address;
    private String city;
    @Enumerated(EnumType.STRING)
    private Province province;
    private Integer postalCode;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;
    private LocalDate registrationDate;
    private Boolean active;

    public void doDefault() {
        if (this.password == null) {
            this.password = UUID.randomUUID().toString();
        }
        if (this.role == null) {
            this.role = Role.CUSTOMER;
        }
        if (this.active == null) {
            this.active = true;
        }
    }

    public boolean isBillable() {
        return this.hasContent(this.firstName)
                && this.hasContent(this.familyName)
                && this.hasContent(this.email)
                && this.hasContent(this.identity)
                && this.hasContent(this.address)
                && this.hasContent(this.city)
                && this.province != null
                && this.postalCode != null;
    }

    private boolean hasContent(String value) {
        return value != null && !value.isBlank();
    }
}
