package stepDefinitions;

import java.io.IOException;

import io.cucumber.java.en.When;
import processbuilderutilities.CommonProcessBuilderMethods;

/**
 * This class perform operations on CommonProcessBuilderMethods.
 * This class contains methods to perform operation on command line.
 */
public class ProcessBuilderStepDef {

    CommonProcessBuilderMethods commonProcessBuilderMethods = new CommonProcessBuilderMethods();

    /**
     * This method is used to executes a given command on a specified operating system. If the command execution is successful, the output is stored in TestContext and a success message is logged. If the command execution fails, an error message is logged and an assertion error is thrown.
     *
     * @param command The command to be executed.
     * @param osName  The name of the operating system Windows or Unix-like operating systems(Linux/macOS) on which the command is to be executed. If null, the command is executed without specifying the operating system.
     * @throws IOException          an exception is thrown when there is an error while executing commands or reading command result.
     * @throws InterruptedException an exception is thrown when the current thread is interrupted by another thread while it is waiting. The exit value of the process represented by this Process object. By convention, the value 0 indicates normal termination.
     */
    @When("ProcessBuilder - we execute the command {string} on the operating system {string}")
    public void fireCommand(String command, String osName) throws IOException, InterruptedException {
        commonProcessBuilderMethods.processCommand(command.trim(), osName);

    }

    /**
     * This method executes a command. The results of the command execution will be stored in a TestContext instance.
     *
     * @param command The command to be executed.
     * @throws IOException          an exception is thrown when there is an error while executing commands or reading command result.
     * @throws InterruptedException an exception is thrown when the current thread is interrupted by another thread while it is waiting. The exit value of the process represented by this Process object. By convention, the value 0 indicates normal termination.
     */
    @When("ProcessBuilder - we execute the command {string}")
    public void fireCommand(String command) throws IOException, InterruptedException {
        commonProcessBuilderMethods.processCommand(command.trim(), null);
    }
}