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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts.SilkyQuiver;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.ConjurerSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTierInfo;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class SpiritBow extends Weapon {
	
	public static final String AC_SHOOT		= "SHOOT";
	public static final String AC_DOWNGRADE = "DOWNGRADE";
	public static final String AC_TIERINFO = "TIERINFO";
	
	{
		image = ItemSpriteSheet.SPIRIT_BOW;
		
		defaultAction = AC_SHOOT;
		usesTargeting = true;
		
		unique = true;
		bones = false;
	}
	
	public boolean sniperSpecial = false;

	@Override
	public int throwPos(Hero user, int dst) {
		return super.throwPos(user, dst);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.remove(AC_EQUIP);
		actions.add(AC_SHOOT);
		if (level() > 0) actions.add(AC_DOWNGRADE);
		actions.add( AC_TIERINFO );
		return actions;
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
	}

	@Override
	public void execute(Hero hero, String action) {
		
		super.execute(hero, action);
		
		if (action.equals(AC_SHOOT)) {
			
			curUser = hero;
			curItem = this;
			GameScene.selectCell( shooter );
			
		} else if (action.equals(AC_DOWNGRADE)){
			GameScene.flash(0xFFFFFF);
			Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
			level(level()-1);
			GLog.warning( Messages.get(ConjurerSpell.class, "lower_tier"));
		} else if (action.equals(AC_TIERINFO)){
			curItem = this;
			ShatteredPixelDungeon.runOnRenderThread(new Callback() {
				@Override
				public void call() {
					Game.scene().addToFront(new WndTierInfo(curItem));
				}
			});
		}
	}

	@Override
	public String toString() {

		String name = name();
		String tier = "";
		switch (level()){
			case 0: tier = "I"; break;
			case 1: tier = "II"; break;
			case 2: tier = "III"; break;
		}

		name = Messages.format( "%s %s", name, tier  );

		return name;

	}
	
	@Override
	public String info() {
		String info = desc();

		int min = Math.round(augment.damageFactor(min()));
		int max = Math.round(augment.damageFactor(max()));
		
		info += "\n\n" + Messages.get( SpiritBow.class, "stats",
				Integer.toString(min()),
				Integer.toString(max()),
				STRReq());
		
		if (STRReq() > Dungeon.hero.STR()) {
			info += " " + Messages.get(Weapon.class, "too_heavy");
		} else if (Dungeon.hero.STR() > STRReq()){
			info += " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - STRReq());
		}
		
		switch (augment) {
			case SPEED:
				info += "\n\n" + Messages.get(Weapon.class, "faster");
				break;
			case DAMAGE:
				info += "\n\n" + Messages.get(Weapon.class, "stronger");
				break;
			case NONE:
		}
		
		if (enchantment != null && (cursedKnown || !enchantment.curse())){
			info += "\n\n" + Messages.get(Weapon.class, "enchanted", enchantment.name());
			info += " " + Messages.get(enchantment, "desc");
		}
		
		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Weapon.class, "cursed");
		} else if (!isIdentified() && cursedKnown){
			info += "\n\n" + Messages.get(Weapon.class, "not_cursed");
		}
		
		info += "\n\n" + Messages.get(MissileWeapon.class, "distance");
		
		return info;
	}
	
	@Override
	public int STRReq(int lvl) {
		lvl = Math.max(0, lvl);
		//strength req decreases at +1,+3,+6,+10,etc.
		return 10 - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
	}

	@Override
	public int STRReq() {
		return STRReq(Dungeon.hero.lvl/5);
	}

	@Override
	public int min() {
		return min(level());
	}

	@Override
	public int max() {
		return max(level());
	}

	@Override
	public int min(int lvl) {
		switch (level()){
			case 1:
				return 2 + Dungeon.hero.lvl/5
						+ RingOfSharpshooting.levelDamageBonus(Dungeon.hero)
						+ (curseInfusionBonus ? 1 : 0);
			case 2:
				return 5 + (int)(Dungeon.hero.lvl/1.5f)
						+ RingOfSharpshooting.levelDamageBonus(Dungeon.hero)
						+ (curseInfusionBonus ? 1 : 0);
		}
		return 1 + Dungeon.hero.lvl/5
				+ RingOfSharpshooting.levelDamageBonus(Dungeon.hero)
				+ (curseInfusionBonus ? 1 : 0);
	}
	
	@Override
	public int max(int lvl) {
		switch (level()){
			case 1:
				return 7 + (int)(Dungeon.hero.lvl/2.5f)
						+ 2*RingOfSharpshooting.levelDamageBonus(Dungeon.hero)
						+ (curseInfusionBonus ? 2 : 0);
			case 2:
				return 15 + (int)(Dungeon.hero.lvl/0.8f)
										+ 2*RingOfSharpshooting.levelDamageBonus(Dungeon.hero)
										+ (curseInfusionBonus ? 1 : 0);
		}
		return 6 + (int)(Dungeon.hero.lvl/2.5f)
				+ 2*RingOfSharpshooting.levelDamageBonus(Dungeon.hero)
				+ (curseInfusionBonus ? 2 : 0);
	}

	@Override
	public int targetingPos(Hero user, int dst) {
		return knockArrow().targetingPos(user, dst);
	}
	
	private int targetPos;
	
	@Override
	public int damageRoll(Char owner) {
		int damage = augment.damageFactor(super.damageRoll(owner));
		
		if (owner instanceof Hero) {
			int exStr = ((Hero)owner).STR() - STRReq();
			if (exStr > 0) {
				damage += Random.IntRange( 0, exStr );
			}
		}
		
		if (sniperSpecial){
			switch (augment){
				case NONE:
					damage = Math.round(damage * 0.667f);
					break;
				case SPEED:
					damage = Math.round(damage * 0.5f);
					break;
				case DAMAGE:
					damage = Math.round(damage * 0.8f);
					break;
			}
		}
		
		return damage;
	}
	
	@Override
	public float speedFactor(Char owner) {
		if (sniperSpecial){
			switch (augment){
				case NONE: default:
					return 0f;
				case SPEED:
					return 1f * RingOfFuror.attackDelayMultiplier(owner);
				case DAMAGE:
					return 2f * RingOfFuror.attackDelayMultiplier(owner);
			}
		} else {
			float mod = 1f;
			switch (level()){
				case 1:
					mod = 1f / 0.75f; break;
				case 2:
					mod = 1f / 0.4f;
			}
			return super.speedFactor(owner)*mod;
		}
	}

	@Override
	public int buffedLvl() {
		switch (level()){
			case 1:
				return Dungeon.hero.lvl/3;
			case 2:
				return Dungeon.hero.lvl/6;
		}
		return Dungeon.hero.lvl/5;
	}
	
	@Override
	public boolean isUpgradable() {
		return level() < 2;
	}

	public static boolean superShot = false;
	
	public SpiritArrow knockArrow(){
		if (superShot){
			return new SuperShot();
		}
		return new SpiritArrow();
	}

	public class SuperShot extends SpiritArrow{
		{
			hitSound = Assets.Sounds.HIT_STRONG;
		}

		@Override
		public int image() {
			return ItemSpriteSheet.SR_RANGED;
		}

		@Override
		public int damageRoll(Char owner) {
			int damage = SpiritBow.this.damageRoll(owner);
			int distance = Dungeon.level.distance(owner.pos, targetPos) - 1;
			float multiplier = Math.min(5f, 1.32f * (float)Math.pow(1.13f, distance));
			damage = Math.round(damage * multiplier);
			return damage;
		}

		@Override
		public float speedFactor(Char user) {
			return SpiritBow.this.speedFactor(user) * 2f;
		}

		@Override
		public void onThrow(int cell) {
			superShot = false;
			super.onThrow(cell);
		}
	}
	
	public class SpiritArrow extends MissileWeapon {
		
		{
			image = ItemSpriteSheet.SPIRIT_ARROW;

			hitSound = Assets.Sounds.HIT_ARROW;
		}

        @Override
        public int image() {
            switch (SpiritBow.this.augment){
                case DAMAGE:
                    return ItemSpriteSheet.SPIRIT_BLAST;
                default:
                    return ItemSpriteSheet.SPIRIT_ARROW;
            }
        }

        @Override
		public int damageRoll(Char owner) {
			return SpiritBow.this.damageRoll(owner);
		}
		
		@Override
		public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
			return SpiritBow.this.hasEnchant(type, owner);
		}

		@Override
		public boolean hasGoodEnchant() {
			return SpiritBow.this.hasGoodEnchant();
		}

		@Override
		public int proc(Char attacker, Char defender, int damage) {
			return SpiritBow.this.proc(attacker, defender, damage);
		}
		
		@Override
		public float speedFactor(Char user) {
			return SpiritBow.this.speedFactor(user);
		}
		
		@Override
		public float accuracyFactor(Char owner) {
			if (sniperSpecial && SpiritBow.this.augment == Augment.DAMAGE){
				return Float.POSITIVE_INFINITY;
			} else {
				return super.accuracyFactor(owner);
			}
		}
		
		@Override
		public int STRReq(int lvl) {
			return SpiritBow.this.STRReq(Dungeon.hero.lvl/5);
		}

		@Override
        public void onThrow(int cell) {
			if ((Dungeon.isChallenged(Conducts.Conduct.PACIFIST))){
				Splash.at( cell, 0xCC99FFFF, 1 );
				return;
			}
			Char enemy = Actor.findChar( cell );
			if (enemy == null || enemy == curUser) {
				parent = null;
				Splash.at( cell, 0xCC99FFFF, 1 );
			} else {
				if (!curUser.shoot( enemy, this )) {
					Splash.at(cell, 0xCC99FFFF, 1);
				}
				SilkyQuiver.quiverBuff buff = Dungeon.hero.buff(SilkyQuiver.quiverBuff.class);
				if (buff != null){
					buff.gainCharge(0.125f);
				}
				if (sniperSpecial && SpiritBow.this.augment != Augment.SPEED) sniperSpecial = false;
			}
		}

		@Override
		public void throwSound() {
			Sample.INSTANCE.play( Assets.Sounds.ATK_SPIRITBOW, 1, Random.Float(0.87f, 1.15f) );
		}

		int flurryCount = -1;
		
		@Override
		public void cast(final Hero user, final int dst) {
            final Ballistica ballistica = new Ballistica( user.pos, dst, Ballistica.STOP_SOLID );
			final int cell = SpiritBow.this.augment == Augment.DAMAGE ?  ballistica.collisionPos : throwPos( user, dst );
            SpiritBow.this.targetPos = cell;
            if (sniperSpecial) {
                if (SpiritBow.this.augment == Augment.DAMAGE) {
                    user.busy();

                    Sample.INSTANCE.play( Assets.Sounds.MISS, 0.6f, 0.6f, 1.5f );
                    user.sprite.zap(cell);

                    ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
                            reset(user.sprite,
                                    cell,
                                    this,
                                    new Callback() {
                                        @Override
                                        public void call() {
                                            for (int pos : ballistica.subPath(1, ballistica.dist)) {


                                                Char ch = Actor.findChar( pos );
                                                if (ch == null) {
                                                    continue;
                                                }

                                                if (Char.hit( user, ch, false )) {
													if (!Dungeon.isChallenged(Conducts.Conduct.PACIFIST)) {
														Sample.INSTANCE.play(Assets.Sounds.HIT, 1, 1, Random.Float(0.8f, 1.25f));
														int damage = (int) (damageRoll(user) * 0.9f);
														ch.sprite.bloodBurstA(user.sprite.center(), damage);
														ch.sprite.flash();

														ch.damage(damage, new SpiritBow.SpiritArrow());
														SpiritBow.this.proc(user, ch, damage);
													}
                                                } else {
                                                    ch.sprite.showStatus( CharSprite.NEUTRAL,  ch.defenseVerb() );
                                                }
                                            }

                                            user.spendAndNext(castDelay(user, dst));
                                            sniperSpecial = false;
                                        }
                                    });

                } else if ( SpiritBow.this.augment == Augment.SPEED){
                    if (flurryCount == -1) flurryCount = 3;

                    final Char enemy = Actor.findChar( cell );

                    if (enemy == null){
                        user.spendAndNext(castDelay(user, dst));
                        sniperSpecial = false;
                        flurryCount = -1;
                        return;
                    }
                    QuickSlotButton.target(enemy);

                    final boolean last = flurryCount == 1;

                    Sample.INSTANCE.play( Assets.Sounds.MISS, 0.6f, 0.6f, 1.5f );

                    ((MissileSprite) user.sprite.parent.recycle(MissileSprite.class)).
                            reset(user.sprite,
                                    cell,
                                    this,
                                    new Callback() {
                                        @Override
                                        public void call() {
                                            if (enemy.isAlive()) {
                                                curUser = user;
                                                onThrow(cell);
                                            }

                                            if (last) {
                                                user.spendAndNext(castDelay(user, dst));
                                                sniperSpecial = false;
                                                flurryCount = -1;
                                            }
                                        }
                                    });

                    user.sprite.zap(cell, new Callback() {
                        @Override
                        public void call() {
                            flurryCount--;
                            if (flurryCount > 0){
                                cast(user, dst);
                            }
                        }
                    });

                } else {
                    super.cast(user, dst);
                    return;
                }
            } else {
				super.cast(user, dst);
				return;
			}
		}
	}
	
	private CellSelector.Listener shooter = new CellSelector.Listener() {
		@Override
		public void onSelect( Integer target ) {
			if (target != null) {
				knockArrow().cast(curUser, target);
			}
		}
		@Override
		public String prompt() {
			return Messages.get(SpiritBow.class, "prompt");
		}
	};
}
