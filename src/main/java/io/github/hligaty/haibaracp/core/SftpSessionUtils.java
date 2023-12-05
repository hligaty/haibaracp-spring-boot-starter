/*
 * Copyright 2021-2023 hligaty
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.hligaty.haibaracp.core;

/**
 * Used internally by Haibaracp. Cannot be used directly in application code.
 *
 * @author hligaty
 */
public final class SftpSessionUtils {

    private SftpSessionUtils() {
    }

    public static SftpSession getSession(SftpSessionFactory factory) {
        return factory.getSftpSession();
    }

    public static void releaseSession(SftpSession session) {
        session.release();
    }

    public static boolean testSession(SftpSession session) {
        return session.test();
    }
}
