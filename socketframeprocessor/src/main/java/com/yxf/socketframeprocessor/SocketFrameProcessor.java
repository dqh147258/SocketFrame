package com.yxf.socketframeprocessor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public class SocketFrameProcessor extends AbstractProcessor {

    private static final String TARGET_ANNOTATION = "com.yxf.socketframe.annotation.SocketInterface";
    private static final String ENCODER_POSTFIX = "_Encoder";
    private static final String DECODER_POSTFIX = "_Decoder";

    private static final List<String> supportClass = new ArrayList<String>() {
        {
            add("com.yxf.socketframe.packet.Packet");
            add("java.lang.String");
        }
    };


    private Messager messager;
    private boolean writerRoundDone;
    private int round;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        round++;
        messager.printMessage(Diagnostic.Kind.NOTE, "Processing round " + round + ", new annotations: " +
                !set.isEmpty() + ", processingOver: " + roundEnvironment.processingOver());

        if (roundEnvironment.processingOver()) {
            if (!set.isEmpty()) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Unexpected processing state: annotations still available after processing over");
                return false;
            }
        }

        if (set.isEmpty()) {
            return false;
        }

        if (writerRoundDone) {
            messager.printMessage(Diagnostic.Kind.ERROR,
                    "Unexpected processing state: annotations still available after writing.");
        }

        if (!generateSourceFile(set, roundEnvironment)) {
            return false;
        }

        writerRoundDone = true;
        return true;
    }

    private boolean generateSourceFile(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (TypeElement annotation : set) {
            Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(annotation);
            for (Element element : elements) {
                if (element.getKind() != ElementKind.INTERFACE) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "the element @SocketInterface attached mast be a interface");
                    return false;
                } else {
                    messager.printMessage(Diagnostic.Kind.NOTE, "class : " + element.getClass().getCanonicalName());
                }
                TypeElement interfaceElement = (TypeElement) element;
                if (!createSourceFileByInterface(interfaceElement, roundEnvironment)) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "create source file by interface failed");
                    return false;
                }
            }
        }
        return false;
    }

    private boolean createSourceFileByInterface(TypeElement interfaceElement, RoundEnvironment roundEnvironment) {
        String qualifiedClassName = interfaceElement.getQualifiedName().toString();
        String className = qualifiedClassName.substring(qualifiedClassName.lastIndexOf(".") + 1);
        String classPath = qualifiedClassName.substring(0, qualifiedClassName.lastIndexOf("."));
        messager.printMessage(Diagnostic.Kind.NOTE, "class name : " + className + " , class path : " + classPath);
        if (!createEncoderSourceFile(interfaceElement, classPath, className)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "create encoder source file failed.");
            return false;
        }
        if (!createDecoderSourceFile(interfaceElement, classPath, className)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "create decoder source file failed.");
            return false;
        }
        return true;
    }

    private boolean createDecoderSourceFile(TypeElement interfaceElement, String classPath, String className) {
        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(classPath).append(";\n\n");
        builder.append("import com.yxf.socketframe.packet.InterfacePacket;\n");
        builder.append("import com.yxf.socketframe.handler.InterfaceHandler;\n");
        builder.append("\n");
        builder.append("import java.util.AbstractMap;\n");
        builder.append("import java.util.ArrayList;\n");
        builder.append("import java.util.List;\n");
        builder.append("import java.util.Map;\n\n");

        builder.append("public class ").append(className).append(DECODER_POSTFIX).append(" implements ").append("InterfaceHandler.Decoder {\n\n");

        builder.append("    @Override\n");
        builder.append("    public boolean dispatchInterfacePacket(InterfacePacket packet, Object instance) {\n");
        builder.append("        ").append(className).append(" __").append(className).append(" = (").append(className).append(") instance;\n");
        builder.append("        ").append("String __method = packet.getMethod();\n");
        builder.append("        ").append("List<Map.Entry<String,Object>> __params = packet.getParams();\n");
        builder.append("        ");
        boolean hasMethod = false;
        for (Element element : interfaceElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.METHOD) {
                hasMethod = true;
                ExecutableElement e = (ExecutableElement) element;
                builder.append("if (\"").append(e.toString()).append("\".equals(__method)) {\n");
                builder.append("            ").append(invokeMethod(e, className));
                builder.append("        } else ");
            }
        }
        if (hasMethod) {
            builder.append("{\n            return false;\n        }\n");
        } else {
            builder.append("        return false;\n");
        }

        builder.append("        return true;\n");
        builder.append("    }\n");

        builder.append("}");
        writeContentToJavaFile(classPath + "." + className + DECODER_POSTFIX, builder.toString());
        return true;
    }

    private String invokeMethod(ExecutableElement element, String className) {
        StringBuilder builder = new StringBuilder();
        builder.append("__").append(className).append(".").append(element.getSimpleName()).append("(");
        List<? extends VariableElement> list = element.getParameters();
        int size = list.size();
        if (size > 0) {
            builder.append("(").append(list.get(0).asType().toString()).append(") ").append("__params.get(0).getValue()");
            for (int i = 1; i < size; i++) {
                builder.append(", (").append(list.get(i).asType().toString()).append(") ").append("__params.get(").append(i).append(").getValue()");
            }
        }
        builder.append(");\n");
        return builder.toString();
    }

    private boolean createEncoderSourceFile(TypeElement interfaceElement, String classPath, String className) {
        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(classPath).append(";\n\n");
        builder.append("import com.yxf.socketframe.packet.InterfacePacket;\n");
        builder.append("import com.yxf.socketframe.handler.InterfaceHandler;\n");
        builder.append("\n");
        builder.append("import java.util.AbstractMap;\n");
        builder.append("import java.util.ArrayList;\n");
        builder.append("import java.util.List;\n");
        builder.append("import java.util.Map;\n\n");

        builder.append("public class ").append(className).append(ENCODER_POSTFIX).append(" implements ").append(className).append(" , InterfaceHandler.Encoder {\n\n");

        builder.append("    private InterfaceHandler __mInterfaceHandler;\n");

        for (Element element : interfaceElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.METHOD) {
                if (!appendMethod(builder, (ExecutableElement) element, interfaceElement)) {
                    return false;
                }
            }
        }

        builder.append("    @Override\n");
        builder.append("    public void setInterfaceHandler(InterfaceHandler handler) {\n");
        builder.append("        __mInterfaceHandler = handler;");
        builder.append("    }\n");

        builder.append("}");
        writeContentToJavaFile(classPath + "." + className + ENCODER_POSTFIX, builder.toString());
        return true;
    }

    private boolean appendMethod(StringBuilder builder, ExecutableElement element, TypeElement interfaceElement) {
        if (element.getReturnType().getKind() != TypeKind.VOID) {
            messager.printMessage(Diagnostic.Kind.ERROR, "the return type of the method in interface @SocketInterface attached must be void , " +
                    "interface : " + interfaceElement.getQualifiedName() + " , method : " + element.getSimpleName());
            return false;
        }

        List<Map.Entry<String, String>> params = getParameters(element, interfaceElement);
        if (params == null) {
            return false;
        }

        builder.append("    @Override\n");
        builder.append("    public void ").append(element.getSimpleName()).append(getParametersString(params)).append(" {\n");
        builder.append("        List<Map.Entry<String,Object>> __params = new ArrayList<Map.Entry<String, Object>>();\n");
        for (Map.Entry<String, String> entry : params) {
            builder.append("        __params.add(new AbstractMap.SimpleEntry<String, Object>(\"").append(entry.getKey()).append("\", ").append(entry.getValue()).append("));\n");
        }
        builder.append("        InterfacePacket __packet = new InterfacePacket();\n");
        builder.append("        __packet.setMethod(\"").append(element.toString()).append("\");\n");
        builder.append("        __packet.setParams(__params);\n");
        builder.append("        __mInterfaceHandler.sendPacket(__packet);\n");
        builder.append("    }\n");

        return true;
    }

    private String getParametersString(List<Map.Entry<String, String>> list) {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        if (list.size() > 0) {
            builder.append(list.get(0).getKey()).append(" ").append(list.get(0).getValue());
        }
        for (int i = 1; i < list.size(); i++) {
            builder.append(", ").append(list.get(i).getKey()).append(" ").append(list.get(i).getValue());
        }
        builder.append(")");
        return builder.toString();
    }

    private List<Map.Entry<String, String>> getParameters(ExecutableElement element, TypeElement interfaceElement) {
        List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>();
        for (VariableElement v : element.getParameters()) {
            String type = v.asType().toString();
            if (v.asType().getKind() == TypeKind.DECLARED) {
                try {
                    Class cla = Class.forName(type);
                    if (!isTypeSupport(cla)) {
                        return null;
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    throw new RuntimeException("can not get the class from name , class : " + type);
                }
            }
            messager.printMessage(Diagnostic.Kind.NOTE, v.getSimpleName() + " , " + v.asType().toString());
            list.add(new AbstractMap.SimpleEntry<String, String>(type, v.getSimpleName().toString()));
        }
        return list;
    }

    private boolean isTypeSupport(Class c) {
        Class cla = c;
        while (true) {
            if (supportClassContains(cla)) {
                return true;
            } else {
                Class[] interfaces = cla.getInterfaces();
                if (interfaces != null) {
                    for (Class i : interfaces) {
                        if (supportClassContains(i)) {
                            return true;
                        }
                    }
                }
            }
            if (cla == Object.class.getClass() && cla.getSuperclass() == null) {
                break;
            }
            cla = cla.getSuperclass();
        }
        messager.printMessage(Diagnostic.Kind.ERROR, "unsupported class type : " + c.getCanonicalName());
        return false;
    }

    private boolean supportClassContains(Class c) {
        for (String support : supportClass) {
            if (support.contains(c.getCanonicalName())) {
                return true;
            }
        }
        return false;
    }

    private boolean writeContentToJavaFile(String path, String content) {
        BufferedWriter writer = null;
        try {
            JavaFileObject javaFileObject = processingEnv.getFiler().createSourceFile(path);
            writer = new BufferedWriter(javaFileObject.openWriter());
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<String>();
        annotations.add(TARGET_ANNOTATION);
        return annotations;
    }
}
