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
        val key = "MyObjectKey"

        println("===========================================")
        println("Getting Started with Amazon S3")
        println("===========================================\n")

        try {
            /*
                        * Create a new S3 bucket - Amazon S3 bucket names are globally unique,
                        * so once a bucket name has been taken by any user, you can't create
                        * another bucket with that same name.
                        *
                        * You can optionally specify a location for your bucket if you want to
                        * keep your data closer to your applications or users.
                        */
            //            System.out.println("Creating bucket " + bucketName + "\n");
            //            s3.createBucket(bucketName);

            /*
             * List the buckets in your account
             */
            //            System.out.println("Listing buckets");
            //            for (Bucket bucket : s3.listBuckets()) {
            //                System.out.println(" - " + bucket.getName());
            //            }
            //            System.out.println();

            /*
             * Upload an object to your bucket - You can easily upload a file to
             * S3, or upload directly an InputStream if you know the length of
             * the data in the stream. You can also specify your own metadata
             * when uploading to S3, which allows you set a variety of options
             * like content-type and content-encoding, plus additional metadata
             * specific to your applications.
             */
            println("Uploading a new object to S3 from a file\n")
            val metaData = ObjectMetadata()
            val dummy = "dummy"
            val fileInputStream = FileInputStream(createSampleFile())
            metaData.addUserMetadata("myName", "David")
            metaData.contentLength = fileInputStream.available().toLong()
            metaData.contentType = "application/octet-stream"
            metaData.contentDisposition = "attachment"
            metaData.sseAlgorithm = ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION
            s3.putObject(PutObjectRequest(bucketName, key, fileInputStream, metaData))
//            s3.putObject(PutObjectRequest(bucketName, key, ByteArrayInputStream(dummy.toByteArray()), metaData))

            /*
                        * Download an object - When you download an object, you get all of
                        * the object's metadata and a stream from which to read the contents.
                        * It's important to read the contents of the stream as quickly as
                        * possibly since the data is streamed directly from Amazon S3 and your
                        * network connection will remain open until you read all the data or
                        * close the input stream.
                        *
                        * GetObjectRequest also supports several other options, including
                        * conditional downloading of objects based on modification times,
                        * ETags, and selectively downloading a range of an object.
                        */
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

            /*
                        * List objects in your bucket by prefix - There are many options for
                        * listing the objects in your bucket.  Keep in mind that buckets with
                        * many objects might truncate their results when listing their objects,
                        * so be sure to check if the returned object listing is truncated, and
                        * use the AmazonS3.listNextBatchOfObjects(...) operation to retrieve
                        * additional results.
                        */
            println("Listing objects")
            val objectListing = s3.listObjects(
                ListObjectsRequest()
                    .withBucketName(bucketName)
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
            println()

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
