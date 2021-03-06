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
 * Copyright 2006-2008 Sun Microsystems, Inc.
 * Portions Copyright 2012-2016 ForgeRock AS.
 */
package org.opends.server.types;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringTokenizer;

import org.forgerock.i18n.LocalizableMessage;
import org.forgerock.i18n.slf4j.LocalizedLogger;
import org.forgerock.opendj.ldap.DN;
import org.forgerock.opendj.ldap.ResultCode;
import org.forgerock.opendj.ldap.SearchScope;
import org.opends.server.core.DirectoryServer;

import static org.forgerock.opendj.ldap.ResultCode.*;
import static org.opends.messages.UtilityMessages.*;
import static org.opends.server.util.StaticUtils.*;

/**
 * This class defines a data structure that represents the components
 * of an LDAP URL, including the scheme, host, port, base DN,
 * attributes, scope, filter, and extensions.  It has the ability to
 * create an LDAP URL based on all of these individual components, as
 * well as parsing them from their string representations.
 */
@org.opends.server.types.PublicAPI(
     stability=org.opends.server.types.StabilityLevel.UNCOMMITTED,
     mayInstantiate=true,
     mayExtend=false,
     mayInvoke=true)
public final class LDAPURL
{
  private static final LocalizedLogger logger = LocalizedLogger.getLoggerForThisClass();

  /** The default scheme that will be used if none is provided. */
  public static final String DEFAULT_SCHEME = "ldap";
  /** The default port value that will be used if none is provided. */
  private static final int DEFAULT_PORT = 389;
  /** The default base DN that will be used if none is provided. */
  private static final DN DEFAULT_BASE_DN = DN.rootDN();
  /** The default search scope that will be used if none is provided. */
  private static final SearchScope DEFAULT_SEARCH_SCOPE =
       SearchScope.BASE_OBJECT;
  /** The default search filter that will be used if none is provided. */
  public static final SearchFilter DEFAULT_SEARCH_FILTER =
       SearchFilter.createPresenceFilter(
            DirectoryServer.getObjectClassAttributeType());


  /** The host for this LDAP URL. */
  private String host;
  /** The port number for this LDAP URL. */
  private int port;
  /** The base DN for this LDAP URL. */
  private DN baseDN;
  /** The raw base DN for this LDAP URL. */
  private String rawBaseDN;
  /** The search scope for this LDAP URL. */
  private SearchScope scope;
  /** The search filter for this LDAP URL. */
  private SearchFilter filter;
  /** The raw filter for this LDAP URL. */
  private String rawFilter;

  /** The set of attributes for this LDAP URL. */
  private final LinkedHashSet<String> attributes;
  /** The set of extensions for this LDAP URL. */
  private final List<String> extensions;


  /** The scheme (i.e., protocol) for this LDAP URL. */
  private String scheme;



  /**
   * Creates a new LDAP URL with the provided information.
   *
   * @param  scheme      The scheme (i.e., protocol) for this LDAP
   *                     URL.
   * @param  host        The address for this LDAP URL.
   * @param  port        The port number for this LDAP URL.
   * @param  rawBaseDN   The raw base DN for this LDAP URL.
   * @param  attributes  The set of requested attributes for this LDAP
   *                     URL.
   * @param  scope       The search scope for this LDAP URL.
   * @param  rawFilter   The string representation of the search
   *                     filter for this LDAP URL.
   * @param  extensions  The set of extensions for this LDAP URL.
   */
  public LDAPURL(String scheme, String host, int port,
                 String rawBaseDN, LinkedHashSet<String> attributes,
                 SearchScope scope, String rawFilter,
                 List<String> extensions)
  {
    this.host = toLowerCase(host);

    baseDN = null;
    filter = null;


    if (scheme == null)
    {
      this.scheme = "ldap";
    }
    else
    {
      this.scheme = toLowerCase(scheme);
    }

    this.port = toPort(port);

    if (rawBaseDN == null)
    {
      this.rawBaseDN = "";
    }
    else
    {
      this.rawBaseDN = rawBaseDN;
    }

    if (attributes == null)
    {
      this.attributes = new LinkedHashSet<>();
    }
    else
    {
      this.attributes = attributes;
    }

    if (scope == null)
    {
      this.scope = DEFAULT_SEARCH_SCOPE;
    }
    else
    {
      this.scope = scope;
    }

    if (rawFilter != null)
    {
      this.rawFilter = rawFilter;
    }
    else
    {
      setFilter(SearchFilter.objectClassPresent());
    }

    if (extensions == null)
    {
      this.extensions = new LinkedList<>();
    }
    else
    {
      this.extensions = extensions;
    }
  }



  /**
   * Creates a new LDAP URL with the provided information.
   *
   * @param  scheme      The scheme (i.e., protocol) for this LDAP
   *                     URL.
   * @param  host        The address for this LDAP URL.
   * @param  port        The port number for this LDAP URL.
   * @param  baseDN      The base DN for this LDAP URL.
   * @param  attributes  The set of requested attributes for this LDAP
   *                     URL.
   * @param  scope       The search scope for this LDAP URL.
   * @param  filter      The search filter for this LDAP URL.
   * @param  extensions  The set of extensions for this LDAP URL.
   */
  public LDAPURL(String scheme, String host, int port, DN baseDN,
                 LinkedHashSet<String> attributes, SearchScope scope,
                 SearchFilter filter, List<String> extensions)
  {
    this.host = toLowerCase(host);


    if (scheme == null)
    {
      this.scheme = "ldap";
    }
    else
    {
      this.scheme = toLowerCase(scheme);
    }

    this.port = toPort(port);

    if (baseDN == null)
    {
      this.baseDN    = DEFAULT_BASE_DN;
      this.rawBaseDN = DEFAULT_BASE_DN.toString();
    }
    else
    {
      this.baseDN    = baseDN;
      this.rawBaseDN = baseDN.toString();
    }

    if (attributes == null)
    {
      this.attributes = new LinkedHashSet<>();
    }
    else
    {
      this.attributes = attributes;
    }

    if (scope == null)
    {
      this.scope = DEFAULT_SEARCH_SCOPE;
    }
    else
    {
      this.scope = scope;
    }

    if (filter == null)
    {
      this.filter    = DEFAULT_SEARCH_FILTER;
      this.rawFilter = DEFAULT_SEARCH_FILTER.toString();
    }
    else
    {
      this.filter    = filter;
      this.rawFilter = filter.toString();
    }

    if (extensions == null)
    {
      this.extensions = new LinkedList<>();
    }
    else
    {
      this.extensions = extensions;
    }
  }



  /**
   * Decodes the provided string as an LDAP URL.
   *
   * @param  url          The URL string to be decoded.
   * @param  fullyDecode  Indicates whether the URL should be fully
   *                      decoded (e.g., parsing the base DN and
   *                      search filter) or just leaving them in their
   *                      string representations.  The latter may be
   *                      required for client-side use.
   *
   * @return  The LDAP URL decoded from the provided string.
   *
   * @throws  DirectoryException  If a problem occurs while attempting
   *                              to decode the provided string as an
   *                              LDAP URL.
   */
  public static LDAPURL decode(String url, boolean fullyDecode)
         throws DirectoryException
  {
    // Find the "://" component, which will separate the scheme from
    // the host.
    String scheme;
    int schemeEndPos = url.indexOf("://");
    if (schemeEndPos < 0)
    {
      LocalizableMessage message = ERR_LDAPURL_NO_COLON_SLASH_SLASH.get(url);
      throw new DirectoryException(INVALID_ATTRIBUTE_SYNTAX, message);
    }
    else if (schemeEndPos == 0)
    {
      LocalizableMessage message = ERR_LDAPURL_NO_SCHEME.get(url);
      throw new DirectoryException(INVALID_ATTRIBUTE_SYNTAX, message);
    }
    else
    {
      scheme = urlDecode(url.substring(0, schemeEndPos));
      // FIXME also need to check that the scheme is actually ldap/ldaps!!
    }


    // If the "://" was the end of the URL, then we're done.
    int length = url.length();
    if (length == schemeEndPos+3)
    {
      return new LDAPURL(scheme, null, DEFAULT_PORT, DEFAULT_BASE_DN,
                         null, DEFAULT_SEARCH_SCOPE,
                         DEFAULT_SEARCH_FILTER, null);
    }


    // Look at the next character.  If it's anything but a slash, then
    // it should be part of the host and optional port.
    String host     = null;
    int    port     = DEFAULT_PORT;
    int    startPos = schemeEndPos + 3;
    int    pos      = startPos;
    while (pos < length)
    {
      char c = url.charAt(pos);
      if (c == '/')
      {
        break;
      }
      pos++;
    }

    if (pos > startPos)
    {
      String hostPort = url.substring(startPos, pos);
      int colonPos = hostPort.lastIndexOf(':');
      if (colonPos < 0)
      {
        host = urlDecode(hostPort);
      }
      else if (colonPos == 0)
      {
        LocalizableMessage message = ERR_LDAPURL_NO_HOST.get(url);
        throw new DirectoryException(INVALID_ATTRIBUTE_SYNTAX, message);
      }
      else if (colonPos == (hostPort.length() - 1))
      {
        LocalizableMessage message = ERR_LDAPURL_NO_PORT.get(url);
        throw new DirectoryException(INVALID_ATTRIBUTE_SYNTAX, message);
      }
      else
      {
        try
        {
          final HostPort hp = HostPort.valueOf(hostPort);
          host = urlDecode(hp.getHost());
          port = hp.getPort();
        }
        catch (NumberFormatException e)
        {
          LocalizableMessage message = ERR_LDAPURL_CANNOT_DECODE_PORT.get(
              url, hostPort.substring(colonPos+1));
          throw new DirectoryException(INVALID_ATTRIBUTE_SYNTAX, message);
        }
        catch (IllegalArgumentException e)
        {
          LocalizableMessage message = ERR_LDAPURL_INVALID_PORT.get(url, port);
          throw new DirectoryException(INVALID_ATTRIBUTE_SYNTAX, message);
        }
      }
    }


    // Move past the slash.  If we're at or past the end of the
    // string, then we're done.
    pos++;
    if (pos > length)
    {
      return new LDAPURL(scheme, host, port, DEFAULT_BASE_DN, null,
                         DEFAULT_SEARCH_SCOPE, DEFAULT_SEARCH_FILTER,
                         null);
    }
    startPos = pos;


    // The next delimiter should be a question mark.  If there isn't
    // one, then the rest of the value must be the base DN.
    String baseDNString = null;
    pos = url.indexOf('?', startPos);
    if (pos < 0)
    {
      baseDNString = urlDecode(url.substring(startPos));
      startPos = length;
    }
    else
    {
      baseDNString = urlDecode(url.substring(startPos, pos));
      startPos = pos+1;
    }

    DN baseDN;
    if (fullyDecode)
    {
      baseDN = DN.valueOf(baseDNString);
    }
    else
    {
      baseDN = null;
    }


    if (startPos >= length)
    {
      if (fullyDecode)
      {
        return new LDAPURL(scheme, host, port, baseDN, null,
                           DEFAULT_SEARCH_SCOPE,
                           DEFAULT_SEARCH_FILTER, null);
      }
      else
      {
        return new LDAPURL(scheme, host, port, baseDNString, null,
                           DEFAULT_SEARCH_SCOPE, null, null);
      }
    }


    // Find the next question mark (or the end of the string if there
    // aren't any more) and get the attribute list from it.
    String attrsString;
    pos = url.indexOf('?', startPos);
    if (pos < 0)
    {
      attrsString = url.substring(startPos);
      startPos = length;
    }
    else
    {
      attrsString = url.substring(startPos, pos);
      startPos = pos+1;
    }

    LinkedHashSet<String> attributes = new LinkedHashSet<>();
    StringTokenizer tokenizer = new StringTokenizer(attrsString, ",");
    while (tokenizer.hasMoreTokens())
    {
      attributes.add(urlDecode(tokenizer.nextToken()));
    }

    if (startPos >= length)
    {
      if (fullyDecode)
      {
        return new LDAPURL(scheme, host, port, baseDN, attributes,
                           DEFAULT_SEARCH_SCOPE,
                           DEFAULT_SEARCH_FILTER, null);
      }
      else
      {
        return new LDAPURL(scheme, host, port, baseDNString,
                           attributes, DEFAULT_SEARCH_SCOPE, null,
                           null);
      }
    }


    // Find the next question mark (or the end of the string if there
    // aren't any more) and get the scope from it.
    String scopeString;
    pos = url.indexOf('?', startPos);
    if (pos < 0)
    {
      scopeString = toLowerCase(urlDecode(url.substring(startPos)));
      startPos = length;
    }
    else
    {
      scopeString =
           toLowerCase(urlDecode(url.substring(startPos, pos)));
      startPos = pos+1;
    }

    SearchScope scope;
    if (scopeString.equals(""))
    {
      scope = DEFAULT_SEARCH_SCOPE;
    }
    else if (scopeString.equals("base"))
    {
      scope = SearchScope.BASE_OBJECT;
    }
    else if (scopeString.equals("one"))
    {
      scope = SearchScope.SINGLE_LEVEL;
    }
    else if (scopeString.equals("sub"))
    {
      scope = SearchScope.WHOLE_SUBTREE;
    }
    else if (scopeString.equals("subord") ||
             scopeString.equals("subordinate"))
    {
      scope = SearchScope.SUBORDINATES;
    }
    else
    {
      LocalizableMessage message = ERR_LDAPURL_INVALID_SCOPE_STRING.get(url, scopeString);
      throw new DirectoryException(
                     ResultCode.INVALID_ATTRIBUTE_SYNTAX, message);
    }

    if (startPos >= length)
    {
      if (fullyDecode)
      {
        return new LDAPURL(scheme, host, port, baseDN, attributes,
                           scope, DEFAULT_SEARCH_FILTER, null);
      }
      else
      {
        return new LDAPURL(scheme, host, port, baseDNString,
                           attributes, scope, null, null);
      }
    }


    // Find the next question mark (or the end of the string if there
    // aren't any more) and get the filter from it.
    String filterString;
    pos = url.indexOf('?', startPos);
    if (pos < 0)
    {
      filterString = urlDecode(url.substring(startPos));
      startPos = length;
    }
    else
    {
      filterString = urlDecode(url.substring(startPos, pos));
      startPos = pos+1;
    }

    SearchFilter filter;
    if (fullyDecode)
    {
      if (filterString.equals(""))
      {
        filter = DEFAULT_SEARCH_FILTER;
      }
      else
      {
        filter = SearchFilter.createFilterFromString(filterString);
      }

      if (startPos >= length)
      {
        if (fullyDecode)
        {
          return new LDAPURL(scheme, host, port, baseDN, attributes,
                             scope, filter, null);
        }
        else
        {
          return new LDAPURL(scheme, host, port, baseDNString,
                             attributes, scope, filterString, null);
        }
      }
    }
    else
    {
      filter = null;
    }


    // The rest of the string must be the set of extensions.
    String extensionsString = url.substring(startPos);
    LinkedList<String> extensions = new LinkedList<>();
    tokenizer = new StringTokenizer(extensionsString, ",");
    while (tokenizer.hasMoreTokens())
    {
      extensions.add(urlDecode(tokenizer.nextToken()));
    }


    if (fullyDecode)
    {
      return new LDAPURL(scheme, host, port, baseDN, attributes,
                         scope, filter, extensions);
    }
    else
    {
      return new LDAPURL(scheme, host, port, baseDNString, attributes,
                         scope, filterString, extensions);
    }
  }



  /**
   * Converts the provided string to a form that has decoded "special"
   * characters that have been encoded for use in an LDAP URL.
   *
   * @param  s  The string to be decoded.
   *
   * @return  The decoded string.
   *
   * @throws  DirectoryException  If a problem occurs while attempting
   *                              to decode the contents of the
   *                              provided string.
   */
  static String urlDecode(String s) throws DirectoryException
  {
    if (s == null)
    {
      return "";
    }

    byte[] stringBytes  = getBytes(s);
    int    length       = stringBytes.length;
    byte[] decodedBytes = new byte[length];
    int    pos          = 0;

    for (int i=0; i < length; i++)
    {
      if (stringBytes[i] == '%')
      {
        // There must be at least two bytes left.  If not, then that's
        // a problem.
        if (i+2 > length)
        {
          LocalizableMessage message = ERR_LDAPURL_PERCENT_TOO_CLOSE_TO_END.get(s, i);
          throw new DirectoryException(
                        ResultCode.INVALID_ATTRIBUTE_SYNTAX, message);
        }

        byte b;
        switch (stringBytes[++i])
        {
          case '0':
            b = (byte) 0x00;
            break;
          case '1':
            b = (byte) 0x10;
            break;
          case '2':
            b = (byte) 0x20;
            break;
          case '3':
            b = (byte) 0x30;
            break;
          case '4':
            b = (byte) 0x40;
            break;
          case '5':
            b = (byte) 0x50;
            break;
          case '6':
            b = (byte) 0x60;
            break;
          case '7':
            b = (byte) 0x70;
            break;
          case '8':
            b = (byte) 0x80;
            break;
          case '9':
            b = (byte) 0x90;
            break;
          case 'a':
          case 'A':
            b = (byte) 0xA0;
            break;
          case 'b':
          case 'B':
            b = (byte) 0xB0;
            break;
          case 'c':
          case 'C':
            b = (byte) 0xC0;
            break;
          case 'd':
          case 'D':
            b = (byte) 0xD0;
            break;
          case 'e':
          case 'E':
            b = (byte) 0xE0;
            break;
          case 'f':
          case 'F':
            b = (byte) 0xF0;
            break;
          default:
            LocalizableMessage message = ERR_LDAPURL_INVALID_HEX_BYTE.get(s, i);
            throw new DirectoryException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, message);
        }

        switch (stringBytes[++i])
        {
          case '0':
            break;
          case '1':
            b |= 0x01;
            break;
          case '2':
            b |= 0x02;
            break;
          case '3':
            b |= 0x03;
            break;
          case '4':
            b |= 0x04;
            break;
          case '5':
            b |= 0x05;
            break;
          case '6':
            b |= 0x06;
            break;
          case '7':
            b |= 0x07;
            break;
          case '8':
            b |= 0x08;
            break;
          case '9':
            b |= 0x09;
            break;
          case 'a':
          case 'A':
            b |= 0x0A;
            break;
          case 'b':
          case 'B':
            b |= 0x0B;
            break;
          case 'c':
          case 'C':
            b |= 0x0C;
            break;
          case 'd':
          case 'D':
            b |= 0x0D;
            break;
          case 'e':
          case 'E':
            b |= 0x0E;
            break;
          case 'f':
          case 'F':
            b |= 0x0F;
            break;
          default:
            LocalizableMessage message = ERR_LDAPURL_INVALID_HEX_BYTE.get(s, i);
            throw new DirectoryException(ResultCode.INVALID_ATTRIBUTE_SYNTAX, message);
        }

        decodedBytes[pos++] = b;
      }
      else
      {
        decodedBytes[pos++] = stringBytes[i];
      }
    }

    try
    {
      return new String(decodedBytes, 0, pos, "UTF-8");
    }
    catch (Exception e)
    {
      logger.traceException(e);

      // This should never happen.
      LocalizableMessage message = ERR_LDAPURL_CANNOT_CREATE_UTF8_STRING.get(
          getExceptionMessage(e));
      throw new DirectoryException(
                     ResultCode.INVALID_ATTRIBUTE_SYNTAX, message);
    }
  }



  /**
   * Encodes the provided string portion for inclusion in an LDAP URL
   * and appends it to the provided buffer.
   *
   * @param  s            The string portion to be encoded.
   * @param  isExtension  Indicates whether the provided component is
   *                      an extension and therefore needs to have
   *                      commas encoded.
   * @param  buffer       The buffer to which the information should
   *                      be appended.
   */
  private static void urlEncode(String s, boolean isExtension,
                                StringBuilder buffer)
  {
    if (s == null)
    {
      return;
    }

    int length = s.length();

    for (int i=0; i < length; i++)
    {
      char c = s.charAt(i);
      if (isAlpha(c) || isDigit(c))
      {
        buffer.append(c);
        continue;
      }

      if (c == ',')
      {
        if (isExtension)
        {
          hexEncode(c, buffer);
        }
        else
        {
          buffer.append(c);
        }

        continue;
      }

      switch (c)
      {
        case '-':
        case '.':
        case '_':
        case '~':
        case ':':
        case '/':
        case '#':
        case '[':
        case ']':
        case '@':
        case '!':
        case '$':
        case '&':
        case '\'':
        case '(':
        case ')':
        case '*':
        case '+':
        case ';':
        case '=':
          buffer.append(c);
          break;
        default:
          hexEncode(c, buffer);
          break;
      }
    }
  }



  /**
   * Appends a percent-encoded representation of the provided
   * character to the given buffer.
   *
   * @param  c       The character to add to the buffer.
   * @param  buffer  The buffer to which the percent-encoded
   *                 representation should be written.
   */
  private static void hexEncode(char c, StringBuilder buffer)
  {
    if ((c & (byte) 0xFF) == c)
    {
      // It's a single byte.
      buffer.append('%');
      buffer.append(byteToHex((byte) c));
    }
    else
    {
      // It requires two bytes, and each should be prefixed by a
      // percent sign.
      buffer.append('%');
      byte b1 = (byte) ((c >>> 8) & 0xFF);
      buffer.append(byteToHex(b1));

      buffer.append('%');
      byte b2 = (byte) (c & 0xFF);
      buffer.append(byteToHex(b2));
    }
  }



  /**
   * Retrieves the scheme for this LDAP URL.
   *
   * @return  The scheme for this LDAP URL.
   */
  public String getScheme()
  {
    return scheme;
  }



  /**
   * Specifies the scheme for this LDAP URL.
   *
   * @param  scheme  The scheme for this LDAP URL.
   */
  public void setScheme(String scheme)
  {
    if (scheme == null)
    {
      this.scheme = DEFAULT_SCHEME;
    }
    else
    {
      this.scheme = scheme;
    }
  }



  /**
   * Retrieves the host for this LDAP URL.
   *
   * @return  The host for this LDAP URL, or <CODE>null</CODE> if none
   *          was provided.
   */
  public String getHost()
  {
    return host;
  }



  /**
   * Specifies the host for this LDAP URL.
   *
   * @param  host  The host for this LDAP URL.
   */
  public void setHost(String host)
  {
    this.host = host;
  }



  /**
   * Retrieves the port for this LDAP URL.
   *
   * @return  The port for this LDAP URL.
   */
  public int getPort()
  {
    return port;
  }



  /**
   * Specifies the port for this LDAP URL.
   *
   * @param  port  The port for this LDAP URL.
   */
  public void setPort(int port)
  {
    this.port = toPort(port);
  }

  private int toPort(int port)
  {
    if (0 < port && port <= 65535)
    {
      return port;
    }
    return DEFAULT_PORT;
  }

  /**
   * Retrieve the raw, unprocessed base DN for this LDAP URL.
   *
   * @return  The raw, unprocessed base DN for this LDAP URL, or
   *          <CODE>null</CODE> if none was given (in which case a
   *          default of the null DN "" should be assumed).
   */
  public String getRawBaseDN()
  {
    return rawBaseDN;
  }



  /**
   * Specifies the raw, unprocessed base DN for this LDAP URL.
   *
   * @param  rawBaseDN  The raw, unprocessed base DN for this LDAP
   *                    URL.
   */
  public void setRawBaseDN(String rawBaseDN)
  {
    this.rawBaseDN = rawBaseDN;
    this.baseDN    = null;
  }



  /**
   * Retrieves the processed DN for this LDAP URL.
   *
   * @return  The processed DN for this LDAP URL.
   *
   * @throws  DirectoryException  If the raw base DN cannot be decoded
   *                              as a valid DN.
   */
  public DN getBaseDN()
         throws DirectoryException
  {
    if (baseDN == null)
    {
      if (rawBaseDN == null || rawBaseDN.length() == 0)
      {
        return DEFAULT_BASE_DN;
      }

      baseDN = DN.valueOf(rawBaseDN);
    }

    return baseDN;
  }



  /**
   * Specifies the base DN for this LDAP URL.
   *
   * @param  baseDN  The base DN for this LDAP URL.
   */
  public void setBaseDN(DN baseDN)
  {
    if (baseDN == null)
    {
      this.baseDN    = null;
      this.rawBaseDN = null;
    }
    else
    {
      this.baseDN    = baseDN;
      this.rawBaseDN = baseDN.toString();
    }
  }



  /**
   * Retrieves the set of attributes for this LDAP URL.  The contents
   * of the returned set may be altered by the caller.
   *
   * @return  The set of attributes for this LDAP URL.
   */
  public LinkedHashSet<String> getAttributes()
  {
    return attributes;
  }



  /**
   * Retrieves the search scope for this LDAP URL.
   *
   * @return  The search scope for this LDAP URL, or <CODE>null</CODE>
   *          if none was given (in which case the base-level scope
   *          should be assumed).
   */
  public SearchScope getScope()
  {
    return scope;
  }



  /**
   * Specifies the search scope for this LDAP URL.
   *
   * @param  scope  The search scope for this LDAP URL.
   */
  public void setScope(SearchScope scope)
  {
    if (scope == null)
    {
      this.scope = DEFAULT_SEARCH_SCOPE;
    }
    else
    {
      this.scope = scope;
    }
  }



  /**
   * Retrieves the raw, unprocessed search filter for this LDAP URL.
   *
   * @return  The raw, unprocessed search filter for this LDAP URL, or
   *          <CODE>null</CODE> if none was given (in which case a
   *          default filter of "(objectClass=*)" should be assumed).
   */
  public String getRawFilter()
  {
    return rawFilter;
  }



  /**
   * Specifies the raw, unprocessed search filter for this LDAP URL.
   *
   * @param  rawFilter  The raw, unprocessed search filter for this
   *                    LDAP URL.
   */
  public void setRawFilter(String rawFilter)
  {
    this.rawFilter = rawFilter;
    this.filter    = null;
  }



  /**
   * Retrieves the processed search filter for this LDAP URL.
   *
   * @return  The processed search filter for this LDAP URL.
   *
   * @throws  DirectoryException  If a problem occurs while attempting
   *                              to decode the raw filter.
   */
  public SearchFilter getFilter()
         throws DirectoryException
  {
    if (filter == null)
    {
      if (rawFilter == null)
      {
        filter = DEFAULT_SEARCH_FILTER;
      }
      else
      {
        filter = SearchFilter.createFilterFromString(rawFilter);
      }
    }

    return filter;
  }



  /**
   * Specifies the search filter for this LDAP URL.
   *
   * @param  filter  The search filter for this LDAP URL.
   */
  public void setFilter(SearchFilter filter)
  {
    if (filter == null)
    {
      this.rawFilter = null;
      this.filter    = null;
    }
    else
    {
      this.rawFilter = filter.toString();
      this.filter    = filter;
    }
  }



  /**
   * Retrieves the set of extensions for this LDAP URL.  The contents
   * of the returned list may be altered by the caller.
   *
   * @return  The set of extensions for this LDAP URL.
   */
  public List<String> getExtensions()
  {
    return extensions;
  }



  /**
   * Indicates whether the provided entry matches the criteria defined
   * in this LDAP URL.
   *
   * @param  entry  The entry for which to make the determination.
   *
   * @return  {@code true} if the provided entry does match the
   *          criteria specified in this LDAP URL, or {@code false} if
   *          it does not.
   *
   * @throws  DirectoryException  If a problem occurs while attempting
   *                              to make the determination.
   */
  public boolean matchesEntry(Entry entry)
         throws DirectoryException
  {
    SearchScope scope = getScope();
    if (scope == null)
    {
      scope = SearchScope.BASE_OBJECT;
    }

    return entry.matchesBaseAndScope(getBaseDN(), scope)
        && getFilter().matchesEntry(entry);
  }



  /**
   * Indicates whether the provided object is equal to this LDAP URL.
   *
   * @param  o  The object for which to make the determination.
   *
   * @return  <CODE>true</CODE> if the object is equal to this LDAP
   *          URL, or <CODE>false</CODE> if not.
   */
  @Override
  public boolean equals(Object o)
  {
    if (o == this)
    {
      return true;
    }
    if (!(o instanceof LDAPURL))
    {
      return false;
    }

    LDAPURL url = (LDAPURL) o;
    return scheme.equals(url.getScheme())
        && hostEquals(url)
        && port == url.getPort()
        && baseDnsEqual(url)
        && scope.equals(url.getScope())
        && filtersEqual(url)
        && attributesEqual(url.getAttributes())
        && extensionsEqual(url.getExtensions());
  }

  private boolean hostEquals(LDAPURL url)
  {
    if (host != null)
    {
      return host.equalsIgnoreCase(url.getHost());
    }
    return url.getHost() == null;
  }

  private boolean baseDnsEqual(LDAPURL url)
  {
    try
    {
      return getBaseDN().equals(url.getBaseDN());
    }
    catch (Exception e)
    {
      logger.traceException(e);
      return Objects.equals(rawBaseDN, url.getRawBaseDN());
    }
  }

  private boolean filtersEqual(LDAPURL url)
  {
    try
    {
      return getFilter().equals(url.getFilter());
    }
    catch (Exception e)
    {
      logger.traceException(e);
      return Objects.equals(rawFilter, url.getRawFilter());
    }
  }

  private boolean attributesEqual(Set<String> urlAttrs)
  {
    if (attributes.size() != urlAttrs.size())
    {
      return false;
    }

    for (String attr : attributes)
    {
      if (!urlAttrs.contains(attr) && !containsIgnoreCase(urlAttrs, attr))
      {
        return false;
      }
    }
    return true;
  }

  private boolean containsIgnoreCase(Set<String> urlAttrs, String attr)
  {
    for (String attr2 : urlAttrs)
    {
      if (attr.equalsIgnoreCase(attr2))
      {
        return true;
      }
    }
    return false;
  }

  private boolean extensionsEqual(List<String> extensions)
  {
    if (this.extensions.size() != extensions.size())
    {
      return false;
    }

    for (String ext : this.extensions)
    {
      if (!extensions.contains(ext))
      {
        return false;
      }
    }
    return true;
  }



  /**
   * Retrieves the hash code for this LDAP URL.
   *
   * @return  The hash code for this LDAP URL.
   */
  @Override
  public int hashCode()
  {
    int hashCode = 0;

    hashCode += scheme.hashCode();

    if (host != null)
    {
      hashCode += toLowerCase(host).hashCode();
    }

    hashCode += port;

    try
    {
      hashCode += getBaseDN().hashCode();
    }
    catch (Exception e)
    {
      logger.traceException(e);

      if (rawBaseDN != null)
      {
        hashCode += rawBaseDN.hashCode();
      }
    }

    hashCode += getScope().intValue();

    for (String attr : attributes)
    {
      hashCode += toLowerCase(attr).hashCode();
    }

    try
    {
      hashCode += getFilter().hashCode();
    }
    catch (Exception e)
    {
      logger.traceException(e);

      if (rawFilter != null)
      {
        hashCode += rawFilter.hashCode();
      }
    }

    for (String ext : extensions)
    {
      hashCode += ext.hashCode();
    }

    return hashCode;
  }



  /**
   * Retrieves a string representation of this LDAP URL.
   *
   * @return  A string representation of this LDAP URL.
   */
  @Override
  public String toString()
  {
    StringBuilder buffer = new StringBuilder();
    toString(buffer, false);
    return buffer.toString();
  }



  /**
   * Appends a string representation of this LDAP URL to the provided
   * buffer.
   *
   * @param  buffer    The buffer to which the information is to be
   *                   appended.
   * @param  baseOnly  Indicates whether the resulting URL string
   *                   should only include the portion up to the base
   *                   DN, omitting the attributes, scope, filter, and
   *                   extensions.
   */
  public void toString(StringBuilder buffer, boolean baseOnly)
  {
    urlEncode(scheme, false, buffer);
    buffer.append("://");

    if (host != null)
    {
      urlEncode(host, false, buffer);
      buffer.append(":");
      buffer.append(port);
    }

    buffer.append("/");
    urlEncode(rawBaseDN, false, buffer);

    if (baseOnly)
    {
      // If there are extensions, then we need to include them.
      // Technically, we only have to include critical extensions, but
      // we'll use all of them.
      if (! extensions.isEmpty())
      {
        buffer.append("????");
        Iterator<String> iterator = extensions.iterator();
        urlEncode(iterator.next(), true, buffer);

        while (iterator.hasNext())
        {
          buffer.append(",");
          urlEncode(iterator.next(), true, buffer);
        }
      }

      return;
    }

    buffer.append("?");
    if (! attributes.isEmpty())
    {
      Iterator<String> iterator = attributes.iterator();
      urlEncode(iterator.next(), false, buffer);

      while (iterator.hasNext())
      {
        buffer.append(",");
        urlEncode(iterator.next(), false, buffer);
      }
    }

    buffer.append("?");
    switch (scope.asEnum())
    {
      case BASE_OBJECT:
        buffer.append("base");
        break;
      case SINGLE_LEVEL:
        buffer.append("one");
        break;
      case WHOLE_SUBTREE:
        buffer.append("sub");
        break;
      case SUBORDINATES:
        buffer.append("subordinate");
        break;
    }

    buffer.append("?");
    urlEncode(rawFilter, false, buffer);

    if (! extensions.isEmpty())
    {
      buffer.append("?");
      Iterator<String> iterator = extensions.iterator();
      urlEncode(iterator.next(), true, buffer);

      while (iterator.hasNext())
      {
        buffer.append(",");
        urlEncode(iterator.next(), true, buffer);
      }
    }
  }
}
