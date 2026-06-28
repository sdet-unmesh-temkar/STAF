package processbuilderutilities;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import generalutilities.*;
import org.apache.commons.io.IOUtils;
import java.util.HashMap;
import java.util.Map;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class perform operation's on CommonShellMethods.
 * This class contains common shell methods such as create session and fire jsch command(JSch is an SSH/SFTP client is used to throw an exception when anything goes wrong with the SSH server).
 */
public class CommonShellMethods {

    ReportAndLogging reportAndLogging = new ReportAndLogging();
    Map<String, String> environment = EnvironmentDataLoader.getInstance().getEnvironment();
    private static final Logger LOG = LoggerFactory.getLogger(CommonShellMethods.class);
    Map<String, Object> outputStatus = new HashMap<>();


    /**
     * This method is used to makes connectivity from int-man-02 to APRM server using jsch and it fires command on APRM Server. It takes argument for command which we need to fire on APRM server.
     *
     * @param command   -  fire unix command to make connectivity from int-man-02 to APRM server using JSch SSH/SFTP client
     * @return String  -  an execution result
     */
    public String command(String command) {
        var errOutput = "";
        if (environment.get("host") == null || environment.get("user") == null || environment.get("password") == null || environment.get("port") == null) {
            reportAndLogging.addStepToReport("Connection failed check env file missing host or user or possword or port","WARN");
            return null;
        }
        String host = environment.get("host");
        String user = environment.get("user");
        String password = EncryptDecrypt.decrypted(environment.get("password"));
        var port = Integer.parseInt(environment.get("port"));
        var output = "";
        LOG.trace("Command to be executed: {}", command);
        try {
            var session = createSession(user, host, port,password);
            var channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            var err = ((ChannelExec) channel).getErrStream();
            var in = channel.getInputStream();
            ((ChannelExec) channel).setPty(true);

            channel.connect();

            output = IOUtils.toString(in, StandardCharsets.UTF_8);
            errOutput = IOUtils.toString(err, StandardCharsets.UTF_8);
            if (channel.isClosed()) {
                LOG.info("exit-status: {}", channel.getExitStatus());
            }
            outputStatus.put("err Output", errOutput);
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            LOG.error("Connection failed to !! {} : {}", host, port);
            reportAndLogging.addStepToReport("Connection failed to !!" + host + " : " + port,"WARN");
            return null;
        }
        return output;
    }

    /**
     * This method is used to creating a session for server connection.
     *
     * @param user           - user to create a session
     * @param host           - create a session for server connection
     * @param port           - to create a session
     * @param password       - create a session to establish server connection
     * @return session       - created session for further usage
     * @throws JSchException - an exception is thrown when anything goes wrong with the SSH protocol
     */
    public Session createSession(String user,String host,int port,String password ) throws JSchException {

        var strHostKeyChecking = "StrictHostKeyChecking";
        var config = new java.util.Properties();
        config.put(strHostKeyChecking, "no");
        var jsch = new JSch();
        var session = jsch.getSession(user, host, port);
        session.setPassword(password);
        session.setConfig(strHostKeyChecking, "no");
        session.connect();
        return session;
    }

    /**
     * This method is used to connectivity from int-man-02 to APRM server using jsch and it fires command on APRM Server. It takes argument for command which we need to fire on APRM server.
     *
     * @param user           - to create a session for server connection
     * @param host           - create a session for server connection
     * @param port           - to create a session and establish server connection
     * @param password       - for creating session
     * @param command        - command which needs to be executed
     * @return Map           - outputStatus
     */
    public Map<String, Object> jschFireCommand(String host, String user, String password, int port, String command) {

        var output = "";
        var errOutput = "";
        LOG.trace("Command to be executed: {}", command);
        try {
            var session = createSession(user, host, port,password);
            var channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            var in = channel.getInputStream();
            var err = ((ChannelExec) channel).getErrStream();
            ((ChannelExec) channel).setPty(true);

            channel.connect();

            output = IOUtils.toString(in, StandardCharsets.UTF_8);
            errOutput = IOUtils.toString(err, StandardCharsets.UTF_8);
            if (channel.isClosed()) {
                LOG.info("exit-status: {}", channel.getExitStatus());

            }
            Thread.sleep(2000);
            outputStatus.put("status", channel.getExitStatus());
            outputStatus.put("output", output);
            outputStatus.put("err Output", errOutput);
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            LOG.error("Connection failed to !! {} : {}", host, port);
            reportAndLogging.addStepToReport("Connection failed to !!" + host + " : " + port,"WARN");
            Thread.currentThread().interrupt();
            return Collections.emptyMap();
        }
        return outputStatus;
    }
}
