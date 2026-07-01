package com.iluha168.mc4d;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
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
    public static final int COLOR_ANA  = ARGB.color(170, 80 , 255);
    public static final int COLOR_KATA = ARGB.color(80 , 255, 80 );

    /**
     * @param dw Distance along W in range [-1; 1].
     * @return tint color for application with {@link ARGB#alphaBlend}.
     */
    public static int getTintColor(double dw) {
        return ARGB.color(
            2 / (1 + (float) Math.exp(-Math.abs(0.2 * dw))) - 1, // Makes rendered stuff that is closer less tinted using a sigmoid
            ARGB.transparent(dw > 0 ? MC4DClient.COLOR_ANA : MC4DClient.COLOR_KATA)
        );
    }

    public static final Identifier NEIGHBOURING_SLICE_BLOCK_RENDERER = Identifier.fromNamespaceAndPath(MC4D.MODID, "neighbouring_slice_block_renderer");
    /** Use this to see the target block outline cleanly. Useful for debugging voxel shapes. */
    public static final Identifier NO_BLOCK_MODEL_RENDERER = Identifier.fromNamespaceAndPath(MC4D.MODID, "no_block_model_renderer");
    public static final Identifier NEIGHBOURING_SLICE_PARTICLE_RENDERER = Identifier.fromNamespaceAndPath(MC4D.MODID, "neighbouring_slice_particle_renderer");
    public static final Identifier NEIGHBOURING_SLICE_ENTITY_RENDERER = Identifier.fromNamespaceAndPath(MC4D.MODID, "neighbouring_slice_entity_renderer");

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