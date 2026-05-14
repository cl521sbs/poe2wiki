package com.poe2wiki.service;

import com.poe2wiki.dto.DpsRequest;
import com.poe2wiki.dto.DpsResponse;
import com.poe2wiki.dto.DpsResponse.DamageBreakdown;
import com.poe2wiki.dto.DpsResponse.DamageBreakdown.*;
import com.poe2wiki.entity.Skill;
import com.poe2wiki.mapper.SkillMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CalculatorService {

    private final SkillMapper skillMapper;

    public DpsResponse calculate(DpsRequest req) {
        DpsRequest.SkillConfig skillConfig = req.getSkills().get(0);  // MVP: 单技能
        Skill skill = skillMapper.selectById(skillConfig.getSkillId());

        // 1. 基础伤害
        DpsRequest.WeaponConfig wp = req.getEquipment().getWeapon();
        double physBase = (nvl(wp.getPhysMin()) + nvl(wp.getPhysMax())) / 2.0;
        double fireBase = (nvl(wp.getFireMin()) + nvl(wp.getFireMax())) / 2.0;
        double coldBase = (nvl(wp.getColdMin()) + nvl(wp.getColdMax())) / 2.0;
        double lightBase = (nvl(wp.getLightningMin()) + nvl(wp.getLightningMax())) / 2.0;
        double chaosBase = (nvl(wp.getChaosMin()) + nvl(wp.getChaosMax())) / 2.0;

        DamageValue baseDamage = new DamageValue();
        baseDamage.setPhysical(physBase);
        baseDamage.setFire(fireBase);
        baseDamage.setCold(coldBase);
        baseDamage.setLightning(lightBase);
        baseDamage.setChaos(chaosBase);
        baseDamage.setTotal(physBase + fireBase + coldBase + lightBase + chaosBase);

        // 2. 伤害倍率
        double skillMultiplier = skill != null ? skill.getDamageMultiplier().doubleValue() / 100.0 : 1.0;
        double baseAfterSkill = baseDamage.getTotal() * skillMultiplier;

        // 3. 增伤 (increased)
        double incMultiplier = 1.0 + nvl(req.getPassives().getDamageInc()) / 100.0;
        List<ModSource> incSources = new ArrayList<>();
        incSources.add(buildSource("天赋", nvl(req.getPassives().getDamageInc())));

        ModSources increasedMods = new ModSources();
        increasedMods.setTotal(incMultiplier);
        increasedMods.setSources(incSources);

        // 4. 更多伤害 (more) — MVP: 仅 Skill multiplier
        double moreMultiplier = skillMultiplier;
        List<ModSource> moreSources = new ArrayList<>();
        moreSources.add(buildSource("技能倍率", skillMultiplier * 100));

        ModSources moreMods = new ModSources();
        moreMods.setTotal(moreMultiplier);
        moreMods.setSources(moreSources);

        // 5. 总伤害
        double totalDamage = baseDamage.getTotal() * incMultiplier * moreMultiplier;

        // 6. 攻速
        double baseSpeed = nvl(wp.getAttackSpeed());
        double speedMultiplier = 1.0 + nvl(req.getPassives().getAttackSpeedInc()) / 100.0;
        double effectiveSpeed = baseSpeed * speedMultiplier;

        SpeedInfo speedInfo = new SpeedInfo();
        speedInfo.setBase(baseSpeed);
        speedInfo.setEffective(effectiveSpeed);

        // 7. 暴击
        double baseCrit = nvl(wp.getCritChance());
        double critChance = Math.min(baseCrit * (1.0 + nvl(req.getPassives().getCritChanceInc()) / 100.0), 100.0);
        double critMulti = (nvl(wp.getCritMultiplier()) + nvl(req.getPassives().getCritMultiInc())) / 100.0;

        CritInfo critInfo = new CritInfo();
        critInfo.setChance(critChance);
        critInfo.setMultiplier(critMulti);

        // 8. 命中率
        double hitChance = 95.0;  // MVP: 固定值

        // 9. 抗性计算
        double penetration = nvl(req.getPassives().getPenetration());
        double enemyLightRes = nvl(req.getTarget().getLightningRes()) - penetration;
        double resistMultiplier = 1.0 - Math.max(enemyLightRes, 0) / 100.0;

        ResistInfo resistInfo = new ResistInfo();
        resistInfo.setLightning(enemyLightRes);
        resistInfo.setFire(nvl(req.getTarget().getFireRes()));
        resistInfo.setCold(nvl(req.getTarget().getColdRes()));
        resistInfo.setChaos(nvl(req.getTarget().getChaosRes()));

        // 10. 汇总
        double avgDamage = totalDamage;
        double dps = avgDamage * effectiveSpeed;
        double critDps = avgDamage * effectiveSpeed
                * (critChance / 100.0 * critMulti + (1.0 - critChance / 100.0));
        double effectiveDps = critDps * (hitChance / 100.0) * resistMultiplier;

        DpsResponse result = new DpsResponse();
        result.setAvgDamage(Math.round(avgDamage * 100.0) / 100.0);
        result.setDps(Math.round(dps * 100.0) / 100.0);
        result.setCritDps(Math.round(critDps * 100.0) / 100.0);
        result.setEffectiveDps(Math.round(effectiveDps * 100.0) / 100.0);

        DamageBreakdown breakdown = new DamageBreakdown();
        breakdown.setBaseDamage(baseDamage);
        breakdown.setIncreasedMods(increasedMods);
        breakdown.setMoreMods(moreMods);
        breakdown.setAttackSpeed(speedInfo);
        breakdown.setCrit(critInfo);
        breakdown.setHitChance(hitChance);
        breakdown.setEnemyResistances(resistInfo);
        result.setBreakdown(breakdown);

        return result;
    }

    public Map<String, DpsResponse> compare(DpsRequest buildA, DpsRequest buildB) {
        Map<String, DpsResponse> result = new LinkedHashMap<>();
        result.put("buildA", calculate(buildA));
        result.put("buildB", calculate(buildB));
        return result;
    }

    private double nvl(Integer v) {
        return v == null ? 0.0 : v.doubleValue();
    }

    private double nvl(Double v) {
        return v == null ? 0.0 : v;
    }

    private ModSource buildSource(String name, Double value) {
        ModSource s = new ModSource();
        s.setName(name);
        s.setValue(value);
        return s;
    }
}
