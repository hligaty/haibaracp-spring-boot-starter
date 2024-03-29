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

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.springframework.lang.Nullable;

/**
 * Callback interface for Sftp 'low level' code. To be used with {@link SftpTemplate} execution methods, often as
 * anonymous classes within a method implementation. Usually, used for chaining several operations together (
 * {@code cd/put/get etc....}.
 *
 * @param <T> the result type
 * @author hligaty
 * @see SftpTemplate
 */
@FunctionalInterface
public interface SftpCallback<T> {

    /**
     * Gets called by {@link SftpTemplate} with an active Sftp channel. Does not need to care about activating or
     * closing the channelSftp or handling exceptions.
     *
     * @param channelSftp active Sftp channel.
     * @return a result object or null if none.
     * @throws SftpException a sftp exception during remote interaction.
     */
    @Nullable
    T doInSftp(ChannelSftp channelSftp) throws SftpException;

}
