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

package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Contain extends TargetedSpell {

    {
        image = ItemSpriteSheet.MAGIC_PORTER;
    }

    public Mob containedMob = null;

    @Override
    protected void affectTarget(Ballistica bolt, Hero hero) {
        Mob mob = (Mob) Actor.findChar(bolt.collisionPos);
        if (mob != null && containedMob == null &&
                !mob.properties().contains(Char.Property.BOSS) &&
                !mob.properties().contains(Char.Property.MINIBOSS) &&
                !mob.properties().contains(Char.Property.IMMOVABLE)){
            float resist = WandOfCorruption.getEnemyResist(mob, mob);
            resist *= 1 + 2*Math.pow(mob.HP/(float)mob.HT, 2);
            if (resist < 5) {
                Dungeon.level.mobs.remove(mob);
                mob.sprite.killAndErase();
                mob.state = mob.PASSIVE;
                containedMob = mob;
                quantity++;
                collect();
                TargetHealthIndicator.instance.target(null);
                for (int i = 0; i < 5; i++) Sample.INSTANCE.play(Assets.Sounds.HIT);
                updateQuickslot();
            } else {
                mob.damage(Math.round(mob.HP * 0.5f), hero);
            }
            curUser.spendAndNext(Actor.TICK);
        } else if (containedMob != null && Dungeon.level.passable[bolt.collisionPos]){
            Mob mb = containedMob;
            containedMob = null;
            mb.pos = bolt.collisionPos;
            GameScene.add(mb);
            ScrollOfTeleportation.appear(mb, bolt.collisionPos);
            Dungeon.level.occupyCell(mb);
            mb.state = mb.WANDERING;
            Actor.fixTime();
            Buff.affect(mb, Amok.class, 6f);
            for (int i= 0; i < 5; i++) Sample.INSTANCE.play(Assets.Sounds.MIMIC);
        } else {
            GLog.i(Messages.get(CursedWand.class, "nothing"));
        }
    }

    @Override
    public int value() {
        return Recipe.calculatePrice(new Recipe()) * quantity;
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
        bundle.put(MOB, containedMob);
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle(bundle);
        containedMob = (Mob) bundle.get(MOB);
    }

    @Override
    public String desc() {
        String desc = super.desc();
        if (containedMob != null){
            desc += "\n\n" + Messages.get(this, "desc_mob", containedMob.getName());
        }
        return desc;
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{ScrollOfMirrorImage.class, ScrollOfTeleportation.class, ArcaneCatalyst.class};
            inQuantity = new int[]{1, 1, 1};

            cost = 10;

            output = Contain.class;
            outQuantity = 3;
        }

    }
}
