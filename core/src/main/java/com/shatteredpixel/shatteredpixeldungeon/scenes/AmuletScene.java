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

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Chaosstone;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Random;

public class AmuletScene extends PixelScene {
	
	private static final int WIDTH			= 120;
	private static final int BTN_HEIGHT		= 18;
	private static final float SMALL_GAP	= 2;
	private static final float LARGE_GAP	= 8;
	
	public static boolean noText = false;
	public static boolean gauntlet = false;
	
	private Image amulet;
	
	@Override
	public void create() {
		super.create();
		
		RenderedTextBlock text = null;
		if (!noText) {
			text = renderTextBlock( Messages.get(this, "text"), 8 );
			text.maxWidth(WIDTH);
			add( text );
		}
		
		amulet = new Image( Assets.Sprites.AMULET );
		add( amulet );
		
		RedButton btnExit = new RedButton( Messages.get(this, "exit") ) {
			@Override
			protected void onClick() {
				if ((Dungeon.mode == Dungeon.GameMode.GAUNTLET && Dungeon.depth > 41) ||
						(Dungeon.mode.isNormal() && Dungeon.depth > 0)) {
					Class clazz = Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE) ? Sword.class : Amulet.class;
					if (Dungeon.hero.belongings.getSimilar(new Chaosstone()) != null) {
						clazz = Chaosstone.class;
					}
					Dungeon.win(clazz);
					Dungeon.deleteGame(GamesInProgress.curSlot, true);
					Game.switchScene(RankingsScene.class);
				}
			}
		};
		btnExit.setSize( WIDTH, BTN_HEIGHT );
		add( btnExit );
		
		RedButton btnStay = new RedButton( Messages.get(this, "stay") ) {
			@Override
			protected void onClick() {
				onBackPressed();
			}
		};
		btnStay.setSize( WIDTH, BTN_HEIGHT );
		add( btnStay );

		RedButton btnMastery = new RedButton( Messages.get(this, "mastery") ) {
			@Override
			protected void onClick() {
				Dungeon.depth = Dungeon.chapterSize()*5+1;
				InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
				ShatteredPixelDungeon.switchScene(InterlevelScene.class);
			}
		};
		btnMastery.setSize( WIDTH, BTN_HEIGHT );
		add( btnMastery );
		
		float height;
		if (noText) {
			height = amulet.height + LARGE_GAP + btnExit.height() + SMALL_GAP + btnStay.height();
			
			amulet.x = (Camera.main.width - amulet.width) / 2;
			amulet.y = (Camera.main.height - height) / 2;
			align(amulet);

			btnExit.setPos( (Camera.main.width - btnExit.width()) / 2, amulet.y + amulet.height + LARGE_GAP );
			btnStay.setPos( btnExit.left(), btnExit.bottom() + SMALL_GAP );
			btnMastery.setPos(btnExit.left(), btnStay.bottom() + SMALL_GAP);
			
		} else {
			height = amulet.height + LARGE_GAP + text.height() + LARGE_GAP + btnExit.height() + SMALL_GAP + btnStay.height();
			
			amulet.x = (Camera.main.width - amulet.width) / 2;
			amulet.y = (Camera.main.height - height) / 2;
			align(amulet);

			text.setPos((Camera.main.width - text.width()) / 2, amulet.y + amulet.height + LARGE_GAP);
			align(text);
			
			btnExit.setPos( (Camera.main.width - btnExit.width()) / 2, text.top() + text.height() + LARGE_GAP );
			btnStay.setPos( btnExit.left(), btnExit.bottom() + SMALL_GAP );
			btnMastery.setPos(btnExit.left(), btnStay.bottom() + SMALL_GAP);
		}

		new Flare( 8, 48 ).color( 0xFFDDBB, true ).show( amulet, 0 ).angularSpeed = +30;
		
		fadeIn();
	}
	
	@Override
	protected void onBackPressed() {
		InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
		Game.switchScene( InterlevelScene.class );
	}
	
	private float timer = 0;
	
	@Override
	public void update() {
		super.update();

		Statistics.realDuration += (int)(Game.elapsed*1000);
		
		if ((timer -= Game.elapsed) < 0) {
			timer = Random.Float( 0.5f, 5f );
			
			Speck star = (Speck)recycle( Speck.class );
			star.reset( 0, amulet.x + 10.5f, amulet.y + 5.5f, Speck.DISCOVER );
			add( star );
		}
	}
}
