package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileNotFoundException;
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
    private static final String SEPARATOR = "/";
    private final ChannelSftp channelSftp;

    public ChannelSftpWrapper(ChannelSftp channelSftp) {
        this.channelSftp = channelSftp;
    }

    /**
     * Switch the directory. If the directory does not exist, recursively create.
     *
     * @param path the directory to switch.
     * @throws SessionException an IO exception during remote interaction.
     */
    public final void cdAndMkdir(String path) throws SessionException {
        Assert.hasLength(path, "Path must not be null");
        path = Paths.get(path).normalize().toString();
        if (path.isEmpty()) {
            return;
        }
        try {
            cd(path);
        } catch (SessionException e) {
            if (e.getCause() instanceof SftpException sftpException && sftpException.id != ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                throw new SessionException("Failed to change remote directory '" + path + "'." + e.getMessage(), e);
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
     * @throws SessionException an IO exception during remote interaction.
     */
    public void cd(String path) throws SessionException {
        try {
            channelSftp.cd(path);
        } catch (SftpException e) {
            throw new SessionException("Failed to change remote directory '" + path + "'." + e.getMessage(), e);
        }
    }

    /**
     * Tests whether a dir exists.
     *
     * @param dir the dir to test.
     * @return true if the dir is existed.
     * @throws SessionException an IO exception during remote interaction.
     */
    public boolean isDir(String dir) throws SessionException {
        try {
            return channelSftp.lstat(dir).isDir();
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            }
            throw new SessionException("Cannot check status for dir '" + dir + "'." + e.getMessage(), e);
        }
    }

    /**
     * Create one level directory. Does not recursively create directories.
     *
     * @param path only one level of directory.
     * @throws SessionException an IO exception during remote interaction.
     */
    public void mkdir(String path) throws SessionException {
        try {
            this.channelSftp.mkdir(path);
        } catch (SftpException e) {
            if (e.id != ChannelSftp.SSH_FX_FAILURE || !exists(path)) {
                throw new SessionException("Failed to create remote directory '" + path + "'." + e.getMessage(), e);
            }
        }
    }

    /**
     * Check if the remote file or directory exists.
     */
    public boolean exists(String path) throws SessionException {
        try {
            this.channelSftp.lstat(path);
            return true;
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            }
            throw new SessionException("Cannot check status for path '" + path + "'." + e.getMessage(), e);
        }
    }

    /**
     * @see SftpTemplate#download(String, String)
     */
    public void download(String from, String to) throws SessionException {
        Assert.hasLength(from, "From must not be null");
        Assert.hasLength(to, "To must not be null");
        try {
            channelSftp.get(from, to);
        } catch (SftpException e) {
            handleDownloadSftpException(from, e);
        }
    }

    /**
     * @see SftpTemplate#download(String, OutputStream)
     */
    public void download(String from, OutputStream to) throws SessionException {
        Assert.hasLength(from, "From must not be null");
        Assert.notNull(to, "To must not be null");
        try {
            channelSftp.get(from, to);
        } catch (SftpException e) {
            handleDownloadSftpException(from, e);
        }
    }

    private static void handleDownloadSftpException(String from, SftpException e) {
        if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
            throw new SessionException("Remote file '" + from + "' not exists.", e);
        }
        throw new SessionException("Cannot get for file '" + from + "'", e);
    }

    /**
     * @see SftpTemplate#upload(String, String)
     */
    public void upload(String from, String to) throws SessionException {
        Assert.hasLength(from, "From must not be null");
        Assert.hasLength(to, "To must not be null");
        if (!new File(from).exists()) {
            throw new SessionException("Local file '" + from + "' not exists.", new FileNotFoundException(from));
        }
        prepareUpload(to);
        try {
            channelSftp.put(from, to.substring(to.lastIndexOf(SEPARATOR) + 1));
        } catch (SftpException e) {
            throw new SessionException("Cannot put for file '" + from + "'", e);
        }
    }

    /**
     * @see SftpTemplate#upload(InputStream, String)
     */
    public void upload(InputStream from, String to) throws SessionException {
        Assert.notNull(from, "From must not be null");
        Assert.hasLength(to, "To must not be null");
        prepareUpload(to);
        try {
            channelSftp.put(from, to.substring(to.lastIndexOf(SEPARATOR) + 1));
        } catch (SftpException e) {
            throw new SessionException("Cannot put for file '" + from + "'", e);
        }
    }

    private void prepareUpload(String to) {
        String dir = to.substring(0, to.lastIndexOf(SEPARATOR) + 1);
        if (!dir.isEmpty()) {
            cdAndMkdir(dir);
        }
    }

    /**
     * @see SftpTemplate#list(String)
     */
    public ChannelSftp.LsEntry[] list(String path) throws SessionException {
        Assert.hasLength(path, "Path must not be null");
        try {
            Vector<?> lsEntries = this.channelSftp.ls(path);
            if (lsEntries == null) {
                return new ChannelSftp.LsEntry[0];
            }
            ChannelSftp.LsEntry[] entries = new ChannelSftp.LsEntry[lsEntries.size()];
            for (int i = 0; i < lsEntries.size(); i++) {
                Object next = lsEntries.get(i);
                Assert.state(next instanceof ChannelSftp.LsEntry, "Expected only LsEntry instances from channel.ls()");
                entries[i] = (ChannelSftp.LsEntry) next;
            }
            return entries;
        } catch (SftpException e) {
            throw new SessionException("Cannot to list path '" + path + "', " + e.getMessage(), e);
        }
    }
}
