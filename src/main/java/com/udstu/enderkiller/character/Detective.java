package com.udstu.enderkiller.character;

import com.udstu.enderkiller.R;
import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.enumeration.Alignment;
import com.udstu.enderkiller.enumeration.Occupation;
import com.udstu.enderkiller.enumeration.SkillStatus;

/**
 * Created by czp on 16-8-12.
 * 侦探
 */
public class Detective extends GameCharacter {
    private SkillStatus researchStatus = SkillStatus.cooldown;
    private GameCharacter targetGameCharacter = null;

    public Detective(GameCharacter gameCharacter, Alignment alignment) {
        super(gameCharacter, alignment);
        occupation = Occupation.detective;
    }

    @Override
    public void nextDay() {
        //每次黎明时刷新技能
        if (researchStatus == SkillStatus.cooldown) {
            player.sendMessage(R.getLang("skillCoolDownComplete").replace("{0}", R.getLang("research")));
        }
        researchStatus = SkillStatus.available;

        //获得上次调查的对象的身份
        if (targetGameCharacter != null) {
            player.sendMessage(R.getLang("alignmentOfThePlayer").replace("{0}", targetGameCharacter.getPlayer().getName()) + ": " + targetGameCharacter.getAlignment().toString());
            targetGameCharacter = null;
        }
    }

    @Override
    public void nextNight() {

    }

    public void research(GameCharacter targetGameCharacter) {
        //技能是否冷却完毕
        if (researchStatus != SkillStatus.available) {
            player.sendMessage(R.getLang("skillCooldown"));
            return;
        }

        //技能发动
        researchStatus = SkillStatus.cooldown;
        this.targetGameCharacter = targetGameCharacter;
        player.sendMessage(R.getLang("skillLaunch").replace("{0}", R.getLang("research")));
    }
}
