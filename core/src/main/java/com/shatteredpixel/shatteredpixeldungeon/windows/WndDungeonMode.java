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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.IntroScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

public class WndDungeonMode extends Window {
    private static final int WIDTH_P = 120;
    private static final int WIDTH_L = 160;

    private static final int MARGIN  = 2;

    private ScrollPane modeList;
    private ArrayList<RedButton> slots = new ArrayList<>();


    public WndDungeonMode( ){
        super();

        int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

        float pos = MARGIN;
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(this, "title")), 9);
        title.hardlight(TITLE_COLOR);
        title.setPos((width-title.width())/2, pos);
        title.maxWidth(width - MARGIN * 2);
        add(title);

        modeList = new ScrollPane( new Component() ){
            @Override
            public void onClick( float x, float y ) {
                int size = slots.size();
                for (int i=0; i < size; i++) {
                    if (slots.get( i ).inside(x, y)) {
                        break;
                    }
                }
            }
        };
        add(modeList);

        pos = title.bottom() + 3*MARGIN;
        Component content = modeList.content();
        int positem = 0;

        for (Dungeon.GameMode mode : Dungeon.GameMode.values()) {
            Image ic = Icons.get(mode.icon);

            RedButton moveBtn = new RedButton(mode.desc(), 6){
                @Override
                protected void onClick() {
                    super.onClick();
                    hide();

                    if (GamesInProgress.selectedClass == null) return;

                    Dungeon.mode = mode;
                    Dungeon.hero = null;
                    ActionIndicator.action = null;
                    InterlevelScene.mode = InterlevelScene.Mode.DESCEND;

                    if (SPDSettings.intro()) {
                        SPDSettings.intro( false );
                        Game.switchScene( IntroScene.class );
                    } else {
                        Game.switchScene( InterlevelScene.class );
                    }
                }
            };
            moveBtn.icon(ic);
            moveBtn.multiline = true;
            moveBtn.setSize(width, moveBtn.reqHeight());
            moveBtn.setRect(0, positem, width, moveBtn.reqHeight());
            moveBtn.enable(true);
            content.add(moveBtn);
//            modeList.add(moveBtn);
//            add(moveBtn);
            positem += moveBtn.height() + MARGIN;
        }
        content.setSize(width, positem+1);

        resize(width, (int)PixelScene.uiCamera.height-100);
        modeList.setRect(0, title.bottom()+MARGIN, width, height - MARGIN*4.5f);

    }
}
