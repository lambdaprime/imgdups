/*
 * Copyright 2021 imgdups project
 * 
 * Website: https://github.com/lambdaprime/jrosclient
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
/*
 * Authors:
 * - lambdaprime <intid@protonmail.com>
 */
package id.imgdups.app;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.swing.JFileChooser;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import id.imgdups.settings.Settings;
import id.imgdups.viewer.MatchResultsFrame;
import id.opencvkit.feature.descriptor.FileDescriptor;
import id.opencvkit.feature.match.MatchResult;
import id.opencvkit.feature.match.Matchers;
import id.xfunction.ResourceUtils;
import id.xfunction.cli.ArgsUtils;
import id.xfunction.cli.ArgumentParsingException;
import id.xfunction.cli.CommandLineInterface;
import id.xfunction.function.Unchecked;
import id.xfunction.util.XCollections;

public class ImgdupsApp {

    private static final ResourceUtils resourceUtils = new ResourceUtils();
    private static final ArgsUtils argsUtils = new ArgsUtils();
    private Matchers matchers = new Matchers();
    private Settings settings = Settings.getInstance();
    private CommandLineInterface cli;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    private static void usage() {
        resourceUtils.readResourceAsStream("README.md")
            .forEach(System.out::println);
    }

    public ImgdupsApp(CommandLineInterface cli) {
        this.cli = cli;
    }
    
    private File choseFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal != JFileChooser.APPROVE_OPTION) {
            throw new RuntimeException("Please select the directory to scan for duplicates");
        }
        return chooser.getSelectedFile();
    }
    
    private void run(String[] args) throws Exception {
        Properties properties = argsUtils.collectOptions(args);
        if (XCollections.isIntersects(properties.keySet(), Set.of("h", "help"))) {
            throw new ArgumentParsingException();
        }
        settings.update(properties);
        cli.print(settings);
        var folder = retrieveTargetFolder();
        cli.print("Folder: " + folder.toAbsolutePath());
        var matches = findSimilar(settings.getQueryImage(), folder);
        cli.print("");
        removeIdenticalInSameFolder(matches);
        if (!settings.hasNoUi()) show(matches);
    }

    private Path retrieveTargetFolder() {
        if (settings.isDevMode()) return Paths.get("samples/");
        return settings.getTargetFolder().orElseGet(() -> choseFolder().toPath());
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
        EventQueue.invokeLater(viewer);
    }
    
    public static void main(String[] args) throws Exception {
        try {
            new ImgdupsApp(new CommandLineInterface()).run(args);
        } catch (ArgumentParsingException e) {
            usage();
        }
    }
    
    private List<MatchResult<Path>> findSimilar(Optional<Path> query, Path folder) throws IOException {
        List<FileDescriptor> trainDescriptors = extractDescriptors(folder);
        List<FileDescriptor> queryDescriptors = trainDescriptors;
        if (query.isPresent()) {
            queryDescriptors = List.of(query.map(this::extractDescriptor)
                    .map(Optional::get).get());
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
        var img = Imgcodecs.imread(path.toAbsolutePath().toString(),
                Imgcodecs.IMREAD_COLOR);
        return img;
    }

    private List<FileDescriptor> extractDescriptors(Path folder) throws IOException {
        return Files.walk(folder)
                .filter(p -> !p.toFile().isDirectory())
                .map(this::extractDescriptor)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(Predicate.not(Mat::empty))
                .collect(Collectors.toList());
    }

}
