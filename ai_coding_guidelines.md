# AI Coding Guidelines for LiteTube

When writing, editing, or analyzing code for **LiteTube** (a fork/extension of NewPipe), all AI agents MUST adhere to these strict coding guidelines to ensure CI/CD builds pass, architectural consistency is maintained, and open-source policies comply with the project standards.

## 1. Strict Checkstyle and Formatting Rules
The project uses automated Checkstyle validation (for Java) and Ktlint (for Kotlin). Failure to adhere to these rules will instantly break the build. Ensure you review these before writing code.

* **Final Modifier Requirement**: All method parameters and local variables **MUST** be explicitly declared as `final` wherever possible. This is the most common cause of Checkstyle failures.
    * *Incorrect*: `public void doWork(String arg) { int i = 5; }`
    * *Correct*: `public void doWork(final String arg) { final int i = 5; }`
* **Line Length Limit**: No line of code may exceed **100 characters**.
    * If a method call, signature, or string approaches 100 lines, use proper indentation wrapping on the next line. 
    * When importing libraries with long class names, ensure method chaining wraps appropriately.
* **Trailing Whitespace**: Absolutely no trailing spaces on any lines, including empty/blank lines.
* **Imports**: Do not use wildcard imports (e.g., `import java.util.*;`). Always specify the exact class path. Remove any unused imports.

## 2. Project Architecture & Components
* **Fragments**: New screens should typically extend `BaseFragment`. If they require intercepting Android's back navigation, they must implement the `BackPressable` interface.
* **Navigation**: All transitions between Activities or Fragments must be proxied through `NavigationHelper`. 
    * Example: Launching a video link is done using `NavigationHelper.getIntentByLink(context, url);` followed by starting the intent.
* **Video/Audio Player**: Handled by ExoPlayer through background services. Avoid writing duplicate media players or interfering directly with default playback threads. Interact using custom Intents or MediaSession commands.

## 3. General Rules & Best Practices
* **No Closed-Source Blobs**: Since this project targets F-Droid inclusion, never introduce proprietary closed-source SDKs (especially avoid Google Play Services libraries or SDKs that gather tracking telemetry).
* **AI Code Policy**: 
  - Ensure generated code strictly aligns with the existing project structure. Read surrounding implementations rather than throwing generic solutions.
  - Fix the root cause of an issue rather than patching the symptoms. 
  - Keep PRs targeted and focused on a single change/feature.
* **Localization**: Hardcoded strings should be avoided in UI code. Add and reference standard strings inside `res/values/strings.xml`.

By reading and adhering to these points, you guarantee that AI-assisted code contributions are mergeable, robust, and cleanly integrated.
