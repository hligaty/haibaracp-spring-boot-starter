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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.Vector;

/**
 * Helper class that simplifies ChannelSftp use code.
 *
 * @author hligaty
 */
public class ChannelSftpWrapper {
    private static final char separatorChar = '/';
    private static final String separator = String.valueOf(separatorChar);
    private final ChannelSftp channelSftp;

    public ChannelSftpWrapper(ChannelSftp channelSftp) {
        this.channelSftp = channelSftp;
    }

    /**
     * Switch the directory. If the directory does not exist, recursively create.
     *
     * @param path the directory to switch.
     * @throws SftpException an IO exception during remote interaction.
     */
    public final void cdAndMkdir(String path) throws SftpException {
        path = Paths.get(path).normalize().toString().replace(File.separatorChar, separatorChar);
        if (path.isEmpty()) {
            return;
        }
        try {
            cd(path);
        } catch (SftpException e) {
            if (e.id != ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                throw e;
            }
            if (path.startsWith(separator)) {
                cd(separator);
            }
            String[] dirs = path.split(separator);
            for (String dir : dirs) {
                if (dir.isEmpty()) {
                    continue;
                }
                if (!isDir(dir)) {
                    mkdir(dir);
                }
                cd(dir);
            }
        }
    }

    /**
     * Tests whether a dir change.
     *
     * @param path the path to change.
     * @throws SftpException an IO exception during remote interaction.
     */
    public void cd(String path) throws SftpException {
        try {
            channelSftp.cd(path);
        } catch (SftpException e) {
            throw new SftpException(e.id, "Failed to change remote directory '" + path + "'.", e);
        }
    }

    /**
     * Tests whether a dir exists.
     *
     * @param dir the dir to test.
     * @return true if the dir is existed.
     * @throws SftpException an IO exception during remote interaction.
     */
    public boolean isDir(String dir) throws SftpException {
        try {
            return channelSftp.lstat(dir).isDir();
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            }
            throw new SftpException(e.id, "Cannot check status for dir '" + dir + "'.", e);
        }
    }

    /**
     * Create one level directory. Does not recursively create directories.
     *
     * @param path only one level of directory.
     * @throws SftpException an IO exception during remote interaction.
     */
    public void mkdir(String path) throws SftpException {
        try {
            this.channelSftp.mkdir(path);
        } catch (SftpException e) {
            if (e.id != ChannelSftp.SSH_FX_FAILURE || !exists(path)) {
                throw new SftpException(e.id, "Failed to create remote directory '" + path + "'.", e);
            }
        }
    }

    /**
     * @see SftpTemplate#exists(String)
     */
    public boolean exists(String path) throws SftpException {
        try {
            this.channelSftp.lstat(path);
            return true;
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            }
            throw new SftpException(e.id, "Cannot check status for path '" + path + "'.", e);
        }
    }

    /**
     * @see SftpTemplate#download(String, String)
     */
    public void download(String from, String to) throws SftpException {
        try {
            channelSftp.get(from, to);
        } catch (SftpException e) {
            handleDownloadSftpException(from, e);
        }
    }

    /**
     * @see SftpTemplate#download(String, OutputStream)
     */
    public void download(String from, OutputStream to) throws SftpException {
        try {
            channelSftp.get(from, to);
        } catch (SftpException e) {
            handleDownloadSftpException(from, e);
        }
    }

    private static void handleDownloadSftpException(String from, SftpException e) throws SftpException {
        if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
            throw new SftpException(e.id, "Remote file '" + from + "' not exists.", e);
        }
        throw new SftpException(e.id, "Cannot get for file '" + from + "'", e);
    }

    /**
     * @see SftpTemplate#upload(String, String)
     */
    public void upload(String from, String to) throws SftpException {
        prepareUpload(to);
        try {
            channelSftp.put(from, to.substring(to.lastIndexOf(separatorChar) + 1));
        } catch (SftpException e) {
            throw new SftpException(e.id, "Cannot put for file '" + from + "'", e);
        }
    }

    /**
     * @see SftpTemplate#upload(InputStream, String)
     */
    public void upload(InputStream from, String to) throws SftpException {
        prepareUpload(to);
        try {
            channelSftp.put(from, to.substring(to.lastIndexOf(separatorChar) + 1));
        } catch (SftpException e) {
            throw new SftpException(e.id, "Cannot put for file '" + from + "'", e);
        }
    }

    private void prepareUpload(String to) throws SftpException {
        String dir = to.substring(0, to.lastIndexOf(separatorChar) + 1);
        if (!dir.isEmpty()) {
            cdAndMkdir(dir);
        }
    }

    /**
     * @see SftpTemplate#list(String)
     */
    public ChannelSftp.LsEntry[] list(String path) throws SftpException {
        try {
            Vector<?> lsEntries = this.channelSftp.ls(path);
            if (lsEntries == null) {
                return new ChannelSftp.LsEntry[0];
            }
            ChannelSftp.LsEntry[] entries = new ChannelSftp.LsEntry[lsEntries.size()];
            for (int i = 0; i < lsEntries.size(); i++) {
                Object next = lsEntries.get(i);
                if (!(next instanceof ChannelSftp.LsEntry)) {
                    throw new IllegalStateException("Expected only LsEntry instances from channel.ls()");
                }
                entries[i] = (ChannelSftp.LsEntry) next;
            }
            return entries;
        } catch (SftpException e) {
            throw new SftpException(e.id, "Cannot to list path '" + path + "'.", e);
        }
    }

}
