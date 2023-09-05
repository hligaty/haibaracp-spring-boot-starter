package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Vector;

/**
 * Helper class that simplifies ChannelSftp use code.
 *
 * @author hligaty
 */
public class ChannelSftpWrapper {
    private static final String SEPARATOR = "/";
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
        Assert.hasLength(path, "path must not be null");
        path = Paths.get(path).normalize().toString();
        if (path.isEmpty()) {
            return;
        }
        try {
            cd(path);
        } catch (SftpException e) {
            if (e.id != ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                throw new SftpException(e.id, "failed to change remote directory '" + path + "'." + e.getMessage(), e.getCause());
            }
            if (path.startsWith(SEPARATOR)) {
                cd(SEPARATOR);
            }
            String[] dirs = path.split(SEPARATOR);
            for (String dir : dirs) {
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
            throw new SftpException(e.id, "failed to change remote directory '" + path + "'." + e.getMessage(), e.getCause());
        }
    }

    /**
     * Tests whether a dir exists.
     *
     * @param dir the dir to test.
     * @return true if the dir is exist.
     * @throws SftpException an IO exception during remote interaction.
     */
    public boolean isDir(String dir) throws SftpException {
        try {
            return channelSftp.lstat(dir).isDir();
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            }
            throw new SftpException(e.id, "cannot check status for dir '" + dir + "'." + e.getMessage(), e.getCause());
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
                throw new SftpException(e.id, "failed to create remote directory '" + path + "'." + e.getMessage(), e.getCause());
            }
        }
    }

    /**
     * Check if the remote file or directory exists.
     */
    public boolean exists(String path) throws SftpException {
        try {
            this.channelSftp.lstat(path);
            return true;
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            }
            throw new SftpException(e.id, "cannot check status for path '" + path + "'." + e.getMessage(), e.getCause());
        }
    }

    /**
     * @see SftpTemplate#download(String, String)
     */
    public void download(String from, String to) throws SftpException {
        Assert.hasLength(from, "from must not be null");
        Assert.hasLength(to, "to must not be null");
        try {
            channelSftp.get(from, to);
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                throw new SftpException(e.id, "remote file '" + from + "' not exists.");
            }
            throw e;
        }
    }

    /**
     * @see SftpTemplate#upload(String, String)
     */
    public void upload(String from, String to) throws SftpException {
        Assert.hasLength(from, "from must not be null");
        Assert.hasLength(to, "to must not be null");
        if (!new File(from).exists()) {
            throw new SftpException(ChannelSftp.SSH_FX_FAILURE, "local file '" + from + "' not exists.", new FileNotFoundException(from));
        }
        String dir = to.substring(0, to.lastIndexOf(SEPARATOR) + 1);
        if (!"".equals(dir)) {
            cdAndMkdir(dir);
        }
        channelSftp.put(from, to.substring(to.lastIndexOf(SEPARATOR) + 1));
    }

    /**
     * @see SftpTemplate#list(String)
     */
    public ChannelSftp.LsEntry[] list(String path) throws SftpException {
        Assert.hasLength(path, "path must not be null");
        try {
            Vector<?> lsEntries = this.channelSftp.ls(path);
            if (lsEntries == null) {
                return new ChannelSftp.LsEntry[0];
            }
            ChannelSftp.LsEntry[] entries = new ChannelSftp.LsEntry[lsEntries.size()];
            for (int i = 0; i < lsEntries.size(); i++) {
                Object next = lsEntries.get(i);
                Assert.state(next instanceof ChannelSftp.LsEntry, "expected only LsEntry instances from channel.ls()");
                entries[i] = (ChannelSftp.LsEntry) next;
            }
            return entries;
        } catch (SftpException e) {
            throw new SftpException(e.id, "failed to list files." + e.getMessage(), e.getCause());
        }
    }
}
