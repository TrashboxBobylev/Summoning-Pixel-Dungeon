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
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Camera;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class Gauntlet extends MeleeWeapon {
	
	{
		image = ItemSpriteSheet.GAUNTLETS;
		hitSound = Assets.Sounds.HIT_CRUSH;
		hitSoundPitch = 1.2f;
		
		tier = 5;
		DLY = 0.5f; //2x speed
	}
	
	@Override
	public int max(int lvl) {
		return  Math.round(2.5f*(tier+1)) +     //15 base, down from 30
				lvl*Math.round(0.5f*(tier+1));  //+3 per level, down from +6
	}

	@Override
	public int warriorAttack(int damage, Char enemy) {
		ArrayList<Char> affectedChars = new ArrayList<>();
		Ballistica trajectory = new Ballistica(Dungeon.hero.pos, enemy.pos, Ballistica.STOP_TARGET);
		ConeAOE cone = new ConeAOE(
				trajectory,
				5,
				90,
				Ballistica.FRIENDLY_MAGIC
		);
		for (int cell : cone.cells){
			CellEmitter.bottom(cell).burst(Speck.factory(Speck.STEAM), 10);
			Char ch = Actor.findChar( cell );
			if (ch != null && !ch.equals(enemy)) {
				affectedChars.add(ch);
			}
		}
		for (Char ch : affectedChars){
			int dmg = Dungeon.hero.attackProc(ch, damage);
			switch (Dungeon.level.distance(ch.pos, Dungeon.hero.pos)){
				case 2: dmg *= 0.66f; break;
				case 3: dmg *= 0.33f; break;
				case 4: dmg *= 0.16f; break;
				case 5: dmg *= 0.1f; break;
			}
			dmg -= ch.actualDrRoll();
			dmg = ch.defenseProc(Dungeon.hero, dmg);
			ch.damage(dmg, Dungeon.hero);
		}
		Sample.INSTANCE.play(Assets.Sounds.ROCKS);
		Camera.main.shake( 3, 0.7f );
		return super.warriorAttack(damage, enemy);
	}
}
