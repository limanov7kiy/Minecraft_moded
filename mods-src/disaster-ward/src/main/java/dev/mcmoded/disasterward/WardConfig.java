package dev.mcmoded.disasterward;

import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;

public final class WardConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue RADIUS;
    public static final ForgeConfigSpec.IntValue EDGE_MARGIN;
    public static final ForgeConfigSpec.BooleanValue PROTECT_BLOCKS;
    public static final ForgeConfigSpec.BooleanValue STEER_STORMS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> PROTECTED_NAMESPACES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DISABLED_ENTITIES;
    public static final ForgeConfigSpec.IntValue MAX_CONCURRENT;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ROOT_DISASTERS;
    public static final ForgeConfigSpec.BooleanValue NATURAL;
    public static final ForgeConfigSpec.IntValue NATURAL_MIN_TICKS;
    public static final ForgeConfigSpec.IntValue NATURAL_MAX_TICKS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> PARASITE_NAMESPACES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> INFECTION_EFFECTS;
    public static final ForgeConfigSpec.BooleanValue CURE_IN_ZONE;
    public static final ForgeConfigSpec.BooleanValue PUSH_PARASITES;
    public static final ForgeConfigSpec.BooleanValue IMMUNE_BOSSES;
    public static final ForgeConfigSpec.BooleanValue ORI_PROTECT;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ORI_ENTITIES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ORI_BLOCKS;
    public static final ForgeConfigSpec.BooleanValue ORI_ROLL;
    public static final ForgeConfigSpec.IntValue ORI_ROLL_TICKS;
    public static final ForgeConfigSpec.DoubleValue ORI_ROLL_CHANCE;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ORI_DIMENSIONS;
    public static final ForgeConfigSpec.BooleanValue ORI_FIX_STUCK;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("ward");
        RADIUS = b.comment("Радиус зоны оберега по X и Z (на всю высоту), блоков. Один для всего: бури Weather2, бедствия Natural Disasters!, паразиты Spore/SRP System.")
                .defineInRange("radius", 96, 8, 512);
        EDGE_MARGIN = b.comment("Запас у края зоны: бедствия Natural Disasters! и паразиты не появляются ближе radius + edgeMargin, паразитов в этой полосе отталкивает,",
                        "бури Weather2 разворачиваются, когда их центр подходит ближе radius + edgeMargin.")
                .defineInRange("edgeMargin", 16, 0, 128);
        PROTECT_BLOCKS = b.comment("Бедствия и паразиты снаружи не меняют блоки внутри зоны (торнадо Weather2, метеоры/землетрясения/цунами/пожар Natural Disasters!, заражение Spore).")
                .define("protectBlocks", true);
        STEER_STORMS = b.comment("Разворачивать бури Weather2 (стадия «формируется» и выше), подошедшие к зоне.")
                .define("steerStorms", true);
        PROTECTED_NAMESPACES = b.comment("Пространства имён существ-бедствий, которые не допускаются в зону (Natural Disasters!).")
                .defineList("protectedNamespaces", List.of("natural_disasters"), o -> o instanceof String);
        DISABLED_ENTITIES = b.comment("Существа, которые не появляются нигде (дубли Weather2: торнадо и песчаная буря Natural Disasters!). Пустой список — всё разрешено.")
                .defineList("disabledEntities", List.of("natural_disasters:tornado", "natural_disasters:sandstorm"), o -> o instanceof String);
        MAX_CONCURRENT = b.comment("Сколько бедствий Natural Disasters! может идти одновременно в измерении (замер: три сразу над городом — ~200 мс/тик, ~5 TPS). 0 — без ограничения.")
                .defineInRange("maxConcurrent", 1, 0, 100);
        ROOT_DISASTERS = b.comment("Какие существа считаются «самим бедствием» для maxConcurrent (метеоры и землетрясения-потомки в счёт не идут).")
                .defineList("rootDisasters", List.of("natural_disasters:spawnmeteorshower", "natural_disasters:spawnmeteor", "natural_disasters:spawnaearthquake",
                        "natural_disasters:acidrain", "natural_disasters:tsunami", "natural_disasters:sinkhole", "natural_disasters:wildfire"), o -> o instanceof String);
        b.pop();
        b.push("parasites");
        PARASITE_NAMESPACES = b.comment("Пространства имён паразитов (Fungal Infection: Spore): не появляются в зоне и у её края, в полосе у края их отталкивает, внутри — убираются.")
                .defineList("namespaces", List.of("spore"), o -> o instanceof String);
        INFECTION_EFFECTS = b.comment("Эффекты заражения: в зоне не накладываются и снимаются (мицелий Spore, заражение SRP System).")
                .defineList("infectionEffects", List.of("spore:mycelium_ef", "srpsystem:infection"), o -> o instanceof String);
        CURE_IN_ZONE = b.comment("Снимать эффекты заражения с существ и игроков в зоне.")
                .define("cureInZone", true);
        PUSH_PARASITES = b.comment("Отталкивать паразитов в полосе edgeMargin у края зоны (иначе убираются только зашедшие внутрь).")
                .define("pushParasites", true);
        IMMUNE_BOSSES = b.comment("Боссы (тег forge:bosses) и существа из тега disaster_ward:infection_immune не получают эффекты заражения нигде",
                        "(иначе SRP System превращает дракона или босса при 30 % здоровья в заражённого человека).")
                .define("immuneBosses", true);
        b.pop();
        b.push("naturalDisasters");
        NATURAL = b.comment("Случайные бедствия Natural Disasters! (в самом моде по умолчанию выкл, а во включённом виде бросок кубика раз в 5 секунд на игрока).")
                .define("enabled", true);
        NATURAL_MIN_TICKS = b.comment("Минимальная пауза между бросками на игрока, тиков (20 тиков = 1 с; 36000 = 30 мин).")
                .defineInRange("minTicks", 36000, 200, 24000000);
        NATURAL_MAX_TICKS = b.comment("Максимальная пауза между бросками на игрока, тиков.")
                .defineInRange("maxTicks", 96000, 200, 24000000);
        b.pop();
        b.push("oriStorm");
        ORI_PROTECT = b.comment("Ориджиниумная буря Bob's Originium Dream: то, что она призывает (черви, огненные шары, молнии, падающие блоки), не появляется в зоне",
                        "и у её края (radius + edgeMargin), а её блоки (шипы бури) не меняют блоки в зоне. Зашедших снаружи червей не трогаем.")
                .define("protect", true);
        ORI_ENTITIES = b.comment("Что считается призывом бури (только во время тика бури Bob's Originium Dream; яйца, команды и обычный спавн не затрагиваются).")
                .defineList("stormEntities", List.of("bobsoriginiumdream:evildustoriworm", "minecraft:fireball", "minecraft:lightning_bolt", "minecraft:falling_block"),
                        o -> o instanceof String);
        ORI_BLOCKS = b.comment("Блоки бури: пока они тикают, блоки в зоне не меняются (шип 50×53×57 из блока бури).")
                .defineList("stormBlocks", List.of("bobsoriginiumdream:oristormblock"), o -> o instanceof String);
        ORI_ROLL = b.comment("Запускать бурю случайно по времени: раз в rollTicks в измерении из dimensions, где есть игрок в выживании, бросок с шансом rollChance.",
                        "Сам мод запускает бурю по счётчику получений опыта (игровое правило oristormfrequency) — в сборке оно поднято до 100000,",
                        "чтобы фермы опыта не вызывали бурю. Выключить бури совсем: /gamerule oristorm false.")
                .define("roll", true);
        ORI_ROLL_TICKS = b.comment("Период броска, тиков (12000 = 10 минут).")
                .defineInRange("rollTicks", 12000, 200, 24000000);
        ORI_ROLL_CHANCE = b.comment("Шанс бури за бросок. Среднее время до бури = rollTicks / rollChance (12000 / 0.055 ≈ 218000 тиков ≈ 3 часа игры), плюс 10 минут предупреждения (oriwait).")
                .defineInRange("rollChance", 0.055, 0.0, 1.0);
        ORI_DIMENSIONS = b.comment("Измерения, где буря бросается по времени.")
                .defineList("dimensions", List.of("minecraft:overworld"), o -> o instanceof String);
        ORI_FIX_STUCK = b.comment("При старте сервера снимать «зависший» запуск бури: мод ждёт oriwait тиков в памяти, и если сервер выключили в это время,",
                        "флаг oristorm_begin остаётся в сохранении навсегда и буря в этом измерении больше не начинается.")
                .define("fixStuckStart", true);
        b.pop();
        SPEC = b.build();
    }

    private WardConfig() {}
}
