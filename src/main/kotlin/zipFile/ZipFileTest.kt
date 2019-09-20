package zipFile

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

//******Target******
//Thoughtworks
//        departments
//            AU
//            HuaWei
//        summary

fun main() {
    println("Demo to show how to zip files under defined structure")

    Demo().run()
}

class Demo {

    private fun createZipOutputStream(tw: Thoughtworks) {
        println("Before create zip file...")
        val zipFile = File("sample.zip")
        val baos = ByteArrayOutputStream()
//        val zos = ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile)))
        val zos = ZipOutputStream(baos)
        constructZipEntry(tw.summary, zos)
        constructZipEntry(tw.departments, zos)
        zos.close()
        val fos = FileOutputStream(zipFile)
        fos.write(baos.toByteArray())
        fos.close()
        println("Zip file created...")
    }

    private fun constructZipEntry(inputStreamResources: List<InputStreamResource>, zos: ZipOutputStream) {
        val departmentStr = "department"
        inputStreamResources.forEach { constructZipEntry(it, zos, departmentStr) }
    }

    private fun constructZipEntry(
        inputStreamResource: InputStreamResource,
        zos: ZipOutputStream,
        baseFolderName: String? = null
    ) {
        val fileName = baseFolderName?.let { "$it/${inputStreamResource.name}" } ?: inputStreamResource.name
        val ze = ZipEntry(fileName)
        zos.putNextEntry(ze)

        inputStreamResource.inputStream.run {
            //            var bufferedStream = BufferedInputStream(this)
            zos.write(this.readAllBytes())
            this.close()
        }
        zos.closeEntry()
    }

    fun run() {
        val thoughtworks = Thoughtworks().apply {
            departments = listOf(
                InputStreamResource("AU.txt", "Something related to AU".byteInputStream()),
                InputStreamResource("HuaWei.txt", "Work work work until god see you!".byteInputStream())
            )
            summary = InputStreamResource("summary.txt", "summary list here".byteInputStream())
        }
        createZipOutputStream(thoughtworks)
    }
}


class Thoughtworks {
    lateinit var departments: List<InputStreamResource>
    lateinit var summary: InputStreamResource
}

class InputStreamResource(
    val name: String,
    val inputStream: InputStream
)
