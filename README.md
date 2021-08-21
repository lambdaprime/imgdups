# Download

You can download **imgdups** from <https://github.com/lambdaprime/imgdups/releases>

# Requirements

- Java 11
- libopencv4.2-java

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

# Contributors

lambdaprime <intid@protonmail.com>
