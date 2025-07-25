package es.upm.miw.apaw.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "goaUser") // conflict with user table
public class User {
    @Id
    @Column(updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;
    @Column(unique = true, nullable = false)
    private String mobile;
    private String firstName;
    private String familyName;
    @Column(unique = true)
    private String email;
    private DocumentType documentType;
    @Column(unique = true)
    private String identity;
    private String address;
    private String city;
    private String province;
    private Integer postalCode;
    private LocalDate registrationDate;
    private Boolean active;
}
