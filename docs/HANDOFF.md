# HANDOFF — 会话交接快照

> **换会话前更新这一页，新会话第一句就读它。**
> 上一次更新：2026-11-02 18:05

## 1. 一句话状态

1.2.7 已构建：`build/libs/inscribe-1.2.7.jar`（171,910 字节）。
显示名 Inscribe / 附魔铭刻；作者 KaiJun；MIT；使用用户设计的 256×256 新封面；
13 个附魔与碎甲旧规则保持不变。

## 2. 这一轮做完的

- 备份 0.2.3：`backups/0.2.3/inscribe-0.2.3.jar` + 源码 zip + README。
- 把附魔拆成 `src/outreach`、`src/double_jump` 两个 source set；核心只留 `Inscribe` + `core/`。
- `InscribeModule` + ServiceLoader 发现模块；Gradle 生成唯一 `META-INF/services/...`，避免两个模块的
  service 文件互相覆盖。
- 附魔 ID 保持 `inscribe:outreach` / `inscribe:double_jump`。
- 战利品改由核心 `LootInjection` 收集模块 entries，仍是一个池子；全量两张表各 4 条。
- 交易由各模块自己加报价；钓鱼只在 outreach；网络/客户端按键只在 double_jump。
- 新增 thunderstrike 模块：剑、RARE、I–III、宝藏、无附魔台；攻击生物时目标处落雷，
  冷却 120/60/30 tick；攻击者 10 tick 内免疫雷击伤害；非生物/盔甲架不触发，闪电伤害也不伤盔甲架。
- 新增 swift_strike 模块：剑/斧/弓/弩/三叉戟、RARE、I–III、非宝藏；近战 ATTACK_SPEED
  +10/20/30%，弓/弩用 UseItem Tick 加速拉弓/装填；交易 + 30 档附魔台（I）+ 古城/末地城。
- 新增 sunder 模块：斧、RARE、I–IV、非宝藏；斧暴击且有护甲才触发；10s 碎甲、7s 冷却；
  攻击者专属护甲/韧性穿透（药水排除）；护盾格挡 +50% 盾耐久且不触发/不扣冷却；
  碎甲图标用 18×18 PNG 自定义粒子（半透明上浮淡出）。
- 0.6.1 修复：铁砧禁止把碎甲上到剑等非斧物品；图标改到头顶上方并放大；
  移除每 tick 的 pending 清理；中文名改为“碎甲”，JAR 内 ID 仍是 `inscribe:sunder`。
- 0.6.2 加固：铁砧守卫 + 每秒背包清理（非斧/非书上的碎甲移除）；护甲判定改用 `getArmorValue()`；
  粒子改 `registerSpriteSet`；新增低频日志便于定位触发/粒子/清理。
- 0.6.3 收紧铁砧：只要碎甲涉及且左槽不是斧，清空结果（书左/剑右也不再绕过）；日志 `Anvil sunder check`。
- 0.6.4 修正：`AnvilUpdateEvent` 必须 `setCanceled(true)`；空输出会被 Forge 当作放行原版，反而敲上。
- 0.6.4 干净版：删除 `SunderDamageProbe` 和测试日志，重建并替换游戏 mods；测试数据已存档。
- 新增 life_siphon 模块：剑、RARE、仅 I；20% 最终伤害吸血（向下取整、上限4、满血不触发、亡灵无效）；
  击杀非亡灵额外回2；1s 冷却；与亡灵杀手冲突；红色受击粒子；交易 + 30档 + 古城/末地城。
- 0.7.1 核心统一限制：所有 inscribe 附魔在铁砧上按目标 canEnchant 校验，非法组合取消事件；
  每20 tick清理非法装备上的附魔；生命汲取粒子移到头顶上方。
- 0.7.2：生命汲取只在服务端结算，避免客户端/服务端重复回血。
- 0.7.3：雷击改用 `setVisualOnly(true)` 视觉闪电，手动只对目标造成一次 5 点雷击伤害；
  去掉范围伤害/火焰/免疫窗口，彻底避免近身误伤。
- 0.7.2 回退版：撤销 0.7.3 的视觉化，恢复真实 LightningBolt；保留短窗口直接雷击免疫，
  接受范围/火焰误伤作为玩法风险。0.7.3 jar 备份在 `backups/0.7.3/`。
- 0.7.4：生命汲取粒子改为红色尘暴24 + 红心8 + 心形4，身体中部向四周爆开。
- 新增 lava_walker 模块：靴子、VERY_RARE、I–II、宝藏、与冰霜行者冲突；自定义 `lava_shell` 方块，
  内圈常驻、外圈约4.5秒裂开回熔岩；石镐及以上固定10 tick、无掉落、无创造物品；古城/猪灵堡垒获取。
- 0.8.1 修复：熔岩壳寿命 9s；范围改圆形且常驻/移动同半径；`noOcclusion` + `cutout` 透出岩浆；
  静止不再补壳（静止挖掘保持熔岩，移动挖掘恢复）。
- 新增 immolation 模块：斧/剑、RARE、I–III、非宝藏、无冲突；MONSTER 非 BOSS 击杀的掉落物
  按数量转经验 5/8/12；BOSS/玩家/非怪物/无掉落不转换，死亡经验保留；图书管理员/30档/古城/堡垒。
- 0.9.1 获取统一：所有附魔默认附魔台；战利品箱为全部 `chests/*`（排除 `end_city_treasure`，
  10% 概率）；移除图书管理员交易、钓鱼、古城/末地城/猪灵堡垒专用过滤；雷霆/熔岩行者非宝藏。
- 0.9.2：新增 `InscribeTrades`，自动给所有 inscribe 附魔加 4 级图书管理员交易；
  雷击/熔岩行者恢复宝藏并关闭附魔台；战利品箱独立额外出书率 10%→15%。
- 新增 giant_slayer 模块：剑/斧、RARE、I–III、非宝藏、无冲突；近战按目标最大生命
  追加 1%/1.5%/2%，≤500 封顶 2/4/8，>500 不封顶；参与护甲/抗性结算。
- 0.9.4：巨人杀手 >500 的追加部分改在 LivingDamageEvent 加，护甲/保护无效；
  ≤500 仍在 LivingHurtEvent 加，保留护甲减伤。
- 新增 unscathed 模块：胸甲、RARE、仅I、非宝藏、无冲突；10秒无实际伤害获得护盾，
  带攻击者伤害最终变1并消耗护盾，无攻击者伤害重置计时但不消耗；消耗后10秒重新充能。
- 1.0.2 修复：任何实际伤害都消耗护盾（包括摔伤/火焰/溺水）；无攻击者伤害不触发变1但破盾。
- 1.0.3：焚灭每掉落物 3/5/8；生命汲取吸血/击杀回复后通过 S2C 包设置原版
  `healthBlinkTime`，生命栏心形抖动；核对焚灭无重复发放。
- 1.0.4：生命汲取仅排除 `EntityTypeTags.SKELETONS`，其他亡灵生效；新增客户端
  “手持生命汲取且生命上升”兜底抖动；焚灭改为 2/4/6。
- 1.0.5：移除生命汲取对亡灵杀手的 `checkCompatibility` 限制；语言/文档同步。
- 1.0.6：新增 frost_thorns 模块/自定义 MobEffect；受击概率给攻击者冰棘，
  玩家/非玩家不同数值；单图标，减速/攻击速度走属性、挖掘走 BreakSpeed。
- 1.0.7：冰棘概率调整：非玩家 40/60/90%，玩家 20/35/50%。
- 1.0.8：新增 beheading；仅斧、宝藏、I、与焚灭冲突；LivingDropsEvent 掉6种原版头颅，
  玩家头写入击杀者 SkullOwner。
- 1.0.9：错误实现（成本3/上限39），已弃用并仅保留备份。
- 1.1.0：重做 AnvilCostRules；书+装备成本32/37/41/45…无上限、20条、纯inscribe书30%减免；
  `AnvilCostOverlay` 隐藏成本≥40时的“过于昂贵”文字；RepairCost封顶10。
- 默认构建只产出一个全量 jar；子集验证包已从 `dist/` 删除。

## 3. 当前卡点

无代码阻塞；只有手测项：

- 远及攻击距离、再跃二段跳高度/水平加速/5 格摔落阈值。
- 雷击：生物目标触发、非生物（船/盔甲架）不触发、冷却 6/3/1.5 秒、攻击者不被自己的雷击伤到。
- 迅击：近战攻击间隔缩短、弓/弩拉弓/装填变快、满级 30 档出现 I 级。
- 碎甲：仅斧暴击、仅对有护甲目标；10s 状态/7s 冷却；图标粒子；切剑/弓仍吃穿透；药水不吃；
  护盾格挡 +50% 盾耐久且不挂状态/不扣冷却；0 护甲不生效。
- 生命汲取：20% 最终伤害吸血、整数/上限4、满血不触发、亡灵无效、击杀回2、红色粒子、与亡灵杀手冲突。
- 靴子+鞘翅同时装备时二段跳优先，二段跳用掉后松开再按跳跃才滑翔。
- 交易/钓鱼/宝箱开箱的实际产出。
- 试炼密室是 1.21 结构，1.20.1 无对应 loot table，未实现。

注意：默认只维护一个全量 jar；`-Pmodules` 只是临时调试能力，当前不需要子集分发。

## 4. 下一步（按顺序，一次只做一件）

1. 用 `build/libs/inscribe-1.2.7.jar` 进世界手测。
2. `/give @p minecraft:enchanted_book{StoredEnchantments:[{id:"inscribe:outreach",lvl:2}]}`，铁砧打剑/斧。
3. `/give @p minecraft:enchanted_book{StoredEnchantments:[{id:"inscribe:double_jump",lvl:2}]}`，打靴子。
4. `/give @p minecraft:enchanted_book{StoredEnchantments:[{id:"inscribe:thunderstrike",lvl:3}]}`，
   打剑后攻击生物，验证冷却、非生物不触发、攻击者不受伤。
5. `/give @p minecraft:enchanted_book{StoredEnchantments:[{id:"inscribe:swift_strike",lvl:3}]}`，
   分别打剑/弓/弩，验证攻击间隔和拉弓/装填速度。
6. `/give @p minecraft:enchanted_book{StoredEnchantments:[{id:"inscribe:sunder",lvl:4}]}`，
   打斧后跳劈有甲目标：看碎甲图标、10s 状态、7s 冷却；切剑/弓继续打；用药水确认不吃穿透；
   用盾格挡确认盾耐久多掉且不挂状态/不扣冷却；打 0 甲目标确认不触发。
7. `/give @p minecraft:enchanted_book{StoredEnchantments:[{id:"inscribe:life_siphon",lvl:1}]}`，
   打剑后攻击非亡灵目标，验证吸血、整数/上限4、满血不触发、击杀回2、红色粒子；打亡灵确认无效。
8. 验证 5 格摔落、水平加速、鞘翅优先级、交易/鱼/宝箱。

## 5. 已排除的尝试（**不要再试**）

- 用项目自带 JDK 17 跑 build：`Files.isWritable()` 恒 false，AT 的 zipfs 只读，必失败。
- 把 AT 输出目录换成无空格 junction：同样失败，不是路径空格问题。
- 手工生成 mapped jar 后重跑：Gradle 会重新执行 AT 并覆盖。
- 走 `/sponge/` 仓库：上游 `repo.spongepowered.org` 经 7890 返回 WinError 10061；
  已从 `build.gradle` 移除该仓库，改走 Forge maven（可代理 modlauncher/Mixin）。
- 给每个模块各放一份 `META-INF/services/...InscribeModule`：资源合并会互相覆盖，只保留一个模块。
  现在由 `generateModuleService` 统一生成。

## 6. 关键文件地图

以下路径相对 `inscribe/`：

| 路径 | 作用 |
|---|---|
| `build.gradle` | 平台/镜像/runs/toolchain，`-Pmodules` 源集选择，生成 service 文件 |
| `src/main/java/com/hekaijun/inscribe/Inscribe.java` | 主类，加载模块 |
| `src/main/java/com/hekaijun/inscribe/core/` | `InscribeModule`、`ModuleLoader`、`LootInjection`、共享 loot/offer helper |
| `src/outreach/java/.../enchantment/outreach/` | 远及附魔、攻击距离、钓鱼、交易、loot entries |
| `src/double_jump/java/.../enchantment/doublejump/` | 再跃附魔、二段跳、摔落、鞘翅、网络/客户端、交易/loot entries |
| `src/thunderstrike/java/.../enchantment/thunderstrike/` | 雷击附魔、攻击落雷/冷却/免伤、交易/loot entries |
| `src/swift_strike/java/.../enchantment/swiftstrike/` | 迅击附魔、攻击速度/拉弓加速、交易/loot entries |
| `src/sunder/java/.../enchantment/sunder/` | 碎甲附魔、暴击状态/穿透/盾耐久、`client/` 粒子 |
| `src/life_siphon/java/.../enchantment/lifesiphon/` | 生命汲取、吸血/击杀回复/粒子、交易/loot entries |
| `src/lava_walker/java/.../enchantment/lavawalker/` | 熔岩行者、`lava_shell` 方块、玩家 tick/挖掘/战利品 |
| `src/lava_walker/resources/assets/inscribe/` | 三阶段方块贴图、模型、blockstate |
| `src/immolation/java/.../enchantment/immolation/` | 焚灭、击杀掉落转经验、交易/loot entries |
| `src/giant_slayer/java/.../enchantment/giantslayer/` | 巨人杀手、最大生命追加伤害 |
| `src/unscathed/java/.../enchantment/unscathed/` | 无伤、10 秒充能护盾 |
| `src/main/java/.../core/InscribeNetwork.java` | S2C 生命栏抖动包 |
| `src/main/java/.../client/ClientHealthShake.java` | 原版 healthBlinkTime 抖动 |
| `src/frost_thorns/java/.../enchantment/frostthorns/` | 冰棘附魔、自定义 MobEffect、减速/挖掘逻辑 |
| `src/frost_thorns/resources/.../mob_effect/frost_thorns.png` | 18×18 冰棘图标 |
| `src/beheading/java/.../enchantment/beheading/` | 斩首、头颅掉落 |
| `src/sunder/resources/assets/inscribe/particles/sunder.json` | 把碎甲 PNG 加进粒子 atlas |
| `src/sunder/resources/assets/inscribe/textures/particle/sunder.png` | 用户提供的 18×18 图标 |
| `src/main/resources/assets/inscribe/lang/` | 五个附魔的中英文 key |
| `build/libs/inscribe-1.2.7.jar` | 当前唯一产物（全量） |
| `README.md` / `LICENSE` / `src/main/resources/logo.png` | 发布说明、MIT 许可证、临时 256×256 logo |
| `backups/0.2.3/` | 模块化前的 0.2.3 jar + 源码快照 |
| `docs/COMMANDS.md` | 构建/运行/排障 |

工作区共享（相对 `inscribe/`）：`../shared/.build-tools/`、`../shared/.gradle-home/`、`../shared/tools/`。

## 7. 复现命令

```text
cd inscribe
export JAVA_HOME="C:/Program Files/Microsoft/jdk-21.0.8.9-hotspot"

全量（默认，唯一产物）：
  ../shared/.build-tools/gradle-8.5/gradle-8.5/bin/gradle.bat -g ../shared/.gradle-home clean build

起客户端：
  ../shared/.build-tools/gradle-8.5/gradle-8.5/bin/gradle.bat -g ../shared/.gradle-home runClient
```

注意：默认只产出 `build/libs/inscribe-1.2.7.jar`；不需要子集构建。历史版本已归档到桌面 `附魔/`。

## 1.1 版本号规则

- 当前基线：`1.0.0`。
- 每次新增附魔或进行一次修改：**最后一位 +1**。
- 满 10 进 1：`1.0.9` → `1.1.0`，`1.1.9` → `1.2.0`。
- 不在第二位/第三位单独加，只有末位进位时才变化。
- 下一次改动的版本应为 `1.2.8`。

前提：在工作区根目录用**真实 Python 解释器**运行 `shared/tools/local_maven_proxy.py`（127.0.0.1:8765）并保持运行。

## 8. 最后一条真实错误

```text
[main/ERROR] [minecraft/Settings]: Failed to load properties from file: server.properties
java.nio.file.NoSuchFileException: server.properties
```

开发服务端首次启动的正常提示；服务端随后 `Done`。模块化构建/客户端/服务端均无 `inscribe` 相关 ERROR。
