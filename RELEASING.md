# Releasing

This repository uses automated, tag-driven releases powered by the industry-standard `me.modmuss50.mod-publish-plugin` (created and maintained by the Fabric development team).

## Prerequisites

- Java 21 installed locally.
- A clean `main` branch.
- GitHub repository settings (configured in Settings > Secrets and variables > Actions):
  - Secret: `MODRINTH_TOKEN`
  - Variable (or secret): `MODRINTH_PROJECT_ID`

If Modrinth credentials are not configured yet, the release workflow still builds and publishes GitHub Releases safely.

## Release steps

1. Update `mod_version` in `gradle.properties` (e.g. `0.1.0-beta.1`).
2. Update `CHANGELOG.md`:
   - Create a section matching `## [X.Y.Z] - YYYY-MM-DD`.
   - The release pipeline automatically parses and extracts this section into GitHub Releases and Modrinth changelogs.
3. Commit and push changes to `main`:

   ```bash
   git add gradle.properties CHANGELOG.md
   git commit -m "chore(release): bump version to X.Y.Z"
   git push origin main
   ```

4. Create and push the release tag:

   ```bash
   git tag vX.Y.Z
   git push origin vX.Y.Z
   ```

5. Monitor `.github/workflows/release.yml` on GitHub Actions.

## Local Testing (Dry Run)

You can preview the release configuration locally without uploading to Modrinth or GitHub:

```bash
./gradlew publishMods
```

## What the workflow does

- Runs `./gradlew publishMods -Pmod_version=X.Y.Z`.
- Automatically extracts the version's release notes from `CHANGELOG.md`.
- Automatically determines release type (`ALPHA`, `BETA`, or `STABLE`).
- Packages and attaches the mod `.jar` and `-sources.jar`.
- Publishes to GitHub Releases and Modrinth (for Fabric `1.21.1`).

