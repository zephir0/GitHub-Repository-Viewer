package com.example.githubrepositoryviewer.services;

import com.example.githubrepositoryviewer.api_key.GitHubApiKey;
import com.example.githubrepositoryviewer.mappers.GitHubMapper;
import com.example.githubrepositoryviewer.models.GitHubBranch;
import com.example.githubrepositoryviewer.models.GitHubBranchDTO;
import com.example.githubrepositoryviewer.models.GitHubRepository;
import com.example.githubrepositoryviewer.models.GitHubRepositoryDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GitHubService {

    private final RestTemplate restTemplate;
    private final GitHubMapper gitHubMapper;
    private final GitHubApiKey getGitHubApiKey;


    public List<GitHubRepository> getUserRepositories(String username) {
        if (doesUserExist(username)) {
            return fetchUserRepositories(username);
        }
        return Collections.emptyList();
    }

    public boolean doesUserExist(String username) {
        String apiUrl = "https://api.github.com/users/" + username;

        HttpEntity<String> entity = createEntityWithApiKey();

        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
        return response.getStatusCode() == HttpStatus.OK;
    }

    public List<GitHubRepository> fetchUserRepositories(String username) {
        String apiUrl = "https://api.github.com/users/" + username + "/repos";

        HttpEntity<String> entity = createEntityWithApiKey();

        GitHubRepositoryDTO[] repositories = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, GitHubRepositoryDTO[].class).getBody();

        if (repositories != null) {
            return Arrays.stream(repositories)
                    .filter(repo -> !repo.isFork())
                    .map(repo -> {
                        List<GitHubBranch> branches = getUserBranches(repo.owner().login(), repo.name());
                        return gitHubMapper.mapToGitHubRepositoryWithBranches(repo, branches);
                    })
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    public List<GitHubBranch> getUserBranches(String login,
                                              String repositoryName) {
        String branchesUrl = "https://api.github.com/repos/" + login + "/" + repositoryName + "/branches";

        HttpEntity<String> entity = createEntityWithApiKey();

        GitHubBranchDTO[] branches = restTemplate.exchange(branchesUrl, HttpMethod.GET, entity, GitHubBranchDTO[].class).getBody();

        if (branches != null) {
            return Arrays.stream(branches)
                    .map(gitHubMapper::mapToGitHubBranch)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    public HttpEntity<String> createEntityWithApiKey() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getGitHubApiKey.getGitHubApiKey());
        return new HttpEntity<>(headers);
    }
}
