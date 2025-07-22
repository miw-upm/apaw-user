package es.upm.api.data.daos;

import es.upm.api.data.entities.DocumentType;
import es.upm.api.data.entities.User;
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
                        .mobile("6").firstName("admin").registrationDate(LocalDate.now()).active(true).build(),
                User.builder().id(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeffff0001"))
                        .mobile("61").firstName("manager").registrationDate(LocalDate.now()).active(true).build(),
                User.builder().id(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeffff0002"))
                        .mobile("62").firstName("operator").registrationDate(LocalDate.now()).active(true).build(),
                User.builder().id(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeffff0004"))
                        .mobile("66").firstName("customer").familyName("family-c1")
                        .documentType(DocumentType.DNI).identity("66666603E").address("C/TPV, 3").email("c1@gmail.com")
                        .city("Madrid").province("Madrid").postalCode(28012)
                        .registrationDate(LocalDate.now()).active(true).build(),
                User.builder().id(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeffff0005"))
                        .mobile("666666001").firstName("c2").familyName("family-c2")
                        .documentType(DocumentType.DNI).identity("66666604T").address("C/TPV, 4").email("c2@gmail.com")
                        .city("Sevilla").province("Sevilla").postalCode(41001)
                        .registrationDate(LocalDate.now()).active(true).build(),
                User.builder().id(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeffff0006"))
                        .mobile("666666002").firstName("c3")
                        .city("Cádiz").province("Cadiz").postalCode(11001)
                        .registrationDate(LocalDate.now()).active(true).build(),
                User.builder().id(UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeffff0007"))
                        .mobile("666666003").firstName("admin3")
                        .registrationDate(LocalDate.now()).active(true).build()
        };
        this.userRepository.saveAll(Arrays.asList(users));
        log.warn("        ------- users");
    }

}
