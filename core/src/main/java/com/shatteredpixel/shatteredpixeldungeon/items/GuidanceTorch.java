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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GuidanceLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class GuidanceTorch extends Item {

	public static final String AC_LIGHT	= "LIGHT";
	
	public static final float TIME_TO_LIGHT = 1;
	
	{
		image = ItemSpriteSheet.GUIDING_TORCH;
		
		stackable = true;
		
		defaultAction = AC_LIGHT;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_LIGHT );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );
		
		if (action.equals( AC_LIGHT )) {
			
			hero.spend( TIME_TO_LIGHT );
			hero.busy();
			
			hero.sprite.operate( hero.pos );
			
			detach( hero.belongings.backpack );

			if (hero.buff(GuidanceLight.class) == null){
				Buff.affect(hero, GuidanceLight.class).set(GuidanceLight.duration());
			} else {
				Buff.affect(hero, GuidanceLight.class).prolong(GuidanceLight.duration()/2f);
			}

			Sample.INSTANCE.play(Assets.Sounds.BURNING);

			Emitter emitter = hero.sprite.centerEmitter();
			emitter.start( GuidanceParticleFactory, 0.2f, 8 );
			
		}
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return !Dungeon.isChallenged(Conducts.Conduct.UNKNOWN);
	}
	
	@Override
	public int value() {
		return 80 * quantity;
	}

	@Override
	public Emitter emitter() {
		if (Dungeon.hero == null) return null;
		Emitter emitter = new Emitter();
		emitter.pos(11f, 2f);
		emitter.fillTarget = false;
		emitter.pour(GuidanceParticleFactory, 0.085f);
		return emitter;
	}

	public final Emitter.Factory GuidanceParticleFactory = new Emitter.Factory() {
		@Override
		//reimplementing this is needed as instance creation of new staff particles must be within this class.
		public void emit(Emitter emitter, int index, float x, float y ) {
			TorchyParticle c = (TorchyParticle)emitter.getFirstAvailable(TorchyParticle.class);
			if (c == null) {
				c = new TorchyParticle();
				emitter.add(c);
			}
			c.reset(x, y);
		}

		@Override
		//some particles need light mode, others don't
		public boolean lightMode() {
			return false;
		}
	};

	public class TorchyParticle extends PixelParticle {

		private float minSize;
		private float maxSize;
		public float sizeJitter = 0;

		public TorchyParticle(){
			super();
		}

		public void reset( float x, float y ) {
			revive();

			speed.set(0);

			this.x = x;
			this.y = y;
			color( 0x00e500 );
			am = 0.5f;
			setLifespan(0.75f);
			acc.set(0, -40);
			setSize( 0f, 4f);
			shuffleXY( 1.5f );
		}

		public void setSize( float minSize, float maxSize ){
			this.minSize = minSize;
			this.maxSize = maxSize;
		}

		public void setLifespan( float life ){
			lifespan = left = life;
		}

		public void shuffleXY(float amt){
			x += Random.Float(-amt, amt);
			y += Random.Float(-amt, amt);
		}

		@Override
		public void update() {
			super.update();
			size(minSize + (left / lifespan)*(maxSize-minSize) + Random.Float(sizeJitter));
		}
	}

}
