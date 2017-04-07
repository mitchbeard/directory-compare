package net.beardlabs.dircompare;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class FileEntrySerializer {

    private final static Logger log = LoggerFactory.getLogger(FileEntrySerializer.class);

    public void writePathsToFile(List<Path> files, Path outputFile) {

        log.info("Writing {} files to file {}", files.size(), outputFile.getFileName());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile.toFile()))) {
            for (Path file : files) {
                writer.write(file.normalize().toString());
                writer.newLine();
            }

            log.info("Completed writing {} files to file {}", files.size(), outputFile.getFileName());
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<Path> readPathsFromFile(Path inputFile) {
        try {
            List<String> pathStrs = Files.readLines(inputFile.toFile(), Charset.defaultCharset());
            return pathStrs.stream()
                .map(p -> Paths.get(p))
                .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    public void writeFileScansToFile(List<FileScan> fileScans, Path outputFile) {

        log.info("Writing {} entries to file {}", fileScans.size(), outputFile.getFileName());

        try (CSVWriter writer = new CSVWriter(new BufferedWriter(new FileWriter(outputFile.toFile())))) {
            for (FileScan fileScan : fileScans) {
                writer.writeNext(toStringVals(fileScan));
            }

            log.info("Completed writing {} entries to file {}", fileScans.size(), outputFile.getFileName());
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<FileScan> readFileScansFromFile(Path inputFile) {

        log.info("Reading entries from file {}", inputFile.getFileName());

        try (CSVReader reader = new CSVReader(new BufferedReader(new FileReader(inputFile.toFile())))) {
            List<FileScan> fileEntries = Lists.newArrayList();

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

    private String[] toStringVals(FileScan fileScan) {
        return new String[]{
            fileScan.getFileName(),
            fileScan.getPath(),
            Long.toString(fileScan.getTotalBytes()),
            fileScan.getMd5()};
    }

    private FileScan fromStringVals(String[] vals) {
        return ImmutableFileScan.builder()
            .fileName(vals[0])
            .path(vals[1])
            .totalBytes(Long.valueOf(vals[2]))
            .md5(vals[3])
            .build();
    }
}
