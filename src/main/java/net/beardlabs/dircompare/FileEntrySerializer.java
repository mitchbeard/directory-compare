package net.beardlabs.dircompare;

import com.google.common.collect.Lists;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

public class FileEntrySerializer {

    private final static Logger log = LoggerFactory.getLogger(FileEntrySerializer.class);

    public void writeToFile(List<FileEntry> fileEntries, Path outputFile) {

        log.info("Writing {} entries to file {}", fileEntries.size(), outputFile.getFileName());

        try (CSVWriter writer = new CSVWriter(new BufferedWriter(new FileWriter(outputFile.toFile())))) {
            for (FileEntry fileEntry : fileEntries) {
                writer.writeNext(toStringVals(fileEntry));
            }

            log.info("Completed writing {} entries to file {}", fileEntries.size(), outputFile.getFileName());
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<FileEntry> readFromFile(Path inputFile) {

        log.info("Reading entries from file {}", inputFile.getFileName());

        try (CSVReader reader = new CSVReader(new BufferedReader(new FileReader(inputFile.toFile())))) {
            List<FileEntry> fileEntries = Lists.newArrayList();

            String[] next;

            while ((next = reader.readNext()) != null) {
                fileEntries.add(fromStringVals(next));
            }

            log.info("Completed reading {} entries from file {}", fileEntries.size(), inputFile.getFileName());
            return fileEntries;
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String[] toStringVals(FileEntry fileEntry) {
        return new String[]{
            fileEntry.getFileName(),
            fileEntry.getPath(),
            Long.toString(fileEntry.getByteSize()),
            fileEntry.getMd5()};
    }

    private FileEntry fromStringVals(String[] vals) {
        return ImmutableFileEntry.builder()
            .fileName(vals[0])
            .path(vals[1])
            .byteSize(Long.valueOf(vals[2]))
            .md5(vals[3])
            .build();
    }

}
