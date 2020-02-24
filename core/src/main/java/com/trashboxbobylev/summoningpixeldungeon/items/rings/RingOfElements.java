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

package com.trashboxbobylev.summoningpixeldungeon.items.rings;

import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.Electricity;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.ToxicGas;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.WandOfStenchGas;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.*;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.*;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.Hacatu;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.Wizard;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.*;
import com.trashboxbobylev.summoningpixeldungeon.levels.traps.DisintegrationTrap;
import com.trashboxbobylev.summoningpixeldungeon.levels.traps.GrimTrap;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;

import java.text.DecimalFormat;
import java.util.HashSet;

public class RingOfElements extends Ring {
	
	public String statsInfo() {
		if (isIdentified()){
			return Messages.get(this, "stats", new DecimalFormat("#.##").format(100f * (1f - Math.pow(0.935f, soloBonus()))));
		} else {
			return Messages.get(this, "typical_stats", new DecimalFormat("#.##").format(7.5f));
		}
	}
	
	@Override
	protected RingBuff buff( ) {
		return new Resistance();
	}

	public static final HashSet<Class> RESISTS = new HashSet<>();
	static {
		RESISTS.add( Burning.class );
		RESISTS.add( Charm.class );
		RESISTS.add( Chill.class );
		RESISTS.add( Frost.class );
		RESISTS.add( Ooze.class );
		RESISTS.add( Paralysis.class );
		RESISTS.add( Poison.class );
		RESISTS.add( Corrosion.class );
		RESISTS.add( Weakness.class );
		RESISTS.add(FrostBurn.class);

		
		RESISTS.add( DisintegrationTrap.class );
		RESISTS.add( GrimTrap.class );

		RESISTS.add( WandOfBlastWave.class );
		RESISTS.add( WandOfDisintegration.class );
		RESISTS.add( WandOfFireblast.class );
		RESISTS.add( WandOfFrost.class );
		RESISTS.add( WandOfLightning.class );
		RESISTS.add( WandOfLivingEarth.class );
		RESISTS.add( WandOfMagicMissile.class );
		RESISTS.add( WandOfPrismaticLight.class );
		RESISTS.add( WandOfTransfusion.class );
		RESISTS.add( WandOfWarding.Ward.class );
		RESISTS.add(WandOfCrystalBullet.class);
		RESISTS.add( WandOfStars.Star.class);
		
		RESISTS.add( ToxicGas.class );
		RESISTS.add (WandOfStenchGas.class);
		RESISTS.add( Electricity.class );
		
		RESISTS.add( Shaman.LightningBolt.class );
        RESISTS.add( Hacatu.LightningBolt.class );
		RESISTS.add( Warlock.DarkBolt.class );
        RESISTS.add( Wizard.DarkBolt.class );
		RESISTS.add( Eye.DeathGaze.class );
		RESISTS.add( Yog.BurningFist.DarkBolt.class );
		RESISTS.add(FinalFroggit.Bolt.class);
	}
	
	public static float resist( Char target, Class effect ){
		if (getBonus(target, Resistance.class) == 0) return 1f;
		
		for (Class c : RESISTS){
			if (c.isAssignableFrom(effect)){
				return (float)Math.pow(0.935, getBonus(target, Resistance.class));
			}
		}
		
		return 1f;
	}

    public static float damageMultiplier( Char t ){
        //(HT - HP)/HT = heroes current % missing health.
        return (float)Math.pow(0.935, getBonus( t, Resistance.class));
    }
	
	public class Resistance extends RingBuff {
	
	}
}
