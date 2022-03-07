package io.github.quotidianennui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import net.sf.practicalxml.DomUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class EmitVariableXpaths {

  public static final String XPATH_WITH_VARS = "//*[not(.//*) and contains(text(),'${')]";
  public static final String XPATH_WITH_VARS_ALT = "//*[contains(text()[1], \"${\")]";

  private final File inputFile;
  private final File outputFile;

  public EmitVariableXpaths(File inputFile) {
    this.inputFile = inputFile;
    this.outputFile = createOutputFile(inputFile);
  }

  public void printXpaths() throws Exception {
    try (FileInputStream in = new FileInputStream(inputFile); FileOutputStream out = new FileOutputStream(outputFile)) {
      Document doc = XmlHelper.roundTripAndLoad(in, out);
      XPath xp = XmlHelper.xpathFactory().newXPath();
      NodeList nodes = (NodeList) xp.evaluate(XPATH_WITH_VARS, doc, XPathConstants.NODESET);
      Map<String, String> variableXpaths = new HashMap<>();
      for (int i = 0; i < nodes.getLength(); i++) {
        Element e = (Element) nodes.item(i);
        variableXpaths.put(DomUtil.getAbsolutePath(e), e.getTextContent());
      }
      print(variableXpaths);
    }
  }

  private void print(Map<String, String> map) throws Exception {
    ObjectMapper mapper = JsonMapper.builder().enable(SerializationFeature.INDENT_OUTPUT).build();
    HashMap<String, Map<String,String>> wrapper = new HashMap<>();
    wrapper.put("variableXpaths", map);
    System.err.println(mapper.writeValueAsString(wrapper));

  }

  private static File createOutputFile(File inputFile) {
    return new File(inputFile.getParentFile(), inputFile.getName() + ".monolithic");
  }

  public static void main(String[] argv) throws Exception {
    String input = argv[0];
    new EmitVariableXpaths(new File(input)).printXpaths();
  }

}
