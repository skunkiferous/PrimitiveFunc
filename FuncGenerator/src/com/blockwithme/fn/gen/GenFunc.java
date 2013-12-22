/*
 * Copyright (C) 2013 Sebastien Diot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blockwithme.fn.gen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import com.blockwithme.fn.gen.FuncFilter.ParamType;
import com.blockwithme.fn.util.Functor;
import com.blockwithme.fn.util.Util;

/**
 * <code>GenFunc</code> generates the source-code of the functions.
 *
 * Usage: GenFunc OutputDirectory LicenseFile PackageName ProcNamePrefix FuncNameInfix MethodName Throws MinimumNumberOfArgs MaximumNumberOfArgs ShortName Filter
 *
 * OutputDirectory is the directory where the source files are generated. For example, C:\temp\funcs
 * LicenseFile is the path to the license file, to use as header. Can be "" or simply empty. For example, APACHE_LICENSE_HEADER.txt.
 * PackageName is the name of the package in which the interfaces are created, for example "com.test".
 * ProcNamePrefix the interface name prefix, for "void" Functors, for example "Proc"
 * FuncNameInfix the interface name infix, for non "void" Functors, for example "Func"
 * MethodName is the method name, for example "apply" or "call".
 * Throws can contain the optional name of a thrown "Throwable". Leave blank for no "throws". Example: java.io.IOException
 * MinimumNumberOfArgs is the minimum number of parameters the functions will have, for example 0.
 * MaximumNumberOfArgs is the maximum number of parameters the functions will have, for example 3.
 * ShortName specifies if the names should be like "ProcZL" (true) or "ProcBooleanLong" (false)
 * Filter is the name of a class that implements com.blockwithme.gen.func.FuncFilter (optional).
 *
 * Hint: It supports up to 5 as the maximum number of parameters,
 * but that would be near 1 million interfaces; not recommended ... ;)
 *
 * @author monster
 *
 */
public class GenFunc {

    /** The possible parameter types, except Object. */
    private static final String[] _PARAM_TYPES = { "boolean", "byte", "char",
            "short", "int", "long", "float", "double" };

    /** The possible return types, including Object (generic R parameter). */
    private static final String[] _RETURN_TYPES = { "void", "boolean", "byte",
            "char", "short", "int", "long", "float", "double", "R" };

    private static Class<?> FUNCTOR_INTERFACE = Functor.class;

    /** ID of parameter of type object. */
    private static final int OBJECT_PARAM = _PARAM_TYPES.length;

    /** ID of return of type object. */
    private static final int OBJECT_RETURN = _RETURN_TYPES.length - 1;

    /** The possible parameter types, as enum for faster filtering. */
    private static final FuncFilter.ParamType[] PARAM_TYPES3 = {
            FuncFilter.ParamType.Boolean, FuncFilter.ParamType.Byte,
            FuncFilter.ParamType.Char, FuncFilter.ParamType.Short,
            FuncFilter.ParamType.Int, FuncFilter.ParamType.Long,
            FuncFilter.ParamType.Float, FuncFilter.ParamType.Double,
            FuncFilter.ParamType.Object };

    /** The possible return types, including Object, capitalized for the interface name. */
    private static String[] RETURN_TYPES2;

    /** The possible return types as a class */
    private static final String[] SIGN_RETURN_TYPES = { "Void.TYPE",
            "Boolean.TYPE", "Byte.TYPE", "Character.TYPE", "Short.TYPE",
            "Integer.TYPE", "Long.TYPE", "Float.TYPE", "Double.TYPE",
            "Object.class" };

    /** Usage */
    private static final String USAGE = "Usage:\n"
            + "    GenFunc OutputDirectory LicenseFile PackageName ProcNamePrefix FuncNameInfix MethodName Throws MinimumNumberOfArgs MaximumNumberOfArgs Filter\n"
            + "\n"
            + "OutputDirectory is the directory where the source files are generated. For example, 'C:\temp\funcs'\n"
            + "LicenseFile is the path to the license file, to use as header. Can be '' or simply empty. For example, 'APACHE_LICENSE_HEADER.txt'.\n"
            + "PackageName is the name of the package in which the interfaces are created, for example 'com,test'.\n"
            + "ClassNamePrefix the interface name prefix, for example 'Func'\n"
            + "MethodName is the method name, for example 'apply' or 'call'.\n"
            + "MinimumNumberOfArgs is the minimum number of parameters the functions will have, for example 0.\n"
            + "MaximumNumberOfArgs is the maximum number of parameters the functions will have, for example 3.\n"
            + "Filter is the name of a class that implements com.blockwithme.gen.func.FuncFilter (optional).\n"
            + "Throws can contain the optional name of a thrown 'Throwable'. Leave blank for no 'throws'. Example: 'java.io.IOException'\n"
            + "\n"
            + "Hint: It supports up to 5 as the maximum number of parameters,\n"
            + "      but that would be near 1 million interfaces; not recommended ... ;)\n";

    /** Generator class name. */
    public static final String GENERATOR = GenFunc.class.getName();

    /** Maximum number of parameters. */
    public static final int MAXIMUM_PARAMETERS = 5;

    /** Generated interfaces per dot. */
    private static final int INTERFACES_PER_DOT = 250;

    /**
     * Generates the functions.
     * The expected format of <code>format</code> is:
     * #0 %s = InterfaceName
     * #1 %s = return type
     * #2 %s = Parameter list
     */
    private static int doGenerate(final File outputrDirectory,
            final String funcNameInfix, final String procNamePrefix,
            final String format, final int numberOfArgs, final FuncFilter filter) {
        final String np = String.valueOf(numberOfArgs);
        int result = 0;
        for (int r = 0; r <= OBJECT_RETURN; r++) {
            final String returnType = _RETURN_TYPES[r];
            final ParamType returnType2 = r == 0 ? ParamType.Void
                    : PARAM_TYPES3[r - 1];
            final int[] params = new int[numberOfArgs];
            int current = 0;
            boolean again = true;
            while (again) {
                if (filter == null
                        || filter
                                .accept(genParameterList2(params), returnType2)) {
                    final String[] generatedStrings = genNameAndSignature(
                            funcNameInfix, procNamePrefix, r, params);
                    final String name = generatedStrings[0];
                    final String signature = generatedStrings[1];
                    final String genParams = genGenericsParams(r, params);
                    final String paramList = genParameterList(params);
                    final String content = String.format(format, name
                            + genParams, signature, returnType, paramList, np);
                    final File file = new File(outputrDirectory, name + ".java");
                    outputInterface(file, content);
                    result++;
                    if (result % INTERFACES_PER_DOT == 0) {
                        System.out.print(".");
                    }
                }
                if (numberOfArgs == 0) {
                    again = false;
                } else {
                    String s = Integer.toString(++current,
                            _PARAM_TYPES.length + 1);
                    if (s.length() <= numberOfArgs) {
                        while (s.length() < numberOfArgs) {
                            s = "0" + s;
                        }
                        final char[] types = s.toCharArray();
                        for (int i = 0; i < params.length; i++) {
                            params[i] = types[i] - '0';
                        }
                    } else {
                        again = false;
                    }
                }
            }
        }
        return result;
    }

    /** Generates the functions. */
    private static void doGenerate(final File outputDirectory,
            final String fileHeader, final String packageName,
            final String funcNameInfix, final String procNamePrefix,
            final String methodName, final String throwsStr,
            final int minimumNumberOfArgs, final int maximumNumberOfArgs,
            final FuncFilter filter) {
        System.out.println("Generating fucntions:");
        System.out.println("    Header:                       "
                + (fileHeader.isEmpty() ? "No" : "Yes"));
        System.out.println("    Package:                      " + packageName);
        System.out
                .println("    Function Class Name Infix:    " + funcNameInfix);
        System.out.println("    Procedure Class Name Prefix:  "
                + procNamePrefix);
        System.out.println("    Method Name:                  " + methodName);
        System.out.println("    Throws:                       " + throwsStr);
        System.out.println("    Minimum Number Of Parameters: "
                + minimumNumberOfArgs);
        System.out.println("    Maximum Number Of Parameters: "
                + maximumNumberOfArgs);
        System.out.println("    Function filter:              " + filter);
        String content = fileHeader;
        content += "\npackage " + packageName + "%5$s;\n\n";
        content += "\nimport " + FUNCTOR_INTERFACE.getName() + ";\n\n";
        content += "/**\n * Primitive Function Interface <code>%1$s</code>.\n";
        content += " * Generated automatically by " + GENERATOR + "\n */\n";
        content += "public interface %1$s extends "
                + FUNCTOR_INTERFACE.getSimpleName() + " {\n";
        content += "    /** SIGNATURE constant */\n";
        content += "    %2$s" + "\n";
        content += "    \n";
        content += "    /** Function <code>" + methodName + "</code> */\n";
        content += "    %3$s " + methodName + "(%4$s)" + throwsStr + ";\n";
        content += "}\n";
        int total = 0;
        System.out.println("Note: One '.' equals " + INTERFACES_PER_DOT
                + " generated interfaces.");
        for (int p = minimumNumberOfArgs; p <= maximumNumberOfArgs; p++) {
            System.out.print("Generation functions with " + p
                    + " parameters ...");
            final File dir = new File(outputDirectory.getAbsolutePath() + p);
            dir.mkdirs();
            final int count = doGenerate(dir, funcNameInfix, procNamePrefix,
                    content, p, filter);
            System.out.println(" " + count
                    + " functions generated (after filtering).");
            total += count;
        }
        System.out.println(total
                + " total functions generated (after filtering).");
    }

    /** Generated the generic parameters for the interface definition. */
    private static String genGenericsParams(final int returnType,
            final int... params) {
        int genericParams = 0;
        for (int i = 0; i < params.length; i++) {
            final int p = params[i];
            if (p == OBJECT_PARAM) {
                genericParams++;
            }
        }
        final boolean returnGeneric = returnType == OBJECT_RETURN;
        if (genericParams == 0) {
            if (!returnGeneric) {
                return "";
            }
            return "<R>";
        }
        String result = "<";
        if (returnGeneric) {
            result += "R,";
        }
        char c = 'A';
        for (int i = 0; i < genericParams; i++) {
            result += c + ",";
            c++;
        }
        return result.substring(0, result.length() - 1) + ">";
    }

    /** Generated the generic parameters for the interface definition. */
    private static String[] genNameAndSignature(final String funcNameInfix,
            final String procNamePrefix, final int returnType,
            final int... params) {
        final int count = params.length;
        if (count > 5) {
            throw new IllegalArgumentException("Too many parameters: " + count);
        }
        String signatureString = "Class<?>[] SIGNATURE = {";
        signatureString += SIGN_RETURN_TYPES[returnType];
        for (int i = 0; i < count; i++) {
            signatureString += ", " + SIGN_RETURN_TYPES[params[i] + 1];
        }
        signatureString += "};";
        String name;
        if (returnType == 0) {
            name = procNamePrefix;
        } else {
            name = RETURN_TYPES2[returnType] + funcNameInfix;
        }
        for (int i = 0; i < count; i++) {
            name += RETURN_TYPES2[params[i] + 1];
        }
        return new String[] { name, signatureString };
    }

    /** Generate the parameter list. */
    private static String genParameterList(final int[] params) {
        String result = "";
        char genParam = 'A';
        for (int i = 0; i < params.length; i++) {
            if (i > 0) {
                result += ", ";
            }
            final int p = params[i];
            if (p == OBJECT_PARAM) {
                result += String.valueOf(genParam++);
            } else {
                result += _PARAM_TYPES[p];
            }
            result += " p" + i;
        }
        return result;
    }

    /** Generate the parameter list for the filter. */
    private static ParamType[] genParameterList2(final int[] params) {
        final FuncFilter.ParamType[] result = new FuncFilter.ParamType[params.length];
        for (int i = 0; i < params.length; i++) {
            result[i] = PARAM_TYPES3[params[i]];
        }
        return result;
    }

    /** Outputs the generated interface. */
    private static void outputInterface(final File file, final String content) {
        if (file.exists()) {
            try (Scanner scanner = new Scanner(file);) {
                if (content.equals(scanner.useDelimiter("\\Z").next())) {
                    return;
                }
            } catch (final FileNotFoundException e) {
                // Should not happen, since we checked ...
            }
        }
        try (FileWriter fw = new FileWriter(file);) {
            fw.write(content);
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to write to " + file, e);
        }
    }

    /** Generates the functions. */
    public static void generate(final File outputrDirectory,
            final String fileHeader, final String packageName,
            final String funcNameInfix, final String procNamePrefix,
            final String methodName, final String throwsStr,
            final int minimumNumberOfArgs, final int maximumNumberOfArgs,
            final String filterType) {
        if (outputrDirectory == null) {
            throw new IllegalArgumentException("outputrDirectory is null");
        }
        if (outputrDirectory.getName().isEmpty()) {
            throw new IllegalArgumentException("outputrDirectory is empty");
        }
        outputrDirectory.mkdirs();
        if (!outputrDirectory.isDirectory()) {
            throw new IllegalArgumentException(
                    "Cannot create outputrDirectory " + outputrDirectory);
        }
        if (!outputrDirectory.canWrite()) {
            throw new IllegalArgumentException(
                    "Cannot write to outputrDirectory " + outputrDirectory);
        }
        // We don't really use outputrDirectory; we just need to know it works
        outputrDirectory.delete();
        if (fileHeader != null) {
            final String h = fileHeader.trim();
            if (!h.isEmpty()) {
                if (!(h.startsWith("/*") && h.endsWith("*/") && h.length() > 3)) {
                    throw new IllegalArgumentException(
                            "fileHeader should contain nothing, or a C-style comment: "
                                    + fileHeader);
                }
            }
        }
        if (packageName == null) {
            throw new IllegalArgumentException("packageName is null");
        }
        if (packageName.isEmpty()) {
            throw new IllegalArgumentException("packageName is empty");
        }
        boolean canBeDot = false;
        boolean identifierStart = true;
        for (final char c : packageName.toCharArray()) {
            if (!canBeDot && c == '.') {
                throw new IllegalArgumentException("packageName is invalid: "
                        + packageName);
            }
            canBeDot = c != '.';
            if (canBeDot) {
                if (identifierStart) {
                    if (!Character.isJavaIdentifierStart(c)) {
                        throw new IllegalArgumentException(
                                "packageName is invalid: " + packageName);
                    }
                } else {
                    if (!Character.isJavaIdentifierPart(c)) {
                        throw new IllegalArgumentException(
                                "packageName is invalid: " + packageName);
                    }
                }
            }
            identifierStart = c == '.';
        }
        validateFuncName(funcNameInfix, "funcNameInfix", false);
        validateFuncName(procNamePrefix, "procNamePrefix", true);
        validateFuncName(methodName, "methodName", true);
        if (minimumNumberOfArgs < 0 || minimumNumberOfArgs > MAXIMUM_PARAMETERS) {
            throw new IllegalArgumentException(
                    "minimumNumberOfArgs must be within [0,"
                            + MAXIMUM_PARAMETERS + "]: " + minimumNumberOfArgs);
        }
        if (maximumNumberOfArgs < 0 || maximumNumberOfArgs > MAXIMUM_PARAMETERS) {
            throw new IllegalArgumentException(
                    "maximumNumberOfArgs must be within [0,"
                            + MAXIMUM_PARAMETERS + "]: " + maximumNumberOfArgs);
        }
        if (minimumNumberOfArgs > maximumNumberOfArgs) {
            throw new IllegalArgumentException("minimumNumberOfArgs ("
                    + minimumNumberOfArgs + ") must be <= maximumNumberOfArgs("
                    + maximumNumberOfArgs + ")");
        }

        FuncFilter filter = null;
        if (filterType != null && !filterType.isEmpty()) {
            try {
                filter = (FuncFilter) Class.forName(filterType).newInstance();
            } catch (InstantiationException | IllegalAccessException
                    | ClassNotFoundException e) {
                throw new IllegalArgumentException("bad filter type ("
                        + filterType + ")", e);
            }
        }
        doGenerate(outputrDirectory, fileHeader == null ? "" : fileHeader,
                packageName, funcNameInfix, procNamePrefix, methodName,
                throwsStr, minimumNumberOfArgs, maximumNumberOfArgs, filter);
    }

    private static void validateFuncName(final String name,
            final String called, boolean identifierStart) {
        if (name == null) {
            throw new IllegalArgumentException(called + " is null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException(called + " is empty");
        }
        for (final char c : name.toCharArray()) {
            if (identifierStart) {
                if (!Character.isJavaIdentifierStart(c)) {
                    throw new IllegalArgumentException(called + " is invalid: "
                            + name);
                }
                identifierStart = false;
            } else {
                if (!Character.isJavaIdentifierPart(c)) {
                    throw new IllegalArgumentException(called + " is invalid: "
                            + name);
                }
            }
        }
    }

    private static void checkNotEmpty(final String value, final String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " is null");
        }
        if (value.isEmpty()) {
            throw new IllegalArgumentException(name + " is empty");
        }
    }

    /** Generates the functions. */
    public static void generate(final String outputrDirectory,
            final String licenseFile, final String packageName,
            final String funcNameInfix, final String procNamePrefix,
            final String methodName, final String throwsStr,
            final String minimumNumberOfArgs, final String maximumNumberOfArgs,
            final String shortName, final String filterType) {
        checkNotEmpty(outputrDirectory, "outputrDirectory");
        checkNotEmpty(minimumNumberOfArgs, "minimumNumberOfArgs");
        checkNotEmpty(maximumNumberOfArgs, "maximumNumberOfArgs");
        checkNotEmpty(shortName, "shortName");
        final int min;
        try {
            min = Integer.parseInt(minimumNumberOfArgs);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException(
                    "minimumNumberOfArgs is not a number", e);
        }
        final int max;
        try {
            max = Integer.parseInt(maximumNumberOfArgs);
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException(
                    "maximumNumberOfArgs is not a number", e);
        }
        RETURN_TYPES2 = Boolean.parseBoolean(shortName) ? Util.SHORT_LABELS
                : Util.LONG_LABELS;
        String outDirFullPath = outputrDirectory;
        if (!outDirFullPath.endsWith(File.separator)) {
            outDirFullPath += File.separator;
        }

        outDirFullPath += packageName.replace('.', File.separatorChar);
        final File dir = new File(outDirFullPath);

        String fileHeader = "";
        if (licenseFile != null && !licenseFile.isEmpty()) {
            final File lic = new File(licenseFile).getAbsoluteFile();
            if (!lic.exists()) {
                throw new IllegalArgumentException(
                        "License File does not exist: " + licenseFile);
            }
            if (!lic.canRead()) {
                throw new IllegalArgumentException(
                        "License File cannot be read: " + licenseFile);
            }
            try (BufferedReader br = new BufferedReader(new FileReader(lic))) {
                String line;
                while ((line = br.readLine()) != null) {
                    fileHeader += line + "\n";
                }
            } catch (final IOException e) {
                throw new IllegalStateException("License File cannot be read: "
                        + licenseFile, e);
            }
        }
        String throwsStr2 = "";
        if (throwsStr != null && !throwsStr.isEmpty()) {
            throwsStr2 = " throws " + throwsStr;
        }
        generate(dir, fileHeader, packageName, funcNameInfix, procNamePrefix,
                methodName, throwsStr2, min, max, filterType);
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        if (args.length < 9 || args.length > 11) {
            System.out.println(USAGE);
        } else {

            final String outputrDirectory = args[0].trim();
            final String licenseFile = args[1].trim();
            final String packageName = args[2].trim();
            final String procNamePrefix = args[3].trim();
            final String funcNameInfix = args[4].trim();
            final String methodName = args[5].trim();
            final String minimumNumberOfArgs = args[6].trim();
            final String maximumNumberOfArgs = args[7].trim();
            final String shortName = args[8].trim();
            final String filterType = ((args.length == 9) || (args[9] == null)) ? null
                    : args[9].trim();
            String throwsStr = args.length < 11 ? "" : args[10];
            if (throwsStr == null) {
                throwsStr = "";
            }
            generate(outputrDirectory, licenseFile, packageName, funcNameInfix,
                    procNamePrefix, methodName, throwsStr, minimumNumberOfArgs,
                    maximumNumberOfArgs, shortName, filterType);
        }
    }
}
