package stepDefinitions;

import aws.AwsSsmUtility;
import io.cucumber.java.en.When;

public class AwsSsmUtilitySteps {

    /**
     * Step definitions class for AWS SSM operations.
     */
    private final AwsSsmUtility awsSsmUtility = new AwsSsmUtility();

    /**
     * Assumes a specific role on a given AWS account.
     *
     * @param role         The role to be assumed on the AWS account, ex.ghe-role-assume-dev-poc
     * @param awsAccountId The AWS account ID on which the role is to be assumed, ex.456693705152
     */
    @When("AWS SSM - We assume the role {string} on the AWS Account {string}")
    public void assumeRole(String role, String awsAccountId) {
        awsSsmUtility.assumeRoleForAccount(role, awsAccountId);
    }


    /**
     * Executes a given command on a specified EC2 instance using AWS SSM.
     *
     * @param command       The command to be executed on the instance, ex.ls -la /home/ec2-user
     * @param ec2InstanceId The ID of the EC2 instance where the command will be executed, ex.i-0ccd00b817bde8770
     */
    @When("AWS SSM - We execute the command {string} on the instance {string}")
    public void executeCommandOnEC2Instance(String command, String ec2InstanceId) {
        System.out.println("**** User Command [ " + command + " ] will be executed on EC2 Instance [ " + ec2InstanceId + " ] ****" );
        awsSsmUtility.executeCommandOnEC2Instance(command, ec2InstanceId);
        assert awsSsmUtility.getCommandInvocationResponseAsString() != null  : "Command COULD NOT be EXECUTED on INSTANCE [" + ec2InstanceId + "]. To Debug please check LOG file. ";
        assert awsSsmUtility.getAwsSsmUtilityCommandExecutionStatus() : "**** User Command [" + command + "] Execution has thrown ERROR : ****\n" + awsSsmUtility.getStandardErrorContent() ;
    }

    @When("AWS SSM - We execute the command {string} on one instance of the auto scale group {string}")
    public void executeCommandOnOneInstanceOfTheAutoScalingGroup(String command, String autoScalingGroupName) {
        System.out.println("**** User Command [ " + command + " ] will be executed on ONE INSTANCE of the Auto Scaling Group [ " + autoScalingGroupName + " ] ****" );
        awsSsmUtility.executeCommandOnTheAutoScalingGroup(command, autoScalingGroupName);
        assert awsSsmUtility.getCommandInvocationResponseAsString() != null  : "Command COULD NOT be EXECUTED on ANY INSTANCE of the Auto Scaling Group [" + autoScalingGroupName + "]. To Debug please check LOG file. ";

    }
}