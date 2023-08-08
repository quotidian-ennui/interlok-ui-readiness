package io.github.quotidianennui;

import org.junit.jupiter.api.Test;

public class EmitVariablesXpathTest {

  @Test
  public void testMain() throws Exception {
    EmitVariableXpaths.main(XmlHelperTest.TESTFILE);
  }
}
