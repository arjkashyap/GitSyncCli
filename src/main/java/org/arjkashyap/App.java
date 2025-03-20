package org.arjkashyap;


import picocli.CommandLine;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "gitsync", mixinStandardHelpOptions = true, version = "checksum 4.0",
        description = "Prints the checksum (SHA-256 by default) of a file to STDOUT.")
public class App implements Callable {
    @CommandLine.Option(names = {"-u", "--username"}, description = "git username")
    private String username;

    @CommandLine.Option(names = {"-p", "--password"}, description = "git password")
    String password;

    @CommandLine.Option(names = {"-t", "--token"}, description = "git authentication token")
    String token;

    @CommandLine.Option(names = {"-r", "--repo"}, description = "git remote repo")
    String remoteRepoUrl = "https://github.com/arjkashyap/erlic.ai.git";

    @CommandLine.Option(names = {"-lr", "--localrepo"}, description = "git local repo")
    String localGitRepoPath;

    @CommandLine.Option(names = {"-b", "--branch"}, description = "git branch default - develop", defaultValue = "develop")
    String branchName = "develop";

    @CommandLine.Option(names = {"-tf", "--tokenfile"}, description = "git authentication token file")
    String tokenFilePath;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);

    }

    @Override
    public Object call()  {
        String validationError = validateInputs();
        if (validationError != null) {
            System.err.println(validationError);
            return 1;
        }

        GitClient gitClient = new GitClient(username,password,token,remoteRepoUrl,localGitRepoPath,branchName);
        try {
            Map<String, Set<String>> files = gitClient.getGitStatus();
            Set<String> modified = files.get("modified");

            if (files.isEmpty()) {
                System.out.println("No changes to push.");
                return 0;
            }

            for (String f : modified) System.out.println("Modified: " + f);
            System.out.println();
            Set<String> newFiles = files.get("new");
            for (String file : newFiles) System.out.println("New: " + file);

            System.out.println("\nDo you want to push these changes? [Y]es/[N]o (default: Yes)");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine().trim();
            if (input.isEmpty() || input.equalsIgnoreCase("Y") || input.equalsIgnoreCase("Yes")) {
                gitClient.pushToRemote();
            } else {
                System.out.println("Push cancelled.");
            }

        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        return 0;
    }

    private String validateInputs() {
        // Authentication validation - need at least one auth method
        if ((username == null || password == null) && token == null && tokenFilePath == null) {
            return "Error: Authentication information is missing. Please provide either:\n" +
                    "  - Both username (-u) and password (-p)\n" +
                    "  - A token (-t)\n" +
                    "  - A token file path (-tf)";
        }

        if (localGitRepoPath == null) {
            return "Error: Local repository path (-lr, --localrepo) is required";
        }

        // Check if the local repo is actually a git repository
        File gitDir = new File(localGitRepoPath + "/.git");
        if (!gitDir.exists() || !gitDir.isDirectory()) {
            return "Error: The specified path does not appear to be a git repository: " + localGitRepoPath;
        }

        // All validations passed
        return null;
    }
}
