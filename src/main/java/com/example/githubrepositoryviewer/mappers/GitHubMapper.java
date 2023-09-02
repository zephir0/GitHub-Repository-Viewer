package com.example.githubrepositoryviewer.mappers;

import com.example.githubrepositoryviewer.models.GitHubBranch;
import com.example.githubrepositoryviewer.models.GitHubBranchDTO;
import com.example.githubrepositoryviewer.models.GitHubRepository;
import com.example.githubrepositoryviewer.models.GitHubRepositoryDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GitHubMapper {
    public GitHubRepository mapToGitHubRepositoryWithBranches(GitHubRepositoryDTO gitHubRepositoryDTO,
                                                              List<GitHubBranch> branchList) {
        GitHubRepository repository = new GitHubRepository();
        repository.setName(gitHubRepositoryDTO.name());
        repository.setLogin(gitHubRepositoryDTO.owner().login());
        repository.setGitHubBranchList(branchList);
        return repository;
    }

    public GitHubBranch mapToGitHubBranch(GitHubBranchDTO branchDTO) {
        GitHubBranch branch = new GitHubBranch();
        branch.setName(branchDTO.name());
        branch.setSha(branchDTO.commit().sha());
        return branch;
    }
}
