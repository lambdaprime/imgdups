/*
 * Copyright 2020 jrosclient project
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
/**
 * Copyright 2020 lambdaprime
 * 
 * Email: intid@protonmail.com 
 * Website: https://github.com/lambdaprime
 * 
 */
package id.jrosclient.tests.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import id.xfunction.AssertRunCommand;
import id.xfunction.nio.file.XFiles;

public class ImgdupsAppTests {

    private static final String COMMAND_PATH = Paths.get("")
            .toAbsolutePath()
            .resolve("build/imgdups/imgdups")
            .toString();
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
                .withOutputFromResource("test_help")
                .withReturnCode(0)
                .run();
    }

    @Test
    public void test_hasNoUi() throws Exception {
        Path dup1 = targetFolder.resolve("111.jpg");
        Files.copy(targetFolder.resolve("1.jpg"), dup1);

        Path dup2 = targetFolder.resolve("lo/111.jpg");
        Path folder = targetFolder.resolve("lo");
        Files.createDirectories(folder);
        Files.copy(targetFolder.resolve("1.jpg"), dup2);
        
        new AssertRunCommand(COMMAND_PATH, "-hasNoUi=true", "-targetFolder=" + targetFolder.toAbsolutePath().toString())
                .withOutputFromResource("test_hasNoUi")
                .withOutputConsumer(System.out::println)
                .withReturnCode(0)
                .withWildcardMatching()
                .withInput(Stream.of("yes\n"))
                .run();
        assertEquals(false, dup1.toFile().exists());
        assertEquals(true, dup2.toFile().exists());
    }

}
