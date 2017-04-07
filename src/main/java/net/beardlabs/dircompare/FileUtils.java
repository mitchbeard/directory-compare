package net.beardlabs.dircompare;

import com.google.common.io.BaseEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public final class FileUtils {

    private final static Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static final String computeMd5(Path file, int byteBufferSize) {
        try {
            InputStream is = Files.newInputStream(file, StandardOpenOption.READ);
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] bytes = new byte[byteBufferSize];
            int numBytes;
            while ((numBytes = is.read(bytes)) != -1) {
                md.update(bytes, 0, numBytes);
            }

            byte[] digest = md.digest();
            return BaseEncoding.base64().encode(digest);
        }
        catch (IOException | NoSuchAlgorithmException ex) {
            log.error("Failed to compute md5 for file {}", file, ex);
            throw new RuntimeException(ex);
        }
    }
}
