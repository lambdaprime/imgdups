/*
 * Copyright 2023 imgdups project
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
package id.imgdups.compressvideo;

import id.xfunction.cli.CommandLineInterface;
import id.xfunction.lang.XExec;
import id.xfunction.nio.file.FilePredicates;
import id.xfunction.nio.file.XPaths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CompressVideo {

    private static final String POSTFIX = "-compressed";
    private CommandLineInterface cli;

    public CompressVideo(CommandLineInterface cli) {
        this.cli = cli;
    }

    public void run(Path folder) throws IOException {
        new XExec("ffmpeg", "-version").start().stderrThrow();
        cli.print(
                """
                Compress video
                Video is compressed with the following ffmpeg command:
                ffmpeg -i video_file.mp4 video_file%s.mp4

                All ffmpeg default settings applies.
                """
                        .formatted(POSTFIX));
        if (!cli.askConfirm("Proceed?")) return;
        Files.walk(folder)
                .filter(FilePredicates.anyExtensionOf("mp4"))
                .filter(p -> !p.getFileName().toString().contains(POSTFIX))
                .sorted()
                .forEach(this::compress);
    }

    private void compress(Path video) {
        video = video.toAbsolutePath();
        cli.print("Compressing " + video);
        var exec =
                new XExec(
                        "ffmpeg",
                        "-loglevel",
                        "warning",
                        "-i",
                        video.toString(),
                        XPaths.appendToFileName(video, POSTFIX).toString());
        exec.getProcessBuilder().inheritIO();
        var proc = exec.start();
        proc.forwardOutputAsync(true);
        proc.await();
    }
}
