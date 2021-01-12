native-image \
    --no-fallback \
    --allow-incomplete-classpath \
    --enable-url-protocols=https \
    -jar "../xkcd-tool-cli/build/libs/proguarded.jar" \
    "xkcd-linux"
