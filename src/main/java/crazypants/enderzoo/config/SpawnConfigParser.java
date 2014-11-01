package crazypants.enderzoo.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.BiomeDictionary;

import org.apache.commons.io.IOUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import crazypants.enderzoo.Log;

public class SpawnConfigParser extends DefaultHandler {

  public static List<SpawnEntry> parseSpawnConfig(String text) throws Exception {
    StringReader sr = new StringReader(text);
    InputSource is = new InputSource(sr);
    try {
      return parse(is);
    } finally {
      IOUtils.closeQuietly(sr);
    }
  }

  public static List<SpawnEntry> parseSpawnConfig(File file) throws Exception {
    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
    InputSource is = new InputSource(bis);
    try {
      return parse(is);
    } finally {
      IOUtils.closeQuietly(bis);
    }
  }

  public static List<SpawnEntry> parse(InputSource is) throws Exception {

    SpawnConfigParser parser = new SpawnConfigParser();

    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(true);
    SAXParser saxParser = spf.newSAXParser();
    XMLReader xmlReader = saxParser.getXMLReader();
    xmlReader.setContentHandler(parser);
    xmlReader.parse(is);

    return parser.getResult();
  }

  //----------------------- Parser -----------------------------------------------------

  public static final String ELEMENT_ROOT = "SpawnConfig";
  public static final String ELEMENT_ENTRY = "entry";
  public static final String ELEMENT_BIOME_TYPE = "biomeType";

  public static final String ATT_ID = "id";
  public static final String ATT_MOB_NAME = "mobName";
  public static final String ATT_CREATURE_TYPE = "creatureType";
  public static final String ATT_RATE = "rate";
  public static final String ATT_MIN_GRP = "minGroupSize";
  public static final String ATT_MAX_GRP = "maxGroupSize";
  public static final String ATT_REMOVE = "remove";
  public static final String ATT_NAME = "name";

  public static final String BASE_LAND_TYPES = "BASE_LAND_TYPES";

  private final List<SpawnEntry> result = new ArrayList<SpawnEntry>();

  private SpawnEntry currentEntry;
  private boolean invalidEntryElement = false;
  private boolean foundRoot = false;
  private boolean documentedClosed = false;
  private boolean printedDocumentClosedWarn = false;

  public List<SpawnEntry> getResult() {
    return result;
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if(documentedClosed) {
      if(!printedDocumentClosedWarn) {
        Log.warn("Elements found after closing " + ELEMENT_ROOT + " they will be ignroed.");
        printedDocumentClosedWarn = true;
      }
      return;
    }
    if(ELEMENT_ROOT.equals(localName)) {
      if(foundRoot) {
        Log.warn("Mulitple " + ELEMENT_ROOT + " elements found.");
      }
      foundRoot = true;
    } else if(ELEMENT_ENTRY.equals(localName)) {
      if(!foundRoot) {
        Log.warn("Element " + ELEMENT_ENTRY + " found before " + ELEMENT_ROOT);
      }
      if(currentEntry != null) {
        Log.warn("New " + ELEMENT_ENTRY + " found before previous element closed. Discarding " + currentEntry);
      }
      parseEntry(attributes);
    } else if(ELEMENT_BIOME_TYPE.equals(localName)) {
      if(!foundRoot) {
        Log.warn("Element " + ELEMENT_BIOME_TYPE + " found before " + ELEMENT_ROOT);
      }
      if(currentEntry == null && !invalidEntryElement) {
        Log.warn(ELEMENT_BIOME_TYPE + " found outside an " + ELEMENT_ENTRY + " element. It will be ignored");
      }
      if(!invalidEntryElement) {
        parseBiomeType(attributes);
      }
    }
  }

  private void parseEntry(Attributes attributes) {
    invalidEntryElement = false;
    String id = getStringValue(ATT_ID, attributes, null);
    if(id == null) {
      Log.error(ELEMENT_ENTRY + " specified without an " + ATT_ID + " atribute");
      invalidEntryElement = true;
      return;
    }
    String mobName = getStringValue(ATT_MOB_NAME, attributes, null);
    if(mobName == null || mobName.trim().length() == 0) {
      Log.error(ELEMENT_ENTRY + " specified without an " + ATT_MOB_NAME + " atribute");
      invalidEntryElement = true;
      return;
    }
    mobName = mobName.trim();

    int rate = getIntValue(ATT_RATE, attributes, -1);
    if(rate <= 0) {
      Log.error(ELEMENT_ENTRY + " specified without a valid " + ATT_RATE + " atribute");
      invalidEntryElement = true;
      return;
    }
    rate = MathHelper.clamp_int(rate, 1, 100);

    currentEntry = new SpawnEntry(id, mobName, rate);
    String creatureType = getStringValue(ATT_CREATURE_TYPE, attributes, null);
    if(creatureType != null) {
      try {
        EnumCreatureType.valueOf(creatureType.trim());
      } catch (Exception e) {
        Log.warn("Invalid value specified for " + ATT_CREATURE_TYPE + " in entry " + id + " using default value " + currentEntry.getCreatureType() + " error: "
            + e);
      }
    }

    int minGrp = getIntValue(ATT_MIN_GRP, attributes, -1);
    if(minGrp != -1) {
      if(minGrp < 0) {
        Log.warn("Value less than 0 found for " + ATT_MIN_GRP + " in entry " + id + " using default value " + currentEntry.getMinGroupSize());
      } else {
        currentEntry.setMinGroupSize(minGrp);
      }
    }

    int maxGrp = getIntValue(ATT_MAX_GRP, attributes, -1);
    if(maxGrp != -1) {
      if(maxGrp < currentEntry.getMinGroupSize()) {
        Log.warn("Value for " + ATT_MAX_GRP + " in entry " + id + " less than " + ATT_MIN_GRP + " using default " + currentEntry.getMaxGroupSize());
      } else {
        currentEntry.setMaxGroupSize(maxGrp);
      }
    }

    currentEntry.setIsRemove(getBooleanValue(ATT_REMOVE, attributes, currentEntry.isRemove()));

  }

  private void parseBiomeType(Attributes attributes) {
    String biomeName = getStringValue(ATT_NAME, attributes, null);
    if(biomeName == null || biomeName.trim().length() == 0) {
      Log.warn("Attribute " + ATT_NAME + " not specified in element " + ELEMENT_BIOME_TYPE + " in entry " + currentEntry.getId());
      return;
    }
    biomeName = biomeName.trim();
    if(BASE_LAND_TYPES.equals(biomeName)) {
      currentEntry.addBaseLandTypes();
    } else {
      try {
        currentEntry.addBiomeType(BiomeDictionary.Type.valueOf(biomeName));
      } catch (Exception e) {
        Log.warn("Attribute " + ATT_NAME + " in element " + ELEMENT_BIOME_TYPE + " with value " + biomeName + " is invalid and has been ignored.");
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if(ELEMENT_ENTRY.equals(localName) && currentEntry != null) {
      result.add(currentEntry);
      currentEntry = null;
    } else if(ELEMENT_ROOT.equals(localName)) {
      documentedClosed = true;
    }
  }

  @Override
  public void warning(SAXParseException e) throws SAXException {
    Log.warn("Warning parsing Spawn config file: " + e.getMessage());
  }

  @Override
  public void error(SAXParseException e) throws SAXException {
    Log.error("Error parsing Spawn config file: " + e.getMessage());
    e.printStackTrace();
  }

  @Override
  public void fatalError(SAXParseException e) throws SAXException {
    Log.error("Error parsing Spawn config file: " + e.getMessage());
    e.printStackTrace();
  }

  public static boolean getBooleanValue(String qName, Attributes attributes, boolean def) {
    String val = attributes.getValue(qName);
    if(val == null) {
      return def;
    }
    val = val.toLowerCase().trim();
    return val.equals("false") ? false : val.equals("true") ? true : def;
  }

  public static int getIntValue(String qName, Attributes attributes, int def) {
    try {
      return Integer.parseInt(getStringValue(qName, attributes, def + ""));
    } catch (Exception e) {
      Log.warn("Could not parse a valid int for attribute " + qName + " with value " + getStringValue(qName, attributes, null));
      return def;
    }
  }

  public static float getFloatValue(String qName, Attributes attributes, float def) {
    try {
      return Float.parseFloat(getStringValue(qName, attributes, def + ""));
    } catch (Exception e) {
      Log.warn("Could not parse a valid float for attribute " + qName + " with value " + getStringValue(qName, attributes, null));
      return def;
    }
  }

  public static String getStringValue(String qName, Attributes attributes, String def) {
    String val = attributes.getValue(qName);
    if(val == null) {
      return def;
    }
    val = val.trim();
    if(val.length() <= 0) {
      return null;
    }
    return val;
  }

}
