package io.github.hligaty.haibaracp.core;

import com.jcraft.jsch.ChannelSftp.LsEntry;
import org.springframework.util.Assert;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author hligaty
 */
public class SftpTemplate {
    
    private final SftpSessionFactory sftpSessionFactory;

    public SftpTemplate(SftpSessionFactory sftpSessionFactory) {
        this.sftpSessionFactory = sftpSessionFactory;
    }

    /**
     * Executes the given action object within a connection, which can be exposed or not.
     *
     * @param action callback object that specifies the Sftp action.
     * @param <T>    return type
     * @return object returned by the action.
     * @throws SessionException a sftp exception during remote interaction.
     */
    public <T> T execute(SftpCallback<T> action) throws SessionException {
        return executeSession(sftpSession -> action.doInSftp(sftpSession.channelSftp()));
    }

    /**
     * Executes the given action object within a connection, which can be exposed or not.
     *
     * @param action callback object that specifies the Sftp action.
     * @throws SessionException an IO exception during remote interaction.
     */
    public void executeWithoutResult(SftpCallbackWithoutResult action) throws SessionException {
        Assert.notNull(action, "Callback object must not be null");
        this.execute(channelSftp -> {
            action.doInSftp(channelSftp);
            return null;
        });
    }

    @SuppressWarnings("unchecked")
    public <S extends SftpSession, T> T executeSession(SessionCallback<S, T> action) throws SessionException {
        Assert.notNull(action, "Callback object must not be null");
        SftpSession sftpSession = sftpSessionFactory.getSftpSession();
        try {
            return action.doInSession((S) sftpSession);
        } finally {
            sftpSession.release();
        }
    }

    @SuppressWarnings("unchecked")
    public <S extends SftpSession> void executeSessionWithoutResult(SessionCallbackWithoutResult<S> action) throws SessionException {
        Assert.notNull(action, "Callback object must not be null");
        this.executeSession(sftpSession -> {
            action.doInSession((S) sftpSession);
            return null;
        });
    }

    /**
     * Download file.
     * Support relative path and absolute path: "/home/haibara/aptx4869.docx" or "aptx4869.docx".
     *
     * @param from the path to the remote file.
     * @param to   the path to the local file.
     * @throws SessionException an IO exception during remote interaction or file not found.
     */
    public void download(String from, String to) {
        this.executeWithoutResult(channelSftp -> new ChannelSftpWrapper(channelSftp).download(from, to));
    }

    /**
     * Download file.
     * Support relative path and absolute path: "/home/haibara/aptx4869.docx" or "aptx4869.docx".
     *
     * @param from the path to the remote file.
     * @param to   the outputStream to the local file.
     * @throws SessionException an IO exception during remote interaction or file not found.
     */
    public void download(String from, OutputStream to) {
        this.executeWithoutResult(channelSftp -> new ChannelSftpWrapper(channelSftp).download(from, to));
    }

    /**
     * Upload file. Create recursively when remote directory does not exist.
     *
     * @param from the path to the local file.
     * @param to   the path to the remote file.
     * @throws SessionException an IO exception during remote interaction or file not found.
     */
    public void upload(String from, String to) {
        this.executeWithoutResult(channelSftp -> new ChannelSftpWrapper(channelSftp).upload(from, to));
    }

    /**
     * Upload file. Create recursively when remote directory does not exist.
     *
     * @param from the inputStream to the local file.
     * @param to   the path to the remote file.
     * @throws SessionException an IO exception during remote interaction or file not found.
     */
    public void upload(InputStream from, String to) {
        this.executeWithoutResult(channelSftp -> new ChannelSftpWrapper(channelSftp).upload(from, to));
    }

    /**
     * Check if the remote file or directory exists.
     *
     * @param path the remote path.
     * @return true if remote path exists.
     * @throws SessionException an IO exception during remote interaction.
     */
    public boolean exists(String path) {
        return this.execute(channelSftp -> new ChannelSftpWrapper(channelSftp).exists(path));
    }

    /**
     * View a list of files or directories. Lists are not recursive.
     *
     * @param path the remote path.
     * @return file list.
     * @throws SessionException an IO exception during remote interaction or path not found.
     */
    public LsEntry[] list(String path) {
        return this.execute(channelSftp -> new ChannelSftpWrapper(channelSftp).list(path));
    }
}
