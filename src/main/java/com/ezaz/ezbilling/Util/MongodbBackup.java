package com.ezaz.ezbilling.Util;

import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


@Component
public class MongodbBackup {
    // MongoDB connection details
    String host = "localhost";
    int port = 27017;
    String databaseName = "ezbilling";
    String username = "";
    String password = "";

    @Scheduled(cron = "0 0 * * * ?")
    public void BackUp() {




        // Output directory for the backup
        String backupDirectory = "F:/ezbilling-backup"; // Change this to the desired directory

        // Backup command with output directory specified
        String[] command = {"mongodump", "--host", host + ":" + port, "--db", databaseName, "--out", backupDirectory};

        try {
            // Execute the command
            Process process = Runtime.getRuntime().exec(command);

            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the command to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Backup completed successfully.");
            } else {
                System.out.println("Backup failed. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void Restore(){

        // Path to the dump directory
        String dumpDirectory = "F:/ezbilling-backup"; // Change this to the actual path of the dump directory

        // Restore command with dump directory specified
        String[] command = {"mongorestore", "--host", host + ":" + port, "--db", databaseName,  dumpDirectory};

        try {
            // Execute the command
            Process process = Runtime.getRuntime().exec(command);

            // Read the output of the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the command to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Restore completed successfully.");
            } else {
                System.out.println("Restore failed. Exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
