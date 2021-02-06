package space.devport.dock.version.v1_16_R3;

import net.minecraft.server.v1_16_R3.ChatMessageType;
import net.minecraft.server.v1_16_R3.IChatBaseComponent;
import net.minecraft.server.v1_16_R3.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.dock.version.api.IVersionUtility;

public class VersionUtility implements IVersionUtility {

    @Override
    public void sendJsonMessage(@NotNull Player player, @NotNull String json) {
        IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a(json);
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(iChatBaseComponent, ChatMessageType.CHAT, player.getUniqueId());
        CraftPlayer craftPlayer = (CraftPlayer) player;
        craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutChat);
    }
}
