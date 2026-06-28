package aws;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * This class contain S3Bucket related method such as s3Upload and s3Download.
 */
public class S3Bucket {
    private static final Logger LOG = LoggerFactory.getLogger(S3Bucket.class);

    /**
     * This method is used to s3 upload.
     *
     * @param bucketName - to S3 upload
     * @param filePath   - path of the file to s3 upload
     */
    public void s3Upload(String bucketName, String filePath) {
        var keyName = Paths.get(filePath).getFileName().toString();
        LOG.info("Uploading {} to S3 bucket {}...\n", filePath, bucketName);
        final S3Client s3 = S3Client.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            s3.putObject(request, RequestBody.fromFile(Paths.get(filePath)));
        } catch (S3Exception e) {
            LOG.error(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        LOG.info("Uploading DONE!!");
    }

    /**
     * This method is used to S3 download.
     *
     * @param args - it stores java command-line arguments and is an array type
     */
    public void s3Download(String[] args) {
        final String USAGE = """
                To run this example, supply the name of an S3 bucket and object to
                download from it.
                
                Ex: GetObject <bucketname> <filename>
                """;

        if (args.length < 2) {
            LOG.info(USAGE);
            System.exit(1);
        }
        String bucketName = args[0];
        String keyName = args[1];
        LOG.info("Downloading {} from S3 bucket {}...\n", keyName, bucketName);
        final S3Client s3 = S3Client.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            ResponseInputStream<GetObjectResponse> s3is = s3.getObject(request);

            var fos = new FileOutputStream(keyName);
            var readBuf = new byte[1024];
            var readLen = 0;
            while ((readLen = s3is.read(readBuf)) > 0) {
                fos.write(readBuf, 0, readLen);
            }
            s3is.close();
            fos.close();
        } catch (S3Exception e) {
            LOG.error(e.awsErrorDetails().errorMessage());
            System.exit(1);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            System.exit(1);
        }
        LOG.info("Downloading DONE!!");
    }
}
