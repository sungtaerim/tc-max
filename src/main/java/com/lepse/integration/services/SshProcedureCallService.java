package com.lepse.integration.services;

import com.jcraft.jsch.*;
import com.lepse.integration.controllers.ResponseMessages;
import org.junit.Assert;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * a service that provides a procedure call over an ssh connection
 */
public class SshProcedureCallService {

    private final String connectionPath;
    private final String password;

    private String username, host;
    private int port;

    private String matcherItem = "(.*?)(F4/F5-query\\s\\^G-retain)(.*?)(\\d{3}\\.?\\d{3,6})(.*?)"; // 4 group include paritem number
    private int groupItem = 4;
    private String matcherDoc = "(.*?)(EC\\d{6})(.*?)"; // 2 group include doc number
    private int groupDoc = 2;

    private String matcherDate = "(.*?)(\\d{2}\\?\\d{2}\\??\\d?)(.*?)";
    private int groupDate = 2;

    /**
     * creates a new instance of the ssh procedure call service
     *
     * @param connectionPath ssh connection string
     * @param password       ssh connection password
     */
    public SshProcedureCallService(String connectionPath, String password) {
        this.connectionPath = connectionPath;
        this.password = password;
    }

    /**
     * runs the command via ssh connection
     *
     * @return returns command execution result
     */
    public String run(String command) throws IOException, InterruptedException, JSchException {
        Session session = null;
        String response = "";

        try {
            session = openSshSession();
            response = executeCommand(session, command);

        } finally {
            if (session != null)
                session.disconnect();
        }
        return response;
    }

    private Session openSshSession() throws JSchException {
        parseConnectionPath();

        Session session = new JSch().getSession(username, host, port);

        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setConfig("-oKexAlgorithms", "+diffie-hellman-group1-sha1");

        session.connect();

        return session;
    }

    private void parseConnectionPath() {
        String[] connectionStringParts = connectionPath.split("[@:]");
        username = connectionStringParts[0];
        host = connectionStringParts[1];
        port = Integer.parseInt(connectionStringParts[2]);
    }

    private String executeCommand(Session session, String command) throws JSchException, InterruptedException {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024 * 1024)) {
            ChannelShell channel = (ChannelShell) session.openChannel("shell");
            channel.setOutputStream(outputStream);
            PrintStream stream = new PrintStream(channel.getOutputStream());
            channel.connect();

            stream.println(command);
            stream.flush();
            String response = waitForPrompt(outputStream);

            // file to driver -p
            if (command.contains(".rec")) {
                if (response.contains(ResponseMessages.INCORRECT_DATE.getMessage())) {
                    return "Извещение с датой ввода " + getMessage(response, matcherDate, groupDate) + " уже имеется для " + getMessage(response, matcherItem, groupItem);
                } else if (response.contains(ResponseMessages.ITEM_NOT_EXIST.getMessage())) {
                    return "Изделие не определено в системе. Используйте im40 для добавления";
                } else if (response.contains(ResponseMessages.CORRECT_OPERATION.getMessage())) {
                    return "Извещение " + getMessage(response, matcherDoc, groupDoc) + " для изделия " + getMessage(response, matcherItem, groupItem) + " добавлено";
                }
            }

            if (response.contains("**** line ")) {
                if (response.contains("format: im40")) {
                    return ResponseMessages.ERROR_IM40.getMessage();
                }
                if (response.contains("format: em42")) {
                    return ResponseMessages.ERROR_EM42.getMessage();
                }
            }

            if (response.contains(">>>>> All lines processed OK") ||
                    response.contains(">>>>> SOME LINES MARKED")) {
                return ResponseMessages.CORRECT_OPERATION.getMessage();
            }

            return response;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return "response";
    }

    private String waitForPrompt(ByteArrayOutputStream outputStream) throws InterruptedException {
        try {
            Thread.sleep(1000);
            String response = outputStream.toString("ISO-8859-5");
            return response;
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }

        return "Operation not complete. Try again";
    }

    private String getMessage(String response, String regex, int groupNumber) {
        Matcher matcher = Pattern.compile(regex).matcher(response);
        if (matcher.find()) {
            return matcher.group(groupNumber);
        }
        return "";
    }
}
