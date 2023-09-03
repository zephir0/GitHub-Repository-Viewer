package com.example.githubrepositoryviewer.api_key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GitHubApiKey {
    @Value("${github.api.key}")
    private String githubApiKey;

    public String getGitHubApiKey() {
        return githubApiKey;
    }
}
