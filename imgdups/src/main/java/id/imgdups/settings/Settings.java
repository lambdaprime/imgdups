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
package id.imgdups.settings;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

public class Settings {

    private static final Settings instance = new Settings();
    
    private int signatureLength;
    private int threshold;
    private int size;
    private boolean isDevMode;
    private Optional<Path> queryImage;
    private boolean hasNoUi;
    private Optional<Path> targetFolder;

    public Settings() {
        update(System.getProperties());
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

    public static Settings getInstance() {
        return instance;
    }

    public Optional<Path> getQueryImage() {
        return queryImage;
    }
    
    public boolean isDevMode() {
        return isDevMode;
    }
    
    public boolean hasNoUi() {
        return hasNoUi;
    }
    
    public Optional<Path> getTargetFolder() {
        return targetFolder;
    }
    
    @Override
    public String toString() {
        var buf = new StringBuilder();
        buf.append("signatureLength: " + signatureLength + "\n");
        buf.append("threshold: " + threshold + "\n");
        buf.append("size: " + size + "\n");
        buf.append("queryImage: " + queryImage + "\n");
        buf.append("isDevMode: " + isDevMode + "\n");
        buf.append("targetFolder: " + targetFolder + "\n");
        buf.append("hasNoUi: " + hasNoUi + "\n");
        return buf.toString();
    }

    public void update(Properties properties) {
        signatureLength = Integer.parseInt(properties.getProperty("signatureLength", "64"));
        threshold = Integer.parseInt(properties.getProperty("threshold", "150"));
        size = Integer.parseInt(properties.getProperty("size", "300"));
        isDevMode = Boolean.parseBoolean(properties.getProperty("isDevMode", "false"));
        hasNoUi = Boolean.parseBoolean(properties.getProperty("hasNoUi", "false"));
        queryImage = Optional.ofNullable(properties.getProperty("queryImage"))
                .map(Paths::get);
        targetFolder = Optional.ofNullable(properties.getProperty("targetFolder"))
                .map(Paths::get);
    }
}
