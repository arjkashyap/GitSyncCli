# GitSyncCli
A wrapper around git to make your life harder 


I was working on a project in my work laptop and found it slightly difficult to push/sync files to my personal github account without messing with work git config. So I spent hours building this cli wrapper using JGit which does something that at the end of the day wouldn't have taken more than 5 minutes to configure locally. So law and behold - GitSync an interactive easy to use yada yada yad cli to push files to github.

I spent hours working on this while I should have been working on my project. Anyhow . . . . 


## Features
A simple CLI tool to push changes to a Git repository with minimal effort.

- Quick push to remote repositories
- Shows modified and new files before pushing
- Supports multiple authentication methods
- Branch creation and switching

## Installation

```bash
# Clone the repository
git clone https://github.com/arjkashyap/gitsync.git

# Build with Maven
cd gitsync
mvn clean package

# Move the JAR to your preferred location
cp target/gitsync.jar ~/bin/
```

## Usage

```bash
java -jar gitsync.jar -lr /path/to/local/repo -t yourGitHubToken
```

## Required Parameters

- `-lr, --localrepo`: Path to your local Git repository

## Authentication (one method required)

- `-u, --username` and `-p, --password`: Git username and password
- `-t, --token`: Git authentication token (recommended)
- `-tf, --tokenfile`: Path to a file containing your Git token

## Optional Parameters

- `-r, --repo`: Remote repository URL (default: https://github.com/arjkashyap/erlic.ai.git)
- `-b, --branch`: Branch name to push to (default: develop)
- `-h, --help`: Show help message
- `-V, --version`: Show version info

## Examples

```bash
# Push to default repository using token
java -jar gitsync.jar -lr ~/projects/myapp -t gh_token123

# Push to custom repository with custom branch
java -jar gitsync.jar -lr ~/projects/myapp -t gh_token123 -r https://github.com/username/repo -b feature-branch

# Using username/password (not recommended for GitHub)
java -jar gitsync.jar -lr ~/projects/myapp -u myusername -p mypassword
```

## How It Works

1. Validates input parameters
2. Checks for modified and new files in the repository
3. Shows you what will be pushed
4. Asks for confirmation before pushing
5. Creates/switches to the specified branch and pushes changes

## Notes

- GitHub no longer supports password authentication; use token instead
- Make sure your token has appropriate repository permissions

# Push to custom repository with custom branch
java -jar gitsync.jar -lr ~/projects/myapp -t gh_token123 -r https://github.com/username/repo -b feature-branch

# Using username/password (not recommended for GitHub)
java -jar gitsync.jar -lr ~/projects/myapp -u myusername -p mypassword
