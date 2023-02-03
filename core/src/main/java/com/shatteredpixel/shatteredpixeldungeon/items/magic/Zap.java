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

package com.shatteredpixel.shatteredpixeldungeon.items.magic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class Zap extends ConjurerSpell {

    {
        image = ItemSpriteSheet.ZAP;
    }

    @Override
    public void effect(Ballistica trajectory) {
        Char ch = Actor.findChar(trajectory.collisionPos);
        if (ch != null){
            if (ch instanceof Minion){
                ch.die( curUser );
                if (level() == 1){
                    Dungeon.hero.mana = Math.min(Dungeon.hero.mana + 24, Dungeon.hero.maxMana);
                } else if (level() == 2){
                    try {
                        Minion minion = (Minion) ch.getClass().newInstance();
                        GameScene.add(minion);
                        ScrollOfTeleportation.appear(minion, ch.pos);
                        minion.setDamage(
                                ((Minion) ch).minDamage * 3 / 2,
                                ((Minion) ch).maxDamage * 3 / 2);
                        Statistics.summonedMinions++;
                        minion.strength = ((Minion) ch).strength;
                        minion.enchantment = ((Minion) ch).enchantment;
                        minion.lvl = ((Minion) ch).lvl;
                        minion.minionClass = ((Minion) ch).minionClass;
                        minion.attunement = ((Minion) ch).attunement;

                        //if we have upgraded robe, increase hp
                        float robeBonus = 1f;
                        minion.setMaxHP((int) (ch.HT * 0.4f * robeBonus));
                    } catch (Exception eee){
                        ShatteredPixelDungeon.reportException(eee);
                    }
                }
            } else {
                ch.damage(0, curUser);
                if (level() == 1){
                    Buff.affect(ch, Weakness.class, 10f);
                    Buff.affect(ch, Hex.class, 10f);
                    Buff.affect(ch, Vulnerable.class, 10f);
                } else if (level() == 2){
                    for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )){
                        if (mob instanceof Minion){
                            mob.enemy = ch;
                            mob.enemySeen = true;
                            mob.aggro(ch);
                            mob.state = mob.HUNTING;
                            mob.notice();
                            mob.beckon(ch.pos);
                            Buff.affect(mob, Haste.class, 4f);
                            Buff.affect(mob, Stamina.class, 4f);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int manaCost() {
        switch (level()){
            case 1: return 11;
            case 2: return 14;
        }
        return 0;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc" + level());
    }

    @Override
    protected void fx( Ballistica beam, Callback callback ) {
        curUser.sprite.parent.add(
                new Beam.LightRay(curUser.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
        Sample.INSTANCE.play( Assets.Sounds.RAY );
        callback.call();
    }
}
