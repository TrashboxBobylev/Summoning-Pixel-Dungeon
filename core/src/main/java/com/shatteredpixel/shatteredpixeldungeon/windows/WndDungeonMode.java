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

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.IntroScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;

public class WndDungeonMode extends Window {
    private static final int WIDTH_P = 120;
    private static final int WIDTH_L = 160;

    private static final int MARGIN  = 2;

    private ScrollPane modeList;
    private ArrayList<ModeButton> slots = new ArrayList<>();
    private Dungeon.GameMode chosenGameMode;
    private float timer;


    public WndDungeonMode( ){
        super();

        int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

        float pos = MARGIN;
        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(this, "title")), 9);
        title.hardlight(TITLE_COLOR);
        title.setPos((width-title.width())/2, pos);
        title.maxWidth(width - MARGIN * 2);
        add(title);

        modeList = new ScrollPane( new Component()){
            @Override
            public void onClick( float x, float y ) {
                for (ModeButton slot : slots) {
                    if (slot.onClick(x, y)) {
                        return;
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

            ModeButton moveBtn = new ModeButton(mode.desc(), 6, mode);
            moveBtn.icon(ic);
            moveBtn.multiline = true;
            moveBtn.setSize(width, moveBtn.reqHeight());
            moveBtn.setRect(0, positem, width, moveBtn.reqHeight());
            moveBtn.enable(true);
            content.add(moveBtn);
//            modeList.add(moveBtn);
            slots.add(moveBtn);
            positem += moveBtn.height() + MARGIN;
        }
        content.setSize(width, positem+1);

        resize(width, (int)PixelScene.uiCamera.height-100);
        modeList.setRect(0, title.bottom()+MARGIN, width, height - MARGIN*4.5f);

    }

    @Override
    public synchronized void update() {
        super.update();
        if (chosenGameMode != null){
            if ((timer += Game.elapsed) > 0.2f){
                hide();

                if (GamesInProgress.selectedClass == null) return;

                Dungeon.mode = chosenGameMode;
                Dungeon.hero = null;
                ActionIndicator.action = null;
                InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
                timer = 0f;

                if (SPDSettings.intro()) {
                    SPDSettings.intro( false );
                    Game.switchScene( IntroScene.class );
                } else {
                    Game.switchScene( InterlevelScene.class );
                }

            }
        }
    }

    public class ModeButton extends StyledButton {

        Dungeon.GameMode mode;

        public ModeButton(String label, int size, Dungeon.GameMode m){
            super();
            add(new Image());
            bg = Chrome.get( Chrome.Type.RED_BUTTON );
            addToBack( bg );

            text = PixelScene.renderTextBlock( size );
            text.text( label );
            add( text );

            mode = m;
        }

        @Override
        protected void createChildren() {

        }

        public boolean onClick(float x, float y){
            if (inside(x, y) && chosenGameMode == null){
                bg.brightness( 1.2f );
                Sample.INSTANCE.play( Assets.Sounds.CLICK );
                chosenGameMode = mode;
                return true;
            }
            return false;
        }
    }
}
