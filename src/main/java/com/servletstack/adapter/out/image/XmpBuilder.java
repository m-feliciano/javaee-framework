package com.servletstack.adapter.out.image;

import com.adobe.internal.xmp.XMPException;
import com.adobe.internal.xmp.XMPMeta;
import com.adobe.internal.xmp.XMPMetaFactory;
import com.adobe.internal.xmp.options.SerializeOptions;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class XmpBuilder {

    public static byte[] build(ImageMetadataContract c) throws XMPException {

        XMPMeta xmp = XMPMetaFactory.create();

        String ns = "urn:image-cloud:metadata:1.0";
        String prefix = "img";

        XMPMetaFactory.getSchemaRegistry().registerNamespace(ns, prefix);

        xmp.setProperty(ns, "assetId", c.asset().assetId());
        xmp.setProperty(ns, "contentHash", c.asset().contentHash());
        xmp.setProperty(ns, "originalFormat", c.asset().originalFormat());
        xmp.setProperty(ns, "createdAt", c.asset().createdAt());

        xmp.setProperty(ns, "source", c.source().origin());
        xmp.setProperty(ns, "ownerService", c.source().ownerService());
        xmp.setProperty(ns, "environment", c.source().environment());

        xmp.setProperty(ns, "pipeline", String.join("|", c.processing().pipeline()));
        xmp.setPropertyInteger(ns, "version", c.processing().version());
        xmp.setPropertyInteger(ns, "quality", c.processing().quality());

        xmp.setPropertyBoolean(ns, "cdnCacheable", c.delivery().cdnCacheable());
        xmp.setProperty(ns, "intendedUsage", c.delivery().intendedUsage());

        return XMPMetaFactory.serializeToBuffer(xmp, new SerializeOptions().setUseCompactFormat(true));
    }
}
