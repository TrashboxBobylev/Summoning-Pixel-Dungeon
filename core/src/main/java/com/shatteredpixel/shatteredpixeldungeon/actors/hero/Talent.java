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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Conducts;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.powers.Wet;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.minions.Minion;
import com.shatteredpixel.shatteredpixeldungeon.effects.*;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.cloakglyphs.CloakGlyph;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.cloakglyphs.Victide;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Scrap;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.staffs.MinionBalanceTable;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.staffs.Staff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DogSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.BArray;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.gltextures.SmartTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.GameMode.HELL;

public enum Talent {

    SPECIAL_DELIVERY(18),
    COLD_FRONT(19),
    ACUTENESS(20),
    HYPERSPACE(21),
    PERDERE_CRIMEN(22),
    SCRAP_BRAIN(23),
    WELCOME_TO_EARTH(50),
    THE_SANDSTORM(51),
    TIME_TOGETHER(52),
    DIRECTIVE(53),
    GOOD_INTENTIONS(54),
    LIFE_ON_AXIOM(55),
    LETHAL_MOMENTUM(56),
    IRON_WILL(91),
    UNSIGHTED(92),
    PLAGUEBRINGER(93),
    WILD_SORCERY(94),
    TOXIC_RELATIONSHIP(95),
    DOG_BREEDING(82, 3, true),
    NUCLEAR_RAGE(83, 3, true),
    SNIPER_PATIENCE(84, 3, true),
    ARCANE_CLOAK(85, 3, true),
    ARMORED_ARMADA(86, 3),
    TIMEBENDING(87, 3, true),
    LUST_AND_DUST(88, 3, true),
    TOWER_OF_POWER(89, 3, true),
    JUST_ONE_MORE_TILE(90, 3, true),
    NEVER_GONNA_GIVE_YOU_UP(114, 3, true),
    ASSASSINATION(115, 3),
    QUICK_HANDS(116, 3, true),
    BREAD_AND_CIRCUSES(117, 3, true),
    COMET_FALL(118, 3, true),
    SUFFERING_AWAY(119, 3, true),
    DETERMINED(120, 3, true),
    MY_SUNSHINE(121, 3, true),
    OLYMPIC_SKILLS(122, 3, true),
    REAL_KNIFE_MASTER(25, 3),
    BLOOD_DRIVE(26, 3),
    UNSETTLING_GAZE(27, 3),
    SUPPORT_POTION(28, 3),
    WITCHING_STRIKE(29, 3),
    SILENCE_OF_LAMBS(30, 3),
    BLESSING_OF_SANITY(57, 3),
    GUIDANCE_FLAME(58, 3),
    SPEEDY_STEALTH(59, 3),
    THAUMATURGY(60, 3),
    SHARP_VISION(61, 3),
    CHEMISTRY_DEGREE(62, 3);

    int icon;
    int maxPoints;
    boolean t3_implemented = false;

    // tiers 1/2/3/4 start at levels 2/7/13/21
    public static int[] tierLevelThresholds = new int[]{0, 2, 7, 13, 21, 31};

    Talent( int icon ){
        this(icon, 2);
    }

    Talent( int icon, int maxPoints ){
        this.icon = icon;
        this.maxPoints = maxPoints;
    }

    Talent( int icon, int maxPoints, boolean implemented ){
        this.icon = icon;
        this.maxPoints = maxPoints;
        this.t3_implemented = implemented;
    }

    public int icon(){
        return icon;
    }

    public int maxPoints(){
        return maxPoints;
    }

    public boolean implementedYet(){
        return t3_implemented;
    }

    public String title(){
        return Messages.get(this, name() + ".title");
    }

    public String desc(){
        return Messages.get(this, name() + ".desc");
    }

    public static abstract class Cooldown extends FlavourBuff {
        public static <T extends Cooldown> void affectHero(Class<T> cls) {
            if(cls == Cooldown.class) return;
            T buff = Buff.affect(Dungeon.hero, cls);
            buff.spend( buff.duration() );
        }
        public abstract float duration();
        public float iconFadePercent() { return Math.max(0, visualcooldown() / duration()); }
        public String toString() { return Messages.get(this, "name"); }
        public String desc() { return Messages.get(this, "desc", dispTurns(visualcooldown())); }
    }
    public static class ImprovisedProjectileCooldown extends Cooldown {
        public float duration() { return Dungeon.hero.pointsInTalent(WELCOME_TO_EARTH) > 1 ? 50 : 80; }
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0.15f, 0.2f, 0.5f); }
    }
    public static class CometFallCooldown extends Cooldown {
        public float duration() {
            return 5;
        }
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0.26f, 0.41f, 0.76f); }
    }
    public static class TowerOfPowerCooldown extends Cooldown {
        public float duration() {
            return 40 - Dungeon.hero.pointsInTalent(TOWER_OF_POWER)*5;
        }
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0.7f, 0.66f, 0.63f); }
    }
    public static class TowerOfPowerDamage extends FlavourBuff{
        public static final float DURATION = 10f;
        public float iconFadePercent() {
            return Math.max(0, (DURATION - visualcooldown()) / DURATION);
        }
        public int icon() { return BuffIndicator.WEAPON; }
        public String toString() { return Messages.get(this, "name"); }
        public String desc() { return Messages.get(this, "desc", Math.floor((TowerOfPowerTracker.damageBoost()-1f)*100f), dispTurns()); }
    }
    public static class TowerOfPowerTracker extends Buff{
        public static float damageBoost() { return 0.65f + 0.35f*Dungeon.hero.pointsInTalent(TOWER_OF_POWER);}
        public String toString() { return Messages.get(this, "name"); }
        public String desc() { return Messages.get(this, "desc", Math.floor((damageBoost()-1f)*100f)); }
        public int icon() { return BuffIndicator.ARMOR_GEN; }
        public void tintIcon(Image icon) { icon.hardlight(0.7f, 0.66f, 0.63f); }

        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add(CharSprite.State.SHIELDED);
            else target.sprite.remove(CharSprite.State.SHIELDED);
        }
    }
    public static class SuckerPunchTracker extends Buff{}
    public static class AcutenessTracker extends CounterBuff{}
    public static class DirectiveTracker extends FlavourBuff{}
    public static class DirectiveMovingTracker extends CounterBuff{
        public int duration() { return Dungeon.hero.pointsInTalent(DIRECTIVE);}
        public float iconFadePercent() { return Math.max(0, 1f - ((count()) / (duration()))); }
        public String toString() { return Messages.get(this, "name"); }
        public String desc() { return Messages.get(this, "desc", dispTurns(duration() - (count()))); }
        public int icon() { return BuffIndicator.MOMENTUM; }
        public void tintIcon(Image icon) { icon.hardlight(0.15f, 0.7f, 0.5f); }
    }
    public static class LifeOnAxiomTracker extends FlavourBuff{
        public String toString() { return Messages.get(this, "name"); }
        public float iconFadePercent() { return Math.max(0, 1f - (15 / visualcooldown())); }
        public String desc() { return Messages.get(this, "desc", dispTurns(visualcooldown())); }
        public int icon() { return BuffIndicator.RAGE; }
        public void tintIcon(Image icon) { icon.hardlight(0.8f, 0.1f, 0.0f); }
    }
    public static class LethalMomentumTracker extends FlavourBuff{}
    public static class LustAndDustTracker extends Buff{}
    public static class LustAndDustDebuffTracker extends Buff{}
    public static class JustOneMoreTileTracker extends FlavourBuff{
        public String toString() { return Messages.get(this, "name"); }
        public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 13f)); }
        public String desc() { return Messages.get(this, "desc", dispTurns(visualcooldown())); }
        public int icon() { return BuffIndicator.WEAPON; }
        public void tintIcon(Image icon) { icon.hardlight(0xFFFF00); }
    }
    public static class QuickHandsWound extends Wound {
        int color;

        @Override
        public void update() {
            super.update();

            hardlight(color);
        }

        public static void hit(int pos, float angle, int color ) {
            Group parent = Dungeon.hero.sprite.parent;
            QuickHandsWound w = (QuickHandsWound)parent.recycle( QuickHandsWound.class );
            parent.bringToFront( w );
            w.reset( pos );
            w.angle = angle;
            w.color = color;
        }
    }
    public static class QuickHandsRegenTracker extends FlavourBuff {}
    public static class MySunshineTracker extends CounterBuff{
        public float iconFadePercent() { return Math.max(0, 1f - ((count()) / (timeRequired()))); }
        public String toString() { return Messages.get(this, "name"); }

        public String desc() {
            return Messages.get(this, "desc", sunCount, dispTurns(timeRequired() - (count())), timeRequired());
        }

        public int icon() { return BuffIndicator.SUNSHINE; }

        public int sunCount;

        public static final int SUN_COLOR = 0xf6ff00;

        public static int timeRequired(){
            switch (Dungeon.hero.pointsInTalent(MY_SUNSHINE)){
                case 1: case 2:
                    return 30;
                case 3:
                    return 25;
            }
            return 0;
        }

        public static int sunObtained(){
            switch (Dungeon.hero.pointsInTalent(MY_SUNSHINE)){
                case 1:
                    return 20;
                case 2:
                    return 25;
                case 3:
                    return 50;
            }
            return 0;
        }

        @Override
        public boolean act() {
            if (target.isAlive()) {
                spend( TICK );
                if (Dungeon.level.openSpace[Dungeon.hero.pos]){
                    if (target.buff(Light.class) != null){
                        SpellSprite.show(Dungeon.hero, SpellSprite.LIGHT, 0.95f, 1, 0);
                        countUp(1.5f);
                    } else {
                        SpellSprite.show(Dungeon.hero, SpellSprite.LIGHT, 1, 1, 0);
                        countUp(1);
                    }
                }
                if (count() >= timeRequired()) {
                    sunCount += sunObtained();
                    countDown(timeRequired());
                    Sample.INSTANCE.play(Assets.Sounds.CHARGEUP, 1f, 2f);
                }
            } else {
                detach();
                sunCount = 0;
            }

            return true;
        }

        private static final String SUN = "sunCount";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(SUN, sunCount);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            sunCount = bundle.getInt(SUN);
        }
    }

    public static class OlympicSkillsCooldown extends Cooldown {
        public float duration() {
            return 20;
        }
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0f, 0.7f, 0.52f); }
    }

    public static class OlympicSkillsTracker extends CounterBuff{
        private float comboTime = 0f;

        @Override
        public boolean act() {
            comboTime-=TICK;
            spend(TICK);
            if (comboTime <= 0) {
                detach();
            }
            return true;
        }

        public static final int MAX_COMBO = 3;

        public static void damageWithSparkles(Char enemy, MissileWeapon wep){
            if (enemy != null && enemy.alignment == Char.Alignment.ENEMY){
                CellEmitter.center(enemy.pos).burst(SparkParticle.FACTORY, 13);
                enemy.damage(wep.damageRoll(Dungeon.hero)/3, wep);
            }
        }

        public void hit( Char enemy, MissileWeapon weapon ) {

            countUp(1);
            comboTime = 2f*(Dungeon.hero.pointsInTalent(OLYMPIC_SKILLS));

            if (count() >= MAX_COMBO) {

                detach();
                Cooldown.affectHero(OlympicSkillsCooldown.class);

                Buff.affect(Dungeon.hero, Talent.LethalMomentumTracker.class, 1f);

                damageWithSparkles(enemy, weapon);
                for (int cel: PathFinder.NEIGHBOURS4){
                    damageWithSparkles(Actor.findChar(enemy.pos+cel), weapon);
                }

            }

            BuffIndicator.refreshHero(); //refresh the buff visually on-hit

        }

        public int icon() { return BuffIndicator.WEAPON; }
        public void tintIcon(Image icon) { icon.hardlight(0f, 0.7f, 0.52f); }
        public String toString() { return Messages.get(this, "name"); }
        public float iconFadePercent() { return Math.max(0, 1f - (MAX_COMBO / count())); }
        public String desc() { return Messages.get(this, "desc", Math.round(count())); }

        private static final String TIME = "comboTime";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(TIME, comboTime);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            comboTime = bundle.getInt(TIME);
        }
    }

    public static class DeterminedTracker extends Buff {
        @Override
        public boolean act() {
            if (target.isAlive() && (target.HP/(float)target.HT) < 0.3f) {
                spend( TICK );
            } else {
                detach();
            }

            return true;
        }

        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add(CharSprite.State.DETERMINED);
            else target.sprite.remove(CharSprite.State.DETERMINED);
        }
    }
    public static class DeterminedReviveCooldown extends Cooldown {
        public float duration() {
            return 500;
        }
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0xcc0c0c); }
    }

    public static class SniperPatienceCooldown extends Cooldown {
        public float duration() { return Dungeon.hero.pointsInTalent(SNIPER_PATIENCE) > 2 ? 25 : 20; }
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0xa12d2d); }
    }

    public static class SniperPatienceTracker extends Buff {
        {
            actPriority = HERO_PRIO+1;
        }

        private int pos;

        @Override
        public boolean attachTo( Char target ) {
            pos = target.pos;
            return super.attachTo( target );
        }

        @Override
        public boolean act() {
            if (target.pos != pos) {
                detach();
            }
            spend( TICK );
            return true;
        }

        public static float damageModifier(){
            switch (Dungeon.hero.pointsInTalent(SNIPER_PATIENCE)){
                case 1: return 1.5f;
                case 2: return 1.75f;
                case 3: return 2.0f;
            }
            return 1f;
        }

        public String toString() { return Messages.get(this, "name"); }
        public String desc() {
            return String.format("%s\n\n%s\n\n%s",
                    Messages.get(this, "desc"),
                    Messages.get(this, "add_desc" + Dungeon.hero.pointsInTalent(SNIPER_PATIENCE)),
                    Messages.get(this, "desc_detach"));
        }
        public int icon() { return BuffIndicator.SNIPER_PAT; }

        private static final String POS		= "pos";

        @Override
        public void storeInBundle( Bundle bundle ) {
            super.storeInBundle( bundle );
            bundle.put( POS, pos );
        }
        @Override
        public void restoreFromBundle( Bundle bundle ) {
            super.restoreFromBundle( bundle );
            pos = bundle.getInt( POS );
        }
    }

    public static class BreadAndCircusesCounter extends CounterBuff{
        public static int mobsForFood(int points){
            return points < 2 ? 20 : 15;
        }
    }

    public static class BreadAndCircusesStatTracker extends Buff{
        public int defense() {
            Hunger baseBuff = target.buff(Hunger.class);
            float progress = Math.min(1f, baseBuff.hunger() / Hunger.HUNGRY);
            return (int) Math.ceil(8 * (1f - progress));
        }
        public String toString() { return Messages.get(this, "name"); }
        public String desc() { return Messages.get(this, "desc", defense()); }
        public int icon() { return BuffIndicator.BREAD_CIRCUS; }
        public void tintIcon(Image icon) {
            Hunger baseBuff = target.buff(Hunger.class);
            if (!baseBuff.isHungry()){
                float progress = (baseBuff.hunger()/Hunger.HUNGRY);
                icon.hardlight(0f + 0.949f*progress, 0.7f - 0.25f*progress, 0f + 0.09f*progress);
            } else
                icon.hardlight(0.95f, 0.45f, 0.09f);
        }
    }
    public static class DogBreedingDeathRefusal extends Buff {

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target) && target instanceof Minion){
                ((Minion) target).timer = 9999;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean act() {
            if (target.HP != target.HT){
                spend(TICK);
            } else {
                detach();
                ((Minion)target).timer = 0;
                Sample.INSTANCE.play(Assets.Sounds.CHARGEUP, 1f, 3f);
                SpellSprite.show(Dungeon.hero, SpellSprite.CHARGE, 1, 0, 0);
            }
            return true;
        }

        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add(CharSprite.State.DARKENED);
            else target.sprite.remove(CharSprite.State.DARKENED);
        }
    }
    public static class DogBreedingMarking extends DummyBuff {
        {
            type = buffType.NEGATIVE;
            severity = buffSeverity.HARMFUL;
        }

        @Override
        public int icon() {
            return BuffIndicator.MARK;
        }

        @Override
        public String desc() {
            if (Dungeon.hero.pointsInTalent(DOG_BREEDING) == 3)
                return Messages.get(this, "desc_armor_ignore", dispTurns());
            return Messages.get(this, "desc", dispTurns());
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.8f, 0f, 0f);
        }
    }
    public static class DogBreedingMinion extends Minion{
        {
            spriteClass = DogSprite.class;
            baseDefense = 4;
            baseSpeed = 1.5f;
        }

        @Override
        protected boolean act() {
            if (DogBreedingStaff.staffInstance == null)
                DogBreedingStaff.staffInstance = new DogBreedingStaff();
            return super.act();
        }

        @Override
        protected float attackDelay() {
            float mod = 0;
            switch (lvl){
                case 0: mod = 1; break;
                case 1: case 2: mod = 0.66f; break;
            }
            return super.attackDelay() * mod;
        }

        @Override
        public int attackProc(Char enemy, int damage) {
            DogBreedingStaff.staffInstance.customizeMinion(this);
            if (lvl > 0){
                Buff.affect(enemy, DogBreedingMarking.class, attackDelay()*2.5f);
            }
            if (enemy.properties().contains(Property.ANIMAL)){
                HP = Math.min(HT, HP + minDamage);
                sprite.emitter().burst( Speck.factory( Speck.HEALING ), minDamage / 3 );
            }
            return super.attackProc(enemy, damage);
        }

        @Override
        public int defenseProc(Char enemy, int damage) {
            DogBreedingStaff.staffInstance.customizeMinion(this);
            return super.defenseProc(enemy, damage);
        }

        @Override
        public boolean interact(Char c) {
            if (DogBreedingStaff.staffInstance == null)
                DogBreedingStaff.staffInstance = new DogBreedingStaff();
            DogBreedingStaff.staffInstance.customizeMinion(this);
            return super.interact(c);
        }

        @Override
        protected boolean canAttack(Char enemy) {
            return super.canAttack(enemy) && buff(DogBreedingDeathRefusal.class) == null;
        }

        public int foodHealing(){
            switch (lvl){
                case 0: return 20;
                case 1: return 35;
                case 2: return 50;
            }
            return 0;
        }
    }
    public static class DogBreedingStaff extends Staff{

        public static DogBreedingStaff staffInstance;

        {
            table = MinionBalanceTable.DOG_BREEDING;
            minionType = DogBreedingMinion.class;
            setClass(Minion.MinionClass.MELEE);
        }

        @Override
        public int STRReq() {return 0;}

        @Override
        public int level() {
            return Dungeon.hero.pointsInTalent(DOG_BREEDING)-1;
        }

        public int minionMin(int lvl) {
            int dmg = 0;
            int ownerLvl = Math.max(0, Dungeon.hero.lvl - 12);
            switch (lvl) {
                case 0: case 1: dmg = 6 + Math.round(ownerLvl * 0.5f); break;
                case 2: dmg = 9 + Math.round(ownerLvl * 1f); break;
            }
            if (Dungeon.isChallenged(Conducts.Conduct.PACIFIST)) dmg /= 3;
            return dmg;
        }

        public int minionMax(int lvl) {
            int dmg = 0;
            int ownerLvl = Math.max(0, Dungeon.hero.lvl - 12);
            switch (lvl) {
                case 0: case 1: dmg = 18 + ownerLvl * 1; break;
                case 2: dmg = 27 + Math.round(ownerLvl * 1.5f); break;
            }
            if (Dungeon.isChallenged(Conducts.Conduct.PACIFIST)) dmg /= 3;
            return dmg;
        }

        //use this method to update dog's stats
        @Override
        public void customizeMinion(Minion minion) {
            super.customizeMinion(minion);
            minion.enchantment = enchantment;
            minion.augmentOffense = augment;
            minion.lvl = level();
            minion.setDamage(minionmin(), minionmax());
            minion.minionClass = minionClass;
            minion.attunement = requiredAttunement();
            minion.HT = hp(level());
        }
    }

    public enum TimebendingActions {
        ATTACKING, WALKING, SPEED_SEED, NOTHING
    }

    //TODO: interfaces are kind of inflexible, but I still should move some of time freeze copying into TimeFreezing
    public static class TimebendingTimeStop extends Buff implements TimeFreezing {

        {
            type = buffType.POSITIVE;
            announced = true;
        }

        private float left;
        ArrayList<Integer> presses = new ArrayList<>();

        @Override
        public int icon() {
            return BuffIndicator.TIME;
        }

        @Override
        public void tintIcon(Image icon) {icon.hardlight(1f, 0.19f, 0.07f);}

        @Override
        public float iconFadePercent() {
            return Math.max(0, (TimebendingCounter.MAX_TIME - left) / TimebendingCounter.MAX_TIME);
        }

        public void reset(float time){
            left = time;
        }

        @Override
        public String toString() {
            return Messages.get(this, "name");
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", dispTurns(left));
        }

        public void processTime(float time){
            left -= time;

            //use 1/1,000 to account for rounding errors
            if (left < -0.001f){
                detach();
            }

        }

        public void setDelayedPress(int cell){
            if (!presses.contains(cell))
                presses.add(cell);
        }

        private void triggerPresses(){
            for (int cell : presses)
                Dungeon.level.pressCell(cell);

            presses = new ArrayList<>();
        }

        @Override
        public void detach(){
            super.detach();
            triggerPresses();
            target.next();
        }

        @Override
        public void fx(boolean on) {
            TimeFreezing.doEffect(on);
        }

        private static final String PRESSES = "presses";
        private static final String LEFT = "left";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);

            int[] values = new int[presses.size()];
            for (int i = 0; i < values.length; i ++)
                values[i] = presses.get(i);
            bundle.put( PRESSES , values );

            bundle.put( LEFT, left);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);

            int[] values = bundle.getIntArray( PRESSES );
            for (int value : values)
                presses.add(value);

            left = bundle.getFloat(LEFT);
        }

    }

    public static class TimebendingCounter extends CounterBuff implements ActionIndicator.Action {
        public int timeCount;

        public static int energyRequired(){
            switch (Dungeon.hero.pointsInTalent(TIMEBENDING)){
                case 1:
                    return 120;
                case 2:
                    return 100;
                case 3:
                    return 80;
            }
            return 0;
        }

        public void investEnergy(TimebendingActions type){
            int howMuch = 0;
            switch (type){
                case ATTACKING:
                    howMuch = 6;
                    break;
                case WALKING:
                    howMuch = 2;
                    break;
                case SPEED_SEED:
                    howMuch = 4*energyRequired();
                    break;

                case NOTHING:
                    howMuch = 1;
                    break;
            }
            countUp(howMuch);
        }

        @Override
        public boolean act() {
            if (target.isAlive()) {
                spend( TICK );

                while (count() >= energyRequired() && timeCount < MAX_TIME) {
                    timeCount++;
                    countDown(energyRequired());
                    Sample.INSTANCE.play(Assets.Sounds.CHARGEUP, 1f, 2f);
                }
                if (timeCount > 0)
                    ActionIndicator.setAction(TimebendingCounter.this);
                else
                    ActionIndicator.clearAction(TimebendingCounter.this);
            } else {
                detach();
                timeCount = 0;
            }

            return true;
        }

        public float iconFadePercent() { return Math.max(0, 1f - ((count()) / (energyRequired()))); }
        public String toString() { return Messages.get(this, "name"); }

        public String desc() {
            return Messages.get(this, "desc", timeCount, dispTurns(count()), energyRequired());
        }

        @Override
        public int icon() {
            return BuffIndicator.TIME;
        }

        public static final float MAX_TIME = 15f;

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1f, 0.98f - 0.79f*(count()/MAX_TIME), 0.57f - 0.5f*(count()/MAX_TIME));
        }

        @Override
        public void doAction() {
            GLog.i( Messages.get(TimekeepersHourglass.class, "onfreeze") );
            GameScene.flash(0xff4600);
            Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
            Sample.INSTANCE.play(Assets.Sounds.BLAST);
            Buff.affect(target, TimebendingTimeStop.class).reset(timeCount);
            timeCount = 0;
        }

        @Override
        public Image getIcon() {
            SmartTexture icons = TextureCache.get( Assets.Interfaces.TALENT_ICONS );
            TextureFilm film = new TextureFilm( icons, 16, 16 );
            Image buffIcon = new Image( icons );
            buffIcon.frame( film.get(TIMEBENDING.icon()) );
            return buffIcon;
        }

        @Override
        public boolean usable() {
            return timeCount > 0;
        }

        private static final String COUNT = "timeCount";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(COUNT, timeCount);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            timeCount = bundle.getInt(COUNT);
        }
    }

    public static final int MAX_TALENT_TIERS = 3;

    public static boolean canSufferAway(Hero hero, Buff debuff){
        if (debuff.target instanceof Hero && debuff.type == Buff.buffType.NEGATIVE && hero.hasTalent(SUFFERING_AWAY)){
            if (debuff.severity == Buff.buffSeverity.NOTHING) return false;

            if (debuff.severity == Buff.buffSeverity.HARMFUL) return true;

            if (debuff.severity == Buff.buffSeverity.DAMAGING)
                return hero.pointsInTalent(SUFFERING_AWAY) >= 2;
        }
        return false;
    }

    public static void onTalentUpgraded( Hero hero, Talent talent ){
        if (talent == BREAD_AND_CIRCUSES){
            Buff.affect(hero, BreadAndCircusesCounter.class);
            if (hero.pointsInTalent(BREAD_AND_CIRCUSES) == 3)
                Buff.affect(hero, BreadAndCircusesStatTracker.class);
        }
        if (talent == TIMEBENDING){
            Buff.affect(hero, TimebendingCounter.class);
        }
        if (talent == DOG_BREEDING){
            boolean dogExists = false;
            for (Char ch: Dungeon.level.mobs) {
                if (ch instanceof DogBreedingMinion){
                    dogExists = true;
                    break;
                }
            }
            if (!dogExists){
                try {
                    new DogBreedingStaff().summon(hero);
                } catch (Exception e) {
                    ShatteredPixelDungeon.reportException(e);
                }
            }
        }
    }

    public static int onAttackProc(Hero hero, Char enemy, int damage){
        if (hero.hasTalent(Talent.COLD_FRONT)
                && enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)
                && enemy.buff(SuckerPunchTracker.class) == null){
            int bonus = hero.pointsInTalent(COLD_FRONT)*2;
            Buff.affect(enemy, FrostBurn.class).reignite(enemy, bonus);
            Buff.affect(enemy, SuckerPunchTracker.class);
        }
        if (hero.hasTalent(Talent.JUST_ONE_MORE_TILE)
                && enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)
                && hero.buff(JustOneMoreTileTracker.class) == null){
            int duration = 1 + hero.pointsInTalent(JUST_ONE_MORE_TILE)*4;
            Buff.affect(hero, JustOneMoreTileTracker.class, duration);
        }
        if (hero.buff(CloakOfShadows.cloakStealth.class) != null
                && enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)
                && enemy.isWet()
                && hero.buff(CloakOfShadows.cloakStealth.class).glyph() instanceof Victide){
            float bonus = 0f;
            if (Dungeon.level.water[enemy.pos]){
                bonus = 1f;
                if (enemy.buff(Wet.class) != null)
                    bonus = 2f;
            }
            if (enemy.buff(Wet.class) != null){
                bonus = 1f;
                if (Dungeon.level.water[enemy.pos])
                    bonus = 2f;
            }
            damage *= Math.max(1f, Math.pow(1.3f, bonus*CloakGlyph.efficiency()));
        }
        if (hero.hasTalent(Talent.DIRECTIVE)
                && enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)
                && enemy.buff(DirectiveTracker.class) == null){
            Actor.addDelayed(new Actor() {
                @Override
                protected boolean act() {
                    Buff.count(hero, DirectiveMovingTracker.class, -1);
                    diactivate();
                    return true;
                }
            }, 0);

            Buff.affect(enemy, DirectiveTracker.class);
        }
        if (hero.hasTalent(SCRAP_BRAIN) && Dungeon.hero.belongings.weapon instanceof MissileWeapon &&
            enemy.HP - damage <= 0 && Random.Int(3) == 0){
            Dungeon.level.drop(new Scrap(), enemy.pos).sprite.drop();
        }
        if (hero.pointsInTalent(TIMEBENDING) > 2 && Dungeon.hero.belongings.weapon instanceof Shuriken &&
            hero.buff(TimebendingTimeStop.class) != null){
            float energy = hero.buff(TimebendingTimeStop.class).left;
            enemy.spend(energy*0.75f);

            //the icon for timebending
            SmartTexture icons = TextureCache.get( Assets.Interfaces.TALENT_ICONS );
            TextureFilm film = new TextureFilm( icons, 16, 16 );
            Image buffIcon = new Image( icons );
            buffIcon.frame( film.get(TIMEBENDING.icon()) );

            Sample.INSTANCE.play(Assets.Sounds.LIGHTNING, 1f, 0.6666f);
            Sample.INSTANCE.play(Assets.Sounds.BLAST, 1f, 0.6666f);

            Transmuting.show(enemy, new ItemSprite(Dungeon.hero.belongings.weapon), buffIcon);
        }
        if (hero.buff(Talent.SniperPatienceTracker.class) != null && hero.belongings.weapon instanceof MissileWeapon){
            switch (Dungeon.hero.pointsInTalent(Talent.SNIPER_PATIENCE)){
                case 2:
                    Buff.affect(enemy, Slow.class, 4f); break;
                case 3:
                    Buff.affect(enemy, Slow.class, 6f);
                    Buff.affect(enemy, StoneOfAggression.Aggression.class, 10f);
                    PathFinder.buildDistanceMap( enemy.pos, BArray.not( Dungeon.level.solid, null ), 2 );
                    for (int i = 0; i < PathFinder.distance.length; i++) {
                        if (PathFinder.distance[i] < Integer.MAX_VALUE) {
                            Char ch = Actor.findChar(i);
                            if (ch != null && ch.alignment == Char.Alignment.ENEMY){
                                ch.damage(1 + Dungeon.chapterNumber(), new WandOfBlastWave());
                            }
                            CellEmitter.get(i).burst(Speck.factory(Speck.SMOKE_DUST, true), Random.Int(4, 8));
                        }
                    }
                    Sample.INSTANCE.play(Assets.Sounds.BLAST);
                    break;
            }
            Buff.detach(Dungeon.hero, Talent.SniperPatienceTracker.class);
            Talent.Cooldown.affectHero(Talent.SniperPatienceCooldown.class);
        }
        return damage;
    }

    public static void onItemCollected( Hero hero, Item item ) {
        boolean id = false;
        if (item.isIdentified()) return;
        if (hero.hasTalent(ACUTENESS)){
            AcutenessTracker buff = Buff.affect(hero, AcutenessTracker.class);
            buff.countUp(1);
            if (buff.count() >= 12 - hero.pointsInTalent(ACUTENESS)*3 && !item.collected){
                item.identify();
                id = true;
                buff.detach();
            }
        }
        if(id && hero.sprite.emitter() != null) hero.sprite.emitter().burst(Speck.factory(Speck.QUESTION),1);
    }

    public static void onItemIdentified( Hero hero, Item item ){

    }

    public static void onFoodEaten( Hero hero, float foodVal, Item foodSource ){
        if (hero.hasTalent(BREAD_AND_CIRCUSES)){
            Buff.affect(hero, Adrenaline.class, 2);
            if (hero.pointsInTalent(BREAD_AND_CIRCUSES) == 2){
                ArtifactRecharge buff = Buff.affect( hero, ArtifactRecharge.class);
                if (buff.left() < 4){
                    Buff.affect( hero, ArtifactRecharge.class).set(4).ignoreHornOfPlenty = foodSource instanceof HornOfPlenty;
                }
                ScrollOfRecharging.charge( hero );
                SpellSprite.show(hero, SpellSprite.CHARGE, 0, 1, 1);
            }
            if (hero.pointsInTalent(BREAD_AND_CIRCUSES) == 3){
                for (Buff b : hero.buffs()){
                    if (b instanceof Artifact.ArtifactBuff && !((Artifact.ArtifactBuff) b).isCursed() ) {
                        ((Artifact.ArtifactBuff) b).charge(hero, 5f);
                    }
                }
                if (Dungeon.mode != HELL) Buff.affect(hero, FoodRegen.class).fullHP = 20;
            }
        }
        if (hero.hasTalent(DOG_BREEDING)){
            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                int p = hero.pos + PathFinder.NEIGHBOURS8[i];
                if (Actor.findChar( p ) instanceof DogBreedingMinion) {
                    DogBreedingMinion dog = (DogBreedingMinion) Actor.findChar( p );
                    dog.HP = Math.min(dog.HT, dog.HP + Math.round(dog.foodHealing()*(foodVal/550f)));
                    dog.sprite.emitter().burst( Speck.factory( Speck.HEALING ), dog.foodHealing() / 5 );
                }
            }
        }
    }

    public static void initClassTalents( Hero hero ){
        initClassTalents( hero.heroClass, hero.talents );
    }

    public static void initClassTalents( HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
        while (talents.size() < MAX_TALENT_TIERS){
            talents.add(new LinkedHashMap<>());
        }

        ArrayList<Talent> tierTalents = new ArrayList<>();

        Collections.addAll(tierTalents, SPECIAL_DELIVERY, COLD_FRONT, ACUTENESS, HYPERSPACE, PERDERE_CRIMEN, SCRAP_BRAIN);

        for (Talent talent : tierTalents){
            talents.get(0).put(talent, 0);
        }

        tierTalents.clear();

        Collections.addAll(tierTalents, WELCOME_TO_EARTH, THE_SANDSTORM, TIME_TOGETHER, DIRECTIVE, GOOD_INTENTIONS, LIFE_ON_AXIOM, LETHAL_MOMENTUM, IRON_WILL, UNSIGHTED, PLAGUEBRINGER, WILD_SORCERY, TOXIC_RELATIONSHIP);

        for (Talent talent : tierTalents){
            talents.get(1).put(talent, 0);
        }

        tierTalents.clear();

        Collections.addAll(tierTalents, DOG_BREEDING, NUCLEAR_RAGE, SNIPER_PATIENCE, ARCANE_CLOAK, ARMORED_ARMADA, TIMEBENDING, LUST_AND_DUST, TOWER_OF_POWER, JUST_ONE_MORE_TILE, NEVER_GONNA_GIVE_YOU_UP, ASSASSINATION, QUICK_HANDS, BREAD_AND_CIRCUSES, COMET_FALL, SUFFERING_AWAY, DETERMINED, MY_SUNSHINE, OLYMPIC_SKILLS);
       for (Talent talent : tierTalents){
            talents.get(2).put(talent, 0);
        }

        tierTalents.clear();

    }

    public static void initSubclassTalents( Hero hero ){
        initSubclassTalents( hero.subClass, hero.talents );
    }

    public static void initSubclassTalents( HeroSubClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
        if (cls == HeroSubClass.NONE) return;

        while (talents.size() < MAX_TALENT_TIERS){
            talents.add(new LinkedHashMap<>());
        }

        ArrayList<Talent> tierTalents = new ArrayList<>();
        switch (cls){
            case ASSASSIN:
                Collections.addAll(tierTalents, REAL_KNIFE_MASTER, BLOOD_DRIVE, UNSETTLING_GAZE, SUPPORT_POTION, WITCHING_STRIKE, SILENCE_OF_LAMBS);
                break;
            case FREERUNNER:
                Collections.addAll(tierTalents, BLESSING_OF_SANITY, GUIDANCE_FLAME, SPEEDY_STEALTH, THAUMATURGY, SHARP_VISION, CHEMISTRY_DEGREE);
                break;
        }

        for (Talent talent : tierTalents){
            talents.get(2).put(talent, 0);
        }
        tierTalents.clear();
    }

    private static final String TALENT_TIER = "talents_tier_";

    public static void storeTalentsInBundle( Bundle bundle, Hero hero ){
        for (int i = 0; i < MAX_TALENT_TIERS; i++){
            LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
            Bundle tierBundle = new Bundle();

            for (Talent talent : tier.keySet()){
                if (tier.get(talent) > 0){
                    tierBundle.put(talent.name(), tier.get(talent));
                }
                if (tierBundle.contains(talent.name())){
                    tier.put(talent, Math.min(tierBundle.getInt(talent.name()), talent.maxPoints()));
                }
            }
            bundle.put(TALENT_TIER+(i+1), tierBundle);
        }
    }

    public static void restoreTalentsFromBundle( Bundle bundle, Hero hero ){
        if (hero.heroClass != null) initClassTalents(hero);
        if (hero.subClass != null)  initSubclassTalents(hero);

        for (int i = 0; i < MAX_TALENT_TIERS; i++){
            LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
            Bundle tierBundle = bundle.contains(TALENT_TIER+(i+1)) ? bundle.getBundle(TALENT_TIER+(i+1)) : null;
            //pre-0.9.1 saves
            if (tierBundle == null && i == 0 && bundle.contains("talents")){
                tierBundle = bundle.getBundle("talents");
            }

            if (tierBundle != null){
                for (Talent talent : tier.keySet()){
                    if (tierBundle.contains(talent.name())){
                        tier.put(talent, Math.min(tierBundle.getInt(talent.name()), talent.maxPoints()));
                    }
                }
            }
        }
    }
}
