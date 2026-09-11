package es.upm.miw.apaw.resources;

import es.upm.miw.apaw.resources.dtos.UserDto;
import es.upm.miw.apaw.services.UserService;
import es.upm.miw.apaw.services.criteria.UserFindCriteria;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Log4j2
public class UserResource {
    public static final String USERS = "/users";
    public static final String USER_ID = USERS + "/{id}";
    private final UserService userService;

    @PostMapping(USERS)
    public void create(@Valid @RequestBody UserDto userDto) {
        this.userService.create(userDto.toDomain());
    }

    @GetMapping(Validations.ID_WITH_UUID)
    public UserDto readById(@PathVariable UUID id) {
        return new UserDto(this.userService.read(id));
    }

    @GetMapping(Validations.ID_WITH_MOBILE)
    public UserDto readByMobile(@PathVariable("id") String mobile) {
        return new UserDto(this.userService.readByMobile(mobile));
    }

    @GetMapping(USERS)
    public List<UserDto> find(@ModelAttribute UserFindCriteria criteria) {
        return this.userService.find(criteria)
                .map(UserDto::new)
                .map(UserDto::toSummary)
                .toList();
    }

    @DeleteMapping(USER_ID)
    public void delete(@PathVariable UUID id) {
        this.userService.delete(id);
    }

}

