# Releasing

This repository uses tag-driven releases.

## Prerequisites

- Java 21 installed locally.
- A clean `main` branch.
- GitHub repository settings configured:
  - Secret: `MODRINTH_TOKEN`
  - Variable (or secret): `MODRINTH_PROJECT_ID`

If Modrinth credentials are not configured yet, the release workflow still succeeds and logs a skip notice.

## Release steps

1. Update `mod_version` in `gradle.properties`.
2. Update `CHANGELOG.md`:
   - Move changes from `Unreleased` to a new `X.Y.Z` section.
   - Keep entries concise and user-facing.
3. Commit and push changes to `main`.
4. Create and push the release tag:

   ```bash
   git tag vX.Y.Z
   git push origin vX.Y.Z
   ```

5. Wait for `.github/workflows/release.yml` to finish.

## What the workflow does

- Builds once with `./gradlew clean build`.
- Creates or updates a GitHub Release for the tag.
- Uploads built JAR artifacts from `build/libs`.
- Publishes to Modrinth for Fabric + Minecraft `1.21.1` when `MODRINTH_TOKEN` and `MODRINTH_PROJECT_ID` are configured.

## Verification checklist

- GitHub Actions release workflow succeeded.
- GitHub Release exists for `vX.Y.Z` and includes JAR artifacts.
- If Modrinth was configured, the new version appears on the Modrinth project page.
