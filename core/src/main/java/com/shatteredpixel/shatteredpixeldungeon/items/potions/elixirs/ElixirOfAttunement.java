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

package com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.AlchemicalCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfAttunement;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

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
	public int value() {
		//prices of ingredients
		return quantity * 80;
	}
	
	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs = new Class[]{ScrollOfAttunement.class, AlchemicalCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 12;

            output = ElixirOfAttunement.class;
            outQuantity = 1;
        }

    }
}
