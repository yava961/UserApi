package UserApi;

import UserApi.dto.user.UserDto;
import UserApi.entity.User;
import UserApi.exception.NotFoundException;
import UserApi.repository.UserRepository;
import UserApi.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Spy
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void testDeleteUserByIdWhenUserExistsThenDeleteUser() {
        // Arrange
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(true);

        // Act
        userService.deleteUserById(id);

        // Assert
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteUserByIdWhenUserDoesNotExistThenThrowNotFoundException() {
        // Arrange
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.deleteUserById(id));
        verify(userRepository, times(1)).existsById(id);
    }

    @Test
    void testGetAllUsersWhenDatesNotProvidedThenReturnAllUsers() {
        // Arrange
        Pageable pageable = mock(Pageable.class);
        var users = new PageImpl<>(Collections.singletonList(createUserStub()));
        Mockito.when(userRepository.findAll(pageable))
                .thenReturn(users);
        // Act
        userService.getAllUsers("", "", pageable);

        // Assert
        verify(userRepository, times(1)).findAll(pageable);
        verify(userRepository, times(0)).findByBirthDateBetween(any(LocalDate.class), any(LocalDate.class), any(Pageable.class));
    }

    @Test
    void testGetAllUsersWhenDatesProvidedThenReturnUsersInDateRange() {
        // Arrange
        Pageable pageable = mock(Pageable.class);
        String localDateFrom = "2022-01-01";
        String localDateTo = "2022-12-31";
        var users = new PageImpl<>(Collections.singletonList(createUserStub()));
        Mockito.when(userRepository.findByBirthDateBetween(LocalDate.parse(localDateFrom), LocalDate.parse(localDateTo), pageable))
                .thenReturn(users);

        // Act
        userService.getAllUsers(localDateFrom, localDateTo, pageable);

        // Assert
        verify(userRepository, times(1)).findByBirthDateBetween(LocalDate.parse(localDateFrom), LocalDate.parse(localDateTo), pageable);
        verify(userRepository, times(0)).findAll(any(Pageable.class));
    }

    @Test
    void testGetAllUsersWhenDateFromIsInvalidThenThrowIllegalArgumentException() {
        // Arrange
        Pageable pageable = mock(Pageable.class);
        String localDateFrom = "2022/01/01";
        String localDateTo = "2022-12-31";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.getAllUsers(localDateFrom, localDateTo,pageable));
        verify(userRepository, times(0)).findByBirthDateBetween(Mockito.any(),Mockito.any(),Mockito.any());
        verify(userRepository, times(0)).findAll(any(Pageable.class));
    }

    @Test
    void testGetAllUsersWhenDateToIsInvalidThenThrowIllegalArgumentException() {
        // Arrange
        Pageable pageable = mock(Pageable.class);
        String localDateFrom = "2022-01-01";
        String localDateTo = "2022/12/31";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.getAllUsers(localDateFrom, localDateTo,pageable));
        verify(userRepository, times(0)).findByBirthDateBetween(Mockito.any(),Mockito.any(),Mockito.any());
        verify(userRepository, times(0)).findAll(any(Pageable.class));
    }

    @Test
    void testGetAllUsersWhenDatesProvidedAreInvalidThenThrowIllegalArgumentException() {
        // Arrange
        Pageable pageable = mock(Pageable.class);
        String localDateFrom = "2022/01/01";
        String localDateTo = "2022/12/31";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.getAllUsers(localDateFrom, localDateTo,pageable));
        verify(userRepository, times(0)).findByBirthDateBetween(Mockito.any(),Mockito.any(),Mockito.any());
        verify(userRepository, times(0)).findAll(any(Pageable.class));
    }


    @Test
    void testUpdateUserByIdWhenUserExistsInRepositoryThenUserIsUpdated() {

        UserDto userDto = createUserDtoStub();
        User user = createUserStub();
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // Act
        UserDto updatedUserDto = userService.updateUserById(1L, userDto);

        // Assert
        assertNotNull(updatedUserDto);
        assertEquals(updatedUserDto, userDto);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUserByIdWhenUserDoesNotExistInRepositoryThenNotFoundExceptionIsThrown() {
        UserDto userDto = createUserDtoStub();
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.updateUserById(1L, userDto));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateUserWhenValidUserDtoThenReturnUserDto() {
        UserDto userDto = createUserDtoStub();
        User user = createUserStub();
        when(modelMapper.map(userDto, User.class)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(modelMapper.map(user, UserDto.class)).thenReturn(userDto);

        UserDto result = userService.createUser(userDto);

        verify(userRepository, times(1)).save(user);
        assertEquals(userDto, result);
    }

    private UserDto createUserDtoStub() {
        UserDto userDto = new UserDto();
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");
        userDto.setBirthDate(LocalDate.of(1990, 1, 1));
        return userDto;
    }

    private User createUserStub() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        return user;
    }
}
