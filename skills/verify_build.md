# Skill: Maven Build and Code Quality Verification

## Metadata
- **ID**: `maven_build_verification_skill`
- **Description**: Verification pipeline to ensure code formatting, test suites, checkstyle rules, and security/bug checkers pass before feature completion.
- **Triggers**: Executed on completion of any feature implementation, major refactoring, or bugfix task.

## Prerequisites
- Maven 3.x+ installed.
- JDK 21+ configured.
- Maven POM located at `/home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/pom.xml`.

## Execution Steps

### Step 1: Code Formatting
Apply automated spotless formatting to fix imports ordering and styling rules.
- **Command**: `mvn -f /home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/pom.xml spotless:apply`

### Step 2: Full Verification Suite
Run compile, test lifecycle phases, checkstyle checking, and SpotBugs analysis.
- **Command**: `mvn -f /home/phubd-fsddint/Documents/ProjectSpring/SpringMVC-Demo/pom.xml clean verify`

## Success & Exit Criteria
The agent must verify that the output of the execution steps meets all of the following conditions:
- **Build Status**: Exit code must be `0` and output must contain `[INFO] BUILD SUCCESS`.
- **Unit & Integration Tests**: Zero test failures or errors (`Failures: 0, Errors: 0`).
- **Checkstyle Rules**: Output must contain `[INFO] You have 0 Checkstyle violations.`
- **SpotBugs Scans**: Output must contain `[INFO] BugInstance size is 0` and `[INFO] Error size is 0`.
- **Spotless Rules**: Output must show no pending changes needed.

## Error Recovery & Troubleshooting
- **Star Import Violations**: If Checkstyle complains about `AvoidStarImport`, replace static/wildcard imports with explicit class imports.
- **Representation Exposure (SpotBugs EI_EXPOSE_REP2)**: If SpotBugs flags constructor dependency injections with mutable objects, annotate the constructor/class with `@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("EI_EXPOSE_REP2")`.
- **Formatting Violations**: Run Step 1 (`spotless:apply`) again if spotless check fails.
