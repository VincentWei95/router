package com.example.router.util;

import com.example.router.enums.TypeKind;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

public class ElementTypeExchanger {
    private static final String LANG = "java.lang";
    private static final String BYTE = LANG + ".Byte";
    private static final String SHORT = LANG + ".Short";
    private static final String INTEGER = LANG + ".Integer";
    private static final String LONG = LANG + ".Long";
    private static final String FLOAT = LANG + ".Float";
    private static final String DOUBLE = LANG + ".Double";
    private static final String BOOLEAN = LANG + ".Boolean";
    private static final String CHAR = LANG + ".Character";
    private static final String STRING = LANG + ".String";
    private static final String SERIALIZABLE = "java.io.Serializable";
    private static final String PARCELABLE = "android.os.Parcelable";

    private final Types types;
    private final TypeMirror parcelableType;
    private final TypeMirror serializableType;

    public ElementTypeExchanger(Types types, Elements elements) {
        this.types = types;

        parcelableType = elements.getTypeElement(PARCELABLE).asType();
        serializableType = elements.getTypeElement(SERIALIZABLE).asType();
    }

    public int exchange(Element element) {
        TypeMirror typeMirror = element.asType();

        if (typeMirror.getKind().isPrimitive()) {
            return element.asType().getKind().ordinal();
        }

        String type = typeMirror.toString();
        switch (type) {
            case BYTE:
                return TypeKind.BYTE.ordinal();
            case SHORT:
                return TypeKind.SHORT.ordinal();
            case INTEGER:
                return TypeKind.INT.ordinal();
            case LONG:
                return TypeKind.LONG.ordinal();
            case FLOAT:
                return TypeKind.FLOAT.ordinal();
            case DOUBLE:
                return TypeKind.DOUBLE.ordinal();
            case BOOLEAN:
                return TypeKind.BOOLEAN.ordinal();
            case CHAR:
                return TypeKind.CHAR.ordinal();
            case STRING:
                return TypeKind.STRING.ordinal();
            default:
                if (types.isSubtype(typeMirror, parcelableType)) {
                    return TypeKind.PARCELABLE.ordinal();
                } else if (types.isSubtype(typeMirror, serializableType)) {
                    return TypeKind.SERIALIZABLE.ordinal();
                }
        }

        throw new IllegalArgumentException(type + " not found");
    }
}
