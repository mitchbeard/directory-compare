package net.beardlabs.dircompare.script;

import com.google.common.collect.Lists;
import net.beardlabs.dircompare.DirCompareService;
import net.beardlabs.dircompare.FileEntrySerializer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class PreScanDirs {

    public static void main(String[] args) {

        List<String> argsList = Lists.newArrayList(args);

        Path outputFile = Paths.get(argsList.remove(0));

        List<Path> pathRoots = argsList.stream()
            .map(s -> Paths.get(s))
            .collect(Collectors.toList());

        DirCompareService compareService = new DirCompareService();
        FileEntrySerializer serializer = new FileEntrySerializer();

        List<Path> paths = compareService.collectPaths(pathRoots);
        serializer.writePathsToFile(paths, outputFile);
    }
}
