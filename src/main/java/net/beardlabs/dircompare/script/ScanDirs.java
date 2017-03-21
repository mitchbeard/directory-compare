package net.beardlabs.dircompare.script;

import com.google.common.collect.Lists;
import net.beardlabs.dircompare.DirCompareService;
import net.beardlabs.dircompare.FileEntry;
import net.beardlabs.dircompare.FileEntrySerializer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ScanDirs {

    public static void main(String[] args) {

        List<String> argsList = Lists.newArrayList(args);

        Path outputFile = Paths.get(argsList.remove(0));

        List<Path> paths = argsList.stream()
            .map(s -> Paths.get(s))
            .collect(Collectors.toList());

        DirCompareService service = new DirCompareService();
        FileEntrySerializer serializer = new FileEntrySerializer();

        List<FileEntry> fileEntries = service.scanPaths(paths);
        serializer.writeToFile(fileEntries, outputFile);
    }
}
