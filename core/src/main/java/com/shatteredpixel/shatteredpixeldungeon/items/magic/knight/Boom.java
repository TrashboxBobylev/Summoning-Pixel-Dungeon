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

package com.shatteredpixel.shatteredpixeldungeon.items.magic.knight;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.GoatClone;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.AdHocSpell;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

import java.text.DecimalFormat;

public class Boom extends AdHocSpell {

    {
        image = ItemSpriteSheet.BOOM;
    }

    @Override
    public boolean effect(Hero hero) {
        GoatClone clone = GoatClone.findClone();
        if (clone != null){
            hero.spendAndNext(1f);
            Sample.INSTANCE.play(Assets.Sounds.BLAST);
            for (int i: PathFinder.NEIGHBOURS8){
                int pos = clone.pos + i;
                CellEmitter.get(pos).burst(MagicMissile.WhiteParticle.FACTORY, 12);
                Char ch = Actor.findChar(pos);
                if (ch != null && ch != Dungeon.hero){
                    ch.damage((int) (Bomb.damageRoll()*damage()), clone);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private float damage(){
        switch (level()){
            case 1: return 0.5f;
            case 2: return 0.2f;
        }
        return 1f;
    }

    @Override
    public int manaCost() {
        switch (level()){
            case 1: return 30;
            case 2: return 15;
        }
        return 50;
    }

    public String desc() {
        return Messages.get(this, "desc", new DecimalFormat("#").format(damage()*100), manaCost());
    }

}
