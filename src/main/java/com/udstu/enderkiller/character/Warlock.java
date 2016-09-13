package com.udstu.enderkiller.character;

import com.udstu.enderkiller.R;
import com.udstu.enderkiller.character.extend.GameCharacter;
import com.udstu.enderkiller.enumeration.Alignment;
import com.udstu.enderkiller.enumeration.GameCharacterStatus;
import com.udstu.enderkiller.enumeration.Occupation;
import com.udstu.enderkiller.enumeration.SkillStatus;
import org.bukkit.Material;
import org.bukkit.inventory.PlayerInventory;

/**
 * Created by czp on 16-8-12.
 * 巫师
 */
public class Warlock extends GameCharacter {
    private SkillStatus curseStatus = SkillStatus.cooldown;

    public Warlock(GameCharacter gameCharacter, Alignment alignment) {
        super(gameCharacter, alignment);
        occupation = Occupation.warlock;
    }

    @Override
    public void nextDay() {

    }

    @Override
    public void nextNight() {
        if (curseStatus == SkillStatus.cooldown) {
            player.sendMessage(R.getLang("skillCoolDownComplete").replace("{0}", R.getLang("curse")));
        }
        curseStatus = SkillStatus.available;
    }

    //技能 诅咒
    public void curse(GameCharacter targetGameCharacter) {
        PlayerInventory playerInventory = player.getInventory();

        //技能是否冷却完毕
        if (curseStatus != SkillStatus.available) {
            player.sendMessage(R.getLang("skillCooldown"));
            return;
        }

        //是否有恶魂之泪
        if (playerInventory.contains(Material.GHAST_TEAR)) {
            playerInventory.remove(Material.GHAST_TEAR);
        } else {
            player.sendMessage(R.getLang("noEnough").replace("{0}", Material.GHAST_TEAR.toString()));
            return;
        }

        //技能发动
        curseStatus = SkillStatus.cooldown;
        player.sendMessage(R.getLang("skillLaunch").replace("{0}", R.getLang("curse")));

        //TODO 这里假设在所有人场游戏中,牧师仅有一个.
        //有牧师可用时进入牧师的rescue方法
        for (GameCharacter gameCharacter : room.getGameCharacters()) {
            if (gameCharacter != targetGameCharacter && gameCharacter.getOccupation() == Occupation.priest && gameCharacter.getGameCharacterStatus() == GameCharacterStatus.alive) {
                //尝试发动此牧师的技能,可发动则退出
                if (((Priest) gameCharacter).rescue(targetGameCharacter)) {
                    return;
                }

            }
        }
        //若无可用牧师,延迟杀死目标
        thisPlugin.getServer().getScheduler().runTaskLater(thisPlugin, new Runnable() {
            @Override
            public void run() {
                targetGameCharacter.getPlayer().setHealth(0);
            }
        }, Integer.valueOf(R.getConfig("skillLaunchVoteTimeout")));
    }
}
