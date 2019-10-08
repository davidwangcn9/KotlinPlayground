package aws

import com.amazonaws.AmazonClientException
import com.amazonaws.AmazonServiceException
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.ListObjectsRequest
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.UUID

fun main() {
    val sample = S3Sample()
    sample.run()
}

class S3Sample {
    fun run() {
        /*
                * Create your credentials file at ~/.aws/credentials (C:\Users\USER_NAME\.aws\credentials for Windows users)
                * and save the following lines after replacing the underlined values with your own.
                *
                * [default]
                * aws_access_key_id = YOUR_ACCESS_KEY_ID
                * aws_secret_access_key = YOUR_SECRET_ACCESS_KEY
                */

        val s3 = AmazonS3Client()
        //        ap-southeast-2
        val usWest2 = Region.getRegion(Regions.AP_SOUTHEAST_2)
        s3.setRegion(usWest2)

        val bucketName = "test-default-notification-service-files"
        val key = "abc/MyObjectKey_20190923"
        val offerRoundId = UUID.fromString("cb2c5faf-fda4-4b61-95f7-3d6793244033")
        val simulationId = UUID.fromString("ab390161-183a-4b8e-83a8-f7e306f0c6f4")
        val key1 = "abc/$offerRoundId/$simulationId"
        val key2 = "abc/$offerRoundId/$simulationId/readme.txt"

        println("===========================================")
        println("Getting Started with Amazon S3")
        println("===========================================\n")

        try {

            uploadFile(s3, bucketName, key2)

            downloadFile(s3, bucketName, key)

            checkExist(s3, bucketName, key1)
            listFiles(s3, bucketName)

            /*
                        * Delete an object - Unless versioning has been turned on for your bucket,
                        * there is no way to undelete an object, so use caution when deleting objects.
                        */
            //            System.out.println("Deleting an object\n");
            //            s3.deleteObject(bucketName, key);

            /*
             * Delete a bucket - A bucket must be completely empty before it can be
             * deleted, so remember to delete any objects from your buckets before
             * you try to delete them.
             */
            //            System.out.println("Deleting bucket " + bucketName + "\n");
            //            s3.deleteBucket(bucketName);
        } catch (ase: AmazonServiceException) {
            println(("Caught an AmazonServiceException, which means your request made it " + "to Amazon S3, but was rejected with an error response for some reason."))
            println("Error Message:    " + ase.message)
            println("HTTP Status Code: " + ase.statusCode)
            println("AWS Error Code:   " + ase.errorCode)
            println("Error Type:       " + ase.errorType)
            println("Request ID:       " + ase.requestId)
        } catch (ace: AmazonClientException) {
            println(
                ("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.")
            )
            println("Error Message: " + ace.message)
        }

    }

    private fun checkExist(s3: AmazonS3Client, bucketName: String, key: String) {
        val exist = s3.doesObjectExist(bucketName, key)
        println("Object under bucket : $bucketName with key $key exist: $exist")
    }

    private fun listFiles(s3: AmazonS3Client, bucketName: String) {
        println("Listing objects")
        val objectListing = s3.listObjects(
            ListObjectsRequest()
                .withBucketName(bucketName).withPrefix("abc1")
        )
        //                    .withPrefix("My"));
        for (objectSummary in objectListing.objectSummaries) {
            println(
                " - " + objectSummary.key + "  " +
                    "(size = " + objectSummary.size + ")"
            )
            println("Key: " + objectSummary.key)
            val `object` = s3.getObject(GetObjectRequest(bucketName, objectSummary.key))
            println("Content-Type: " + `object`.objectMetadata.contentType)
            displayTextInputStream(`object`.objectContent)
        }
    }

    private fun downloadFile(s3: AmazonS3Client, bucketName: String, key: String) {
        println("Downloading an object")
        val downloadObject = s3.getObject(GetObjectRequest(bucketName, key))
        println(
            String.format(
                "Get meta data{ myName : %s } ",
                downloadObject.objectMetadata.userMetadata["myName"]
            )
        )
        println("Content-Type: " + downloadObject.objectMetadata.contentType)
        displayTextInputStream(downloadObject.objectContent)
    }

    private fun uploadFile(s3: AmazonS3Client, bucketName: String, key: String) {
        println("Uploading a new object to S3 from a file\n")
        val metaData = ObjectMetadata()
        val dummy = "dummy"
        val fileInputStream = FileInputStream(createSampleFile())
        metaData.addUserMetadata("myName", "David")
        metaData.contentLength = fileInputStream.available().toLong()
        metaData.contentType = "application/octet-stream"
        metaData.contentDisposition = "attachment"
        metaData.sseAlgorithm = ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION
        metaData.userMetadata["test"] = (1..2000).map { "a" }.reduce { a, b -> a + b }
        s3.putObject(PutObjectRequest(bucketName, key, fileInputStream, metaData))
    }

    private fun createSampleFile(): File {
        val file = File("test.txt")
        file.deleteOnExit()

        val writer = OutputStreamWriter(FileOutputStream(file))
        writer.write("abcdefghijklmnopqrstuvwxyz\n")
        writer.write("01234567890112345678901234\n")
        writer.write("!@#$%^&*()-=[]{};':',.<>/?\n")
        writer.write("01234567890112345678901234\n")
        writer.write("abcdefghijklmnopqrstuvwxyz\n")
        writer.close()

        return file
    }

    private fun displayTextInputStream(input: InputStream) {
        val reader = BufferedReader(InputStreamReader(input))
        while (true) {
            val line = reader.readLine() ?: break

            println("    $line")
        }
        println()
    }
}
