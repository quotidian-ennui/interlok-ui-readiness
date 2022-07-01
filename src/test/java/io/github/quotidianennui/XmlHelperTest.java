package io.github.quotidianennui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlHelperTest {

  public static final String TESTFILE = "./src/test/resources/adapter.xml";

  private static Document ADAPTER_XML;

  @BeforeAll
  public static void beforeAll() throws Exception {
    try (FileInputStream in = new FileInputStream(TESTFILE); OutputStream out = new ByteArrayOutputStream()) {
      ADAPTER_XML = XmlHelper.roundTripAndLoad(in, out);
    }
  }


  @Test
  public void testResolveXpath() throws Exception {
    XPath xp = XmlHelper.xpathFactory().newXPath();
    NodeList nodes = (NodeList) xp.evaluate(EmitVariableXpaths.XPATH_WITH_VARS, ADAPTER_XML, XPathConstants.NODESET);
    assertTrue(nodes.getLength() > 0);
  }

  @Test
  public void testBuildPath() throws Exception {
    XPath xp = XmlHelper.xpathFactory().newXPath();
    NodeList nodes = (NodeList) xp.evaluate(EmitVariableXpaths.XPATH_WITH_VARS, ADAPTER_XML, XPathConstants.NODESET);
    for (int i = 0; i < nodes.getLength(); i++) {
      Element e = (Element) nodes.item(i);
      assertTrue(XmlHelper.buildPath(e).startsWith("/adapter"));
    }
  }
}