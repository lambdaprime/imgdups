# Download

You can download **imgdups** from <https://github.com/lambdaprime/imgdups/releases>

# Requirements

- Java 11
- OpenCV >= 4.2

To install OpenCV in Linux:

```bash
apt install -y libopencv4.2-java
```

For Windows in can be downloaded from (https://opencv.org/)

# Usage

Once you run **imgdups** command it will ask you select folder for scanning.

Once scanning completes it will allow you to navigate over the duplicate images and chose which one you want to delete.

# Configuration

**imgdups** command accepts following properties which can be used to configure it.

```
-DsignatureLength=NUMBER
```
Length of the image signature to use.

```
-Dthreshold=NUMBER
```
Threshold below which to consider two images as duplicates.

```
-Dsize=NUMBER
```
Control resize in comparison algorithm.

```
-DisDevMode=<true|false>
```
Has no special meaning outside of development. When run inside IDE it will run scanning for the predefined "samples" folder (so you don't have to chose it each time).

```
-DqueryImage=<QUERY_IMAGE>
```
By default **imgdups** compares all images in the target folder and finds all duplicates. When you need to find duplicates of a single image instead you can use following option where QUERY_IMAGE is an image which duplicates you are trying to find. 

# Implementation

**imgdups** is based on RgbMean algorithm for finding duplicate images described in:

```
Implementation of Naive Similarity Finder algorithm from 
Java Image Processing Cookbook by Rafael Santos

http://www.lac.inpe.br/JIPCookbook/6050-howto-compareimages.jsp
```

The algorithm is the following
- resize image to `size`
- extract `signatureLength` pitches from an image with equal size and equally distributed across the image
- for each pitch calculate feature vector which is RGB triple of average values for R, G and B (this will give us `signatureLength`x3 feature vector)
- use FLANN for feature matching

# Examples

Run with default parameters:

``` bash
imgdups
```

Overwrite parameters:

``` bash
imgdups -Dthreshold=300 -DsignatureLength=16
```

# Contributors

lambdaprime <intid@protonmail.com>
