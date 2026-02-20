package zezagi.breakingblocks.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import zezagi.breakingblocks.blockentity.MacerationBarrelBlockEntity;

@Environment(EnvType.CLIENT)
public class MacerationBarrelBlockEntityRenderer implements BlockEntityRenderer<MacerationBarrelBlockEntity, MacerationBarrelBlockEntityRenderer.BarrelRenderState> {

    private final BlockEntityRendererFactory.Context context;

    public MacerationBarrelBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.context = context;
    }

    @Override
    public BarrelRenderState createRenderState() {
        return new BarrelRenderState();
    }

    @Override
    public void updateRenderState(MacerationBarrelBlockEntity blockEntity, BarrelRenderState state, float tickProgress, Vec3d cameraPos, ModelCommandRenderer.@Nullable CrumblingOverlayCommand crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);

        state.gasolineLevel = blockEntity.getGasolineLevel();
        state.leavesCount = blockEntity.getLeavesLevel();
        state.pos = blockEntity.getPos();
        state.isProductionEnabled = blockEntity.isProductionEnabled();
        state.isPasteReadyToCollect = blockEntity.isPasteReadyToCollect();
    }

    @Override
    public void render(BarrelRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        net.minecraft.util.hit.HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;
        if (!(hitResult instanceof net.minecraft.util.hit.BlockHitResult blockHit) || !blockHit.getBlockPos().equals(state.pos)) {
            return;
        }

        matrices.push();

        matrices.translate(0.5, 2, 0.5);

        matrices.multiply(MinecraftClient.getInstance().gameRenderer.getCamera().getRotation());
        matrices.multiply(net.minecraft.util.math.RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));

        matrices.scale(-0.025F, -0.025F, 0.025F);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        int light = net.minecraft.client.render.LightmapTextureManager.MAX_LIGHT_COORDINATE;

        int whiteArgb = 0xFFFFFFFF;
        int greenArgb = 0xFF00FF00;
        int orangeArgb = 0xFFFFAA00;
        int backgroundArgb = 0x40000000;

        if (state.isPasteReadyToCollect) {
            String readyLine = Text.translatable("block.breakingblocks.maceration_barrel.ready").getString();
            float px = -textRenderer.getWidth(readyLine) / 2.0f;
            OrderedText pt = Text.literal(readyLine).asOrderedText();
            queue.submitText(matrices, px, 5f, pt, false, TextRenderer.TextLayerType.SEE_THROUGH, light, 0xFF00FFFF, backgroundArgb, 0);
        }
        else if (state.isProductionEnabled) {
            String prodLine = Text.translatable("block.breakingblocks.maceration_barrel.producing").getString();
            float px = -textRenderer.getWidth(prodLine) / 2.0f;
            net.minecraft.text.OrderedText pt = net.minecraft.text.Text.literal(prodLine).asOrderedText();
            queue.submitText(matrices, px, 5f, pt, false, TextRenderer.TextLayerType.SEE_THROUGH, light, orangeArgb, backgroundArgb, 0);
        } else {

            String gasTransalatable = Text.translatable("block.breakingblocks.maceration_barrel.gasoline").getString();
            String leavesTranslatable = Text.translatable("block.breakingblocks.maceration_barrel.leaves").getString();

            String line1 = gasTransalatable + state.gasolineLevel + "/100L";
            String line2 = leavesTranslatable + state.leavesCount + "/64";

            float x1 = -textRenderer.getWidth(line1) / 2.0f;
            float x2 = -textRenderer.getWidth(line2) / 2.0f;

            net.minecraft.text.OrderedText t1 = net.minecraft.text.Text.literal(line1).asOrderedText();
            net.minecraft.text.OrderedText t2 = net.minecraft.text.Text.literal(line2).asOrderedText();

            queue.submitText(matrices, x1, 0f, t1, false, TextRenderer.TextLayerType.SEE_THROUGH, light, whiteArgb, backgroundArgb, 0);
            queue.submitText(matrices, x2, 10f, t2, false, TextRenderer.TextLayerType.SEE_THROUGH, light, greenArgb, backgroundArgb, 0);
        }

        matrices.pop();
    }

    public static class BarrelRenderState extends BlockEntityRenderState {
        public int gasolineLevel = 0;
        public int leavesCount = 0;
        boolean isProductionEnabled = false;
        public BlockPos pos;
        boolean isPasteReadyToCollect = false;
    }
}