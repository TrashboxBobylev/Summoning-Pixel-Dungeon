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
    ECHERS(10),
    ECHEREST(20);

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

    public static final int MAX_TALENT_TIERS = 1;

    public static void initClassTalents( Hero hero ){
        initClassTalents( hero.heroClass, hero.talents );
    }

    public static void initClassTalents( HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
        while (talents.size() < MAX_TALENT_TIERS){
            talents.add(new LinkedHashMap<>());
        }

        ArrayList<Talent> tierTalents = new ArrayList<>();

        Collections.addAll(tierTalents, SPECIAL_DELIVERY, COLD_FRONT, ACUTENESS, HYPERSPACE, PERDERE_CRIMEN);

        for (Talent talent : tierTalents){
            talents.get(0).put(talent, 0);
        }

        tierTalents.clear();

//        Collections.addAll(tierTalents, ECHERS);
//
//        for (Talent talent : tierTalents){
//            talents.get(1).put(talent, 0);
//        }
//
//        tierTalents.clear();
//
//        Collections.addAll(tierTalents, ECHEREST);
//
//        for (Talent talent : tierTalents){
//            talents.get(2).put(talent, 0);
//        }
//
//        tierTalents.clear();

    }

    public static void initSubclassTalents( Hero hero ){
        initSubclassTalents( hero.subClass, hero.talents );
    }

    public static void initSubclassTalents( HeroSubClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
//        if (cls == HeroSubClass.NONE) return;
//
//        while (talents.size() < MAX_TALENT_TIERS){
//            talents.add(new LinkedHashMap<>());
//        }
//
//        ArrayList<Talent> tierTalents = new ArrayList<>();
//
//
//        for (Talent talent : tierTalents){
//            talents.get(2).put(talent, 0);
//        }
//        tierTalents.clear();
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
