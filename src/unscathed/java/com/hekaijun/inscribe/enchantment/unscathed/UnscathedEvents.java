package com.hekaijun.inscribe.enchantment.unscathed;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.hekaijun.inscribe.Inscribe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Inscribe.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class UnscathedEvents {
    private static final long RECHARGE_TICKS = 200L;
    private static final Map<UUID, Long> LAST_DAMAGE = new HashMap<>();
    private static final Set<UUID> SHIELD = new HashSet<>();

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (event.isCanceled() || !(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (unscathedLevel(player) <= 0) {
            return;
        }

        UUID id = player.getUUID();
        long now = player.level().getGameTime();
        Long last = LAST_DAMAGE.get(id);

        boolean hasShield = SHIELD.contains(id);
        if (!hasShield && last != null && now - last >= RECHARGE_TICKS) {
            SHIELD.add(id);
            hasShield = true;
        }

        if (hasShield && event.getAmount() > 0.0F) {
            boolean hasAttacker = event.getSource().getEntity() != null;
            if (hasAttacker && event.getAmount() > 1.0F) {
                event.setAmount(1.0F);
            }
            // 任何实际伤害都会消耗护盾；无攻击者伤害不触发减伤，但同样破盾。
            SHIELD.remove(id);
        }

        // 任何实际伤害都会重置充能；包括摔伤、火焰、溺水等无攻击者伤害。
        LAST_DAMAGE.put(id, now);
    }

    @SubscribeEvent
    public static void onLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        // 登录后从 0 开始充能，避免上线即获得护盾。
        LAST_DAMAGE.put(event.getEntity().getUUID(), event.getEntity().level().getGameTime());
        SHIELD.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        UUID id = event.getEntity().getUUID();
        LAST_DAMAGE.put(id, event.getEntity().level().getGameTime());
        SHIELD.remove(id);
    }

    @SubscribeEvent
    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID id = event.getEntity().getUUID();
        LAST_DAMAGE.remove(id);
        SHIELD.remove(id);
    }

    private static int unscathedLevel(Player player) {
        return player.getItemBySlot(EquipmentSlot.CHEST)
                .getEnchantmentLevel(UnscathedModule.UNSCATHED.get());
    }

    private UnscathedEvents() {
    }
}
