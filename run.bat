@echo off
REM Run script for ShapeShift JavaFX Application
REM This script compiles and runs the application

echo Compiling project...
if exist target\classes rmdir /s /q target\classes
mkdir target\classes

REM Compile module-info.java first
javac -d target\classes --module-path "C:\Users\amirr\.m2\repository\org\openjfx\javafx-controls\21.0.6\javafx-controls-21.0.6.jar;C:\Users\amirr\.m2\repository\org\openjfx\javafx-fxml\21.0.6\javafx-fxml-21.0.6.jar" src\main\java\module-info.java

REM Compile all Java files
javac -d target\classes --module-path "C:\Users\amirr\.m2\repository\org\openjfx\javafx-controls\21.0.6\javafx-controls-21.0.6.jar;C:\Users\amirr\.m2\repository\org\openjfx\javafx-fxml\21.0.6\javafx-fxml-21.0.6.jar" -cp target\classes src\main\java\com\example\demo\*.java

echo.
echo To run the application, use Maven:
echo   mvn javafx:run
echo.
echo Or install Maven and run:
echo   mvn clean javafx:run
echo.

