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

public class Settings {

    private static final Settings instance = new Settings();
    
    private int signatureLength = Integer.parseInt(System.getProperty("signatureLength", "64"));
    private int threshold = Integer.parseInt(System.getProperty("threshold", "150"));
    private int size = Integer.parseInt(System.getProperty("size", "300"));
    private boolean isDevMode = Boolean.parseBoolean(System.getProperty("isDevMode", "false"));
    private Optional<Path> queryImage = Optional.ofNullable(System.getProperty("queryImage"))
            .map(Paths::get);

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
    
    @Override
    public String toString() {
        var buf = new StringBuilder();
        buf.append("signatureLength: " + signatureLength + "\n");
        buf.append("threshold: " + threshold + "\n");
        buf.append("size: " + size + "\n");
        buf.append("queryImage: " + queryImage + "\n");
        buf.append("isDevMode: " + isDevMode + "\n");
        return buf.toString();
    }
}
