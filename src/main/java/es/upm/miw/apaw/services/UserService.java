package es.upm.miw.apaw.services;

import es.upm.miw.apaw.infrastructure.data.daos.UserRepository;
import es.upm.miw.apaw.infrastructure.data.models.User;
import es.upm.miw.apaw.services.criteria.UserFindCriteria;
import es.upm.miw.apaw.services.exceptions.ClientBusinessException;
import es.upm.miw.apaw.services.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

    private final UserRepository userRepository;

    public void create(User user) {
        this.assertNoExistByMobile(user.getMobile());
        user.setId(UUID.randomUUID());
        if (Objects.isNull(user.getPassword())) {
            user.setPassword(UUID.randomUUID().toString());
        }
        user.setRegistrationDate(LocalDate.now());
        this.userRepository.save(user);
    }

    private void assertNoExistByMobile(String mobile) {
        if (this.userRepository.existsByMobile(mobile)) {
            throw new ClientBusinessException("El móvil ya existe: " + mobile);
        }
    }

    public Stream<User> find(UserFindCriteria criteria) {
        return this.findByActiveAndMobile(criteria)
                .filter(user -> this.matchBillable(criteria, user));
    }

    private Stream<User> findByActiveAndMobile(UserFindCriteria criteria) {
        if (!criteria.hasActive() && !criteria.hasMobile()) {
            return this.userRepository.findAll().stream();
        }
        if (criteria.hasMobile() && criteria.hasActive()) {
            return this.userRepository.findByMobileAndActive(criteria.getMobile(), criteria.getActive()).stream();
        }
        if (criteria.hasMobile()) {
            return this.userRepository.findByMobile(criteria.getMobile()).stream();
        }
        return this.userRepository.findByActive(criteria.getActive()).stream();
    }

    private boolean matchBillable(UserFindCriteria criteria, User user) {
        return !criteria.hasBillable() || user.isBillable() == criteria.getBillable();
    }

    public User read(UUID id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User id not found: " + id));
    }

    public void delete(UUID id) {
        this.userRepository.deleteById(id);
    }

}
