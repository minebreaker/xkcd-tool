REM Run this file from Visual Studio Native Tools Command Propmpt

REM Add the graal path to PATH
REM set PATH=%PATH%;F:\lib\graalvm-ce-java11-20.3.0\bin

native-image ^
    --no-fallback ^
    --allow-incomplete-classpath ^
    --enable-url-protocols=https ^
    -jar "../xkcd-tool-cli/build/libs/proguarded.jar" ^
    "xkcd"
