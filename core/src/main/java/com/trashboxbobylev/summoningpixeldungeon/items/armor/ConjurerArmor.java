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

package com.trashboxbobylev.summoningpixeldungeon.items.armor;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.*;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.Mob;
import com.trashboxbobylev.summoningpixeldungeon.effects.particles.ElmoParticle;
import com.trashboxbobylev.summoningpixeldungeon.items.Item;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.sprites.HeroSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.trashboxbobylev.summoningpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class ConjurerArmor extends ClassArmor {
	
	{
		image = ItemSpriteSheet.ARMOR_CONJURER;
	}

    private static final String AC_IMBUE = "IMBUE";

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        if (!cursed) actions.add( AC_IMBUE );
        actions.remove(AC_UNEQUIP);
        actions.remove(AC_DROP);
        actions.remove(AC_THROW);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_IMBUE)){
            curUser = hero;
            GameScene.selectItem( itemSelector, WndBag.Mode.ARMOR, Messages.get(this, "prompt") );
        }
    }

    private final WndBag.Listener itemSelector = new WndBag.Listener() {
        @Override
        public void onSelect( Item item ) {
            if (item != null && item.isIdentified()) {
                upgrade( (Armor)item );
            }
        }
    };

    private void upgrade(Armor armor){
        GLog.warning( Messages.get(ConjurerArmor.class, "upgraded", armor.name()) );

        //syncs the level of the two items.
        int targetLevel = Math.max(this.level() - (curseInfusionBonus ? 1 : 0), armor.level());

        //if the robe's level is being overridden by the armor, preserve 1 upgrade
        if (armor.level() >= this.level() && this.level() > (curseInfusionBonus ? 1 : 0)) targetLevel++;

        level(targetLevel);
        updateQuickslot();
        this.armorTier = armor.tier;
        this.augment = armor.augment;
        this.inscribe( armor.glyph );
        this.cursed = armor.cursed;
        this.curseInfusionBonus = armor.curseInfusionBonus;
        curUser.belongings.armor = this;
        ((HeroSprite)curUser.sprite).updateArmor();
        this.activate(curUser);
        armor.detach( curUser.belongings.backpack );
        curUser.sprite.operate( curUser.pos );
        curUser.spend( 2f );
        curUser.busy();
        GameScene.flash(0xFFFFFF);
        Sample.INSTANCE.play( Assets.SND_EVOKE );
    }

    @Override
	public void doSpecial() {
		
		for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
			if (Dungeon.level.heroFOV[mob.pos]
				&& mob.alignment != Char.Alignment.ALLY) {
				Buff.prolong( mob, Paralysis.class, 7 );
				Buff.prolong( mob, Charm.class, 8 );
			}
		}

		curUser.HP -= (curUser.HT / 2);
		
		curUser.spend( Actor.TICK );
		curUser.sprite.operate( curUser.pos );
		curUser.busy();
		
		curUser.sprite.centerEmitter().start( ElmoParticle.FACTORY, 0.15f, 4 );
		Sample.INSTANCE.play( Assets.SND_LULLABY );
	}

    @Override
    public int STRReq(int lvl) {
        lvl = Math.max(0, lvl);

        //strength req decreases at +1,+3,+6,+10,etc.
        return (7 + Math.round(armorTier * 2)) - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
    }

    @Override
    public int DRMax(int lvl){
        //only 80% as effective
        int max = (int) ((armorTier * (2 + lvl) + augment.defenseFactor(lvl))*0.8f);
        if (lvl > max){
            return ((lvl - max)+1)/2;
        } else {
            return max;
        }
    }

}