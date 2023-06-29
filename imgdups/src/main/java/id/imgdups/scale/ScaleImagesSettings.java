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
package id.imgdups.scale;

import java.util.Properties;

public class ScaleImagesSettings {

    private static final ScaleImagesSettings instance = new ScaleImagesSettings();

    private int sourceWidth;
    private int sourceHeight;
    private int scalePercent;
    private boolean isCommutative;

    public ScaleImagesSettings() {
        update(System.getProperties());
    }

    public static ScaleImagesSettings getInstance() {
        return instance;
    }

    public int getSourceWidth() {
        return sourceWidth;
    }

    public int getSourceHeight() {
        return sourceHeight;
    }

    public int getScalePercent() {
        return scalePercent;
    }

    public boolean isCommutative() {
        return isCommutative;
    }

    @Override
    public String toString() {
        var buf = new StringBuilder();
        buf.append("sourceHeight: " + sourceHeight + "\n");
        buf.append("sourceWidth: " + sourceWidth + "\n");
        buf.append("scalePercent: " + scalePercent + "\n");
        buf.append("isCommutative: " + isCommutative + "\n");
        return buf.toString();
    }

    public void update(Properties properties) {
        var resolution = properties.getProperty("sourceResolution", "3920x2204").split("x");
        sourceWidth = Integer.parseInt(resolution[0]);
        sourceHeight = Integer.parseInt(resolution[1]);
        scalePercent = Integer.parseInt(properties.getProperty("scalePercent", "70"));
        isCommutative = Boolean.parseBoolean(properties.getProperty("isCommutative", "false"));
    }
}
