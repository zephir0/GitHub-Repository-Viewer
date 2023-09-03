package com.example.githubrepositoryviewer;

import com.example.githubrepositoryviewer.mappers.GitHubMapper;
import com.example.githubrepositoryviewer.models.*;
import com.example.githubrepositoryviewer.services.GitHubService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GitHubServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private GitHubMapper gitHubMapper;

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
        String apiUrl = "https://api.github.com/users/" + username;
        String reposUrl = "https://api.github.com/users/" + username + "/repos";

        ResponseEntity<String> userExistsResponse = new ResponseEntity<>("", HttpStatus.OK);
        when(restTemplate.getForEntity(apiUrl, String.class)).thenReturn(userExistsResponse);

        List<GitHubRepositoryDTO> mockRepositoryDTOs = new ArrayList<>();
        mockRepositoryDTOs.add(new GitHubRepositoryDTO("repo1", new GitHubOwnerDto("login"), false, new ArrayList<>()));
        mockRepositoryDTOs.add(new GitHubRepositoryDTO("repo2", new GitHubOwnerDto("login"), false, new ArrayList<>()));

        when(restTemplate.getForObject(reposUrl, GitHubRepositoryDTO[].class)).thenReturn(mockRepositoryDTOs.toArray(new GitHubRepositoryDTO[0]));

        GitHubRepository mockRepository1 = new GitHubRepository();
        GitHubRepository mockRepository2 = new GitHubRepository();

        when(gitHubMapper.mapToGitHubRepositoryWithBranches(any(), anyList())).thenReturn(mockRepository1, mockRepository2);

        // Act
        List<GitHubRepository> repositories = gitHubService.getUserRepositories(username);

        // Assert
        assertFalse(repositories.isEmpty());
        assertEquals(2, repositories.size());
    }

    @Test
    void testGetUserRepositories_User_Not_Found() {
        // Arrange
        String username = "nonExistingUser";
        String apiUrl = "https://api.github.com/users/" + username;

        when(restTemplate.getForEntity(apiUrl, String.class))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // Act and Assert
        assertThrows(HttpClientErrorException.class, () -> gitHubService.getUserRepositories(username));
    }

    @Test
    void testGetUserBranches_Success() {
        // Arrange
        String login = "testUser";
        String repositoryName = "testRepo";
        String branchesUrl = "https://api.github.com/repos/" + login + "/" + repositoryName + "/branches";

        List<GitHubBranchDTO> mockBranchDTOs = new ArrayList<>();
        mockBranchDTOs.add(new GitHubBranchDTO("branch1", new GitHubBranchDTO.Commit("commit1")));
        mockBranchDTOs.add(new GitHubBranchDTO("branch2", new GitHubBranchDTO.Commit("commit2")));

        when(restTemplate.getForObject(branchesUrl, GitHubBranchDTO[].class)).thenReturn(mockBranchDTOs.toArray(new GitHubBranchDTO[0]));

        GitHubBranch mockBranch1 = new GitHubBranch();
        GitHubBranch mockBranch2 = new GitHubBranch();

        when(gitHubMapper.mapToGitHubBranch(any())).thenReturn(mockBranch1, mockBranch2);

        // Act
        List<GitHubBranch> branches = gitHubService.getUserBranches(login, repositoryName);

        // Assert
        assertFalse(branches.isEmpty());
        assertEquals(2, branches.size());
    }

    @Test
    void testGetUserBranches_Invalid_Repository() {
        // Arrange
        String login = "testUser";
        String repositoryName = "invalidRepo";
        String branchesUrl = "https://api.github.com/repos/" + login + "/" + repositoryName + "/branches";

        when(restTemplate.getForObject(branchesUrl, GitHubBranchDTO[].class)).thenReturn(new GitHubBranchDTO[0]);

        // Act
        List<GitHubBranch> branches = gitHubService.getUserBranches(login, repositoryName);

        // Assert
        assertTrue(branches.isEmpty());
    }


}
