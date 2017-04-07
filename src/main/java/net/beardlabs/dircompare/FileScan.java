package net.beardlabs.dircompare;

import com.google.common.io.BaseEncoding;
import org.immutables.value.Value;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Value.Immutable
public abstract class FileScan {

    public abstract String getPath();
    public abstract String getFileName();

    public abstract long getTotalBytes();
    public abstract String getMd5();

    public static FileScan fromFile(Path file) {

        try {
            ImmutableFileScan.Builder builder = ImmutableFileScan.builder();

            builder.path(file.toString());
            builder.fileName(file.getFileName().toString());

            long totalBytes = 0;
            InputStream is = Files.newInputStream(file, StandardOpenOption.READ);
            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] bytes = new byte[2048];
            int numBytes;
            while ((numBytes = is.read(bytes)) != -1) {
                md.update(bytes, 0, numBytes);
                totalBytes += numBytes;
            }

            byte[] digest = md.digest();

            builder.totalBytes(totalBytes);
            builder.md5(BaseEncoding.base64().encode(digest));

            return builder.build();
        }
        catch (IOException | NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
}
