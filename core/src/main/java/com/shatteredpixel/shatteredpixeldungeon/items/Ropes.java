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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Chains;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class Ropes extends Item {

    {
        image = ItemSpriteSheet.ROPES;
        defaultAction = AC_THROW;
        stackable = true;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public void doThrow(Hero hero) {
        GameScene.selectCell(caster);
    }

    //lovely copy-paste from EtherealChains.java
    private CellSelector.Listener caster = new CellSelector.Listener(){

        @Override
        public void onSelect(Integer target) {
            if (target != null && (Dungeon.level.visited[target] || Dungeon.level.mapped[target])){

                //chains cannot be used to go where it is impossible to walk to
                PathFinder.buildDistanceMap(target, BArray.or(Dungeon.level.passable, Dungeon.level.losBlocking, null));
                if (PathFinder.distance[curUser.pos] == Integer.MAX_VALUE){
                    GLog.warning( Messages.get(EtherealChains.class, "cant_reach") );
                    return;
                }

                final Ballistica chain = new Ballistica(curUser.pos, target, Ballistica.STOP_TARGET);

                if (Actor.findChar( chain.collisionPos ) != null){
                    chainEnemy( chain, curUser, Actor.findChar( chain.collisionPos ));
                } else if (Dungeon.level.heaps.get( chain.collisionPos ) != null) {
                    chainItem( chain, curUser, Dungeon.level.heaps.get( chain.collisionPos ) );
                } else if (Dungeon.level.traps.get( chain.collisionPos ) != null) {
                    chainTrap( chain, curUser, Dungeon.level.traps.get( chain.collisionPos ) );
                }
                else {
                    chainLocation( chain, curUser );
                }
                throwSound();
                detach( curUser.belongings.backpack );
                updateQuickslot();

                Sample.INSTANCE.play( Assets.Sounds.MISS );

            }

        }

        @Override
        public String prompt() {
            return Messages.get(EtherealChains.class, "prompt");
        }
    };

    //pulls an enemy to a position along the chain's path, as close to the hero as possible
    private void chainEnemy( Ballistica chain, final Hero hero, final Char enemy ){

        if (enemy.properties().contains(Char.Property.IMMOVABLE)) {
            GLog.warning( Messages.get(EtherealChains.class, "cant_pull") );
            return;
        }

        int bestPos = -1;
        for (int i : chain.subPath(1, chain.dist)){
            //prefer to the earliest point on the path
            if (!Dungeon.level.solid[i]
                    && Actor.findChar(i) == null
                    && (!Char.hasProp(enemy, Char.Property.LARGE) || Dungeon.level.openSpace[i])){
                bestPos = i;
                break;
            }
        }

        if (bestPos == -1) {
            GLog.i(Messages.get(EtherealChains.class, "does_nothing"));
            return;
        }

        final int pulledPos = bestPos;

        hero.busy();
        hero.sprite.parent.add(new Chains(hero.sprite.center(), enemy.sprite.center(), new Callback() {
            public void call() {
                Actor.add(new Pushing(enemy, enemy.pos, pulledPos, new Callback() {
                    public void call() {
                        Dungeon.level.occupyCell(enemy);
                    }
                }));
                enemy.pos = pulledPos;
                Dungeon.observe();
                GameScene.updateFog();
                hero.spendAndNext(1f);
            }
        }, Effects.get(Effects.Type.ROPE)));
    }

    //pulls the hero along the chain to the collisionPos, if possible.
    private void chainLocation( Ballistica chain, final Hero hero ){

        //don't pull if rooted
        if (hero.rooted){
            GLog.warning( Messages.get(EtherealChains.class, "rooted") );
            return;
        }

        //don't pull if the collision spot is in a wall
        if (Dungeon.level.solid[chain.collisionPos]){
            GLog.i( Messages.get(EtherealChains.class, "inside_wall"));
            return;
        }

        final int newHeroPos = chain.collisionPos;

        hero.busy();
        hero.sprite.parent.add(new Chains(hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(newHeroPos), new Callback() {
            public void call() {
                Actor.add(new Pushing(hero, hero.pos, newHeroPos, new Callback() {
                    public void call() {
                        Dungeon.level.occupyCell(hero);
                    }
                }));
                hero.spendAndNext(1f);
                hero.pos = newHeroPos;
                Dungeon.observe();
                GameScene.updateFog();
            }
        }, Effects.get(Effects.Type.ROPE)));
    }

    //get an item at position of
    private void chainItem( Ballistica chain, final Hero hero, Heap heap ){

        final int newHeroPos = chain.collisionPos;

        hero.busy();
        hero.sprite.parent.add(new Chains(hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(newHeroPos), new Callback() {
            public void call() {
                Item item = heap.peek();
                if (item.doPickUp( hero )) {
                    heap.pickUp();

                    if (item instanceof Dewdrop
                            || item instanceof TimekeepersHourglass.sandBag
                            || item instanceof DriedRose.Petal
                            || item instanceof Key) {
                        //Do Nothing
                    } else {

                        //TODO make all unique items important? or just POS / SOU?
                        boolean important = item.unique && item.isIdentified() &&
                                (item instanceof Scroll || item instanceof Potion);
                        if (important) {
                            GLog.positive( Messages.get(hero, "you_now_have", item.name()) );
                        } else {
                            GLog.i( Messages.get(hero, "you_now_have", item.name()) );
                        }
                    }
                } else {

                    if (item instanceof Dewdrop
                            || item instanceof TimekeepersHourglass.sandBag
                            || item instanceof DriedRose.Petal
                            || item instanceof Key) {
                        //Do Nothing
                    } else {
                        //TODO temporary until 0.8.0a, when all languages will get this phrase
                        if (Messages.lang() == Languages.ENGLISH) {
                            GLog.newLine();
                            GLog.negative(Messages.get(this, "you_cant_have", item.name()));
                        }
                    }

                    heap.sprite.drop();
                }
            }
        }, Effects.get(Effects.Type.ROPE)));
    }

    //activate the trap at position
    private void chainTrap( Ballistica chain, final Hero hero, Trap trap ){
        hero.busy();
        hero.sprite.parent.add(new Chains(hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(chain.collisionPos), new Callback() {
            public void call() {
                Dungeon.level.pressCell(chain.collisionPos);
                hero.spendAndNext(1f);
            }
        }, Effects.get(Effects.Type.ROPE)));
    }

    @Override
    public int value() {
        return 6 * quantity;
    }
}
