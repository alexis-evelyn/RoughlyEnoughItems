package me.shedaniel.rei;

import me.shedaniel.rei.client.ClientHelper;
import me.shedaniel.rei.listeners.ClientTick;
import me.shedaniel.rei.listeners.IListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.events.client.ClientTickEvent;
import net.fabricmc.fabric.networking.CustomPayloadPacketRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoughlyEnoughItemsCore implements ClientModInitializer, ModInitializer {
    
    public static final Logger LOGGER = LogManager.getFormatterLogger("REI");
    public static final Identifier DELETE_ITEMS_PACKET = new Identifier("roughlyenoughitems", "deleteitem");
    public static final Identifier CREATE_ITEMS_PACKET = new Identifier("roughlyenoughitems", "createitem");
    private static final List<IListener> listeners = new ArrayList<>();
    
    public static <T> List<T> getListeners(Class<T> listenerClass) {
        return listeners.stream().filter(listener -> {
            return listenerClass.isAssignableFrom(listener.getClass());
        }).map(listener -> {
            return listenerClass.cast(listener);
        }).collect(Collectors.toList());
    }
    
    @Override
    public void onInitializeClient() {
        registerREIListeners();
        registerFabricEvents();
    }
    
    private void registerREIListeners() {
        registerListener(new ClientHelper());
    }
    
    private IListener registerListener(IListener listener) {
        listeners.add(listener);
        return listener;
    }
    
    private boolean removeListener(IListener listener) {
        if (!listeners.contains(listener))
            return false;
        listeners.remove(listener);
        return true;
    }
    
    private void registerFabricEvents() {
        ClientTickEvent.CLIENT.register(minecraftClient -> {
            getListeners(ClientTick.class).forEach(clientTick -> clientTick.onTick(minecraftClient));
        });
    }
    
    @Override
    public void onInitialize() {
        registerFabricPackets();
    }
    
    private void registerFabricPackets() {
        CustomPayloadPacketRegistry.SERVER.register(DELETE_ITEMS_PACKET, (packetContext, packetByteBuf) -> {
            ServerPlayerEntity player = (ServerPlayerEntity) packetContext.getPlayer();
            if (!player.inventory.getCursorStack().isEmpty())
                player.inventory.setCursorStack(ItemStack.EMPTY);
        });
        CustomPayloadPacketRegistry.SERVER.register(CREATE_ITEMS_PACKET, (packetContext, packetByteBuf) -> {
            ServerPlayerEntity player = (ServerPlayerEntity) packetContext.getPlayer();
            ItemStack stack = packetByteBuf.readItemStack();
            if (player.inventory.insertStack(stack.copy()))
                player.sendChatMessage(new TranslatableTextComponent("text.rei.cheat_items", stack.getDisplayName().getFormattedText(), stack.getAmount(), player.getEntityName()), ChatMessageType.SYSTEM);
            else player.sendChatMessage(new TranslatableTextComponent("text.rei.failed_cheat_items"), ChatMessageType.SYSTEM);
        });
    }
    
}