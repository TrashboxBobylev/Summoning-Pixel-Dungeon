/*
 * Pixel Dungeon
 *   * Copyright (C) 2012-2015 Oleg Dolya
 *   *
 *   * Shattered Pixel Dungeon
 *   * Copyright (C) 2014-2019 Evan Debenham
 *   *
 *   * Summoning Pixel Dungeon
 *   * Copyright (C) 2019-2020 TrashboxBobylev
 *   *
 *   * This program is free software: you can redistribute it and/or modify
 *   * it under the terms of the GNU General Public License as published by
 *   * the Free Software Foundation, either version 3 of the License, or
 *   * (at your option) any later version.
 *   *
 *   * This program is distributed in the hope that it will be useful,
 *   * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   * GNU General Public License for more details.
 *   *
 *   * You should have received a copy of the GNU General Public License
 *   * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.magic.soulreaver;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ConstantShielding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ManaStealing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.HolyAuraBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.AdHocSpell;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class Support extends AdHocSpell {

    {
        image = ItemSpriteSheet.SR_SUPPORT;
    }

    @Override
    public void effect(Hero hero) {
        Sample.INSTANCE.play(Assets.Sounds.READ);
        HolyAuraBuff aura = Buff.affect(hero, HolyAuraBuff.class, duration());
        aura.healingRate = healingRate();
        aura.shieldingRate = shieldingRate();
        aura.cd = cooldown();
        aura.minDamage = minDamage();
        aura.maxDamage = maxDamage();
        aura.manaSteal = manaSteal();
        Buff.affect(hero, ConstantShielding.class);
        Buff.affect(hero, ManaStealing.class);
        hero.spendAndNext(1f);
    }

    private int shieldingRate(){
        switch (level()){
            case 1: return 2;
            case 2: return 30;
        }
        return 4;
    }

    private int healingRate(){
        switch (level()){
            case 1: return 2;
            case 2: return 25;
        }
        return 5;
    }

    private int minDamage(){
        switch (level()){
            case 1: return 50;
            case 2: return 3;
        }
        return 20;
    }

    private int maxDamage(){
        switch (level()){
            case 1: return 150;
            case 2: return 25;
        }
        return 50;
    }

    private int manaSteal(){
        switch (level()){
            case 1: return 3;
            case 2: return 30;
        }
        return 5;
    }

    private int duration(){
        switch (level()){
            case 1: return 20;
            case 2: return 1000;
        }
        return 50;
    }

    private int cooldown(){
        switch (level()){
            case 1: return 400;
            case 2: return Integer.MAX_VALUE;
        }
        return 250;
    }

//    @Override
//    public int manaCost(){
//        switch (level()){
//            case 1: return 60;
//            case 2: return 0;
//        }
//        return 90;
//    }

    public String desc() {
        return Messages.get(this, "desc", shieldingRate(), healingRate(), manaCost());
    }
}
