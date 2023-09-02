package com.example.githubrepositoryviewer.models;

import java.util.List;

public record GitHubRepositoryDTO(String name, GitHubOwnerDto owner, boolean isFork,
                                  List<GitHubBranchDTO> gitHubBranchList) {
}