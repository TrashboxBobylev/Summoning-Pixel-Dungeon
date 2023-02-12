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

package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.ShapedParticle;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.Emitter.Factory;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;

public class WebParticle extends PixelParticle {
	
	public static final Emitter.Factory FACTORY = new Factory() {
		@Override
		public void emit( Emitter emitter, int index, float x, float y ) {
			for (int i=0; i < 8; i++) {
				((WebParticle)emitter.recycle( WebParticle.class )).reset( x, y, i );
			}

			for (int i=1; i < 3; i++){
				((WebCircleParticle)emitter.recycle( WebCircleParticle.class )).reset( x, y, i );
			}
		}
	};

	public WebParticle() {
		super();
		
		color( 0xCCCCCC );
		lifespan = 0.65f;
	}
	
	public void reset( float x, float y, int index ) {
		revive();
		
		this.x = x;
		this.y = y;
		
		left = lifespan;
		angle = 360 - 45 * index;
	}

	@Override
	public void update() {
		super.update();
		
		float p = left / lifespan;
		if (rm != 0xCC){

			resetColor();
			am = p < 0.6f ? p : 1 - p;
			if (lifespan == 0.33f){
				hardlight( ColorMath.interpolate(0x6cb3ff, 0xe0efff, am));
				scale.y = 9f - p * 6f;
			} else {
				hardlight( ColorMath.interpolate(0x4fa4ff, 0xd5e9ff, am));
				scale.y = 11.5f - p * 5f;
			}
		}
		else {
			am = p < 0.25f ? p : 1 - p;
			scale.y = 12 + p * 6;
		}
	}

	public static class WebCircleParticle extends ShapedParticle{
		public WebCircleParticle(){
			super(Effects.Type.CIRCLE);
			color( 0xCCCCCC );
			lifespan = 0.65f;
		}

		public void reset( float x, float y, int index) {
			revive();
			if (index == 1){
				this.x = x - 3f;
				this.y = y - 3.5f;
			}
			else {
				this.x = x - 4.7f;
				this.y = y - 5f;
			}

			left = lifespan;
			size(0.4f + index*0.4f);
		}

		@Override
		public void update() {
			super.update();

			float p = left / lifespan;
			am = p < 0.25f ? p : 1 - p;
		}
	}
}