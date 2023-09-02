package com.example.githubrepositoryviewer.models;

import lombok.Data;

import java.util.List;

@Data
public class GitHubRepository {
    private String name;
    private String login;
    private List<GitHubBranch> gitHubBranchList;
}
