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

package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.GameMode.HELL;

public class Food extends Item {

	public static final float TIME_TO_EAT	= 3f;
	
	public static final String AC_EAT	= "EAT";
	
	public float energy = 550;
	public int regen = 0;
	public String message = Messages.get(this, "eat_msg");
	
	{
		stackable = true;
		image = ItemSpriteSheet.RATION;
		defaultAction = AC_EAT;

		bones = true;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_EAT );
		return actions;
	}

	protected float eatingTime(){
		if (Dungeon.hero.hasTalent(Talent.BREAD_AND_CIRCUSES)){
			return TIME_TO_EAT - 2;
		} else {
			return TIME_TO_EAT;
		}
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_EAT )) {
			
			detach( hero.belongings.backpack );
//			InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
//			ShatteredPixelDungeon.switchScene(InterlevelScene.class);
			
			satisfy(hero);
			GLog.i( message );


			foodProc( hero );
			Talent.onFoodEaten(hero, energy, this);
			
			hero.sprite.operate( hero.pos );
			hero.busy();
			SpellSprite.show( hero, SpellSprite.FOOD );
			Sample.INSTANCE.play( Assets.Sounds.EAT );
			
			hero.spend( eatingTime() );
			
			Statistics.foodEaten++;
			Badges.validateFoodEaten();
			
		}
	}
	
	protected void satisfy( Hero hero ){
		Buff.affect(hero, Hunger.class).satisfy( energy );
		if (regen > 0 && Dungeon.mode != HELL) Buff.affect(hero, FoodRegen.class).fullHP = regen;
		if (regen < 0) Buff.affect(hero, FoodDebuff.class).fullHP = -regen;
	}
	
	public static void foodProc( Hero hero ){
		switch (hero.heroClass) {
			case MAGE:
				//1 charge
				Buff.affect( hero, Recharging.class, 4f );
				ScrollOfRecharging.charge( hero );
				break;
			case CONJURER:
				if (hero.HP < hero.HT && Dungeon.mode != HELL) {
					hero.HP = Math.min( hero.HP + 5, hero.HT );
					hero.sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
				}
				break;
		}
	}

	@Override
	public Item random() {
		Item item = super.random();
		if (Dungeon.hero.pointsInTalent(Talent.BREAD_AND_CIRCUSES) > 1){
			if (Random.Int(2) == 0)
				item.quantity(2);
		}
		return item;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return !Dungeon.isChallenged(Conducts.Conduct.UNKNOWN);
	}
	
	@Override
	public int value() {
		return 20 * quantity;
	}

    @Override
    public String desc() {
        String desc = super.desc();
        if (regen != 0) {
        	desc += Messages.get(Food.class, "stats", Math.round(energy / 11), regen);
        } else {
			desc += Messages.get(Food.class, "stats_regular", Math.round(energy / 11));
        }

        return desc;
    }
}
