package net.beardlabs.dircompare.script;

import com.google.common.collect.Lists;
import net.beardlabs.dircompare.DirCompareService;
import net.beardlabs.dircompare.FileEntrySerializer;
import net.beardlabs.dircompare.FileScan;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ScanFiles {

    public static void main(String[] args) {

        Path inputFile = Paths.get(args[0]);
        Path outputFile = Paths.get(args[1]);

        DirCompareService compareService = new DirCompareService();
        FileEntrySerializer serializer = new FileEntrySerializer();

        List<Path> paths = serializer.readPathsFromFile(inputFile);

        List<FileScan> scans = compareService.scanPaths(paths);
        serializer.writeFileScansToFile(scans, outputFile);
    }
}
