**imgdups** - application for finding and removing duplicate images.

# Download

You can download **imgdups** release versions from <https://github.com/lambdaprime/imgdups/releases>

Latest prerelease version can be found here <https://github.com/lambdaprime/imgdups/tree/main/imgdups/release>

# Requirements

- Java 11
- OpenCV >= 4.2

To install OpenCV in Linux:

```bash
apt install -y libopencv4.2-java
```

For Windows in can be downloaded from <https://opencv.org/>

# Usage

Once you run **imgdups** command it will ask you select folder for scanning.

Once scanning completes it will allow you to navigate over the duplicate images and chose which one you want to delete.

# Configuration

**imgdups** command accepts following properties which can be used to configure it.

## General

`-action=<find_dups|scale_images>` - specifies what action should be performed. Each action has its own set of properties which can be configured. This property is optional (default action is `find_dups`)

`-isDevMode=<true|false>` - has no special meaning outside of development. When run inside IDE it will run scanning for the predefined "samples" folder (so you don't have to chose it each time). This property is optional (default is `false`)

`-queryImage=<QUERY_IMAGE>` - by default **imgdups** compares all images in the target folder and finds all duplicates. When you need to find duplicates of a single image instead you can use following option where QUERY_IMAGE is an image which duplicates you are trying to find. This property is optional.

## finddups

`-signatureLength=NUMBER` - length of the image signature to use. This property is optional.

`-threshold=NUMBER` - treshold below which to consider two images as duplicates. This property is optional.

`-size=NUMBER` - control resize in comparison algorithm. This property is optional.

## scale_images

`-sourceResolution=<RESOLUTION>` - images with what resolution to scale. This property is optional (default is `3920x2204`)

`-scalePercent=NUMBER` - percentage by which images will be scaled. This property is optional (default is `70` which means that they will be reduced in size)

`isCommutative=<true|false>` - by default resolution of images need to match sourceResolution exactly. For example if the image is 123x456 and sourceResolution is 456x123 then image will not be scaled. This property allows to make resolution matching commutative so that images with 123x456 and 456x123 will be scaled. This property is optional (default is `false`)

# Implementation

**imgdups** is based on RgbMean algorithm for finding duplicate images described in:

```
Implementation of Naive Similarity Finder algorithm from 
Java Image Processing Cookbook by Rafael Santos

http://www.lac.inpe.br/JIPCookbook/6050-howto-compareimages.jsp
```

The algorithm is the following:
- resize image to `size`
- extract `signatureLength` pitches from an image with equal size and equally distributed across the image
- for each pitch calculate feature vector which is RGB triple of average values for R, G and B (this will give us `signatureLength`x3 feature vector)

**imgdups** is using FLANN for feature matching.

# Examples

Run with default parameters:

``` bash
imgdups
```

Overwrite parameters:

``` bash
imgdups -threshold=300 -signatureLength=16
```
Scale all images with resolution 1030x700 in the folder ~/Photos by 70%:

``` bash
imgdups -action=scale_images -targetFolder=/google_drive/photos -sourceResolution=1030x700
```

# Contributors

lambdaprime <intid@protonmail.com>
