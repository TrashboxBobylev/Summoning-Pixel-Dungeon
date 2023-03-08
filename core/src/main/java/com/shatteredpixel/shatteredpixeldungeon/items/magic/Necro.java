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

package com.shatteredpixel.shatteredpixeldungeon.items.magic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.NecromancyCD;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.NecromancyStat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.WardingWraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class Necro extends ConjurerSpell {

    {
        image = ItemSpriteSheet.CLONE;
        manaCost = 0;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        if ((ch instanceof Minion || ch instanceof DriedRose.GhostHero || ch instanceof WandOfLivingEarth.EarthGuardian ||
                ch instanceof WandOfWarding.Ward || (ch instanceof WardingWraith && ch.alignment == Char.Alignment.ALLY))
                    && Dungeon.hero.buff(NecromancyCD.class) == null && ch.isAlive()){
            Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
            int healing = heal();
            ch.sprite.emitter().burst(Speck.factory(Speck.STEAM), 20);
            Buff.affect(ch, NecromancyStat.class, 1000f).level = healing;
            Buff.affect(Dungeon.hero, NecromancyCD.class, cd());

            ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);
        }
    }

    @Override
    public int manaCost() {
        switch (level()){
            case 1: return 12;
            case 2: return 30;
        }
        return 9;
    }

    private int heal(){
        switch (level()){
            case 1: return 10;
            case 2: return 21;
        }
        return 4;
    }

    private int cd(){
        switch (level()){
            case 1: return 640;
            case 2: return 800;
        }
        return 200;
    }


    @Override
    public String desc() {
        return Messages.get(this, "desc", heal(), cd(), manaCost());
    }
}
