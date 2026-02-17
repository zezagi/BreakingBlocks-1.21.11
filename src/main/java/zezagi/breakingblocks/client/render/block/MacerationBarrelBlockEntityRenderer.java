package zezagi.breakingblocks.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import zezagi.breakingblocks.blockEntity.MacerationBarrelBlockEntity;

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
        state.gasolineLevel = blockEntity.getGasolineLevel();
        state.leavesCount = blockEntity.getLeavesLevel();
    }


    @Override
    public void render(BarrelRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        matrices.push();
        matrices.translate(0.5, 1.2, 0.5);

        matrices.multiply(MinecraftClient.getInstance().gameRenderer.getCamera().getRotation());

        matrices.scale(-0.025F, -0.025F, 0.025F);

        TextRenderer textRenderer = this.context.textRenderer();
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        String line1 = "Benzyna: " + state.gasolineLevel + "/100L";
        String line2 = "Liscie Koki: " + state.leavesCount;

        float x1 = (float) (-textRenderer.getWidth(line1) / 2);
        float x2 = (float) (-textRenderer.getWidth(line2) / 2);

        int light = 15728880;

        textRenderer.draw(line1, x1, 0f, 0xFFFFFF, false, matrix, (VertexConsumerProvider) queue, TextRenderer.TextLayerType.NORMAL, 0x40000000, light);
        textRenderer.draw(line2, x2, 10f, 0x00FF00, false, matrix, (VertexConsumerProvider) queue, TextRenderer.TextLayerType.NORMAL, 0x40000000, light);

        matrices.pop();
    }

    public static class BarrelRenderState extends BlockEntityRenderState {
        public int gasolineLevel = 0;
        public int leavesCount = 0;
    }
}