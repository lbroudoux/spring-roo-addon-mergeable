package com.github.lbroudoux.roo.addon.mergeable;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.osgi.service.component.ComponentContext;
import org.springframework.roo.classpath.PhysicalTypeIdentifier;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.classpath.itd.AbstractItdMetadataProvider;
import org.springframework.roo.classpath.itd.ItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.classpath.scanner.MemberDetails;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.LogicalPath;

/**
 * Provides {@link MergeableMetadata}. This type is called by Roo to retrieve the metadata for this add-on.
 * Use this type to reference external types and services needed by the metadata type. Register metadata triggers and
 * dependencies here. Also define the unique add-on ITD identifier.
 * 
 * @since 1.1
 */
@Component
@Service
public final class MergeableMetadataProvider extends AbstractItdMetadataProvider {

    /**
     * The activate method for this OSGi component, this will be called by the OSGi container upon bundle activation 
     * (result of the 'addon install' command) 
     * 
     * @param context the component context can be used to get access to the OSGi container (ie find out if certain bundles are active)
     */
    protected void activate(ComponentContext context) {
        metadataDependencyRegistry.registerDependency(PhysicalTypeIdentifier.getMetadataIdentiferType(), getProvidesType());
        addMetadataTrigger(new JavaType(RooMergeable.class.getName()));
    }
    
    /**
     * The deactivate method for this OSGi component, this will be called by the OSGi container upon bundle deactivation 
     * (result of the 'addon uninstall' command) 
     * 
     * @param context the component context can be used to get access to the OSGi container (ie find out if certain bundles are active)
     */
    protected void deactivate(ComponentContext context) {
        metadataDependencyRegistry.deregisterDependency(PhysicalTypeIdentifier.getMetadataIdentiferType(), getProvidesType());
        removeMetadataTrigger(new JavaType(RooMergeable.class.getName()));    
    }
    
    /**
     * Return an instance of the Metadata offered by this add-on
     */
    protected ItdTypeDetailsProvidingMetadataItem getMetadata(String metadataIdentificationString, JavaType aspectName, PhysicalTypeMetadata governorPhysicalTypeMetadata, String itdFilename) {
       // Retrieve javaType, id and version fields.
       final JavaType javaType = governorPhysicalTypeMetadata.getMemberHoldingTypeDetails().getName();
       final List<FieldMetadata> idFields = persistenceMemberLocator.getIdentifierFields(javaType);
       List<String> idFieldsName = new ArrayList<String>();
       for (FieldMetadata idField : idFields){
           idFieldsName.add(idField.getFieldName().toString());
       }
       final FieldMetadata versionField = persistenceMemberLocator.getVersionField(javaType);
      
       MemberDetails memberDetails = getMemberDetails(governorPhysicalTypeMetadata);
       List<FieldMetadata> mergeableFields = new ArrayList<FieldMetadata>();
      
       for (final FieldMetadata field : memberDetails.getFields()){ 
          if (Modifier.isStatic(field.getModifier()) ||
               Modifier.isTransient(field.getModifier()) ||
                   field.getFieldType().isCommonCollectionType() || 
                       field.getFieldType().isArray()){ 
             continue; 
          }
          if (idFields != null && idFieldsName.contains(field.getFieldName().toString())){
             continue;
          }
          if (versionField != null && field.getFieldName().equals(versionField.getFieldName())){ 
             continue; 
          }
         
          // Also exclude the references to originals object in case of merging a merge result.
          if (!"mergeMainReference".equals(field.getFieldName().toString())
                && !"mergeSecondReference".equals(field.getFieldName().toString())){ 
              mergeableFields.add(field);
          }
       }
       
       // Pass dependencies required by the metadata in through its constructor
       return new MergeableMetadata(metadataIdentificationString, aspectName, governorPhysicalTypeMetadata, mergeableFields);
    }
    
    /**
     * Define the unique ITD file name extension, here the resulting file name will be **_ROO_Mergeable.aj
     */
    public String getItdUniquenessFilenameSuffix() {
        return "Mergeable";
    }

    protected String getGovernorPhysicalTypeIdentifier(String metadataIdentificationString) {
        JavaType javaType = MergeableMetadata.getJavaType(metadataIdentificationString);
        LogicalPath path = MergeableMetadata.getPath(metadataIdentificationString);
        return PhysicalTypeIdentifier.createIdentifier(javaType, path);
    }
    
    protected String createLocalIdentifier(JavaType javaType, LogicalPath path) {
        return MergeableMetadata.createIdentifier(javaType, path);
    }

    public String getProvidesType() {
        return MergeableMetadata.getMetadataIdentiferType();
    }
}