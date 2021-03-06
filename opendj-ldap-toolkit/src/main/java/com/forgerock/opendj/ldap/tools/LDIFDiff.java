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
 * Copyright 2012-2016 ForgeRock AS.
 * Portions Copyright 2014-2015 ForgeRock AS.
 */
package com.forgerock.opendj.ldap.tools;

import static com.forgerock.opendj.cli.ArgumentConstants.OPTION_LONG_OUTPUT_LDIF_FILENAME;
import static com.forgerock.opendj.cli.ArgumentConstants.OPTION_SHORT_OUTPUT_LDIF_FILENAME;
import static com.forgerock.opendj.cli.ToolVersionHandler.newSdkVersionHandler;
import static com.forgerock.opendj.ldap.tools.ToolsMessages.*;
import static com.forgerock.opendj.cli.Utils.filterExitCode;
import static com.forgerock.opendj.cli.CommonArguments.*;

import static org.forgerock.util.Utils.closeSilently;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.forgerock.i18n.LocalizableException;
import org.forgerock.i18n.LocalizableMessage;
import org.forgerock.opendj.ldap.ResultCode;
import org.forgerock.opendj.ldif.LDIF;
import org.forgerock.opendj.ldif.LDIFChangeRecordWriter;
import org.forgerock.opendj.ldif.LDIFEntryReader;

import com.forgerock.opendj.cli.ArgumentException;
import com.forgerock.opendj.cli.ArgumentParser;
import com.forgerock.opendj.cli.BooleanArgument;
import com.forgerock.opendj.cli.ConsoleApplication;
import com.forgerock.opendj.cli.StringArgument;

/**
 * This utility can be used to compare two LDIF files and report the differences
 * in LDIF format.
 */
public final class LDIFDiff extends ConsoleApplication {

    /**
     * The main method for LDIFDiff tool.
     *
     * @param args
     *            The command-line arguments provided to this program.
     */
    public static void main(final String[] args) {
        final int retCode = new LDIFDiff().run(args);
        System.exit(filterExitCode(retCode));
    }

    private LDIFDiff() {
        // Nothing to do.
    }

    private int run(final String[] args) {
        // Create the command-line argument parser for use with this program.
        final LocalizableMessage toolDescription = INFO_LDIFDIFF_TOOL_DESCRIPTION.get();
        final ArgumentParser argParser = new ArgumentParser(
            LDIFDiff.class.getName(), toolDescription, false, true, 2, 2, "source target");
        argParser.setVersionHandler(newSdkVersionHandler());
        argParser.setShortToolDescription(REF_SHORT_DESC_LDIFDIFF.get());

        final BooleanArgument showUsage;
        final StringArgument outputFilename;
        try {
            outputFilename =
                    StringArgument.builder(OPTION_LONG_OUTPUT_LDIF_FILENAME)
                            .shortIdentifier(OPTION_SHORT_OUTPUT_LDIF_FILENAME)
                            .description(INFO_LDIFDIFF_DESCRIPTION_OUTPUT_FILENAME.get(
                                    INFO_OUTPUT_LDIF_FILE_PLACEHOLDER.get()))
                            .defaultValue("stdout")
                            .valuePlaceholder(INFO_OUTPUT_LDIF_FILE_PLACEHOLDER.get())
                            .buildAndAddToParser(argParser);

            showUsage = showUsageArgument();
            argParser.addArgument(showUsage);
            argParser.setUsageArgument(showUsage, getOutputStream());
        } catch (final ArgumentException ae) {
            final LocalizableMessage message = ERR_CANNOT_INITIALIZE_ARGS.get(ae.getMessage());
            errPrintln(message);
            return ResultCode.CLIENT_SIDE_PARAM_ERROR.intValue();
        }

        // Parse the command-line arguments provided to this program.
        try {
            argParser.parseArguments(args);

            /* If we should just display usage or version information, then print it and exit. */
            if (argParser.usageOrVersionDisplayed()) {
                return ResultCode.SUCCESS.intValue();
            }
        } catch (final ArgumentException ae) {
            argParser.displayMessageAndUsageReference(getErrStream(), ERR_ERROR_PARSING_ARGS.get(ae.getMessage()));
            return ResultCode.CLIENT_SIDE_PARAM_ERROR.intValue();
        }

        InputStream sourceInputStream = null;
        InputStream targetInputStream = null;
        OutputStream outputStream = null;

        try {
            // First source file.
            final List<String> trailingArguments = argParser.getTrailingArguments();
            if (!"-".equals(trailingArguments.get(0))) {
                try {
                    sourceInputStream = new FileInputStream(trailingArguments.get(0));
                } catch (final FileNotFoundException e) {
                    final LocalizableMessage message =
                            ERR_LDIF_FILE_CANNOT_OPEN_FOR_READ.get(trailingArguments.get(0), e
                                    .getLocalizedMessage());
                    errPrintln(message);
                    return ResultCode.CLIENT_SIDE_PARAM_ERROR.intValue();
                }
            }

            // Patch file.
            if (!"-".equals(trailingArguments.get(1))) {
                try {
                    targetInputStream = new FileInputStream(trailingArguments.get(1));
                } catch (final FileNotFoundException e) {
                    final LocalizableMessage message =
                            ERR_LDIF_FILE_CANNOT_OPEN_FOR_READ.get(trailingArguments.get(1), e
                                    .getLocalizedMessage());
                    errPrintln(message);
                    return ResultCode.CLIENT_SIDE_PARAM_ERROR.intValue();
                }
            }

            // Output file.
            if (outputFilename.isPresent() && !"-".equals(outputFilename.getValue())) {
                try {
                    outputStream = new FileOutputStream(outputFilename.getValue());
                } catch (final FileNotFoundException e) {
                    final LocalizableMessage message =
                            ERR_LDIF_FILE_CANNOT_OPEN_FOR_WRITE.get(outputFilename.getValue(), e
                                    .getLocalizedMessage());
                    errPrintln(message);
                    return ResultCode.CLIENT_SIDE_PARAM_ERROR.intValue();
                }
            }

            // Default to stdin/stdout for all streams if not specified.
            if (sourceInputStream == null) {
                // Command line parameter was "-".
                sourceInputStream = System.in;
            }

            if (targetInputStream == null) {
                targetInputStream = System.in;
            }

            if (outputStream == null) {
                outputStream = System.out;
            }

            /* Check that we are not attempting to read both the source and target from stdin. */
            if (sourceInputStream == targetInputStream) {
                final LocalizableMessage message = ERR_LDIFDIFF_MULTIPLE_USES_OF_STDIN.get();
                errPrintln(message);
                return ResultCode.CLIENT_SIDE_PARAM_ERROR.intValue();
            }

            // Perform the diff.
            try (LDIFEntryReader sourceReader = new LDIFEntryReader(sourceInputStream);
                LDIFEntryReader targetReader = new LDIFEntryReader(targetInputStream);
                LDIFChangeRecordWriter outputWriter = new LDIFChangeRecordWriter(outputStream)) {
                LDIF.copyTo(LDIF.diff(sourceReader, targetReader), outputWriter);
            }
        } catch (final IOException e) {
            if (e instanceof LocalizableException) {
                errPrintln(ERR_LDIFDIFF_DIFF_FAILED.get(((LocalizableException) e).getMessageObject()));
            } else {
                errPrintln(ERR_LDIFDIFF_DIFF_FAILED.get(e.getLocalizedMessage()));
            }
            return ResultCode.CLIENT_SIDE_LOCAL_ERROR.intValue();
        } finally {
            closeSilently(sourceInputStream, targetInputStream, outputStream);
        }

        return ResultCode.SUCCESS.intValue();
    }
}
