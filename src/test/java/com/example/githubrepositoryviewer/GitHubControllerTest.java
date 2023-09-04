package com.example.githubrepositoryviewer;

import com.example.githubrepositoryviewer.configs.filters.AcceptHeaderCheckFilter;
import com.example.githubrepositoryviewer.controllers.GitHubController;
import com.example.githubrepositoryviewer.exception_handlers.CustomExceptionHandler;
import com.example.githubrepositoryviewer.models.GitHubRepository;
import com.example.githubrepositoryviewer.services.GitHubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class GitHubControllerTest {


    private MockMvc mockMvc;
    @Mock
    private GitHubService gitHubService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new GitHubController(gitHubService))
                .addFilter(new AcceptHeaderCheckFilter())
                .setControllerAdvice(new CustomExceptionHandler())
                .build();
    }

    @Test
    public void testListUserRepositories_Success() throws Exception {
        // Arrange
        GitHubRepository gitHubRepository = new GitHubRepository();
        gitHubRepository.setName("repositoryName");
        gitHubRepository.setLogin("ownerLogin");
        gitHubRepository.setGitHubBranchList(new ArrayList<>());
        List<GitHubRepository> mockRepositories = Collections.singletonList(gitHubRepository);
        when(gitHubService.getUserRepositories(anyString())).thenReturn(mockRepositories);

        // Make an HTTP GET request to the controller.
        mockMvc.perform(get("/api/repositories/{username}", gitHubRepository.getName())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value(gitHubRepository.getName()))
                .andExpect(jsonPath("$[0].login").value(gitHubRepository.getLogin()))
                .andExpect(jsonPath("$[0].gitHubBranchList").value(gitHubRepository.getGitHubBranchList()));
    }

    @Test
    public void testGetUserRepositories_UserNotFound() throws Exception {
        Mockito.doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "User profile was not found."))
                .when(gitHubService).doesUserExist("");

        // Make an HTTP GET request to the controller.
        mockMvc.perform(get("/api/repositories/{username}", "")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetUserRepositories_NotAcceptableMediaType() throws Exception {
        // Make an HTTP GET request to the controller with an invalid header.
        mockMvc.perform(get("/api/repositories/{username}", "testuser")
                        .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    public void testGetUserRepositories_InvalidApiKey() throws Exception {
        // Given
        String username = "testUser";

        Mockito.doThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED))
                .when(gitHubService).getUserRepositories(username);

        // When & Then
        mockMvc.perform(get("/api/repositories/{username}", username).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}


