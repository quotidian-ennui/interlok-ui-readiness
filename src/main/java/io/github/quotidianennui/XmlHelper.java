package io.github.quotidianennui;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import net.sf.practicalxml.DomUtil;
import net.sf.saxon.xpath.XPathFactoryImpl;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XmlHelper {

  private static final String XINCLUDE_FIXUP_BASE_URI_FEATURE = "http://apache.org/xml/features/xinclude/fixup-base-uris";
  private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";

  private static final ThreadLocal<XPath> XPATH_UTIL = ThreadLocal.withInitial(() -> xpathFactory().newXPath());

  public static Document roundTripAndLoad(InputStream source, OutputStream target) throws Exception {
    DocumentBuilder builder = newBuilderFactory().newDocumentBuilder();
    Document resolved = builder.parse(source);
    write(resolved, target);
    return resolved;
  }

  public static void write(Document input, OutputStream target) throws Exception {
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

  public static String buildPath(Element elem) throws Exception {
    StringBuilder sb = new StringBuilder();
    buildPath(elem, sb);
    return sb.toString();
  }

  private static void buildPath(Element elem, StringBuilder sb) throws Exception {
    Node parent = elem.getParentNode();
    if (parent instanceof Element) {
      buildPath((Element) parent, sb);
    }
    String localName = DomUtil.getLocalName(elem);
    sb.append("/").append(localName);
    String uid = uniqueIdOrNull(elem);
    // If there's a UID the append it to qualify it.
    // But only if it's not the adapter element...
    if (!StringUtils.isBlank(uid) && !localName.equalsIgnoreCase("adapter")) {
      sb.append("[unique-id=\"").append(uid).append("\"]");
    } else {
      List<Element> siblings = DomUtil.getSiblings(elem, localName);
      if (siblings.size() > 1) {
        sb.append("[").append(getIndex(elem, siblings)).append("]");
      }
    }
  }

  private static String uniqueIdOrNull(Element elem) throws Exception {
    return XPATH_UTIL.get().evaluate("./unique-id", elem);
  }

  private static int getIndex(Element elem, List<Element> siblings) {
    for (int i = 0; i < siblings.size(); i++) {
      if (siblings.get(i) == elem) {
        // not zero-index.
        return i + 1;
      }
    }
    throw new IllegalArgumentException("element not amongst its siblings");
  }
}
