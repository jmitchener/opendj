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
 * Copyright 2008-2009 Sun Microsystems, Inc.
 * Copyright 2013-2015 ForgeRock AS.
 */
package org.opends.server.util;

import java.io.File;
import org.forgerock.i18n.slf4j.LocalizedLogger;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.opends.server.core.DirectoryServer;

import static org.opends.messages.CoreMessages.*;
import static org.opends.messages.RuntimeMessages.*;
import static org.opends.server.util.DynamicConstants.*;

 /**
  * This class is used to gather and display information from the runtime
  * environment.
  */
 public class RuntimeInformation {

  private static final LocalizedLogger logger = LocalizedLogger.getLoggerForThisClass();



   private static boolean is64Bit;

   static {
     String arch = System.getProperty("sun.arch.data.model");
     if (arch != null) {
       try {
         is64Bit = Integer.parseInt(arch) == 64;
       } catch (NumberFormatException ex) {
         //Default to 32 bit.
       }
     }
   }

   /**
    * Returns whether the architecture of the JVM we are running under is 64-bit
    * or not.
    *
    * @return <CODE>true</CODE> if the JVM architecture we running under is
    * 64-bit and <CODE>false</CODE> otherwise.
    */
   public static boolean is64Bit() {
     return is64Bit;
   }

   /**
    * Returns a string representing the JVM input arguments as determined by the
    * MX runtime bean. The individual arguments are separated by commas.
    *
    * @return  A string representation of the JVM input arguments.
    */
   private static String getInputArguments() {
     int count=0;
     RuntimeMXBean rtBean = ManagementFactory.getRuntimeMXBean();
     StringBuilder argList = new StringBuilder();
     List<String> jvmArguments = rtBean.getInputArguments();
     if (jvmArguments != null && !jvmArguments.isEmpty()) {
       for (String jvmArg : jvmArguments) {
         if (argList.length() > 0)  {
           argList.append(" ");
         }
         argList.append("\"");
         argList.append(jvmArg);
         argList.append("\"");
         count++;
         if (count < jvmArguments.size())  {
           argList.append(",");
         }
       }
     }
     return argList.toString();
   }

   /**
    * Writes runtime information to a print stream.
    */
   public static void printInfo() {
     System.out.println(NOTE_VERSION.get(DirectoryServer.getVersionString()));
     System.out.println(NOTE_BUILD_ID.get(BUILD_ID));
     System.out.println(
             NOTE_JAVA_VERSION.get(System.getProperty("java.version")));
     System.out.println(
             NOTE_JAVA_VENDOR.get(System.getProperty("java.vendor")));
     System.out.println(
             NOTE_JVM_VERSION.get(System.getProperty("java.vm.version")));
     System.out.println(
             NOTE_JVM_VENDOR.get(System.getProperty("java.vm.vendor")));
     System.out.println(
             NOTE_JAVA_HOME.get(System.getProperty("java.home")));
     System.out.println(
             NOTE_JAVA_CLASSPATH.get(System.getProperty("java.class.path")));
     System.out.println(
             NOTE_CURRENT_DIRECTORY.get(System.getProperty("user.dir")));
     String installDir = toCanonicalPath(DirectoryServer.getServerRoot());
     if (installDir == null)
     {
       System.out.println(NOTE_UNKNOWN_INSTALL_DIRECTORY.get());
     }
     else
     {
       System.out.println(NOTE_INSTALL_DIRECTORY.get(installDir));
     }
     String instanceDir = toCanonicalPath(DirectoryServer.getInstanceRoot());
     if (instanceDir == null)
     {
       System.out.println(NOTE_UNKNOWN_INSTANCE_DIRECTORY.get());
     }
     else
     {
       System.out.println(NOTE_INSTANCE_DIRECTORY.get(instanceDir));
     }
     System.out.println(
             NOTE_OPERATING_SYSTEM.get(System.getProperty("os.name") + " " +
                     System.getProperty("os.version") + " " +
                     System.getProperty("os.arch")));
     String sunOsArchDataModel = System.getProperty("sun.arch.data.model");
     if (sunOsArchDataModel != null) {
       if (! sunOsArchDataModel.toLowerCase().equals("unknown")) {
         System.out.println(NOTE_JVM_ARCH.get(sunOsArchDataModel + "-bit"));
       }
     }
     else{
       System.out.println(NOTE_JVM_ARCH.get("unknown"));
     }
     try {
       System.out.println(NOTE_SYSTEM_NAME.get(InetAddress.getLocalHost().
               getCanonicalHostName()));
     }
     catch (Exception e) {
       System.out.println(NOTE_SYSTEM_NAME.get("Unknown (" + e + ")"));
     }
     System.out.println(NOTE_AVAILABLE_PROCESSORS.get(Runtime.getRuntime().
             availableProcessors()));
     System.out.println(NOTE_MAX_MEMORY.get(Runtime.getRuntime().maxMemory()));
     System.out.println(
             NOTE_TOTAL_MEMORY.get(Runtime.getRuntime().totalMemory()));
     System.out.println(
             NOTE_FREE_MEMORY.get(Runtime.getRuntime().freeMemory()));
   }

  private static String toCanonicalPath(String path)
  {
    try
    {
      return new File(path).getCanonicalPath();
    }
    catch (Exception ignored)
    {
      return path;
    }
  }

   /**
     * Returns the physical memory size, in bytes, of the hardware we are
     * running on.
     *
     * @return Bytes of physical memory of the hardware we are running on.
     */
  private static long getPhysicalMemorySize()
  {
    MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
    try
    {
      // Assuming the RuntimeMXBean has been registered in mbs
      ObjectName oname = new ObjectName(
          ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME);
      // Check if this MXBean contains Sun's extension
      if (mbs.isInstanceOf(oname, "com.sun.management.OperatingSystemMXBean"))
      {
        // Get platform-specific attribute "TotalPhysicalMemorySize"
        return (Long) mbs.getAttribute(oname, "TotalPhysicalMemorySize");
      }
      else if (mbs.isInstanceOf(oname, "com.ibm.lang.management.OperatingSystemMXBean"))
      {
        // IBM JVM attribute is named differently
        return (Long) mbs.getAttribute(oname, "TotalPhysicalMemory");
      }
    }
    catch (Exception ignored)
    {
    }
    return -1;
   }

   /**
    * Returns a string representing the fully qualified domain name.
    *
    * @return A string representing the fully qualified domain name or the
    * string "unknown" if an exception was thrown.
    */
   private static String getHostName() {
     try {
       return InetAddress.getLocalHost().getCanonicalHostName();
     }
     catch (Exception e) {
       return "Unknown (" + e + ")";
     }
   }

   /**
    * Returns string representing operating system name,
    * version and architecture.
    *
    * @return String representing the operating system information the JVM is
    * running under.
    */
   private static String getOSInfo() {
    return System.getProperty("os.name") + " " +
           System.getProperty("os.version") + " " +
           System.getProperty("os.arch");
   }

   /**
    * Return string representing the architecture of the JVM we are running
    * under.
    *
    * @return A string representing the architecture of the JVM we are running
    * under or "unknown" if the architecture cannot be determined.
    */
   private static String getArch() {
     String sunOsArchDataModel = System.getProperty("sun.arch.data.model");
     if (sunOsArchDataModel != null
         && !sunOsArchDataModel.toLowerCase().equals("unknown"))
     {
       return sunOsArchDataModel + "-bit";
     }
     return "unknown";
   }

   /**
    * Write runtime information to error log.
    */
   public static void logInfo() {
     String installDir = toCanonicalPath(DirectoryServer.getServerRoot());
     if (installDir == null)
     {
       logger.info(NOTE_UNKNOWN_INSTALL_DIRECTORY);
     }
     else
     {
       logger.info(NOTE_INSTALL_DIRECTORY, installDir);
     }
     String instanceDir = toCanonicalPath(DirectoryServer.getInstanceRoot());
     if (instanceDir == null)
     {
       logger.info(NOTE_UNKNOWN_INSTANCE_DIRECTORY);
     }
     else
     {
       logger.info(NOTE_INSTANCE_DIRECTORY, instanceDir);
     }
    logger.info(NOTE_JVM_INFO, System.getProperty("java.runtime.version"),
                               System.getProperty("java.vendor"),
                               getArch(),Runtime.getRuntime().maxMemory());
    long physicalMemorySize = getPhysicalMemorySize();
    if (physicalMemorySize != -1)
    {
      logger.info(NOTE_JVM_HOST, getHostName(), getOSInfo(),
          physicalMemorySize, Runtime.getRuntime().availableProcessors());
    }
    else
    {
      logger.info(NOTE_JVM_HOST_WITH_UNKNOWN_PHYSICAL_MEM, getHostName(),
          getOSInfo(), Runtime.getRuntime().availableProcessors());
    }
    logger.info(NOTE_JVM_ARGS, getInputArguments());
   }
 }
