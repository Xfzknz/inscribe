# COMMANDS — 构建、运行、排障

> 目标环境：**Minecraft 1.20.1 + Forge 47.x**，Java 17，Gradle 8.x，ForgeGradle 6。
> 这份文件是从上一个 1.18.1 项目继承过来的——**排障部分踩过的坑一一对应，别删**。
>
> **工作区布局**：本模组在 `inscribe/`，以下命令默认在 `inscribe/` 下执行；
> 共享工具链在 `../shared/.build-tools/`、`../shared/.gradle-home/`、`../shared/tools/`。

## 1. 工具链

| 用途 | 路径 |
|---|---|
| JDK 21（**构建 JVM，必需**） | `C:\Program Files\Microsoft\jdk-21.0.8.9-hotspot` |
| JDK 17（保留，构建不再使用） | `../shared/.build-tools/jdk17/jdk-17.0.20.1+1` |
| Gradle 8.x（首次运行时下载） | `../shared/.build-tools/gradle-8.5/gradle-8.5` |
| Gradle 用户目录（工作区共享） | `../shared/.gradle-home` |
| 本地 Maven 镜像缓存 | `../shared/.build-tools/maven-cache` |

> **1.20.1 的 mod 字节码必须是 Java 17**，但本机的构建 JVM 用 JDK 21（见 D-005）：
> 项目自带 JDK 17 在沙箱里 `Files.isWritable()` 恒为 false，ForgeGradle 的 AT 会以只读打开 jar 并失败。
> `build.gradle` 里用 `java.toolchain.languageVersion = 21` + `options.release = 17` 保证产物仍是 Java 17。

> **1.20.1 必须用 Java 17 字节码。** 旧项目里的 Gradle 7.3/7.6 是 1.18.1 时代的，ForgeGradle 6
> 跑不起来，不要拷过来。

## 2. 首次准备（只需一次）

> 下面的 `python` 指真实解释器；系统商店占位程序会以 exit 49 失败。

```text
1) 备齐 JDK 和 Gradle
   python ../shared/tools/bootstrap_toolchain.py
   （走 127.0.0.1:7890 代理下载；JDK 已经有了会跳过）

2) 起本地 Maven 镜像（另开一个窗口，保持运行）
   python ../shared/tools/local_maven_proxy.py
   监听 127.0.0.1:8765

3) 让 build.gradle 走镜像
   buildscript / repositories 里加：
     maven { url "http://127.0.0.1:8765/forge/" }     -> maven.minecraftforge.net
     maven { url "http://127.0.0.1:8765/central/" }   -> Maven Central
     maven { url "http://127.0.0.1:8765/mojang/" }    -> libraries.minecraft.net
   （/sponge/ 前缀镜像仍保留，但 build.gradle 已不用：上游不可达，Forge maven 已代理
   modlauncher / Mixin，见 D-011）
```

ForgeGradle 用 `buildscript { classpath 'net.minecraftforge.gradle:ForgeGradle:6.+' }` 引入，
不要用 `plugins {}`——后者会去找 Gradle Plugin Portal，而镜像里没有那个仓库。

## 3. 日常构建

```text
export JAVA_HOME="C:/Program Files/Microsoft/jdk-21.0.8.9-hotspot"

gradle.bat build              # 出包 -> build/libs/*.jar
gradle.bat runClient          # 起开发客户端
gradle.bat runServer          # 起开发服务端
gradle.bat --refresh-dependencies   # 慎用，见第 6 节
```

> **JAVA_HOME 必须指向 JDK 21**（D-005）。用项目自带 JDK 17 会在 AT 步骤抛
> `ReadOnlyFileSystemException`，不是依赖问题，换回 21 即可。

统一加 `-g ../shared/.gradle-home`，依赖落在工作区共享缓存里，多个模组复用：

```text
../shared/.build-tools/gradle-8.5/gradle-8.5/bin/gradle.bat -g ../shared/.gradle-home build
```

### 3.1 模块化说明

默认 `build` 产出一个全量 jar（`build/libs/inscribe-1.2.7.jar`），包含当前所有附魔。
源码在 `src/outreach`、`src/double_jump`，由核心 `ModuleLoader` 合并；日常不需要关心子集构建。
`-Pmodules` 仅保留作临时调试，不是常规流程，也不默认产出子集 jar。

## 4. 起测试服 + RCON

```text
单独装一个 Forge 1.20.1 服务端到 ../test-server/
把 build/libs/*.jar 放进 ../test-server/mods/
../test-server/run.bat
```

`../shared/tools/rcon_test.py` 用来做自动验证，需要服务端 `server.properties` 里开：

```properties
enable-rcon=true
rcon.port=25575
rcon.password=test
```

## 5. 日志位置

| 位置 | 内容 |
|---|---|
| `run/logs/latest.log` | 开发客户端/服务端最近一次运行 |
| `../test-server/logs/latest.log` | 独立测试服 |
| `run/crash-reports/` | 崩溃报告 |

排查顺序：先搜 `ERROR` 和 `Exception`，再看 `Mod loading` 段里自己的 modId 有没有失败行。

## 6. 依赖拉不下来时的处置顺序

**按顺序做，不要跳步，也不要把"换个参数重试"当成排查。**

1. **先拿到真实报错行**：往上翻到 `Could not resolve` / `Could not GET` / `SSLHandshakeException` / `Connection timed out`，贴 10 行以内的原文。`exit code: 1` 本身没有信息量。
2. **清坏包**：报 `corrupted` 时先删缓存再重下。
   - `../shared/.gradle-home/caches/modules-2/files-2.1/<group>/<artifact>/`
   - `../shared/.gradle-home/caches/forge_gradle/`
   - 判断坏包：目录里有 `.part` 残留，或 jar 明显偏小。
3. **确认镜像进程还活着**：`python ../shared/tools/local_maven_proxy.py`，端口 8765。
4. **大文件单独续传**：服务端 jar 这类几十 MB 的包用
   `python ../shared/tools/resume_server_jar.py 1.20.1`，不要塞进 Gradle 一起下。
5. **最后才考虑手工补库**：手工 jar 会让 classpath 越来越难维护，能不用就不用。

## 7. 已经证明无效的做法（别再试）

上一个项目连续失败了十几次，结论如下：

- 改 TLS 1.2 / 加 `-Dhttps.protocols`：JDK 17 默认就支持，问题不在协议版本。
- 在沙箱外跑 Gradle：不是权限问题。
- 换绝对 JDK 路径 / 换 Gradle home：不是路径问题。
- 反复 `--refresh-dependencies`：只会重下同一个坏包。
- 用系统 `python`：那是 Windows 商店占位程序，退出码 49，什么都不执行。用真实解释器。
- 用项目自带 JDK 17 跑 ForgeGradle 的 AT：`Files.isWritable()` 恒 false，zipfs 只读，
  必抛 `ReadOnlyFileSystemException`；换 JDK 21 工具链才解决。手工生成 mapped jar、
  换无空格 junction、删 22 字节空 jar 都无效。

## 8. 网络环境

- 本机 VPN 代理：`127.0.0.1:7890`。
- 症状：Gradle 走这个代理时 TLS 握手会失败，Python urllib 不会。
- 所以才有 `../shared/tools/local_maven_proxy.py` 这个纯 HTTP 中转。
- 每次构建前确认代理进程还在，不然会看到"忽然全部超时"。
