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

package com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.staffs;

import com.trashboxbobylev.summoningpixeldungeon.*;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Invisibility;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.Chicken;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.Minion;
import com.trashboxbobylev.summoningpixeldungeon.effects.Beam;
import com.trashboxbobylev.summoningpixeldungeon.items.armor.ConjurerArmor;
import com.trashboxbobylev.summoningpixeldungeon.items.rings.RingOfAttunement;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.Wand;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.scenes.CellSelector;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.tiles.DungeonTilemap;
import com.trashboxbobylev.summoningpixeldungeon.ui.QuickSlotButton;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class StationaryStaff extends Staff {
    //because of precise strategy, I need to rewrite some of Staff methods to allow exact placing
    {
        defaultAction = AC_SUMMON;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        actions.remove(AC_ZAP);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);

        if (action.equals(AC_SUMMON)) {

            curUser = hero;
            try {
                curItem = this;
                GameScene.selectCell(placer);
            } catch (Exception e) {
                ShatteredPixelDungeon.reportException(e);
                GLog.warning(Messages.get(Wand.class, "fizzles"));
            }

        }
    }

    protected static CellSelector.Listener placer = new  CellSelector.Listener() {

        @Override
        public void onSelect( Integer target ) {

            if (target != null) {

                final Staff staff = (Staff)curItem;

                //searching for available space
                ArrayList<Integer> spawnPoints = new ArrayList<Integer>();

                for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                    int p = curUser.pos + PathFinder.NEIGHBOURS8[i];
                    if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
                        spawnPoints.add( p );
                    }
                }

                if (spawnPoints.size() == 0){
                    curUser.sprite.zap(0);
                    GLog.i( Messages.get(Staff.class, "no_space") );
                    return;
                } else {
                    for (int pos:
                         spawnPoints) {
                        if (pos == target) break;
                        GLog.i( Messages.get(StationaryStaff.class, "not_nearby") );
                        return;
                    }
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

                    if (curItem.cursed){
                        GLog.negative(Messages.get(Staff.class, "curse_discover", staff.name()));
                        curUser.damage(staff.minionDamageRoll(curUser), staff);
                        curUser.spendAndNext(1f);
                    } else {
                        Sample.INSTANCE.play( Assets.SND_ZAP );
                        Char ch = Actor.findChar(target);
                        if (ch != null){
                            if (ch instanceof Minion){
                                ch.die( curUser );
                            }
                            else {
                                try {
                                    ((StationaryStaff)staff).summon(curUser, target);
                                } catch (Exception e) {
                                    ShatteredPixelDungeon.reportException(e);
                                    GLog.warning( Messages.get(Wand.class, "fizzles") );
                                }
                            }
                        }
                        staff.wandUsed(false);
                    }
                    curItem.cursedKnown = true;
                }

            }
        }

        @Override
        public String prompt() {
            return Messages.get(Wand.class, "prompt");
        }
    };

    //summoning logic
    public void summon(Hero owner, int target) throws InstantiationException, IllegalAccessException {

        //searching for available space
        //did it before summoning

        //checking attunement
        if (requiredAttunement() > owner.attunement() || (requiredAttunement() + owner.usedAttunement > owner.attunement())){
            owner.sprite.zap(0);
            GLog.warning( Messages.get(Staff.class, "too_low_attunement") );
            return;
        }

        int strength = 1;
        if (STRReq() > owner.STR())  strength += STRReq(level()) - owner.STR();

        //if anything is met, spawn minion
        //if hero do not have enough strength, summoning might fail
        if (strength == 1 || Random.Float() < 1 / (float) (strength * 2)) {
            Minion minion = minionType.newInstance();
            GameScene.add(minion);
            ScrollOfTeleportation.appear(minion, target);
            owner.usedAttunement += minion.attunement;
            minion.setDamage(
                    Math.round(minionMin(level())* RingOfAttunement.damageMultiplier(owner)),
                    Math.round(minionMax(level()) * RingOfAttunement.damageMultiplier(owner)));
            Statistics.summonedMinions++;
            Badges.validateConjurerUnlock();
            minion.strength = STRReq();
            this.customizeMinion(minion);

            //if we have upgraded robe, increase hp
            float robeBonus = 1f;
            if (curUser.belongings.armor instanceof ConjurerArmor && curUser.belongings.armor.level() > 0) {
                robeBonus = 1f + curUser.belongings.armor.level() * 0.1f;
            }
            minion.setMaxHP((int) (hp(level()) * robeBonus));
        } else GLog.warning( Messages.get(Wand.class, "fizzles") );
        wandUsed(false);
    }
}
