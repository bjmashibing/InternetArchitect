/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dubbo.admin.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.admin.model.domain.MethodMetadata;
import org.apache.dubbo.metadata.definition.model.FullServiceDefinition;
import org.apache.dubbo.metadata.definition.model.MethodDefinition;
import org.apache.dubbo.metadata.definition.model.ServiceDefinition;
import org.apache.dubbo.metadata.definition.model.TypeDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceTestUtil {
    private static Pattern COLLECTION_PATTERN = Pattern.compile("^java\\.util\\..*(Set|List|Queue|Collection|Deque)(<.*>)*$");
    private static Pattern MAP_PATTERN = Pattern.compile("^java\\.util\\..*Map.*(<.*>)*$");

    public static boolean sameMethod(MethodDefinition m, String methodSig) {
        String name = m.getName();
        String[] parameters = m.getParameterTypes();
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("~");
        for (String parameter : parameters) {
            sb.append(parameter).append(";");
        }
        String sig = StringUtils.removeEnd(sb.toString(), ";");
        return sig.equals(methodSig);
    }

    public static MethodMetadata generateMethodMeta(FullServiceDefinition serviceDefinition, MethodDefinition methodDefinition) {
        MethodMetadata methodMetadata = new MethodMetadata();
        String[] parameterTypes = methodDefinition.getParameterTypes();
        String returnType = methodDefinition.getReturnType();
        String signature = methodDefinition.getName() + "~" + String.join(";", parameterTypes);
        methodMetadata.setSignature(signature);
        methodMetadata.setReturnType(returnType);
        List<Object> parameters = generateParameterTypes(parameterTypes, serviceDefinition);
        methodMetadata.setParameterTypes(parameters);
        return methodMetadata;
    }

    private static boolean isPrimitiveType(TypeDefinition td) {
        String type = td.getType();
        return type.equals("byte") || type.equals("java.lang.Byte") ||
                type.equals("short") || type.equals("java.lang.Short") ||
                type.equals("int") || type.equals("java.lang.Integer") ||
                type.equals("long") || type.equals("java.lang.Long") ||
                type.equals("float") || type.equals("java.lang.Float") ||
                type.equals("double") || type.equals("java.lang.Double") ||
                type.equals("boolean") || type.equals("java.lang.Boolean") ||
                type.equals("void") || type.equals("java.lang.Void") ||
                type.equals("java.lang.String") ||
                type.equals("java.util.Date") ||
                type.equals("java.lang.Object");
    }

    private static List<Object> generateParameterTypes(String[] parameterTypes, ServiceDefinition serviceDefinition) {
        List<Object> parameters = new ArrayList<>();
        for (String type : parameterTypes) {
            Object result = generateType(serviceDefinition, type);
            parameters.add(result);
        }
        return parameters;
    }

    private static TypeDefinition findTypeDefinition(ServiceDefinition serviceDefinition, String type) {
        return serviceDefinition.getTypes().stream()
                .filter(t -> t.getType().equals(type))
                .findFirst().orElse(new TypeDefinition(type));
    }

    private static void generateComplexType(ServiceDefinition sd, TypeDefinition td, Map<String, Object> holder) {
        for (Map.Entry<String, TypeDefinition> entry : td.getProperties().entrySet()) {
            if (isPrimitiveType(td)) {
                holder.put(entry.getKey(), generatePrimitiveType(td));
            } else {
                generateEnclosedType(holder, entry.getKey(), sd, entry.getValue());
            }
        }
    }
    private static Object generateComplexType(ServiceDefinition sd, TypeDefinition td) {
        Map<String, Object> holder = new HashMap<>();
        generateComplexType(sd, td, holder);
        return holder;
    }

    private static boolean isMap(TypeDefinition td) {
        String type = StringUtils.substringBefore(td.getType(), "<");
        Matcher matcher = MAP_PATTERN.matcher(type);
        return matcher.matches();
    }

    private static boolean isCollection(TypeDefinition td) {
        String type = StringUtils.substringBefore(td.getType(), "<");
        Matcher matcher = COLLECTION_PATTERN.matcher(type);
        return matcher.matches();
    }

    private static boolean isArray(TypeDefinition td) {
        return StringUtils.endsWith(td.getType(), "[]");
    }

    private static Object generatePrimitiveType(TypeDefinition td) {
        String type = td.getType();
        switch (type) {
            case "byte":
            case "java.lang.Byte":
            case "short":
            case "java.lang.Short":
            case "int":
            case "java.lang.Integer":
            case "long":
            case "java.lang.Long":
                return 0;
            case "float":
            case "java.lang.Float":
            case "double":
            case "java.lang.Double":
                return 0.0;
            case "boolean":
            case "java.lang.Boolean":
                return true;
            case "void":
            case "java.lang.Void":
                return null;
            case "java.lang.String":
                return "";
            case "java.lang.Object":
                return Collections.emptyMap();
            case "java.util.Date":
                return System.currentTimeMillis();
            default:
                return Collections.emptyMap();
        }
    }

    private static Object generateType(ServiceDefinition sd, String type) {
        TypeDefinition td = findTypeDefinition(sd, type);
        return generateType(sd, td);
    }

    private static Object generateType(ServiceDefinition sd, TypeDefinition td) {
        if (isPrimitiveType(td)) {
            return generatePrimitiveType(td);
        } else if (isMap(td)) {
            return generateMapType(sd, td);
        } else if (isArray(td)) {
            return generateArrayType(sd, td);
        } else if (isCollection(td)) {
            return generateCollectionType(sd, td);
        } else {
            return generateComplexType(sd, td);
        }
    }

    private static Object generateMapType(ServiceDefinition sd, TypeDefinition td) {
        String keyType = StringUtils.substringAfter(td.getType(), "<");
        keyType = StringUtils.substringBefore(keyType, ",");
        keyType = StringUtils.strip(keyType);

        Map<Object, Object> map = new HashMap<>();
        // 生成 key 默认值
        Object key = generateType(sd, keyType);

        // 生成 value 默认值
        String valueType = StringUtils.substringAfter(td.getType(), ",");
        valueType = StringUtils.substringBefore(valueType, ">");
        valueType = StringUtils.strip(valueType);
        valueType = StringUtils.isNotEmpty(valueType) ? valueType : "java.lang.Object";
        Object value = generateType(sd, valueType);
        map.put(key, value);
        return map;
    }

    private static Object generateCollectionType(ServiceDefinition sd, TypeDefinition td) {
        String type = StringUtils.substringAfter(td.getType(), "<");
        type = StringUtils.substringBefore(type, ">");
        if (StringUtils.isEmpty(type)) {
            // 如果 collection 类型未声明，则生成空 collection
            return new Object[] {};
        }
        return new Object[]{generateType(sd, type)};

    }
    private static Object generateArrayType(ServiceDefinition sd, TypeDefinition td) {
        String type = StringUtils.substringBeforeLast(td.getType(), "[]");
        return new Object[]{generateType(sd, type)};
    }

    private static void generateEnclosedType(Map<String, Object> holder, String key, ServiceDefinition sd, TypeDefinition td) {
        if (td.getProperties() == null || td.getProperties().size() == 0 || isPrimitiveType(td)) {
            holder.put(key, generateType(sd, td));
        } else {
            Map<String, Object> enclosedMap = new HashMap<>();
            holder.put(key, enclosedMap);
            generateComplexType(sd, td, enclosedMap);
        }
    }
}
