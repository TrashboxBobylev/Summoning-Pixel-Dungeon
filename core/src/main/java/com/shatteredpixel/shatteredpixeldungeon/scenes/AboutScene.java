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

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.*;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.DeviceCompat;

public class AboutScene extends PixelScene {

	@Override
	public void create() {
		super.create();

		final float colWidth = 120;
		final float fullWidth = colWidth * (landscape() ? 2 : 1);

		int w = Camera.main.width;
		int h = Camera.main.height;

		ScrollPane list = new ScrollPane( new Component() );
		add( list );

		Component content = list.content();
		content.clear();

		CreditsBlock tb = new CreditsBlock(true, Window.TITLE_COLOR,
				"Summoning Pixel Dungeon",
				Icons.TRASHBOXBOBYLEV.get(),
				"Developed by: _Trashbox Bobylev_\nBased on ShatteredPD's open source",
				"reddit.com/u/TrashboxBobylev",
				"https://reddit.com/u/TrashboxBobylev");
		tb.setRect((w - fullWidth)/2f, 6, 120, 0);
		content.add(tb);

		CreditsBlock zachary = new CreditsBlock(false, ItemSlot.GOLD,
				"Wiki, Testing and Proofreading:",
				Icons.ZACHARY.get(),
				"Zackary4536",
				null,
				null);
		zachary.setSize(colWidth/2f, 0);
		if (landscape()){
			zachary.setPos(tb.right()-10, tb.top() + (tb.height() - tb.height())/2f);
		} else {
			zachary.setPos(w/2f - colWidth/2f, tb.bottom()+5);
		}
		content.add(zachary);

		CreditsBlock omicronrg = new CreditsBlock(false, ItemSlot.ENHANCED,
				"Support and Testing:",
				Icons.OMICRONRG.get(),
				"Daniel Ømicrón Rodriguez",
				"Click for Youtube channel",
				"https://www.youtube.com/channel/UCH5v6V_Be2_AY0bprgHS4Bw");
		omicronrg.setRect(zachary.right()+10, zachary.top(), colWidth/2f, 0);
		content.add(omicronrg);

		CreditsBlock krauzxe = new CreditsBlock(false, ItemSlot.BRONZE,
				"Former Sprite Artist:",
				Icons.KRAUZXE.get(),
				"Krauzxe##1119",
				null,
				null);
		krauzxe.setSize(colWidth/2f, 0);
		if (landscape()){
			krauzxe.setPos(zachary.left()+zachary.width()/2, omicronrg.bottom()+6);
		} else {
			krauzxe.setPos(tb.left()+tb.width()/4, omicronrg.bottom()+5);
		}
		content.add(krauzxe);

		CreditsBlock lolman = new CreditsBlock(false, ItemSlot.SILVER,
				"Support and Ideas:",
				Icons.LOLMAN.get(),
				"Gammalolman#1119",
				"reddit.com/u/Hyperlolman",
				"https://www.reddit.com/user/Hyperlolman");
		lolman.setSize(colWidth/2f, 0);
		if (landscape()){
			lolman.setPos(tb.right(), krauzxe.bottom()+8);
		} else {
			lolman.setPos(tb.left()-5, krauzxe.bottom()+8);
		}
		content.add(lolman);

		CreditsBlock marshall = new CreditsBlock(false, ItemSlot.WARNING,
				"Conjurer's sprites:",
				Icons.MARSHALL.get(),
				"MarshalldotEXE",
				"Click for Youtube channel",
				"https://www.youtube.com/channel/UCEhheHlAmqkGMqULoDAIMOQ");
		marshall.setRect(lolman.right()+10, lolman.top(), colWidth/2f, 0);
		content.add(marshall);

		CreditsBlock guys = new CreditsBlock(false, Window.TITLE_COLOR,
				"The rest of credits:",
				Icons.INFO.get(),
				"RavenWolf#4290 - Advanced sprites\n" +
						"Zrp200#0484, Kohru#4813 - Code spinnets\n"+
						"smujames#5300, NeoSlav#5320 and rest of community - ideas and support",
				"discord.gg/tkrjtUS",
				"https://discord.gg/tkrjtUS");
		guys.setSize(colWidth, 4);
		if (landscape()){
			guys.setPos(tb.left(), krauzxe.bottom()+10);
		} else {
			guys.setPos(tb.left(), marshall.bottom()+10);
		}
		content.add(guys);

		//*** Shattered Pixel Dungeon Credits ***


		String shpxLink = "https://ShatteredPixel.com";
		//tracking codes, so that the website knows where this pageview came from
		shpxLink += "?utm_source=shatteredpd";
		shpxLink += "&utm_medium=about_page";
		shpxLink += "&utm_campaign=ingame_link";

		CreditsBlock shpx = new CreditsBlock(true, Window.SHPX_COLOR,
				"Shattered Pixel Dungeon",
				Icons.SHPX.get(),
				"Developed by: _Evan Debenham_\nBased on Pixel Dungeon's open source",
				"ShatteredPixel.com",
				shpxLink);
		shpx.setRect((w - fullWidth)/2f, 6, 120, 0);
		content.add(shpx);
		if (landscape()){
			shpx.setRect(tb.left(), guys.bottom() + 24, colWidth, 0);
		} else {
			shpx.setRect(tb.left(), guys.bottom() + 16, colWidth, 0);
		}

		addLine(shpx.top() - 4, content);

		CreditsBlock alex = new CreditsBlock(false, Window.SHPX_COLOR,
				"Hero Art & Design:",
				Icons.ALEKS.get(),
				"Aleksandar Komitov",
				"alekskomitov.com",
				"https://www.alekskomitov.com");
		alex.setSize(colWidth/2f, 0);
		if (landscape()){
			alex.setPos(shpx.right(), shpx.top() + (shpx.height() - alex.height())/2f);
		} else {
			alex.setPos(w/2f - colWidth/2f, shpx.bottom()+5);
		}
		content.add(alex);

		CreditsBlock charlie = new CreditsBlock(false, Window.SHPX_COLOR,
				"Sound Effects:",
				Icons.CHARLIE.get(),
				"Charlie",
				"s9menine.itch.io",
				"https://s9menine.itch.io");
		charlie.setRect(alex.right(), alex.top(), colWidth/2f, 0);
		content.add(charlie);

		//*** Pixel Dungeon Credits ***

		final int WATA_COLOR = 0x55AAFF;
		CreditsBlock wata = new CreditsBlock(true, WATA_COLOR,
				"Pixel Dungeon",
				Icons.WATA.get(),
				"Developed by: _Watabou_\nInspired by Brian Walker's Brogue",
				"pixeldungeon.watabou.ru",
				"http://pixeldungeon.watabou.ru");
		if (landscape()){
			wata.setRect(shpx.left(), shpx.bottom() + 8, colWidth, 0);
		} else {
			wata.setRect(shpx.left(), alex.bottom() + 8, colWidth, 0);
		}
		content.add(wata);

		addLine(wata.top() - 4, content);

		CreditsBlock cube = new CreditsBlock(false, WATA_COLOR,
				"Music:",
				Icons.CUBE_CODE.get(),
				"Cube Code",
				null,
				null);
		cube.setSize(colWidth/2f, 0);
		if (landscape()){
			cube.setPos(wata.right(), wata.top() + (wata.height() - cube.height())/2f);
		} else {
			cube.setPos(alex.left(), wata.bottom()+5);
		}
		content.add(cube);

		//*** libGDX Credits ***

		final int GDX_COLOR = 0xE44D3C;
		CreditsBlock gdx = new CreditsBlock(true,
				GDX_COLOR,
				null,
				Icons.LIBGDX.get(),
				"ShatteredPD is powered by _libGDX_!",
				"libgdx.badlogicgames.com",
				"http://libgdx.badlogicgames.com");
		if (landscape()){
			gdx.setRect(wata.left(), wata.bottom() + 8, colWidth, 0);
		} else {
			gdx.setRect(wata.left(), cube.bottom() + 8, colWidth, 0);
		}
		content.add(gdx);

		addLine(gdx.top() - 4, content);

		//blocks the rays from the LibGDX icon going above the line
		ColorBlock blocker = new ColorBlock(w, 8, 0xFF000000);
		blocker.y = gdx.top() - 12;
		content.addToBack(blocker);
		content.sendToBack(gdx);

		CreditsBlock arcnor = new CreditsBlock(false, GDX_COLOR,
				"Pixel Dungeon GDX:",
				Icons.ARCNOR.get(),
				"Edu García",
				"twitter.com/arcnor",
				"https://twitter.com/arcnor");
		arcnor.setSize(colWidth/2f, 0);
		if (landscape()){
			arcnor.setPos(gdx.right(), gdx.top() + (gdx.height() - arcnor.height())/2f);
		} else {
			arcnor.setPos(alex.left(), gdx.bottom()+5);
		}
		content.add(arcnor);

		CreditsBlock purigro = new CreditsBlock(false, GDX_COLOR,
				"Shattered GDX Help:",
				Icons.PURIGRO.get(),
				"Kevin MacMartin",
				"github.com/prurigro",
				"https://github.com/prurigro/");
		purigro.setRect(arcnor.right()+2, arcnor.top(), colWidth/2f, 0);
		content.add(purigro);

		//*** Transifex Credits ***

		CreditsBlock transifex = new CreditsBlock(true,
				Window.TITLE_COLOR,
				null,
				null,
				"ShatteredPD is community-translated via _Transifex_! Thank you so much to all of Shattered's volunteer translators!",
				"www.transifex.com/shattered-pixel/",
				"https://www.transifex.com/shattered-pixel/shattered-pixel-dungeon/");
		transifex.setRect((Camera.main.width - colWidth)/2f, purigro.bottom() + 8, colWidth, 0);
		content.add(transifex);

		addLine(transifex.top() - 4, content);

		addLine(transifex.bottom() + 4, content);

		//*** Freesound Credits ***

		CreditsBlock freesound = new CreditsBlock(true,
				Window.TITLE_COLOR,
				null,
				null,
				"Shattered Pixel Dungeon uses the following sound samples from _freesound.org_:\n\n" +

				"Creative Commons Attribution License:\n" +
				"_SFX ATTACK SWORD 001.wav_ by _JoelAudio_\n" +
				"_Pack: Slingshots and Longbows_ by _saturdaysoundguy_\n" +
				"_Cracking/Crunching, A.wav_ by _InspectorJ_\n" +
				"_Extracting a sword.mp3_ by _Taira Komori_\n" +
				"_Pack: Uni Sound Library_ by _timmy h123_\n\n" +

				"Creative Commons Zero License:\n" +
				"_Pack: Movie Foley: Swords_ by _Black Snow_\n" +
				"_machine gun shot 2.flac_ by _qubodup_\n" +
				"_m240h machine gun burst 4.flac_ by _qubodup_\n" +
				"_Pack: Onomatopoeia_ by _Adam N_\n" +
				"_Pack: Watermelon_ by _lolamadeus_\n" +
				"_metal chain_ by _Mediapaja2009_\n" +
				"_Pack: Sword Clashes Pack_ by _JohnBuhr_\n" +
				"_Pack: Metal Clangs and Pings_ by _wilhellboy_\n" +
				"_Pack: Stabbing Stomachs & Crushing Skulls_ by _TheFilmLook_\n" +
				"_Sheep bleating_ by _zachrau_\n" +
				"_Lemon,Juicy,Squeeze,Fruit.wav_ by _Filipe Chagas_\n" +
				"_Lemon,Squeeze,Squishy,Fruit.wav_ by _Filipe Chagas_",
				"www.freesound.org",
				"https://www.freesound.org");
		freesound.setRect(transifex.left()-10, transifex.bottom() + 8, colWidth+20, 0);
		content.add(freesound);

		content.setSize( fullWidth, freesound.bottom()+10 );

		list.setRect( 0, 0, w, h );
		list.scrollTo(0, 0);

		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );

		fadeIn();
	}
	
	@Override
	protected void onBackPressed() {
		ShatteredPixelDungeon.switchScene(TitleScene.class);
	}

	private void addLine( float y, Group content ){
		ColorBlock line = new ColorBlock(Camera.main.width, 1, 0xFF333333);
		line.y = y;
		content.add(line);
	}

	private static class CreditsBlock extends Component {

		boolean large;
		RenderedTextBlock title;
		Image avatar;
		Flare flare;
		RenderedTextBlock body;

		RenderedTextBlock link;
		ColorBlock linkUnderline;
		PointerArea linkButton;

		//many elements can be null, but body is assumed to have content.
		private CreditsBlock(boolean large, int highlight, String title, Image avatar, String body, String linkText, String linkUrl){
			super();

			this.large = large;

			if (title != null) {
				this.title = PixelScene.renderTextBlock(title, large ? 8 : 6);
				if (highlight != -1) this.title.hardlight(highlight);
				add(this.title);
			}

			if (avatar != null){
				this.avatar = avatar;
				add(this.avatar);
			}

			if (large && highlight != -1 && this.avatar != null){
				this.flare = new Flare( 7, 24 ).color( highlight, true ).show(this.avatar, 0);
				this.flare.angularSpeed = 20;
			}

			this.body = PixelScene.renderTextBlock(body, 6);
			if (highlight != -1) this.body.setHightlighting(true, highlight);
			if (large) this.body.align(RenderedTextBlock.CENTER_ALIGN);
			add(this.body);

			if (linkText != null && linkUrl != null){

				int color = 0xFFFFFFFF;
				if (highlight != -1) color = 0xFF000000 | highlight;
				this.linkUnderline = new ColorBlock(1, 1, color);
				add(this.linkUnderline);

				this.link = PixelScene.renderTextBlock(linkText, 6);
				if (highlight != -1) this.link.hardlight(highlight);
				add(this.link);

				linkButton = new PointerArea(0, 0, 0, 0){
					@Override
					protected void onClick( PointerEvent event ) {
						DeviceCompat.openURI( linkUrl );
					}
				};
				add(linkButton);
			}

		}

		@Override
		protected void layout() {
			super.layout();

			float topY = top();

			if (title != null){
				title.maxWidth((int)width());
				title.setPos( x + (width() - title.width())/2f, topY);
				topY += title.height() + (large ? 2 : 1);
			}

			if (large){

				if (avatar != null){
					avatar.x = x + (width()-avatar.width())/2f;
					avatar.y = topY;
					PixelScene.align(avatar);
					if (flare != null){
						flare.point(avatar.center());
					}
					topY = avatar.y + avatar.height() + 2;
				}

				body.maxWidth((int)width());
				body.setPos( x + (width() - body.width())/2f, topY);
				topY += body.height() + 2;

			} else {

				if (avatar != null){
					avatar.x = x;
					body.maxWidth((int)(width() - avatar.width - 1));

					if (avatar.height() > body.height()){
						avatar.y = topY;
						body.setPos( avatar.x + avatar.width() + 1, topY + (avatar.height() - body.height())/2f);
						topY += avatar.height() + 1;
					} else {
						avatar.y = topY + (body.height() - avatar.height())/2f;
						PixelScene.align(avatar);
						body.setPos( avatar.x + avatar.width() + 1, topY);
						topY += body.height() + 2;
					}

				} else {
					topY += 1;
					body.maxWidth((int)width());
					body.setPos( x, topY);
					topY += body.height()+2;
				}

			}

			if (link != null){
				if (large) topY += 1;
				link.maxWidth((int)width());
				link.setPos( x + (width() - link.width())/2f, topY);
				topY += link.height() + 2;

				linkButton.x = link.left()-1;
				linkButton.y = link.top()-1;
				linkButton.width = link.width()+2;
				linkButton.height = link.height()+2;

				linkUnderline.size(link.width(), PixelScene.align(0.49f));
				linkUnderline.x = link.left();
				linkUnderline.y = link.bottom()+1;

			}

			topY -= 2;

			height = Math.max(height, topY - top());
		}
	}
}
