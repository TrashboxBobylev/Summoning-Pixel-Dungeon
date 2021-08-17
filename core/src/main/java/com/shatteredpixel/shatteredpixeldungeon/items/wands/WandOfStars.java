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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FrostBurn;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.SoulWeakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FrostfireParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.*;

public class WandOfStars extends DamageWand {

    public static final String AC_UNLEASH = "UNLEASH";

	{
		image = ItemSpriteSheet.STAR_WAND;
		collisionProperties = Ballistica.STOP_TARGET | Ballistica.STOP_SOLID;
	}

    @Override
    protected int initialCharges() {
        return 3;
    }

    private boolean detonation = false;

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_UNLEASH)){
            charger.gainCharge(1f);
            SparseArray<Star> stars = Dungeon.level.stars();
            int[] pos = stars.keyArray();
            boolean effect = false;
            for (int p : pos){
                if (Dungeon.level.heroFOV[p]){
                    effect = true;
                    for (Heap heap : Dungeon.level.heaps.valueList()){
                        for (Item item : heap.items){
                            if (item instanceof Star) heap.remove(item);
                        }
                    }
                    Sample.INSTANCE.play(Assets.Sounds.ZAP);
                    if (!Dungeon.isChallenged(Conducts.Conduct.PACIFIST)) {
                        for (int i : PathFinder.NEIGHBOURS9) {
                            CellEmitter.get(p + i).burst(FrostfireParticle.FACTORY, 8 + 4 * level());
                            Char ch = Actor.findChar(p + i);
                            if (ch != null) {
                                ch.damage(damageRoll(), this);
                                processSoulMark(ch, chargesPerCast());
                            }
                        }
                    }
                }
            }

            if (!effect) GLog.warning( Messages.get(CursedWand.class, "nothing"));
        }
    }

    @Override
    public boolean tryToZap( Hero owner, int target ){

        if (owner.buff(MagicImmune.class) != null){
            GLog.warning( Messages.get(this, "no_magic") );
            return false;
        }
        if (owner.buff(SoulWeakness.class) != null){
            GLog.warning(Messages.get(this, "fizzles"));
            return false;
        }

        if (Dungeon.level.numberOfStars() > 0 && curCharges == 0){
                Heap heap = Dungeon.level.heaps.get(target);
                if (heap != null) {
                    for (Item item : heap.items) {
                        if (item instanceof Star) return true;
                    }
                }
            GLog.warning(Messages.get(this, "star_fizzles"));
            return false;
        }
        else if ( curCharges >= (cursed ? 1 : chargesPerCast())){
            return true;
        } else {
            GLog.warning(Messages.get(this, "fizzles"));
            return false;
        }
    }

    public int min(int lvl){
	    if (Dungeon.level == null) return (3+lvl);
	    int num = Dungeon.level.numberOfStars();
	    if (num < 3) num = 0;
		return (int) ((3+lvl) * (Math.pow(0.93f, Math.max(0, num - 2))));
	}

	public int max(int lvl){
        if (Dungeon.level == null) return (8+lvl*3);
        int num = Dungeon.level.numberOfStars();
        if (num < 3) num = 0;
        return (int) ((9+lvl*4) * (Math.pow(0.93f, Math.max(0, num - 2))));
	}

    @Override
    protected int chargesPerCast() {
        if (detonation){
            detonation = false;
            return 0;
        } else return super.chargesPerCast();
    }

    @Override
    public void onZap(Ballistica bolt) {

	    Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
	    if (heap != null){
	        for (Item item: heap.items){
	            if (item instanceof Star) {
	                detonation = true;
	                execute(Dungeon.hero, AC_UNLEASH);
                }
	            return;
            }
        }

        if (canPlaceStar(bolt.collisionPos)){
            Star star = new Star();
            Dungeon.level.drop(star, bolt.collisionPos);
            Dungeon.level.pressCell(bolt.collisionPos);
        } else {
            GLog.warning( Messages.get(this, "bad_location"));
        }

	}

    public static boolean canPlaceStar(int pos){

        for (int i : PathFinder.NEIGHBOURS9){
            Heap heap = Dungeon.level.heaps.get(pos + i);
            if (heap != null) {
                for (Item item : heap.items) {
                    if (item instanceof Star) return false;
                }
            }
        }

        return true;

    }

    @Override
    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0x6de8e4);
        particle.am = 0.6f;
        particle.setLifespan(1.2f);
        float angle = Random.Float(PointF.PI2);
        particle.speed.polar( angle, 5f);
        particle.acc.set( 0f, 2f);
        particle.setSize( 0f, 3.2f);
        particle.sizeJitter = 1f;
        particle.radiateXY(Random.Float(1f));
        float dst = Random.Float(1f);
        particle.x -= dst;
        particle.y += dst;
    }

    @Override
    public void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(curUser.sprite.parent,
                MagicMissile.STAR,
                curUser.sprite,
                bolt.collisionPos,
                callback);
        Sample.INSTANCE.play(Assets.Sounds.ZAP);
    }

    @Override
    public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {

        if (Random.Int( staff.level() + 3 ) >= 2) {

            if (defender.buff(FrostBurn.class) != null){
                Buff.affect(defender, FrostBurn.class).reignite(defender, 8f);
                int burnDamage = Random.NormalIntRange( 1, 3 + Dungeon.depth/4 * 5 / Dungeon.chapterSize() );
                defender.damage( Math.round(burnDamage * 0.67f), this );
            } else {
                Buff.affect(defender, FrostBurn.class).reignite(defender, 8f);
            }

            defender.sprite.emitter().burst( FlameParticle.FACTORY, staff.level() + 1 );

        }
    }

	public static class Star extends Item {
        {
            image = ItemSpriteSheet.CRYSTAL;
        }

        public int minDamage;
	    public int maxDamage;

        private static ItemSprite.Glowing WHITE = new ItemSprite.Glowing( 0xFFFFFF, 0.25f );

        @Override
        public ItemSprite.Glowing glowing() {
            return WHITE;
        }

        @Override
        public boolean doPickUp(Hero hero) {
            GLog.i( Messages.get(this, "not_pickable") );
            return false;
        }

    }

}
