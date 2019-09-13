/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2019 Evan Debenham
 *  *
 *  * Summoning Pixel Dungeon
 *  * Copyright (C) 2019-2020 TrashboxBobylev
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.trashboxbobylev.summoningpixeldungeon.items.artifacts;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.ShatteredPixelDungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Barrier;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Invisibility;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.Minion;
import com.trashboxbobylev.summoningpixeldungeon.effects.MagicMissile;
import com.trashboxbobylev.summoningpixeldungeon.effects.Speck;
import com.trashboxbobylev.summoningpixeldungeon.items.Item;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.Wand;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.scenes.CellSelector;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
import com.trashboxbobylev.summoningpixeldungeon.ui.QuickSlotButton;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.trashboxbobylev.summoningpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class LoveHolder extends Artifact {

    private int str;
    private int soultype = -1;
    public int totalHealing = 0;

    private int[] healingTable = {
            10, 25, 50, 75, 100, 125, 150, 175, 200, 210
    };

	{
		image = ItemSpriteSheet.ARTIFACT_LOVE1;

		levelCap = 10;
		defaultAction = AC_PRICK;

        charge = 0;
        chargeCap = Math.min(50 + level()*25, 500);
	}

	public static final String AC_PRICK = "PRICK";
    public static final String AC_TUNE = "TUNE";
    public static final String AC_SHIELD = "SHIELD";
    public static final String AC_SPLASH = "SPLASH";
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
        }
		return actions;
	}



	@Override
	public void execute(final Hero hero, String action ) {
		super.execute(hero, action);

		if (action.equals(AC_PRICK) || action.equals(AC_SHIELD) || action.equals(AC_SPLASH)){
		    int type = -1;
		    switch (action){
                case AC_PRICK:
                    type = 0; break;
                case AC_SHIELD:
                    type = 1; break;
                case AC_SPLASH:
                    type = 2; break;
            }
		    prick(Dungeon.hero, type);
		}
        if (action.equals(AC_TUNE)){
            GameScene.show(
                    new WndOptions(Messages.titleCase(Messages.get(this, "name")),
                            Messages.get(this, "how_many"),
                            Messages.get(this, "bit"),
                            Messages.get(this, "few")) {
                        @Override
                        protected void onSelect(int index) {
                                if (index == 0) str++;
                                else str--;
                                GLog.w(Messages.get(LoveHolder.class, "ontune", str));
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
	}

	private void prick(Hero hero, int type){
        if (charge < str || (type == 2 && (charge < str * 2))) {
            GLog.i(Messages.get(LoveHolder.class, "not_enough"));
        } else {
            curUser = hero;
            curItem = this;
            soultype = type;
            GLog.w(Messages.get(this, "onprick"));
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

                if (charge >= str) {
                    charge -= str;
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

                                    if (ch instanceof Minion){
                                        Sample.INSTANCE.play(Assets.SND_DRINK);

                                        int healing = getHealingFromStrength(str);

                                        int wastedHealing = (ch.HP + healing) - ch.HT;
                                        if (wastedHealing > 0){
                                            healing -= wastedHealing;
                                            charge += wastedHealing / 2; //some of unnecessary soul will return
                                        }

                                        if (soultype == 0) {

                                            ch.HP += healing;

                                            ch.sprite.emitter().burst(Speck.factory(Speck.STEAM), 5);

                                            ch.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", healing);
                                        } else if (soultype == 1){
                                            healing *= 1.6f;
                                            Buff.affect(ch, Barrier.class).setShield(healing);
                                            ch.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", healing);
                                        } else if (soultype == 2){
                                            healing *= 2;
                                            charge -= str;
                                            //searching for neighbours
                                            ArrayList<Integer> neighbours = new ArrayList<Integer>();

                                            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                                                int p = ch.pos + PathFinder.NEIGHBOURS8[i];
                                                if (Actor.findChar( p ) instanceof Minion) {
                                                    neighbours.add( p );
                                                }
                                            }

                                            if (neighbours.size() == 0){
                                                ch.HP += healing;

                                                ch.sprite.emitter().burst(Speck.factory(Speck.STEAM), 10);

                                                ch.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", healing);
                                            } else {
                                                ArrayList<Minion> tempChar = new ArrayList<Minion>();
                                                for (Integer pos : neighbours){
                                                    tempChar.add((Minion) Actor.findChar(pos));
                                                }
                                                int healingForEveryMinion = healing / tempChar.size();
                                                ch.HP += healingForEveryMinion;

                                                ch.sprite.emitter().burst(Speck.factory(Speck.STEAM), 10);

                                                ch.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", healingForEveryMinion);
                                                for (Minion minion : tempChar){
                                                    minion.HP += healingForEveryMinion;

                                                    minion.sprite.emitter().burst(Speck.factory(Speck.STEAM), 10);

                                                    minion.sprite.showStatus(CharSprite.POSITIVE, "+%dHP", healingForEveryMinion);
                                                }
                                            }
                                        }

                                        if (level() < 10){
                                            if (totalHealing < healingTable[level()]){
                                                totalHealing += healing;
                                                if (totalHealing >= healingTable[level()]){
                                                    upgrade();
                                                    chargeCap = Math.min(50 + level()*50, 500);
                                                    GLog.h(Messages.get(LoveHolder.class, "upgrade"));
                                                    totalHealing = 0;
                                                }
                                            }
                                        }
                                    }
                                    curUser.spendAndNext( 1f );
                                }
                            });
                            Sample.INSTANCE.play( Assets.SND_ZAP );

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

    public static int getHealingFromStrength(int str) {
        if (str == 1) {
            return 1;
        }
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
			else if (level() >=2 && level() < 6){
                desc += Messages.get(this, "desc_2");
            } else if (level() >= 6){
                desc += Messages.get(this, "desc_3");
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
                    GLog.p( Messages.get(LoveHolder.class, "full_charge") );
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
