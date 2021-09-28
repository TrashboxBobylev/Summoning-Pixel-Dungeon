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
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

public class WndChallenges extends Window {

	private final int WIDTH = Math.min(138, (int) (PixelScene.uiCamera.width * 0.9));
	private final int HEIGHT = (int) (PixelScene.uiCamera.height * 0.9);
	private static final int TTL_HEIGHT    = 18;
	private static final int BTN_HEIGHT    = 18;
	private static final int GAP        = 1;

	private boolean editable;
	private ArrayList<IconButton> infos = new ArrayList<>();
	private ArrayList<ConduitBox> boxes = new ArrayList<>();
	private ScrollPane pane;

	public WndChallenges( Conducts.Conduct conduct, boolean editable){
		ArrayList<Conducts.Conduct> singleton = new ArrayList<>();
		singleton.add(conduct);
		new WndChallenges(singleton, editable);
	}

	public WndChallenges( ArrayList<Conducts.Conduct> conduct, boolean editable ) {

		super();

		this.editable = editable;
		resize(WIDTH, HEIGHT);

		RenderedTextBlock title = PixelScene.renderTextBlock( Messages.get(this, "title"), 12 );
		title.hardlight( TITLE_COLOR );
		title.setPos(
				(WIDTH - title.width()) / 2,
				(TTL_HEIGHT - title.height()) / 2
		);
		PixelScene.align(title);
		add( title );
		Conducts.Conduct[] sorted = Conducts.Conduct.values();

		ScrollPane pane = new ScrollPane(new Component()) {
			@Override
			public void onClick(float x, float y) {
				int size = boxes.size();
				if (editable) {
					for (int i = 0; i < size; i++) {
						if (boxes.get(i).onClick(x, y)) break;
					}
				}
				size = infos.size();
				for (int i = 1; i < size+1; i++) {
					if (infos.get(i-1).inside(x, y)) {

						String message = sorted[i].desc();
						String title = Messages.titleCase(Messages.get(Conducts.class, sorted[i].name()));
						ShatteredPixelDungeon.scene().add(
								new WndTitledMessage(
										new Image(Assets.Interfaces.SUBCLASS_ICONS, (sorted[i].ordinal() - 1) * 16, 16, 16, 16),
										title, message)
						);

						break;
					}
				}
			}
		};
		add(pane);
		pane.setRect(0, title.bottom()+2, WIDTH, HEIGHT - title.bottom() - 2);
		Component content = pane.content();

		float pos = 2;
		for (Conducts.Conduct i : Conducts.Conduct.values()) {

				final String challenge = i.toString();

				ConduitBox cb = new ConduitBox( i == Conducts.Conduct.NULL ? challenge : "       " + challenge);
				cb.checked(conduct.contains(i));
				if (i == Conducts.Conduct.NULL && conduct.isEmpty()) cb.checked(true);
				cb.active = editable;
				cb.conduct = i;

				pos += GAP;
				cb.setRect(0, pos, WIDTH - 16, BTN_HEIGHT);
				if (i == Conducts.Conduct.NULL){
					cb.setSize(WIDTH, BTN_HEIGHT);
				}

				content.add(cb);
				boxes.add(cb);
				if (i != Conducts.Conduct.NULL) {
					IconButton info = new IconButton(Icons.get(Icons.INFO)) {
						@Override
						protected void layout() {
							super.layout();
							hotArea.y = -5000;
						}
					};
					info.setRect(cb.right(), pos, 16, BTN_HEIGHT);
					content.add(info);
					infos.add(info);
					Image icon = new Image(Assets.Interfaces.SUBCLASS_ICONS, (i.ordinal() - 1) * 16, 16, 16, 16);
					icon.x = cb.left()+1;
					icon.y = cb.top()+1;
					content.add(icon);
				}

				pos = cb.bottom();
		}

		content.setSize(WIDTH, pos);
	}

	@Override
	public void onBackPressed() {

		if (editable) {
			ArrayList<Conducts.Conduct> value = new ArrayList<>();
			for (ConduitBox slot : boxes) {
				if (slot.checked()) {
					value.add(slot.conduct);
				}
			}
			SPDSettings.challenges( value );
		}

		super.onBackPressed();
	}

	public class ConduitBox extends CheckBox{

		public Conducts.Conduct conduct;

		public ConduitBox(String label) {
			super(label);
		}

		@Override
		protected void onClick() {
			super.onClick();
			if (active){
//				for (CheckBox slot : boxes){
//					if (slot != this) slot.checked(false);
//				}
			}
		}

		protected boolean onClick(float x, float y) {
			if (!inside(x, y)) return false;
			Sample.INSTANCE.play(Assets.Sounds.CLICK);
			onClick();
			return true;
		}

		@Override
		protected void layout() {
			super.layout();
			hotArea.width = hotArea.height = 0;
		}
	}
}