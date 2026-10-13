package dev.mcmoded.disasterward;

import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;

public final class WardConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue RADIUS;
    public static final ForgeConfigSpec.IntValue EDGE_MARGIN;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> PROTECTED_NAMESPACES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> DISABLED_ENTITIES;
    public static final ForgeConfigSpec.IntValue MAX_CONCURRENT;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ROOT_DISASTERS;
    public static final ForgeConfigSpec.BooleanValue NATURAL;
    public static final ForgeConfigSpec.IntValue NATURAL_MIN_TICKS;
    public static final ForgeConfigSpec.IntValue NATURAL_MAX_TICKS;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("ward");
        RADIUS = b.comment("Радиус зоны оберега по X и Z (на всю высоту), блоков. У Weather2 своя зона отражателя — 128 блоков.")
                .defineInRange("radius", 64, 8, 512);
        EDGE_MARGIN = b.comment("Бедствия Natural Disasters! убираются уже на расстоянии radius + edgeMargin от оберега, чтобы их область действия не заходила в зону.")
                .defineInRange("edgeMargin", 16, 0, 128);
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
        b.push("naturalDisasters");
        NATURAL = b.comment("Случайные бедствия Natural Disasters! (в самом моде по умолчанию выкл, а во включённом виде бросок кубика раз в 5 секунд на игрока).")
                .define("enabled", true);
        NATURAL_MIN_TICKS = b.comment("Минимальная пауза между бросками на игрока, тиков (20 тиков = 1 с; 36000 = 30 мин).")
                .defineInRange("minTicks", 36000, 200, 24000000);
        NATURAL_MAX_TICKS = b.comment("Максимальная пауза между бросками на игрока, тиков.")
                .defineInRange("maxTicks", 96000, 200, 24000000);
        b.pop();
        SPEC = b.build();
    }

    private WardConfig() {}
}
