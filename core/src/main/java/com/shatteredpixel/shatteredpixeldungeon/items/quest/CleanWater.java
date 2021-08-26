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

package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class CleanWater extends Item {

    private static final String AC_DRINK = "DRINK";

    {
        image = ItemSpriteSheet.CLEAN_WATER;
		bones = true;
		stackable = true;
	}

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.add( AC_DRINK );
        return actions;
    }

    @Override
    public boolean isIdentified() {
        return !Dungeon.isChallenged(Conducts.Conduct.UNKNOWN);
    }

    @Override
    public void execute( final Hero hero, String action ) {

        super.execute( hero, action );

        if (action.equals( AC_DRINK )) {

                drink( hero );
        }
    }

    protected void drink( Hero hero ) {

        detach( hero.belongings.backpack );

        hero.spend( 1f );
        hero.busy();
        apply( hero );

        Sample.INSTANCE.play( Assets.Sounds.DRINK );

        hero.sprite.operate( hero.pos );
    }

	public void apply( Hero hero ) {


		if (Dungeon.mode == Dungeon.GameMode.HELL){
		    hero.HP = Math.min(hero.HT, hero.HP + hero.HT / 2);
        }
		else {
            hero.HP = hero.HT;
        }
        hero.sprite.emitter().start( Speck.factory( Speck.HEALING ), 0.4f, 4 );
		cure( hero );
		GLog.positive( Messages.get(PotionOfHealing.class, "heal") );
	}
	
	public static void cure( Char ch ) {
		Buff.detach( ch, Poison.class );
		Buff.detach( ch, Cripple.class );
		Buff.detach( ch, Weakness.class );
		Buff.detach( ch, Bleeding.class );
		
	}

    @Override
    protected void onThrow( int cell ) {
        if (Dungeon.level.map[cell] == Terrain.WELL || Dungeon.level.pit[cell]) {

            super.onThrow( cell );

        } else  {

            Dungeon.level.pressCell(cell);
            shatter( cell );

        }
    }

    public void shatter( int cell ) {
        if (Dungeon.level.heroFOV[cell]) {
            GLog.i( Messages.get(Potion.class, "shatter") );
            Sample.INSTANCE.play( Assets.Sounds.SHATTER );
            splash( cell );
        }
    }

    protected void splash( int cell ) {

        Fire fire = (Fire)Dungeon.level.blobs.get( Fire.class );
        if (fire != null)
            fire.clear( cell );

        final int color = 0xFFFFFF;

        Char ch = Actor.findChar(cell);
        if (ch != null) {
            Buff.detach(ch, Burning.class);
            Buff.detach(ch, Ooze.class);
            Splash.at( ch.sprite.center(), color, 5 );
        } else {
            Splash.at( cell, color, 5 );
        }
    }

    @Override
    public boolean isUpgradable() {

        return false;
    }

    @Override
	public int value() {
        int i = 100 * quantity;
        return i;
	}
}
