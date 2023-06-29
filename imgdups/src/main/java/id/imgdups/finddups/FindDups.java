/*
 * Copyright 2021 imgdups project
 * 
 * Website: https://github.com/lambdaprime
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package id.imgdups.finddups;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import id.imgdups.finddups.viewer.MatchResultsFrame;
import id.imgdups.settings.Settings;
import id.opencvkit.feature.descriptor.FileDescriptor;
import id.opencvkit.feature.match.MatchResult;
import id.opencvkit.feature.match.Matchers;
import id.xfunction.cli.CommandLineInterface;
import id.xfunction.function.Unchecked;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.swing.SwingUtilities;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class FindDups {

    private Settings globalSettings = Settings.getInstance();
    private Matchers matchers = new Matchers();
    private FindDupsSettings settings = new FindDupsSettings();
    private CommandLineInterface cli;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public FindDups(CommandLineInterface cli, Properties properties) {
        this.cli = cli;
        settings.update(properties);
    }

    public void run(Path folder) throws Exception {
        cli.print("Finding duplicates");
        cli.print(settings);
        var matches = findSimilar(settings.getQueryImage(), folder);
        cli.print("");
        removeIdenticalInSameFolder(matches);
        if (!globalSettings.hasNoUi()) show(matches);
    }

    private void removeIdenticalInSameFolder(List<MatchResult<Path>> matches) {
        var identical = new ArrayList<MatchResult<Path>>();
        for (var result : matches) {
            Path pathA = result.getA();
            Path pathB = result.getB();
            if (pathA.equals(pathB)) continue;
            var sizeA = pathA.toFile().length();
            var sizeB = pathB.toFile().length();
            if (sizeA != sizeB) continue;
            if (!pathA.getParent().equals(pathB.getParent())) continue;
            identical.add(result);
        }
        if (identical.isEmpty()) return;

        cli.print("Identical files detected:");
        cli.print(identical.stream().map(MatchResult::toString).collect(joining("\n")));
        cli.print("");

        var delete = identical.stream().map(MatchResult<Path>::getB).collect(toList());
        cli.print("Identical files to be deleted:");
        cli.print(delete.stream().map(Path::toString).collect(joining("\n")));
        cli.print("");

        if (cli.askConfirm("Delete identical files?")) {
            delete.stream().forEach(Unchecked.wrapAccept(Files::delete));
        }
    }

    private void show(List<MatchResult<Path>> matches) {
        var viewer = new MatchResultsFrame(matches);
        SwingUtilities.invokeLater(viewer);
    }

    private List<MatchResult<Path>> findSimilar(Optional<Path> query, Path folder)
            throws IOException {
        List<FileDescriptor> trainDescriptors = extractDescriptors(folder);
        List<FileDescriptor> queryDescriptors = trainDescriptors;
        if (query.isPresent()) {
            queryDescriptors = List.of(query.map(this::extractDescriptor).map(Optional::get).get());
        }

        System.out.format("Descriptors found: %d\n", trainDescriptors.size());

        return matchers.matchRadius(queryDescriptors, trainDescriptors, settings.getThreshold());
    }

    private Optional<FileDescriptor> extractDescriptor(Path path) {
        System.out.println("Extracting descriptors for " + path);
        var img = readRgbImage(path);
        if (img.empty()) return Optional.empty();
        Imgproc.resize(img, img, new Size(settings.getSize(), settings.getSize()));
        var descriptorExtractor = new RgbMeanDescriptorExtractor(settings.getSignatureLength());
        var descriptor = descriptorExtractor.apply(img);
        return Optional.of(new FileDescriptor(descriptor, path));
    }

    private static Mat readRgbImage(Path path) {
        var img = Imgcodecs.imread(path.toAbsolutePath().toString(), Imgcodecs.IMREAD_COLOR);
        return img;
    }

    private List<FileDescriptor> extractDescriptors(Path folder) throws IOException {
        return Files.walk(folder)
                .filter(p -> !p.toFile().isDirectory())
                .sorted()
                .map(this::extractDescriptor)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(Predicate.not(Mat::empty))
                .collect(Collectors.toList());
    }
}
