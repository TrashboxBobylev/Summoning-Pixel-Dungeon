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

package com.trashboxbobylev.summoningpixeldungeon.actors.buffs;

import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.summoningpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

public class Shrink extends Buff {
	
	public int distance = 2;

	{
		type = buffType.POSITIVE;
		announced = true;
	}


	
	@Override
	public int icon() {
		return BuffIndicator.MOMENTUM;
	}
	
	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

    @Override
    public void tintIcon(Image icon) {
        icon.tint(0.5f, 0, 1, 0.75f);
    }

    @Override
    public void fx(boolean on) {
        if (on) target.sprite.add(CharSprite.State.SHRUNK);
        else target.sprite.remove(CharSprite.State.SHRUNK);
    }

	@Override
	public String desc() {
		return Messages.get(this, "desc");
	}
}
