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
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.*;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class WndHeroInfo extends WndTabbed {

    private HeroInfoTab heroInfo;
    private TalentInfoTab talentInfo;
    private SubclassInfoTab subclassInfo;

    private static int WIDTH = 120;
    private static int MIN_HEIGHT = 125;
    private static int MARGIN = 2;

    public WndHeroInfo( HeroClass cl ){

        Image tabIcon;
        switch (cl){
            case WARRIOR: default:
                tabIcon = new ItemSprite(ItemSpriteSheet.SEAL, null);
                break;
            case MAGE:
                tabIcon = new ItemSprite(ItemSpriteSheet.MAGES_STAFF, null);
                break;
            case ROGUE:
                tabIcon = new ItemSprite(ItemSpriteSheet.ARTIFACT_CLOAK, null);
                break;
            case HUNTRESS:
                tabIcon = new ItemSprite(ItemSpriteSheet.SPIRIT_BOW, null);
                break;
            case CONJURER:
                tabIcon = new ItemSprite(ItemSpriteSheet.STARS, null);
                break;
            case ADVENTURER:
                tabIcon = Icons.get(Icons.BACKPACK);
                break;
        }

        int finalHeight = MIN_HEIGHT;

        heroInfo = new HeroInfoTab(cl);
        add(heroInfo);
        heroInfo.setSize(WIDTH, MIN_HEIGHT);
        finalHeight = (int)Math.max(finalHeight, heroInfo.height());

        add( new IconTab( tabIcon ){
            @Override
            protected void select(boolean value) {
                super.select(value);
                heroInfo.visible = heroInfo.active = value;
            }
        });

        if (cl == HeroClass.ROGUE) {
            talentInfo = new TalentInfoTab(cl);
            add(talentInfo);
            talentInfo.setSize(WIDTH, MIN_HEIGHT);
            finalHeight = (int) Math.max(finalHeight, talentInfo.height());

            add(new IconTab(Icons.get(Icons.TALENT)) {
                @Override
                protected void select(boolean value) {
                    super.select(value);
                    talentInfo.visible = talentInfo.active = value;
                }
            });
        }

        if (Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_2) && cl != HeroClass.ADVENTURER) {
            subclassInfo = new SubclassInfoTab(cl);
            add(subclassInfo);
            subclassInfo.setSize(WIDTH, MIN_HEIGHT);
            finalHeight = (int)Math.max(finalHeight, subclassInfo.height());

            add(new IconTab(new ItemSprite(ItemSpriteSheet.MASTERY, null)) {
                @Override
                protected void select(boolean value) {
                    super.select(value);
                    subclassInfo.visible = subclassInfo.active = value;
                }
            });
        }

        resize(WIDTH, finalHeight);

        layoutTabs();
        if (talentInfo != null)
            talentInfo.layout();

        select(0);

    }

    private static class HeroInfoTab extends Component {

        private RenderedTextBlock title;
        private RenderedTextBlock[] info;
        private Image[] icons;

        public HeroInfoTab(HeroClass cls){
            super();
            title = PixelScene.renderTextBlock(Messages.titleCase(cls.title()), 9);
            title.hardlight(TITLE_COLOR);
            add(title);

            String[] desc_entries = cls.desc().split("\n\n");

            info = new RenderedTextBlock[desc_entries.length];

            for (int i = 0; i < desc_entries.length; i++){
                info[i] = PixelScene.renderTextBlock(desc_entries[i], 6);
                add(info[i]);
            }

            switch (cls){
                case WARRIOR: default:
                    icons = new Image[]{ new ItemSprite(ItemSpriteSheet.SEAL),
                            new ItemSprite(ItemSpriteSheet.WORN_SHORTSWORD),
                            new ItemSprite(ItemSpriteSheet.WEAPON_HOLDER),
                            new ItemSprite(ItemSpriteSheet.SLINGSHOT),
                            Icons.get(Icons.ADVENTURER),
                            new ItemSprite(ItemSpriteSheet.STEWED),
                            new ItemSprite(ItemSpriteSheet.SCROLL_ISAZ)};
                    break;
                case MAGE:
                    icons = new Image[]{ new ItemSprite(ItemSpriteSheet.MAGES_STAFF),
                            new ItemSprite(ItemSpriteSheet.ELEMENTAL_BLAST),
                            new ItemSprite(ItemSpriteSheet.WAND_HOLDER),
                            Icons.get(Icons.ADVENTURER),
                            new ItemSprite(ItemSpriteSheet.SCROLL_ISAZ)};
                    break;
                case ROGUE:
                    icons = new Image[]{ new ItemSprite(ItemSpriteSheet.ARTIFACT_CLOAK),
                            new Image(Assets.Environment.TERRAIN_FEATURES, 112, 80, 16, 16),
                            Icons.get(Icons.DEPTH),
                            Icons.get(Icons.TALENT),
                            Icons.get(Icons.ADVENTURER),
                            new ItemSprite(ItemSpriteSheet.SCROLL_ISAZ)};
                    break;
                case HUNTRESS:
                    icons = new Image[]{ new ItemSprite(ItemSpriteSheet.SPIRIT_BOW),
                            new Image(Assets.Environment.TILES_SEWERS, 112, 96, 16, 16),
                            new ItemSprite(ItemSpriteSheet.THROWING_CLUB),
                            new ItemSprite(ItemSpriteSheet.ARTIFACT_TALISMAN),
                            new ItemSprite(ItemSpriteSheet.ARMOR_SCOUT),
                            Icons.get(Icons.ADVENTURER),
                            new ItemSprite(ItemSpriteSheet.SCROLL_ISAZ)};
                    break;
                case CONJURER:
                    icons = new Image[]{ Icons.get(Icons.CONJURER_BOOK),
                            new ItemSprite(ItemSpriteSheet.FROGGIT_STAFF),
                            new ItemSprite(ItemSpriteSheet.ARMOR_CONJURER),
                            new Image(Assets.Interfaces.BUFFS_LARGE, 112, 32, 16, 16),
                            new Image(Assets.Interfaces.BUFFS_LARGE, 96, 48, 16, 16),
                            new ItemSprite(ItemSpriteSheet.RATION),
                            Icons.get(Icons.ADVENTURER),
                            new ItemSprite(ItemSpriteSheet.SCROLL_ISAZ)};
                    break;
                case ADVENTURER:
                    icons = new Image[]{ new ItemSprite(ItemSpriteSheet.SOMETHING),
                            new ItemSprite(ItemSpriteSheet.CHEST),
                            new ItemSprite(ItemSpriteSheet.ARMOR_ROGUE),
                            new Image(Assets.Interfaces.BUFFS_LARGE, 208, 32, 16, 16),
                            Icons.get(Icons.ADVENTURER),
                            new ItemSprite(ItemSpriteSheet.MASTERY)};
                    break;
            }
            for (Image im : icons) {
                add(im);
            }

        }

        @Override
        protected void layout() {
            super.layout();

            title.setPos((width-title.width())/2, MARGIN);

            float pos = title.bottom()+4*MARGIN;

            for (int i = 0; i < info.length; i++){
                info[i].maxWidth((int)width - 20);
                info[i].setPos(20, pos);

                icons[i].x = (20-icons[i].width())/2;
                icons[i].y = info[i].top() + (info[i].height() - icons[i].height())/2;

                pos = info[i].bottom() + 4*MARGIN;
            }

            height = Math.max(height, pos - 4*MARGIN);

        }
    }

    private static class TalentInfoTab extends Component {

        private RenderedTextBlock title;
        private RenderedTextBlock message;
        private TalentsPane talentPane;

        public TalentInfoTab( HeroClass cls ){
            super();
            title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(WndHeroInfo.class, "talents")), 9);
            title.hardlight(TITLE_COLOR);
            add(title);

            message = PixelScene.renderTextBlock(Messages.get(WndHeroInfo.class, "talents_msg"), 6);
            add(message);

            ArrayList<LinkedHashMap<Talent, Integer>> talents = new ArrayList<>();
            Talent.initClassTalents(cls, talents);

            talentPane = new TalentsPane(false, talents);
            add(talentPane);
        }

        @Override
        protected void layout() {
            super.layout();

            title.setPos((width-title.width())/2, MARGIN);
            message.maxWidth((int)width);
            message.setPos(0, title.bottom()+4*MARGIN);

            talentPane.setRect(0, message.bottom() + 3*MARGIN, width, 85);

            height = Math.max(height, talentPane.bottom());
        }
    }

    private static class SubclassInfoTab extends Component {

        private RenderedTextBlock title;
        private RenderedTextBlock message;
        private RenderedTextBlock[] subClsDescs;
        private IconButton[] subClsInfos;

        public SubclassInfoTab( HeroClass cls ){
            super();
            title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(WndHeroInfo.class, "subclasses")), 9);
            title.hardlight(TITLE_COLOR);
            add(title);

            message = PixelScene.renderTextBlock(Messages.get(WndHeroInfo.class, "subclasses_msg"), 6);
            add(message);

            HeroSubClass[] subClasses = cls.subClasses();

            subClsDescs = new RenderedTextBlock[subClasses.length];
            subClsInfos = new IconButton[subClasses.length];

            for (int i = 0; i < subClasses.length; i++){
                subClsDescs[i] = PixelScene.renderTextBlock(Messages.titleCase(subClasses[i].title()), 6);
                int finalI = i;
                subClsInfos[i] = new IconButton( subClasses[i].icon() ){
                    @Override
                    protected void onClick() {
                        Game.scene().addToFront(new WndInfoSubclass(cls, subClasses[finalI]));
                    }
                };
                add(subClsDescs[i]);
                add(subClsInfos[i]);
            }

        }

        @Override
        protected void layout() {
            super.layout();

            title.setPos((width-title.width())/2, MARGIN);
            message.maxWidth((int)width);
            message.setPos(0, title.bottom()+4*MARGIN);

            float pos = message.bottom()+4*MARGIN;

            for (int i = 0; i < subClsDescs.length; i++){
                subClsDescs[i].maxWidth((int)width - 32);
                subClsDescs[i].setPos(33, pos);

                subClsInfos[i].setRect(0, subClsDescs[i].top() + (subClsDescs[i].height()-32)/2, 32, 32);

                pos = subClsDescs[i].bottom() + 8*MARGIN;
            }

            height = Math.max(height, pos - 8*MARGIN);

        }
    }

    public static class WndInfoSubclass extends Window {

        protected static final int WIDTH_MIN    = 120;
        protected static final int WIDTH_MAX    = 220;
        protected static final int GAP	= 2;

        public WndInfoSubclass(HeroClass cls, HeroSubClass subCls){

            super();

            Component titlebar = new IconTitle( subCls.icon(), Messages.titleCase(subCls.title()) );
            String message = subCls.desc();

            int width = WIDTH_MIN;

            titlebar.setRect( 0, 0, width, 0 );
            add(titlebar);

            RenderedTextBlock text = PixelScene.renderTextBlock( 6 );
            text.text( message, width );
            text.setPos( titlebar.left(), titlebar.bottom() + 2*GAP );
            add( text );

            while (PixelScene.landscape()
                    && text.bottom() > (PixelScene.MIN_HEIGHT_L - 10)
                    && width < WIDTH_MAX){
                width += 20;
                titlebar.setRect(0, 0, width, 0);
                text.setPos( titlebar.left(), titlebar.bottom() + 2*GAP );
                text.maxWidth(width);
            }

            bringToFront(titlebar);

            resize( width, (int)text.bottom() + 2 );

            if (cls == HeroClass.ROGUE){
                ArrayList<LinkedHashMap<Talent, Integer>> talentList = new ArrayList<>();
                Talent.initSubclassTalents(subCls, talentList);

                TalentsPane.TalentTierPane talentPane = new TalentsPane.TalentTierPane(talentList.get(2), 3, false);
                talentPane.setRect(0, height + 5, width, talentPane.height());
                add(talentPane);
                resize(width, (int) talentPane.bottom());

            }
        }

    }

}
