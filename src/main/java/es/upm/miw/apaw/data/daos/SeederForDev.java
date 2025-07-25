package es.upm.miw.apaw.data.daos;

import es.upm.miw.apaw.data.entities.DocumentType;
import es.upm.miw.apaw.data.entities.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

@Log4j2
@Repository
@Profile({"dev", "test"})
public class SeederForDev {
    private final UserRepository userRepository;

    @Autowired
    public SeederForDev(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.deleteAllAndInitializeAndSeedDataBase();
    }

    public void deleteAllAndInitializeAndSeedDataBase() {
        this.deleteAllAndInitialize();
        this.seedDataBase();
    }

    public void deleteAllAndInitialize() {
        this.userRepository.deleteAll();
        log.warn("------- Deleted All -----------");
    }

    private void seedDataBase() {
        log.warn("------- Initial Load from JAVA -----------");
        User[] users = {
                User.builder().id(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeffff0000"))
                        .mobile("666000660").firstName("user0")
                        .documentType(DocumentType.DNI).identity("66666600D")
                        .address("C/User, 0 - 0A").email("user0@gmail.com")
                        .city("User0 City").province("User 0 Province").postalCode(28850)
                        .registrationDate(LocalDate.now()).active(true).build(),
                User.builder().id(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeffff0001"))
                        .mobile("666000661").firstName("user1").familyName("u1 family")
                        .registrationDate(LocalDate.now()).active(true).build(),
                User.builder().id(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeffff0002"))
                        .mobile("666000662").firstName("user2").familyName("u2 family")
                        .documentType(DocumentType.DNI).identity("66666602D")
                        .registrationDate(LocalDate.now()).active(true).build(),
                User.builder().id(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeffff0003"))
                        .mobile("666000663").firstName("user3").familyName("u3 family")
                        .documentType(DocumentType.CIF).identity("C66666603")
                        .address("C/User, 3 - 3A").email("user3@gmail.com")
                        .city("User3 City").province("User 3 Province").postalCode(28830)
                        .registrationDate(LocalDate.now()).active(true).build(),
                User.builder().id(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeffff0004"))
                        .mobile("666000664").firstName("user4").familyName("u4 family")
                        .documentType(DocumentType.NIE).identity("N66666604N")
                        .address("C/User, 4 - 4A").email("user4@gmail.com")
                        .city("User4 City").province("User 4 Province").postalCode(41001)
                        .registrationDate(LocalDate.now()).active(true).build(),
                User.builder().id(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeffff0005"))
                        .mobile("666666005").firstName("user5")
                        .city("User5 City").province("User 5 Province").postalCode(11001)
                        .registrationDate(LocalDate.now()).active(true).build()
        };
        this.userRepository.saveAll(Arrays.asList(users));
        log.warn("        ------- users");
    }

}
