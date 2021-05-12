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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Rankings;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class WndExploreResurrect extends Window {

	private static final int WIDTH		= 120;
	private static final int BTN_HEIGHT	= 20;
	private static final float GAP		= 2;

	public static WndExploreResurrect instance;
	public static Object causeOfDeath;

	public WndExploreResurrect(Object causeOfDeath ) {
		
		super();
		
		instance = this;
		WndExploreResurrect.causeOfDeath = causeOfDeath;
		
		IconTitle titlebar = new IconTitle();
		titlebar.icon(Icons.get(Dungeon.GameMode.EXPLORE.icon));
		titlebar.label( Messages.get(this, "title") );
		titlebar.setRect( 0, 0, WIDTH, 0 );
		add( titlebar );
		
		RenderedTextBlock message = PixelScene.renderTextBlock( Messages.get(this, "message"), 6 );
		message.maxWidth(WIDTH);
		message.setPos(0, titlebar.bottom() + GAP);
		add( message );
		
		RedButton btnYes = new RedButton( Messages.get(this, "yes") ) {
			@Override
			protected void onClick() {
				hide();

				Dungeon.hero.HP = Dungeon.hero.HT/4;

				//ensures that you'll get to act first in almost any case, to prevent reviving and then instantly dieing again.
				PotionOfHealing.cure(Dungeon.hero);
				Buff.detach(Dungeon.hero, Paralysis.class);
				Dungeon.hero.spend(-Dungeon.hero.cooldown());

				new Flare(8, 32).color(0xFFFF66, true).show(Dungeon.hero.sprite, 2f);
				CellEmitter.get(Dungeon.hero.pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3);

				Sample.INSTANCE.play( Assets.Sounds.TELEPORT );
				GLog.warning( Messages.get(this, "revive") );
				Statistics.ankhsUsed++;

				for (Char ch : Actor.chars()){
					if (ch instanceof DriedRose.GhostHero){
						((DriedRose.GhostHero) ch).sayAnkh();
						return;
					}
				}
			}
		};
		btnYes.setRect( 0, message.top() + message.height() + GAP, WIDTH, BTN_HEIGHT );
		add( btnYes );
		
		RedButton btnNo = new RedButton( Messages.get(this, "no") ) {
			@Override
			protected void onClick() {
				hide();

				Hero.reallyDie( WndExploreResurrect.causeOfDeath );
				Rankings.INSTANCE.submit( false, WndExploreResurrect.causeOfDeath.getClass() );
			}
		};
		btnNo.setRect( 0, btnYes.bottom() + GAP, WIDTH, BTN_HEIGHT );
		add( btnNo );
		
		resize( WIDTH, (int)btnNo.bottom() );
	}
	
	@Override
	public void destroy() {
		super.destroy();
		instance = null;
	}
	
	@Override
	public void onBackPressed() {
	}
}
