package com.iluha168.mc4d;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = MC4D.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = MC4D.MODID, value = Dist.CLIENT)
public class MC4DClient {
    public static final KeyMapping keyAna = new KeyMapping("key.ana", -1, KeyMapping.Category.MOVEMENT);
    public static final KeyMapping keyKata = new KeyMapping("key.kata", -1, KeyMapping.Category.MOVEMENT);

    public MC4DClient() {
    }

    @SubscribeEvent
    private static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(keyAna);
        event.register(keyKata);
    }
}