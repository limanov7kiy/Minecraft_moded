package dev.mcmoded.undeadrising.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.mcmoded.undeadrising.UndeadRising;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraftforge.common.world.ModifiableStructureInfo;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

/// Structure modifier `mod_lavacow:add_spawns` — добавляет мобов в спавн-лист строения. Аналог
/// `StructureSpawnListGatherEvent` 1.16.5 (в Forge 1.20.1 готового типа нет). Если у строения не было своего списка
/// для категории, он создаётся с рамкой `piece` (мобы появляются только внутри частей строения, как в 1.16.5).
/// JSON: `data/mod_lavacow/forge/structure_modifier/*.json`, поля `structures` (id/тег/список) и `spawners`
/// (как у `forge:add_spawns`).
public record AddStructureSpawnsModifier(HolderSet<Structure> structures, List<MobSpawnSettings.SpawnerData> spawners) implements StructureModifier {
    public static final DeferredRegister<Codec<? extends StructureModifier>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, UndeadRising.MOD_ID);

    public static final RegistryObject<Codec<AddStructureSpawnsModifier>> CODEC = SERIALIZERS.register("add_spawns", () ->
            RecordCodecBuilder.create(b -> b.group(
                    RegistryCodecs.homogeneousList(Registries.STRUCTURE).fieldOf("structures").forGetter(AddStructureSpawnsModifier::structures),
                    Codec.either(MobSpawnSettings.SpawnerData.CODEC.listOf(), MobSpawnSettings.SpawnerData.CODEC)
                            .xmap(e -> e.map(l -> l, List::of), com.mojang.datafixers.util.Either::left)
                            .fieldOf("spawners").forGetter(AddStructureSpawnsModifier::spawners)
            ).apply(b, AddStructureSpawnsModifier::new)));

    @Override
    public void modify(Holder<Structure> structure, Phase phase, ModifiableStructureInfo.StructureInfo.Builder builder) {
        if (phase != Phase.ADD || !structures.contains(structure)) return;
        var settings = builder.getStructureSettings();
        for (MobSpawnSettings.SpawnerData data : spawners) {
            MobCategory category = data.type.getCategory();
            boolean existed = settings.getSpawnOverrides(category) != null;
            var overrides = settings.getOrAddSpawnOverrides(category);
            if (!existed) overrides.setBoundingBox(StructureSpawnOverride.BoundingBoxType.PIECE);
            overrides.addSpawn(data);
        }
    }

    @Override
    public Codec<? extends StructureModifier> codec() {
        return CODEC.get();
    }
}
