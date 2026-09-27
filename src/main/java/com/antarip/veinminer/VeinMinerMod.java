/*
 * Credits - Antarip
 */
package com.antarip.veinminer;

import com.antarip.veinminer.config.VeinMinerConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class VeinMinerMod implements ModInitializer {
    public static final String MOD_ID = "veinminer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final TagKey<Block> C_ORES = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "ores"));
    private static final TagKey<Block> FORGE_ORES = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("forge", "ores"));

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Vein Miner Mod (Credits - Antarip)");
        VeinMinerConfig.load();

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            if (world.isClientSide()) return true;
            if (!VeinMinerConfig.get().enabled) return true;

            // Trigger: Sneaking + Correct tool
            if (!player.isCrouching() && !player.isShiftKeyDown()) return true;
            ItemStack heldItem = player.getMainHandItem();
            if (state.requiresCorrectToolForDrops() && !heldItem.isCorrectToolForDrops(state)) return true;

            // Target: STRICTLY ores only
            if (!isOre(state)) return true;

            // Run Vein Miner logic
            runVeinMiner((ServerLevel) world, player, pos, state);
            return false; // Cancel standard block break to let mod handle it
        });
    }

    private boolean isOre(BlockState state) {
        if (state.is(C_ORES) || state.is(FORGE_ORES)) return true;

        String path = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath().toLowerCase(Locale.ROOT);
        return path.contains("ore");
    }

    private void runVeinMiner(ServerLevel world, Player player, BlockPos startPos, BlockState startState) {
        VeinMinerConfig config = VeinMinerConfig.get();
        Queue<BlockPos> queue = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();
        List<BlockPos> toBreak = new ArrayList<>();

        queue.add(startPos);
        visited.add(startPos);

        // BFS for connected ores (no recursion to prevent StackOverflow errors)
        while (!queue.isEmpty() && toBreak.size() < config.maxOres) {
            BlockPos current = queue.poll();
            toBreak.add(current);

            // Scan all 26 adjacent directions (3x3x3 box) for 3D ore connectivity
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) continue;
                        BlockPos neighbor = current.offset(dx, dy, dz);

                        if (!visited.contains(neighbor) && visited.size() < config.maxOres) {
                            BlockState neighborState = world.getBlockState(neighbor);
                            // Only mine identical ore blocks
                            if (neighborState.getBlock() == startState.getBlock()) {
                                visited.add(neighbor);
                                queue.add(neighbor);
                            }
                        }
                    }
                }
            }
        }

        // Gather all drops to bundle them and reduce tick/entity lag
        List<ItemStack> bundledDrops = new ArrayList<>();
        ItemStack tool = player.getMainHandItem();
        boolean applyDurability = config.applyToolDamage && !player.isCreative() && tool.isDamageableItem();

        for (BlockPos pos : toBreak) {
            BlockState state = world.getBlockState(pos);
            // Collect drops
            bundledDrops.addAll(Block.getDrops(state, world, pos, null, player, tool));
            // Erase block
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);

            // Apply durability damage
            if (applyDurability) {
                tool.hurtAndBreak(1, player, InteractionHand.MAIN_HAND);
                if (tool.isEmpty()) {
                    break; // Stop vein mining if tool breaks
                }
            }
        }

        // Spawn bundled ore drops at the start position
        Map<Item, Integer> mergedCounts = new HashMap<>();
        for (ItemStack stack : bundledDrops) {
            if (stack.isEmpty()) continue;
            mergedCounts.put(stack.getItem(), mergedCounts.getOrDefault(stack.getItem(), 0) + stack.getCount());
        }

        for (Map.Entry<Item, Integer> entry : mergedCounts.entrySet()) {
            Item item = entry.getKey();
            int totalCount = entry.getValue();
            while (totalCount > 0) {
                int countToSpawn = Math.min(totalCount, item.getDefaultMaxStackSize());
                ItemStack bundledStack = new ItemStack(item, countToSpawn);
                ItemEntity entity = new ItemEntity(world, startPos.getX() + 0.5, startPos.getY() + 0.5, startPos.getZ() + 0.5, bundledStack);
                world.addFreshEntity(entity);
                totalCount -= countToSpawn;
            }
        }
    }
}