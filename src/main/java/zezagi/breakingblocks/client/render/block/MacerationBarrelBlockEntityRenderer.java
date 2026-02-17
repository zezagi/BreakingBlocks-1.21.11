package zezagi.breakingblocks.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
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
    public boolean rendersOutsideBoundingBox() {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256; // testowo duży zasięg, potem możesz zmniejszyć
    }

    @Override
    public void render(BarrelRenderState state, MatrixStack matrices, OrderedRenderCommandQueue queue, CameraRenderState cameraState) {
        System.out.println("[BreakingBlocks] MacerationBarrelBlockEntityRenderer.render() CALLED");
        matrices.push();

        // Nad blokiem (środek X/Z, wysoko Y)
        matrices.translate(0.5, 2.5, 0.5);

        // W TEŚCIE: nie obracamy do kamery, bo to potrafi wyjść "bokiem"
        // matrices.multiply(MinecraftClient.getInstance().gameRenderer.getCamera().getRotation());

        // Duży tekst (mniej skalowania = większy tekst w świecie)
        matrices.scale(-0.05F, -0.05F, 0.05F);

        int light = LightmapTextureManager.MAX_LIGHT_COORDINATE;

        OrderedText text = Text.literal("TEST_RENDERER").asOrderedText();

        queue.submitText(
                matrices,
                0f,
                0f,
                text,
                false,
                TextRenderer.TextLayerType.NORMAL,
                light,
                0xFFFFFFFF,
                0x00000000,
                0
        );

        matrices.pop();
    }
    public static class BarrelRenderState extends BlockEntityRenderState {
        public int gasolineLevel = 0;
        public int leavesCount = 0;
    }
}