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

package com.shatteredpixel.shatteredpixeldungeon.items.magic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.WardingWraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class Shocker extends ConjurerSpell {

    {
        image = ItemSpriteSheet.SHOCKER;
        manaCost = 0;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        shock(ch);
        if (level() == 2){
            for (int i: PathFinder.NEIGHBOURS8){
                ch = Actor.findChar(trajectory.collisionPos + i);
                shock(ch);
            }
        }
    }

    private void shock(Char ch) {
        if (ch instanceof Minion || ch instanceof DriedRose.GhostHero || ch instanceof WandOfLivingEarth.EarthGuardian ||
                ch instanceof WandOfWarding.Ward || (ch instanceof WardingWraith && ch.alignment == Char.Alignment.ALLY)){
            Sample.INSTANCE.play(Assets.Sounds.ZAP);
            Sample.INSTANCE.play(Assets.Sounds.HEALTH_WARN);
            ch.damage((int) (ch.HT * dmg()), new Grim());
            Camera.main.shake(4f, 0.4f);
            GameScene.flash(0xFFFFFF);
            Buff.affect(ch, Empowered.class, buff());
            Buff.affect(ch, Haste.class, buff());
            Buff.affect(ch, Adrenaline.class, buff());
            Buff.affect(ch, Bless.class, buff());
            Buff.affect(ch, NoHeal.class, noheal());

            ch.sprite.burst(0xFFFFFFFF, buffedLvl() / 2 + 2);
        }
    }

    private float dmg(){
        switch (level()){
            case 1: return 0.25f;
            case 2: return 0.5f;
        }
        return 0.5f;
    }

//    @Override
//    public int manaCost() {
//        switch (level()){
//            case 1: return 25;
//            case 2: return 35;
//        }
//        return 15;
//    }

    private int noheal(){
        switch (level()){
            case 1: return 30;
            case 2: return 40;
        }
        return 50;
    }

    private int buff(){
        switch (level()){
            case 1: return 15;
            case 2: return 10;
        }
        return 20;
    }


    @Override
    public String desc() {
        return Messages.get(this, "desc" + level());
    }

    public static class NoHeal extends FlavourBuff {

    }
}
