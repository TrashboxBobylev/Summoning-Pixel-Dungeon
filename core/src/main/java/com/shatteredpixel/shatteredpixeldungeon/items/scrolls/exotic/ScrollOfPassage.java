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

package com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TimeFreezing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;

public class ScrollOfPassage extends ExoticScroll {
	
	{
		icon = ItemSpriteSheet.Icons.SCROLL_PASSAGE;
	}
	
	@Override
	public void doRead() {
		
		setKnown();
		
		if (Dungeon.bossLevel()) {
			
			GLog.warning( Messages.get(ScrollOfTeleportation.class, "no_tele") );
			return;
			
		}

		TimeFreezing timeFreeze = Dungeon.hero.buff( TimeFreezing.class );
		if (timeFreeze != null) timeFreeze.detach();
		
		InterlevelScene.mode = InterlevelScene.Mode.RETURN;
		InterlevelScene.returnDepth = Math.max(1, (Dungeon.depth - 1 - (Dungeon.depth-2)%5 * 5 / Dungeon.chapterSize()));
		InterlevelScene.returnPos = -1;
		Game.switchScene( InterlevelScene.class );
	}
}
