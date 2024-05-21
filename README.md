# DLHttpUtil

网络请求 Ktor Client 插件，拦截部分错误转换成中文，提供错误信息Flow用于UI统一展示；

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
  implementation 'com.github.D10NGYANG:DLHttpUtil:1.0.1'
  // ktor核心库
  implementation 'io.ktor:ktor-client-core:2.3.11'
}
```

3. 混淆
```properties
-keep class com.d10ng.http.** {*;}
-dontwarn com.d10ng.http.**
```
