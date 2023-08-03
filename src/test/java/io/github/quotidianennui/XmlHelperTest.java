package io.github.quotidianennui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Arrays;
import java.util.List;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlHelperTest {

  public static final String TESTFILE = "./src/test/resources/adapter.xml";
  private static final String ADAPTER_XML_FILE = "/adapter.xml";

  private static Document ADAPTER_XML;

  public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
  private static final List<String> VARIABLES = Arrays.asList(
      "${local.elastic.transporturl.1}",
      "${local.elastic.transporturl.2}",
      "${speedtest-cli.path}",
      "${adapter.base.url}/config/flatten-speedtest-output.json",
      "${elastic.speedtest.index}",
      "${channel.auto.start}"
  );

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
      String path = XmlHelper.buildPath(e);
      String pathValue = ((Element) xp.evaluate(path, ADAPTER_XML, XPathConstants.NODE)).getTextContent();
      System.err.printf("[%s]=[%s]%n", path, pathValue);
      assertTrue(path.startsWith("/adapter"));
      assertTrue(VARIABLES.contains(pathValue));
    }
  }

  // This should technically raise the github codeql issue 'java/unsafe-get-resource' since this
  // class is extendable.
  @Test
  public void testResourceFromStream() throws Exception {
    Document myAdapterXml;
    try (InputStream in = getClass().getResourceAsStream(
        ADAPTER_XML_FILE); OutputStream out = new ByteArrayOutputStream()) {
      myAdapterXml = XmlHelper.roundTripAndLoad(in, out);
    }
    XPath xp = XmlHelper.xpathFactory().newXPath();
    NodeList nodes = (NodeList) xp.evaluate(EmitVariableXpaths.XPATH_WITH_VARS, myAdapterXml, XPathConstants.NODESET);
    assertTrue(nodes.getLength() > 0);
  }

  @Test
  public void testPointlessTests() throws Exception {
    assertFalse(hash("Hello World".getBytes(StandardCharsets.UTF_8)).isEmpty());
    assertFalse(
        hash(new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(new Date()).getBytes(StandardCharsets.UTF_8)).isEmpty());
  }

  // This should technically raise the github codeql issue 'java/weak-cryptographic-algorithm' if we're using MD5
  private static String hash(byte[] in) throws Exception {
    MessageDigest md5 = MessageDigest.getInstance("MD5");
    byte[] digest = md5.digest(in);
    return Base64.getEncoder().encodeToString(digest);
  }
}