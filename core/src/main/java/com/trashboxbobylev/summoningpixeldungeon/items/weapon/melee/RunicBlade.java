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

package com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee;

import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.FlavourBuff;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Invisibility;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.MagicImmune;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.summoningpixeldungeon.effects.Splash;
import com.trashboxbobylev.summoningpixeldungeon.items.Item;
import com.trashboxbobylev.summoningpixeldungeon.items.stones.Runestone;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.CursedWand;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.Wand;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.Weapon;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.scenes.CellSelector;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
import com.trashboxbobylev.summoningpixeldungeon.sprites.MissileSprite;
import com.trashboxbobylev.summoningpixeldungeon.ui.QuickSlotButton;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class RunicBlade extends MeleeWeapon {

    private static final String AC_ZAP = "ZAP";

    {
		image = ItemSpriteSheet.RUNIC_BLADE;

		tier = 4;

		defaultAction = AC_ZAP;
		usesTargeting = true;
	}

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        if (charged) {
            actions.add( AC_ZAP );
        }

        return actions;
    }

    @Override
    public void execute( Hero hero, String action ) {

        super.execute( hero, action );

        if (action.equals( AC_ZAP )) {

            curUser = hero;
            curItem = this;
            GameScene.selectCell( zapper );

        }
    }

	public boolean charged;

	//Essentially it's a tier 4 weapon, with tier 3 base max damage, and tier 5 scaling.
	//equal to tier 4 in damage at +5

	@Override
	public int max(int lvl) {
        int i = 4 * (tier) +                    //16 base, down from 25
                Math.round(lvl * (tier + 1)); //+6 per level, up from +5
        if (!charged) i = super.max(lvl);
        return i;
	}

    public boolean tryToZap(Hero owner){

        if (owner.buff(MagicImmune.class) != null){
            GLog.warning( Messages.get(this, "no_magic") );
            return false;
        }

        if (charged){
            return true;
        } else {
            GLog.warning(Messages.get(this, "fizzles"));
            return false;
        }
    }

    @Override
    public String info() {
	    String info = super.info();
	    if (!charged){
	        RunicCooldown cooldown = Dungeon.hero.buff(RunicCooldown.class);
	        if (cooldown != null){
                info += "\n\n" + Messages.get(this, "cooldown", cooldown.cooldown()+1f);
            }
        }
        return info;
    }

    protected static CellSelector.Listener zapper = new  CellSelector.Listener() {

        @Override
        public void onSelect( Integer target ) {

            if (target != null) {

                //FIXME this safety check shouldn't be necessary
                //it would be better to eliminate the curItem static variable.
                final RunicBlade curBlade;
                if (curItem instanceof RunicBlade) {
                    curBlade = (RunicBlade) curItem;
                } else {
                    return;
                }

                final Ballistica shot = new Ballistica( curUser.pos, target, Ballistica.PROJECTILE);
                final int cell = shot.collisionPos;

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

                if (curBlade.tryToZap(curUser)) {

                    curUser.busy();
                    Invisibility.dispel();

                    if (curBlade.cursed){
                        if (!curBlade.cursedKnown){
                            GLog.negative(Messages.get(Wand.class, "curse_discover", curBlade.name()));
                        }
                    } else {
                        ((MissileSprite) curUser.sprite.parent.recycle(MissileSprite.class)).
                                reset(curUser.sprite,
                                        target,
                                        new RunicMissile(),
                                        new Callback() {
                                            @Override
                                            public void call() {
                                                Char enemy = Actor.findChar( cell );
                                                if (enemy != null && enemy != curUser){
                                                    int dmg = curBlade.damageRoll(curUser);
                                                    enemy.damage(dmg, curBlade);
                                                    curBlade.proc(curUser, enemy, dmg);
                                                } else {
                                                    Dungeon.level.pressCell(cell);
                                                }
                                                Splash.at(cell, 0x38c3c3, 15);
                                                curBlade.charged = false;
                                                Buff.affect(curUser, RunicCooldown.class, 3);
                                                curUser.spendAndNext(curBlade.speedFactor(curUser));
                                            }
                                        });
                    }
                    curBlade.cursedKnown = true;

                }

            }
        }

        @Override
        public String prompt() {
            return Messages.get(RunicBlade.class, "prompt");
        }
    };

	public class RunicCooldown extends FlavourBuff {
        @Override
        public void detach() {
            charged = true;
            super.detach();
        }
    }

	public static class RunicMissile extends Item {
        {
            image = ItemSpriteSheet.RUNIC_SHOT;
        }
    }
}
