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

import java.util.ArrayList;

public class WndChallenges extends Window {

	private static final int WIDTH		= 135;
	private static final int TTL_HEIGHT    = 18;
	private static final int BTN_HEIGHT    = 18;
	private static final int GAP        = 1;

	private boolean editable;
	private ArrayList<ConduitBox> slots = new ArrayList<>();

	public WndChallenges( Conducts.Conduct conduct, boolean editable ) {

		super();

		this.editable = editable;

		RenderedTextBlock title = PixelScene.renderTextBlock( Messages.get(this, "title"), 12 );
		title.hardlight( TITLE_COLOR );
		title.setPos(
				(WIDTH - title.width()) / 2,
				(TTL_HEIGHT - title.height()) / 2
		);
		PixelScene.align(title);
		add( title );

		float pos = TTL_HEIGHT;
		for (Conducts.Conduct i : Conducts.Conduct.values()) {

				final String challenge = i.toString();

				ConduitBox cb = new ConduitBox( i == Conducts.Conduct.NULL ? challenge : "       " + challenge);
				cb.checked(i == conduct);
				if (i == Conducts.Conduct.NULL && conduct == null) cb.checked(true);
				cb.active = editable;
				cb.conduct = i;

				pos += GAP;
				cb.setRect(0, pos, WIDTH - 16, BTN_HEIGHT);
				if (i == Conducts.Conduct.NULL){
					cb.setSize(WIDTH, BTN_HEIGHT);
				}

				add(cb);
				slots.add(cb);
				if (i != Conducts.Conduct.NULL) {
					IconButton info = new IconButton(Icons.get(Icons.INFO)) {
						@Override
						protected void onClick() {
							super.onClick();
							ShatteredPixelDungeon.scene().add(
									new WndTitledMessage(
											new Image(Assets.Interfaces.SUBCLASS_ICONS, (i.ordinal() - 1) * 16, 16, 16, 16),
											challenge,
											Messages.get(Conducts.class, i.name() + "_desc"))
							);
						}
					};
					info.setRect(cb.right(), pos, 16, BTN_HEIGHT);
					add(info);
					Image icon = new Image(Assets.Interfaces.SUBCLASS_ICONS, (i.ordinal() - 1) * 16, 16, 16, 16);
					icon.x = cb.left()+1;
					icon.y = cb.top()+1;
					add(icon);
				}

				pos = cb.bottom();
		}

		resize( WIDTH, (int)pos );
	}

	@Override
	public void onBackPressed() {

		if (editable) {
			Conducts.Conduct value = null;
			for (ConduitBox slot : slots) {
				if (slot.checked()) {
					value = slot.conduct;
				}
			}
			SPDSettings.challenges( value == Conducts.Conduct.NULL ? null : value );
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
				for (CheckBox slot : slots){
					if (slot != this) slot.checked(false);
				}
			}
		}
	}
}