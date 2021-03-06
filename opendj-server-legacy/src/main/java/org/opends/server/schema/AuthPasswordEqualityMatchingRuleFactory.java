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
 * Copyright 2008 Sun Microsystems, Inc.
 * Portions Copyright 2014-2016 ForgeRock AS.
 */
package org.opends.server.schema;

import java.util.Collection;
import java.util.Collections;

import org.forgerock.opendj.config.server.ConfigException;
import org.forgerock.opendj.ldap.schema.CoreSchema;
import org.forgerock.opendj.ldap.schema.SchemaBuilder;
import org.forgerock.opendj.server.config.server.MatchingRuleCfg;
import org.opends.server.api.MatchingRuleFactory;
import org.opends.server.types.InitializationException;

import static org.opends.server.schema.SchemaConstants.*;

/**
 * This class is a factory class for {@link AuthPasswordEqualityMatchingRule}.
 */
public final class AuthPasswordEqualityMatchingRuleFactory
        extends MatchingRuleFactory<MatchingRuleCfg>
{
  private org.forgerock.opendj.ldap.schema.MatchingRule matchingRule;

 /** {@inheritDoc} */
 @Override
 public final void initializeMatchingRule(MatchingRuleCfg configuration)
         throws ConfigException, InitializationException
 {
   matchingRule = new SchemaBuilder(CoreSchema.getInstance()).buildMatchingRule(EMR_AUTH_PASSWORD_OID)
       .names(EMR_AUTH_PASSWORD_NAME)
       .syntaxOID(SYNTAX_AUTH_PASSWORD_OID).description(EMR_AUTH_PASSWORD_DESCRIPTION)
       .implementation(new AuthPasswordEqualityMatchingRule())
       .addToSchema().toSchema().getMatchingRule(EMR_AUTH_PASSWORD_OID);
 }

 /** {@inheritDoc} */
 @Override
 public final Collection<org.forgerock.opendj.ldap.schema.MatchingRule> getMatchingRules()
 {
    return Collections.singleton(matchingRule);
 }
}
