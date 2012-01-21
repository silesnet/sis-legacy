package cz.silesnet.util;

import java.util.Map;

public class ControllerUtils {
  @SuppressWarnings("unchecked")
  public static void putAll(Map destinationMap, Map sourceMap) {
    if (sourceMap != null && sourceMap.size() > 0)
      destinationMap.putAll(sourceMap);
  }

}
