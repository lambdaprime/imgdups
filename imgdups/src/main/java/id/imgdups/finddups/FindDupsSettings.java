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

import id.xfunction.cli.CommandOptions;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * @author lambdaprime <intid@protonmail.com>
 */
public class FindDupsSettings {

    private static final FindDupsSettings instance = new FindDupsSettings();

    private int signatureLength;
    private int threshold;
    private int size;
    private Optional<Path> queryImage;

    public FindDupsSettings() {
        update(new CommandOptions(System.getProperties()));
    }

    public static FindDupsSettings getInstance() {
        return instance;
    }

    public int getSignatureLength() {
        return signatureLength;
    }

    public int getThreshold() {
        return threshold;
    }

    public int getSize() {
        return size;
    }

    public Optional<Path> getQueryImage() {
        return queryImage;
    }

    @Override
    public String toString() {
        var buf = new StringBuilder();
        buf.append("signatureLength: " + signatureLength + "\n");
        buf.append("threshold: " + threshold + "\n");
        buf.append("size: " + size + "\n");
        buf.append("queryImage: " + queryImage + "\n");
        return buf.toString();
    }

    public void update(CommandOptions properties) {
        signatureLength = Integer.parseInt(properties.getOption("signatureLength").orElse("64"));
        threshold = Integer.parseInt(properties.getOption("threshold").orElse("150"));
        size = Integer.parseInt(properties.getOption("size").orElse("300"));
        queryImage = properties.getOption("queryImage").map(Paths::get);
    }
}
