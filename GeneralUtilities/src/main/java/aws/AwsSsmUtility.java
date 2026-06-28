package aws;

import generalutilities.StringInterpolation;
import generalutilities.TestContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.AutoScalingGroup;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingGroupsResponse;
import software.amazon.awssdk.services.autoscaling.model.Instance;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.*;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithWebIdentityRequest;
import software.amazon.awssdk.services.sts.model.AssumeRoleWithWebIdentityResponse;
import software.amazon.awssdk.services.sts.model.Credentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;


/**
 * Utility class for AWS SSM operations.
 */
public class AwsSsmUtility {

    private static final Region region = Region.EU_CENTRAL_1;
    private static final Logger log = LoggerFactory.getLogger(AwsSsmUtility.class);
    private final TestContext<Object> testContext = TestContext.getInstance();
    private static final String AWS_SSM_UTILITY_COMMAND_OUTPUT = "awsSsmUtilityCommandOutput";
    private static final String SESSION_CREDENTIALS = "sessionCredentials";
    private static final String COMMAND_STATUS_SUCCESS = "isCommandStatusSuccess";
    private StringInterpolation stringInterpolation = new StringInterpolation();
    private static final String ERROR_CONTENT = "standardErrorContent";
    /**
     * Assumes a role for a given AWS account.
     *
     * @param roleName  The name of the role to assume, ex.ghe-role-assume-dev-poc
     * @param accountId The ID of the AWS account, ex.456693705152
     */
    public void assumeRoleForAccount(String roleName, String accountId) {
        try {
            String roleArn = String.format("arn:aws:iam::%s:role/%s", stringInterpolation.stringInterpolation(accountId), stringInterpolation.stringInterpolation(roleName));
            String audience = "sts.amazonaws.com";
            String idTokenRequestUrl = System.getenv("ACTIONS_ID_TOKEN_REQUEST_URL") + "&audience=" + audience;
            String idTokenRequestToken = System.getenv("ACTIONS_ID_TOKEN_REQUEST_TOKEN");
            String idToken = getIdToken(idTokenRequestUrl, idTokenRequestToken);

            AssumeRoleWithWebIdentityResponse stsResponse = assumeRoleWithWebIdentity(idToken, roleArn);
            assert stsResponse != null : "Assume Role With Web Identity Response is NULL";
            log.info("*** Assume Role Response Defined ***");
            Credentials myCreds = stsResponse.credentials();
            AwsSessionCredentials sessionCredentials = AwsSessionCredentials.create(
                    myCreds.accessKeyId(),
                    myCreds.secretAccessKey(),
                    myCreds.sessionToken()
            );

            testContext.setProperty(SESSION_CREDENTIALS, sessionCredentials);

            // Display the time when the temp creds expire:
            Instant exTime = myCreds.expiration();

            // Convert the Instant to readable date:
            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    .withLocale(Locale.US)
                    .withZone(ZoneId.systemDefault());

            formatter.format(exTime);
            log.info("The token expires on {}",  exTime);
        } catch (Exception e) {
            log.error("Exception on assumeRoleForAccount method: {} ", e.getMessage());
        }
    }

    /**
     * Executes a given command on an EC2 instance using AWS Systems Manager (SSM).
     *
     * @param command The command to be executed on the EC2 instance, ex.ls -la /home/ec2-user
     * @param ec2InstanceId The ID of the EC2 instance where the command will be executed, ex.i-0ccd00b817bde8770
     *
     */
    public void executeCommandOnEC2Instance(String command, String ec2InstanceId) {
        log.info("Command [ {} ]  will be executed on [ {} ] Instance ", command, (String)stringInterpolation.stringInterpolation(ec2InstanceId)); //NOSONAR
        AwsSessionCredentials sessionCredentials = (AwsSessionCredentials) testContext.getProperty(SESSION_CREDENTIALS);
        try {
            SsmClient ssmClient = SsmClient.builder()
                    .region(region)
                    .credentialsProvider(StaticCredentialsProvider.create(sessionCredentials))
                    .build();

            Map<String, List<String>> parameters = new HashMap<>();
            parameters.put("commands", List.of(command));

            SendCommandRequest sendCommandRequest = SendCommandRequest.builder()
                    .instanceIds((String)stringInterpolation.stringInterpolation(ec2InstanceId))
                    .documentName("AWS-RunShellScript")
                    .parameters(parameters)
                    .build();

            SendCommandResponse sendCommandResponse = ssmClient.sendCommand(sendCommandRequest);
            String commandId = sendCommandResponse.command().commandId();

            GetCommandInvocationResponse commandInvocationResponse = getCommandInvocationResponse(ssmClient, commandId, (String)stringInterpolation.stringInterpolation(ec2InstanceId));
            String commandInvocationResponseToString = commandInvocationResponse.toString();
            log.info("Command Invocation Response As String : {}", commandInvocationResponseToString);
            testContext.setProperty("commandInvocationResponseToString", commandInvocationResponseToString);

            String standardOutputContent = commandInvocationResponse.standardOutputContent();
            StringBuilder commandOutput = new StringBuilder();
            if (!Objects.equals(standardOutputContent, "")) {
                commandOutput.append(standardOutputContent);
                log.info("Standard Output Content : {}", standardOutputContent);
            }
            String standardErrorContent = commandInvocationResponse.standardErrorContent();
            String commandStatus = commandInvocationResponse.statusAsString();
            int responseCode = commandInvocationResponse.responseCode();


            if (responseCode != 0 || "Failed".equalsIgnoreCase(commandStatus)) {
                commandOutput.append(standardErrorContent);
                log.info("Standard Error Content : {}", standardErrorContent);
                testContext.setProperty(COMMAND_STATUS_SUCCESS, false);
                testContext.setProperty(ERROR_CONTENT, standardErrorContent);
            } else {
                testContext.setProperty(COMMAND_STATUS_SUCCESS, true);
                if (!Objects.equals(standardErrorContent, "")) {
                    log.warn("Non-critical stderr messages: {}", standardErrorContent);
                    testContext.setProperty(ERROR_CONTENT, standardErrorContent);
                }
            }


            log.info("User Command Execution Output (Standard Output&Error Content): {}", commandOutput);
            testContext.setProperty(AWS_SSM_UTILITY_COMMAND_OUTPUT, commandOutput.toString());
            ssmClient.close();

        } catch (InterruptedException e) {
            log.error("InterruptedException on executeCommandOnEC2Instance method: {} ", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Executes a given command on instances of Auto Scaling Group using AWS Systems Manager (SSM).
     *
     * @param command The command to be executed on the EC2 instance, ex.ls -la /home/ec2-user
     * @param autoScalingGroupName The name of the Auto Scaling Group, ex.github-test-group
     *
     */
    public void executeCommandOnTheAutoScalingGroup(String command, String autoScalingGroupName) {
        log.info("Command will be executed on ONE Instance of the Auto Scaling Group [ {} ] ", autoScalingGroupName);
        AwsSessionCredentials sessionCredentials = (AwsSessionCredentials) testContext.getProperty(SESSION_CREDENTIALS);

        try {
            AutoScalingClient autoScalingClient = AutoScalingClient.builder()
                    .region(region)
                    .credentialsProvider(StaticCredentialsProvider.create(sessionCredentials))
                    .build();

            DescribeAutoScalingGroupsRequest request = DescribeAutoScalingGroupsRequest.builder()
                    .autoScalingGroupNames(autoScalingGroupName)
                    .build();

            DescribeAutoScalingGroupsResponse response = autoScalingClient.describeAutoScalingGroups(request);

            List<AutoScalingGroup> autoScalingGroups = response.autoScalingGroups();

            if (!autoScalingGroups.isEmpty()) {
                AutoScalingGroup group = autoScalingGroups.get(0);

                List<Instance> instances = group.instances();

                if (!instances.isEmpty()) {
                    log.info("[ {} ] Instance(s) found on the Auto Scaling Group [ {} ]", instances.size(),  autoScalingGroupName);
                    for (int i=0; i<instances.size(); i++) {
                        String ec2InstanceId = instances.get(i).instanceId();
                        log.info("Instance ID : {} ", ec2InstanceId);
                        if (instances.get(i).healthStatus().equals("Healthy")) {
                            executeCommandOnEC2Instance(command, ec2InstanceId);
                            break;
                        } else if (i == instances.size()-1){
                            log.error("[ {} ] is LAST Instance and NOT Healthy Instance. NO HEALTHY INSTANCE found on the Auto Scaling Group [ {} ]", ec2InstanceId, autoScalingGroupName);
                        } else {
                            log.info("[ {} ] is NOT Healthy Instance, command execution will retry on next instance of the Auto Scaling Group [ {} ]", ec2InstanceId, autoScalingGroupName);
                        }
                    }

                } else {
                    log.error("NO INSTANCE found on the Auto Scaling Group [ {} ]", autoScalingGroupName);
                }
            } else {
                log.error("NO AUTO SCALING GROUP found with the name: {} ", autoScalingGroupName);
            }
            autoScalingClient.close();
        } catch (Exception e) {
            log.error("Exception on executeCommandOnTheAutoScalingGroup method: {} ", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }


    /**
     * Retrieves the command invocation response from SSM.
     *
     * @param ssmClient The SSM client.
     * @param commandId The ID of the command.
     * @param instanceId The ID of the EC2 instance.
     * @throws InterruptedException If the thread is interrupted while waiting for the command to finish executing.
     * @return The command invocation response from SSM.
     */
    public static GetCommandInvocationResponse getCommandInvocationResponse(SsmClient ssmClient, String commandId, String instanceId) throws InterruptedException {
        while (true) {
            try {
                GetCommandInvocationRequest commandInvocationRequest = GetCommandInvocationRequest.builder()
                        .commandId(commandId)
                        .instanceId(instanceId)
                        .build();

                GetCommandInvocationResponse commandInvocationResponse = ssmClient.getCommandInvocation(commandInvocationRequest);

                if (!commandInvocationResponse.status().equals(CommandInvocationStatus.IN_PROGRESS)) {
                    return commandInvocationResponse;
                }

            } catch (InvocationDoesNotExistException e) {
                log.info("Invocation DOES NOT EXIST yet. Retrying...");
            } catch (SdkClientException e) {
                log.error("SDK Client Exception Occurred on getCommandInvocationResponse method: {}", e.getMessage());
            }
            Thread.sleep(5000);
        }
    }

    /**
     * Retrieves the ID token for AWS STS.
     *
     * @param idTokenRequestUrl The URL to request the ID token.
     * @param idTokenRequestToken The token to request the ID token.
     * @throws IOException If an I/O error occurs.
     * @throws JSONException If a JSON error occurs.
     * @return The ID token.
     */
    private static String getIdToken(String idTokenRequestUrl, String idTokenRequestToken) throws IOException, JSONException {
        URL url = new URL(idTokenRequestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + idTokenRequestToken);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            JSONObject json = new JSONObject(content.toString());
            return json.getString("value");
        } finally {
            conn.disconnect();
        }
    }

    /**
     * Assumes a role with web identity.
     *
     * @param idToken The ID token.
     * @param roleArn The ARN of the role to assume.
     * @return The response from the AssumeRoleWithWebIdentity operation.
     */
    private static AssumeRoleWithWebIdentityResponse assumeRoleWithWebIdentity(String idToken, String roleArn) {
        try (StsClient stsClient = StsClient.builder().region(region).build()) { //NOSONAR
            AssumeRoleWithWebIdentityRequest request = AssumeRoleWithWebIdentityRequest.builder()
                    .roleArn(roleArn)
                    .roleSessionName("GitHub")
                    .webIdentityToken(idToken)
                    .durationSeconds(3600)
                    .build();
            log.info("*** Assume Role Request Defined ***");
            return stsClient.assumeRoleWithWebIdentity(request);
        } catch (Exception e){
            log.error("Exception on assumeRoleWithWebIdentity method: {} ", e.getMessage());
            return null;
        }
    }

    /**
     * Gets command output (Standard Output and Error Content) which has been already stored to TestContext.
     *
     * @return Command Output as a String.
     */
    public String getAwsSsmUtilityCommandOutput(){
        return (String) testContext.getProperty(AWS_SSM_UTILITY_COMMAND_OUTPUT);
    }

    /**
     * Gets command status which has been already stored to TestContext.
     *
     * @return boolean value indicating whether the user command execution was successful or not.
     */
    public boolean getAwsSsmUtilityCommandExecutionStatus(){
        return (boolean) testContext.getProperty(COMMAND_STATUS_SUCCESS);
    }

    /**
     * Gets command invocation response which has been already stored to TestContext.
     *
     * @return command invocation response as a String.
     */
    public String getCommandInvocationResponseAsString(){
        return (String) testContext.getProperty("commandInvocationResponseToString");
    }


    /**
     * Gets standard error content which has been already stored to TestContext.
     *
     * @return standard error content as a String.
     */
    public String getStandardErrorContent(){
        return (String) testContext.getProperty(ERROR_CONTENT);
    }
}
