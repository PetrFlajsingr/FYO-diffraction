package cz.vutbr.fit.xflajs00.fyo

import java.io.File


fun getJarPath(): String {
    return File(Main::class.java.protectionDomain.codeSource.location
            .toURI()).path
}

fun getFilesInFolder(path: String): List<String> {
    val files = File(path).listFiles() ?: return emptyList()
    val result = MutableList(files.size) { "" }
    for (i in 0 until files.size) {
        result[i] = files[i].path
    }
    return result
}