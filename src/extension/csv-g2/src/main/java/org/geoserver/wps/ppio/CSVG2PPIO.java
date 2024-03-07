/* (c) 2019 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wps.ppio;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.geoserver.config.GeoServer;
import org.geoserver.platform.resource.Resource;
import org.geoserver.util.IOUtils;
import org.geoserver.wfs.response.CSVG2OutputFormat;
import org.geoserver.wps.resource.WPSResourceManager;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.xsd.Text;

public class CSVG2PPIO extends CDataPPIO {
  WPSResourceManager resourceManager;

  private GeoServer gs;
  private String delimiter;

  protected CSVG2PPIO(WPSResourceManager resourceManager, GeoServer gs, String delimiter) {
    super(SimpleFeatureCollection.class, SimpleFeatureCollection.class, "text/csv-g2");
    this.resourceManager = resourceManager;

    this.gs = gs;
    this.delimiter = delimiter;
  }

  @Override
  public Object decode(String input) throws Exception {
    throw new IOException("Decode not supported for CVS-G2!");
  }

  @Override
  public Object decode(InputStream input) throws Exception {
    throw new IOException("Decode not supported for CVS-G2!");
  }

  @Override
  public String getFileExtension() {
    return "csv";
  }

  @Override
  public Object decode(Object input) throws Exception {
    Class<? extends Object> type = input.getClass();
    if (type.isAssignableFrom(String.class)) {
      return decode((String) input);
    }
    if (type.isAssignableFrom(Text.class)) {
      return decode(((Text) input).getValue());
    }
    return super.decode(input);
  }

  @Override
  public void encode(Object value, OutputStream os) throws Exception {
    // will be deleted when the process finishes
    Resource tmp = resourceManager.getTemporaryResource(".csv");
    SimpleFeatureCollection collection = (SimpleFeatureCollection) value;

    CSVG2OutputFormat csvg2 = new CSVG2OutputFormat(gs, delimiter);
    FileOutputStream out = new FileOutputStream(tmp.file());
    csvg2.write(collection, out);
    out.close();

    IOUtils.copy(tmp.in(), os);
  }
}
