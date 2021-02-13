package io.github.cvrunmin.enhancedmachine;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.cvrunmin.enhancedmachine.cap.CapabilityUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = EnhancedMachine.MODID)
public class BlockPopupDrawer {

    private static BlockPos prevLookingBlockPos = null;
    private static long animationStartMs = 0;
    private static boolean animationDone;

//    @SubscribeEvent
//    public static void whenBlockHighlightDrawing(DrawHighlightEvent event) {
//        RayTraceResult target = event.getTarget();
//        if (target.getType() == RayTraceResult.Type.BLOCK) {
//            EntityPlayer player = event.getPlayer();
//            BlockPos tagLocation;
//
//
//        }
//    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        if (Minecraft.getInstance().objectMouseOver == null
                || Minecraft.getInstance().objectMouseOver.getType() != RayTraceResult.Type.BLOCK
        || (Minecraft.getInstance().player != null && !Minecraft.getInstance().player.hasPermissionLevel(EMConfig.UPGRADES_ABSTRACT_VIEWING_PERMISSION.get()))) {
            prevLookingBlockPos = null;
        } else {
            BlockRayTraceResult target = ((BlockRayTraceResult) Minecraft.getInstance().objectMouseOver);
            PlayerEntity player = Minecraft.getInstance().player;
            BlockPos tagLocation;

            World world = player.getEntityWorld();
            TileEntity tileEntity = world.getTileEntity(target.getPos());
            if (tileEntity == null) {
                prevLookingBlockPos = null;
                return;
            }
            LazyOptional<IUpgradeSlot> capability = tileEntity.getCapability(CapabilityUpgradeSlot.UPGRADE_SLOT);
            if (!capability.isPresent()) {
                prevLookingBlockPos = null;
                return;
            }
            if (world.isAirBlock(target.getPos().up())) {
                tagLocation = target.getPos().up();
            } else {
                tagLocation = target.getPos().offset(target.getFace());
            }

            BlockState lookingBlock = world.getBlockState(target.getPos());
            if (lookingBlock == null) return;
            if (lookingBlock.getBlock() == null) return;
            long ms = System.currentTimeMillis();
            double animationProgress;
            if (!target.getPos().equals(prevLookingBlockPos)) {
                prevLookingBlockPos = target.getPos();
                animationStartMs = ms;
                animationDone = false;
                animationProgress = 0;
            } else {
                if (!animationDone) {
                    animationProgress = Math.min((ms - animationStartMs) / 1000.0 * 2, 1.0);
                    if (animationProgress == 1.0) {
                        animationDone = true;
                    }
                } else {
                    animationProgress = 1.0;
                }
            }
            float partialTicks = event.getPartialTicks();
            double playerX = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * (double) partialTicks;
            double playerY = player.lastTickPosY + (player.getPosY() - player.lastTickPosY) * (double) partialTicks + player.getEyeHeight();
            double playerZ = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * (double) partialTicks;
            Vec3d d = new Vec3d(tagLocation).subtract(playerX, playerY, playerZ);
            List<String> details = new ArrayList<>();
            for (UpgradeDetail detail : capability.orElseThrow(NullPointerException::new).getUpgrades().pickleUpgrades()) {
                if (!detail.getType().equals(Upgrades.EMPTY)) {
                    details.add(Upgrades.getUpgradeFullTitle(detail).getFormattedText());
                }
            }
            if (details.isEmpty()) return;
            details.add(0, I18n.format(lookingBlock.getBlock().getTranslationKey()));
            FontRenderer fontRenderer = Minecraft.getInstance().getRenderManager().getFontRenderer();
            if (fontRenderer == null) return;
            int rectWidth = 0;
            for (String detail : details) {
                rectWidth = Math.max(rectWidth, fontRenderer.getStringWidth(detail));
            }
            int rectHalfWidth = rectWidth / 2;
            float stringHeight = fontRenderer.FONT_HEIGHT * 1.1f;
            float stringsHeight = stringHeight * 1.1f * details.size();
            int prevMatrixMode = GlStateManager.getInteger(GL11.GL_MATRIX_MODE);
//            RenderSystem.matrixMode(GL11.GL_MODELVIEW);
//            RenderSystem.pushMatrix();
//            RenderSystem.translated(0.5, 0.5, 0.50005);
//            RenderSystem.translated(d.x, d.y, d.z);
//            RenderSystem.normal3f(0.0F, 1.0F, 0.0F);
//            RenderSystem.scalef(1 / 16f, 1 / 16f, 1 / 16f);
//            RenderSystem.scaled(0.4, 0.4, 0.4);
//            RenderSystem.pushMatrix();
//            RenderSystem.rotatef(-player.rotationYaw+180, 0, 1, 0);
//            RenderSystem.rotatef(player.rotationPitch, 1, 0, 0);
            MatrixStack matrixStack = event.getMatrixStack();
            matrixStack.push();
            matrixStack.translate(0.5,0.5,0.50005);
            matrixStack.translate(d.x,d.y,d.z);
            matrixStack.scale(1 / 16f,1 / 16f,1 / 16f);
            matrixStack.scale(0.4f, 0.4f, 0.4f);
            matrixStack.push();
            matrixStack.rotate(new Quaternion(0,-player.rotationYaw + 180, 0, true));
            matrixStack.rotate(new Quaternion(-player.rotationPitch, 0, 0, true));
//            matrixStack.rotate(this.renderManager.getCameraOrientation());
            RenderSystem.disableLighting();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.disableTexture();
            Matrix4f last = matrixStack.getLast().getMatrix();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
            bufferBuilder.pos(last, (float) (-rectHalfWidth * animationProgress), -stringsHeight / 2f, -0.0005f).color(.0f, .0f, .0f, .3333f).endVertex();
            bufferBuilder.pos(last, (float) (rectHalfWidth * animationProgress), -stringsHeight / 2f, -0.0005f).color(.0f, .0f, .0f, .3333f).endVertex();
            bufferBuilder.pos(last, (float) (-rectHalfWidth * animationProgress), stringsHeight / 2f, -0.0005f).color(.0f, .0f, .0f, .3333f).endVertex();

            bufferBuilder.pos(last, (float) (-rectHalfWidth * animationProgress), stringsHeight / 2f, -0.0005f).color(.0f, .0f, .0f, .3333f).endVertex();
            bufferBuilder.pos(last, (float) (rectHalfWidth * animationProgress), -stringsHeight / 2f, -0.0005f).color(.0f, .0f, .0f, .3333f).endVertex();
            bufferBuilder.pos(last, (float) (rectHalfWidth * animationProgress), stringsHeight / 2f, -0.0005f).color(.0f, .0f, .0f, .3333f).endVertex();
            tessellator.draw();
//            RenderSystem.popMatrix();
//            RenderSystem.pushMatrix();
//            RenderSystem.scalef(1, -1, 1);
//            RenderSystem.rotatef(-player.rotationYaw + 180, 0, 1, 0);
//            RenderSystem.rotatef(-player.rotationPitch/* - 90*/, 1, 0, 0);
            matrixStack.pop();
            matrixStack.push();
            matrixStack.scale(1,-1,1);
            matrixStack.rotate(new Quaternion(0,-player.rotationYaw + 180, 0, true));
            matrixStack.rotate(new Quaternion(player.rotationPitch, 0, 0, true));
            last = matrixStack.getLast().getMatrix();
            RenderSystem.enableDepthTest();
            RenderSystem.enableTexture();
            RenderSystem.enableAlphaTest();
            RenderSystem.depthMask(true);
            int alpha = (int) (0xFF * (float) animationProgress) << 24;
            if (alpha != 0) {
                int color = 0xFFFFFF | alpha;
                for (int i = 0; i < details.size(); i++) {
                    String text = details.get(i);
//                    fontRenderer.drawString(text, -fontRenderer.getStringWidth(text) / 2, (int) (-stringsHeight / 2 + stringHeight * i), color);
                    IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
                    fontRenderer.renderString(text, -fontRenderer.getStringWidth(text) / 2, (int) (-stringsHeight / 2 + stringHeight * i), color, false, last, irendertypebuffer$impl, false, 0, 0xf000f0);
                    irendertypebuffer$impl.finish();
                }
            }
            matrixStack.pop();
            matrixStack.pop();
//            RenderSystem.popMatrix();
//            RenderSystem.popMatrix();
            RenderSystem.matrixMode(prevMatrixMode);
            RenderSystem.disableBlend();
        }
    }
}
