/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions Copyright [year] [name of copyright owner]".
 *
 * Copyright 2006-2009 Sun Microsystems, Inc.
 * Portions Copyright 2014-2016 ForgeRock AS.
 */
package org.opends.server.util;

import static org.forgerock.util.Reject.*;
import static org.opends.messages.ToolMessages.*;
import static org.opends.messages.UtilityMessages.*;
import static org.opends.server.util.StaticUtils.*;

import static com.forgerock.opendj.cli.CommonArguments.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.forgerock.i18n.LocalizableMessage;
import org.forgerock.opendj.ldap.ByteSequence;
import org.opends.server.core.DirectoryServer.DirectoryServerVersionHandler;
import org.opends.server.types.NullOutputStream;

import com.forgerock.opendj.cli.ArgumentException;
import com.forgerock.opendj.cli.BooleanArgument;
import com.forgerock.opendj.cli.StringArgument;
import com.forgerock.opendj.cli.SubCommand;
import com.forgerock.opendj.cli.SubCommandArgumentParser;

/**
 * This class provides methods for performing base64 encoding and decoding.
 * Base64 is a mechanism for encoding binary data in ASCII form by converting
 * sets of three bytes with eight significant bits each to sets of four bytes
 * with six significant bits each.
 */
@org.opends.server.types.PublicAPI(
     stability=org.opends.server.types.StabilityLevel.UNCOMMITTED,
     mayInstantiate=false,
     mayExtend=false,
     mayInvoke=true)
public final class Base64
{
  /** The set of characters that may be used in base64-encoded values. */
  private static final char[] BASE64_ALPHABET =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

  /** Prevent instance creation. */
  private Base64() {
    // No implementation required.
  }

  /**
   * Encodes the provided raw data using base64.
   *
   * @param  rawData  The raw data to encode.  It must not be <CODE>null</CODE>.
   *
   * @return  The base64-encoded representation of the provided raw data.
   */
  public static String encode(byte[] rawData)
  {
    ifNull(rawData);


    StringBuilder buffer = new StringBuilder(4 * rawData.length / 3);

    int pos = 0;
    int iterations = rawData.length / 3;
    for (int i=0; i < iterations; i++)
    {
      int value = ((rawData[pos++] & 0xFF) << 16) |
                  ((rawData[pos++] & 0xFF) <<  8) | (rawData[pos++] & 0xFF);

      buffer.append(BASE64_ALPHABET[(value >>> 18) & 0x3F]);
      buffer.append(BASE64_ALPHABET[(value >>> 12) & 0x3F]);
      buffer.append(BASE64_ALPHABET[(value >>>  6) & 0x3F]);
      buffer.append(BASE64_ALPHABET[value & 0x3F]);
    }


    switch (rawData.length % 3)
    {
      case 1:
        buffer.append(BASE64_ALPHABET[(rawData[pos] >>> 2) & 0x3F]);
        buffer.append(BASE64_ALPHABET[(rawData[pos] <<  4) & 0x3F]);
        buffer.append("==");
        break;
      case 2:
        int value = ((rawData[pos++] & 0xFF) << 8) | (rawData[pos] & 0xFF);
        buffer.append(BASE64_ALPHABET[(value >>> 10) & 0x3F]);
        buffer.append(BASE64_ALPHABET[(value >>>  4) & 0x3F]);
        buffer.append(BASE64_ALPHABET[(value <<   2) & 0x3F]);
        buffer.append("=");
        break;
    }

    return buffer.toString();
  }

  /**
   * Encodes the provided raw data using base64.
   *
   * @param  rawData  The raw data to encode.  It must not be <CODE>null</CODE>.
   *
   * @return  The base64-encoded representation of the provided raw data.
   */
  public static String encode(ByteSequence rawData)
  {
    ifNull(rawData);


    StringBuilder buffer = new StringBuilder(4 * rawData.length() / 3);

    int pos = 0;
    int iterations = rawData.length() / 3;
    for (int i=0; i < iterations; i++)
    {
      int value = ((rawData.byteAt(pos++) & 0xFF) << 16) |
                  ((rawData.byteAt(pos++) & 0xFF) <<  8) |
          (rawData.byteAt(pos++) & 0xFF);

      buffer.append(BASE64_ALPHABET[(value >>> 18) & 0x3F]);
      buffer.append(BASE64_ALPHABET[(value >>> 12) & 0x3F]);
      buffer.append(BASE64_ALPHABET[(value >>>  6) & 0x3F]);
      buffer.append(BASE64_ALPHABET[value & 0x3F]);
    }


    switch (rawData.length() % 3)
    {
      case 1:
        buffer.append(BASE64_ALPHABET[(rawData.byteAt(pos) >>> 2) & 0x3F]);
        buffer.append(BASE64_ALPHABET[(rawData.byteAt(pos) <<  4) & 0x3F]);
        buffer.append("==");
        break;
      case 2:
        int value = ((rawData.byteAt(pos++) & 0xFF) << 8) |
            (rawData.byteAt(pos) & 0xFF);
        buffer.append(BASE64_ALPHABET[(value >>> 10) & 0x3F]);
        buffer.append(BASE64_ALPHABET[(value >>>  4) & 0x3F]);
        buffer.append(BASE64_ALPHABET[(value <<   2) & 0x3F]);
        buffer.append("=");
        break;
    }

    return buffer.toString();
  }



  /**
   * Decodes the provided set of base64-encoded data.
   *
   * @param  encodedData  The base64-encoded data to decode.  It must not be
   *                      <CODE>null</CODE>.
   *
   * @return  The decoded raw data.
   *
   * @throws  ParseException  If a problem occurs while attempting to decode the
   *                          provided data.
   */
  public static byte[] decode(String encodedData)
         throws ParseException
  {
    ifNull(encodedData);


    // The encoded value must have  length that is a multiple of four bytes.
    int length = encodedData.length();
    if (length % 4 != 0)
    {
      LocalizableMessage message = ERR_BASE64_DECODE_INVALID_LENGTH.get(encodedData);
      throw new ParseException(message.toString(), 0);
    }


    ByteBuffer buffer = ByteBuffer.allocate(length);
    for (int i=0; i < length; i += 4)
    {
      boolean append = true;
      int     value  = 0;

      for (int j=0; j < 4; j++)
      {
        switch (encodedData.charAt(i+j))
        {
          case 'A':
            value <<= 6;
            break;
          case 'B':
            value = (value << 6) | 0x01;
            break;
          case 'C':
            value = (value << 6) | 0x02;
            break;
          case 'D':
            value = (value << 6) | 0x03;
            break;
          case 'E':
            value = (value << 6) | 0x04;
            break;
          case 'F':
            value = (value << 6) | 0x05;
            break;
          case 'G':
            value = (value << 6) | 0x06;
            break;
          case 'H':
            value = (value << 6) | 0x07;
            break;
          case 'I':
            value = (value << 6) | 0x08;
            break;
          case 'J':
            value = (value << 6) | 0x09;
            break;
          case 'K':
            value = (value << 6) | 0x0A;
            break;
          case 'L':
            value = (value << 6) | 0x0B;
            break;
          case 'M':
            value = (value << 6) | 0x0C;
            break;
          case 'N':
            value = (value << 6) | 0x0D;
            break;
          case 'O':
            value = (value << 6) | 0x0E;
            break;
          case 'P':
            value = (value << 6) | 0x0F;
            break;
          case 'Q':
            value = (value << 6) | 0x10;
            break;
          case 'R':
            value = (value << 6) | 0x11;
            break;
          case 'S':
            value = (value << 6) | 0x12;
            break;
          case 'T':
            value = (value << 6) | 0x13;
            break;
          case 'U':
            value = (value << 6) | 0x14;
            break;
          case 'V':
            value = (value << 6) | 0x15;
            break;
          case 'W':
            value = (value << 6) | 0x16;
            break;
          case 'X':
            value = (value << 6) | 0x17;
            break;
          case 'Y':
            value = (value << 6) | 0x18;
            break;
          case 'Z':
            value = (value << 6) | 0x19;
            break;
          case 'a':
            value = (value << 6) | 0x1A;
            break;
          case 'b':
            value = (value << 6) | 0x1B;
            break;
          case 'c':
            value = (value << 6) | 0x1C;
            break;
          case 'd':
            value = (value << 6) | 0x1D;
            break;
          case 'e':
            value = (value << 6) | 0x1E;
            break;
          case 'f':
            value = (value << 6) | 0x1F;
            break;
          case 'g':
            value = (value << 6) | 0x20;
            break;
          case 'h':
            value = (value << 6) | 0x21;
            break;
          case 'i':
            value = (value << 6) | 0x22;
            break;
          case 'j':
            value = (value << 6) | 0x23;
            break;
          case 'k':
            value = (value << 6) | 0x24;
            break;
          case 'l':
            value = (value << 6) | 0x25;
            break;
          case 'm':
            value = (value << 6) | 0x26;
            break;
          case 'n':
            value = (value << 6) | 0x27;
            break;
          case 'o':
            value = (value << 6) | 0x28;
            break;
          case 'p':
            value = (value << 6) | 0x29;
            break;
          case 'q':
            value = (value << 6) | 0x2A;
            break;
          case 'r':
            value = (value << 6) | 0x2B;
            break;
          case 's':
            value = (value << 6) | 0x2C;
            break;
          case 't':
            value = (value << 6) | 0x2D;
            break;
          case 'u':
            value = (value << 6) | 0x2E;
            break;
          case 'v':
            value = (value << 6) | 0x2F;
            break;
          case 'w':
            value = (value << 6) | 0x30;
            break;
          case 'x':
            value = (value << 6) | 0x31;
            break;
          case 'y':
            value = (value << 6) | 0x32;
            break;
          case 'z':
            value = (value << 6) | 0x33;
            break;
          case '0':
            value = (value << 6) | 0x34;
            break;
          case '1':
            value = (value << 6) | 0x35;
            break;
          case '2':
            value = (value << 6) | 0x36;
            break;
          case '3':
            value = (value << 6) | 0x37;
            break;
          case '4':
            value = (value << 6) | 0x38;
            break;
          case '5':
            value = (value << 6) | 0x39;
            break;
          case '6':
            value = (value << 6) | 0x3A;
            break;
          case '7':
            value = (value << 6) | 0x3B;
            break;
          case '8':
            value = (value << 6) | 0x3C;
            break;
          case '9':
            value = (value << 6) | 0x3D;
            break;
          case '+':
            value = (value << 6) | 0x3E;
            break;
          case '/':
            value = (value << 6) | 0x3F;
            break;
          case '=':
            append = false;
            switch (j)
            {
              case 2:
                buffer.put((byte) ((value >>> 4) & 0xFF));
                break;
              case 3:
                buffer.put((byte) ((value >>> 10) & 0xFF));
                buffer.put((byte) ((value >>>  2) & 0xFF));
                break;
            }
            break;
          default:
            LocalizableMessage message = ERR_BASE64_DECODE_INVALID_CHARACTER.get(
                encodedData, encodedData.charAt(i+j));
            throw new ParseException(message.toString(), i+j);
        }


        if (! append)
        {
          break;
        }
      }


      if (append)
      {
        buffer.put((byte) ((value >>> 16) & 0xFF));
        buffer.put((byte) ((value >>>  8) & 0xFF));
        buffer.put((byte) (value & 0xFF));
      }
      else
      {
        break;
      }
    }


    buffer.flip();
    byte[] returnArray = new byte[buffer.limit()];
    buffer.get(returnArray);
    return returnArray;
  }



  /**
   * Provide a command-line utility that may be used to base64-encode and
   * decode strings and file contents.
   *
   * @param  args  The command-line arguments provided to this program.
   */
  public static void main(String[] args)
  {
    LocalizableMessage description = INFO_BASE64_TOOL_DESCRIPTION.get();
    SubCommandArgumentParser argParser =
         new SubCommandArgumentParser(Base64.class.getName(), description,
                                      false);
    argParser.setShortToolDescription(REF_SHORT_DESC_BASE64.get());
    argParser.setVersionHandler(new DirectoryServerVersionHandler());

    BooleanArgument showUsage        = null;
    StringArgument  encodedData      = null;
    StringArgument  encodedFile      = null;
    StringArgument  rawData          = null;
    StringArgument  rawFile          = null;
    StringArgument  toEncodedFile    = null;
    StringArgument  toRawFile        = null;
    SubCommand      decodeSubCommand = null;
    SubCommand      encodeSubCommand = null;

    try
    {
      decodeSubCommand = new SubCommand(argParser, "decode",
                                        INFO_BASE64_DECODE_DESCRIPTION.get());

      encodeSubCommand = new SubCommand(argParser, "encode",
                                        INFO_BASE64_ENCODE_DESCRIPTION.get());

      encodedData =
              StringArgument.builder("encodedData")
                      .shortIdentifier('d')
                      .description(INFO_BASE64_ENCODED_DATA_DESCRIPTION.get())
                      .valuePlaceholder(INFO_DATA_PLACEHOLDER.get())
                      .buildAndAddToSubCommand(decodeSubCommand);
      encodedFile =
              StringArgument.builder("encodedDataFile")
                      .shortIdentifier('f')
                      .description(INFO_BASE64_ENCODED_FILE_DESCRIPTION.get())
                      .valuePlaceholder(INFO_PATH_PLACEHOLDER.get())
                      .buildAndAddToSubCommand(decodeSubCommand);
      toRawFile =
              StringArgument.builder("toRawFile")
                      .shortIdentifier('o')
                      .description(INFO_BASE64_TO_RAW_FILE_DESCRIPTION.get())
                      .valuePlaceholder(INFO_PATH_PLACEHOLDER.get())
                      .buildAndAddToSubCommand(decodeSubCommand);
      rawData =
              StringArgument.builder("rawData")
                      .shortIdentifier('d')
                      .description(INFO_BASE64_RAW_DATA_DESCRIPTION.get())
                      .valuePlaceholder(INFO_DATA_PLACEHOLDER.get())
                      .buildAndAddToSubCommand(encodeSubCommand);
      rawFile =
              StringArgument.builder("rawDataFile")
                      .shortIdentifier('f')
                      .description(INFO_BASE64_RAW_FILE_DESCRIPTION.get())
                      .valuePlaceholder(INFO_PATH_PLACEHOLDER.get())
                      .buildAndAddToSubCommand(encodeSubCommand);
      toEncodedFile =
              StringArgument.builder("toEncodedFile")
                      .shortIdentifier('o')
                      .description(INFO_BASE64_TO_ENCODED_FILE_DESCRIPTION.get())
                      .valuePlaceholder(INFO_PATH_PLACEHOLDER.get())
                      .buildAndAddToSubCommand(encodeSubCommand);

      ArrayList<SubCommand> subCommandList = new ArrayList<>(2);
      subCommandList.add(decodeSubCommand);
      subCommandList.add(encodeSubCommand);

      showUsage = showUsageArgument();
      argParser.addGlobalArgument(showUsage);
      argParser.setUsageGroupArgument(showUsage, subCommandList);
      argParser.setUsageArgument(showUsage, NullOutputStream.printStream());
    }
    catch (ArgumentException ae)
    {
      System.err.println(ERR_CANNOT_INITIALIZE_ARGS.get(ae.getMessage()));
      System.exit(1);
    }

    try
    {
      argParser.parseArguments(args);
    }
    catch (ArgumentException ae)
    {
      argParser.displayMessageAndUsageReference(System.err, ERR_ERROR_PARSING_ARGS.get(ae.getMessage()));
      System.exit(1);
    }

    SubCommand subCommand = argParser.getSubCommand();
    if (argParser.isUsageArgumentPresent())
    {
      if (subCommand == null)
      {
        System.out.println(argParser.getUsage());
      }
      else
      {
        final StringBuilder messageBuilder = new StringBuilder();
        argParser.getSubCommandUsage(messageBuilder, subCommand);
        System.out.println(messageBuilder.toString());
      }

      return;
    }

    if (argParser.isVersionArgumentPresent())
    {
      // version has already been printed
      System.exit(0);
    }

    if (subCommand == null)
    {
      System.err.println(argParser.getUsage());
      System.exit(1);
    }
    if (subCommand.getName().equals(encodeSubCommand.getName()))
    {
      byte[] dataToEncode = null;
      if (rawData.isPresent())
      {
        try
        {
          dataToEncode = rawData.getValue().getBytes("UTF-8");
        }
        catch(UnsupportedEncodingException ex)
        {
          System.err.println(ERR_UNEXPECTED.get(ex));
          System.exit(1);
        }
      }
      else
      {
        try
        {
          boolean shouldClose;
          InputStream inputStream;
          if (rawFile.isPresent())
          {
            inputStream = new FileInputStream(rawFile.getValue());
            shouldClose = true;
          }
          else
          {
            inputStream = System.in;
            shouldClose = false;
          }

          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          byte[] buffer = new byte[8192];
          while (true)
          {
            int bytesRead = inputStream.read(buffer);
            if (bytesRead < 0)
            {
              break;
            }
            else
            {
              baos.write(buffer, 0, bytesRead);
            }
          }

          if (shouldClose)
          {
            inputStream.close();
          }

          dataToEncode = baos.toByteArray();
        }
        catch (Exception e)
        {
          System.err.println(ERR_BASE64_CANNOT_READ_RAW_DATA.get(
                                  getExceptionMessage(e)));
          System.exit(1);
        }
      }

      String base64Data = encode(dataToEncode);
      if (toEncodedFile.isPresent())
      {
        try
        {
          BufferedWriter writer =
               new BufferedWriter(new FileWriter(toEncodedFile.getValue()));
          writer.write(base64Data);
          writer.newLine();
          writer.close();
        }
        catch (Exception e)
        {
          System.err.println(ERR_BASE64_CANNOT_WRITE_ENCODED_DATA.get(
                                  getExceptionMessage(e)));
          System.exit(1);
        }
      }
      else
      {
        System.out.println(base64Data);
      }
    }
    else if (subCommand.getName().equals(decodeSubCommand.getName()))
    {
      String dataToDecode = null;
      if (encodedData.isPresent())
      {
        dataToDecode = encodedData.getValue();
      }
      else
      {
        try
        {
          boolean shouldClose;
          BufferedReader reader;
          if (encodedFile.isPresent())
          {
            reader = new BufferedReader(new FileReader(encodedFile.getValue()));
            shouldClose = true;
          }
          else
          {
            reader = new BufferedReader(new InputStreamReader(System.in));
            shouldClose = false;
          }

          StringBuilder buffer = new StringBuilder();
          while (true)
          {
            String line = reader.readLine();
            if (line == null)
            {
              break;
            }

            StringTokenizer tokenizer = new StringTokenizer(line);
            while (tokenizer.hasMoreTokens())
            {
              buffer.append(tokenizer.nextToken());
            }
          }

          if (shouldClose)
          {
            reader.close();
          }

          dataToDecode = buffer.toString();
        }
        catch (Exception e)
        {
          System.err.println(ERR_BASE64_CANNOT_READ_ENCODED_DATA.get(
                                  getExceptionMessage(e)));
          System.exit(1);
        }
      }

      byte[] decodedData = null;
      try
      {
        decodedData = decode(dataToDecode);
      }
      catch (ParseException pe)
      {
        System.err.println(pe.getMessage());
        System.exit(1);
      }

      try
      {
        if (toRawFile.isPresent())
        {
          FileOutputStream outputStream =
               new FileOutputStream(toRawFile.getValue());
          outputStream.write(decodedData);
          outputStream.close();
        }
        else
        {
          System.out.write(decodedData);
          System.out.println();
          System.out.flush();
        }
      }
      catch (Exception e)
      {
        System.err.println(ERR_BASE64_CANNOT_WRITE_RAW_DATA.get(
                                getExceptionMessage(e)));
        System.exit(1);
      }
    }
    else
    {
      System.err.println(ERR_BASE64_UNKNOWN_SUBCOMMAND.get(
                              subCommand.getName()));
      System.exit(1);
    }
  }
}
