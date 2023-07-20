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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class CloakGlyph implements Bundlable {
    private static final Class<?>[] glyphs = new Class<?>[]{};

    public abstract int proc(CloakOfShadows cloak, Char defender, int charges );

    public String name() {
        return name( Messages.get(this, "glyph") );
    }

    public static float efficiency(){
        switch (Dungeon.hero.pointsInTalent(Talent.ARCANE_CLOAK)){
            case 1: return 0.5f;
            case 2: return 0.66f;
            case 3: return 1f;
        }
        return 0f;
    }

    public String name( String armorName ) {
        return Messages.get(this, "name", armorName);
    }

    public String desc() {
        return Messages.get(this, "desc");
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
    }

    @Override
    public void storeInBundle( Bundle bundle ) {
    }

    public abstract ItemSprite.Glowing glowing();

    @SuppressWarnings("unchecked")
    public static CloakGlyph random(Class<? extends CloakGlyph> ... toIgnore ) {
        float[] chances = new float[]{1, 0};
        if (Dungeon.hero.pointsInTalent(Talent.ARCANE_CLOAK) > 1) chances = new float[]{1, 1};
        switch(Random.chances(chances)){
            case 0: default:
                return randomCommon( toIgnore );
            case 1:
                return randomRare( toIgnore );
        }
    }

    @SuppressWarnings("unchecked")
    public static CloakGlyph randomCommon(Class<? extends CloakGlyph> ... toIgnore ) {
        ArrayList<Class<?>> glyphsToSample = new ArrayList<>(Arrays.asList(glyphs));
        glyphsToSample.removeAll(Arrays.asList(toIgnore));
        if (glyphsToSample.isEmpty()) {
            return random();
        } else {
            return (CloakGlyph) Reflection.newInstance(Random.element(glyphsToSample));
        }
    }

    @SuppressWarnings("unchecked")
    public static CloakGlyph randomRare(Class<? extends CloakGlyph> ... toIgnore ) {
        ArrayList<Class<?>> glyphsToSample = new ArrayList<>(Arrays.asList(glyphs));
        glyphsToSample.removeAll(Arrays.asList(toIgnore));
        if (glyphsToSample.isEmpty()) {
            return random();
        } else {
            return (CloakGlyph) Reflection.newInstance(Random.element(glyphsToSample));
        }
    }
}
