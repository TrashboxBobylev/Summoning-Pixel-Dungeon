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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.PerfumeGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Yog;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.stationary.StationaryMinion;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ConjurerArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.magic.Shocker;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public abstract class Minion extends Mob {

    public int independenceRange = 8;

    public enum  MinionClass{
        DEFENSE, MELEE, MAGIC, RANGE, SUPPORT
    }

    public MinionClass minionClass = MinionClass.MELEE;

    public int minDamage = 0;
    public int maxDamage = 0;
    private float partialHealing;

    public int baseDefense = 0;

    public int strength = 9;
    protected int defendingPos = -1;
    protected boolean movingToDefendPos = false;

    {
        immunities.add(Corruption.class);
        immunities.add(Shrink.class);
        immunities.add(TimedShrink.class);
    }

    public void defendPos( int cell ){
        aggro(null);
        state = WANDERING;
        defendingPos = cell;
        movingToDefendPos = true;
    }

    public void clearDefensingPos(){
        defendingPos = -1;
        movingToDefendPos = false;
    }

    public void followHero(){
        aggro(null);
        state = WANDERING;
        defendingPos = -1;
        movingToDefendPos = false;
    }

    public void targetChar( Char ch ){
        aggro(ch);
        target = ch.pos;
        defendingPos = -1;
        movingToDefendPos = false;
    }

    public void directTocell( int cell ){
        if (!Dungeon.level.heroFOV[cell]
                || Actor.findChar(cell) == null
                || (Actor.findChar(cell) != Dungeon.hero && Actor.findChar(cell).alignment != Char.Alignment.ENEMY)){
            defendPos( cell );
            return;
        }

        if (Actor.findChar(cell) == Dungeon.hero){
            followHero();

        } else if (Actor.findChar(cell).alignment == Char.Alignment.ENEMY){
            targetChar(Actor.findChar(cell));

        }
    }

    public boolean isTanky = false;
    public Weapon.Enchantment enchantment;
    public Weapon.Augment augmentOffense = Weapon.Augment.NONE;
    public int lvl;
    public int timer = -1;

    private static final String DEFEND_POS = "defend_pos";
    private static final String MOVING_TO_DEFEND = "moving_to_defend";
    private static final String AUGMENT1 = "augment1";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("minDamage", minDamage);
        bundle.put("maxDamage", maxDamage);
        bundle.put("maxDR", baseDefense);
        bundle.put("str", strength);
        bundle.put("att", attunement);
        bundle.put("enchantment", enchantment);
        bundle.put("level", lvl);
        bundle.put("class", minionClass);
        bundle.put("deathtimer", timer);
        bundle.put("partialhp", partialHealing);
        bundle.put(DEFEND_POS, defendingPos);
        bundle.put(MOVING_TO_DEFEND, movingToDefendPos);
        bundle.put(AUGMENT1, augmentOffense);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);

        minDamage = bundle.getInt("minDamage");
        maxDamage = bundle.getInt("maxDamage");
        baseDefense = bundle.getInt("maxDR");
        strength = bundle.getInt("str");
        attunement = bundle.getInt("att");
        lvl = bundle.getInt("level");
        enchantment = (Weapon.Enchantment) bundle.get("enchantment");
        minionClass = bundle.getEnum("class", MinionClass.class);
        timer = bundle.getInt("deathtimer");
        partialHealing = bundle.getFloat("partialHealing");
        if (bundle.contains(DEFEND_POS)) defendingPos = bundle.getInt(DEFEND_POS);
        movingToDefendPos = bundle.getBoolean(MOVING_TO_DEFEND);

        if (bundle.contains(AUGMENT1)) augmentOffense = bundle.getEnum(AUGMENT1, Weapon.Augment.class);
        else augmentOffense = Weapon.Augment.NONE;
    }

    public float attunement = 1;

    {
        //all minions are allies and kinda intelligent
        alignment = Alignment.ALLY;
        intelligentAlly = true;

        WANDERING = new Wandering();
        state = WANDERING;

        //before other mobs
        actPriority = MOB_PRIO + 1;

        immunities.add(PerfumeGas.Affection.class);
    }

    @Override
    protected boolean act() {
        if (!isAlive()){
            timer--;
            if (timer > 0) {
                sprite.emitter().burst(MagicMissile.WhiteParticle.FACTORY, 15);

                return super.act();
            } else {
                sprite.remove(CharSprite.State.SPIRIT);
                destroy();
                sprite.die();

                return true;
            }
        }

        if (Dungeon.hero.buff(HolyAuraBuff.class) != null){
            HolyAuraBuff buff = Dungeon.hero.buff(HolyAuraBuff.class);
            partialHealing += 1.0f/buff.healingRate;
            if (partialHealing >= 1) {
                partialHealing--;
                HP = Math.min(HP+1, HT);
                sprite.emitter().burst(Speck.factory(Speck.HEALING), 3);
            }
        }

        return super.act();
    }

    @Override
    public String getName() {
        return enchantment != null ? enchantment.name( super.getName() ) : super.getName();
    }

    public void setMaxHP(int hp){
        HP = HT = hp;
    }

    public void setStrength(int str){
        strength = str;
    }

    public void setDamage(int min, int max){
        minDamage = min;
        maxDamage = max;
    }

    @Override
    public int damageRoll() {
        int i = Random.NormalIntRange(minDamage, maxDamage);
        if (buff(AdditionalDamage.class) != null) i += minDamage*2;
        if (Dungeon.hero.buff(Attunement.class) != null) i *= Attunement.empowering();
        if (buff(Chungus.class) != null) i*=1.4f;
        if (Dungeon.hero.belongings.armor instanceof ScaleArmor &&
            Dungeon.hero.belongings.armor.level() == 2)
            i *= 1.25f;
        return augmentOffense.damageFactor(i);
    }

    @Override
    public int damage(int dmg, Object src) {
        if (AntiMagic.RESISTS.contains(src.getClass()) && buff(MagicalResistance.class) != null){
            dmg -= Random.NormalIntRange(0, 7);
            if (dmg < 0) dmg = 0;
        }
        if (Dungeon.hero.belongings.armor instanceof ConjurerArmor &&
                Dungeon.hero.belongings.armor.level() == 2)
            dmg *= 0.6f;
        return super.damage(dmg, src);
    }

    @Override
    public int defenseValue() {
        return augmentOffense.damageFactor(baseDefense);
    }

    @Override
    public Char chooseEnemy() {
        Char enemy = super.chooseEnemy();

        int targetPos = defendingPos != -1 ? defendingPos : Dungeon.hero.pos;
        int distance = (this instanceof StationaryMinion || buff(ArmoredShielding.class) != null) ? Integer.MAX_VALUE : independenceRange;

        //will never attack something far from their target
        if (enemy != null
                && Dungeon.level.mobs.contains(enemy)
                && (Dungeon.level.distance(enemy.pos, targetPos) <= distance)
                && (invisible == 0)){
            return enemy;
        }

        return null;
    }

    @Override
    public void updateSpriteState() {
        super.updateSpriteState();
        if (buff(ArmoredShielding.class) != null){
            sprite.add(CharSprite.State.SHIELDED);
        } else {
            sprite.remove(CharSprite.State.SHIELDED);
        }
    }

    @Override
    public void die(Object cause) {
        sprite.emitter().burst(MagicMissile.WhiteParticle.FACTORY, 15);
        if (timer == -1) {
            if (cause == Chasm.class){
                super.die( cause );
            } else if (this instanceof Talent.DogBreedingMinion){
                Buff.affect(this, Talent.DogBreedingDeathRefusal.class);
            } else if (buff(NecromancyStat.class) != null){
                timer = buff(NecromancyStat.class).level+1;
                sprite.add(CharSprite.State.SPIRIT);
                Buff.detach(this, NecromancyStat.class);
                Buff.affect(this, Shocker.NoHeal.class, 9999f);
            } else {
                super.die( cause );
            }
        }
    }

    //same accuracy and dexterity as player
    @Override
    public int defenseSkill(Char enemy) {
        if (buff(Block.class) != null) return INFINITE_EVASION;
        boolean seen = (enemySeen && enemy.invisible == 0);

        if (enemy == Dungeon.hero && !Dungeon.hero.canSurpriseAttack()) seen = true;
        if ( seen
                && paralysed == 0
                && !(alignment == Alignment.ALLY && enemy == Dungeon.hero)) {
            int i = Dungeon.hero.defenseSkill(enemy);
            if (buff(AdditionalEvasion.class) != null) i *= 1.3f;
            return Math.round(i * (1f / augmentOffense.delayFactor(1)));
        } else {
            return 0;
        }
    }

    @Override
    public int defenseSkillDesc() {
        return Math.round(Dungeon.hero.defenseSkill(enemy) * (1f / augmentOffense.delayFactor(1)));
    }

    @Override
    public void add(Buff buff) {
        super.add(buff);
        if (buff instanceof TankHeal && minionClass == MinionClass.DEFENSE){
            HP = HT;
            sprite.emitter().start( Speck.factory( Speck.HEALING ), 0.4f, 4 );
            buff.detach();
        }
    }

    @Override
    public int attackSkill(Char target) {

        int encumbrance = strength - Dungeon.hero.STR();

        float accuracy = 1;

        if (encumbrance > 0){
            accuracy /= Math.pow(1.5, encumbrance);
        }

        return (int) (Dungeon.hero.getAttackSkill() * accuracy);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        if (enchantment != null && buff(MagicImmune.class) == null) {
            damage = enchantment.proc(  this, enemy, damage );
        }
        if (buff(RootingOnShots.class) != null && Random.Float() < 0.5) Buff.prolong(enemy, Roots.class, 5f);
        return super.attackProc(enemy, damage);
    }

    @Override
    public boolean canBeIgnored(Char ch) {
        if (ch instanceof Piranha){
            return ch.HT == ch.HP;
        } else if (ch instanceof Yog){
            return false;
        }
        else {
            Mob mob = (Mob) ch;
            return (mob.state == mob.SLEEPING || mob.state == mob.PASSIVE || mob.state == mob.WANDERING);
        }
    }

    @Override
    protected boolean getCloser(int target) {
        if (buff(ArmoredShielding.class) != null) return false;

        return super.getCloser(target);
    }

    @Override
    protected boolean getFurther(int target) {
        if (buff(ArmoredShielding.class) != null) return false;
        return super.getFurther(target);
    }

    @Override
    public float speed() {
        float speed = 1f / augmentOffense.delayFactor(super.speed()*Dungeon.hero.speed());

        if (Dungeon.hero.hasTalent(Talent.SUFFERING_AWAY) && Dungeon.hero.buff(Charm.class) != null &&
                enemy.id() == Dungeon.hero.buff(Charm.class).object)
            speed *= 2;

        //moves 2 tiles at a time when returning to the hero
        if (state == WANDERING && defendingPos == -1){
            speed *= 2;
        }

        return speed;
    }

    @Override
    protected float attackDelay() {
        float delay = super.attackDelay();
        return augmentOffense.delayFactor(delay);
    }

    public static Char whatToFollow(Char follower, Char start) {
        Char toFollow = start;
        boolean[] passable = Dungeon.level.passable.clone();
        PathFinder.buildDistanceMap(follower.pos, passable, Integer.MAX_VALUE);//No limit on distance
        for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
            if (mob.alignment == follower.alignment &&
                    PathFinder.distance[toFollow.pos] > PathFinder.distance[mob.pos] &&
                    mob.following(toFollow)) {
                toFollow = whatToFollow(follower, mob);
            }
            else {
                return start;
            }
        }
        return toFollow;
    }

    //ported from DriedRose.java
    //minions will always move towards hero if enemies not here
    public class Wandering extends Mob.Wandering implements AiState{

        @Override
        public boolean act( boolean enemyInFOV, boolean justAlerted ) {

            //Ensure there is direct line of sight from ally to enemy, and the distance is small. This is enforced so that allies don't end up trailing behind when following hero.
            if ( enemyInFOV && !movingToDefendPos) {

                enemySeen = true;

                notice();
                alerted = true;

                state = HUNTING;
                target = enemy.pos;

            } else {

                enemySeen = false;
                Char toFollow = whatToFollow(Minion.this, Dungeon.hero);
                int oldPos = pos;
                target = defendingPos != -1 ? defendingPos : toFollow.pos;
                //always move towards the target when wandering
                if (getCloser( target)) {
                    spend( 1 / speed() );
                    if (pos == defendingPos) movingToDefendPos = false;
                    return moveSprite( oldPos, pos );
                } else {
                    //if it can't move closer to defending pos, then give up and defend current position
                    if (movingToDefendPos){
                        defendingPos = pos;
                        movingToDefendPos = false;
                    }
                    spend( TICK );
                }

            }
            return true;
        }

    }

    @Override
    public String description() {
        String d = super.description();
        float empowering = 1f;
        if (buff(Chungus.class) != null) empowering *= 1.4f;
        if (Dungeon.hero.buff(Attunement.class) != null) empowering = Attunement.empowering();
        return d + "\n\n" + Messages.get(Minion.class, "stats",
                augmentOffense.damageFactor(Math.round(minDamage*empowering)),
                      augmentOffense.damageFactor(Math.round(maxDamage*empowering)),
                    HP, HT,
                    attunement);
    }

    public void onLeaving(){}
}
