package be.isach.ultracosmetics.listeners;

import be.isach.ultracosmetics.UltraCosmetics;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class ClientBrandListener implements PluginMessageListener {
    private final UltraCosmetics ultraCosmetics;

    public ClientBrandListener(UltraCosmetics ultraCosmetics) {
        this.ultraCosmetics = ultraCosmetics;
    }

    @Override
    public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] message) {
        if (!channel.equals("minecraft:brand")) {
            return;
        }
        ByteArrayDataInput input = ByteStreams.newDataInput(message);
        // Discard message length
        input.readByte();
        ultraCosmetics.getPlayerManager().getUltraPlayer(player).setClientBrand(input.readLine());
    }
}
