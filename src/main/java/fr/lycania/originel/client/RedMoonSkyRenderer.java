package fr.lycania.originel.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import fr.lycania.originel.config.LuneRougeConfig;
import fr.lycania.originel.redmoon.RedMoonState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.joml.Matrix4f;

/**
 * Client-only visuals for the Lune Rouge event. No mixin needed: the moon
 * "recolor" is vanilla's own moon quad (see LevelRenderer#renderSky) drawn a
 * second time, slightly larger, in blood red, right on top of it during the
 * NeoForge AFTER_SKY stage - and the fog color is nudged red the same way
 * the Nether's own fog is tinted (ViewportEvent.ComputeFogColor). Registered
 * manually in OriginelModClient, never touched on a dedicated server.
 */
public final class RedMoonSkyRenderer {

    private static final ResourceLocation MOON_LOCATION = ResourceLocation.withDefaultNamespace("textures/environment/moon_phases.png");

    private RedMoonSkyRenderer() {
    }

    @SubscribeEvent
    public static void onRenderSky(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) {
            return;
        }
        LuneRougeConfig cfg = LuneRougeConfig.get();
        if (!RedMoonState.isActive() || !cfg.skyTintEnabled()) {
            return;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || level.effects().skyType() != DimensionSpecialEffects.SkyType.NORMAL) {
            return;
        }

        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.mulPose(event.getModelViewMatrix());
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(level.getTimeOfDay(partialTick) * 360.0F));
        Matrix4f matrix = poseStack.last().pose();

        int phase = level.getMoonPhase();
        int column = phase % 4;
        int row = phase / 4 % 2;
        float u0 = column / 4.0F;
        float v0 = row / 2.0F;
        float u1 = (column + 1) / 4.0F;
        float v1 = (row + 1) / 2.0F;

        // Slightly bigger than vanilla's own 20-unit moon quad so it fully covers it, edges included.
        float size = 21.5F;

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, MOON_LOCATION);
        RenderSystem.setShaderColor((float) cfg.moonRed(), (float) cfg.moonGreen(), (float) cfg.moonBlue(), 1.0F);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.addVertex(matrix, -size, -100.0F, size).setUv(u1, v1);
        buffer.addVertex(matrix, size, -100.0F, size).setUv(u0, v1);
        buffer.addVertex(matrix, size, -100.0F, -size).setUv(u0, v0);
        buffer.addVertex(matrix, -size, -100.0F, -size).setUv(u1, v0);
        BufferUploader.drawWithShader(buffer.buildOrThrow());

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        LuneRougeConfig cfg = LuneRougeConfig.get();
        if (!RedMoonState.isActive() || !cfg.skyTintEnabled()) {
            return;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || level.effects().skyType() != DimensionSpecialEffects.SkyType.NORMAL) {
            return;
        }

        float far = (float) cfg.fogDistanceBlocks();
        event.setNearPlaneDistance(far * 0.15F);
        event.setFarPlaneDistance(far);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        LuneRougeConfig cfg = LuneRougeConfig.get();
        if (!RedMoonState.isActive() || !cfg.skyTintEnabled()) {
            return;
        }
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null || level.effects().skyType() != DimensionSpecialEffects.SkyType.NORMAL) {
            return;
        }

        float strength = (float) Math.max(0.0, Math.min(1.0, cfg.fogStrength()));
        event.setRed(lerp(event.getRed(), (float) cfg.fogRed(), strength));
        event.setGreen(lerp(event.getGreen(), (float) cfg.fogGreen(), strength));
        event.setBlue(lerp(event.getBlue(), (float) cfg.fogBlue(), strength));
    }

    private static float lerp(float from, float to, float t) {
        return from + (to - from) * t;
    }
}
