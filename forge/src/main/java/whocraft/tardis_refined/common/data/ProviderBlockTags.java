package whocraft.tardis_refined.common.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.*;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import whocraft.tardis_refined.TardisRefined;
import whocraft.tardis_refined.common.crafting.astral_manipulator.ManipulatorCraftingIngredient;
import whocraft.tardis_refined.common.crafting.astral_manipulator.ManipulatorRecipes;
import whocraft.tardis_refined.registry.TRBlockRegistry;
import whocraft.tardis_refined.registry.TRTagKeys;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ProviderBlockTags extends BlockTagsProvider {

    public ProviderBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TardisRefined.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        for (Map.Entry<ResourceKey<Block>, Block> blocksEntry : TRBlockRegistry.BLOCKS.entrySet().stream().toList()) {
            Block block = blocksEntry.getValue();

            if (TRBlockRegistry.BLOCKS.getKey(block).getNamespace().equals(TardisRefined.MODID)) {
                /*Fences*/
                if (block instanceof FenceBlock fenceBlock) {
                    tag(BlockTags.FENCES).add(fenceBlock);
                }

                /*Leaves*/
                if (block instanceof LeavesBlock leavesBlock) {
                    tag(BlockTags.LEAVES).add(leavesBlock);
                }

                /*Slabs*/
                if (block instanceof SlabBlock slabBlock) {
                    tag(BlockTags.SLABS).add(slabBlock);
                }
            }
        }

        tag(Tags.Blocks.ORES).add(TRBlockRegistry.ZEITON_ORE.get()).add(TRBlockRegistry.ZEITON_ORE_DEEPSLATE.get()).add(TRBlockRegistry.ZEITON_BLOCK.get());


        tag(BlockTags.DRAGON_IMMUNE).add(TRBlockRegistry.ROOT_SHELL_BLOCK.get()).add(TRBlockRegistry.GLOBAL_SHELL_BLOCK.get()).add(TRBlockRegistry.GLOBAL_CONSOLE_BLOCK.get());
        tag(BlockTags.WITHER_IMMUNE).add(TRBlockRegistry.ROOT_SHELL_BLOCK.get()).add(TRBlockRegistry.GLOBAL_SHELL_BLOCK.get()).add(TRBlockRegistry.GLOBAL_CONSOLE_BLOCK.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(TRBlockRegistry.CONSOLE_CONFIGURATION_BLOCK.get())
                .add(TRBlockRegistry.LANDING_PAD.get())
                .add(TRBlockRegistry.FLIGHT_DETECTOR.get())
                .add(TRBlockRegistry.TERRAFORMER_BLOCK.get())
                .add(TRBlockRegistry.ZEITON_FUSED_IRON_BLOCK.get())
                .add(TRBlockRegistry.ZEITON_FUSED_COPPER_BLOCK.get())
                .add(TRBlockRegistry.ZEITON_ORE_DEEPSLATE.get())
                .add(TRBlockRegistry.ZEITON_ORE.get())
                .add(TRBlockRegistry.ASTRAL_MANIPULATOR_BLOCK.get())
                .add(TRBlockRegistry.ZEITON_BLOCK.get())
                .add(TRBlockRegistry.GRAVITY_WELL.get())
                .add(TRBlockRegistry.ROOT_PLANT_BLOCK.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(TRBlockRegistry.CONSOLE_CONFIGURATION_BLOCK.get())
                .add(TRBlockRegistry.LANDING_PAD.get())
                .add(TRBlockRegistry.GRAVITY_WELL.get())
                .add(TRBlockRegistry.FLIGHT_DETECTOR.get())
                .add(TRBlockRegistry.TERRAFORMER_BLOCK.get())
                .add(TRBlockRegistry.ZEITON_FUSED_IRON_BLOCK.get())
                .add(TRBlockRegistry.ZEITON_FUSED_COPPER_BLOCK.get())
                .add(TRBlockRegistry.ZEITON_ORE_DEEPSLATE.get())
                .add(TRBlockRegistry.ZEITON_ORE.get())
                .add(TRBlockRegistry.ASTRAL_MANIPULATOR_BLOCK.get())
                .add(TRBlockRegistry.ZEITON_BLOCK.get())
                .add(TRBlockRegistry.ASTRAL_MANIPULATOR_BLOCK.get());

        tag(TRTagKeys.TERRAFORMER_CENTER)
                .addOptionalTag(Tags.Blocks.STORAGE_BLOCKS_REDSTONE)
                .addOptionalTag(BlockTags.create(new ResourceLocation("c", "storage_blocks/redstone")))
                .add(Blocks.REDSTONE_BLOCK.builtInRegistryHolder().key());

        tag(TRTagKeys.TERRAFORMER_ENCASED_CORRIDORS)
                .addOptionalTag(Tags.Blocks.STORAGE_BLOCKS_COPPER)
                .addOptionalTag(BlockTags.create(new ResourceLocation("c", "storage_blocks/copper")))
                .add(
                        Blocks.COPPER_BLOCK.builtInRegistryHolder().key(),
                        Blocks.EXPOSED_COPPER.builtInRegistryHolder().key(),
                        Blocks.WEATHERED_COPPER.builtInRegistryHolder().key(),
                        Blocks.OXIDIZED_COPPER.builtInRegistryHolder().key(),
                        Blocks.WAXED_COPPER_BLOCK.builtInRegistryHolder().key(),
                        Blocks.WAXED_EXPOSED_COPPER.builtInRegistryHolder().key(),
                        Blocks.WAXED_WEATHERED_COPPER.builtInRegistryHolder().key(),
                        Blocks.WAXED_OXIDIZED_COPPER.builtInRegistryHolder().key()
                );

        tag(TRTagKeys.TERRAFORMER_DELETE)
                .add(Blocks.TNT);

        tag(TRTagKeys.TERRAFORMER_DELETE_CENTER)
                .addTag(TRTagKeys.TERRAFORMER_DELETE);


        // ===== DIAGONAL WALLS =====
        // This is cursed, but we gotta do what we gotta do

        // Blocks
        Set<Block> normalBlocks = new HashSet<>();
        ManipulatorRecipes.MANIPULATOR_CRAFTING_RECIPES.forEach((resourceLocation, manipulatorCraftingRecipe) -> {
            for (ManipulatorCraftingIngredient ingredient : manipulatorCraftingRecipe.ingredients()) {
                normalBlocks.add(ingredient.inputBlockState().getBlock());
            }
        });
        tag(TRTagKeys.DIAGONAL_COMPAT_WALLS).add(normalBlocks.toArray(new Block[0]));

        Set<Block> glassBlocks = new HashSet<>();
        ManipulatorRecipes.MANIPULATOR_CRAFTING_RECIPES.forEach((resourceLocation, manipulatorCraftingRecipe) -> {
            for (ManipulatorCraftingIngredient ingredient : manipulatorCraftingRecipe.ingredients()) {
                if (ingredient.inputBlockState().getBlock() instanceof GlassBlock || ingredient.inputBlockState().getBlock() instanceof AbstractGlassBlock || ingredient.inputBlockState().getBlock() instanceof StainedGlassBlock) {
                    glassBlocks.add(ingredient.inputBlockState().getBlock());
                }
            }
        });
        tag(TRTagKeys.DIAGONAL_COMPAT_GLASS).add(glassBlocks.toArray(new Block[0]));

        Set<Block> fenceBlocks = new HashSet<>();
        ManipulatorRecipes.MANIPULATOR_CRAFTING_RECIPES.forEach((resourceLocation, manipulatorCraftingRecipe) -> {
            for (ManipulatorCraftingIngredient ingredient : manipulatorCraftingRecipe.ingredients()) {
                if(ingredient.inputBlockState().getBlock() instanceof FenceBlock) {
                    fenceBlocks.add(ingredient.inputBlockState().getBlock());
                }
            }
        });
        tag(TRTagKeys.DIAGONAL_COMPAT_FENCES).add(fenceBlocks.toArray(new Block[0]));

    }
}
