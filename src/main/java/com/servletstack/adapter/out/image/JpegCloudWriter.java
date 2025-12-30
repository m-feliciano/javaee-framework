package com.servletstack.adapter.out.image;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.io.ByteArrayOutputStream;

@ApplicationScoped
public class JpegCloudWriter {

    public static void write(byte[] bytes, ImageMetadataContract contract, ByteArrayOutputStream baos) throws Exception {
        TiffOutputSet exif = new TiffOutputSet();
        TiffOutputDirectory root = exif.getOrCreateRootDirectory();

        root.add(TiffTagConstants.TIFF_TAG_SOFTWARE, "image-pipeline/1.0");
        root.add(TiffTagConstants.TIFF_TAG_ARTIST, contract.source().ownerService());

        byte[] xmpBytes = XmpBuilder.build(contract);
        root.add(TiffTagConstants.TIFF_TAG_XMP, xmpBytes);

        new ExifRewriter().updateExifMetadataLossless(bytes, baos, exif);
    }
}
