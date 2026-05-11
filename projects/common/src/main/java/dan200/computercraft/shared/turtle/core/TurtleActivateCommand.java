// SPDX-License-Identifier: LicenseRef-CCPL

package dan200.computercraft.shared.turtle.core;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleCommand;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.shared.turtle.TurtleUtil;
import dan200.computercraft.shared.util.DirectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Activates (right-clicks) the block or entity in front of the turtle.
 * Allows turtles to interact with crafting tables, furnaces, levers,
 * buttons, and any other block that responds to right-click.
 */
public class TurtleActivateCommand implements TurtleCommand {
    private final InteractDirection direction;

    public TurtleActivateCommand(InteractDirection direction) {
        this.direction = direction;
    }

    @Override
    public TurtleCommandResult execute(ITurtleAccess turtle) {
        Direction dir = DirectionUtil.toDirection(direction, turtle.getDirection());

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

        // Try interacting with the block
        InteractionResult result = world.getBlockState(targetPos)
            .useWithoutItem(world, turtlePlayer.player(), hitResult);

        TurtleUtil.stopConsuming(turtle);

        if (result.consumesAction()) {
            return TurtleCommandResult.success();
        }

        return TurtleCommandResult.failure("Block did not respond to activation");
    }
}
