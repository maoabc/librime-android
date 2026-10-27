# 智能体指令：Rime JNI 模块开发规范

本文件定义了该模块的严格约束与编码标准。当你在该目录及其子目录下生成、重构或审查代码时，必须严格遵守以下规则。

## 🚫 1. 语言约束：全 Java 开发
* **严禁使用 Kotlin：** 切勿生成任何 Kotlin 代码（`.kt` 文件），也不要将现有的 Java 代码转换为 Kotlin。
* **保持纯净 Java 风格：** 编写符合当前项目 JDK 版本的现代 Java 代码，避免引入任何 Kotlin 特有的编程习惯（Idioms）。
* **依赖控制：** 除非有明确指示，否则切勿引入任何需要 Kotlin 标准库（`kotlin-stdlib`）或 Kotlin 优先（Kotlin-first）的 Jetpack 组件。

## 🛠️ 2. Java 端 JNI/Native 方法约束
为了与底层的 C/C++ 实现保持绝对一致，本模块中的所有 Native 桥接方法必须遵循高度结构化的模式：

* **统一集中管理：** 所有带有 `native` 关键字的方法**必须且只能**声明在 **`RimeJni`** 类中。严禁将 native 方法分散到其他类中。
* **必须为静态方法：** 每一个 native 方法都必须声明为静态的（`static native`）。严禁使用基于类实例的 native 方法。
* **命名后缀规范：** 每一个 native 方法的名称必须明确以数字 **`0`** 结尾（例如：`start0`、`processInput0`）。

### Java 代码结构示例
在创建或修改 `RimeJni` 类时，请务必参考以下标准结构：

```java
package com.github.maoabc.librime;

public class RimeJni {

    static {
        System.loadLibrary("rime-jni");
    }

    private RimeJni() {
        // Prevent instantiation since all methods are static
    }

    // 正确的 Native 方法声明示例
    static native void start0(RimeNotification notification);
    
}
```

## 💻 3. C/C++ (Native) 层 JNI 函数实现与 Rime API 约束
本模块的 C/C++ 层直接接入了 Rime 核心 API，其全局初始化和调用必须遵循以下严格约定：

* **全局 API 声明与初始化：** 必须使用全局静态变量 `static const RimeApi *api;`，并通过 `__attribute__((constructor))` 属性的构造函数在动态库加载时自动完成初始化。
* **绝对无需判空：** 因为库加载时已经完成了初始化，所以在所有 JNI 函数实现中，**严禁**编写对 `api` 指ren（或 `env` 指针）进行是否为 `NULL` 的判断逻辑，直接使用 `api->xxx` 调用 Rime 功能，以确保代码极致简洁高效。

### C++ 代码结构示例
底层 C++ 实现必须遵循如下结构：

```cpp
#include <jni.h>

// 全局 Rime API 指针声明
static const RimeApi *api;

// 动态库加载时自动初始化，注入指针
static void __attribute__((constructor)) init_rime_api(void) {
    api = rime_get_api();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_github_maoabc_librime_RimeJni_start0(JNIEnv *env, jclass clazz, jobject notification) {
    // 约束：严禁对全局变量 api 进行 if (api == NULL) , if (!api)等判空操作，直接使用！
    api->start(nullptr); // 示例：直接通过 api 调用 rime 的接口
}
```

## ⚠️ 4. 交付前自检清单
在向用户输出任何代码之前，请在底层逻辑中进行以下最终确认：
1. 是否包含了任何 Kotlin 代码？（必须为“否”）
2. Native 方法是否全部定义在 `RimeJni` 类中？（必须为“是”）
3. 所有 Native 方法是否都是 `static` 的？（必须为“是”）
4. 所有 Native 方法名是否都以 `0` 结尾？（必须为“是”）
5. C 层的 JNI 函数实现中是否包含了任何针对 `api` 的判空语句？（必须为“否”，绝对不允许出现）
