---
name: refer-ios-version
description: Reference the Kurogoma4D/ShiroGuessr-iOS repository using gh CLI commands. Use when the user mentions "iOS版" (iOS version), "現行プロジェクト" (current project), or needs to reference existing implementations from the iOS version of ShiroGuessr. Supports code search, keyword search, and file content retrieval.
---

# Refer iOS Version

Reference the iOS version of ShiroGuessr (`Kurogoma4D/ShiroGuessr-iOS` repository) using GitHub CLI.

## Repository Information

- **Repository**: `Kurogoma4D/ShiroGuessr-iOS`
- **Main Branch**: `main`
- **Purpose**: iOS version reference for implementation patterns and features

## Search Operations

### Code Search

Search for specific code patterns or implementations:

```bash
gh search code --repo Kurogoma4D/ShiroGuessr-iOS "search query"
```

**Examples:**

```bash
# Search for map-related code
gh search code --repo Kurogoma4D/ShiroGuessr-iOS "map"

# Search for API endpoints
gh search code --repo Kurogoma4D/ShiroGuessr-iOS "api"

# Search for configuration
gh search code --repo Kurogoma4D/ShiroGuessr-iOS "config"
```

### Keyword Search

Search across the entire repository:

```bash
gh search repos Kurogoma4D/ShiroGuessr-iOS
```

Or search issues/PRs for context:

```bash
gh search issues --repo Kurogoma4D/ShiroGuessr-iOS "keyword"
gh search prs --repo Kurogoma4D/ShiroGuessr-iOS "keyword"
```

### View File Contents

Retrieve specific file contents using the GitHub API:

```bash
gh api repos/Kurogoma4D/ShiroGuessr-iOS/contents/path/to/file
```

**Note:** The response includes a `content` field with base64-encoded file contents. Decode using:

```bash
gh api repos/Kurogoma4D/ShiroGuessr-iOS/contents/path/to/file --jq '.content' | base64 -d
```

**Examples:**

```bash
# Get package.json
gh api repos/Kurogoma4D/ShiroGuessr-iOS/contents/package.json --jq '.content' | base64 -d

# Get a specific component file
gh api repos/Kurogoma4D/ShiroGuessr-iOS/contents/src/components/Map.tsx --jq '.content' | base64 -d

# List directory contents
gh api repos/Kurogoma4D/ShiroGuessr-iOS/contents/src
```

**Alternative - Clone and read locally:**

For extensive reading of multiple files, clone the repository:

```bash
gh repo clone Kurogoma4D/ShiroGuessr-iOS /tmp/ShiroGuessr-iOS
```

Then use standard Read tool on local files.

## Usage Patterns

**When user asks about iOS version implementation:**

1. Identify the feature or component they're asking about
2. Use `gh search code` to find relevant implementations
3. Use `gh api` to retrieve specific file contents if needed
4. Summarize the findings with file paths and line references

**Example conversation:**

- User: "iOS版では地図の表示はどうやっているの？" (How is the map displayed in the iOS version?)
- Action: `gh search code --repo Kurogoma4D/ShiroGuessr-iOS "map display"`
- Then: Retrieve relevant file with `gh api repos/Kurogoma4D/ShiroGuessr-iOS/contents/src/...`

**When comparing iOS vs current implementation:**

1. First search the iOS version using gh commands
2. Then search the local project using Glob/Grep
3. Compare and highlight differences

**Example conversation:**

- User: "現行プロジェクトのconfig設定を見せて" (Show me the config settings in the current project)
- Action: `gh api repos/Kurogoma4D/ShiroGuessr-iOS/contents/config.json --jq '.content' | base64 -d`

## Tips

- Use `--jq` for JSON parsing and filtering
- Search results may be paginated; check for `next` links
- For binary files, the API returns a download URL instead of base64 content
- Clone the repository locally if reading many files to avoid API rate limits
