# Contributing

Thanks for contributing to Chunk Deeds.

## Local setup

1. Install Java 21.
2. From the repository root, run:

   ```bash
   ./gradlew clean build
   ```

This validates compilation, packaging, and any tests present.

## Pull request expectations

- Keep changes focused and minimal.
- Ensure `./gradlew clean build` passes before opening/updating a PR.
- Update `CHANGELOG.md` for user-visible changes.
- Include a clear PR description with testing notes.
