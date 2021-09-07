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
package id.imgdups.scale;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import id.xfunction.cli.CommandLineInterface;
import id.xfunction.nio.file.XPaths;

public class ScaleImages {

    private ScaleImagesSettings settings = new ScaleImagesSettings();
    private CommandLineInterface cli;
    private Size targetSize;

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public ScaleImages(CommandLineInterface cli, Properties properties) {
        this.cli = cli;
        settings.update(properties);
    }
    
    public void run(Path folder) throws Exception {
        cli.print("Scale images");
        cli.print(settings);
        var ratio = settings.getScalePercent() / 100.;
        var newHeight = settings.getSourceHeight() * ratio;
        var newWidth = settings.getSourceWidth() * ratio;
        cli.print("Target size of the longest side of the image will be <%dx%d>", (int)newWidth, (int)newHeight);
        targetSize = new Size(newWidth, newHeight);
        if (!cli.askConfirm("Proceed?")) return;
        Files.walk(folder)
            .filter(p -> !p.toFile().isDirectory())
            .forEach(this::resize);
    }

    private void resize(Path path) {
        var img = readRgbImage(path);
        if (img.empty()) return;
        if (!settings.isCommutative()) {
            if (img.height() != settings.getSourceHeight()) return;
            if (img.width() != settings.getSourceWidth()) return;
        } else {
            var actual = List.of(img.height(), img.width());
            var source = List.of(settings.getSourceHeight(), settings.getSourceWidth());
            if (!actual.containsAll(source)) return;
        }
        cli.print("Scaling image " + path);
        var newSize = targetSize;
        var isRotated = img.height() != settings.getSourceHeight();
        if (isRotated) {
            newSize = new Size(newSize.height, newSize.width);
        }
        Imgproc.resize(img, img, newSize);
        var fileName = XPaths.splitFileName(path.getFileName().toString());
        fileName[1] = Optional.ofNullable(fileName[1]).orElse("");
        var newFileName = fileName[0] + "_scaled." + fileName[1];
        String outputFile = path.resolveSibling(newFileName).toAbsolutePath().toString();
        switch (fileName[1].toLowerCase()) {
        case "jpg":
            Imgcodecs.imwrite(outputFile, img, new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, 80));
            break;
        default:
            Imgcodecs.imwrite(outputFile, img);
        }
    }

    private static Mat readRgbImage(Path path) {
        var img = Imgcodecs.imread(path.toAbsolutePath().toString(),
                Imgcodecs.IMREAD_COLOR);
        return img;
    }


}
