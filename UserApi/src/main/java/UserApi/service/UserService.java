package UserApi.service;

import UserApi.dto.user.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {

    UserDto createUser(UserDto userDto);

    Page<UserDto> getAllUsers(String localDateFrom, String localDateTo, Pageable pageable);

    UserDto updateUserById(Long id, UserDto userDto);

    void deleteUserById(Long id);

}
