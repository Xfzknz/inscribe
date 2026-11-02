# 附魔铭刻

**Minecraft 1.20.1 · Forge 47.x**

- 作者：KaiJun
- 许可证：MIT
- 版本：1.2.7
- Mod ID：`inscribe`

## 简介

**附魔铭刻**是面向原版玩家与整合包的附魔扩展，不改变原版附魔的基础逻辑。适合原版玩家过渡到模组，也适合作为整合包前期的轻量内容。

模组共新增 **13 个附魔**，没有破坏平衡的超标附魔，也没有凑数的弱附魔。设计重点是补全原版缺失的机制、增加环境特化内容：铁砧支持更多词条，不再轻易提示“过于昂贵”，经验成本也做了线性化优化。数值多采用百分比形式，在原版环境中小幅增强，在高难度整合包中也仍有发挥空间。

## 设计原则

- 不提供飞行、创造模式能力、秒杀或无限资源。
- 不覆盖原版附魔的核心数值，尽量通过机制补足玩法。
- 以环境特化和条件触发为主，数值多采用百分比。
- 原版环境中小幅增强；高难度整合包中仍有发挥空间。
- 单件装备最多 20 条附魔。
- 所有附魔 ID 固定，升级模组不会改变已有书本或装备的 NBT。

## 附魔一览

### 战斗与输出

| 附魔 | 适用 | 效果 |
|---|---|---|
| 远及 | 剑、斧 | 每级攻击距离 +1 格 |
| 雷击 | 剑 | 命中时召唤闪电，冷却 6/3/1.5 秒 |
| 迅击 | 剑、斧、弓、弩、三叉戟 | 攻击冷却或拉弓/装填速度 +10/20/30% |
| 碎甲 | 斧 | 暴击施加 10 秒破甲，攻击者获得护甲穿透 |
| 生命汲取 | 剑 | 20% 吸血，单次上限 4；击杀额外回复 2 |
| 焚灭 | 剑、斧 | 普通怪物掉落物转化为经验；精英怪和 Boss 保留掉落 |
| 巨人杀手 | 剑、斧 | 按目标最大生命追加伤害 |
| 斩首 | 斧 | 击杀指定生物掉落对应头颅 |
| 追猎 | 剑 | 连续命中同一目标叠层，提高近战伤害 |

### 生存、移动与环境

| 附魔 | 适用 | 效果 |
|---|---|---|
| 再跃 | 靴子 | 每次滞空一次二段跳，等级提高高度 |
| 熔岩行者 | 靴子 | 将附近岩浆变成临时熔岩壳 |
| 无伤 | 胸甲 | 10 秒无伤后获得一次免伤护盾 |
| 冰棘 | 全部护甲 | 受击概率给攻击者施加冰棘 |

## 铁砧规则

- 单件装备最多 **20 条**附魔，超过时铁砧不放行。
- 低位操作保持原版成本逻辑。
- 高位成本改为线性增长，不再轻易触发“过于昂贵”。

## 安装

1. 安装 **Minecraft 1.20.1** 和 **Forge 47.x**。
2. 将 `inscribe-1.2.7.jar` 放入游戏的 `mods` 文件夹。
3. 启动游戏，不需要额外依赖。

## 整合包

允许将本模组加入整合包，但请**保留作者与模组名**。

## 链接

- GitHub：https://github.com/Xfzknz/inscribe
- Modrinth：待补充
- CurseForge：待补充
- 问题反馈：https://github.com/Xfzknz/inscribe/issues

## 反馈

发现 Bug 或有平衡建议，请联系作者 **KaiJun**。

## 许可证

MIT，详见 [LICENSE](LICENSE)。

---

# Inscribe

**Minecraft 1.20.1 · Forge 47.x**

- Author: KaiJun
- License: MIT
- Version: 1.2.7
- Mod ID: `inscribe`

## Introduction

**Inscribe** is an enchantment expansion for vanilla players and modpacks alike. It preserves vanilla enchantment fundamentals, making it a gentle step from vanilla into modded play or an early-game modpack addition.

The mod adds **13 enchantments** with no balance-breaking outliers and no filler. The design focuses on filling gaps in vanilla mechanics and adding environment-specific options. The anvil supports more enchantments without hitting "Too Expensive" so easily, and its XP costs have been linearized. Most values are percentage-based, giving a modest boost in vanilla while remaining useful in high-difficulty modpacks.

## Design Principles

- No flight, creative-mode abilities, instant kills, or infinite resources.
- Vanilla enchantment values are not overwritten; gameplay is expanded through mechanics.
- Environment-specific and conditional effects are preferred, with percentage-based values.
- A modest boost in vanilla, with room to remain useful in high-difficulty modpacks.
- Up to 20 enchantments on a single item.
- All enchantment IDs are stable; updating the mod does not change existing book or gear NBT.

## Enchantments

### Combat & Output

| Enchantment | Slot | Effect |
|---|---|---|
| Outreach | Sword, Axe | +1 attack reach per level |
| Thunderstrike | Sword | Calls lightning on hit; cooldown 6/3/1.5s |
| Swift Strike | Sword, Axe, Bow, Crossbow, Trident | +10/20/30% attack or draw speed |
| Sunder | Axe | Critical hits apply a 10s armor break; the attacker gains armor penetration |
| Life Siphon | Sword | 20% lifesteal, max 4 per hit; kill heal +2 |
| Immolation | Sword, Axe | Converts normal monster drops into XP; elites and bosses keep their drops |
| Giant Slayer | Sword, Axe | Bonus damage based on the target's max health |
| Beheading | Axe | Drops matching vanilla heads |
| Hunt | Sword | Consecutive hits on one target build stacks and increase melee damage |

### Survival, Movement & Environment

| Enchantment | Slot | Effect |
|---|---|---|
| Double Jump | Boots | One mid-air jump per airtime; higher levels increase height |
| Lava Walker | Boots | Turns nearby lava into temporary shells |
| Unscathed | Chestplate | A damage shield after 10s without damage |
| Frost Thorns | Armor | Chance to slow and weaken attackers |

## Anvil Rules

- Up to **20 enchantments** per item; the anvil rejects anything beyond that.
- Low-end operations keep vanilla cost logic.
- High-end costs grow linearly instead of easily triggering "Too Expensive".

## Installation

1. Install **Minecraft 1.20.1** and **Forge 47.x**.
2. Put `inscribe-1.2.7.jar` into the game's `mods` folder.
3. Launch the game. No extra dependencies are required.

## Modpacks

You may include this mod in modpacks **with credit to the author and the mod name**.

## Links

- GitHub: https://github.com/Xfzknz/inscribe
- Modrinth: to be added
- CurseForge: to be added
- Issue tracker: https://github.com/Xfzknz/inscribe/issues

## Feedback

If you find a bug or have balance feedback, contact **KaiJun**.

## License

MIT. See [LICENSE](LICENSE).
