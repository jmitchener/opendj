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
package org.opends.server.schema;

import static org.opends.server.schema.SchemaConstants.*;

import org.forgerock.opendj.ldap.schema.Schema;
import org.forgerock.opendj.ldap.schema.Syntax;
import org.forgerock.opendj.server.config.server.AttributeSyntaxCfg;
import org.opends.server.api.AttributeSyntax;

/**
 * This class defines the delivery method attribute syntax.  This contains one
 * or more of a fixed set of values.  If there are multiple values, then they
 * are separated by spaces with a dollar sign between them.  The allowed values
 * include:
 *
 * <UL>
 *   <LI>any</LI>
 *   <LI>mhs</LI>
 *   <LI>physical</LI>
 *   <LI>telex</LI>
 *   <LI>teletex</LI>
 *   <LI>g3fax</LI>
 *   <LI>g4fax</LI>
 *   <LI>ia5</LI>
 *   <LI>videotex</LI>
 *   <LI>telephone</LI>
 * </UL>
 */
public class DeliveryMethodSyntax
       extends AttributeSyntax<AttributeSyntaxCfg>
{

  /**
   * Creates a new instance of this syntax.  Note that the only thing that
   * should be done here is to invoke the default constructor for the
   * superclass.  All initialization should be performed in the
   * <CODE>initializeSyntax</CODE> method.
   */
  public DeliveryMethodSyntax()
  {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public Syntax getSDKSyntax(Schema schema)
  {
    return schema.getSyntax(SchemaConstants.SYNTAX_DELIVERY_METHOD_OID);
  }

  /**
   * Retrieves the common name for this attribute syntax.
   *
   * @return  The common name for this attribute syntax.
   */
  @Override
  public String getName()
  {
    return SYNTAX_DELIVERY_METHOD_NAME;
  }

  /**
   * Retrieves the OID for this attribute syntax.
   *
   * @return  The OID for this attribute syntax.
   */
  @Override
  public String getOID()
  {
    return SYNTAX_DELIVERY_METHOD_OID;
  }

  /**
   * Retrieves a description for this attribute syntax.
   *
   * @return  A description for this attribute syntax.
   */
  @Override
  public String getDescription()
  {
    return SYNTAX_DELIVERY_METHOD_DESCRIPTION;
  }
}

