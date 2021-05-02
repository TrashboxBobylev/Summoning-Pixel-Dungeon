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

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chungus;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TimedShrink;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.ConjurerSpell;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;

public class Magical extends ConjurerSpell {

    {
        image = ItemSpriteSheet.SR_MAGICAL;
        usesTargeting = true;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        if (ch != null) {
            CellEmitter.center( trajectory.collisionPos ).burst( MagicMissile.WardParticle.UP, Random.IntRange( 8, 15 ) );
            if (ch.alignment == Char.Alignment.ALLY) {
                Buff.affect(ch, Chungus.class, enlargement());
            } else if (ch.alignment == Char.Alignment.ENEMY) {
                Buff.affect(ch, TimedShrink.class, shrinking());
            }
        }
    }

    private int enlargement(){
        switch (level()){
            case 1: return 13;
            case 2: return 5;
        }
        return 5;
    }

    private int shrinking(){
        switch (level()){
            case 1: return 7;
            case 2: return 15;
        }
        return 5;
    }

    @Override
    public int manaCost(){
        switch (level()){
            case 1: return 30;
            case 2: return 35;
        }
        return 18;
    }

    public String desc() {
        return Messages.get(this, "desc", shrinking(), enlargement(), manaCost());
    }

}
