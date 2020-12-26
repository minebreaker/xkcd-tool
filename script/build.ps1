# %comspec% /k "C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\VC\Auxiliary\Build\vcvars64.bat"
$Env:Path += ";F:\lib\graalvm-ce-java11-20.3.0\bin"
native-image `
    --no-server `
    --no-fallback `
    --allow-incomplete-classpath `
    -jar "../xkcd-tool-cli/build/libs/xkcd-tool-cli-1.0-SNAPSHOT.jar"
