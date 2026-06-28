package processbuilderutilities;

import generalutilities.ReportAndLogging;
import generalutilities.TestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


/**
 * This class contains methods related to execute commands.
 * This class contains methods to perform operation on command line depending upon operating system Windows or Unix-like operating systems(Linux/macOS) on which the command will be executed.
 */

public class CommonProcessBuilderMethods {
    private static final Logger LOG = LoggerFactory.getLogger(CommonProcessBuilderMethods.class);
    ReportAndLogging reportAndLogging = new ReportAndLogging();
    private final TestContext<Object> testContext = TestContext.getInstance();


    /**
     * This method executes commands depends on the operating system Windows or Unix-like operating systems(Linux/macOS) on which the command will be executed.
     *
     * @param cmd                   -  the command as a String which will be executed and can be separated with using tilde sign.
     * @param osName                -  the operating system Windows or Unix-like operating systems(Linux/macOS) on which the command will be executed.
     * @return Map                  -  a Map containing the results of the command execution.
     * @throws IOException          -  an exception is thrown when there is an error while executing commands or reading command result.
     * @throws InterruptedException -  an exception is thrown when the current thread is interrupted by another thread while it is waiting. The exit value of the process represented by this Process object. By convention, the value 0 indicates normal termination.
     */
    public Map<String, Object> fireCommand(String cmd, String osName) throws IOException, InterruptedException {
        return execCommand(cmd, osName);
    }

    /**
     * This method executes a command and returns the results..
     *
     * @param cmd                   -  the command as a String which will be executed and can be separated with using tilde sign.
     * @return Map                  -  a Map containing the results of the command execution.
     * @throws IOException          -  an exception is thrown when there is an error while executing commands or reading command result.
     * @throws InterruptedException -  an exception is thrown when the current thread is interrupted by another thread while it is waiting. The exit value of the process represented by this Process object. By convention, the value 0 indicates normal termination.
     */
    public Map<String, Object> fireCommand(String cmd) throws IOException, InterruptedException {
        return execCommand(cmd, "");
    }


    /**
     * This method is used to executes a given command on a specified operating system. If the command execution is successful, the output is stored in TestContext and a success message is logged. If the command execution fails, an error message is logged and an assertion error is thrown.
     *
     * @param command               - The command to be executed.
     * @param osName                - The name of the operating system Windows or Unix-like operating systems(Linux/macOS) on which the command is to be executed. If null, the command is executed without specifying the operating system.
     * @throws IOException          - an exception is thrown when there is an error while executing commands or reading command result.
     * @throws InterruptedException - an exception is thrown when the current thread is interrupted by another thread while it is waiting. The exit value of the process represented by this Process object. By convention, the value 0 indicates normal termination.
     */
    public void processCommand(String command, String osName) throws IOException, InterruptedException {
        CommonProcessBuilderMethods commonProcessBuilderMethods = new CommonProcessBuilderMethods();
        Map<String, Object> outputStatus = osName == null ? commonProcessBuilderMethods.fireCommand(command) : commonProcessBuilderMethods.fireCommand(command, osName);

        String status = outputStatus.get("status").toString();
        if (!status.equals("0")) {
            LOG.error("Error occurred!! Command was not fired successfully");
            reportAndLogging.addStepToReport(" Error in executing command " + command,"WARN");
            reportAndLogging.logStepInJira(" Error in executing command " + command);
            assert false : "Command execution failed with status: " + status;
        } else {
            testContext.setProperty("PBfirecommand", outputStatus.get("output"));
            reportAndLogging.addStepToReport("Command " + command + " executed","INFO");
        }

    }
    /**
     * This method executes commands using Process and ProcessBuilder classes.
     *
     * @param cmd                   -  the command as a String which will be executed by using process and process builder classes and can be separated with using tilde sign.
     * @param osName                -  the operating system Windows or Unix-like operating systems(Linux/macOS) on which the command will be executed.
     * @return Map                  -  a Map containing the results of the command execution.
     * @throws IOException          -  an exception is thrown when there is an error while executing commands or reading command result.
     * @throws InterruptedException -  an exception is thrown when the current thread is interrupted by another thread while it is waiting. The exit value of the process represented by this Process object. By convention, the value 0 indicates normal termination.
     */
    public Map<String, Object> execCommand(String cmd, String osName) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        if (osName.isEmpty()) {
            if (System.getProperty("os.name").contains("Windows"))
                osName = "cmd.exe~/c";
            else
                osName = "sh~-c";
        }
        String[] osNameArray = osName.split("~");
        command.add(osNameArray[0]);
        command.add(osNameArray[1]);
        command.add(cmd);
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);

        // Redirect output and error streams
        builder.redirectErrorStream(true);
        Process process = builder.start();

        // Read the output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> outputReader = executor.submit(() -> {
            try {
                String read;
                while ((read = reader.readLine()) != null) {
                    output.append(read).append("\n");
                }
            } catch (IOException e) {
                LOG.error("Error reading output", e);
            }
        });

        boolean finished = process.waitFor(10, TimeUnit.SECONDS); // Timeout for the process
        if (!finished) {
            LOG.info("Process timed out. Destroying process forcefully...");
            process.destroyForcibly();
        }

        try {
            outputReader.get(2, TimeUnit.SECONDS); // Wait for output to finish
        } catch (ExecutionException | TimeoutException e) {
            throw new IOException("Error while reading process output",e);
        }
        executor.shutdownNow();


        int exitCode = process.waitFor();

        LOG.info("Command output : {}",output);
        LOG.info("Process exited with code: {}", exitCode);
        HashMap<String, Object> outputStatus = new HashMap<>();
        outputStatus.put("output", output.toString());
        outputStatus.put("status", exitCode);
        return outputStatus;
    }

}