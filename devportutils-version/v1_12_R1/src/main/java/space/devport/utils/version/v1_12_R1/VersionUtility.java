package space.devport.utils.version.v1_12_R1;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.version.api.IVersionUtility;

public class VersionUtility implements IVersionUtility {

    @Override
    public void sendJsonMessage(@NotNull Player player, @NotNull String json) {
        IChatBaseComponent iChatBaseComponent = IChatBaseComponent.ChatSerializer.a(json);
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(iChatBaseComponent);
        CraftPlayer craftPlayer = (CraftPlayer) player;
        craftPlayer.getHandle().playerConnection.sendPacket(packetPlayOutChat);
    }
}
