/*
 * Copyright (c) 2019. Maverick Labs
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as,
 *   published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package net.mavericklabs.bos.utils;

import android.util.Log;

public class AppLogger {
    private final String className;
    public AppLogger(String className) {
        this.className = className;
    }

    public void logDebug(String message){
        Log.d(this.className,"DEBUG : "+message);
    }
    public void logError(String message){
        Log.e(this.className,"ERR : "+message);
    }
    public void logWarning(String message){
        Log.w(this.className,"WARN : "+message);
    }
    public void logInformation(String message){
        Log.i(this.className, "INFO : " + message);
    }
}
