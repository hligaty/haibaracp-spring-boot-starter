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
 * Defines a provider for sftp sessions.
 * <p>
 * This interface is typically used to encapsulate a native factory which returns a {@link SftpSession
 * session} of on each invocation.
 * <p>
 * Connection providers may create a new session on each invocation or return pooled instances. Each obtained
 * session must be released through its session provider to allow disposal or release back to the pool.
 *
 * @author hligaty
 * @since 2.0
 * @see SftpSession
 */
@FunctionalInterface
public interface SftpSessionProvider {

    /**
     * Request a session.
     *
     * @return the requested session. Must be {@link #release(SftpSession) released} if the session is no
     *         longer in use.
     */
    SftpSession getSftpClient();

    /**
     * Release the {@link SftpSession sftpSession}. Closes session {@link SftpSession##release(SftpSession)} by default.
     * Implementations may choose whether they override this method and return the session to a pool.
     *
     * @param sftpSession must not be {@literal null}.
     */
    default void release(SftpSession sftpSession) {
        sftpSession.disconnect();
    }
}
