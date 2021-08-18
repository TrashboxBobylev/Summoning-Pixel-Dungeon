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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.CorrosionParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.ColorMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.text.DecimalFormat;

public class WandOfCorrosion extends Wand {

	{
		image = ItemSpriteSheet.WAND_CORROSION;

		collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
	}

	public int amountOfGas(int level){
		switch (level){
			case 1: return 1;
			case 2: return 100;
		}
		return 75;
	}

	public int gasStrength(int level){
		switch (level){
			case 1: return 5 + Dungeon.hero.lvl/3;
			case 2: return 3 + Dungeon.hero.lvl/2;
		}
		return 2 + Dungeon.hero.lvl/4;
	}

	@Override
	public float rechargeModifier(int level) {
		switch (level){
			case 0: return 1.0f;
			case 1: return 1.0f;
			case 2: return 2.2f;
		}
		return 0f;
	}

	@Override
	public String getTierMessage(int tier){
		return Messages.get(this, "tier" + tier,
				new DecimalFormat("#.##").format(charger.getTurnsToCharge(tier-1)),
				amountOfGas(tier-1),
				gasStrength(tier-1)
		);
	}

	@Override
	public void onZap(Ballistica bolt) {
		CellEmitter.get(bolt.collisionPos).burst(Speck.factory(Speck.CORROSION), 10);
		Sample.INSTANCE.play(Assets.Sounds.GAS);
		if (level() != 1) {
			CorrosiveGas gas = Blob.seed(bolt.collisionPos, amountOfGas(level()), CorrosiveGas.class);
			CellEmitter.get(bolt.collisionPos).burst(Speck.factory(Speck.CORROSION), 10);
			gas.setStrength(gasStrength(level()));
			if ((Dungeon.isChallenged(Conducts.Conduct.PACIFIST)))
				gas.setStrength(level() / 5);
			GameScene.add(gas);
		} else {
			Char ch = Actor.findChar(bolt.collisionPos);
			if (ch != null){
				Buff.affect(ch, Corrosion.class).set(5f, gasStrength(1));
			}
		}

		for (int i : PathFinder.NEIGHBOURS9) {
			Char ch = Actor.findChar(bolt.collisionPos + i);
			if (ch != null && (!Dungeon.isChallenged(Conducts.Conduct.PACIFIST))) {
				processSoulMark(ch, chargesPerCast());
			}
		}
		
		if (Actor.findChar(bolt.collisionPos) == null){
			Dungeon.level.pressCell(bolt.collisionPos);
		}
	}

	@Override
    public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(
				curUser.sprite.parent,
				MagicMissile.CORROSION,
				curUser.sprite,
				bolt.collisionPos,
				callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}

	@Override
	public void onHit(Wand wand, Char attacker, Char defender, int damage) {
		// lvl 0 - 33%
		// lvl 1 - 50%
		// lvl 2 - 60%
		if (Random.Int( buffedLvl() + 3 ) >= 2) {
			
			Buff.affect( defender, Ooze.class ).set( Ooze.DURATION );
			CellEmitter.center(defender.pos).burst( CorrosionParticle.SPLASH, 5 );
			
		}
	}

	@Override
	public void staffFx(StaffParticle particle) {
		particle.color( ColorMath.random( 0xAAAAAA, 0xFF8800) );
		particle.am = 0.6f;
		particle.setLifespan( 1f );
		particle.acc.set(0, 20);
		particle.setSize( 0.5f, 3f );
		particle.shuffleXY( 1f );
	}

    @Override
    public String statsDesc() {
        if (!levelKnown)
            return Messages.get(this, "stats_desc", 2, 75);
        else
            return Messages.get(this, "stats_desc", gasStrength(level()), amountOfGas(level()));
    }

}
