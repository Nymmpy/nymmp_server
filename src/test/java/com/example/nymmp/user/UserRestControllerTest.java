package com.example.nymmp.user;

import com.example.nymmp.dto.user.UserRequest;
import com.example.nymmp.model.Group;
import com.example.nymmp.model.User;
import com.example.nymmp.repository.GroupRepository;
import com.example.nymmp.repository.UserJPARepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJPARepository userJPARepository;

    @Autowired
    private GroupRepository groupRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private Group group;

    @BeforeEach
    public void setUp() {
        userJPARepository.deleteAll();
        groupRepository.deleteAll();

        group = Group.builder()
                .groupName("USER")
                .build();
        groupRepository.save(group);

        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> "{noop}" + invocation.getArgument(0));
        when(passwordEncoder.matches(anyString(), anyString())).thenAnswer(invocation -> {
            String rawPassword = invocation.getArgument(0);
            String encodedPassword = invocation.getArgument(1);
            return ("{noop}" + rawPassword).equals(encodedPassword);
        });
    }

    @Test
    public void checkMember_whenEmailDoesNotExist_returnsSuccess() throws Exception {
        UserRequest.EmailCheckDTO emailCheckDTO = new UserRequest.EmailCheckDTO();
        emailCheckDTO.setEmail("newuser@example.com");

        mockMvc.perform(post("/api/user/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailCheckDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void joinUser_whenValidInput_returnsOk() throws Exception {
        UserRequest.JoinDTO joinDTO = new UserRequest.JoinDTO();
        joinDTO.setEmail("newuser@example.com");
        joinDTO.setPassword("Password1!");
        joinDTO.setName("New User");
        joinDTO.setGroupName("USER");

        mockMvc.perform(post("/api/user/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void joinUser_whenEmailAlreadyExists_returnsForbidden() throws Exception {
        UserRequest.JoinDTO joinDTO = new UserRequest.JoinDTO();
        joinDTO.setEmail("test@example.com");
        joinDTO.setPassword("Password1!");
        joinDTO.setName("Test User");
        joinDTO.setGroupName("USER");

        User existingUser = joinDTO.toEntity("{noop}Password1!", group);
        userJPARepository.save(existingUser);

        mockMvc.perform(post("/api/user/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(joinDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void loginUser_whenValidCredentials_returnsOk() throws Exception {
        UserRequest.JoinDTO joinDTO = new UserRequest.JoinDTO();
        joinDTO.setEmail("test@example.com");
        joinDTO.setPassword("Password1!");
        joinDTO.setName("Test User");
        joinDTO.setGroupName("USER");

        User existingUser = joinDTO.toEntity("{noop}Password1!", group);
        userJPARepository.save(existingUser);

        UserRequest.LoginDTO loginDTO = new UserRequest.LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("Password1!");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void loginUser_whenEmailNotFound_returnsNotFound() throws Exception {
        UserRequest.LoginDTO loginDTO = new UserRequest.LoginDTO();
        loginDTO.setEmail("nonexistent@example.com");
        loginDTO.setPassword("Password1!");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    public void loginUser_whenPasswordDoesNotMatch_returnsBadrequest() throws Exception {
        UserRequest.JoinDTO joinDTO = new UserRequest.JoinDTO();
        joinDTO.setEmail("test@example.com");
        joinDTO.setPassword("Password1!");
        joinDTO.setName("Test User");
        joinDTO.setGroupName("USER");

        User existingUser = joinDTO.toEntity("{noop}Password1!", group);
        userJPARepository.save(existingUser);

        UserRequest.LoginDTO loginDTO = new UserRequest.LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("WrongPassword!");

        mockMvc.perform(post("/api/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
