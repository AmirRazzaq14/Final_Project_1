# How to Run the Application

## Option 1: Using Maven (Recommended)

If you have Maven installed:

```bash
mvn clean javafx:run
```

Or compile first, then run:

```bash
mvn clean compile
mvn javafx:run
```

## Option 2: Using IDE Run Configuration

### VS Code / Cursor

1. Open the project in VS Code/Cursor
2. Go to Run and Debug (F5)
3. Select "Run HelloApplication" configuration
4. Click Run

### IntelliJ IDEA

1. Right-click on `Launcher.java`
2. Select "Run 'Launcher.main()'"
3. Or create a run configuration:
   - Main class: `com.example.demo.Launcher`
   - VM options: `--module-path <path-to-javafx-libs> --add-modules javafx.controls,javafx.fxml`

## Option 3: Manual Compilation and Run

If Maven is not available, you can compile manually:

1. **Compile the module:**
   ```bash
   javac -d target/classes --module-path <javafx-jars> src/main/java/module-info.java
   ```

2. **Compile Java files:**
   ```bash
   javac -d target/classes --module-path <javafx-jars> src/main/java/com/example/demo/*.java
   ```

3. **Run the application:**
   ```bash
   java --module-path <javafx-jars>:target/classes --add-modules javafx.controls,javafx.fxml -m com.example.demo/com.example.demo.Launcher
   ```

## Troubleshooting

### Module Not Found Error

If you see "Module com.example.demo not found":
- Make sure `module-info.class` is compiled in `target/classes/`
- Ensure all dependencies are on the module path
- Try using `Launcher` class instead of `HelloApplication` directly

### Firebase Dependencies Not Found

If Firebase modules are not found:
- The app will automatically fall back to local storage for authentication
- This is expected behavior if Firebase service account key is not configured
- See `FIREBASE_SETUP.md` for Firebase configuration

### JavaFX Not Found

If JavaFX modules are not found:
- Download JavaFX SDK from https://openjfx.io/
- Extract and add to module path
- Or use Maven which handles dependencies automatically

## Quick Start (Without Maven)

1. Install Maven from https://maven.apache.org/download.cgi
2. Add Maven to your PATH
3. Run: `mvn clean javafx:run`

This is the easiest way to run the application!

