/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * Summoning Pixel Dungeon
 * Copyright (C) 2019-2020 TrashboxBobylev
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

package com.trashboxbobylev.summoningpixeldungeon.items.scrolls;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.*;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.Mob;
import com.trashboxbobylev.summoningpixeldungeon.effects.Flare;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ScrollOfAttunement extends Scroll {

	{
		initials = 9;
	}

	@Override
	public void doRead() {
		
		new Flare( 5, 32 ).color( 0xFFFFFF, true ).show( curUser.sprite, 2f );
		Sample.INSTANCE.play( Assets.SND_READ );
        Sample.INSTANCE.play( Assets.SND_LULLABY );
        GameScene.flash( 0xFFFFFF );
		Invisibility.dispel();
		
		int count = 0;
		Mob affected = null;
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (Dungeon.level.heroFOV[mob.pos])
                if (mob.alignment == Char.Alignment.ALLY) {
                    Buff.affect(mob, Empowered.class, Empowered.DURATION);

                    if (mob.buff(Empowered.class) != null) {
                        count++;
                        affected = mob;
                    }
                } else if (mob.alignment == Char.Alignment.ENEMY){
                    Buff.affect(mob, Weakness.class, Weakness.DURATION / 4);
                }
		}
		
		switch (count) {
		case 0:
			GLog.i( Messages.get(this, "none") );
			break;
		case 1:
			GLog.i( Messages.get(this, "one", affected.getName()) );
			break;
		default:
			GLog.i( Messages.get(this, "many") );
		}
		setKnown();

		readAnimation();
	}
	
	@Override
	public int price() {
		return isKnown() ? 50 * quantity : super.price();
	}
}
