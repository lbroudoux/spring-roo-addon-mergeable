package com.github.lbroudoux.roo.addon.mergeable;

import static org.springframework.roo.model.JpaJavaType.ONE_TO_ONE;
import static org.springframework.roo.model.JpaJavaType.MANY_TO_ONE;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.roo.classpath.PhysicalTypeIdentifierNamingUtils;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.classpath.details.FieldMetadataBuilder;
import org.springframework.roo.classpath.details.MethodMetadata;
import org.springframework.roo.classpath.details.MethodMetadataBuilder;
import org.springframework.roo.classpath.details.annotations.AnnotatedJavaType;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.classpath.itd.AbstractItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.classpath.itd.InvocableMemberBodyBuilder;
import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.LogicalPath;

/**
 * This type produces metadata for a new ITD. It uses an {@link ItdTypeDetailsBuilder} provided by 
 * {@link AbstractItdTypeDetailsProvidingMetadataItem} to register a field in the ITD and a new method.
 * 
 * @since 1.1.0
 */
public class MergeableMetadata extends AbstractItdTypeDetailsProvidingMetadataItem {

   // Constants
   private static final String PROVIDES_TYPE_STRING = MergeableMetadata.class.getName();
   private static final String PROVIDES_TYPE = MetadataIdentificationUtils.create(PROVIDES_TYPE_STRING);

   private String entityName;
   private List<FieldMetadata> mergeableFields;
   
   
   public static final String getMetadataIdentiferType() {
      return PROVIDES_TYPE;
   }

   public static final String createIdentifier(JavaType javaType, LogicalPath path) {
      return PhysicalTypeIdentifierNamingUtils.createIdentifier(PROVIDES_TYPE_STRING, javaType, path);
   }

   public static final JavaType getJavaType(String metadataIdentificationString) {
      return PhysicalTypeIdentifierNamingUtils.getJavaType(PROVIDES_TYPE_STRING, metadataIdentificationString);
   }

   public static final LogicalPath getPath(String metadataIdentificationString) {
      return PhysicalTypeIdentifierNamingUtils.getPath(PROVIDES_TYPE_STRING, metadataIdentificationString);
   }

   public static boolean isValid(String metadataIdentificationString) {
      return PhysicalTypeIdentifierNamingUtils.isValid(PROVIDES_TYPE_STRING, metadataIdentificationString);
   }

   public MergeableMetadata(String identifier, JavaType aspectName, PhysicalTypeMetadata governorPhysicalTypeMetadata, List<FieldMetadata> mergeableFields) {
      super(identifier, aspectName, governorPhysicalTypeMetadata);
      Validate.isTrue(isValid(identifier), "Metadata identification string '" + identifier + "' does not appear to be a valid");

      // Initialize entity name.
      entityName = getJavaType(identifier).getSimpleTypeName();
      this.mergeableFields = mergeableFields;
      
      // Add merge related fields and methods.
      builder.addField(getMainReferenceField());
      builder.addField(getSecondReferenceField());
      builder.addField(getMergeResultField());

      // Adding a new sample method definition
      builder.addMethod(getMainReferenceAccessor());
      builder.addMethod(getSecondReferenceAccessor());
      builder.addMethod(getMergeResultAccessor());
      builder.addMethod(getWasMergedMethod());
      builder.addMethod(getIsMergeResultMethod());
      builder.addMethod(getMergeMethod());

      // Create a representation of the desired output ITD
      itdTypeDetails = builder.build();
   }

   /**
    * Create metadata for the mergeResult field definition. 
    * @return a FieldMetadata object
    */
   private FieldMetadata getMainReferenceField() {
      List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();
      annotations.add(new AnnotationMetadataBuilder(ONE_TO_ONE));
      
      // Using the FieldMetadataBuilder to create the field definition. 
      final FieldMetadataBuilder fieldBuilder = new FieldMetadataBuilder(getId(), // Metadata ID provided by supertype
            Modifier.PRIVATE, 
            annotations,
            new JavaSymbolName("mergeMainReference"), // Field name
            getJavaType(getId())); // Field type

      return fieldBuilder.build(); // Build and return a FieldMetadata instance
   }
   
   /**
    * Create metadata for the mergeResult field definition. 
    * @return a FieldMetadata object
    */
   private FieldMetadata getSecondReferenceField() {
      List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();
      annotations.add(new AnnotationMetadataBuilder(ONE_TO_ONE));
      
      // Using the FieldMetadataBuilder to create the field definition. 
      final FieldMetadataBuilder fieldBuilder = new FieldMetadataBuilder(getId(), // Metadata ID provided by supertype
            Modifier.PRIVATE, 
            annotations,
            new JavaSymbolName("mergeSecondReference"), // Field name
            getJavaType(getId())); // Field type

      return fieldBuilder.build(); // Build and return a FieldMetadata instance
   }
   
   /**
    * Create metadata for the mergeResult field definition. 
    * @return a FieldMetadata object
    */
   private FieldMetadata getMergeResultField() {
      List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();
      annotations.add(new AnnotationMetadataBuilder(MANY_TO_ONE));

      // Using the FieldMetadataBuilder to create the field definition. 
      final FieldMetadataBuilder fieldBuilder = new FieldMetadataBuilder(getId(), // Metadata ID provided by supertype
            Modifier.PRIVATE, 
            annotations,
            new JavaSymbolName("mergeResult"), // Field name
            getJavaType(getId())); // Field type

      return fieldBuilder.build(); // Build and return a FieldMetadata instance
   }

   private MethodMetadata getMainReferenceAccessor() {
      // Specify the desired method name
      JavaSymbolName methodName = new JavaSymbolName("getMergeMainReference");
      
      // Check if a method with the same signature already exists in the target type
      MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
      if (method != null) {
         return method;
      }
      
      // Define method parameter types (none in this case)
      List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
      
      // Define method parameter names (none in this case)
      List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
      
      // Create the method body
      InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
      bodyBuilder.appendFormalLine("return this.mergeMainReference;");
      
      // Use the MethodMetadataBuilder for easy creation of MethodMetadata
      MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, 
            destination, parameterTypes, parameterNames, bodyBuilder);
      
      return methodBuilder.build(); // Build and return a MethodMetadata instance
   }
   
   private MethodMetadata getSecondReferenceAccessor() {
      // Specify the desired method name
      JavaSymbolName methodName = new JavaSymbolName("getMergeSecondReference");
      
      // Check if a method with the same signature already exists in the target type
      MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
      if (method != null) {
         return method;
      }
      
      // Define method parameter types (none in this case)
      List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
      
      // Define method parameter names (none in this case)
      List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
      
      // Create the method body
      InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
      bodyBuilder.appendFormalLine("return this.mergeSecondReference;");
      
      // Use the MethodMetadataBuilder for easy creation of MethodMetadata
      MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, 
            destination, parameterTypes, parameterNames, bodyBuilder);
      
      return methodBuilder.build(); // Build and return a MethodMetadata instance
   }
   
   private MethodMetadata getMergeResultAccessor() {
      // Specify the desired method name
      JavaSymbolName methodName = new JavaSymbolName("getMergeResult");
      
      // Check if a method with the same signature already exists in the target type
      MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
      if (method != null) {
         return method;
      }
      
      // Define method parameter types (none in this case)
      List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
      
      // Define method parameter names (none in this case)
      List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
      
      // Create the method body
      InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
      bodyBuilder.appendFormalLine("return this.mergeResult;");
      
      // Use the MethodMetadataBuilder for easy creation of MethodMetadata
      MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, 
            destination, parameterTypes, parameterNames, bodyBuilder);
      
      return methodBuilder.build(); // Build and return a MethodMetadata instance
   }
   
   private MethodMetadata getMergeMethod() {
      // Specify the desired method name
      JavaSymbolName methodName = new JavaSymbolName("merge");

      // Check if a method with the same signature already exists in the target type
      final MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
      if (method != null) {
         // If it already exists, just return the method and omit its generation via the ITD
         return method;
      }

      // Define method parameter types
      List<JavaType> parameterTypes = Arrays.asList(getJavaType(getId()));
      
      // Define method parameter names
      List<JavaSymbolName> parameterNames = Arrays.asList(new JavaSymbolName("second"));

      // Create the method body
      InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
      bodyBuilder.appendFormalLine(entityName + " merge = new " + entityName + "();");
      bodyBuilder.appendFormalLine("merge.mergeMainReference = this;");
      bodyBuilder.appendFormalLine("merge.mergeSecondReference = second;");
      for (final FieldMetadata field : mergeableFields){
         bodyBuilder.appendFormalLine("if (this." + field.getFieldName() + " != null){");
         bodyBuilder.appendFormalLine("   merge." + field.getFieldName() + " = this." + field.getFieldName() + ";");
         bodyBuilder.appendFormalLine("} else if (second." + field.getFieldName() + " != null){");
         bodyBuilder.appendFormalLine("   merge." + field.getFieldName() + " = second." + field.getFieldName() + ";");
         bodyBuilder.appendFormalLine("}");
      }
      bodyBuilder.appendFormalLine("merge.persist();");
      bodyBuilder.appendFormalLine("this.mergeResult = merge;");
      bodyBuilder.appendFormalLine("this.merge();");
      bodyBuilder.appendFormalLine("second.mergeResult = merge;");
      bodyBuilder.appendFormalLine("second.merge();");
      bodyBuilder.appendFormalLine("return merge;");

      // Use the MethodMetadataBuilder for easy creation of MethodMetadata
      MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, 
            destination, AnnotatedJavaType.convertFromJavaTypes(parameterTypes), parameterNames, bodyBuilder);

      return methodBuilder.build(); // Build and return a MethodMetadata instance
   }
   
   private MethodMetadata getWasMergedMethod() {
      // Specify the desired method name
      JavaSymbolName methodName = new JavaSymbolName("wasMerged");
      
      // Check if a method with the same signature already exists in the target type
      MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
      if (method != null) {
         return method;
      }
      
      // Define method parameter types (none in this case)
      List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
      
      // Define method parameter names (none in this case)
      List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
      
      // Create the method body
      InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
      bodyBuilder.appendFormalLine("return (this.mergeResult != null);");
      
      // Use the MethodMetadataBuilder for easy creation of MethodMetadata
      MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, 
            new JavaType(boolean.class), parameterTypes, parameterNames, bodyBuilder);
      
      return methodBuilder.build(); // Build and return a MethodMetadata instance
   }
   
   private MethodMetadata getIsMergeResultMethod() {
      // Specify the desired method name
      JavaSymbolName methodName = new JavaSymbolName("isMergeResult");
      
      // Check if a method with the same signature already exists in the target type
      MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
      if (method != null) {
         return method;
      }
      
      // Define method parameter types (none in this case)
      List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
      
      // Define method parameter names (none in this case)
      List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
      
      // Create the method body
      InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
      bodyBuilder.appendFormalLine("return (this.mergeMainReference != null);");
      
      // Use the MethodMetadataBuilder for easy creation of MethodMetadata
      MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, 
            new JavaType(boolean.class), parameterTypes, parameterNames, bodyBuilder);
      
      return methodBuilder.build(); // Build and return a MethodMetadata instance
   }

   private MethodMetadata methodExists(JavaSymbolName methodName, List<AnnotatedJavaType> paramTypes) {
      // We have no access to method parameter information, so we scan by name alone and treat any match as authoritative
      // We do not scan the superclass, as the caller is expected to know we'll only scan the current class
      for (MethodMetadata method : governorTypeDetails.getDeclaredMethods()) {
         if (method.getMethodName().equals(methodName) && method.getParameterTypes().equals(paramTypes)) {
            // Found a method of the expected name; we won't check method parameters though
            return method;
         }
      }
      return null;
   }

   // Typically, no changes are required beyond this point

   public String toString() {
      final ToStringBuilder builder = new ToStringBuilder(this);
      builder.append("identifier", getId());
      builder.append("valid", valid);
      builder.append("aspectName", aspectName);
      builder.append("destinationType", destination);
      builder.append("governor", governorPhysicalTypeMetadata.getId());
      builder.append("itdTypeDetails", itdTypeDetails);
      return builder.toString();
   }
}
