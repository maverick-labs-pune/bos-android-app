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

package net.mavericklabs.bos.realm;

import net.mavericklabs.bos.utils.AppLogger;

import io.realm.DynamicRealm;
import io.realm.RealmSchema;

public class RealmMigration implements io.realm.RealmMigration {
    private AppLogger appLogger = new AppLogger(getClass().toString());

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        final RealmSchema schema = realm.getSchema();
        appLogger.logDebug("RealmMigration oldVersion " + oldVersion);
        appLogger.logDebug("RealmMigration newVersion " + newVersion);
    }
}
