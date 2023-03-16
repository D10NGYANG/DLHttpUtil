# DLHttpUtil
网络请求封装库；

[![](https://jitpack.io/v/D10NGYANG/DLHttpUtil.svg)](https://jitpack.io/#D10NGYANG/DLHttpUtil)

# 使用说明
1. Add the JitPack repository to your build file
```build.gradle
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
    // 我的github镜像仓库
    maven { url 'https://raw.githubusercontent.com/D10NGYANG/maven-repo/main/repository'}
  }
}
```

2. Add the dependency
```build.gradle
dependencies {
  implementation 'com.github.D10NGYANG:DLHttpUtil:0.8.0'
  // JSON序列化
  implementation "org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0"
}
```

3. 混淆
```properties
-keep class com.d10ng.http.** {*;}
-dontwarn com.d10ng.http.**
```
