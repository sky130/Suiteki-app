package ml.sky233.choseki.util

import android.util.Log
import ml.sky233.suiteki.MainApplication.Companion.context
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


object ZipUtils {

//    fun unZipFile(zipPath: String, unzipPath: String): Boolean {
//        return try {
//            var file = File(unzipPath)
//            if (!file.exists()) file.mkdirs()
//            val inputStream: InputStream = FileInputStream(zipPath)
//            val zipInputStream = ZipInputStream(inputStream)
//            var zipEntry = zipInputStream.nextEntry
//            val buffer = ByteArray(1024 * 1024)
//            var count = 0
//            while (zipEntry != null) {
//                if (!zipEntry.isDirectory) {
//                    var fileName = zipEntry.name
//                    fileName = fileName.substring(fileName.lastIndexOf("/") + 1)
//                    file = File(unzipPath + File.separator + fileName)
//                    file.createNewFile()
//                    val fileOutputStream = FileOutputStream(file)
//                    while (zipInputStream.read(buffer)
//                            .also { count = it } > 0
//                    ) fileOutputStream.write(buffer, 0, count)
//                    fileOutputStream.close()
//                }
//                zipEntry = zipInputStream.nextEntry
//            }
//            zipInputStream.close()
//            true
//        } catch (e: Exception) {
//            e.printStackTrace()
//            false
//        }
//    }

//    fun unZipFile(zipFile: String, targetFile: String) {
//        var outputStream: OutputStream? = null
//        var inputStream: InputStream? = null
//        try {
//            File(targetFile).run {
//                if (!exists()) {
//                    mkdirs()
//                }
//            }
//            val zf = ZipFile(zipFile)
//            val entries = zf.entries()
//            while (entries.hasMoreElements()) {
//                val zipEntry: ZipEntry = entries.nextElement()
//                val zipEntryName = zipEntry.name
//                inputStream = zf.getInputStream(zipEntry)
//                if (zipEntryName.startsWith("__MACOSX")) {
//                    continue
//                }
//                File(targetFile, zipEntryName).apply {
//                    if (isDirectory) {
//                        if (!exists()) {
//                            mkdirs()
//                        }
//                    } else {
//                        createNewFile()
//                        val fos = FileOutputStream(this).use { fos ->
//                            inputStream.use {
//                                val buffer = ByteArray(1024)
//                                var len: Int
//                                var total = 0
//                                while ((it.read(buffer).also { len = it }) != -1) {
//                                    fos.write(buffer, 0, len)
//                                    total += len
//                                }
//                                fos.flush()
//                            }
//                        }
//
//                    }
//                }
//                inputStream?.close()
//                outputStream?.close()
//            }
//            File(zipFile).delete()
//        } finally {
//            inputStream?.close()
//            outputStream?.close()
//        }
//    }

    fun unZipFile(zipFile: String, targetFile: String) {
        val webroot = File(targetFile)    //解压目录
        val webzip = File(zipFile).inputStream()    //压缩文件
        val zip = ZipInputStream(webzip.buffered())
        var entry = zip.nextEntry
        webroot.mkdir()
        while (entry != null) {
            val current = File("$webroot/${entry.name.replace("\\","/")}")
            Log.d("unzip","$webroot${entry.name.replace("\\","/")}")
            if (entry.isDirectory) {
                current.mkdirs()
            } else {
                current.parentFile?.mkdirs()
                zip.buffered().copyTo(current.outputStream())
            }
            entry = zip.nextEntry
        }
        zip.closeEntry()
        webzip.close()
    }

    fun zipFiles(id: String, op: String) {
        val inputDirectory = File(id)
        val outputZipFile = File(op)
        ZipOutputStream(BufferedOutputStream(FileOutputStream(outputZipFile))).use { zos ->
            inputDirectory.walkTopDown().forEach { file ->
                // Skip files with certain extensions
                if (file.isFile && file.extension == "zip") {
                    return@forEach
                }

                val zipFileName =
                    file.absolutePath.removePrefix(inputDirectory.absolutePath).removePrefix("/")
                val entry = ZipEntry("$zipFileName${(if (file.isDirectory) "/" else "")}")
                zos.putNextEntry(entry)
                if (file.isFile) {
                    file.inputStream().copyTo(zos)
                }
            }
        }
    }

}