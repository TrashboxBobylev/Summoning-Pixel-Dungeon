/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2022 Evan Debenham
 *
 * Summoning Pixel Dungeon
 * Copyright (C) 2019-2022 TrashboxBobylev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.items.magic.soulreaver;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ConstantShielding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ManaStealing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.HolyAuraBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.HolyAuraCD;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.AdHocSpell;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class Support extends AdHocSpell {

    {
        image = ItemSpriteSheet.SR_SUPPORT;
    }

    @Override
    public boolean effect(Hero hero) {
        Sample.INSTANCE.play(Assets.Sounds.READ);
        HolyAuraBuff aura = Buff.affect(hero, HolyAuraBuff.class, duration());
        aura.healingRate = healingRate();
        aura.shieldingRate = shieldingRate();
        aura.cd = cooldown();
        aura.minDamage = minDamage();
        aura.maxDamage = maxDamage();
        aura.manaSteal = manaSteal();
        if (shieldingRate() != 0) Buff.affect(hero, ConstantShielding.class);
        Buff.affect(hero, ManaStealing.class);
        hero.spendAndNext(1f);
        return true;
    }

    private int shieldingRate(){
        switch (level()){
            case 1: return 1;
            case 2: return 0;
        }
        return 4;
    }

    private int healingRate(){
        switch (level()){
            case 1: return 1;
            case 2: return 20;
        }
        return 5;
    }

    private int minDamage(){
        switch (level()){
            case 1: return 50;
            case 2: return 3;
        }
        return 10;
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
            case 1: return 2;
            case 2: return 15;
        }
        return 5;
    }

    private int duration(){
        switch (level()){
            case 1: return 20;
            case 2: return 500;
        }
        return 60;
    }

    private int cooldown(){
        switch (level()){
            case 1: return 500;
            case 2: return 1000;
        }
        return 300;
    }

    @Override
    public int manaCost(){
        switch (level()){
            case 1: return 90;
            case 2: return 30;
        }
        return 75;
    }

    @Override
    public boolean tryToZap(Hero owner) {
        if (owner.buff(HolyAuraCD.class) != null){
            GLog.warning( Messages.get(this, "no_magic") );
            return false;
        }
        return super.tryToZap(owner);
    }

    public String desc() {
        return Messages.get(this, "desc", shieldingRate(), healingRate(), duration(), manaCost());
    }
}
