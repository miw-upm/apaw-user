package es.upm.miw.apaw.config;

import es.upm.miw.apaw.infrastructure.data.daos.UserRepository;
import es.upm.miw.apaw.infrastructure.data.models.Province;
import es.upm.miw.apaw.infrastructure.data.models.Role;
import es.upm.miw.apaw.infrastructure.data.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Log4j2
@Component
@Profile({"dev", "test"})
@Order(1)
@RequiredArgsConstructor
public class SeederForDev implements ApplicationRunner {
    public static final String PREFIX = "aaaaaaaa-bbbb-cccc-dddd-eeeeffff";
    public static final UUID ID_0 = UUID.fromString(PREFIX + "0000");
    public static final User C_0 = User.builder()
            .id(ID_0)
            .mobile("600000100")
            .firstName("cliente0")
            .familyName("García López")
            .role(Role.CUSTOMER)
            .identity("00000000T")
            .address("C/ Alcalá, 100")
            .email("cliente0@example.com")
            .city("Madrid")
            .province(Province.MADRID)
            .postalCode(28001)
            .registrationDate(LocalDate.of(2024, 1, 1))
            .active(true)
            .build();
    public static final UUID ID_1 = UUID.fromString(PREFIX + "0001");
    public static final User C_1 = User.builder()
            .id(ID_1)
            .mobile("600000101")
            .firstName("cliente1")
            .familyName("Martínez Ruiz")
            .role(Role.CUSTOMER)
            .identity("00000001R")
            .address("C/ Sierpes, 101")
            .email("cliente1@example.com")
            .city("Sevilla")
            .province(Province.SEVILLA)
            .postalCode(41001)
            .registrationDate(LocalDate.of(2025, 2, 1))
            .active(true)
            .build();
    public static final UUID ID_2 = UUID.fromString(PREFIX + "0002");
    public static final User C_2 = User.builder()
            .id(ID_2)
            .mobile("600000102")
            .firstName("cliente2")
            .familyName("Sánchez Pérez")
            .role(Role.CUSTOMER)
            .identity("00000002W")
            .address("Av. Andalucía, 102")
            .email("cliente2@example.com")
            .city("Cádiz")
            .province(Province.CADIZ)
            .postalCode(11001)
            .registrationDate(LocalDate.of(2025, 3, 1))
            .active(true)
            .build();
    public static final UUID ID_3 = UUID.fromString(PREFIX + "0003");
    public static final User C_3 = User.builder()
            .id(ID_3)
            .mobile("600000103")
            .firstName("cliente3")
            .familyName("Fernández Torres")
            .role(Role.CUSTOMER)
            .identity("00000003A")
            .address("C/ Mayor, 103")
            .email("cliente3@example.com")
            .city("Madrid")
            .province(Province.MADRID)
            .postalCode(28013)
            .registrationDate(LocalDate.of(2025, 4, 1))
            .active(true)
            .build();
    public static final UUID ID_4 = UUID.fromString(PREFIX + "0004");
    public static final User C_4 = User.builder()
            .id(ID_4)
            .mobile("600000104")
            .firstName("cliente4")
            .familyName("Romero Navarro")
            .role(Role.CUSTOMER)
            .identity("00000004G")
            .address("Av. Constitución, 104")
            .email("cliente4@example.com")
            .city("Sevilla")
            .province(Province.SEVILLA)
            .postalCode(41004)
            .registrationDate(LocalDate.of(2025, 5, 1))
            .active(true)
            .build();
    public static final UUID ID_5 = UUID.fromString(PREFIX + "0005");
    public static final User C_5 = User.builder()
            .id(ID_5)
            .mobile("600000105")
            .firstName("cliente5")
            .familyName("Moreno Castro")
            .role(Role.CUSTOMER)
            .identity("00000005M")
            .address("C/ Ancha, 105")
            .email("cliente5@example.com")
            .city("Cádiz")
            .province(Province.CADIZ)
            .postalCode(11005)
            .registrationDate(LocalDate.of(2025, 6, 1))
            .active(true)
            .build();
    public static final UUID ID_6 = UUID.fromString(PREFIX + "0006");
    public static final User C_6 = User.builder()
            .id(ID_6)
            .mobile("600000106")
            .firstName("Cliente6")
            .role(Role.CUSTOMER)
            .registrationDate(LocalDate.of(2025, 6, 7))
            .active(true)
            .build();
    public static final UUID ID_7 = UUID.fromString(PREFIX + "0007");
    public static final User C_7 = User.builder()
            .id(ID_7)
            .mobile("600000107")
            .firstName("Cliente7")
            .role(Role.CUSTOMER)
            .registrationDate(LocalDate.of(2025, 7, 7))
            .active(true)
            .build();
    public static final UUID ID_8 = UUID.fromString(PREFIX + "0008");
    public static final User C_8 = User.builder()
            .id(ID_8)
            .mobile("600000108")
            .firstName("Cliente8")
            .role(Role.CUSTOMER)
            .registrationDate(LocalDate.of(2025, 8, 7))
            .active(true)
            .build();
    public static final UUID ID_9 = UUID.fromString(PREFIX + "0009");
    public static final User C_9 = User.builder()
            .id(ID_9)
            .mobile("600000109")
            .firstName("Cliente9")
            .role(Role.CUSTOMER)
            .registrationDate(LocalDate.of(2025, 9, 7))
            .active(true)
            .build();
    public static final UUID ID_A = UUID.fromString(PREFIX + "000a");
    public static final UUID ID_B = UUID.fromString(PREFIX + "000b");
    public static final UUID ID_C = UUID.fromString(PREFIX + "000c");
    public static final User ADMIN_6 = User.builder()
            .id(ID_C)
            .mobile("6")
            .firstName("Admin6")
            .role(Role.ADMIN)
            .registrationDate(LocalDate.of(2025, 10, 1))
            .active(true)
            .build();
    public static final UUID ID_D = UUID.fromString(PREFIX + "000d");
    public static final User ADMIN = User.builder()
            .id(ID_D)
            .mobile("600000110")
            .firstName("Admin1")
            .familyName("García López")
            .role(Role.ADMIN)
            .identity("00000010X")
            .address("C/ Gran Vía, 10")
            .email("admin1@example.com")
            .city("Madrid")
            .province(Province.MADRID)
            .postalCode(28013)
            .registrationDate(LocalDate.of(2025, 10, 1))
            .active(true)
            .build();
    public static final UUID ID_E = UUID.fromString(PREFIX + "000e");
    public static final User MANAGER = User.builder()
            .id(ID_E)
            .mobile("600000111")
            .firstName("Manager1")
            .familyName("Martínez Ruiz")
            .role(Role.MANAGER)
            .identity("00000011B")
            .address("C/ Sierpes, 11")
            .email("manager1@example.com")
            .city("Sevilla")
            .province(Province.SEVILLA)
            .postalCode(41001)
            .registrationDate(LocalDate.of(2025, 11, 1))
            .active(true)
            .build();
    public static final UUID ID_F = UUID.fromString(PREFIX + "000f");
    public static final User OPERATOR = User.builder()
            .id(ID_F)
            .mobile("600000112")
            .firstName("Operator1")
            .familyName("Sánchez Pérez")
            .role(Role.OPERATOR)
            .identity("00000012N")
            .address("C/ Ancha, 12")
            .email("operator1@example.com")
            .city("Cádiz")
            .province(Province.CADIZ)
            .postalCode(11005)
            .registrationDate(LocalDate.of(2025, 12, 1))
            .active(true)
            .build();

    private final UserRepository userRepository;

    @Value("${app.db.password}")
    private String password;

    @Override
    public void run(ApplicationArguments args) {
        this.deleteAll();
        this.seed();
    }

    private void deleteAll() {
        this.userRepository.deleteAll();
        log.warn("------- Deleted All -----------");
    }

    private void seed() {
        log.warn("------- Initial Load from JAVA -----------");
        String pass = this.password;

        List<User> users = List.of(
                withEncodedPassword(C_0, pass),
                withEncodedPassword(C_1, pass),
                withEncodedPassword(C_2, pass),
                withEncodedPassword(C_3, pass),
                withEncodedPassword(C_4, pass),
                withEncodedPassword(C_5, pass),
                withEncodedPassword(C_6, pass),
                withEncodedPassword(C_7, pass),
                withEncodedPassword(C_8, pass),
                withEncodedPassword(C_9, pass),
                withEncodedPassword(ADMIN, pass),
                withEncodedPassword(MANAGER, pass),
                withEncodedPassword(OPERATOR, pass),
                withEncodedPassword(ADMIN_6, pass)
        );
        this.userRepository.saveAll(users);
        log.warn("        ------- users");

    }

    private User withEncodedPassword(User user, String pass) {
        return User.builder()
                .id(user.getId())
                .mobile(user.getMobile())
                .firstName(user.getFirstName())
                .familyName(user.getFamilyName())
                .email(user.getEmail())
                .identity(user.getIdentity())
                .address(user.getAddress())
                .city(user.getCity())
                .province(user.getProvince())
                .postalCode(user.getPostalCode())
                .password(pass)
                .role(user.getRole())
                .registrationDate(user.getRegistrationDate())
                .active(user.getActive())
                .build();
    }

}
