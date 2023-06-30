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
package id.imgdups.settings;

import id.xfunction.cli.CommandOptions;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Settings {

    private static final Settings instance = new Settings();

    private ActionType action;
    private boolean isDevMode;
    private boolean hasNoUi;
    private Optional<Path> targetFolder;

    public Settings() {
        update(new CommandOptions(System.getProperties()));
    }

    public static Settings getInstance() {
        return instance;
    }

    public ActionType getAction() {
        return action;
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
        buf.append("action: " + action + "\n");
        buf.append("isDevMode: " + isDevMode + "\n");
        buf.append("targetFolder: " + targetFolder + "\n");
        buf.append("hasNoUi: " + hasNoUi + "\n");
        return buf.toString();
    }

    public void update(CommandOptions properties) {
        action =
                ActionType.valueOf(
                        properties.getOption("action").orElse("FIND_DUPS").toUpperCase());
        isDevMode = properties.isOptionTrue("isDevMode");
        hasNoUi = Boolean.parseBoolean(properties.getOption("hasNoUi").orElse("false"));
        targetFolder = properties.getOption("targetFolder").map(Paths::get);
    }
}
