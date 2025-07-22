package es.upm.api.resources;

import es.upm.api.data.entities.UserFindCriteria;
import es.upm.api.resources.dtos.UserDto;
import es.upm.api.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Stream;

@Log4j2
@RestController
@RequestMapping(UserResource.USERS)
public class UserResource {
    public static final String USERS = "/users";
    public static final String ID_ID = "/{id}";
    public static final String MOBILE = "/mobile";
    public static final String MOBILE_ID = "/{mobile}";
    private final UserService userService;

    @Autowired
    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public void create(@Valid @RequestBody UserDto creationUserDto) {
        creationUserDto.doDefault();
        this.userService.create(creationUserDto.toUser());
    }

    @GetMapping(ID_ID)
    public UserDto read(@PathVariable UUID id) {
        return new UserDto(this.userService.read(id));
    }

    @GetMapping(MOBILE + MOBILE_ID)
    public UserDto readByMobile(@PathVariable String mobile) {
        return new UserDto(this.userService.readByMobile(mobile));
    }

    @PutMapping(MOBILE + MOBILE_ID)
    public UserDto updateByMobile(@PathVariable String mobile, @Valid @RequestBody UserDto userDto) {
        return new UserDto(this.userService.updateByMobile(mobile, userDto.toUser()));
    }

    @GetMapping
    public Stream<UserDto> findNullSafe(@ModelAttribute UserFindCriteria criteria) {
        Stream<UserDto> userDtos = this.userService.findNullSafe(criteria)
                .map(UserDto::new);
        if (criteria.isProjection()) {
            return userDtos;
        } else {
            return userDtos.map(UserDto::ofMobileFirstNameFamilyName);
        }
    }

}
