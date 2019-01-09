package org.tensorflow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

final class NativeLibrary {
    private static final boolean DEBUG = (System.getProperty("org.tensorflow.NativeLibrary.DEBUG") != null);
    private static final String JNI_LIBNAME = "tensorflow_jni";

    public static void load() {
        if (!isLoaded()) {
            if (!tryLoadLibrary()) {
                String mapLibraryName = System.mapLibraryName(JNI_LIBNAME);
                String makeResourceName = makeResourceName(mapLibraryName);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("jniResourceName: ");
                stringBuilder.append(makeResourceName);
                log(stringBuilder.toString());
                InputStream resourceAsStream = NativeLibrary.class.getClassLoader().getResourceAsStream(makeResourceName);
                String maybeAdjustForMacOS = maybeAdjustForMacOS(System.mapLibraryName("tensorflow_framework"));
                String makeResourceName2 = makeResourceName(maybeAdjustForMacOS);
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("frameworkResourceName: ");
                stringBuilder2.append(makeResourceName2);
                log(stringBuilder2.toString());
                InputStream resourceAsStream2 = NativeLibrary.class.getClassLoader().getResourceAsStream(makeResourceName2);
                if (resourceAsStream == null) {
                    throw new UnsatisfiedLinkError(String.format("Cannot find TensorFlow native library for OS: %s, architecture: %s. See https://github.com/tensorflow/tensorflow/tree/master/tensorflow/java/README.md for possible solutions (such as building the library from source). Additional information on attempts to find the native library can be obtained by adding org.tensorflow.NativeLibrary.DEBUG=1 to the system properties of the JVM.", new Object[]{os(), architecture()}));
                }
                try {
                    File createTemporaryDirectory = createTemporaryDirectory();
                    createTemporaryDirectory.deleteOnExit();
                    String file = createTemporaryDirectory.toString();
                    if (resourceAsStream2 != null) {
                        extractResource(resourceAsStream2, maybeAdjustForMacOS, file);
                    } else {
                        StringBuilder stringBuilder3 = new StringBuilder();
                        stringBuilder3.append(makeResourceName2);
                        stringBuilder3.append(" not found. This is fine assuming ");
                        stringBuilder3.append(makeResourceName);
                        stringBuilder3.append(" is not built to depend on it.");
                        log(stringBuilder3.toString());
                    }
                    System.load(extractResource(resourceAsStream, mapLibraryName, file));
                } catch (IOException e) {
                    throw new UnsatisfiedLinkError(String.format("Unable to extract native library into a temporary file (%s)", new Object[]{e.toString()}));
                }
            }
        }
    }

    private static boolean tryLoadLibrary() {
        try {
            System.loadLibrary(JNI_LIBNAME);
            return true;
        } catch (UnsatisfiedLinkError e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("tryLoadLibraryFailed: ");
            stringBuilder.append(e.getMessage());
            log(stringBuilder.toString());
            return false;
        }
    }

    private static boolean isLoaded() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
	at jadx.api.JadxDecompiler$$Lambda$8/32863545.run(Unknown Source)
*/
        /*
        org.tensorflow.TensorFlow.version();	 Catch:{ UnsatisfiedLinkError -> 0x000a }
        r0 = "isLoaded: true";	 Catch:{ UnsatisfiedLinkError -> 0x000a }
        log(r0);	 Catch:{ UnsatisfiedLinkError -> 0x000a }
        r0 = 1;
        return r0;
    L_0x000a:
        r0 = 0;
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.tensorflow.NativeLibrary.isLoaded():boolean");
    }

    private static String maybeAdjustForMacOS(String str) {
        if (!System.getProperty("os.name").contains("OS X") || NativeLibrary.class.getClassLoader().getResource(makeResourceName(str)) != null || !str.endsWith(".dylib")) {
            return str;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str.substring(0, str.length() - ".dylib".length()));
        stringBuilder.append(".so");
        return stringBuilder.toString();
    }

    private static String extractResource(InputStream inputStream, String str, String str2) throws IOException {
        File file = new File(str2, str);
        file.deleteOnExit();
        str = file.toString();
        str2 = new StringBuilder();
        str2.append("extracting native library to: ");
        str2.append(str);
        log(str2.toString());
        long copy = copy(inputStream, file);
        log(String.format("copied %d bytes to %s", new Object[]{Long.valueOf(copy), str}));
        return str;
    }

    private static String os() {
        String toLowerCase = System.getProperty("os.name").toLowerCase();
        if (toLowerCase.contains("linux")) {
            return "linux";
        }
        if (!toLowerCase.contains("os x")) {
            if (!toLowerCase.contains("darwin")) {
                if (toLowerCase.contains("windows")) {
                    return "windows";
                }
                return toLowerCase.replaceAll("\\s", "");
            }
        }
        return "darwin";
    }

    private static String architecture() {
        String toLowerCase = System.getProperty("os.arch").toLowerCase();
        return toLowerCase.equals("amd64") ? "x86_64" : toLowerCase;
    }

    private static void log(String str) {
        if (DEBUG) {
            PrintStream printStream = System.err;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("org.tensorflow.NativeLibrary: ");
            stringBuilder.append(str);
            printStream.println(stringBuilder.toString());
        }
    }

    private static String makeResourceName(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("org/tensorflow/native/");
        stringBuilder.append(String.format("%s-%s/", new Object[]{os(), architecture()}));
        stringBuilder.append(str);
        return stringBuilder.toString();
    }

    private static long copy(InputStream inputStream, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            file = new byte[1048576];
            long j = 0;
            while (true) {
                int read = inputStream.read(file);
                if (read < 0) {
                    break;
                }
                fileOutputStream.write(file, 0, read);
                j += (long) read;
            }
            return j;
        } finally {
            fileOutputStream.close();
            inputStream.close();
        }
    }

    private static File createTemporaryDirectory() {
        File file = new File(System.getProperty("java.io.tmpdir"));
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("tensorflow_native_libraries-");
        stringBuilder.append(System.currentTimeMillis());
        stringBuilder.append("-");
        String stringBuilder2 = stringBuilder.toString();
        for (int i = 0; i < 1000; i++) {
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(stringBuilder2);
            stringBuilder3.append(i);
            File file2 = new File(file, stringBuilder3.toString());
            if (file2.mkdir()) {
                return file2;
            }
        }
        StringBuilder stringBuilder4 = new StringBuilder();
        stringBuilder4.append("Could not create a temporary directory (tried to make ");
        stringBuilder4.append(stringBuilder2);
        stringBuilder4.append("*) to extract TensorFlow native libraries.");
        throw new IllegalStateException(stringBuilder4.toString());
    }

    private NativeLibrary() {
    }
}
