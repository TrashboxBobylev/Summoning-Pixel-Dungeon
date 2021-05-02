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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class Scimitar extends MeleeWeapon {

	{
		image = ItemSpriteSheet.SCIMITAR;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.2f;

		tier = 3;
		DLY = 0.9f; //1.11x speed
	}

	public int strikes;

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("strikes", strikes);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        strikes = bundle.getInt("strikes");
    }

    @Override
    public int min(int lvl) {
        return tier + lvl/2;
    }

    @Override
	public int max(int lvl) {
		return  5*(tier) +    //15 base, down from 20
				lvl*(tier);   //+3 instead of +4
	}

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (++strikes == 4) {
            damage *= 2;
            defender.sprite.showStatus(CharSprite.WARNING, "crit!");
            strikes = 0;
        }

        return super.proc(attacker, defender, damage);
    }

    static int targetNum = 0;

    @Override
    public int warriorAttack(int damage, Char enemy) {
        ArrayList<Char> targets = new ArrayList<>();

        for (int i : PathFinder.NEIGHBOURS8){
            if (Actor.findChar(Dungeon.hero.pos + i) != null) targets.add(Actor.findChar(Dungeon.hero.pos + i));
        }

        for (Char target : targets){
            Dungeon.hero.attack(target);
        }
        targetNum = targets.size();

        Dungeon.hero.sprite.centerEmitter().start( Speck.factory( Speck.KIT ), 0.03f, 8 );
        Sample.INSTANCE.play(Assets.Sounds.CHAINS, 3);

        return 0;
    }

    @Override
    public float warriorDelay(float delay, Char enemy) {
        int tries = targetNum;
        targetNum = 0;
        return speedFactor(Dungeon.hero) * tries;
    }
}
