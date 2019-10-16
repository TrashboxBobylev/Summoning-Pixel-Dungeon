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

package com.trashboxbobylev.summoningpixeldungeon.items.potions.elixirs;

import com.trashboxbobylev.summoningpixeldungeon.Badges;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.AlchemicalCatalyst;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfStrength;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfAttunement;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
import com.trashboxbobylev.summoningpixeldungeon.ui.BuffIndicator;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class ElixirOfAttunement extends Elixir {

	{
		image = ItemSpriteSheet.ELIXIR_ATTUNEMENT;
	}
	
	@Override
	public void apply( Hero hero ) {
		setKnown();
		
		hero.attunement++;

		
		hero.updateHT( true );
		hero.sprite.showStatus( CharSprite.POSITIVE, Messages.get(this, "msg_1" ));
		GLog.positive( Messages.get(this, "msg_2") );

		Badges.validateStrengthAttained();
	}
	
	public String desc() {
		return Messages.get(this, "desc");
	}
	
	@Override
	public int price() {
		//prices of ingredients
		return quantity * (50 + 40);
	}
	
	public static class Recipe extends com.trashboxbobylev.summoningpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs = new Class[]{ScrollOfAttunement.class, AlchemicalCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 12;

            output = ElixirOfAttunement.class;
            outQuantity = 1;
        }

    }
}
