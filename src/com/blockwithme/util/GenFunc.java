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

package com.blockwithme.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * <code>GenFunc</code> generates the source-code of the functions.
 *
 * Usage: GenFunc OutputDirectory LicenseFile PackageName ClassNamePrefix MethodName Throws MinimumNumberOfArgs MaximumNumberOfArgs
 *
 * OutputDirectory is the directory where the source files are generated. For example, C:\temp\funcs
 * LicenseFile is the path to the license file, to use as header. Can be "" or simply empty. For example, APACHE_LICENSE_HEADER.txt.
 * PackageName is the name of the package in which the interfaces are created, for example "com,test".
 * ClassNamePrefix the interface name prefix, for example "Func"
 * MethodName is the method name, for example "apply" or "call".
 * Throws can contain the optional name of a thrown "Throwable". Leave blank for no "throws". Example: java.io.IOException
 * MinimumNumberOfArgs is the minimum number of parameters the functions will have, for example 0.
 * MaximumNumberOfArgs is the maximum number of parameters the functions will have, for example 3.
 *
 * Hint: It supports up to 5 as the maximum number of parameters,
 * but that would be near 1 million interfaces; not recommended ... ;)
 *
 * @author monster
 *
 */
public class GenFunc {

	/** Usage */
	private static final String USAGE =
		"Usage:\n" +
		"    GenFunc OutputDirectory LicenseFile PackageName ClassNamePrefix MethodName Throws MinimumNumberOfArgs MaximumNumberOfArgs\n"+
		"\n"+
		"OutputDirectory is the directory where the source files are generated. For example, 'C:\temp\funcs'\n"+
		"LicenseFile is the path to the license file, to use as header. Can be '' or simply empty. For example, 'APACHE_LICENSE_HEADER.txt'.\n"+
		"PackageName is the name of the package in which the interfaces are created, for example 'com,test'.\n"+
		"ClassNamePrefix the interface name prefix, for example 'Func'\n"+
		"MethodName is the method name, for example 'apply' or 'call'.\n"+
		"Throws can contain the optional name of a thrown 'Throwable'. Leave blank for no 'throws'. Example: 'java.io.IOException'\n"+
		"MinimumNumberOfArgs is the minimum number of parameters the functions will have, for example 0.\n"+
		"MaximumNumberOfArgs is the maximum number of parameters the functions will have, for example 3.\n"+
		"\n"+
		"Hint: It supports up to 5 as the maximum number of parameters,\n"+
		"      but that would be near 1 million interfaces; not recommended ... ;)\n"
	;
	/** Format for interface name generation. */
	private static final String[] INTERFACE_NAME_FORMAT = {
		"%1$s0%2$s",
		"%1$s1%2$s%3$s",
		"%1$s2%2$s%3$s%4$s",
		"%1$s3%2$s%3$s%4$s%5$s",
		"%1$s4%2$s%3$s%4$s%5$s%6$s",
		"%1$s5%2$s%3$s%4$s%5$s%6$s%7$s",
	};

	/** The possible parameter types, except Object. */
	private static final String[] PARAM_TYPES = {
		"boolean", "byte", "char", "short", "int", "long", "float", "double"
	};

	/** ID of parameter of type object. */
	private static final int OBJECT_PARAM = PARAM_TYPES.length;

	/** The possible parameter types, including Object, capitalized for the interface name. */
	private static final String[] PARAM_TYPES2 = {
		"Boolean", "Byte", "Char", "Short", "Int", "Long", "Float", "Double", "Object"
	};

	/** The possible return types, including Object (generic R parameter). */
	private static final String[] RETURN_TYPES = {
		"void", "boolean", "byte", "char", "short", "int", "long", "float", "double", "R"
	};

	/** ID of return of type object. */
	private static final int OBJECT_RETURN = RETURN_TYPES.length-1;

	/** The possible return types, including Object, capitalized for the interface name. */
	private static final String[] RETURN_TYPES2 = {
		"Void", "Boolean", "Byte", "Char", "Short", "Int", "Long", "Float", "Double", "Object"
	};

	/** Maximum number of parameters. */
	public static final int MAXIMUM_PARAMETERS = INTERFACE_NAME_FORMAT.length-1;

	/** Generator class name. */
	public static final String GENERATOR = GenFunc.class.getName();

	/** Generates the functions. */
	public static void generate(final String outputrDirectory,
			final String licenseFile,
			final String packageName,
			final String classNamePrefix,
			final String methodName,
			final String throwsStr,
			final String minimumNumberOfArgs,
			final String maximumNumberOfArgs) {
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
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("minimumNumberOfArgs is not a number", e);
		}
		final int max;
		try {
			max = Integer.parseInt(maximumNumberOfArgs);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("maximumNumberOfArgs is not a number", e);
		}
		final File dir = new File(outputrDirectory);

		String fileHeader = "";
		if ((licenseFile != null) && !licenseFile.isEmpty()) {
			final File lic = new File(licenseFile);
			if (!lic.exists()) {
				throw new IllegalArgumentException("License File does not exist: "+licenseFile);
			}
			if (!lic.canRead()) {
				throw new IllegalArgumentException("License File cannot be read: "+licenseFile);
			}
			try {
				final BufferedReader br = new BufferedReader(new FileReader(lic));
				try {
					String line;
					while ((line = br.readLine()) != null) {
						fileHeader += line+"\n";
					}
				} finally {
					br.close();
				}
			} catch (IOException e) {
				throw new IllegalStateException("License File cannot be read: "+licenseFile, e);
			}
		}
		String throwsStr2 = "";
		if ((throwsStr != null) && !throwsStr.isEmpty()) {
			throwsStr2 = " throws "+throwsStr;
		}
		generate(dir, fileHeader, packageName, classNamePrefix, methodName, throwsStr2, min, max);
	}

	/** Generates the functions. */
	public static void generate(final File outputrDirectory,
			final String fileHeader,
			final String packageName,
			final String classNamePrefix,
			final String methodName,
			final String throwsStr,
			final int minimumNumberOfArgs,
			final int maximumNumberOfArgs) {
		if (outputrDirectory == null) {
			throw new IllegalArgumentException("outputrDirectory is null");
		}
		if (outputrDirectory.getName().isEmpty()) {
			throw new IllegalArgumentException("outputrDirectory is empty");
		}
		outputrDirectory.mkdirs();
		if (!outputrDirectory.isDirectory()) {
			throw new IllegalArgumentException("Cannot create outputrDirectory "+outputrDirectory);
		}
		if (!outputrDirectory.canWrite()) {
			throw new IllegalArgumentException("Cannot write to outputrDirectory "+outputrDirectory);
		}
		if (fileHeader != null) {
			final String h = fileHeader.trim();
			if (!h.isEmpty()) {
				if (!(h.startsWith("/*") && h.endsWith("*/") && (h.length() > 3))) {
					throw new IllegalArgumentException("fileHeader should contain nothing, or a C-style comment: "+fileHeader);
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
			if (!canBeDot && (c == '.')) {
				throw new IllegalArgumentException("packageName is invalid: "+packageName);
			}
			canBeDot = (c != '.');
			if (canBeDot) {
				if (identifierStart) {
					if (!Character.isJavaIdentifierStart(c)) {
						throw new IllegalArgumentException("packageName is invalid: "+packageName);
					}
				} else {
					if (!Character.isJavaIdentifierPart(c)) {
						throw new IllegalArgumentException("packageName is invalid: "+packageName);
					}
				}
			}
			identifierStart = (c == '.');
		}
		if (classNamePrefix == null) {
			throw new IllegalArgumentException("classNamePrefix is null");
		}
		if (classNamePrefix.isEmpty()) {
			throw new IllegalArgumentException("classNamePrefix is empty");
		}
		identifierStart = true;
		for (final char c : classNamePrefix.toCharArray()) {
			if (identifierStart) {
				if (!Character.isJavaIdentifierStart(c)) {
					throw new IllegalArgumentException("classNamePrefix is invalid: "+packageName);
				}
				identifierStart = false;
			} else {
				if (!Character.isJavaIdentifierPart(c)) {
					throw new IllegalArgumentException("classNamePrefix is invalid: "+packageName);
				}
			}
		}
		if (methodName == null) {
			throw new IllegalArgumentException("methodName is null");
		}
		if (methodName.isEmpty()) {
			throw new IllegalArgumentException("methodName is empty");
		}
		identifierStart = true;
		for (final char c : methodName.toCharArray()) {
			if (identifierStart) {
				if (!Character.isJavaIdentifierStart(c)) {
					throw new IllegalArgumentException("methodName is invalid: "+methodName);
				}
				identifierStart = false;
			} else {
				if (!Character.isJavaIdentifierPart(c)) {
					throw new IllegalArgumentException("methodName is invalid: "+methodName);
				}
			}
		}
		if ((minimumNumberOfArgs < 0) || (minimumNumberOfArgs > MAXIMUM_PARAMETERS)) {
			throw new IllegalArgumentException("minimumNumberOfArgs must be within [0,"+MAXIMUM_PARAMETERS+"]: "+minimumNumberOfArgs);
		}
		if ((maximumNumberOfArgs < 0) || (maximumNumberOfArgs > MAXIMUM_PARAMETERS)) {
			throw new IllegalArgumentException("maximumNumberOfArgs must be within [0,"+MAXIMUM_PARAMETERS+"]: "+maximumNumberOfArgs);
		}
		if (minimumNumberOfArgs > maximumNumberOfArgs) {
			throw new IllegalArgumentException("minimumNumberOfArgs ("+minimumNumberOfArgs
					+") must be <= maximumNumberOfArgs("+maximumNumberOfArgs+")");
		}

		doGenerate(outputrDirectory, (fileHeader == null) ? "" : fileHeader,
				packageName, classNamePrefix, methodName, throwsStr, minimumNumberOfArgs, maximumNumberOfArgs);
	}

	/** Generates the functions. */
	private static void doGenerate(final File outputrDirectory,
			final String fileHeader,
			final String packageName,
			final String classNamePrefix,
			final String methodName,
			final String throwsStr,
			final int minimumNumberOfArgs,
			final int maximumNumberOfArgs) {
		System.out.println("Generating fucntions:");
		System.out.println("    Header:                       "+(fileHeader.isEmpty() ? "No" : "Yes"));
		System.out.println("    Package:                      "+packageName);
		System.out.println("    Class Name Prefix:            "+classNamePrefix);
		System.out.println("    Method Name:                  "+methodName);
		System.out.println("    Throws:                       "+throwsStr);
		System.out.println("    Minimum Number Of Parameters: "+minimumNumberOfArgs);
		System.out.println("    Maximum Number Of Parameters: "+maximumNumberOfArgs);
		String content = fileHeader;
		content += "\npackage "+packageName+";\n\n";
		content += "/**\n * Primitive Function Interface <ode>%1$s</code>.\n";
		content += " * Generated automatically by "+GENERATOR+"\n */\n";
		content += "public interface %1$s {\n";
		content += "    /** Function <code>"+methodName+"</code> */\n";
		content += "    %2$s "+methodName+"(%3$s)"+throwsStr+";\n";
		content += "}\n";
		int total = 0;
		for (int p = minimumNumberOfArgs; p <= maximumNumberOfArgs; p++) {
			System.out.print("Generation functions with "+p+" parameters ...");
			final int count = doGenerate(outputrDirectory, classNamePrefix, content, p);
			System.out.println(" "+count+" functions generated.");
			total += count;
		}
		System.out.println(total+" total functions generated.");
	}

	/** Generated the interface name for 0-arg function. */
	private static String genName(final String classNamePrefix, final int returnType) {
		return String.format(INTERFACE_NAME_FORMAT[0], classNamePrefix, RETURN_TYPES2[returnType]);
	}

	/** Generated the interface name for 1-arg function. */
	private static String genName(final String classNamePrefix, final int returnType,
			final int a0) {
		return String.format(INTERFACE_NAME_FORMAT[1], classNamePrefix,
				RETURN_TYPES2[returnType], PARAM_TYPES2[a0]);
	}

	/** Generated the interface name for 2-arg function. */
	private static String genName(final String classNamePrefix, final int returnType,
			final int a0, final int a1) {
		return String.format(INTERFACE_NAME_FORMAT[2], classNamePrefix,
				RETURN_TYPES2[returnType], PARAM_TYPES2[a0], PARAM_TYPES2[a1]);
	}

	/** Generated the interface name for 3-arg function. */
	private static String genName(final String classNamePrefix, final int returnType,
			final int a0, final int a1, final int a2) {
		return String.format(INTERFACE_NAME_FORMAT[3], classNamePrefix,
				RETURN_TYPES2[returnType], PARAM_TYPES2[a0], PARAM_TYPES2[a1],
				PARAM_TYPES2[a2]);
	}

	/** Generated the interface name for 4-arg function. */
	private static String genName(final String classNamePrefix, final int returnType,
			final int a0, final int a1, final int a2, final int a3) {
		return String.format(INTERFACE_NAME_FORMAT[4], classNamePrefix,
				RETURN_TYPES2[returnType], PARAM_TYPES2[a0], PARAM_TYPES2[a1],
				PARAM_TYPES2[a2], PARAM_TYPES2[a3]);
	}

	/** Generated the interface name for 5-arg function. */
	private static String genName(final String classNamePrefix, final int returnType,
			final int a0, final int a1, final int a2, final int a3, final int a4) {
		return String.format(INTERFACE_NAME_FORMAT[5], classNamePrefix,
				RETURN_TYPES2[returnType], PARAM_TYPES2[a0], PARAM_TYPES2[a1],
				PARAM_TYPES2[a2], PARAM_TYPES2[a3], PARAM_TYPES2[a4]);
	}

	/** Generated the generic parameters for the interface definition. */
	private static String genGenericsParams(final int returnType, final int ... params) {
		int genericParams = 0;
		for (int i = 0; i < params.length; i++) {
			final int p = params[i];
			if (p == OBJECT_PARAM) {
				genericParams++;
			}
		}
		final boolean returnGeneric = (returnType == OBJECT_RETURN);
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
			result += c+",";
			c++;
		}
		return result.substring(0, result.length()-1) + ">";
	}

	/** Generated the generic parameters for the interface definition. */
	private static String genName2(final String classNamePrefix,
			final int returnType, final int ... params) {
		final String name;
		final int count = params.length;
		switch(count) {
		case 0:
			name = genName(classNamePrefix, returnType);
			break;
		case 1:
			name = genName(classNamePrefix, returnType, params[0]);
			break;
		case 2:
			name = genName(classNamePrefix, returnType, params[0], params[1]);
			break;
		case 3:
			name = genName(classNamePrefix, returnType, params[0], params[1], params[2]);
			break;
		case 4:
			name = genName(classNamePrefix, returnType, params[0], params[1], params[2], params[3]);
			break;
		case 5:
			name = genName(classNamePrefix, returnType, params[0], params[1], params[2], params[3], params[4]);
			break;
		default:
			throw new IllegalArgumentException("Too many parameters: "+count);
		}
		return name;
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
				result += PARAM_TYPES[p];
			}
			result += " p"+i;
		}
		return result;
	}

	/** Outputs the generated interface. */
	private static void outputInterface(final File file, final String content) {
		try {
			final FileWriter fw = new FileWriter(file);
			try {
				fw.write(content);
			} finally {
				fw.close();
			}
		} catch(IOException e) {
			throw new IllegalStateException("Failed to write to "+file, e);
		}
	}


	/**
	 * Generates the functions.
	 * The expected format of <code>format</code> is:
	 * #0 %s = InterfaceName
	 * #1 %s = return type
	 * #2 %s = Parameter list
	 */
	private static int doGenerate(final File outputrDirectory,
			final String classNamePrefix,
			final String format,
			final int numberOfArgs) {
		int result = 0;
		for (int r = 0; r <= OBJECT_RETURN; r++) {
			final String returnType = RETURN_TYPES[r];
			final int[] params = new int[numberOfArgs];
			int current = 0;
			boolean again = true;
			while (again) {
				final String name = genName2(classNamePrefix, r, params);
				final String genParams = genGenericsParams(r, params);
				final String paramList = genParameterList(params);
				final String content = String.format(format, name+genParams, returnType, paramList);
				final File file = new File(outputrDirectory, name+".java");
				outputInterface(file, content);
				result++;
				if (result % 1000 == 0) {
					System.out.print(".");
				}
				if (numberOfArgs == 0) {
					again = false;
				} else {
					String s = Integer.toString(++current, PARAM_TYPES.length+1);
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 8) {
			System.out.println(USAGE);
		} else {
			final String outputrDirectory = args[0].trim();
			final String licenseFile = args[1].trim();
			final String packageName = args[2].trim();
			final String classNamePrefix = args[3].trim();
			final String methodName = args[4].trim();
			final String throwsStr = args[5].trim();
			final String minimumNumberOfArgs = args[6].trim();
			final String maximumNumberOfArgs = args[7].trim();
			generate(outputrDirectory, licenseFile, packageName, classNamePrefix,
					methodName, throwsStr, minimumNumberOfArgs, maximumNumberOfArgs);
		}
	}
}
