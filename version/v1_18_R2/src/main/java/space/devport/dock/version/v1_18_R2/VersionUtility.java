package space.devport.dock.version.v1_18_R2;

import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.version.api.IVersionUtility;

public class VersionUtility implements IVersionUtility {

    @Override
    public void sendJsonMessage(@NotNull Player player, @NotNull String json) {
        IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a(json);
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(iChatBaseComponent, ChatMessageType.a, player.getUniqueId());
        CraftPlayer craftPlayer = (CraftPlayer) player;
        craftPlayer.getHandle().b.a(packetPlayOutChat); // #sendPacket(packetPlayOutChat);
    }
}
