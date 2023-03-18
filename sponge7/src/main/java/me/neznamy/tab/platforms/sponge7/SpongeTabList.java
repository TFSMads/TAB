package me.neznamy.tab.platforms.sponge7;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.neznamy.tab.api.chat.IChatBaseComponent;
import me.neznamy.tab.api.tablist.TabListEntry;
import me.neznamy.tab.api.util.ComponentCache;
import me.neznamy.tab.shared.tablist.SingleUpdateTabList;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.UUID;

@RequiredArgsConstructor
public class SpongeTabList extends SingleUpdateTabList {

    private static final ComponentCache<IChatBaseComponent, Text> textCache = new ComponentCache<>(10000,
            (component, version) -> TextSerializers.JSON.deserialize(component.toString(version)));

    /** Player this TabList belongs to */
    private final SpongeTabPlayer player;

    @Override
    public void removeEntry(@NonNull UUID entry) {
        player.getPlayer().getTabList().removeEntry(entry);
    }

    @Override
    public void updateDisplayName(@NonNull UUID id, IChatBaseComponent displayName) {
        player.getPlayer().getTabList().getEntry(id).ifPresent(
                entry -> entry.setDisplayName(textCache.get(displayName, player.getVersion())));
    }

    @Override
    public void updateLatency(@NonNull UUID id, int latency) {
        player.getPlayer().getTabList().getEntry(id).ifPresent(entry -> entry.setLatency(latency));
    }

    @Override
    public void updateGameMode(@NonNull UUID id, int gameMode) {
        player.getPlayer().getTabList().getEntry(id).ifPresent(
                entry -> entry.setGameMode(convertGameMode(gameMode)));
    }

    @Override
    public void addEntry(TabListEntry entry) {
        GameProfile profile = GameProfile.of(entry.getUniqueId(), entry.getName());
        if (entry.getSkin() != null) profile.addProperty(ProfileProperty.of(
                "textures", entry.getSkin().getValue(), entry.getSkin().getSignature()));
        player.getPlayer().getTabList().addEntry(org.spongepowered.api.entity.living.player.tab.TabListEntry.builder()
                .list(player.getPlayer().getTabList())
                .profile(profile)
                .latency(entry.getLatency())
                .gameMode(convertGameMode(entry.getGameMode()))
                .displayName(textCache.get(entry.getDisplayName(), player.getVersion()))
                .build());
    }

    private GameMode convertGameMode(int mode) {
        switch (mode) {
            case 0: return GameModes.SURVIVAL;
            case 1: return GameModes.CREATIVE;
            case 2: return GameModes.ADVENTURE;
            case 3: return GameModes.SPECTATOR;
            default: return GameModes.NOT_SET;
        }
    }
}