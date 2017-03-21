package net.beardlabs.dircompare;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class DirCompareService {

    private final static Logger log = LoggerFactory.getLogger(DirCompareService.class);

    public List<FileEntry> scanPaths(List<Path> dirs) {

        try {
            log.info("Parsing input dirs for file count...");

            ScannerCountVisitor fileCountVisitor = new ScannerCountVisitor();
            for (Path dir : dirs) {
                Files.walkFileTree(dir, fileCountVisitor);
            }

            long fileCount = fileCountVisitor.getFileCount();

            log.info("Found {} files to scan", fileCount);

            ScannerFileVisitor fileEntryVisitor = new ScannerFileVisitor(fileCount);

            for (Path dir : dirs) {
                Files.walkFileTree(dir, fileEntryVisitor);
            }

            return fileEntryVisitor.getFileEntries();
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    private static class ScannerCountVisitor extends SimpleFileVisitor<Path> {

        private long fileCount = 0;

        public long getFileCount() {
            return fileCount;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (attrs.isRegularFile())
                fileCount++;

            return FileVisitResult.CONTINUE;
        }
    }

    private static class ScannerFileVisitor extends SimpleFileVisitor<Path> {

        private final long fileCount;
        private final List<FileEntry> fileEntries = Lists.newArrayList();

        public ScannerFileVisitor(long fileCount) {
            this.fileCount = fileCount;
        }

        public List<FileEntry> getFileEntries() {
            return fileEntries;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (attrs.isRegularFile())
                fileEntries.add(FileEntry.fromFile(file));

            if (fileEntries.size() % 1000 == 0) {
                log.info("Scanned {}/{} files so far", fileEntries.size(), fileCount);
            }

            return FileVisitResult.CONTINUE;
        }
    }
}
