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

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;

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
    DOG_BREEDING(82, 3),
    NUCLEAR_RAGE(83, 3),
    SNIPER_PATIENCE(84, 3),
    ARCANE_CLOAK(85, 3),
    ARMORED_ARMADA(86, 3),
    TIMEBENDING(87, 3),
    LUST_AND_DUST(88, 3),
    TOWER_OF_POWER(89, 3),
    JUST_ONE_MORE_TILE(90, 3),
    NEVER_GONNA_GIVE_YOU_UP(114, 3),
    ASSASSINATION(115, 3),
    SPEED_SHOES(116, 3),
    BREAD_AND_CIRCUSES(117, 3),
    COMET_FALL(118, 3),
    SPYDER_MAN(119, 3),
    DETERMINED(120, 3),
    MY_SUNSHINE(121, 3),
    OLYMPIC_SKILLS(122, 3),
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

    // tiers 1/2/3/4 start at levels 2/7/13/21
    public static int[] tierLevelThresholds = new int[]{0, 2, 7, 13, 21, 31};

    Talent( int icon ){
        this(icon, 2);
    }

    Talent( int icon, int maxPoints ){
        this.icon = icon;
        this.maxPoints = maxPoints;
    }

    public int icon(){
        return icon;
    }

    public int maxPoints(){
        return maxPoints;
    }

    public String title(){
        return Messages.get(this, name() + ".title");
    }

    public String desc(){
        return Messages.get(this, name() + ".desc");
    }

    public static final int MAX_TALENT_TIERS = 3;

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

        Collections.addAll(tierTalents, WELCOME_TO_EARTH, THE_SANDSTORM, TIME_TOGETHER, DIRECTIVE, GOOD_INTENTIONS, LIFE_ON_AXIOM, LETHAL_MOMENTUM);

        for (Talent talent : tierTalents){
            talents.get(1).put(talent, 0);
        }

        tierTalents.clear();

        Collections.addAll(tierTalents, DOG_BREEDING, NUCLEAR_RAGE, SNIPER_PATIENCE, ARCANE_CLOAK, ARMORED_ARMADA, TIMEBENDING, LUST_AND_DUST, TOWER_OF_POWER, JUST_ONE_MORE_TILE, NEVER_GONNA_GIVE_YOU_UP, ASSASSINATION, SPEED_SHOES, BREAD_AND_CIRCUSES, COMET_FALL, SPYDER_MAN, DETERMINED, MY_SUNSHINE, OLYMPIC_SKILLS);
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
