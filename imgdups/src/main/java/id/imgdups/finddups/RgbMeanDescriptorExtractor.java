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

import id.matcv.OpenCvKit;
import id.matcv.SubmatrixDetector;
import id.matcv.feature.detector.OddPatchesFeatureDetector;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.opencv.core.Core;
import org.opencv.core.Mat;

public class RgbMeanDescriptorExtractor implements Function<Mat, Mat> {

    private static final OpenCvKit cvKit = new OpenCvKit();
    private int descriptorLen;

    public RgbMeanDescriptorExtractor(int descriptorLen) {
        this.descriptorLen = descriptorLen;
    }

    @Override
    public Mat apply(Mat mat) {
        var means =
                Stream.of(mat)
                        .flatMap(new OddPatchesFeatureDetector(descriptorLen))
                        // .limit(1)
                        .peek(new SubmatrixDetector(mat))
                        .map(patch -> Core.mean(patch))
                        .collect(Collectors.toList());
        return cvKit.toFlatMatrix(means);
    }
}
