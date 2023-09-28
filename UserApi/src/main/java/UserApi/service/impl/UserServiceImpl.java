package UserApi.service.impl;


import UserApi.dto.user.UserDto;
import UserApi.entity.User;
import UserApi.exception.NotFoundException;
import UserApi.repository.UserRepository;
import UserApi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");


    private final UserRepository userRepo;
    private final ModelMapper modelMapper;

    @Override
    public UserDto createUser(UserDto userDto) {
        var user = modelMapper.map(userDto, User.class);
        return modelMapper.map(userRepo.save(user), UserDto.class);

    }

    @Override
    public Page<UserDto> getAllUsers(String localDateFrom, String localDateTo, Pageable pageable) {
        Page<User> users = null;

        if (StringUtils.isEmpty(localDateFrom) || StringUtils.isEmpty(localDateTo)) {
            users = userRepo.findAll(pageable);
            return users.map(user -> modelMapper.map(user, UserDto.class));
        }
        return getAllUsersWithDateRange(localDateFrom, localDateTo, pageable);
    }

    @Override
    public UserDto updateUserById(Long id, UserDto userDto) {
        var user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + id));
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setBirthDate(userDto.getBirthDate());
        var updatedUser = userRepo.save(user);
        return modelMapper.map(updatedUser, UserDto.class);
    }

    @Override
    public void deleteUserById(Long id) {
        if (userRepo.existsById(id)) {
            userRepo.deleteById(id);
        } else {
            throw new NotFoundException("User with this id not found!");
        }
    }

    private Page<UserDto> getAllUsersWithDateRange(String localDateFrom, String localDateTo, Pageable pageable) {
        validateDateRangeFormat(localDateFrom, localDateTo);
        LocalDate from = LocalDate.parse(localDateFrom);
        LocalDate to = LocalDate.parse(localDateTo);
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("From date must be less than To date");
        }
        return userRepo.findByBirthDateBetween(from, to, pageable).map(user -> modelMapper.map(user, UserDto.class));
    }

    private void validateDateRangeFormat(String localDateFrom, String localDateTo) {
        Matcher localDateFromMatcher = DATE_PATTERN.matcher(localDateFrom);
        Matcher localDateToMatcher = DATE_PATTERN.matcher(localDateTo);
        if (!localDateFromMatcher.matches() || !localDateToMatcher.matches()) {
            throw new IllegalArgumentException("Date should be in format YYYY-MM-dd");
        }
    }
}
