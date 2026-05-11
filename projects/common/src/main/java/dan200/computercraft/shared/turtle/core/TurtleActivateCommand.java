// SPDX-License-Identifier: LicenseRef-CCPL

package dan200.computercraft.shared.turtle.core;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleCommand;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.shared.turtle.TurtleUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Activates (right-clicks) the block or entity in front of the turtle.
 */
public class TurtleActivateCommand implements TurtleCommand {
    private final InteractDirection direction;

    public TurtleActivateCommand(InteractDirection direction) {
        this.direction = direction;
    }

    @Override
    public TurtleCommandResult execute(ITurtleAccess turtle) {
        Direction dir = direction.toWorldDir(turtle);
        
        TurtlePlayer turtlePlayer = TurtlePlayer.getWithPosition(
            turtle, turtle.getPosition().relative(dir), dir
        );

        Level world = turtle.getLevel();
        BlockPos targetPos = turtle.getPosition().relative(dir);

        if (world.isEmptyBlock(targetPos)) {
            return TurtleCommandResult.failure("No block to activate");
        }

        Vec3 hitVec = new Vec3(
            targetPos.getX() + 0.5,
            targetPos.getY() + 0.5,
            targetPos.getZ() + 0.5
        );

        BlockHitResult hitResult = new BlockHitResult(
            hitVec,
            dir.getOpposite(),
            targetPos,
            false
        );

        // Use the block's use method (standard right-click)
        InteractionResult result = world.getBlockState(targetPos)
            .use(world, turtlePlayer.player(), InteractionHand.MAIN_HAND, hitResult);

        TurtleUtil.stopConsuming(turtle);

        if (result.consumesAction()) {
            return TurtleCommandResult.success();
        }

        return TurtleCommandResult.failure("Block did not respond to activation");
    }
}
