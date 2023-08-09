package org.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FtpFunction{
    private static final FTPClient ftpClient = new FTPClient();

    public void establishConnection(String host, int port, String username, String password){

        try {
            ftpClient.connect(host, port);
            ftpClient.login(username, password);
        }catch (Exception e){
            System.out.println(e);
        }

        System.out.println(ftpClient.isConnected() ? "Connected to the FTP server." : "Unable To Connect To FTP Server");

    }

    public void uploadFile(String localFile, String workingDirectory, boolean deleteFromSource){

        try{
            ftpClient.changeWorkingDirectory(workingDirectory);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE); // Set the transfer mode to binary

            File fileToUpload = new File(localFile);
            FileInputStream inputStream = new FileInputStream(fileToUpload);

            boolean ftpFile = ftpClient.storeFile(fileToUpload.getName(), inputStream);

            System.out.println(ftpFile ? "File Uploaded Successfully" : "Unable to upload file");

            inputStream.close();

            if(ftpFile && deleteFromSource) {
                if (fileToUpload.delete()) {
                    System.out.println("Deleted " + fileToUpload.getName());
                } else {
                    System.out.println("Unable to delete");
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void uploadAllFiles(String localDirectory, String workingDirectory, boolean deleteFromSource){
        List<File> deletedFiles = new ArrayList<>();
        try{
            ftpClient.changeWorkingDirectory(workingDirectory);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE); // Set the transfer mode to binary

            // Get a list of files in the local directory
            File localDir = new File(localDirectory);
            List<File> fileList = Arrays.asList(localDir.listFiles());

            if (!fileList.isEmpty()) {
                for (File file : fileList) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        FileInputStream fis = new FileInputStream(file);

                        // Upload the file to the server
                        boolean uploaded = ftpClient.storeFile(fileName, fis);
                        fis.close();

                        if (uploaded) {
                            System.out.println("File " + fileName + " uploaded successfully.");
                            deletedFiles.add(file);
                        } else {
                            System.out.println("Failed to upload file " + fileName);
                        }
                    }
                }
            }

        }catch(Exception e){
            System.out.println(e);
        }finally {
            if(deleteFromSource)
                deleteAfter(deletedFiles);
        }
    }

    public void downloadFile(String remoteDirectory, String localDirectory){
        try{
            // Set file transfer mode to binary
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // Change the working directory on the server
            ftpClient.changeWorkingDirectory(remoteDirectory);

            // Get a list of files on the server
            String[] fileNames = ftpClient.listNames();

            if (fileNames != null) {
                for (String fileName : fileNames) {
                    String localFilePath = localDirectory + File.separator + fileName;
                    FileOutputStream fos = new FileOutputStream(localFilePath);

                    // Download the file from the server
                    boolean downloaded = ftpClient.retrieveFile(fileName, fos);
                    fos.close();

                    if (downloaded) {
                        System.out.println("File " + fileName + " downloaded successfully.");
                    } else {
                        System.out.println("Failed to download file " + fileName);
                    }
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public void moveInsideServer(String sourceFromServer, String fileNameToMove, String destinationDirectoryFromServer){
        try{
            // Set file transfer mode to binary
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // Change the working directory on the server to the source directory
            ftpClient.changeWorkingDirectory(sourceFromServer);

            // Move the file to the destination directory
            boolean moved = ftpClient.rename(fileNameToMove, destinationDirectoryFromServer + "/" + fileNameToMove);

            if (moved) {
                System.out.println("File " + fileNameToMove + " moved successfully.");
            } else {
                System.out.println("Failed to move file " + fileNameToMove);
            }

        }catch(Exception e){
            System.out.println(e);
        }

    }

    public void uploadAllFilesExtension(String localDirectory, String workingDirectory, String extensions, boolean deleteFromSource){
        List<File> deletedFiles = new ArrayList<>();
        try{
            // Set the transfer mode to binary
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            // Change the working directory on the server to the source directory
            ftpClient.changeWorkingDirectory(workingDirectory);

            String[] extensionsArray = extensions.split(",");

            // Get a list of files in the local directory
            File localDir = new File(localDirectory);
            List<File> fileList = new ArrayList<>();
            for(String extension : extensionsArray){
                File[] files = localDir.listFiles((dir, name) -> name.toLowerCase().endsWith("." + extension.trim()));
                fileList.addAll(Arrays.asList(files));
            }

            if (!fileList.isEmpty()) {
                for (File file : fileList) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        FileInputStream fis = new FileInputStream(file);

                        // Upload the file to the server
                        boolean uploaded = ftpClient.storeFile(fileName, fis);
                        fis.close();

                        if (uploaded) {
                            System.out.println("File " + fileName + " uploaded successfully.");
                            deletedFiles.add(file);
                        } else {
                            System.out.println("Failed to upload file " + fileName);
                        }
                    }
                }
            }

        }catch(Exception e){
            System.out.println(e);
        }finally{
            if(deleteFromSource)
                deleteAfter(deletedFiles);
        }
    }

    private void deleteAfter(List<File> files){
        for (File file : files) {
            if (file.delete()) {
                System.out.println("Deleted: " + file.getName());
            } else {
                System.err.println("Failed to delete: " + file.getName());
            }
        }
    }

    public void executeFtpConnection(){
        try{
            ftpClient.logout();
            ftpClient.disconnect();
            System.out.println("Disconnected from the FTP server.");
        }catch (Exception e){
            System.out.println(e);
        }

    }

}
