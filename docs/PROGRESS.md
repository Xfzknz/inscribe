# PROGRESS — Inscribe / 附魔铭刻

> 「做到哪了」的唯一事实来源。每完成一小步就更新这里，别靠对话记忆。
> 规格：`docs/SPEC.md`　命令：`docs/COMMANDS.md`　决策：`docs/DECISIONS.md`　交接：`docs/HANDOFF.md`

## 当前状态

- **版本**：`1.2.7`
- **工程目录**：`inscribe/`；可复用工具链/缓存在 `shared/`（`.build-tools/`、`.gradle-home/`、`tools/`）
- **平台**：Minecraft 1.20.1 + Forge 47.2.0，Java 17 字节码（JDK 21 构建）
- **阶段**：功能开发（远及 + 再跃 + 雷击 + 迅击 + 碎甲 + 生命汲取 + 熔岩行者 + 焚灭 + 巨人杀手 + 无伤 + 冰棘 + 斩首 + 追猎，源码模块化，默认单 jar）
- **产物**：`build/libs/inscribe-1.2.7.jar`（171,910 字节，全量：十三个附魔都在里面）

## 里程碑

| # | 目标 | 状态 | 验证方式 |
|---|---|---|---|
| 1 | 空 mod 能加载 | ✅ | `runClient` 已进主菜单，日志有 `Inscribe inscribe loaded`、无 ERROR |
| 2 | 远及附魔（注册 + 获取） | 🟡 | 注册 true、宝箱注入、附魔台 cost30/31=true；攻击距离/交易/钓鱼待进世界手测 |
| 3 | 再跃附魔（注册/获取/二段跳） | 🟡 | 注册/网络/30档/宝箱日志通过；二段跳手感待进世界手测 |
| 4 | 雷击附魔（注册/获取） | 🟡 | 注册/交易/7 条 loot 日志通过；雷击伤害/冷却/免伤待手测 |
| 5 | 迅击附魔（注册/获取/加速） | 🟡 | 注册/交易/10 条 loot 日志通过；攻击间隔/拉弓加速待手测 |
| 6 | 碎甲附魔（暴击/状态/穿透/图标） | 🟡 | 注册/交易/14 条 loot 日志通过；暴击/格挡/图标/穿透待手测 |
| 7 | 生命汲取附魔（吸血/击杀回复） | 🟡 | 注册/交易/15 条 loot 日志通过；吸血/击杀回复/粒子待手测 |
| 8 | 附魔模块化（默认单 jar） | ✅ | 源码按模块拆，默认构建只产出一个全量 jar |
| 9 | 注册第一个物品 | ⬜ | 创造模式物品栏能拿到 |

状态用 ⬜ 未开始 / 🟡 进行中 / ✅ 完成 / ⚠️ 有已知问题。

## 待办

- [x] 取 Forge 1.20.1-47.2.0 MDK，裁剪为最小骨架（`buildscript` + 本地镜像仓库）
- [x] `gradle build` 产出 jar
- [x] `runClient` 进主菜单（7 个缺失 assets 已补齐）
- [x] 注册远及附魔（RARE / I–II / 剑斧 / 每级 +1 格攻击距离）
- [x] 图书管理员交易、钓鱼、古城/末地城宝箱获取代码
- [x] 附魔台满级 30 档出现（只出 I 级；II 级靠铁砧/宝箱/交易）
- [x] 再跃（Double Jump）：靴子、I–II、网络包、客户端跳跃键、服务端一次滞空一次
- [x] 再跃获取：图书管理员/30 档/古城/末地城（与远及共享宝箱池）
- [ ] 进世界手测：远及攻击距离、再跃二段跳（I 2.5 / II 3.5 格）、交易/钓鱼/开箱
- [ ] 进世界手测：靴子+鞘翅同时装备时，二段跳优先；用掉后松开再按跳跃才滑翔
- [ ] 进世界手测：雷击对生物触发、非生物不触发、冷却 6/3/1.5 秒、攻击者不受伤
- [ ] 进世界手测：迅击近战攻击间隔变短；弓/弩拉弓/装填变快；30 档附魔台出现 I 级
- [ ] 进世界手测：碎甲仅斧暴击触发、仅对有护甲目标；10s 状态/7s 冷却；碎甲图标和半透明淡出
- [ ] 进世界手测：碎甲后切剑/弓仍吃穿透，药水不吃；护盾格挡 +50% 盾耐久且不挂状态/不扣冷却
- [ ] 进世界手测：生命汲取吸血 20%、整数/上限 4、满血不触发、亡灵无效、击杀回 2、红色粒子
- [ ] 进世界手测：所有附魔只能上对应装备；铁砧不能把书贴到裤子/非法武器，旧非法附魔会被清理
- [ ] 进世界手测：熔岩行者 I/II 范围、内圈常驻、外圈 4.5 秒裂开变熔岩、贴图三阶段
- [ ] 进世界手测：斩首斧击杀僵尸/骷髅/凋灵骷髅/苦力怕/末影龙/玩家，检查头颅与玩家皮肤
- [ ] 进世界手测：斩首与焚灭铁砧互斥；非玩家直接击杀/箭/火焰不掉头
- [ ] 进世界手测：BOSS（凋灵/末影龙）与玩家掉落保留；动物/村民不触发；怪物死亡经验仍在
- [ ] 默认构建只产出一个全量 jar；`-Pmodules` 仅临时调试，不需要子集
- [ ] 试炼密室：1.20.1 无此结构，待决定替代结构或以后升 1.21
- [ ] 可选：占位物品进创造模式物品栏

## 工作记录（按时间倒序，只追加）

```text
2026-11-02 18:05  1.2.7：替换为用户设计的 Inscribe 封面；README/版本/构建产物同步。
2026-11-02 17:20  回退1.2.7：确认僵尸基础护甲2点，0甲不触发碎甲是原版数据；恢复1.2.6并安装归档jar。
2026-11-02 17:17  1.2.7：碎甲不再跳过0护甲目标；斧暴击统一显示碎甲图标，0甲仅无穿透增伤。
2026-11-02 16:35  1.2.6：追猎改为宝藏附魔；关闭附魔台，保留战利品箱/图书管理员获取。
2026-11-02 16:09  1.2.5：删除自定义 InscribeTrades；模组附魔完全走原版 EnchantBookForEmeralds 池。
2026-11-02 15:58  1.2.4：修复新手交易池被13条模组报价淹没；改为1条35%概率随机报价，最多1条。
2026-11-02 15:45  1.2.3：图书管理员交易从4级改为新手1级；所有 inscribe 附魔可在1级刷新。
2026-11-02 13:18  1.2.2：去掉 logo 外圈光晕，保留紫染、斜向附魔光带与高光；发布构建。
2026-11-02 13:03  1.2.1：临时logo叠加紫色附魔光带、外发光与高光；保持原版附魔书贴图。
2026-11-02 12:59  1.2.0：临时logo改用原版附魔书贴图（放大256×256，居中留边）；版本进位。
2026-11-02 12:54  1.1.9：发布前整理。显示名改为 Inscribe / 附魔铭刻；作者 KaiJun；MIT；
                  加入 README、LICENSE、临时 logo、中英简介与分类概览，允许整合包收录。
2026-11-02 08:43  1.1.8：追猎群伤不再误清零，主目标记录2tick；焚灭保留精英怪掉落
                  （坚守者/远古守卫者/劫掠兽/唤魔者/猪灵蛮兵/最大生命≥80）。
2026-11-02 08:34  1.1.7：修复旧物品高位次数从0开始。初始step取 max(NBT, 词条数-8)；
                  13条旧剑下一次约61-65，7/8条附近约41。
2026-11-01 22:55  1.1.6：新增追猎（hunt）。剑、RARE、III；同目标5层，每层3/5/7%，4秒；
                  buff图标等级同步、无倒计时；3层以上红色环绕粒子；miss发包清零。
2026-11-01 20:33  1.0.9：铁砧优化（已弃用：错误地把成本压成3/上限39）。
2026-11-01 19:54  1.0.8：新增斩首（beheading）。仅斧、RARE、I、宝藏、与焚灭冲突；
                  LivingDropsEvent 中玩家直接斧击杀6种原版头颅目标掉头，玩家头写入击杀者SkullOwner；
                  箱子28条/42表、末地城0条。
2026-11-01 19:40  1.0.7：冰棘触发概率调整：非玩家40/60/90%，玩家20/35/50%。
2026-11-01 18:43  1.0.6：新增冰棘（frost_thorns）。全部护甲、RARE、I–III、非宝藏、无冲突；
                  受击15/30/45%给攻击者施加自定义冰棘效果，玩家/非玩家不同减速/挖掘/时长；
                  单一效果图标，减速+攻击速度走属性，挖掘走BreakSpeed；箱子27条/42表、末地城0条。
2026-11-01 17:59  1.0.5：移除生命汲取与亡灵杀手的互斥，二者可同时附魔。
2026-11-01 17:49  1.0.4：生命汲取改为仅排除 EntityTypeTags.SKELETONS，其他亡灵可生效；
                  客户端增加“手持生命汲取且生命上升”的兜底抖动触发；焚灭改为2/4/6。
2026-11-01 17:33  1.0.3：焚灭每掉落物经验改为3/5/8；生命汲取吸血/击杀回复后通过
                  S2C 包触发原版 healthBlinkTime，生命栏抖动；核对焚灭无重复发放，
                  经验跳变来自原版等级曲线与按物品数量结算。
2026-11-01 17:03  1.0.2：修复无伤护盾。任何实际伤害都会消耗护盾，不再只由带攻击者伤害消耗；
                  无攻击者伤害不触发变1，但会破盾并重置计时；之后需重新10秒充能。
2026-11-01 16:54  1.0.1：新增无伤（unscathed）。胸甲、RARE、仅I、非宝藏、无冲突；
                  连续10秒无实际伤害获得一次护盾，带攻击者伤害最终变为1点并消耗护盾，
                  无攻击者伤害不消耗但重置计时；消耗后10秒重新充能；箱子24条/42表、末地城0条。
2026-11-01 16:29  1.0.0：版本基线，功能与 0.9.4 相同；启用“末位+1、满10进位”的版本规则。
2026-11-01 16:07  0.9.4：巨人杀手 >500 最大生命的追加伤害改走 LivingDamageEvent，
                  护甲/保护结算后再加，绕过护甲（类似真实伤害，仍可被伤害吸收吸收）；
                  ≤500 仍走 LivingHurtEvent 参与护甲结算。
2026-11-01 16:02  0.9.3：新增巨人杀手（giant_slayer）。剑/斧、RARE、I–III、非宝藏、无冲突；
                  近战命中按目标最大生命追加1%/1.5%/2%，最大生命≤500时封顶2/4/8，>500不封顶；
                  追加伤害在LivingHurtEvent参与护甲/抗性；箱子23条/42表、末地城0条。
2026-11-01 15:30  0.9.2：恢复图书管理员交易，新增核心 InscribeTrades 自动遍历所有 inscribe 附魔，
                  后续新增附魔自动进 4 级交易；雷击/熔岩行者改回宝藏（不进附魔台，仍可箱子/交易）；
                  战利品箱独立额外出书率 10%→15%（原版书池不受影响）。
2026-11-01 15:21  0.9.1：获取途径统一。所有附魔改为战利品箱（全部 minecraft:chests/*，
                  排除不产附魔书的 end_city_treasure，10% 概率）+ 默认附魔台；
                  移除图书管理员交易、钓鱼 2%、古城/末地城/猪灵堡垒专用过滤；
                  雷霆/熔岩行者改为非宝藏以解除附魔台过滤。服务端注入 42 个 chest 表，各20条。
2026-11-01 15:07  0.9.0：新增焚灭（immolation）。RARE、I–III、斧/剑、非宝藏、无冲突；
                  LivingDropsEvent（掠夺结算后）把 MONSTER 非 BOSS 击杀的掉落物按数量转经验
                  （5/8/12），BOSS/玩家/非怪物保留，死亡经验不动；图书管理员/30档/古城/猪灵堡垒。
2026-11-01 14:44  0.8.1：熔岩壳修复。寿命 4.5s→9s；移动/常驻范围改圆形且同半径；
                  数据面加 noOcclusion + 模型 cutout，破损透明区域可透出岩浆；
                  静止不再做补壳扫描，静止挖掘后保持熔岩，移动挖掘才恢复壳。
2026-11-01 14:27  0.8.0：新增熔岩行者（lava_walker）模块。VERY_RARE、I–II、靴子、宝藏、与冰霜行者冲突；
                  玩家移动时将半径 3/4 内岩浆替换为自定义 lava_shell 方块，内圈 3×3 持续刷新，
                  外圈约4.5秒按完整→一级破碎→二级破碎→熔岩；石镐及以上固定10 tick、无掉落、无创造物品；
                  挖掘时移动则恢复壳、静止则变熔岩；古城17条/猪灵堡垒各2条/末地城15条。
2026-11-01 12:17  0.7.4：生命汲取粒子加强。红色尘暴24颗 + 红心8颗 + 心形4颗，生成在目标身体中部，
                  带扩散速度；不再只在头顶发1-2颗红心。
2026-11-01 12:14  0.7.2 回退版：用户更喜欢真实闪电的视觉，并接受贴身误伤风险；撤销 0.7.3 的
                  visualOnly + 单目标伤害，恢复真实 LightningBolt（保留短窗口直接雷击免疫）。
                  全局目标限制、生命汲取服务端结算/粒子等修复保留。
2026-11-01 12:09  0.7.3：修复雷击误伤。原实现生成真实闪电，范围伤害/火焰不受 10 tick 免疫窗口控制；
                  改为 setVisualOnly(true) 的视觉闪电 + 只对目标 target.hurt(lightningBolt, 5) 一次，
                  不再有范围伤害/点燃/免疫窗口，攻击者结构上不会被自己的雷击打到。
2026-11-01 11:56  0.7.2：生命汲取加服务端侧过滤，避免客户端/服务端重复结算；粒子保持在头顶上方多颗。
2026-11-01 11:54  0.7.1：修复附魔目标限制。核心新增 EnchantmentRestrictions：所有 inscribe 附魔在铁砧上
                  必须满足目标物品 canEnchant（取消事件），并每20 tick清理非法装备上的附魔；
                  移除 Sunder 专用守卫。生命汲取粒子改到头顶上方并增加数量。
2026-11-01 11:38  0.7.0：新增生命汲取（life_siphon）模块。剑、RARE、仅 I；20% 最终伤害吸血向下取整，
                  单次上限4，满血不触发，亡灵无效；击杀非亡灵额外回2；1s冷却；与亡灵杀手冲突；
                  红色受击粒子；交易/30档附魔台/古城/末地城；全量两张表各15条。
2026-10-31 20:32  0.6.4 干净版：移除 SunderDamageProbe 和全部测试日志；重建 60,693 字节 jar，
                  已替换游戏 mods 里的探针包。测试数据保存在 docs/sunder-damage-2026-10-31.log。
2026-10-31 20:00  0.6.4：找到铁砧根因。AnvilUpdateEvent 是 @Cancelable，之前 setOutput(EMPTY) 的语义是
                  “未处理、放行原版”，所以原版继续把碎甲敲上去；现改为 setCanceled(true)。
                  左槽非斧且涉及碎甲时直接取消，原版不再计算。
2026-10-31 19:53  0.6.3：铁砧规则收紧。只要碎甲出现在铁砧两侧/输出，而左槽不是斧子，就清空结果；
                  书左/剑右也不再绕过；新增 Anvil sunder check 日志。保留每秒背包清理兜底。
                  用户已确认 0.6.2 的图标显示和劈斧卡顿修复。
2026-10-31 19:45  0.6.2：碎甲鲁棒性修复。铁砧守卫 + 每秒背包兜底清理，非斧/非书物品上的碎甲会被移除；
                  护甲判定改用 LivingEntity.getArmorValue()（覆盖穿甲僵尸/怪物）；
                  粒子改为标准 SpriteSet 注册，避免每次创建粒子查 atlas；
                  移除每 tick 的 pending 清理；新增低频日志：Sunder applied / particle created / removed from invalid item。
2026-10-31 19:35  0.6.1：修复碎甲。铁砧禁止把碎甲应用到剑等非斧物品；图标改到头顶上方、
                  放大到 0.5、不透明度 0.8，并换成新 18×18 PNG；移除每 tick 的 pending 清理；
                  中文名改为“碎甲”，附魔 ID 仍为 inscribe:sunder。
2026-10-31 19:12  0.6.0：新增碎甲（sunder）模块。斧暴击、目标有护甲才触发；10s 碎甲、7s 冷却；
                  攻击者专属护甲/韧性穿透（LivingHurt 记原始伤害，LivingDamage 加穿透增量，药水排除）；
                  护盾格挡时 +50% 盾耐久且不挂状态/不扣冷却；0 护甲不生效；
                  碎甲图标走自定义粒子（18×18 PNG + particles/sunder.json + 半透明上浮淡出）。
                  全量两张表各 14 条书条目。
2026-10-31 18:00  0.5.0：新增迅击（swift_strike）模块。剑/斧/弓/弩/三叉戟，RARE，I–III，非宝藏；
                  近战用 ATTACK_SPEED +10/20/30%，弓/弩用 UseItem Tick 额外减少剩余使用时间；
                  交易 + 满级附魔台 30 档（I）+ 古城/末地城；全量两张表各 10 条书条目。
2026-10-31 17:45  0.4.0：新增雷击（thunderstrike）模块。剑、RARE、I–III、宝藏、无附魔台；
                  攻击生物时在目标处生成闪电，冷却 120/60/30 tick；攻击者 10 tick 内免疫雷击伤害；
                  非生物/盔甲架不触发；交易 + 古城/末地城；全量服务端两张表各 7 条书条目。
2026-10-31 17:30  按用户澄清：不需要子集 jar。删除 dist 下的 outreach/double_jump/full 副本，
                  只保留 build/libs/inscribe-0.3.0.jar（全量，两个附魔都在里面）。
                  源码模块化保留用于维护，默认构建不分包。
2026-10-31 17:25  0.3.0 模块化完成：附魔拆到 src/outreach、src/double_jump，各自 DeferredRegister/事件/网络；
                  核心用 InscribeModule + ServiceLoader，Gradle 生成唯一 service 文件；-Pmodules 选择子集。
                  备份 0.2.3 在 backups/0.2.3/。full/outreach/double_jump 三种 jar 均构建；
                  两种子集客户端启动 ERROR 0；全量服务端两张表各注入 4 条书条目。
2026-10-31 17:18  备份 0.2.3：jar SHA-256 4f11a168…，source zip SHA-256 3f69aecd…，见 backups/0.2.3/。
2026-10-31 16:54  0.2.3：修复二段跳水平减速。服务端不再读自己的 deltaMovement（玩家移动是客户端权威，
                  服务端值可能为 0），改为包内带客户端 x/z；服务端校验有限值、封顶 1.0 后乘 1.30，
                  客户端本地也先乘 1.30；网络协议升 2。clean build 15s，客户端 ERROR 0；jar 19,872 字节。
2026-10-31 16:49  0.2.2：再跃优先于鞘翅。客户端空中跳跃且二段跳可用时发包，并压掉跳跃键一 tick
                  阻止原版 START_FALL_FLYING；服务端若已 fall flying 则 stopFallFlying 后执行二段跳。
                  二段跳用掉后不再拦截，松开再按跳跃即可滑翔。clean build 12s；jar 19,501 字节。
2026-10-31 16:44  0.2.1：再跃摔落阈值改为 5 格（LivingFallEvent：<=5 免伤，>5 按 distance-5 算）；
                  二段跳保留水平动量并乘 1.30 瞬时加速。clean build 16s，客户端 ERROR 0；
                  jar inscribe-0.2.1.jar，19,288 字节。
2026-10-31 16:34  版本 0.1.0 -> 0.2.0（功能新增升 minor）：改 gradle.properties 的 mod_version 后
                  clean build 12s 通过，产物 inscribe-0.2.0.jar，jar 内 mods.toml version=0.2.0；
                  旧 0.1.0 已被 clean 移除。
2026-10-31 16:31  再跃最终验证：客户端 Enchantments present: outreach=true, double_jump=true，ERROR 0；
                  服务端确认 30 档 cost29=false/cost30=true，两条宝箱注入均含两本附魔书；
                  专用服务器未加载客户端输入类。jar 18,937 字节。
                  试炼密室为 1.21 结构，1.20.1 无对应 loot table，未添加。
2026-10-31 16:24  再跃实现：客户端 InputEvent.Key 空中按跳跃 -> SimpleChannel 包 -> 服务端校验靴子附魔；
                  每次滞空一次、落地/登出/重生/换维度重置；二段速度 I=0.42（总高约2.50）、II=0.58（约3.48），
                  重置坠落距离并同步速度包。
2026-10-31 15:43  附魔台支持：canApplyAtEnchantingTable 仅剑/斧；getMinCost(1)=30、(2)=45。
                  启动校验 cost29=false、cost30=true、cost31=true（满级 15 书架第三档 30/31），
                  即 30 档只出 I 级；临时校验代码已移除，最终 build 15s 通过。
2026-10-31 15:36  Outreach 最终构建通过（16s、无警告）；jar 10,226 字节，含 4 个 class + 中英文 lang。
                  服务端验证两条宝箱注入日志；客户端注册 true、ERROR 0。
                  默认获取率：图书管理员专家 20–32 绿宝石/3 次、钓鱼 2%、古城 10%、末地城 15%，
                  宝箱内 I:II = 3:1。
2026-10-31 15:34  服务端启动验证：Added Outreach to loot table minecraft:chests/ancient_city /
                  minecraft:chests/end_city_treasure 均出现；无 inscribe 相关 ERROR。
2026-10-31 15:24  构建时 Sponge 上游 WinError 10061 拒绝；移除 build.gradle 的 /sponge/ 仓库，
                  改走 Forge maven（已缓存 modlauncher / Mixin），构建恢复。
2026-10-31 15:20  注册 Outreach 附魔（RARE、I–II、剑/斧），用 ForgeMod.ENTITY_REACH 临时修饰器
                  实现每级 +1 格攻击距离；服务端 tick 更新、客户端属性同步。
2026-10-31 14:46  最终 runClient 复核（移除 copyIdeResources 后）：ERROR 0，
                  有 "Inscribe inscribe loaded"，SoundEngine/TextureAtlas 主菜单标记齐全；已关闭客户端。
2026-10-31 14:44  文件审计通过：源码 3 个文件、构建配置 3 个；jar 仅 4 项（MANIFEST/mods.toml/
                  Inscribe.class/pack.mcmeta），2062 字节；mods.toml 与 pack.mcmeta 解析通过，
                  displayName 码点 U+94ED/U+523B 正确，无未展开 token；class major 61（Java 17）。
                  clean build 19s 通过且无 IDE 警告；运行日志 ERROR 0、有 "Inscribe inscribe loaded"。
                  移除 copyIdeResources 与临时的 run/capture.ps1。
2026-10-31 14:36  主验收通过：runClient 进主菜单（截图 run/main-menu.png），
                  日志有 "Inscribe inscribe loaded"，ERROR 为 0 行。
2026-10-31 14:35  补齐 7 个缺失 assets（Python 代理下载，逐个校验 SHA-1/大小）；
                  runClient 启动成功，资源加载和声音引擎正常。
2026-10-31 14:34  可复用数据收进 shared/：tools、.build-tools、.gradle-home 整体移动；
                  镜像从 shared/tools 重启；从 inscribe/ 用 ../shared 路径 build，12s 通过。
2026-10-31 14:30  工程移入 inscribe/：build.gradle/settings.gradle/gradle.properties/src/docs/build/.gradle。
                  共享 .build-tools/.gradle-home/tools 留在工作区根；从 inscribe/ 重跑 build，16s 通过。
                  删掉 gradle.properties 里指向旧根目录的 JDK 17 installations 路径；文档相对路径同步更新。
2026-10-31 14:27  runClient 失败：downloadAssets 7 个文件 SSL/读取超时，未启动游戏 JVM。
                  缺 lang/lv_lv.json、lang/ru_ru.json 和 5 个 sounds/*.ogg；其余 assets 已缓存。
2026-10-31 14:26  工作区清理：删除 .build-tools/tmp 诊断文件、gradle-8.5-bin.zip（127 MB）、
                  .build-tools/mdk 模板；移除测试用 C:\mcwork junction。
                  源码、build/libs/inscribe-0.1.0.jar、.gradle-home 缓存均未动。
2026-10-31 14:21  build 成功后启动 runClient。首次下载 assets（数千个文件），
                  日志出现少量 resources.download.minecraft.net 的 SSL 握手失败，
                  ForgeGradle 仍在继续下载；主菜单尚未到达。
2026-10-31 14:19  build 成功，1m13s，产出 build/libs/inscribe-0.1.0.jar。
                  根因：项目自带 JDK 17.0.20.1 在本沙箱里 Files.isWritable() 对 Java 自己刚写的文件
                  返回 false，zipfs 因此把新 jar 以只读打开，ForgeGradle 的 AT 处理器必然失败。
                  解决：Gradle 与工具链改用系统 JDK 21（zipfs 正常），mod 用 --release 17 保持 Java 17 字节码。
2026-10-31 14:15  build 已推进到 :compileJava，卡在 ForgeGradle 的 Access Transformer：
                  java.nio.file.ReadOnlyFileSystemException，输出
                  ..._mapped_official_1.20.1.jar 被创建成 22 字节空 zip 后以只读打开。
                  手动运行 AT 8.2.14 fatjar（输出到新路径）同样复现；文件本身可写。
                  依赖/镜像/骨架均无问题；MCP setup 已到 rename 并缓存。
2026-10-31 14:05  按用户要求停止 build。停在 ForgeGradle 首次 MCP setup 的 rename → decompile 之间：
                  下载/合并/rename 已完成，decompile 尚未跑完；无 jar。
                  不是卡死：停止前 daemon CPU 47s、内存 1.28 GB，14:03 仍在写 MCP 缓存。
                  下次重跑复用缓存，剩余长尾是 decompile。
2026-10-31 13:47  build 第二次失败，停在 :_compileJava_1 的 fastutil 8.5.9（23.4 MB）：
                  镜像先整包下载再转发，Gradle 读超时；.part 已接近完整但未 rename。
                  与 8.3.1 同类，连续第 2 次，停止重试。已缓存 client.jar / userdev / 大部分依赖。
2026-10-31 13:35  最小骨架已写入：build.gradle（buildscript + 镜像 clear）、settings.gradle、
                  gradle.properties、Inscribe.java、mods.toml、pack.mcmeta；未用 gradlew。
                  首次 build 被沙箱挡住：Gradle fork 子 JVM 时 CreatePipe error=5（拒绝访问）；
                  独立 Java 子进程测试同样失败，确认是沙箱对带管道子进程的限制，不是 ACL/依赖问题。
                  切到 full access 后复测通过，build 重新进行中。
2026-10-31 13:28  工具链就绪：Gradle 8.5（项目内）+ JDK 17.0.20.1，`gradle -v` 通过。
                  偏差与处理：
                  - 系统 `python` 是 Windows 商店占位程序（exit 49），改用 DSH 自带解释器。
                  - bootstrap_toolchain.py 不识别已解压的 JDK，会重复下载约 190 MB（实测 ~55 KB/s）；
                    停掉后复用它的 download() 只取 Gradle（~720 KB/s），JDK 未动。
                  - 不加 `-g .gradle-home` 时 Gradle 无法写用户目录 `~/.gradle`（沙箱拒绝），
                    native-platform.dll 释放失败；统一加 `-g .gradle-home` 后正常。
2026-10-31 13:26  本地 Maven 镜像 127.0.0.1:8765 已起；/forge/maven-metadata.xml 返回 200（213366 字节）。
```

## 下一步

1. 取 Forge 1.20.1-47.2.0 MDK，裁剪到最小骨架并套用 inscribe 命名。
2. `gradle build` 验证 jar。
