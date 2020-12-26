package rip.deadcode.xkcdtool.core

import java.nio.file.FileSystems


object Toolbox {
    var fileSystem = FileSystems.getDefault()
    var cachePath = fileSystem.getPath("~/xkcd-tool/index").normalize().toAbsolutePath()
}
