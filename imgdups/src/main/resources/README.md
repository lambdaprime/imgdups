# Download

You can download **imgdups** from <https://github.com/lambdaprime/imgdups/releases>

# Requirements

Java 11, libopencv-java >= 3.2

# Usage

```
java -Djava.library.path=LIB_PATH -p MODULE_PATH --add-modules opencv -jar imgdups.jar
```

Where: 

* LIB_PATH -- directory with OpenCV native library (ex. libopencv_java320.so)
* MODULE_PATH -- directory with OpenCV Java library (ex. opencv-320.jar)

# Examples

```
java  -Djava.library.path=/usr/lib/jni -p /usr/share/java/opencv-320.jar --add-modules opencv -jar overwatch.jar /tmp/day1
```

# Contributors

lambdaprime <intid@protonmail.com>
