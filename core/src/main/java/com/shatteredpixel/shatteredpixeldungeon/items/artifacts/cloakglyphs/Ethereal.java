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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts.cloakglyphs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;

public class Ethereal extends CloakGlyph{
    private static ItemSprite.Glowing AIRY = new ItemSprite.Glowing( 0xe6e6e6);

    @Override
    public void proc(CloakOfShadows cloak, Char defender, int charges) {

    }

    @Override
    public float chargeModifier(CloakOfShadows cloak, Char defender) {
        if (Dungeon.level.map[defender.pos] == Terrain.CHASM ||
            Dungeon.level.pit[defender.pos])
            return 0.75f;
        return super.chargeModifier(cloak, defender)*1.33f;
    }

    @Override
    public void onCloaking(CloakOfShadows cloak, Char defender) {
        defender.flying = true;
        Roots.detach( defender, Roots.class );
    }

    @Override
    public void onDetaching(CloakOfShadows cloak, Char defender) {
        super.onDetaching(cloak, defender);
        defender.flying = false;
        Dungeon.level.occupyCell(defender );
    }

    @Override
    public void onUncloaking(CloakOfShadows cloak, Char defender) {
        onDetaching(cloak, defender);
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return AIRY;
    }
}
