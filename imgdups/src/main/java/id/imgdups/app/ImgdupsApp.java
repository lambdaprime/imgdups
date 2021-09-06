/*
 * Copyright 2021 imgdups project
 * 
 * Website: https://github.com/lambdaprime/imgdups
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

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;

import javax.swing.JFileChooser;

import id.imgdups.finddups.FindDups;
import id.imgdups.settings.Settings;
import id.xfunction.ResourceUtils;
import id.xfunction.cli.ArgsUtils;
import id.xfunction.cli.ArgumentParsingException;
import id.xfunction.cli.CommandLineInterface;
import id.xfunction.util.XCollections;

public class ImgdupsApp {

    private static final ResourceUtils resourceUtils = new ResourceUtils();
    private static final ArgsUtils argsUtils = new ArgsUtils();
    private Settings settings = Settings.getInstance();
    private CommandLineInterface cli;

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
        switch (settings.getAction()) {
        case FIND_DUPS:
            new FindDups(cli, properties).run(folder);
            break;
        }
    }

    private Path retrieveTargetFolder() {
        if (settings.isDevMode()) return Paths.get("samples/");
        return settings.getTargetFolder().orElseGet(() -> choseFolder().toPath());
    }

    public static void main(String[] args) throws Exception {
        try {
            new ImgdupsApp(new CommandLineInterface()).run(args);
        } catch (ArgumentParsingException e) {
            usage();
        }
    }
    
}
