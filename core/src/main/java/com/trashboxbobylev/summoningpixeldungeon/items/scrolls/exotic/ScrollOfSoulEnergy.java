/*
 *
 *  * Pixel Dungeon
 *  * Copyright (C) 2012-2015 Oleg Dolya
 *  *
 *  * Shattered Pixel Dungeon
 *  * Copyright (C) 2014-2019 Evan Debenham
 *  *
 *  * Summoning Pixel Dungeon
 *  * Copyright (C) 2019-2020 TrashboxBobylev
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */

package com.trashboxbobylev.summoningpixeldungeon.items.scrolls.exotic;

import com.trashboxbobylev.summoningpixeldungeon.Assets;
import com.trashboxbobylev.summoningpixeldungeon.Badges;
import com.trashboxbobylev.summoningpixeldungeon.Dungeon;
import com.trashboxbobylev.summoningpixeldungeon.Statistics;
import com.trashboxbobylev.summoningpixeldungeon.actors.Actor;
import com.trashboxbobylev.summoningpixeldungeon.actors.buffs.Invisibility;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.Mob;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.Minion;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.minions.SoulFlame;
import com.trashboxbobylev.summoningpixeldungeon.actors.mobs.npcs.PrismaticImage;
import com.trashboxbobylev.summoningpixeldungeon.effects.Speck;
import com.trashboxbobylev.summoningpixeldungeon.items.armor.ConjurerArmor;
import com.trashboxbobylev.summoningpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.trashboxbobylev.summoningpixeldungeon.items.weapon.melee.staffs.ChickenStaff;
import com.trashboxbobylev.summoningpixeldungeon.messages.Messages;
import com.trashboxbobylev.summoningpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ScrollOfSoulEnergy extends ExoticScroll {

    {
        initials = 9;
    }

    @Override
    public void doRead() {

        //destroy all minions on level
        for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])){
            if (m instanceof Minion){
                m.die(curUser);
                m.sprite.emitter().burst(Speck.factory(Speck.STEAM), (int) (((Minion) m).attunement*5));
            }
        }

        //searching for free space
        //TODO: maybe it's should be in Level static method?
        ArrayList<Integer> respawnPoints = new ArrayList<Integer>();

        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
            int p = curUser.pos + PathFinder.NEIGHBOURS8[i];
            if (Actor.findChar( p ) == null && Dungeon.level.passable[p]) {
                respawnPoints.add( p );
            }
        }

        if (respawnPoints.size() > 0){
            SoulFlame minion = new SoulFlame();
            GameScene.add(minion);
            ScrollOfTeleportation.appear(minion, respawnPoints.get(Random.index(respawnPoints)));
            curUser.usedAttunement = curUser.attunement();
            minion.setDamage(
                    SoulFlame.adjustMinDamage(curUser.lvl),
                    SoulFlame.adjustMaxDamage(curUser.lvl));
            Statistics.summonedMinions++;
            Badges.validateConjurerUnlock();
            minion.strength = curUser.STR;
            minion.setMaxHP(SoulFlame.adjustHP((int) minion.attunement));
        }

        Sample.INSTANCE.play( Assets.SND_READ );
        Invisibility.dispel();

        setKnown();

        readAnimation();
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", SoulFlame.adjustMinDamage(Dungeon.hero.lvl), SoulFlame.adjustMaxDamage(Dungeon.hero.lvl), SoulFlame.adjustHP((int) Dungeon.hero.attunement()) );
    }
}
