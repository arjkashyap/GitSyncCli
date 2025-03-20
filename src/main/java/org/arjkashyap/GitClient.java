package org.arjkashyap;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GitClient {

    private final String username;
    private final String password;
    private final String token;
    private final String remoteRepoUrl;
    private final String localGitRepoPath;
    private final String branchName;

    public GitClient(String username, String password, String token, String remoteRepoUrl, String localGitRepoPath, String branchName) {
        this.username = username;
        this.password = password;
        this.token = token;
        this.remoteRepoUrl = remoteRepoUrl.endsWith(".git") ? remoteRepoUrl : (remoteRepoUrl+".git");
        this.localGitRepoPath = localGitRepoPath;
        this.branchName = branchName;
    }

    public void pushToRemote() throws GitAPIException, IOException {

        Map<String, Set<String>> mp = getGitStatus();

        System.out.printf("%s pushing files to remote %s%n", branchName,remoteRepoUrl);
        List<String> allFiles = mp.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toList());

        if(allFiles.isEmpty()) {
            System.out.println("No files have been modified or created.");
            return;
        }
        
        createBranchWithFiles(allFiles);
    }

    public void createBranchWithFiles(List<String> filePaths) {

        try (Git git = Git.open(new File(localGitRepoPath))) {

            boolean branchExists = git.branchList().call().stream()
                    .anyMatch(ref -> ref.getName().equals("refs/heads/" + branchName));

            if (branchExists) {
                git.checkout()
                        .setName(branchName)
                        .call();
            } else {
                git.checkout()
                        .setCreateBranch(true)
                        .setName(branchName)
                        .call();
            }


            for (String filePath : filePaths) {
                git.add()
                        .addFilepattern(filePath)
                        .call();
            }

            git.commit()
                    .setMessage("Added/modified files to new branch: " + branchName)
                    .call();

            CredentialsProvider credentialsProvider =
                    new UsernamePasswordCredentialsProvider(token, "");


            git.push()
                    .setRemote(remoteRepoUrl)
                    .setRefSpecs(new RefSpec(branchName + ":" + branchName))
                    .setCredentialsProvider(credentialsProvider)
                    .call();


            System.out.println("Successfully created branch '" + branchName +
                    "' and pushed it to remote with the specified files.");

        } catch (GitAPIException | IOException e) {
            System.err.println("Error creating branch: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public Map<String, Set<String>> getGitStatus() throws IOException, GitAPIException {
        Map<String, Set<String>> result = new HashMap<>();

        try (Git git = Git.open(new File(localGitRepoPath))) {
            Status status = git.status().call();

            Set<String> modifiedFiles = status.getModified();
            result.put("modified", modifiedFiles);

            Set<String> untrackedFiles = status.getUntracked();
            result.put("new", untrackedFiles);

            Set<String> addedFiles = status.getAdded();
            result.put("added", addedFiles);

            Set<String> deletedFiles = status.getRemoved();
            result.put("deleted", deletedFiles);

            return result;
        }
    }

}
