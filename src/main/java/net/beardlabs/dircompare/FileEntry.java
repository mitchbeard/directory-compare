package net.beardlabs.dircompare;

import com.google.common.io.BaseEncoding;
import org.immutables.value.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Value.Immutable
public abstract class FileEntry {

    public abstract String getPath();
    public abstract String getFileName();

    public abstract long getByteSize();
    public abstract String getMd5();

    public static FileEntry fromFile(Path file) {

        try {
            ImmutableFileEntry.Builder builder = new ImmutableFileEntry.builder();

            builder.path(file.toString());
            builder.fileName(file.getFileName().toString());

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = Files.readAllBytes(file);
            byte[] md5 = md.digest(bytes);

            builder.byteSize(bytes.length);
            builder.md5(BaseEncoding.base64().encode(md5));

            return builder.build();
        }
        catch (IOException | NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }
}
