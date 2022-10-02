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

package com.shatteredpixel.shatteredpixeldungeon;

import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PlatformSupport;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ShatteredPixelDungeon extends Game {

	//variable constants for specific older versions of shattered, used for data conversion
	//versions older than v1.1.5 are no longer supported, and data from them is ignored
	public static final int v1_2_3 = 422;
	public static final int v1_1_5 = 390;
	public static final int v0_7_3b = 349;
	public static final int v0_7_4c = 362;
	public static final int v0_7_5e = 382;
	
	public ShatteredPixelDungeon( PlatformSupport platform ) {
		super( sceneClass == null ? TitleScene.class : sceneClass, platform );

		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts.SubtilitasSigil.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAccuracy" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts.MirrorOfFates.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEvasion" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts.FuelContainer.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts.ParchmentOfElbereth.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts.HeavyFlail.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts.MomentumBoots.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts.MomentumBoots.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfHaste" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts.SilkyQuiver.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ringartifacts.BadgeOfBravery.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth" );
		com.watabou.utils.Bundle.addAlias(
				com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose.class,
				"com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAttunement" );
	}
	
	@Override
	public void create() {
		super.create();

		updateSystemUI();
		SPDAction.loadBindings();

		Music.INSTANCE.enable( SPDSettings.music() );
		Music.INSTANCE.volume( SPDSettings.musicVol()*SPDSettings.musicVol()/100f );
		Sample.INSTANCE.enable( SPDSettings.soundFx() );
		Sample.INSTANCE.volume( SPDSettings.SFXVol()*SPDSettings.SFXVol()/100f );

		Sample.INSTANCE.load( Assets.Sounds.all );
		
	}

	public static void switchNoFade(Class<? extends PixelScene> c){
		switchNoFade(c, null);
	}

	public static void switchNoFade(Class<? extends PixelScene> c, SceneChangeCallback callback) {
		PixelScene.noFade = true;
		switchScene( c, callback );
	}
	
	public static void seamlessResetScene(SceneChangeCallback callback) {
		if (scene() instanceof PixelScene){
			((PixelScene) scene()).saveWindows();
			switchNoFade((Class<? extends PixelScene>) sceneClass, callback );
		} else {
			resetScene();
		}
	}
	
	public static void seamlessResetScene(){
		seamlessResetScene(null);
	}
	
	@Override
	protected void switchScene() {
		super.switchScene();
		if (scene instanceof PixelScene){
			((PixelScene) scene).restoreWindows();
		}
	}
	
	@Override
	public void resize( int width, int height ) {
		if (width == 0 || height == 0){
			return;
		}

		if (scene instanceof PixelScene &&
				(height != Game.height || width != Game.width)) {
			PixelScene.noFade = true;
			((PixelScene) scene).saveWindows();
		}

		super.resize( width, height );

		updateDisplaySize();

	}

	@Override
	public void destroy(){
		super.destroy();
		GameScene.endActorThread();
	}

	public void updateDisplaySize(){
		platform.updateDisplaySize();
	}

	public static void updateSystemUI() {
		platform.updateSystemUI();
	}

    public static void logSomething( Object obj ){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.flush();
        Gdx.app.error("GAME", sw.toString() + obj.toString());
    }
}