package com.example.githubrepositoryviewer.models;

public record GitHubBranchDTO(String name, Commit commit) {
    public record Commit(String sha) {
    }
}
