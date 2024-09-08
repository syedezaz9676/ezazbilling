package com.ezaz.ezbilling.services;

import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class CsvExportService {


    // Directory to save the CSV file
    private static final String CSV_DIRECTORY = "F:\\ezbillingCSV";

    // Method to generate the CSV file
    public void generateGstDetailsCsvFile(List<String[]> gstDetails,String[] gstDetailsHeaders,String documentName) throws IOException {
        String fileName = CSV_DIRECTORY + File.separator + documentName;

        // Check if the directory exists, if not, create it
        File directory = new File(CSV_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();  // Creates the directory if it doesn't exist
        }

        List<String[]> data = gstDetails;
        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
            writer.writeNext(gstDetailsHeaders);
            writer.writeAll(data);
        }
    }

}

