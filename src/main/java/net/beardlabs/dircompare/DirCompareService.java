package net.beardlabs.dircompare;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
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
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DirCompareService {

    private final static Logger log = LoggerFactory.getLogger(DirCompareService.class);

    private final static Set<String> SKIP_FILES = ImmutableSet.of(
        "Icon", ".DS_Store", ".dropbox");

    public List<Path> collectPaths(List<Path> dirs) {
        log.info("Collecting from input dirs...");

        PathCollectorVisitor pathCollectorVisitor = new PathCollectorVisitor();
        for (Path dir : dirs) {
            try {
                Files.walkFileTree(dir, pathCollectorVisitor);
            }
            catch (IOException ex) {
                log.error("Failed to read path {}", dir.getFileName());
            }
        }

        log.info("Collected {} total paths", pathCollectorVisitor.getPaths().size());
        return pathCollectorVisitor.getPaths();
    }

    private final static long GB_SIZE = (long)Math.pow(2, 32);

    public List<FileScan> scanPaths(List<Path> paths) {

        ConcurrentLinkedQueue<FileScan> scans = new ConcurrentLinkedQueue<>();

        long totalBytesScanned = 0;
        long lastBytesLogged = 0;

        for (Path path : paths) {

            try {
                FileScan scan = FileScan.fromFile(path);

                scans.add(scan);
                totalBytesScanned += scan.getTotalBytes();
            }
            catch(Exception ex) {
                log.error("Failed to scan file {}", path);
            }

            if (scans.size() % 1000 == 0) {
                log.info("Scanned {} files so far", scans.size());
            }

            if (totalBytesScanned - lastBytesLogged > GB_SIZE) {
                log.info("Scanned {} GB so far", totalBytesScanned / GB_SIZE);
                lastBytesLogged = totalBytesScanned;
            }
        }

        log.info("Scanned {} total files", scans.size());
        return ImmutableList.copyOf(scans);
    }

    private static class PathCollectorVisitor extends SimpleFileVisitor<Path> {

        private final List<Path> paths = Lists.newArrayList();

        public List<Path> getPaths() {
            return paths;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (Files.exists(file) && Files.isReadable(file) && attrs.isRegularFile() && !SKIP_FILES.contains(file.getFileName().toString()))
                paths.add(file);

            if (paths.size() % 1000 == 0)
            log.info("Collected {} paths so far", paths.size());

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException io)
        {
            return FileVisitResult.SKIP_SUBTREE;
        }
    }
}
