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
 * Portions Copyright 2013-2016 ForgeRock AS.
 */
package org.opends.server.types;

import java.util.Collection;
import java.util.Set;

import org.forgerock.i18n.LocalizableMessage;
import org.forgerock.opendj.ldap.DN;
import org.forgerock.opendj.ldap.ResultCode;
import org.forgerock.opendj.ldap.schema.AttributeType;
import org.forgerock.opendj.ldap.schema.MatchingRule;
import org.forgerock.opendj.ldap.schema.MatchingRuleUse;
import org.forgerock.opendj.ldap.schema.NameForm;
import org.forgerock.opendj.ldap.schema.ObjectClass;
import org.forgerock.opendj.ldap.schema.Syntax;
import org.opends.server.api.AlertGenerator;
import org.opends.server.api.ExtendedOperationHandler;
import org.opends.server.api.SASLMechanismHandler;
import org.opends.server.api.ServerShutdownListener;
import org.opends.server.core.DirectoryServer;

import com.forgerock.opendj.util.OperatingSystem;

/**
 * This interface defines a set of methods that may be used by
 * third-party code to obtain information about the core Directory
 * Server configuration and the instances of various kinds of
 * components that have registered themselves with the server.
 * <BR><BR>
 * Note that this interface is not intended to be implemented by any
 * third-party code.  It is merely used to control which elements are
 * intended for use by external classes.
 */
@org.opends.server.types.PublicAPI(
     stability=org.opends.server.types.StabilityLevel.VOLATILE,
     mayInstantiate=false,
     mayExtend=false,
     mayInvoke=true)
public final class DirectoryConfig
{
  /**
   * Retrieves a reference to the Directory Server crypto manager.
   *
   * @return  A reference to the Directory Server crypto manager.
   */
  public static CryptoManager getCryptoManager()
  {
    return DirectoryServer.getCryptoManager();
  }

  /**
   * Retrieves the operating system on which the Directory Server is
   * running.
   *
   * @return  The operating system on which the Directory Server is
   *          running.
   */
  public static OperatingSystem getOperatingSystem()
  {
    return DirectoryServer.getOperatingSystem();
  }

  /**
   * Retrieves the path to the root directory for this instance of the
   * Directory Server.
   *
   * @return  The path to the root directory for this instance of the
   *          Directory Server.
  */
  public static String getServerRoot()
  {
    return DirectoryServer.getServerRoot();
  }

  /**
   * Retrieves the time that the Directory Server was started, in
   * milliseconds since the epoch.
   *
   * @return  The time that the Directory Server was started, in
   *          milliseconds since the epoch.
   */
  public static long getStartTime()
  {
    return DirectoryServer.getStartTime();
  }

  /**
   * Retrieves the time that the Directory Server was started,
   * formatted in UTC.
   *
   * @return  The time that the Directory Server was started,
   *          formatted in UTC.
   */
  public static String getStartTimeUTC()
  {
    return DirectoryServer.getStartTimeUTC();
  }

  /**
   * Retrieves a reference to the Directory Server schema.
   *
   * @return  A reference to the Directory Server schema.
   */
  public static Schema getSchema()
  {
    return DirectoryServer.getSchema();
  }

  /**
   * Retrieves the set of matching rules registered with the Directory
   * Server.
   *
   * @return  The set of matching rules registered with the Directory
   *          Server.
   */
  public static Collection<MatchingRule> getMatchingRules()
  {
    return DirectoryServer.getMatchingRules();
  }

  /**
   * Retrieves the matching rule with the specified name or OID.
   *
   * @param  lowerName  The lowercase name or OID for the matching
   *                    rule to retrieve.
   *
   * @return  The requested matching rule, or <CODE>null</CODE> if no
   *          such matching rule has been defined in the server.
   */
  public static MatchingRule getMatchingRule(String lowerName)
  {
    return DirectoryServer.getMatchingRule(lowerName);
  }

  /**
   * Retrieves the approximate matching rule with the specified name
   * or OID.
   *
   * @param  lowerName  The lowercase name or OID for the approximate
   *                    matching rule to retrieve.
   *
   * @return  The requested approximate matching rule, or
   *          <CODE>null</CODE> if no such matching rule has been
   *          defined in the server.
   */
  public static MatchingRule
       getApproximateMatchingRule(String lowerName)
  {
    return DirectoryServer.getMatchingRule(lowerName);
  }

  /**
   * Retrieves the equality matching rule with the specified name or
   * OID.
   *
   * @param  lowerName  The lowercase name or OID for the equality
   *                    matching rule to retrieve.
   *
   * @return  The requested equality matching rule, or
   *          <CODE>null</CODE> if no such matching rule has been
   *          defined in the server.
   */
  public static MatchingRule
       getEqualityMatchingRule(String lowerName)
  {
    return DirectoryServer.getMatchingRule(lowerName);
  }

  /**
   * Retrieves the ordering matching rule with the specified name or
   * OID.
   *
   * @param  lowerName  The lowercase name or OID for the ordering
   *                    matching rule to retrieve.
   *
   * @return  The requested ordering matching rule, or
   *          <CODE>null</CODE> if no such matching rule has been
   *          defined in the server.
   */
  public static MatchingRule
       getOrderingMatchingRule(String lowerName)
  {
    return DirectoryServer.getMatchingRule(lowerName);
  }

  /**
   * Retrieves the substring matching rule with the specified name or
   * OID.
   *
   * @param  lowerName  The lowercase name or OID for the substring
   *                    matching rule to retrieve.
   *
   * @return  The requested substring matching rule, or
   *          <CODE>null</CODE> if no such matching rule has been
   *          defined in the server.
   */
  public static MatchingRule
       getSubstringMatchingRule(String lowerName)
  {
    return DirectoryServer.getMatchingRule(lowerName);
  }

  /**
   * Retrieves the "top" objectClass, which should be the topmost
   * objectclass in the inheritance chain for most other
   * objectclasses.
   *
   * @return  The "top" objectClass.
   */
  public static ObjectClass getTopObjectClass()
  {
    return DirectoryServer.getTopObjectClass();
  }

  /**
   * Retrieves the set of attribute type definitions that have been
   * defined in the Directory Server.  The mapping will be between the
   * lowercase name or OID for each attribute type and the attribute
   * type implementation.  The same attribute type may be included
   * multiple times with different keys.  The returned map must not be
   * altered by the caller.
   *
   * @return The set of attribute type definitions that have been
   *         defined in the Directory Server.
   */
  public static Collection<AttributeType> getAttributeTypes()
  {
    return DirectoryServer.getAttributeTypes();
  }

  /**
   * Retrieves the attribute type for the "objectClass" attribute.
   *
   * @return  The attribute type for the "objectClass" attribute.
   */
  public static AttributeType getObjectClassAttributeType()
  {
    return DirectoryServer.getObjectClassAttributeType();
  }

  /**
   * Retrieves the set of attribute syntaxes defined in the Directory
   * Server.
   *
   * @return  The set of attribute syntaxes defined in the Directory
   *          Server.
   */
  public static Collection<Syntax> getAttributeSyntaxes()
  {
    return DirectoryServer.getAttributeSyntaxes();
  }

  /**
   * Retrieves the default attribute syntax that should be used for
   * attributes that are not defined in the server schema and are
   * meant to store binary values.
   *
   * @return  The default attribute syntax that should be used for
   *          attributes that are not defined in the server schema and
   *          are meant to store binary values.
   */
  public static Syntax getDefaultBinarySyntax()
  {
    return DirectoryServer.getDefaultBinarySyntax();
  }

  /**
   * Retrieves the default attribute syntax that should be used for
   * attributes that are not defined in the server schema and are
   * meant to store Boolean values.
   *
   * @return  The default attribute syntax that should be used for
   *          attributes that are not defined in the server schema and
   *          are meant to store Boolean values.
   */
  public static Syntax getDefaultBooleanSyntax()
  {
    return DirectoryServer.getDefaultBooleanSyntax();
  }

  /**
   * Retrieves the default attribute syntax that should be used for
   * attributes that are not defined in the server schema and are
   * meant to store DN values.
   *
   * @return  The default attribute syntax that should be used for
   *          attributes that are not defined in the server schema and
   *          are meant to store DN values.
   */
  public static Syntax getDefaultDNSyntax()
  {
    return DirectoryServer.getDefaultDNSyntax();
  }

  /**
   * Retrieves the default attribute syntax that should be used for
   * attributes that are not defined in the server schema and are
   * meant to store integer values.
   *
   * @return  The default attribute syntax that should be used for
   *          attributes that are not defined in the server schema and
   *          are meant to store integer values.
   */
  public static Syntax getDefaultIntegerSyntax()
  {
    return DirectoryServer.getDefaultIntegerSyntax();
  }

  /**
   * Retrieves the default attribute syntax that should be used for
   * attributes that are not defined in the server schema and are
   * meant to store string values.
   *
   * @return  The default attribute syntax that should be used for
   *          attributes that are not defined in the server schema and
   *          are meant to store string values.
   */
  public static Syntax getDefaultStringSyntax()
  {
    return DirectoryServer.getDefaultStringSyntax();
  }

  /**
   * Retrieves the set of matching rule uses defined in the Directory
   * Server.  The mapping will be between the matching rule and its
   * corresponding matching rule use.  The returned map must not be
   * altered by the caller.
   *
   * @return  The set of matching rule uses defined in the Directory
   *          Server.
   */
  public static Collection<MatchingRuleUse> getMatchingRuleUses()
  {
    return DirectoryServer.getMatchingRuleUses();
  }

  /**
   * Retrieves the DIT content rule associated with the specified
   * objectclass.
   *
   * @param  objectClass  The objectclass for which to retrieve the
   *                      associated DIT content rule.
   *
   * @return  The requested DIT content rule, or <CODE>null</CODE> if
   *          no such rule is defined in the schema.
   */
  public static DITContentRule
       getDITContentRule(ObjectClass objectClass)
  {
    return DirectoryServer.getDITContentRule(objectClass);
  }

  /**
   * Retrieves the DIT structure rule associated with the provided
   * rule ID.
   *
   * @param  ruleID  The rule ID for which to retrieve the associated
   *                 DIT structure rule.
   *
   * @return  The requested DIT structure rule, or <CODE>null</CODE>
   *          if no such rule is defined.
   */
  public static DITStructureRule getDITStructureRule(int ruleID)
  {
    return DirectoryServer.getDITStructureRule(ruleID);
  }

  /**
   * Retrieves the DIT structure rule associated with the provided
   * name form.
   *
   * @param  nameForm  The name form for which to retrieve the
   *                   associated DIT structure rule.
   *
   * @return  The requested DIT structure rule, or <CODE>null</CODE>
   *          if no such rule is defined.
   */
  public static DITStructureRule
       getDITStructureRule(NameForm nameForm)
  {
    return DirectoryServer.getDITStructureRule(nameForm);
  }

  /**
   * Retrieves the collection of name forms associated with the specified
   * structural objectclass.
   *
   * @param  objectClass  The structural objectclass for which to
   *                      retrieve the  associated name form.
   *
   * @return  The collection of requested name forms, or <CODE>null</CODE>
   *           if no such name form is defined in the schema.
   */
  public static Collection<NameForm> getNameForm(ObjectClass objectClass)
  {
    return DirectoryServer.getNameForm(objectClass);
  }

  /**
   * Registers the provided alert generator with the Directory Server.
   *
   * @param  alertGenerator  The alert generator to register.
   */
  public static void registerAlertGenerator(
                                AlertGenerator alertGenerator)
  {
    DirectoryServer.registerAlertGenerator(alertGenerator);
  }

  /**
   * Deregisters the provided alert generator with the Directory
   * Server.
   *
   * @param  alertGenerator  The alert generator to deregister.
   */
  public static void deregisterAlertGenerator(
                                AlertGenerator alertGenerator)
  {
    DirectoryServer.deregisterAlertGenerator(alertGenerator);
  }

  /**
   * Sends an alert notification with the provided information.
   *
   * @param  generator     The alert generator that created the alert.
   * @param  alertType     The alert type name for this alert.
   * @param  alertMessage  A message (possibly <CODE>null</CODE>) that
   *                       can provide more information about this
   *                       alert.
   */
  public static void
       sendAlertNotification(AlertGenerator generator,
                             String alertType,
                             LocalizableMessage alertMessage)
  {
    DirectoryServer.sendAlertNotification(generator, alertType,
            alertMessage);
  }

  /**
   * Retrieves the result code that should be used when the Directory
   * Server encounters an internal server error.
   *
   * @return  The result code that should be used when the Directory
   *          Server encounters an internal server error.
   */
  public static ResultCode getServerErrorResultCode()
  {
    return DirectoryServer.getServerErrorResultCode();
  }

  /**
   * Retrieves the entry with the requested DN.  It will first
   * determine which backend should be used for this DN and will then
   * use that backend to retrieve the entry.  The caller must already
   * hold the appropriate lock on the specified entry.
   *
   * @param  entryDN  The DN of the entry to retrieve.
   *
   * @return  The requested entry, or <CODE>null</CODE> if it does not
   *          exist.
   *
   * @throws  DirectoryException  If a problem occurs while attempting
   *                              to retrieve the entry.
   */
  public static Entry getEntry(DN entryDN)
         throws DirectoryException
  {
    return DirectoryServer.getEntry(entryDN);
  }

  /**
   * Indicates whether the specified entry exists in the Directory
   * Server.  The caller is not required to hold any locks when
   * invoking this method.
   *
   * @param  entryDN  The DN of the entry for which to make the
   *                  determination.
   *
   * @return  <CODE>true</CODE> if the specified entry exists in one
   *          of the backends, or <CODE>false</CODE> if it does not.
   *
   * @throws  DirectoryException  If a problem occurs while attempting
   *                              to make the determination.
   */
  public static boolean entryExists(DN entryDN)
         throws DirectoryException
  {
    return DirectoryServer.entryExists(entryDN);
  }

  /**
   * Retrieves the set of OIDs for the supported controls registered
   * with the Directory Server.
   *
   * @return  The set of OIDS for the supported controls registered
   *          with the Directory Server.
   */
  public static Set<String> getSupportedControls()
  {
    return DirectoryServer.getSupportedControls();
  }

  /**
   * Indicates whether the specified OID is registered with the
   * Directory Server as a supported control.
   *
   * @param  controlOID  The OID of the control for which to make the
   *                     determination.
   *
   * @return  <CODE>true</CODE> if the specified OID is registered
   *          with the server as a supported control, or
   *          <CODE>false</CODE> if not.
   */
  public static boolean isSupportedControl(String controlOID)
  {
    return DirectoryServer.isSupportedControl(controlOID);
  }

  /**
   * Registers the provided OID as a supported control for the
   * Directory Server.  This will have no effect if the specified
   * control OID is already present in the list of supported controls.
   *
   * @param  controlOID  The OID of the control to register as a
   *                     supported control.
   */
  public static void registerSupportedControl(String controlOID)
  {
    DirectoryServer.registerSupportedControl(controlOID);
  }

  /**
   * Deregisters the provided OID as a supported control for the
   * Directory Server.  This will have no effect if the specified
   * control OID is not present in the list of supported controls.
   *
   * @param  controlOID  The OID of the control to deregister as a
   *                     supported control.
   */
  public static void
       deregisterSupportedControl(String controlOID)
  {
    DirectoryServer.deregisterSupportedControl(controlOID);
  }

  /**
   * Retrieves the set of OIDs for the supported features registered
   * with the Directory Server.
   *
   * @return  The set of OIDs for the supported features registered
   *          with the Directory Server.
   */
  public static Set<String> getSupportedFeatures()
  {
    return DirectoryServer.getSupportedFeatures();
  }

  /**
   * Indicates whether the specified OID is registered with the
   * Directory Server as a supported feature.
   *
   * @param  featureOID  The OID of the feature for which to make the
   *                     determination.
   *
   * @return  <CODE>true</CODE> if the specified OID is registered
   *          with the server as a supported feature, or
   *          <CODE>false</CODE> if not.
   */
  public static boolean isSupportedFeature(String featureOID)
  {
    return DirectoryServer.isSupportedFeature(featureOID);
  }

  /**
   * Registers the provided OID as a supported feature for the
   * Directory Server.  This will have no effect if the specified
   * feature OID is already present in the list of supported features.
   *
   * @param  featureOID  The OID of the feature to register as a
   *                     supported feature.
   */
  public static void registerSupportedFeature(String featureOID)
  {
    DirectoryServer.registerSupportedFeature(featureOID);
  }

  /**
   * Deregisters the provided OID as a supported feature for the
   * Directory Server.  This will have no effect if the specified
   * feature OID is not present in the list of supported features.
   *
   * @param  featureOID  The OID of the feature to deregister as a
   *                     supported feature.
   */
  public static void
       deregisterSupportedFeature(String featureOID)
  {
    DirectoryServer.deregisterSupportedFeature(featureOID);
  }

  /**
   * Retrieves the handler for the extended operation for the provided
   * extended operation OID.
   *
   * @param  oid  The OID of the extended operation to retrieve.
   *
   * @return  The handler for the specified extended operation, or
   *          <CODE>null</CODE> if there is none.
   */
  public static ExtendedOperationHandler<?> getExtendedOperationHandler(String oid)
  {
    return DirectoryServer.getExtendedOperationHandler(oid);
  }

  /**
   * Registers the provided extended operation handler with the
   * Directory Server.
   *
   * @param  oid      The OID for the extended operation to register.
   * @param  handler  The extended operation handler to register with
   *                  the Directory Server.
   */
  public static void registerSupportedExtension(String oid, ExtendedOperationHandler<?> handler)
  {
    DirectoryServer.registerSupportedExtension(oid, handler);
  }

  /**
   * Deregisters the provided extended operation handler with the
   * Directory Server.
   *
   * @param  oid  The OID for the extended operation to deregister.
   */
  public static void deregisterSupportedExtension(String oid)
  {
    DirectoryServer.deregisterSupportedExtension(oid);
  }

  /**
   * Retrieves the handler for the specified SASL mechanism.
   *
   * @param  name  The name of the SASL mechanism to retrieve.
   *
   * @return  The handler for the specified SASL mechanism, or
   *          <CODE>null</CODE> if there is none.
   */
  public static SASLMechanismHandler<?> getSASLMechanismHandler(String name)
  {
    return DirectoryServer.getSASLMechanismHandler(name);
  }

  /**
   * Registers the provided SASL mechanism handler with the Directory
   * Server.
   *
   * @param  name     The name of the SASL mechanism to be registered.
   * @param  handler  The SASL mechanism handler to register with the
   *                  Directory Server.
   */
  public static void registerSASLMechanismHandler(String name, SASLMechanismHandler<?> handler)
  {
    DirectoryServer.registerSASLMechanismHandler(name, handler);
  }

  /**
   * Deregisters the provided SASL mechanism handler with the
   * Directory Server.
   *
   * @param  name  The name of the SASL mechanism to be deregistered.
   */
  public static void deregisterSASLMechanismHandler(String name)
  {
    DirectoryServer.deregisterSASLMechanismHandler(name);
  }

  /**
   * Registers the provided shutdown listener with the Directory
   * Server so that it will be notified when the server shuts down.
   *
   * @param  listener  The shutdown listener to register with the
   *                   Directory Server.
   */
  public static void
       registerShutdownListener(ServerShutdownListener listener)
  {
    DirectoryServer.registerShutdownListener(listener);
  }

  /**
   * Deregisters the provided shutdown listener with the Directory
   * Server.
   *
   * @param  listener  The shutdown listener to deregister with the
   *                   Directory Server.
   */
  public static void
       deregisterShutdownListener(ServerShutdownListener listener)
  {
    DirectoryServer.deregisterShutdownListener(listener);
  }

  /**
   * Retrieves the full version string for the Directory Server.
   *
   * @return  The full version string for the Directory Server.
   */
  public static String getVersionString()
  {
    return DirectoryServer.getVersionString();
  }
}
