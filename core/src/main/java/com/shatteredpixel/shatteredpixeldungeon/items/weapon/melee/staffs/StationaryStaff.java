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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.staffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class StationaryStaff extends Staff {
    //because of precise strategy, I need to rewrite some of Staff methods to allow exact placing
    {
        defaultAction = AC_SUMMON;
        chargeTurns = 550;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_SUMMON)) {

            curUser = hero;
            curItem = this;
            GameScene.selectCell(placer);

        }
    }

    protected static CellSelector.Listener placer = new  CellSelector.Listener() {

        @Override
        public void onSelect( Integer target ) {

            if (target != null) {

                final StationaryStaff staff = (StationaryStaff)curItem;

                if (Actor.findChar( target ) != null || !Dungeon.level.passable[target] || !Dungeon.level.heroFOV[target]){
                    curUser.sprite.zap(0);
                    GLog.i( Messages.get(StationaryStaff.class, "no_space") );
                    return;
                }

                if (target == curUser.pos) {
                    GLog.i( Messages.get(Wand.class, "self_target") );
                    return;
                }

                curUser.sprite.zap(target);

                //okay, this is incredible mess
                //basically it's copy-paste from various wand classes
                //ZAP from summon staff doesn't do damage and serves only as targetting tool for your minions

                if (staff.tryToZap(curUser, target)){
                    curUser.busy();
                    Invisibility.dispel();
                        try {
                            Sample.INSTANCE.play( Assets.Sounds.ZAP );
                            if (staff.summon(curUser, target))
                                staff.wandUsed(false);
                        } catch (Exception e) {
                            ShatteredPixelDungeon.reportException(e);
                            GLog.warning( Messages.get(Wand.class, "fizzles") );
                        }
                    curItem.cursedKnown = true;
                }
            }
        }

        @Override
        public String prompt() {
            return Messages.get(StationaryStaff.class, "prompt");
        }
    };

    //summoning logic
    public boolean summon(Hero owner, int target) throws InstantiationException, IllegalAccessException {

        //searching for available space
        //did it before summoning

        //checking attunement
        if (requiredAttunement() > owner.maxAttunement() || (requiredAttunement() + owner.usedAttunement() > owner.maxAttunement())){
            owner.sprite.zap(0);
            GLog.warning( Messages.get(Staff.class, "too_low_attunement") );
            return false;
        }

        int strength = 1;
        if (STRReq() > owner.STR())  strength += STRReq(level()) - owner.STR();

        //if anything is met, spawn minion
        //if hero do not have enough strength, summoning might fail
        if (strength == 1 || Random.Float() < 1 / (float) (strength * 2)) {
            Minion minion = minionType.newInstance();
            GameScene.add(minion);
            ScrollOfTeleportation.appear(minion, target);
            minion.setDamage(
                    Math.round(minionMin(level())),
                    Math.round(minionMax(level())));
            Statistics.summonedMinions++;
            minion.strength = STRReq();
            minion.attunement = requiredAttunement();
            minion.lvl = level();
            this.customizeMinion(minion);
            minion.setMaxHP((int) (hp(level())));
        } else GLog.warning( Messages.get(Wand.class, "fizzles") );
        return true;
    }
}
