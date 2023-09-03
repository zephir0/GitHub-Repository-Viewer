package com.example.githubrepositoryviewer;

import com.example.githubrepositoryviewer.api_key.GitHubApiKey;
import com.example.githubrepositoryviewer.mappers.GitHubMapper;
import com.example.githubrepositoryviewer.models.*;
import com.example.githubrepositoryviewer.services.GitHubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GitHubServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private GitHubMapper gitHubMapper;

    @Mock
    private GitHubApiKey gitHubApiKey;

    @InjectMocks
    private GitHubService gitHubService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserRepositories_Success() {
        // Arrange
        String username = "testUser";

        when(restTemplate.exchange(eq("https://api.github.com/users/" + username), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        GitHubRepositoryDTO mockRepoDTO = mock(GitHubRepositoryDTO.class);
        GitHubRepositoryDTO[] repoArray = new GitHubRepositoryDTO[]{mockRepoDTO};
        when(mockRepoDTO.isFork()).thenReturn(false);
        when(mockRepoDTO.owner()).thenReturn(new GitHubOwnerDto("testUser"));
        when(mockRepoDTO.name()).thenReturn("mockRepo");

        GitHubRepository mockRepo = mock(GitHubRepository.class);

        when(restTemplate.exchange(eq("https://api.github.com/users/" + username + "/repos"), eq(HttpMethod.GET), any(HttpEntity.class), eq(GitHubRepositoryDTO[].class)))
                .thenReturn(new ResponseEntity<>(repoArray, HttpStatus.OK));

        when(restTemplate.exchange(startsWith("https://api.github.com/repos/"), eq(HttpMethod.GET), any(HttpEntity.class), eq(GitHubBranchDTO[].class)))
                .thenReturn(new ResponseEntity<>(new GitHubBranchDTO[0], HttpStatus.OK));

        when(gitHubMapper.mapToGitHubRepositoryWithBranches(any(), anyList())).thenReturn(mockRepo);

        // Act
        List<GitHubRepository> result = gitHubService.getUserRepositories(username);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(mockRepo, result.get(0));
    }

    @Test
    void testGetUserRepositories_UserDoesNotExist() {
        // Arrange
        String username = "invalidUser";
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        // Act
        List<GitHubRepository> result = gitHubService.getUserRepositories(username);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testDoesUserExist_UserExists() {
        // Arrange
        String username = "testUser";
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        // Act
        boolean result = gitHubService.doesUserExist(username);
        // Assert
        assertTrue(result);
    }

    @Test
    void testDoesUserExist_UserDoesNotExist() {
        // Arrange
        String username = "testUser";
        when(restTemplate.exchange(anyString(), any(), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        // Act
        boolean result = gitHubService.doesUserExist(username);
        // Assert
        assertFalse(result);
    }


    @Test
    void testFetchUserRepositories_Success() {
        // Arrange
        String username = "testUser";
        String apiUrl = "https://api.github.com/users/" + username + "/repos";

        GitHubOwnerDto mockOwner = new GitHubOwnerDto(username);
        GitHubRepositoryDTO mockRepositoryDTO1 = new GitHubRepositoryDTO(
                "mockRepo1",
                mockOwner,
                false,
                Collections.emptyList()
        );

        GitHubRepositoryDTO mockRepositoryDTO2 = new GitHubRepositoryDTO(
                "mockRepo2",
                mockOwner,
                true,
                Collections.emptyList()
        );

        GitHubRepositoryDTO[] mockRepositories = {mockRepositoryDTO1, mockRepositoryDTO2};

        when(restTemplate.exchange(eq(apiUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(GitHubRepositoryDTO[].class)))
                .thenReturn(new ResponseEntity<>(mockRepositories, HttpStatus.OK));

        GitHubRepository mockRepo = mock(GitHubRepository.class);
        when(gitHubMapper.mapToGitHubRepositoryWithBranches(eq(mockRepositoryDTO1), anyList()))
                .thenReturn(mockRepo);

        when(restTemplate.exchange(startsWith("https://api.github.com/repos/"), eq(HttpMethod.GET), any(HttpEntity.class), eq(GitHubBranchDTO[].class)))
                .thenReturn(new ResponseEntity<>(new GitHubBranchDTO[0], HttpStatus.OK));

        // Act
        List<GitHubRepository> repos = gitHubService.fetchUserRepositories(username);

        // Assert
        assertEquals(1, repos.size());
        assertEquals(mockRepo, repos.get(0));
    }

    @Test
    void testFetchUserRepositories_EmptyRepositories() {
        // Arrange
        String username = "testUser";

        GitHubRepositoryDTO[] repoArray = new GitHubRepositoryDTO[]{};

        when(restTemplate.exchange(anyString(), any(), any(), eq(GitHubRepositoryDTO[].class)))
                .thenReturn(new ResponseEntity<>(repoArray, HttpStatus.OK));
        // Act
        List<GitHubRepository> result = gitHubService.fetchUserRepositories(username);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUserBranches_Success() {
        // Arrange
        String login = "testUser";
        String repoName = "testRepo";

        GitHubBranchDTO mockBranchDTO = mock(GitHubBranchDTO.class);
        GitHubBranchDTO[] branchArray = new GitHubBranchDTO[]{mockBranchDTO};

        GitHubBranch mockBranch = mock(GitHubBranch.class);

        when(restTemplate.exchange(anyString(), any(), any(), eq(GitHubBranchDTO[].class)))
                .thenReturn(new ResponseEntity<>(branchArray, HttpStatus.OK));
        when(gitHubMapper.mapToGitHubBranch(mockBranchDTO)).thenReturn(mockBranch);

        // Act
        List<GitHubBranch> result = gitHubService.getUserBranches(login, repoName);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(mockBranch, result.get(0));
    }

    @Test
    void testGetUserBranches_NoBranches() {
        // Arrange
        String login = "testUser";
        String repoName = "testRepo";
        String branchesUrl = "https://api.github.com/repos/" + login + "/" + repoName + "/branches";

        GitHubBranchDTO[] branchArray = new GitHubBranchDTO[]{};

        when(restTemplate.exchange(eq(branchesUrl), eq(HttpMethod.GET), any(HttpEntity.class), eq(GitHubBranchDTO[].class)))
                .thenReturn(new ResponseEntity<>(branchArray, HttpStatus.OK));

        // Act
        List<GitHubBranch> result = gitHubService.getUserBranches(login, repoName);

        // Assert
        assertTrue(result.isEmpty());
    }
}
