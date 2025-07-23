package es.upm.api.services;


import es.upm.api.data.daos.UserRepository;
import es.upm.api.data.entities.User;
import es.upm.api.data.entities.UserFindCriteria;
import es.upm.api.services.exceptions.ConflictException;
import es.upm.api.services.exceptions.NotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class UserService {
    public static final String SCOPE_EDIT_PROFILE = "EDIT_PROFILE";

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void create(User user) {
        this.assertNoExistByMobile(user.getMobile());
        this.assertNoExistByEmail(user.getEmail());
        this.assertNoExistByDni(user.getIdentity());
        user.setId(UUID.randomUUID());
        user.setRegistrationDate(LocalDate.now());
        this.userRepository.save(user);
    }

    public User updateByMobile(String mobile, User user) {
        return this.updateUser(mobile, user);
    }

    private User updateUser(String mobile, User user) {
        User existing = this.readByMobile(mobile);
        if (!mobile.equals(user.getMobile())) {
            this.assertNoExistByMobile(user.getMobile());
        }
        if (!Objects.equals(existing.getEmail(), user.getEmail())) {
            this.assertNoExistByEmail(user.getEmail());
        }
        if (!Objects.equals(existing.getIdentity(), user.getIdentity())) {
            this.assertNoExistByDni(user.getIdentity());
        }
        BeanUtils.copyProperties(user, existing, "id", "registrationDate");
        return this.userRepository.save(existing);
    }

    public User read(UUID id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("The id don't exist: " + id));
    }

    public User readByMobile(String mobile) {
        return this.userRepository.findByMobile(mobile)
                .orElseThrow(() -> new NotFoundException("The mobile don't exists: " + mobile));
    }

    private void assertNoExistByEmail(String email) {
        if (email != null && this.userRepository.existsByEmail(email)) {
            throw new ConflictException("The email already exists: " + email);
        }
    }

    private void assertNoExistByMobile(String mobile) {
        if (this.userRepository.existsByMobile(mobile)) {
            throw new ConflictException("The mobile already exists: " + mobile);
        }
    }

    private void assertNoExistByDni(String dni) {
        if (dni != null && this.userRepository.existsByIdentity(dni)) {
            throw new ConflictException("The dni already exists: " + dni);
        }
    }

    public Stream<User> findNullSafe(UserFindCriteria criteria) {
        Stream<User> userDtos;
        if (criteria.all()) {
            userDtos = this.userRepository.findAll().stream();
        } else {
            userDtos = this.userRepository.findByMobileAndFirstNameAndFamilyNameNullSafe(
                    criteria.getMobile(), criteria.getFirstName(), criteria.getFamilyName()).stream();
        }
        return userDtos;
    }

}
