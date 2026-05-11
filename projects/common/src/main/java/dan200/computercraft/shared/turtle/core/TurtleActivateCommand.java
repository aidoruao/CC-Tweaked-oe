// SPDX-License-Identifier: LicenseRef-CCPL

package dan200.computercraft.shared.turtle.core;

import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.shared.turtle.TurtleUtil;
import dan200.computercraft.shared.util.DirectionUtil;
import dan200.computercraft.shared.platform.PlatformHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Activates (right-clicks) the block or entity in front of the turtle.
 * Allows turtles to interact with crafting tables, furnaces, levers,
 * buttons, and any other block that responds to right-click.
 *
 * Governed by the TruthSystems Merkle Notary and Yeshua audit layer.
 */
public class TurtleActivateCommand implements TurtleCommand {
    private final InteractDirection direction;

    public TurtleActivateCommand(InteractDirection direction) {
        this.direction = direction;
    }

    @Override
    public TurtleCommandResult execute(ITurtleAccess turtle) {
        Direction dir = DirectionUtil.toDirection(direction, turtle.getDirection());
        TurtlePlayer turtlePlayer = TurtlePlayer.getWithPosition(turtle, turtle.getPosition().relative(dir), dir);

        Level world = turtle.getLevel();
        BlockPos targetPos = turtle.getPosition().relative(dir);

        // Check that there's a block to interact with
        if (world.isEmptyBlock(targetPos)) {
            return TurtleCommandResult.failure("No block to activate");
        }

        // Get the item the turtle is holding
        ItemStack heldStack = turtle.getInventory().getItem(turtle.getSelectedSlot());

        // Calculate hit position
        Vec3 hitVec = new Vec3(
            targetPos.getX() + 0.5f,
            targetPos.getY() + 0.5f,
            targetPos.getZ() + 0.5f
        );

        BlockHitResult hitResult = new BlockHitResult(
            hitVec,
            dir.getOpposite(),
            targetPos,
            false
        );

        // Try right-click with held item first
        InteractionResult result = PlatformHelper.get().useOn(
            turtlePlayer.player(),
            heldStack,
            hitResult
        );

        // If the block didn't respond, try without an item
        if (result == InteractionResult.PASS || result == InteractionResult.FAIL) {
            result = world.getBlockState(targetPos).useWithoutItem(
                world,
                turtlePlayer.player(),
                hitResult
            );
        }

        TurtleUtil.stopConsuming(turtle);

        if (result.consumesAction()) {
            return TurtleCommandResult.success();
        }

        return TurtleCommandResult.failure("Block did not respond to activation");
    }
}
