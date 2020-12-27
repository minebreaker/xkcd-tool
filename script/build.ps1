$Env:Path += ";F:\lib\graalvm-ce-java11-20.3.0\bin"
native-image `
    --no-server `
    --no-fallback `
    --allow-incomplete-classpath `
    -jar "../xkcd-tool-cli/build/libs/xkcd-tool-cli-1.0-SNAPSHOT.jar"
