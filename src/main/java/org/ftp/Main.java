package org.ftp;

import java.io.FileInputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws Exception{

        FileInputStream fileInputStream = new FileInputStream("config\\config.properties");
        Properties properties = new Properties();
        properties.load(fileInputStream);

        FtpFunction ftpFunction = new FtpFunction();

        ftpFunction.establishConnection
                 (properties.getProperty("hostName"),
                 Integer.parseInt(properties.getProperty("port")),
                 properties.getProperty("username"),
                 properties.getProperty("password"));

        ftpFunction.uploadAllFiles(properties.getProperty("localPath"),
                                            properties.getProperty("serverPath"),
                                            Boolean.parseBoolean(properties.getProperty("deleteAfterExecution")));

        ftpFunction.executeFtpConnection();

    }
}