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

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.FlowersCD;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.ConjurerSpell;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class Flower extends ConjurerSpell {

    {
        image = ItemSpriteSheet.FLOWER;
    }

    @Override
    public void effect(Ballistica trajectory) {
        int pos = trajectory.collisionPos;
        if ((Dungeon.level.map[pos] != Terrain.ALCHEMY
                && !Dungeon.level.pit[pos]
                && Dungeon.level.traps.get(pos) == null
                && !Dungeon.isChallenged(Challenges.NO_HERBALISM)))
        Dungeon.level.plant(new Swiftthistle.Seed(), pos);
        Buff.affect(Dungeon.hero, FlowersCD.class, cooldown());
    }

    private int cooldown(){
        switch (level()){
            case 1: return 25;
            case 2: return 10;
        }
        return 50;
    }

    @Override
    public int manaCost() {
        switch (level()){
            case 1: return 36;
            case 2: return 72;
        }
        return 18;
    }

    @Override
    public boolean tryToZap(Hero owner) {
        if (owner.buff(FlowersCD.class) != null){
            GLog.warning( Messages.get(this, "no_magic") );
            return false;
        }
        return super.tryToZap(owner);
    }

    public String desc() {
        return Messages.get(this, "desc", cooldown(), manaCost());
    }

}
