package com.poe2wiki.dto;

import lombok.Data;
import java.util.List;

@Data
public class DpsResponse {
    private Double avgDamage;
    private Double dps;
    private Double critDps;
    private Double effectiveDps;
    private DamageBreakdown breakdown;

    @Data
    public static class DamageBreakdown {
        private DamageValue baseDamage;
        private ModSources increasedMods;
        private ModSources moreMods;
        private SpeedInfo attackSpeed;
        private CritInfo crit;
        private Double hitChance;
        private ResistInfo enemyResistances;

        @Data
        public static class DamageValue {
            private Double physical, fire, cold, lightning, chaos, total;
        }

        @Data
        public static class ModSources {
            private Double total;
            private List<ModSource> sources;
        }

        @Data
        public static class ModSource {
            private String name;
            private Double value;
        }

        @Data
        public static class SpeedInfo {
            private Double base, effective;
        }

        @Data
        public static class CritInfo {
            private Double chance, multiplier;
        }

        @Data
        public static class ResistInfo {
            private Double fire, cold, lightning, chaos;
        }
    }
}
