# DLHttpUtil
网络请求封装库；

[![](https://jitpack.io/v/D10NGYANG/DLHttpUtil.svg)](https://jitpack.io/#D10NGYANG/DLHttpUtil)

# 使用说明
1. 添加仓库
```build.gradle
allprojects {
  repositories {
    ...
    maven { url 'https://raw.githubusercontent.com/D10NGYANG/maven-repo/main/repository'}
  }
}
```

2. 添加依赖
```build.gradle
dependencies {
  implementation 'com.github.D10NGYANG:DLHttpUtil:0.9.3'
  // JSON序列化
  implementation "org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3"
}
```

3. 混淆
```properties
-keep class com.d10ng.http.** {*;}
-dontwarn com.d10ng.http.**
```
