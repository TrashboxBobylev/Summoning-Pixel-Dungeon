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

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.TomeOfMastery;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.watabou.noosa.Image;

public class WndChooseSubclass extends Window {

    private static final int WIDTH		= 130;
    private static final float GAP		= 4;

    public WndChooseSubclass(final TomeOfMastery tome, final Hero hero ) {

        super();

        IconTitle titlebar = new IconTitle();
        titlebar.icon( new ItemSprite( tome.image(), null ) );
        titlebar.label( tome.name() );
        titlebar.setRect( 0, 0, WIDTH, 0 );
        add( titlebar );

        RenderedTextBlock message = PixelScene.renderTextBlock( 6 );
        message.text( Messages.get(this, "message"), WIDTH );
        message.setPos( titlebar.left(), titlebar.bottom() + GAP );
        add( message );

        float pos = message.bottom() + 3*GAP;

        for (HeroSubClass subCls : hero.heroClass.subClasses()){
            RedButton btnCls = new RedButton( Messages.titleCase(subCls.title()), 9 ) {
                @Override
                protected void onClick() {
                    GameScene.show(new WndOptions(subCls.icon(),
                            Messages.titleCase(subCls.title()),
                            Messages.get(WndChooseSubclass.this, "are_you_sure"),
                            Messages.get(WndChooseSubclass.this, "yes"),
                            Messages.get(WndChooseSubclass.this, "no")){
                        @Override
                        protected void onSelect(int index) {
                            hide();
                            if (index == 0 && WndChooseSubclass.this.parent != null){
                                WndChooseSubclass.this.hide();
                                tome.choose( subCls );
                            }
                        }
                    });
                }
            };
            btnCls.multiline = true;
            btnCls.setSize(WIDTH-32, btnCls.reqHeight()+6);
            btnCls.setRect( 0, pos, WIDTH-32, btnCls.reqHeight()+6);
            add( btnCls );

            IconButton clsInfo = new IconButton(subCls.icon()){
                @Override
                protected void onClick() {
                    GameScene.show(new WndHeroInfo.WndInfoSubclass(hero.heroClass, subCls));
                }
            };
            clsInfo.setRect(WIDTH-32, btnCls.top() + (btnCls.height()-32)/2, 32, 32);
            add(clsInfo);
            Image infoIcon = Icons.get(Icons.INFO);
            infoIcon.scale.scale(0.75f);
            IconButton clsInfoIcon = new IconButton(infoIcon){
                {
                    hotArea = null;
                }

                @Override
                protected void onPointerDown() {
                }

                @Override
                protected void onPointerUp() {
                }

                @Override
                protected void onClick() {
                }
            };
            clsInfoIcon.setRect(clsInfo.right()-clsInfo.width()/3, clsInfo.bottom()-clsInfo.height()/3, 4, 4);
            add(clsInfoIcon);

            pos = btnCls.bottom() + GAP;
        }

        RedButton btnCancel = new RedButton( Messages.get(this, "cancel") ) {
            @Override
            protected void onClick() {
                hide();
            }
        };
        btnCancel.setRect( 0, pos, WIDTH, 18 );
        add( btnCancel );

        resize( WIDTH, (int)btnCancel.bottom() );
    }
}
