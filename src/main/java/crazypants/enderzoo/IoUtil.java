package crazypants.enderzoo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.io.IOUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class IoUtil {

  public static String readConfigFile(File copyTo, String resourcePath, boolean replaceIfExists) throws IOException {
    return readConfigFile(copyTo, IoUtil.class.getResourceAsStream(resourcePath), replaceIfExists);
  }

  public static String readConfigFile(File copyTo, InputStream in, boolean replaceIfExists) throws IOException {
    if (!replaceIfExists && copyTo.exists()) {
      return readStream(new FileInputStream(copyTo));
    }
    String output = copyTextTo(copyTo, in);
    return output.toString();
  }

  public static String copyTextTo(File copyTo, InputStream from) throws IOException {
    String output = readStream(from);
    BufferedWriter writer = null;
    try {
      makePath(copyTo);
      writer = new BufferedWriter(new FileWriter(copyTo, false));
      writer.write(output.toString());
    } finally {
      IOUtils.closeQuietly(writer);
    }
    return output;
  }

  public static void makePath(File copyTo) throws IOException {
    File p = copyTo.getParentFile();
    if (p == null) {
      return;
    }
    if (!p.exists()) {
      if (!p.mkdirs()) {
        throw new IOException("Could not create directory: " + p.getAbsolutePath());
      }
    }
  }

  public static String readStream(InputStream in) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    StringBuilder output = new StringBuilder();
    try {
      String line = reader.readLine();
      while (line != null) {
        output.append(line);
        output.append("\n");
        line = reader.readLine();
      }
    } finally {
      IOUtils.closeQuietly(reader);
    }
    return output.toString();
  }
  
  public static NBTTagCompound readNBTTagCompound(ByteBuf dataIn) {
    try {
      short size = dataIn.readShort();
      if (size < 0) {
        return null;
      } else {
        byte[] buffer = new byte[size];
        dataIn.readBytes(buffer);
        return CompressedStreamTools.readCompressed(new ByteArrayInputStream(buffer));
      }
    } catch (IOException e) {
      FMLCommonHandler.instance().raiseException(e, "Custom Packet", true);
      return null;
    }
  }

  public static void writeNBTTagCompound(NBTTagCompound compound, ByteBuf dataout) {
    try {
      if (compound == null) {
        dataout.writeShort(-1);
      } else {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CompressedStreamTools.writeCompressed(compound, baos);
        byte[] buf = baos.toByteArray();
        dataout.writeShort((short) buf.length);
        dataout.writeBytes(buf);
      }
    } catch (IOException e) {
      FMLCommonHandler.instance().raiseException(e, "IoUtil.writeNBTTagCompound", true);
    }
  }

  public static byte[] readByteArray(ByteBuf buf) {
    int size = buf.readMedium();
    byte[] res = new byte[size];
    buf.readBytes(res);
    return res;
  }

  public static void writeByteArray(ByteBuf buf, byte[] arr) {
    buf.writeMedium(arr.length);
    buf.writeBytes(arr);
  }

}
