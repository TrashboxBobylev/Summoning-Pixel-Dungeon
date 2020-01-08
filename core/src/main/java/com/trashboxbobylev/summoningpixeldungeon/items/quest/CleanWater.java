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

package com.trashboxbobylev.summoningpixeldungeon.items.quest;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Challenges;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.Blob;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.Fire;
import com.trashboxbobylev.summoningpixeldungeon.actors.blobs.HealGas;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.*;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.summoningpixeldungeon.effects.CellEmitter;
import com.trashboxbobylev.summoningpixeldungeon.effects.Speck;
import com.trashboxbobylev.summoningpixeldungeon.effects.Splash;
import com.trashboxbobylev.summoningpixeldungeon.effects.particles.ShaftParticle;
import com.trashboxbobylev.summoningpixeldungeon.items.Item;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.Potion;
import com.trashboxbobylev.summoningpixeldungeon.items.potions.PotionOfHealing;
import com.trashboxbobylev.summoningpixeldungeon.levels.Terrain;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.trashboxbobylev.summoningpixeldungeon.windows.WndItem;
import com.trashboxbobylev.summoningpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class CleanWater extends Item {

    private static final String AC_DRINK = "DRINK";

    {
        image = ItemSpriteSheet.CLEAN_WATER;
		bones = true;
	}

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.add( AC_DRINK );
        return actions;
    }

    @Override
    public boolean isIdentified() {
        return true;
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

        Sample.INSTANCE.play( Assets.SND_DRINK );

        hero.sprite.operate( hero.pos );
    }

	public void apply( Hero hero ) {

		hero.HP = hero.HT;
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

            Dungeon.level.press( cell, null, true );
            shatter( cell );

        }
    }

    public void shatter( int cell ) {
        if (Dungeon.level.heroFOV[cell]) {
            GLog.i( Messages.get(Potion.class, "shatter") );
            Sample.INSTANCE.play( Assets.SND_SHATTER );
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
	public int price() {
        int i = 100 * quantity;
        if (Dungeon.isChallenged(Challenges.NO_HEALING)) i *= 4;
        return i;
	}
}
