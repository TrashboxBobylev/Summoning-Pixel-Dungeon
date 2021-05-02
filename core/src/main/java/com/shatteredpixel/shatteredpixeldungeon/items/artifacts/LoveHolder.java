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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.WardingWraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.stationary.RoseWraith;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WhiteParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ConjurerArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class LoveHolder extends Artifact {

    public int str;
    private int soultype = -1;
    public int totalHealing = 0;

    private int[] healingTable = {
            10, 25, 50, 75, 100, 125, 150, 175, 200, 225, Integer.MAX_VALUE
    };

	{
		image = ItemSpriteSheet.ARTIFACT_LOVE1;

		levelCap = 10;
		defaultAction = AC_PRICK;

        charge = 0;
        str = 1;
        chargeCap = Math.min(50 + level()*25, 500);
	}

	public static final String AC_PRICK = "PRICK";
    public static final String AC_TUNE = "TUNE";
    public static final String AC_SHIELD = "SHIELD";
    public static final String AC_SPLASH = "SPLASH";
    public static final String AC_DEFENSE = "DEFENSE";
    public static final String AC_OFFENSE = "OFFENSE";
    public static final String AC_DAMAGE = "DAMAGE";
    public static final String AC_HEAL = "HEAL";
    public static final String HEALING = "healing";
    public static final String STRENGTH = "str";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(HEALING, totalHealing);
        bundle.put(STRENGTH, str);
    }

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped( hero ) && !cursed) {
            actions.add(AC_PRICK);
            actions.add(AC_TUNE);
            if (level() >= 3) actions.add(AC_SHIELD);
            if (level() >= 6) actions.add(AC_SPLASH);
            if (hero.subClass == HeroSubClass.SOUL_REAVER){
                actions.add(AC_OFFENSE);
                actions.add(AC_DEFENSE);
            }
            if (hero.subClass == HeroSubClass.OCCULTIST){
                actions.remove(AC_PRICK);
                actions.remove(AC_SHIELD);
                actions.remove(AC_SPLASH);
                actions.add(AC_DAMAGE);
                actions.add(AC_HEAL);
                defaultAction = AC_DAMAGE;
            }
        }
		return actions;
	}



	@Override
	public void execute(final Hero hero, String action ) {
		super.execute(hero, action);

		if (action.equals(AC_PRICK) || action.equals(AC_SHIELD) || action.equals(AC_SPLASH) || action.equals(AC_OFFENSE) || action.equals(AC_DEFENSE)){
		    int type = -1;
		    switch (action){
                case AC_PRICK:
                    type = 0; break;
                case AC_SHIELD:
                    type = 1; break;
                case AC_SPLASH:
                    type = 2; break;
                case AC_OFFENSE:
                    type = 3; break;
                case AC_DEFENSE:
                    type = 4; break;
            }
		    prick(Dungeon.hero, type);
		}
        if (action.equals(AC_TUNE)){
            GameScene.show(
                    new WndOptions(Messages.titleCase(Messages.get(this, "name")),
                            Messages.get(this, "how_many"),
                            Messages.get(this, "bit"),
                            str > 1 ? Messages.get(this, "few") : "") {
                        @Override
                        protected void onSelect(int index) {
                                if (index == 0) str++;
                                else str--;
                                GLog.warning(Messages.get(LoveHolder.class, "ontune", str));
                                hero.spendAndNext(Actor.TICK);
                        }
                    }
            );

			/*int damage = 3*(level()*level());

			if (damage > hero.HP*0.75) {

				GameScene.show(
					new WndOptions(Messages.titleCase(Messages.get(this, "name")),
							Messages.get(this, "prick_warn"),
							Messages.get(this, "yes"),
							Messages.get(this, "no")) {
						@Override
						protected void onSelect(int index) {
							if (index == 0)
								prick(Dungeon.hero);
						}
					}
				);

			} else {
				prick(hero);
			}*/
		}
        if (action.equals(AC_DAMAGE)){
            if (hero.subClass != HeroSubClass.NONE) {
                curUser = hero;
                GameScene.selectCell(zapper2);
            } else {
                Messages.get(ConjurerArmor.class, "no_mastery");
            }
        }
        if (action.equals(AC_HEAL)){

            if (charge >= str*1.5f) {
                hero.busy();
                hero.sprite.operate(hero.pos, new Callback() {
                    @Override
                    public void call() {
                        hero.spendAndNext(2f);
                        hero.sprite.idle();
                        hero.sprite.emitter().burst(WhiteParticle.UP, 8);
                        Sample.INSTANCE.play(Assets.Sounds.LULLABY);
                        hero.sprite.showStatus( CharSprite.POSITIVE, Integer.toString( str*2 ) );
                        hero.HP = Math.min(hero.HT, hero.HP + str*2);
                        charge = (int) Math.max(0, charge - str*1.5f);
                        new Flare(10, 64).color(0xFFFFFF, true).show(Dungeon.hero.sprite.parent, DungeonTilemap.tileCenterToWorld(hero.pos), 1.5f);
                    }
                });
            } else {
                GLog.i(Messages.get(LoveHolder.class, "not_enough"));
            }
        }
	}

	private void prick(Hero hero, int type){
        if (hero.subClass == HeroSubClass.OCCULTIST) GLog.i(Messages.get(LoveHolder.class, "no_way"));
        if (charge < str || (type == 2 && (charge < str * 2))) {
            GLog.i(Messages.get(LoveHolder.class, "not_enough"));
        }

                else {
            curUser = hero;
            curItem = this;
            soultype = type;
            GameScene.selectCell(zapper);
        }
        updateQuickslot();
    }

	protected CellSelector.Listener zapper = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null){
                final Ballistica shot = new Ballistica( curUser.pos, target, Ballistica.MAGIC_BOLT);
                int cell = shot.collisionPos;

                if (target == curUser.pos || cell == curUser.pos) {
                    GLog.i( Messages.get(Wand.class, "self_target") );
                    return;
                }

                curUser.sprite.zap(cell);

                //attempts to target the cell aimed at if something is there, otherwise targets the collision pos.
                if (Actor.findChar(target) != null)
                    QuickSlotButton.target(Actor.findChar(target));
                else
                    QuickSlotButton.target(Actor.findChar(cell));

                if (Dungeon.hero.mana >= str) {
                    Dungeon.hero.mana -= str;
                    updateQuickslot();
                    curUser.busy();
                    Invisibility.dispel();
                    MagicMissile.boltFromChar(curUser.sprite.parent,
                            MagicMissile.MAGIC_MISSILE,
                            curUser.sprite,
                            shot.collisionPos,
                            new Callback() {
                                @Override
                                public void call() {
                                    Char ch = Actor.findChar(shot.collisionPos);

                                    if (ch instanceof Minion || ch instanceof DriedRose.GhostHero || ch instanceof WandOfLivingEarth.EarthGuardian || ch instanceof WandOfWarding.Ward || (ch instanceof WardingWraith && ch.alignment == Char.Alignment.ALLY)){
                                        Sample.INSTANCE.play(Assets.Sounds.DRINK);

                                        int healing = getHealingFromStrength(str);

                                        if (Random.Float() < 0.33f && curUser.subClass == HeroSubClass.SOUL_REAVER){
                                            if (Random.Int(1) == 0) Buff.prolong(ch, Empowered.class, 7f);
                                            else Buff.prolong(ch, Bless.class, 7f);
                                        }

                                        int wastedHealing = (ch.HP + healing) - ch.HT;
                                        if (wastedHealing > 0){
                                            healing -= wastedHealing;
                                            if (soultype < 2) Dungeon.hero.mana += wastedHealing / 2; //some of unnecessary soul will return
                                        }
                                        updateQuickslot();

                                        if (soultype == 0) {

                                            ch.HP += healing;

                                            ch.sprite.emitter().burst(Speck.factory(Speck.STEAM), 5);

                                            ch.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", healing);
                                        } else if (soultype == 1){
                                            healing = Math.round(1.6f * (healing + wastedHealing));
                                            Buff.affect(ch, Barrier.class).setShield(healing);
                                            ch.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", healing);
                                        } else if (soultype == 2){
                                            healing *= 2;
                                            Dungeon.hero.mana -= str;
                                            //searching for neighbours
                                            ArrayList<Minion> neighbours = new ArrayList<>();

                                            for(int i : PathFinder.NEIGHBOURS8){
                                                Char uh = Actor.findChar(shot.collisionPos + i);
                                                if (uh instanceof Minion) neighbours.add((Minion) uh);
                                            }

                                            if (neighbours.size() == 0){
                                                ch.HP += healing;

                                                ch.sprite.emitter().burst(Speck.factory(Speck.STEAM), 10);

                                                ch.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", healing);
                                            } else {
                                                int healingForEveryMinion = healing / neighbours.size();
                                                ch.HP += healingForEveryMinion;

                                                ch.sprite.emitter().burst(Speck.factory(Speck.STEAM), 10);

                                                ch.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", healingForEveryMinion);
                                                for (Minion minion : neighbours){
                                                    minion.HP += healingForEveryMinion;

                                                    minion.sprite.emitter().burst(Speck.factory(Speck.STEAM), 10);

                                                    minion.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", healingForEveryMinion);
                                                }
                                            }
                                        } else if (soultype == 3){
                                            ((Minion) ch).adjustDamage(str, str*2);
                                            ((Minion) ch).setMaxHP(Math.max(1, ch.HT - str*2));
                                            ((Minion) ch).adjustDR(-str, -str*2);
                                            ch.sprite.showStatus(CharSprite.NEGATIVE, "-%dHP", str*2);
                                            ch.sprite.showStatus(CharSprite.POSITIVE, "+%1$d/+%2$dDMG", str, str*2);
                                            ch.sprite.showStatus(CharSprite.NEGATIVE, "-%1$d/-%2$dDR", str, str*2);
                                        } else if (soultype == 4){
                                            ((Minion) ch).adjustDamage(-str, -str*2);
                                            ((Minion) ch).setMaxHP(Math.max(1, ch.HT + str*3));
                                            ((Minion) ch).adjustDR(str, str*2);
                                            ch.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", str*3);
                                            ch.sprite.showStatus(CharSprite.NEGATIVE, "-%1$d/-%2$dDMG", str, str*2);
                                            ch.sprite.showStatus(CharSprite.POSITIVE, "+%1$d/+%2$dDR", str, str*2);
                                        }

                                        if (level() < 10 && soultype < 3){
                                            if (totalHealing < healingTable[level()]){
                                                totalHealing += healing;
                                                if (totalHealing >= healingTable[level()]){
                                                    upgrade();
                                                    chargeCap = Math.min(50 + level()*25, 500);
                                                    GLog.highlight(Messages.get(LoveHolder.class, "upgrade"));
                                                    totalHealing = 0;
                                                }
                                            }
                                        }
                                    }
                                    else if (ch instanceof WardingWraith){
                                        if (str >= 12){
                                            ((WardingWraith) ch).enraged = false;
                                            ch.alignment = Char.Alignment.ALLY;
                                            ch.HP = ch.HT;
                                            charge += 12;
                                            charge = Math.min(0, charge - 50);
                                            Buff.prolong(ch, RoseWraith.Timer.class, Integer.MAX_VALUE);
                                        }
                                    }
                                    curUser.spendAndNext( 1f );
                                }
                            });
                            Sample.INSTANCE.play( Assets.Sounds.ZAP );

                } else {
                    GLog.i(Messages.get(LoveHolder.class, "not_enough"));
                }
            }
        }

        @Override
        public String prompt() {
             return Messages.get(Wand.class, "prompt");
        }
    };

    //offensive option
    protected CellSelector.Listener zapper2 = new CellSelector.Listener() {
        @Override
        public void onSelect(Integer target) {
            if (target != null){
                final Ballistica shot = new Ballistica( curUser.pos, target, Ballistica.MAGIC_BOLT);
                int cell = shot.collisionPos;

                if (target == curUser.pos || cell == curUser.pos) {
                    GLog.i( Messages.get(Wand.class, "self_target") );
                    return;
                }

                curUser.sprite.zap(cell);

                //attempts to target the cell aimed at if something is there, otherwise targets the collision pos.
                if (Actor.findChar(target) != null)
                    QuickSlotButton.target(Actor.findChar(target));
                else
                    QuickSlotButton.target(Actor.findChar(cell));

                LoveHolder artifact = null;
                if (curUser.belongings.artifact instanceof LoveHolder) artifact = (LoveHolder) curUser.belongings.artifact;
                else if (curUser.belongings.misc instanceof LoveHolder) artifact = (LoveHolder) curUser.belongings.misc;

                if (artifact == null){
                    GLog.i( Messages.get(Artifact.class, "need_to_equip") );
                    return;
                } else {
                    final int str = artifact.str;
                    if (artifact.charge >= str){
                        artifact.charge -= str;
                        updateQuickslot();
                        curUser.busy();
                        Invisibility.dispel();
                        curUser.sprite.zap(shot.collisionPos);
                        Sample.INSTANCE.play(Assets.Sounds.BURNING);
                        MagicMissile.boltFromChar(curUser.sprite.parent,
                                MagicMissile.BEACON,
                                curUser.sprite,
                                shot.collisionPos,
                                new Callback() {
                                    @Override
                                    public void call() {
                                        Char ch = Actor.findChar(shot.collisionPos);

                                        if (ch != null){
                                            //min damage: 6, +0.33 per hero level, +15% addictive per str
                                            //max damage: 12, +0.75 per hero level, +15% addicitive per str
                                            int damageRoll = Random.NormalIntRange(
                                                    (int) ((6 + curUser.lvl / 3) * (1 + (str - 1) * 0.15f)),
                                                    (int) ((12 + curUser.lvl * 0.75f) * (1 + (str - 1) * 0.15f)));
                                            ch.damage(damageRoll, this);
                                            Sample.INSTANCE.play(Assets.Sounds.BLAST);

                                            ch.sprite.burst(0xFFFFFFFF, str*3);
                                            ch.sprite.emitter().burst(WhiteParticle.UP, 8);

                                        }
                                        curUser.spendAndNext(Actor.TICK);
                                    }
                                });
                    } else {
                        GLog.i(Messages.get(LoveHolder.class, "not_enough"));
                    }
                }
            }
        }

        @Override
        public String prompt() {
            return Messages.get(Wand.class, "prompt");
        }
    };

    public static int getHealingFromStrength(int str) {
        return str * 2;
    }

    @Override
	public Item upgrade() {
		if (level() >= 6)
			image = ItemSpriteSheet.ARTIFACT_LOVE3;
		else if (level() >= 2)
			image = ItemSpriteSheet.ARTIFACT_LOVE2;
        chargeCap = Math.min(chargeCap + 25, 500);
		return super.upgrade();
	}

    @Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
        totalHealing = bundle.getInt(HEALING);
        str = bundle.getInt(STRENGTH);
		if (level() >= 7) image = ItemSpriteSheet.ARTIFACT_LOVE3;
		else if (level() >= 3) image = ItemSpriteSheet.ARTIFACT_LOVE2;
	}

	//LOVE holder doesn't do something in passive way
	@Override
	protected ArtifactBuff passiveBuff() {
		return new lul();
	}

	@Override
	public String desc() {
		String desc = Messages.get(this, "desc", str, healingTable[level()] - totalHealing);

		if (isEquipped (Dungeon.hero)){
			desc += "\n\n";
			if (cursed)
				desc += Messages.get(this, "desc_cursed");
			else if (level() >= 3 && level() < 6){
                desc += Messages.get(this, "desc_2");
            } else if (level() >= 6){
                desc += Messages.get(this, "desc_3");
            }

			if (Dungeon.hero.subClass == HeroSubClass.OCCULTIST){
			    desc += Messages.get(this, "dmg", (int) ((6 + Dungeon.hero.lvl / 3) * (1 + (str - 1) * 0.15f)),
                        (int) ((12 + Dungeon.hero.lvl * 0.75f) * (1 + (str - 1) * 0.15f)), str*2, Math.round(str*1.5));
            }
		}

		return desc;
	}

	public class lul extends ArtifactBuff {
        public int gainCharge(int amount){
            if (charge < chargeCap){
                charge += amount;
                updateQuickslot();
                if (charge >= chargeCap){
                    int overcharge = chargeCap - charge;
                    charge = chargeCap;
                    GLog.positive( Messages.get(LoveHolder.class, "full_charge") );
                    updateQuickslot();
                    return overcharge;
                }
                updateQuickslot();
                return 0;
            }
            return chargeCap;
        }

        public float getHealingRatio(){
            return (float)(charge / chargeCap);
        }
	}

}
