/*
 * Copyright 2020 imgdups project
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
package id.jrosclient.tests.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import id.xfunction.nio.file.XFiles;
import id.xfunctiontests.AssertRunCommand;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @author lambdaprime <intid@protonmail.com>
 */
public class ImgdupsAppTests {

    private static final String COMMAND_PATH =
            Paths.get("").toAbsolutePath().resolve("build/imgdups/imgdups").toString();
    private static Path targetFolder;

    @BeforeAll
    public static void setup() throws IOException {
        targetFolder = Files.createTempDirectory("imgdups-tests");
        Path sourceFolder = Paths.get("").resolve("samples/");
        XFiles.copyRecursively(sourceFolder, targetFolder);
    }

    @Test
    public void test_no_args() throws Exception {
        new AssertRunCommand(COMMAND_PATH, "-h")
                .assertOutputFromResource("test_help")
                .assertReturnCode(0)
                .run();
    }

    @Test
    public void test_finddups() throws Exception {
        Path dup1 = targetFolder.resolve("111.jpg");
        Files.copy(targetFolder.resolve("1.jpg"), dup1);

        Path dup2 = targetFolder.resolve("lo/111.jpg");
        Path folder = targetFolder.resolve("lo");
        Files.createDirectories(folder);
        Files.copy(targetFolder.resolve("1.jpg"), dup2);

        new AssertRunCommand(
                        COMMAND_PATH,
                        "-hasNoUi=true",
                        "-targetFolder=" + targetFolder.toAbsolutePath().toString())
                .assertOutputFromResource("test_finddups")
                .withOutputConsumer(System.out::println)
                .assertReturnCode(0)
                .withWildcardMatching()
                .withInput(Stream.of("yes\n"))
                .run();
        assertEquals(false, dup1.toFile().exists());
        assertEquals(true, dup2.toFile().exists());
    }

    @Test
    public void test_scaleimages() throws Exception {
        new AssertRunCommand(
                        COMMAND_PATH,
                        "-action=scale_images",
                        "-targetFolder=" + targetFolder.toAbsolutePath().toString(),
                        "-sourceResolution=1030x700")
                .assertOutputFromResource("test_scale_images")
                .withOutputConsumer(System.out::println)
                .assertReturnCode(0)
                .withWildcardMatching()
                .withInput(Stream.of("yes\n"))
                .run();
        assertEquals(75089, targetFolder.resolve("1_scaled.jpg").toFile().length());
        assertEquals(54911, targetFolder.resolve("brightness-100_scaled.jpg").toFile().length());
    }
}
