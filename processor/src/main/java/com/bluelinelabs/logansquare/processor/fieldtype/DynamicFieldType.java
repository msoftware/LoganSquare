package com.bluelinelabs.logansquare.processor.fieldtype;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.processor.JsonFieldHolder;
import com.squareup.javapoet.MethodSpec.Builder;
import com.squareup.javapoet.TypeName;

import static com.bluelinelabs.logansquare.processor.ObjectMapperInjector.JSON_GENERATOR_VARIABLE_NAME;
import static com.bluelinelabs.logansquare.processor.ObjectMapperInjector.JSON_PARSER_VARIABLE_NAME;

public class DynamicFieldType extends FieldType {

    private TypeName mTypeName;

    public DynamicFieldType(TypeName typeName) {
        mTypeName = typeName;
    }

    @Override
    public TypeName getTypeName() {
        return mTypeName;
    }

    @Override
    public String getJsonParserGetter(JsonFieldHolder fieldHolder) {
        return String.format("LoganSquare.typeConverterFor(%s.class).parse(%s)", mTypeName.toString(), JSON_PARSER_VARIABLE_NAME);
    }

    @Override
    public void serialize(Builder builder, JsonFieldHolder fieldHolder, String variableName, String getter, boolean writeFieldNameForObject) {
        if (!fieldHolder.fieldType.getTypeName().isPrimitive()) {
            builder.beginControlFlow("if (object.$L != null)", variableName);
        }

        if (writeFieldNameForObject) {
            builder.addStatement("$L.writeFieldName($S)", JSON_GENERATOR_VARIABLE_NAME, fieldHolder.fieldName[0]);
        }

        builder.addStatement("$T.typeConverterFor($T.class).serialize($L, $S, $L)", LoganSquare.class, mTypeName, getter, fieldHolder.fieldName[0], JSON_GENERATOR_VARIABLE_NAME);

        if (!fieldHolder.fieldType.getTypeName().isPrimitive()) {
            builder.endControlFlow();
        }
    }
}
