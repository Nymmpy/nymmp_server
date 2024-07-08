package com.example.nymmp.user;

import com.example.nymmp._core.exception.Exception401;
import com.example.nymmp._core.exception.Exception403;
import com.example.nymmp._core.exception.Exception404;
import com.example.nymmp.dto.user.UserRequest;
import com.example.nymmp.dto.user.UserResponse;
import com.example.nymmp.model.Group;
import com.example.nymmp.model.User;
import com.example.nymmp.repository.GroupRepository;
import com.example.nymmp.repository.UserJPARepository;
import com.example.nymmp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserJPARepository userJPARepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private UserService userService;

    private UserRequest.JoinDTO joinDTO;
    private UserRequest.LoginDTO loginDTO;
    private User user;
    private Group group;

    @BeforeEach
    public void setUp() {
        joinDTO = new UserRequest.JoinDTO();
        joinDTO.setEmail("test@example.com");
        joinDTO.setPassword("Password1!");
        joinDTO.setName("Test User");
        joinDTO.setGroupName("USER");

        loginDTO = new UserRequest.LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("Password1!");

        group = Group.builder()
                .groupId(1L)
                .groupName("USER")
                .build();

        user = joinDTO.toEntity("encodedPassword", group);
        user.assignGroup(group);
    }

    @Test
    public void join_whenEmailAlreadyExists_throwsException403() {
        when(userJPARepository.findByEmail(joinDTO.getEmail())).thenReturn(Optional.of(user));

        assertThrows(Exception403.class, () -> userService.join(joinDTO));

        verify(userJPARepository, times(1)).findByEmail(joinDTO.getEmail());
    }

    @Test
    public void join_whenGroupDoesNotExist_throwsException404() {
        when(userJPARepository.findByEmail(joinDTO.getEmail())).thenReturn(Optional.empty());
        when(groupRepository.findByGroupName(joinDTO.getGroupName())).thenReturn(Optional.empty());

        assertThrows(Exception404.class, () -> userService.join(joinDTO));

        verify(userJPARepository, times(1)).findByEmail(joinDTO.getEmail());
        verify(groupRepository, times(1)).findByGroupName(joinDTO.getGroupName());
    }

    @Test
    public void join_whenEmailDoesNotExist_savesUser() {
        when(userJPARepository.findByEmail(joinDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(joinDTO.getPassword())).thenReturn("encodedPassword");
        when(groupRepository.findByGroupName(joinDTO.getGroupName())).thenReturn(Optional.of(group));

        userService.join(joinDTO);

        verify(userJPARepository, times(1)).findByEmail(joinDTO.getEmail());
        verify(groupRepository, times(1)).findByGroupName(joinDTO.getGroupName());
        verify(userJPARepository, times(1)).save(any(User.class));
    }

    @Test
    public void login_whenEmailNotFound_throwsException404() {
        when(userJPARepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());

        assertThrows(Exception404.class, () -> userService.login(loginDTO));

        verify(userJPARepository, times(1)).findByEmail(loginDTO.getEmail());
    }

    @Test
    public void login_whenPasswordDoesNotMatch_throwsException401() {
        when(userJPARepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(Exception401.class, () -> userService.login(loginDTO));

        verify(userJPARepository, times(1)).findByEmail(loginDTO.getEmail());
    }

    @Test
    public void login_whenCredentialsAreValid_returnsLoginResponse() {
        when(userJPARepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())).thenReturn(true);

        UserResponse.LoginResponse response = userService.login(loginDTO);

        assertNotNull(response.getJwtToken());
        assertEquals("/home", response.getRedirectUrl());

        verify(userJPARepository, times(1)).findByEmail(loginDTO.getEmail());
    }

    @Test
    public void checkSameEmail_whenEmailAlreadyExists_throwsException403() {
        when(userJPARepository.findByEmail(joinDTO.getEmail())).thenReturn(Optional.of(user));

        assertThrows(Exception403.class, () -> userService.checkSameEmail(joinDTO.getEmail()));

        verify(userJPARepository, times(1)).findByEmail(joinDTO.getEmail());
    }

    @Test
    public void checkSameEmail_whenEmailDoesNotExist_doesNotThrowException() {
        when(userJPARepository.findByEmail(joinDTO.getEmail())).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> userService.checkSameEmail(joinDTO.getEmail()));

        verify(userJPARepository, times(1)).findByEmail(joinDTO.getEmail());
    }
}
