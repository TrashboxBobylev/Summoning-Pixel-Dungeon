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
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public enum HeroSubClass {

	NONE( null , 0),
	
	GLADIATOR( "gladiator" , 0),
	BERSERKER( "berserker", 1),
	
	WARLOCK( "warlock" , 3),
	BATTLEMAGE( "battlemage", 2 ),
	
	ASSASSIN( "assassin", 4),
	FREERUNNER( "freerunner", 5 ),
	
	SNIPER( "sniper", 6 ),
	WARDEN( "warden", 7 ),

	SOUL_REAVER("soul_reaver", 8),
    OCCULTIST("occultist", 9),

	NOTHING_1("no_1", 10),
	NOTHING_2("no_2", 10);
	
	private String title;
	private int iconNumber;
	
	HeroSubClass( String title, int iconNumber ) {
		this.title = title;
		this.iconNumber = iconNumber;
	}
	
	public String title() {
		return Messages.get(this, title);
	}
	
	public String desc() {
		return Messages.get(this, title+"_desc");
	}
	
	private static final String SUBCLASS	= "subClass";
	
	public void storeInBundle( Bundle bundle ) {
		bundle.put( SUBCLASS, toString() );
	}
	
	public static HeroSubClass restoreInBundle( Bundle bundle ) {
		String value = bundle.getString( SUBCLASS );
		return valueOf( value );
	}

	public Image icon(){
		return new Image(Assets.Interfaces.SUBCLASS_ICONS, iconNumber*16, 0, 16, 16);
	}
	
}
