/*
 * Pixel Dungeon
 *   * Copyright (C) 2012-2015 Oleg Dolya
 *   *
 *   * Shattered Pixel Dungeon
 *   * Copyright (C) 2014-2019 Evan Debenham
 *   *
 *   * Summoning Pixel Dungeon
 *   * Copyright (C) 2019-2020 TrashboxBobylev
 *   *
 *   * This program is free software: you can redistribute it and/or modify
 *   * it under the terms of the GNU General Public License as published by
 *   * the Free Software Foundation, either version 3 of the License, or
 *   * (at your option) any later version.
 *   *
 *   * This program is distributed in the hope that it will be useful,
 *   * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   * GNU General Public License for more details.
 *   *
 *   * You should have received a copy of the GNU General Public License
 *   * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.ConjurerSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.staffs.Staff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

public class WndTierInfo extends Window {
    private static final float GAP	= 2;
    private int tier = 1;

    private static final int WIDTH_MIN = 120;
    private static final int WIDTH_MAX = 220;
    private static RedButton btnForward;
    private static RedButton btnBack;

    public WndTierInfo( Item item ) {
        super();

        fillFields( item );
    }

    private void fillFields( Item item ) {

        int color = TITLE_COLOR;
        switch (tier) {
            case 1:
                color = ItemSlot.BRONZE;
                break;
            case 2:
                color = ItemSlot.SILVER;
                break;
            case 3:
                color = ItemSlot.GOLD;
                break;
        }

        IconTitle titlebar = new IconTitle( item );
        titlebar.label(Messages.get(this, "tier", tier));
        titlebar.color( color );

        final RenderedTextBlock[] txtInfo = {PixelScene.renderTextBlock(Messages.get(item, "tier" + tier), 6)};

        layoutFields(titlebar, txtInfo[0]);

        btnForward = new RedButton( "<", 8 ) {
            @Override
            protected void onClick() {
                if (--tier < 1) tier = 3;
                rerender(txtInfo, titlebar, item);
            }
        };
        add( btnForward );

        btnBack = new RedButton( ">", 8 ) {
            @Override
            protected void onClick() {
                if (++tier > 3) tier = 1;
                rerender(txtInfo, titlebar, item);

            }
        };
        add( btnBack );
        resize((int) Math.max(WIDTH_MIN, txtInfo[0].maxWidth()), (int)(btnForward.bottom() + 2) );
        btnForward.setRect(0, txtInfo[0].bottom() + GAP,  width / 2 - 15, 16 );
        btnBack.setRect(width - width / 2 + 15, txtInfo[0].bottom() + GAP, width / 2 - 15, 16 );
        resize((int) Math.max(WIDTH_MIN, txtInfo[0].maxWidth()), (int)(btnForward.bottom() + 2) );

    }

    private void rerender(RenderedTextBlock[] txtInfo, IconTitle titlebar, Item item){
        txtInfo[0].clear();
        txtInfo[0] = PixelScene.renderTextBlock( Messages.get(item, "tier" + tier), 6);
        int color = TITLE_COLOR;
        switch (tier) {
            case 1:
                color = ItemSlot.BRONZE;
                break;
            case 2:
                color = ItemSlot.SILVER;
                break;
            case 3:
                color = ItemSlot.GOLD;
                break;
        }
        titlebar.color( color );
        layoutFields(titlebar, txtInfo[0]);
        btnForward.setRect(0, txtInfo[0].bottom() + GAP,  width / 2 - 15, 16 );
        btnBack.setRect(width - width / 2 + 15, txtInfo[0].bottom() + GAP, width / 2 - 15, 16 );
        resize(Math.max(WIDTH_MIN, txtInfo[0].maxWidth()), (int)(btnForward.bottom() + 2) );
    }

    private void layoutFields(IconTitle title, RenderedTextBlock info){
        int width = WIDTH_MIN;

        info.maxWidth(width);

        //window can go out of the screen on landscape, so widen it as appropriate
        while (PixelScene.landscape()
                && info.height() > 100
                && width < WIDTH_MAX){
            width += 20;
            info.maxWidth(width);
        }

        title.setRect( 0, 0, WIDTH_MAX, 0 );
        add( title );

        info.setPos(title.left(), title.bottom() + GAP);
        add( info );
    }
}
