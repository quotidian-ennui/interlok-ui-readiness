package io.github.quotidianennui;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathFactory;
import net.sf.saxon.xpath.XPathFactoryImpl;
import org.w3c.dom.Document;

public class XmlHelper {
  private static final String XINCLUDE_FIXUP_BASE_URI_FEATURE = "http://apache.org/xml/features/xinclude/fixup-base-uris";
  private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";

  static Document roundTripAndLoad(InputStream source, OutputStream target) throws Exception{
    DocumentBuilder builder = newBuilderFactory().newDocumentBuilder();
    Document resolved = builder.parse(source);
    write(resolved, target);
    return resolved;
  }

  static void write(Document input, OutputStream target) throws Exception {
    StreamResult result = new StreamResult(target);
    DOMSource dom = new DOMSource(input);
    Transformer serializer = TransformerFactory.newInstance().newTransformer();
    serializer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
    serializer.setOutputProperty(OutputKeys.INDENT, "yes");
    serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    serializer.transform(dom, result);
  }

  public static XPathFactory xpathFactory() {
    return new XPathFactoryImpl();
  }

  private static DocumentBuilderFactory newBuilderFactory() throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setXIncludeAware(true);
    dbf.setNamespaceAware(true);
    dbf.setFeature(XINCLUDE_FEATURE, true);
    dbf.setFeature(XINCLUDE_FIXUP_BASE_URI_FEATURE, true);
    return dbf;
  }
}
