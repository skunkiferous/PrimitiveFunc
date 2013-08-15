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

package com.blockwithme.tuples.gen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import com.blockwithme.fn.util.Tuple;

/**
 * <code>GenTuple</code> generates the source-code of the tuples.
 *
 * Usage: GenTuple OutputDirectory LicenseFile PackageName MinimumNumberOfArgs MaximumNumberOfArgs Filter
 *
 * OutputDirectory is the directory where the source files are generated. For example, C:\temp\tuples
 * LicenseFile is the path to the license file, to use as header. Can be "" or simply empty. For example, APACHE_LICENSE_HEADER.txt.
 * PackageName is the name of the package in which the classes are created, for example "com.test".
 * MinimumNumberOfArgs is the minimum number of parameters the tuples will have, for example 0.
 * MaximumNumberOfArgs is the maximum number of parameters the tuples will have, for example 3.
 * Filter is the name of a class that implements com.blockwithme.fn.gen.TupleFilter (optional).
 *
 * Hint: It supports up to 5 as the maximum number of parameters,
 * but that would be near 1 million interfaces; not recommended ... ;)
 *
 * @author monster
 *
 */
public class GenTuple {

    /** The possible parameter types, except Object. */
    private static final String[] _PARAM_TYPES = { "boolean", "byte", "char",
            "short", "int", "long", "float", "double" };

    private static Class<?> TUPLE_INTERFACE = Tuple.class;

    /** Format for class name generation. */
    private static final String[] CLASS_NAME_FORMAT = { null, "T1%1$s",
            "T2%1$s%2$s", "T3%1$s%2$s%3$s", "T4%1$s%2$s%3$s%4$s",
            "T5%1$s%2$s%3$s%4$s%5$s", };

    /** ID of parameter of type object. */
    private static final int OBJECT_PARAM = _PARAM_TYPES.length;

    /** The possible parameter types, including Object, capitalized for the class name. */
    private static final String[] PARAM_TYPES2 = { "Z", "B", "C", "S", "I",
            "L", "F", "D", "O" };

    /** The possible parameter types, as enum for faster filtering. */
    private static final TupleFilter.ParamType[] PARAM_TYPES3 = {
            TupleFilter.ParamType.Boolean, TupleFilter.ParamType.Byte,
            TupleFilter.ParamType.Char, TupleFilter.ParamType.Short,
            TupleFilter.ParamType.Int, TupleFilter.ParamType.Long,
            TupleFilter.ParamType.Float, TupleFilter.ParamType.Double,
            TupleFilter.ParamType.Object };

    /** Format for class name generation. */
    private static final String[] SIGN_FORMAT = { null, "{%1$s};",
            "{%1$s, %2$s};", "{%1$s, %2$s, %3$s};",
            "{%1$s, %2$s, %3$s, %4$s};", "{%1$s, %2$s, %3$s, %4$s, %5$s};", };

    /** The possible parameter types as a class */
    private static final String[] SIGN_PARAM_TYPES = { "Boolean.TYPE",
            "Byte.TYPE", "Character.TYPE", "Short.TYPE", "Integer.TYPE",
            "Long.TYPE", "Float.TYPE", "Double.TYPE", "Object.class" };

    /** Usage */
    private static final String USAGE = "Usage:\n"
            + "    GenTuple OutputDirectory LicenseFile PackageName MinimumNumberOfArgs MaximumNumberOfArgs Filter\n"
            + "\n"
            + "OutputDirectory is the directory where the source files are generated. For example, 'C:\temp\funcs'\n"
            + "LicenseFile is the path to the license file, to use as header. Can be '' or simply empty. For example, 'APACHE_LICENSE_HEADER.txt'.\n"
            + "PackageName is the name of the package in which the interfaces are created, for example 'com,test'.\n"
            + "MinimumNumberOfArgs is the minimum number of parameters the functions will have, for example 0.\n"
            + "MaximumNumberOfArgs is the maximum number of parameters the functions will have, for example 3.\n"
            + "Filter is the name of a class that implements com.blockwithme.fn.gen.TupleFilter (optional).\n"
            + "\n"
            + "Hint: It supports up to 5 as the maximum number of parameters,\n"
            + "      but that would be near 1 million classes; not recommended ... ;)\n";

    /** Generator class name. */
    public static final String GENERATOR = GenTuple.class.getName();

    /** Maximum number of parameters. */
    public static final int MAXIMUM_PARAMETERS = CLASS_NAME_FORMAT.length - 1;

    /**
     * Generates the tuples.
     * The expected format of <code>format</code> is:
     * #0 %s = ClassName
     * #1 %s = Parameter list
     */
    private static int doGenerate(final File outputrDirectory,
            final String format, final int numberOfArgs,
            final TupleFilter filter) {
        int result = 0;
        final int[] params = new int[numberOfArgs];
        int current = 0;
        boolean again = true;
        while (again) {
            if (filter == null || filter.accept(genParameterList2(params))) {
                final String[] generatedStrings = genName2(params);
                final String name = generatedStrings[0];
                final String signature = generatedStrings[1];
                final String fields = genFields(params);
                final String ctr = genConstructor(params);
                final String equals = genEquals(params);
                final String hashCode = genHashCode(params);
                final String toString = genToString(params);
                final String genParams = genGenericsParams(params);
                final String paramList = genParameterList(params);
                final String size = String.valueOf(params.length);
                final String get = genGet(params);
                final String content = String.format(format, name, genParams,
                        signature, paramList, fields, ctr, equals, hashCode,
                        toString, size, get);
                final File file = new File(outputrDirectory, name + ".java");
                outputClass(file, content);
            }
            result++;
            if (result % 250 == 0) {
                System.out.print(".");
            }
            if (numberOfArgs == 0) {
                again = false;
            } else {
                String s = Integer.toString(++current, _PARAM_TYPES.length + 1);
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
        return result;
    }

    /** Generates the classes. */
    private static void doGenerate(final File outputrDirectory,
            final String fileHeader, final String packageName,
            final int minimumNumberOfArgs, final int maximumNumberOfArgs,
            final TupleFilter filter) {
        System.out.println("Generating classes:");
        System.out.println("    Header:                       "
                + (fileHeader.isEmpty() ? "No" : "Yes"));
        System.out.println("    Package:                      " + packageName);
        System.out.println("    Minimum Number Of Parameters: "
                + minimumNumberOfArgs);
        System.out.println("    Maximum Number Of Parameters: "
                + maximumNumberOfArgs);
        System.out.println("    Function filter:              " + filter);
        String content = fileHeader;
        content += "\npackage " + packageName + ";\n\n";
        content += "\nimport " + TUPLE_INTERFACE.getName() + ";\n\n";
        content += "/**\n * Primitive Tuple Class <code>%1$s</code>.\n";
        content += " * Generated automatically by " + GENERATOR + "\n */\n";
        content += "public class %1$s%2$s extends "
                + TUPLE_INTERFACE.getSimpleName() + " {\n";
        content += "    /** SIGNATURE constant */\n";
        content += "    public static final %3$s" + "\n";
        content += "    \n";
        content += "%5$s";
        content += "    /** Constructor */\n";
        content += "    public %1$s(%4$s) {\n";
        content += "%6$s";
        content += "    }\n\n";
        content += "    /** equals */\n";
        content += "    @Override\n";
        content += "    public final boolean equals(final Object other) {\n";
        content += "        if (!(other instanceof %1$s)) {\n";
        content += "            return false;\n";
        content += "        }\n";
        content += "        final %1$s o = (%1$s) other;\n";
        content += "%7$s";
        content += "    }\n\n";
        content += "    /** hashCode */\n";
        content += "    @Override\n";
        content += "    public final int hashCode() {\n";
        content += "%8$s";
        content += "    }\n\n";
        content += "    /** toString */\n";
        content += "    @Override\n";
        content += "    public final String toString() {\n";
        content += "%9$s";
        content += "    }\n\n";
        content += "    /** Returns the number of fields */\n";
        content += "    @Override\n";
        content += "    public final int size() {\n";
        content += "        return %10$s;\n";
        content += "    }\n\n";
        content += "    /** Returns the field with the given number */\n";
        content += "    @Override\n";
        content += "    public final Object get(final int fieldNumber) {\n";
        content += "%11$s";
        content += "    }\n";
        content += "}\n";
        int total = 0;
        for (int p = minimumNumberOfArgs; p <= maximumNumberOfArgs; p++) {
            System.out.print("Generation tuples with " + p + " parameters ...");
            final int count = doGenerate(outputrDirectory, content, p, filter);
            System.out.println(" " + count + " tuples generated.");
            total += count;
        }
        System.out.println(total + " total tuples generated.");
    }

    /** Generated the generic parameters for the class definition. */
    private static String genGenericsParams(final int... params) {
        int genericParams = 0;
        for (int i = 0; i < params.length; i++) {
            final int p = params[i];
            if (p == OBJECT_PARAM) {
                genericParams++;
            }
        }
        if (genericParams == 0) {
            return "";
        }
        String result = "<";
        char c = 'A';
        for (int i = 0; i < genericParams; i++) {
            result += c + ",";
            c++;
        }
        return result.substring(0, result.length() - 1) + ">";
    }

    /** Generated the fields for the class definition. */
    private static String genFields(final int... params) {
        final StringBuilder buf = new StringBuilder(params.length * 100);
        int genericParams = 0;
        for (int i = 0; i < params.length; i++) {
            final int p = params[i];
            final String type;
            if (p == OBJECT_PARAM) {
                type = String.valueOf((char) ('A' + genericParams));
                genericParams++;
            } else {
                type = _PARAM_TYPES[p];
            }
            buf.append("    /** Tuple field #").append(i).append(" */\n");
            buf.append("    public final ").append(type).append(" _").append(i)
                    .append(";\n\n");
        }
        return buf.toString();
    }

    /** Generated the fields initialization for the class definition. */
    private static String genConstructor(final int... params) {
        final StringBuilder buf = new StringBuilder(params.length * 100);
        for (int i = 0; i < params.length; i++) {
            buf.append("        _").append(i).append(" = p").append(i)
                    .append(";\n");
        }
        return buf.toString();
    }

    /** Generated the toString() method for the class definition. */
    private static String genToString(final int... params) {
        final StringBuilder buf = new StringBuilder(params.length * 100);
        buf.append("        final StringBuilder buf = new StringBuilder(");
        buf.append(params.length * 10).append(");\n");
        buf.append("        buf.append('(');\n");
        for (int i = 0; i < params.length; i++) {
            if (i > 0) {
                buf.append("        buf.append(',');\n");
            }
            buf.append("        buf.append(_").append(i).append(");\n");
        }
        buf.append("        buf.append(')');\n");
        buf.append("        return buf.toString();\n");
        return buf.toString();
    }

    /** Generated the hashCode() method for the class definition. */
    private static String genHashCode(final int... params) {
        final StringBuilder buf = new StringBuilder(params.length * 100);
        buf.append("        final int prime = 31;\n");
        buf.append("        int result = 1;\n");
        for (int i = 0; i < params.length; i++) {
            buf.append("        result = prime * result + hash(_").append(i)
                    .append(");\n");
        }
        buf.append("        return result;\n");
        return buf.toString();
    }

    /** Generated the equals() method for the class definition. */
    private static String genEquals(final int... params) {
        final StringBuilder buf = new StringBuilder(params.length * 20);
        buf.append("        return ");
        for (int i = 0; i < params.length; i++) {
            if (i > 0) {
                buf.append(" && ");
            }
            if (params[i] == OBJECT_PARAM) {
                buf.append("equals(_").append(i).append(", o._").append(i)
                        .append(")");
            } else {
                buf.append("(_").append(i).append(" == o._").append(i)
                        .append(")");
            }
        }
        buf.append(";\n");
        return buf.toString();
    }

    /** Generated the get() method for the class definition. */
    private static String genGet(final int... params) {
        final StringBuilder buf = new StringBuilder(params.length * 20);
        buf.append("        switch (fieldNumber) {\n");
        for (int i = 0; i < params.length; i++) {
            buf.append("            case ").append(i).append(": return _")
                    .append(i).append(";\n");
        }
        buf.append("            default: throw new IllegalArgumentException(String.valueOf(fieldNumber));\n");
        buf.append("        }\n");
        return buf.toString();
    }

    /** Generated the class name for 1-arg tuple. */
    private static String genName(final int a0) {
        return String.format(CLASS_NAME_FORMAT[1], PARAM_TYPES2[a0]);
    }

    /** Generated the class name for 2-arg tuple. */
    private static String genName(final int a0, final int a1) {
        return String.format(CLASS_NAME_FORMAT[2], PARAM_TYPES2[a0],
                PARAM_TYPES2[a1]);
    }

    /** Generated the class name for 3-arg tuple. */
    private static String genName(final int a0, final int a1, final int a2) {
        return String.format(CLASS_NAME_FORMAT[3], PARAM_TYPES2[a0],
                PARAM_TYPES2[a1], PARAM_TYPES2[a2]);
    }

    /** Generated the class name for 4-arg tuple. */
    private static String genName(final int a0, final int a1, final int a2,
            final int a3) {
        return String.format(CLASS_NAME_FORMAT[4], PARAM_TYPES2[a0],
                PARAM_TYPES2[a1], PARAM_TYPES2[a2], PARAM_TYPES2[a3]);
    }

    /** Generated the class name for 5-arg tuple. */
    private static String genName(final int a0, final int a1, final int a2,
            final int a3, final int a4) {
        return String.format(CLASS_NAME_FORMAT[5], PARAM_TYPES2[a0],
                PARAM_TYPES2[a1], PARAM_TYPES2[a2], PARAM_TYPES2[a3],
                PARAM_TYPES2[a4]);
    }

    /** Generated the generic parameters for the class definition. */
    private static String[] genName2(final int... params) {
        final String name;
        final int count = params.length;
        String signatureString = "Class<?>[] SIGNATURE = ";
        switch (count) {
        case 0:
            throw new IllegalArgumentException("count == 0");
        case 1:
            name = genName(params[0]);
            signatureString += genSignature(params[0]);
            break;
        case 2:
            name = genName(params[0], params[1]);
            signatureString += genSignature(params[0], params[1]);
            break;
        case 3:
            name = genName(params[0], params[1], params[2]);
            signatureString += genSignature(params[0], params[1], params[2]);
            break;
        case 4:
            name = genName(params[0], params[1], params[2], params[3]);
            signatureString += genSignature(params[0], params[1], params[2],
                    params[3]);
            break;
        case 5:
            name = genName(params[0], params[1], params[2], params[3],
                    params[4]);
            signatureString += genSignature(params[0], params[1], params[2],
                    params[3], params[4]);
            break;
        default:
            throw new IllegalArgumentException("Too many parameters: " + count);
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
    private static TupleFilter.ParamType[] genParameterList2(final int[] params) {
        final TupleFilter.ParamType[] result = new TupleFilter.ParamType[params.length];
        for (int i = 0; i < params.length; i++) {
            result[i] = PARAM_TYPES3[params[i]];
        }
        return result;
    }

    /** Generated the class name for 1-arg tuple. */
    private static String genSignature(final int a0) {
        return String.format(SIGN_FORMAT[1], SIGN_PARAM_TYPES[a0]);
    }

    /** Generated the class name for 2-arg tuple. */
    private static String genSignature(final int a0, final int a1) {
        return String.format(SIGN_FORMAT[2], SIGN_PARAM_TYPES[a0],
                SIGN_PARAM_TYPES[a1]);
    }

    /** Generated the class name for 3-arg tuple. */
    private static String genSignature(final int a0, final int a1, final int a2) {
        return String.format(SIGN_FORMAT[3], SIGN_PARAM_TYPES[a0],
                SIGN_PARAM_TYPES[a1], SIGN_PARAM_TYPES[a2]);
    }

    /** Generated the class name for 4-arg tuple. */
    private static String genSignature(final int a0, final int a1,
            final int a2, final int a3) {
        return String.format(SIGN_FORMAT[4], SIGN_PARAM_TYPES[a0],
                SIGN_PARAM_TYPES[a1], SIGN_PARAM_TYPES[a2],
                SIGN_PARAM_TYPES[a3]);
    }

    /** Generated the class name for 5-arg tuple. */
    private static String genSignature(final int a0, final int a1,
            final int a2, final int a3, final int a4) {
        return String.format(SIGN_FORMAT[5], SIGN_PARAM_TYPES[a0],
                SIGN_PARAM_TYPES[a1], SIGN_PARAM_TYPES[a2],
                SIGN_PARAM_TYPES[a3], SIGN_PARAM_TYPES[a4]);
    }

    /** Outputs the generated class. */
    private static void outputClass(final File file, final String content) {
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

    /** Generates the tuples. */
    public static void generate(final File outputrDirectory,
            final String fileHeader, final String packageName,
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
        if (minimumNumberOfArgs < 1 || minimumNumberOfArgs > MAXIMUM_PARAMETERS) {
            throw new IllegalArgumentException(
                    "minimumNumberOfArgs must be within [1,"
                            + MAXIMUM_PARAMETERS + "]: " + minimumNumberOfArgs);
        }
        if (maximumNumberOfArgs < 1 || maximumNumberOfArgs > MAXIMUM_PARAMETERS) {
            throw new IllegalArgumentException(
                    "maximumNumberOfArgs must be within [1,"
                            + MAXIMUM_PARAMETERS + "]: " + maximumNumberOfArgs);
        }
        if (minimumNumberOfArgs > maximumNumberOfArgs) {
            throw new IllegalArgumentException("minimumNumberOfArgs ("
                    + minimumNumberOfArgs + ") must be <= maximumNumberOfArgs("
                    + maximumNumberOfArgs + ")");
        }

        TupleFilter filter = null;
        if (filterType != null && !filterType.isEmpty()) {
            try {
                filter = (TupleFilter) Class.forName(filterType).newInstance();
            } catch (InstantiationException | IllegalAccessException
                    | ClassNotFoundException e) {
                throw new IllegalArgumentException("bad filter type ("
                        + filterType + ")", e);
            }
        }
        doGenerate(outputrDirectory, fileHeader == null ? "" : fileHeader,
                packageName, minimumNumberOfArgs, maximumNumberOfArgs, filter);
    }

    /** Generates the tuples. */
    public static void generate(final String outputrDirectory,
            final String licenseFile, final String packageName,
            final String minimumNumberOfArgs, final String maximumNumberOfArgs,
            final String filterType) {
        if (outputrDirectory == null) {
            throw new IllegalArgumentException("outputrDirectory is null");
        }
        if (outputrDirectory.isEmpty()) {
            throw new IllegalArgumentException("outputrDirectory is empty");
        }
        if (minimumNumberOfArgs == null) {
            throw new IllegalArgumentException("minimumNumberOfArgs is null");
        }
        if (minimumNumberOfArgs.isEmpty()) {
            throw new IllegalArgumentException("minimumNumberOfArgs is empty");
        }
        if (maximumNumberOfArgs == null) {
            throw new IllegalArgumentException("maximumNumberOfArgs is null");
        }
        if (maximumNumberOfArgs.isEmpty()) {
            throw new IllegalArgumentException("maximumNumberOfArgs is empty");
        }
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
        generate(dir, fileHeader, packageName, min, max, filterType);
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        if ((args.length != 5) && (args.length != 6)) {
            System.out.println(USAGE);
        } else {

            final String outputrDirectory = args[0].trim();
            final String licenseFile = args[1].trim();
            final String packageName = args[2].trim();
            final String minimumNumberOfArgs = args[3].trim();
            final String maximumNumberOfArgs = args[4].trim();
            final String filterType = args.length == 5 ? null : args[5].trim();
            generate(outputrDirectory, licenseFile, packageName,
                    minimumNumberOfArgs, maximumNumberOfArgs, filterType);
        }
    }
}
