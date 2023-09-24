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

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.springframework.lang.Nullable;

import java.io.IOException;

/**
 * Callback interface for Session 'low level' code. To be used with {@link SftpTemplate} execution methods, often as
 * anonymous classes within a method implementation. Usually, used for chaining several operations of {@link SftpSession}
 * or its subclasses together ( {@code jschSession/channelSftp etc...}.
 *
 * @param <T> the result type
 * @author hligaty
 * @see SftpTemplate
 */
@FunctionalInterface
public interface SessionCallback<S extends SftpSession, T> {

    /**
     * Gets called by {@link SftpTemplate} with an active Sftp session. Does not need to care about activating or
     * closing the connection or handling exceptions.
     *
     * @param sftpSession active Sftp session.
     * @return a result object or null if none.
     * @throws SftpException a sftp exception during remote interaction.
     * @throws JSchException a sftp session exception during remote interaction.
     * @throws IOException a sftp session exception during remote interaction.
     */
    @Nullable
    T doInSession(S sftpSession) throws SftpException, JSchException, IOException;

}
