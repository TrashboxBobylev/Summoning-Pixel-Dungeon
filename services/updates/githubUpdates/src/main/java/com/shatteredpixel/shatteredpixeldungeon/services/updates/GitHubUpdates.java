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

package com.shatteredpixel.shatteredpixeldungeon.services.updates;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;

import javax.net.ssl.SSLProtocolException;
import java.util.regex.Pattern;

public class GitHubUpdates extends UpdateService {

	private static Pattern descPattern = Pattern.compile("(.*?)(\r\n|\n|\r)(\r\n|\n|\r)---", Pattern.DOTALL + Pattern.MULTILINE);
	private static Pattern versionCodePattern = Pattern.compile("internal version number: ([0-9]*)", Pattern.CASE_INSENSITIVE);

	@Override
	public boolean isUpdateable() {
		return true;
	}

	public boolean acceptSnapshots = true;

	@Override
	public void checkForUpdate(boolean useMetered, UpdateResultCallback callback) {

		if (!useMetered && !Game.platform.connectedToUnmeteredNetwork()){
			callback.onConnectionFailed();
			return;
		}

		Net.HttpRequest httpGet = new Net.HttpRequest(Net.HttpMethods.GET);
		httpGet.setUrl("https://api.github.com/repos/TrashboxBobylev/Summoning-Pixel-Dungeon/releases");
		httpGet.setHeader("Accept", "application/vnd.github.v3+json");

		Gdx.net.sendHttpRequest(httpGet, new Net.HttpResponseListener() {
			@Override
			public void handleHttpResponse(Net.HttpResponse httpResponse) {
				try {

					boolean found = false;
					String versionCode = "";
					String changelog = "";
					String versionURL = "";

					for (Bundle b : Bundle.read( httpResponse.getResultAsStream() ).getBundleArray()){
						if (((b.getString("tag_name").equals("latest") && acceptSnapshots) ||
								(!acceptSnapshots && !b.getBoolean("prerelease"))) &&
										!b.getString("name").equals(Game.version)){
							found = true;
							versionCode = b.getString("name");
							changelog = b.getString("body").substring(10);
							if (DeviceCompat.isDesktop()){
								versionURL = b.getBundleArray("assets")[1].getString("browser_download_url");
							} else {
								versionURL = b.getBundleArray("assets")[0].getString("browser_download_url");
							}
							break;
						}
					}

					if (!found){
						callback.onNoUpdateFound();
					} else {

						AvailableUpdateData update = new AvailableUpdateData();

						update.versionName = versionCode;
						update.versionCode = Game.versionCode;
						update.desc = changelog;
						update.URL = versionURL;

						callback.onUpdateAvailable(update);
					}
				} catch (Exception e) {
					Game.reportException( e );
					callback.onConnectionFailed();
				}
			}

			@Override
			public void failed(Throwable t) {
				//Failure in SSL handshake, possibly because GitHub requires TLS 1.2+.
				// Often happens for old OS versions with outdated security protocols.
				// Future update attempts won't work anyway, so just pretend nothing was found.
				if (t instanceof SSLProtocolException){
					callback.onNoUpdateFound();
				} else {
					Game.reportException(t);
					callback.onConnectionFailed();
				}
			}

			@Override
			public void cancelled() {
				callback.onConnectionFailed();
			}
		});

	}

	@Override
	public void initializeUpdate(AvailableUpdateData update) {
		DeviceCompat.openURI( update.URL );
	}

	@Override
	public boolean isInstallable() {
		return false;
	}

	@Override
	public void initializeInstall() {
		//does nothing, always installed
	}

	@Override
	public void initializeReview(ReviewResultCallback callback) {
		//does nothing, no review functionality here
		callback.onComplete();
	}
}
