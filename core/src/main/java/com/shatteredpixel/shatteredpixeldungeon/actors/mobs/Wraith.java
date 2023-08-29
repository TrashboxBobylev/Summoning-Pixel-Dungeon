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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Attunement;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.stationary.RoseWraith;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WraithSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Wraith extends Mob {

	private static final float SPAWN_DELAY	= 2f;
	
	private int level;
    public RoseWraith parent = null;
	
	{
		spriteClass = WraithSprite.class;
		
		HP = HT = 1;
		EXP = 0;

		maxLvl = -2;
		
		flying = true;

		properties.add(Property.UNDEAD);
	}
	
	private static final String LEVEL = "level";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( LEVEL, level );
		bundle.put( "parent", parent);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		level = bundle.getInt( LEVEL );
		parent = (RoseWraith) bundle.get("parent");
		if (parent == null) adjustStats( level );
		else adjustStatsWhenSummoned(parent);
	}
	
	@Override
	public int damageRoll() {
        int i = Random.NormalIntRange(1 + level / 2, 2 + level);
        if (parent != null) {
            i = Random.NormalIntRange(parent.minDamage, parent.maxDamage);
            if (Dungeon.hero.buff(Attunement.class) != null) i *= Attunement.empowering();
        }
		switch (Dungeon.hero.pointsInTalent(Talent.ARMORED_ARMADA)){
			case 2: i *= 1.25f; break;
			case 3: i *= 1.40f; break;
		}
        return i;
	}
	
	@Override
	public int attackSkill( Char target ) {
        int i = 10 + level;
        if (parent != null) i = parent.attackSkill(target);
        return i;
	}

	@Override
	public int defenseSkill(Char enemy) {
		int skill = super.defenseSkill(enemy);
		switch (Dungeon.hero.pointsInTalent(Talent.ARMORED_ARMADA)){
			case 1: skill *= 0.3f; break;
			case 2: skill *= 0.5f; break;
			case 3: skill *= 0.66f; break;
		}
		return skill;
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		if (buff(Talent.ArmoredArmadaArmor.class) != null){
			Talent.ArmoredArmadaArmor armor = buff(Talent.ArmoredArmadaArmor.class);
			armor.hits--;
			if (armor.hits <= 0)
				armor.detach();
			return 0;
		}
		else
			return super.defenseProc(enemy, damage);
	}

	public void adjustStats(int level ) {
		this.level = level;
		defenseSkill = attackSkill( null ) * 5;
		enemySeen = true;
	}

    public void adjustStatsWhenSummoned( RoseWraith wraith ) {
        this.level = Dungeon.depth;
        defenseSkill = wraith.attackSkill( wraith.enemy )*5;
        enemySeen = true;
    }

	@Override
	public boolean reset() {
		state = WANDERING;
		return true;
	}
	
	public static Wraith spawnAround( int pos ) {
		for (int n : PathFinder.NEIGHBOURS4) {
			int cell = pos + n;
			if (Dungeon.level.passable[cell] && Actor.findChar( cell ) == null) {
				return spawnAt( cell );
			}
		}
		return null;
	}

	@Override
	public void spend(float time) {
		if (Dungeon.mode == Dungeon.GameMode.DIFFICULT){
			super.spend(time*0.75f);
		}
		else super.spend(time);
	}

	public static Wraith spawnAt(int pos ) {
		if (Dungeon.level.passable[pos] && Actor.findChar( pos ) == null) {
			
			Wraith w = new Wraith();
			w.adjustStats( Dungeon.scaledDepth() );
			w.pos = pos;
			w.state = w.HUNTING;
			GameScene.add( w, SPAWN_DELAY );
			
			w.sprite.alpha( 0 );
			w.sprite.parent.add( new AlphaTweener( w.sprite, 1, 0.5f ) );
			
			w.sprite.emitter().burst( ShadowParticle.CURSE, 5 );
			
			return w;
		} else {
			return null;
		}
	}

	public static Wraith spawnForcefullyAt( int pos ) {
		if (Dungeon.level.passable[pos]) {

			Wraith w = new Wraith();
			w.adjustStats( Dungeon.scaledDepth() );
			w.pos = pos;
			w.state = w.HUNTING;
			GameScene.add( w, SPAWN_DELAY );

			w.sprite.alpha( 0 );
			w.sprite.parent.add( new AlphaTweener( w.sprite, 1, 0.5f ) );

			w.sprite.emitter().burst( ShadowParticle.CURSE, 5 );

			return w;
		} else {
			return null;
		}
	}

    public static Wraith summonAt(RoseWraith wraith) {

        ArrayList<Integer> points = Level.getSpawningPoints(wraith.pos);

        if (points.size() > 0) {
            int position = points.get(Random.index(points));

                Wraith w = new Wraith();
                w.parent = wraith;
                w.adjustStatsWhenSummoned(wraith);
                w.pos = position;
                w.state = w.HUNTING;
                GameScene.add(w);
                Buff.affect(w, Corruption.class);
                w.sprite.alpha(0);
                w.sprite.parent.add(new AlphaTweener(w.sprite, 1, 0.5f));

                w.sprite.emitter().burst(ShadowParticle.CURSE, 5);

                Sample.INSTANCE.play( Assets.Sounds.CURSED );

                return w;
        } else {
            return null;
        }
    }
	
	{
		immunities.add( Grim.class );
		immunities.add( Terror.class );
	}
}
