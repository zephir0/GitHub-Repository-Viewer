package com.example.githubrepositoryviewer;

import com.example.githubrepositoryviewer.controllers.GitHubController;
import com.example.githubrepositoryviewer.models.GitHubRepository;
import com.example.githubrepositoryviewer.services.GitHubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class GitHubControllerTest {

    @Mock
    private GitHubService gitHubService;
    @InjectMocks
    private GitHubController gitHubController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListUserRepositories_Success() {
        // Arrange
        String username = "existingUser";
        List<GitHubRepository> mockRepositories = new ArrayList<>();
        mockRepositories.add(new GitHubRepository());
        mockRepositories.add(new GitHubRepository());
        when(gitHubService.getUserRepositories(username)).thenReturn(mockRepositories);

        // Act
        ResponseEntity<List<GitHubRepository>> response = gitHubController.listUserRepositories(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRepositories, response.getBody());
    }
}
