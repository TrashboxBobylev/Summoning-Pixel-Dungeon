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

package com.shatteredpixel.shatteredpixeldungeon.items.powers;

import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

import java.util.ArrayList;

public abstract class Power extends Item {

    public static final String AC_DRINK = "DRINK";
    ArrayList<? extends Class> minionClasses;
    Minion.MinionClass featuredClass = null;

    {
        stackable = true;
        defaultAction = AC_DRINK;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_DRINK);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_DRINK)){
            use();
        }
    }

    protected void use(){
        affectDungeon();
        buffPlayer();
        buffMinions();
        Invisibility.dispel();
        Dungeon.hero.spendAndNext(Actor.TICK);
        detach( Dungeon.hero.belongings.backpack );
    }

    protected boolean isRespectable(Minion minion){
        return minion.minionClass == featuredClass;
    }

    @Override
    public boolean isIdentified() {
        return !Dungeon.isChallenged(Conducts.Conduct.UNKNOWN);
    }

    Class<? extends FlavourBuff> playerBuff;
    float playerBuffDuration;
    protected void buffPlayer(){
        Buff.affect(Dungeon.hero, playerBuff, playerBuffDuration);
    }

    protected abstract void affectDungeon();

    protected void buffMinions(){
        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (mob instanceof Minion && Dungeon.hero.fieldOfView[mob.pos]){
                affectMinion((Minion) mob);
            }
        }
    }

    Class<? extends FlavourBuff> basicBuff;
    Class<? extends FlavourBuff> classBuff;
    int basicBuffDuration;
    int classBuffDuration;

    protected void affectMinion(Minion minion){
        if (isRespectable(minion)){
                Buff.affect(minion, classBuff, classBuffDuration);
        } else Buff.affect(minion, basicBuff, basicBuffDuration);
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public int value() {
        return 200 * quantity;
    }

    @Override
    public String info() {
        String info = desc();
        info += "\n\n" + Messages.get(this, "class_minion_buff");
        info += "\n\n" + Messages.get(Power.class, "class_members");
        info += "\n\n" + Messages.get(this, "members");
        info += "\n\n" + Messages.get(this, "other_minion_buff");
        return info;
    }
}
