/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at
 * trunk/opends/resource/legal-notices/OpenDS.LICENSE
 * or https://OpenDS.dev.java.net/OpenDS.LICENSE.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at
 * trunk/opends/resource/legal-notices/OpenDS.LICENSE.  If applicable,
 * add the following below this CDDL HEADER, with the fields enclosed
 * by brackets "[]" replaced with your own identifying * information:
 *      Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *      Portions Copyright 2006 Sun Microsystems, Inc.
 */

package org.opends.build.tools;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.FileReader;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

public class PrepTestNG extends Task
{
  private String file;
  private String toFile;
  private String groupList;
  private String packageList;
  private String classList;
  private String methodList;

  public void setFile(String file)
  {
    this.file = file;
  }

  public void setToFile(String toFile)
  {
    this.toFile = toFile;
  }

  public void setGroupList(String groupList)
  {
    this.groupList = groupList;
  }

  public void setPackageList(String packageList)
  {
    this.packageList = packageList;
  }

  public void setClassList(String classList)
  {
    this.classList = classList;
  }

  public void setMethodList(String methodList)
  {
    this.methodList = methodList;
  }

  public void execute() throws BuildException
  {
    if(file == null)
    {
      throw new BuildException("Attribute file must be set to the orginal " +
          "TestNG XML file");
    }

    if(toFile == null)
    {
      throw new BuildException("Attribute toFile must be set to the modified " +
          "TestNG XML file");
    }

    BufferedReader reader;
    FileOutputStream outFile;
    PrintStream writer;
    String line;
    String[] groups;
    String[] packages;
    String[] classes;
    String[] methods;
    String[] groupLine;
    String[] methodLine;
    String methodClass;
    String methodName;
    int methodNameStartIdx;
    int groupCount = 0;
    int packageCount = 0;
    int classCount = 0;
    int methodCount = 0;

    try
    {
      reader = new BufferedReader(new FileReader(file));
      outFile = new FileOutputStream(toFile);

      writer = new PrintStream(outFile);

      line = reader.readLine();

      if(groupList != null && !groupList.trim().equals("") &&
          !groupList.startsWith("${"))
      {
        groups = groupList.split(",");
      }
      else
      {
        groups = new String[0];
      }
      if(packageList != null && !packageList.trim().equals("") &&
          !packageList.startsWith("${"))
      {
        packages = packageList.split(",");
      }
      else
      {
        packages = new String[0];
      }

      if(classList != null && !classList.trim().equals("") &&
          !classList.startsWith("${"))
      {
        classes = classList.split(",");
      }
      else
      {
        classes = new String[0];
      }

      if(methodList != null && !methodList.trim().equals("") &&
          !methodList.startsWith("${"))
      {
        methods = methodList.split(";");
      }
      else
      {
        methods = new String[0];
      }

      while(line != null)
      {
        if(line.indexOf("<!-- DO NOT REMOVE! - THIS LINE WILL BE " +
            "REPLACED WITH TAGS GENERATED BY ANT -->") >= 0)
        {
          if(groups.length > 0)
          {
            writer.println("<groups>\n  <run>");
            for(String group : groups)
            {
              groupLine = group.split("=");
              if(groupLine.length == 2)
              {
                writer.println("    <"+groupLine[0].trim()+" " +
                               "name=\""+groupLine[1].trim() + "\" />");
                groupCount++;
              }
            }
            writer.println("  </run>\n</groups>");
          }

          if(packages.length > 0)
          {
            writer.println("<packages>");
            for(String pkg : packages)
            {
              writer.println("  <package name=\"" + pkg.trim() + "\" />");

              packageCount++;
            }
            writer.println("</packages>");
          }

          if(classes.length > 0 || methods.length > 0)
          {
            writer.println("<classes>");

            if(classes.length > 0)
            {
              for(String cls : classes)
              {
                writer.println("  <class name=\"" + cls.trim() + "\" />");

                classCount++;
              }
            }

            if(methods.length > 0)
            {
              for(String mhd : methods)
              {
                methodLine = mhd.split(",");
                if(methodLine.length > 0)
                {
                  methodNameStartIdx = methodLine[0].lastIndexOf(".");
                  methodClass = methodLine[0].substring(0,
                                  methodNameStartIdx);
                  methodName = methodLine[0].substring(methodNameStartIdx + 1,
                                methodLine[0].length());
                  writer.println("  <class name=\"" +
                      methodClass.trim() + "\" >");
                  writer.println("  <methods>");
                  writer.println("    <include name=\"" +
                      methodName.trim() + "\" />");
                  methodCount++;
                  classCount++;
                  for(int i = 1; i < methodLine.length; i ++)
                  {
                    writer.println("    <include name=\"" +
                      methodLine[i].trim() + "\" />");
                    methodCount++;
                  }
                  writer.println("  </methods>");
                  writer.println("</class>");
                }
              }
            }

            writer.println("</classes>");
          }
        }
        else
        {
          writer.println(line);
        }

        line = reader.readLine();
      }

      System.out.println("Adding " + groupCount + " group tags, " +
          packageCount + " package tags, " + classCount + " class tags, " +
          methodCount + " method tags to " + toFile);
    }
    catch(Exception e)
    {
      throw new BuildException("File Error: " + e.toString());
    }
  }
}
