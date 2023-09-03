package com.example.githubrepositoryviewer.controllers;

import com.example.githubrepositoryviewer.models.GitHubRepository;
import com.example.githubrepositoryviewer.services.GitHubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/")
public class GitHubController {
    private final GitHubService gitHubService;

    @GetMapping(value = "/repositories/{username}")
    public ResponseEntity<List<GitHubRepository>> getUserRepositories(@PathVariable String username) {
        List<GitHubRepository> repositories = gitHubService.getUserRepositories(username);
        return new ResponseEntity<>(repositories, HttpStatus.OK);
    }
}
