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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class BadgeOfBravery extends Artifact {
    {
        image = ItemSpriteSheet.ARTIFACT_BADGE;
        levelCap = 4;
        setArtifactClass(ArtifactClass.OFFENSE);
    }

    public static final String AC_REPAIR = "REPAIR";

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        if (isEquipped( hero ) && level() < levelCap && !cursed)
            actions.add(AC_REPAIR);
        return actions;
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new braveryBuff();
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if ( isEquipped( Dungeon.hero ) ){
            if (!cursed) {
                if (level() < levelCap)
                    desc += "\n\n" + Messages.get(this, "desc_worn", (level()+1)*2, (level()+1)*5, level()+2);
                else
                    desc += "\n\n" + Messages.get(this, "desc_full", (level()+1)*3, (level()+1)*5);
            } else {
                desc += "\n\n" + Messages.get(this, "desc_cursed");
            }
        }

        return desc;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_REPAIR)){
            curUser = hero;
            curItem = this;
            GameScene.selectItem(itemSelector);
        }
    }

    public class braveryBuff extends ArtifactBuff {

    }

    protected static WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

        @Override
        public String textPrompt() {
            return Messages.get(BadgeOfBravery.class, "prompt", curItem.level()+2);
        }

        @Override
        public Class<?extends Bag> preferredBag(){
            return Belongings.Backpack.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            boolean condition = item instanceof MeleeWeapon && ((MeleeWeapon) item).tier == curItem.level() + 2;
            if (item.isEquipped(curUser) && item.visiblyCursed()) condition = false;
            return condition;

        }

        @Override
        public void onSelect( Item item ) {
            if (item instanceof Weapon) {
                Hero hero = Dungeon.hero;
                hero.sprite.operate( hero.pos );
                hero.busy();
                Sample.INSTANCE.play( Assets.Sounds.EVOKE );
                Item.evoke(hero);
                hero.spend( Food.TIME_TO_EAT );

                if (++((BadgeOfBravery) curItem).exp == 2){
                    curItem.upgrade();
                    ((BadgeOfBravery) curItem).exp = 0;
                    GLog.positive( Messages.get(BadgeOfBravery.class, "levelup") );
                } else {
                    GLog.i( Messages.get(BadgeOfBravery.class, "feed") );
                }
                if (item.isEquipped(hero))
                    ((Weapon) item).doUnequip(hero, false);
                item.detach(hero.belongings.backpack);
            }
        }
    };
}
