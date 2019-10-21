/*
 *  Pixel Dungeon
 *  Copyright (C) 2012-2015 Oleg Dolya
 *
 *  Shattered Pixel Dungeon
 *  Copyright (C) 2014-2019 Evan Debenham
 *
 *  Summoning Pixel Dungeon
 *  Copyright (C) 2019-2020 TrashboxBobylev
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.trashboxbobylev.summoningpixeldungeon.items.spells;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.Char;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Amok;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.summoningpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.Mob;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.exotic.ScrollOfPassage;
import com.trashboxbobylev.summoningpixeldungeon.items.wands.CursedWand;
import com.trashboxbobylev.summoningpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSprite;
import com.trashboxbobylev.summoningpixeldungeon.sprites.ItemSpriteSheet;
import com.trashboxbobylev.summoningpixeldungeon.ui.TargetHealthIndicator;
import com.trashboxbobylev.summoningpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

public class Contain extends TargetedSpell {

    {
        image = ItemSpriteSheet.MAGIC_PORTER;
    }

    public Mob containedMob = null;

    @Override
    protected void affectTarget(Ballistica bolt, Hero hero) {
        Mob mob = (Mob) Actor.findChar(bolt.collisionPos);
        if (mob != null && containedMob == null){
            Dungeon.level.mobs.remove(mob);
            mob.sprite.killAndErase();
            Actor.remove(mob);
            mob.state = mob.WANDERING;
            mob.enemy = null;
            containedMob = mob;
            TargetHealthIndicator.instance.target(null);
            for (int i= 0; i < 5; i++) Sample.INSTANCE.play(Assets.SND_HIT);
            updateQuickslot();
            curUser.spendAndNext(Actor.TICK);
        } else if (containedMob != null && Dungeon.level.passable[bolt.collisionPos]){
            Mob mb = containedMob;
            containedMob = null;
            mb.pos = bolt.collisionPos;
            Actor.add(mb);
            GameScene.add(mb, 0f);
            ScrollOfTeleportation.appear(mb, bolt.collisionPos);
            Dungeon.level.occupyCell(mb);
            mb.state = mb.WANDERING;
            for (int i= 0; i < 5; i++) Sample.INSTANCE.play(Assets.SND_MIMIC);
        } else {
            GLog.i(Messages.get(CursedWand.class, "nothing"));
        }
    }

    private static final ItemSprite.Glowing RED = new ItemSprite.Glowing( 0xff002a, 0.5f );

    @Override
    public ItemSprite.Glowing glowing() {
        return containedMob != null ? RED : null;
    }

    private static final String MOB	= "mob";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        if (containedMob != null) {
            containedMob.storeInBundle(bundle);
        }
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle(bundle);
        Bundlable mobBundle = bundle.get(MOB);
        if (mobBundle != null){
            containedMob = (Mob) mobBundle;
        }
    }

    @Override
    public String desc() {
        String desc = super.desc();
        if (containedMob != null){
            desc += "\n\n" + Messages.get(this, "desc_mob", containedMob.name);
        }
        return desc;
    }

    public static class Recipe extends com.trashboxbobylev.summoningpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{ScrollOfMirrorImage.class, ArcaneCatalyst.class};
            inQuantity = new int[]{1, 1};

            cost = 8;

            output = Contain.class;
            outQuantity = 2;
        }

    }
}
