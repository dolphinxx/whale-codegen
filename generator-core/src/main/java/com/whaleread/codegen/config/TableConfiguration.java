/*
 * Copyright 2006-2017 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.whaleread.codegen.config;

import com.whaleread.codegen.api.dom.xml.Attribute;
import com.whaleread.codegen.api.dom.xml.XmlElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.whaleread.codegen.internal.util.EqualsUtil.areEqual;
import static com.whaleread.codegen.internal.util.HashCodeUtil.SEED;
import static com.whaleread.codegen.internal.util.HashCodeUtil.hash;
import static com.whaleread.codegen.internal.util.StringUtility.composeFullyQualifiedTableName;
import static com.whaleread.codegen.internal.util.StringUtility.stringHasValue;
import static com.whaleread.codegen.internal.util.messages.Messages.getString;

/**
 * The Class TableConfiguration.
 *
 * @author Jeff Butler
 */
public class TableConfiguration extends PropertyHolder {
    private boolean enableModel;

    private boolean enableDTO;

    private boolean enableDAO;

    private boolean enableService;

    private boolean enableInsert;

    private boolean enableInsertSelective;

    private boolean enableSelectByPrimaryKey;

    private boolean enableCountByCriteria;
    private boolean enableSelectByCriteria;

    private boolean enableUpdateByPrimaryKey;

    private boolean enableUpdateByPrimaryKeySelective;

    private boolean enableDeleteByPrimaryKey;

    private boolean enableDeleteByCriteria;

    private List<ColumnOverride> columnOverrides;

    private Map<IgnoredColumn, Boolean> ignoredColumns;

    private GeneratedKey generatedKey;

    private String catalog;

    private String schema;

    private String tableName;

    private String javaTypeName;

    private String alias;

    private boolean wildcardEscapingEnabled;

    private boolean delimitIdentifiers;

    private DomainObjectRenamingRule domainObjectRenamingRule;

    private ColumnRenamingRule columnRenamingRule;

    private boolean isAllColumnDelimitingEnabled;

    private String mapperName;
    private String sqlProviderName;

    private List<IgnoredColumnPattern> ignoredColumnPatterns = new ArrayList<>();

    public TableConfiguration(Context context) {
        super();

        columnOverrides = new ArrayList<>();
        ignoredColumns = new HashMap<>();
    }

    public boolean isColumnIgnored(String columnName) {
        for (Map.Entry<IgnoredColumn, Boolean> entry : ignoredColumns
                .entrySet()) {
            if (entry.getKey().matches(columnName)) {
                entry.setValue(Boolean.TRUE);
                return true;
            }
        }

        for (IgnoredColumnPattern ignoredColumnPattern : ignoredColumnPatterns) {
            if (ignoredColumnPattern.matches(columnName)) {
                return true;
            }
        }

        return false;
    }

    public void addIgnoredColumn(IgnoredColumn ignoredColumn) {
        ignoredColumns.put(ignoredColumn, Boolean.FALSE);
    }

    public void addIgnoredColumnPattern(IgnoredColumnPattern ignoredColumnPattern) {
        ignoredColumnPatterns.add(ignoredColumnPattern);
    }

    public void addColumnOverride(ColumnOverride columnOverride) {
        columnOverrides.add(columnOverride);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof TableConfiguration)) {
            return false;
        }

        TableConfiguration other = (TableConfiguration) obj;

        return areEqual(this.catalog, other.catalog)
                && areEqual(this.schema, other.schema)
                && areEqual(this.tableName, other.tableName);
    }

    @Override
    public int hashCode() {
        int result = SEED;
        result = hash(result, catalog);
        result = hash(result, schema);
        result = hash(result, tableName);

        return result;
    }

    /**
     * May return null if the column has not been overridden.
     *
     * @param columnName the column name
     * @return the column override (if any) related to this column
     */
    public ColumnOverride getColumnOverride(String columnName) {
        for (ColumnOverride co : columnOverrides) {
            if (co.isColumnNameDelimited()) {
                if (columnName.equals(co.getColumnName())) {
                    return co;
                }
            } else {
                if (columnName.equalsIgnoreCase(co.getColumnName())) {
                    return co;
                }
            }
        }

        return null;
    }

    public GeneratedKey getGeneratedKey() {
        return generatedKey;
    }

//    public boolean areAnyStatementsEnabled() {
//        return selectByExampleStatementEnabled
//                || enableSelectByPrimaryKey || enableInsert
//                || enableUpdateByPrimaryKey
//                || deleteByExampleStatementEnabled
//                || enableDeleteByPrimaryKey
//                || countByExampleStatementEnabled
//                || updateByExampleStatementEnabled
//                || updateByCriteriaStatementEnabled
//                || enableDeleteByCriteria
//                || enableInsertSelective
//                || enableUpdateByPrimaryKeySelective;
//    }

    public void setGeneratedKey(GeneratedKey generatedKey) {
        this.generatedKey = generatedKey;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getJavaTypeName() {
        return javaTypeName;
    }

    public void setJavaTypeName(String javaTypeName) {
        this.javaTypeName = javaTypeName;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnOverride> getColumnOverrides() {
        return columnOverrides;
    }

    /**
     * Returns a List of Strings. The values are the columns
     * that were specified to be ignored in the table, but do not exist in the
     * table.
     *
     * @return a List of Strings - the columns that were improperly configured
     * as ignored columns
     */
    public List<String> getIgnoredColumnsInError() {
        List<String> answer = new ArrayList<>();

        for (Map.Entry<IgnoredColumn, Boolean> entry : ignoredColumns
                .entrySet()) {
            if (Boolean.FALSE.equals(entry.getValue())) {
                answer.add(entry.getKey().getColumnName());
            }
        }

        return answer;
    }

    public XmlElement toXmlElement() {
        XmlElement xmlElement = new XmlElement("table"); //$NON-NLS-1$
        xmlElement.addAttribute(new Attribute("tableName", tableName)); //$NON-NLS-1$

        if (stringHasValue(catalog)) {
            xmlElement.addAttribute(new Attribute("catalog", catalog)); //$NON-NLS-1$
        }

        if (stringHasValue(schema)) {
            xmlElement.addAttribute(new Attribute("schema", schema)); //$NON-NLS-1$
        }

        if (stringHasValue(alias)) {
            xmlElement.addAttribute(new Attribute("alias", alias)); //$NON-NLS-1$
        }

        if (stringHasValue(javaTypeName)) {
            xmlElement.addAttribute(new Attribute(
                    "javaTypeName", javaTypeName)); //$NON-NLS-1$
        }

//        if (!enableInsert) {
//            xmlElement.addAttribute(new Attribute("enableInsert", "false")); //$NON-NLS-1$ //$NON-NLS-2$
//        }
//
//        if (!enableSelectByPrimaryKey) {
//            xmlElement.addAttribute(new Attribute(
//                    "enableSelectByPrimaryKey", "false")); //$NON-NLS-1$ //$NON-NLS-2$
//        }

//        if (!selectByExampleStatementEnabled) {
//            xmlElement.addAttribute(new Attribute(
//                    "enableSelectByExample", "false")); //$NON-NLS-1$ //$NON-NLS-2$
//        }

//        if (!enableUpdateByPrimaryKey) {
//            xmlElement.addAttribute(new Attribute(
//                    "enableUpdateByPrimaryKey", "false")); //$NON-NLS-1$ //$NON-NLS-2$
//        }
//
//        if (!enableDeleteByPrimaryKey) {
//            xmlElement.addAttribute(new Attribute(
//                    "enableDeleteByPrimaryKey", "false")); //$NON-NLS-1$ //$NON-NLS-2$
//        }

//        if (!deleteByExampleStatementEnabled) {
//            xmlElement.addAttribute(new Attribute(
//                    "enableDeleteByExample", "false")); //$NON-NLS-1$ //$NON-NLS-2$
//        }
//
//        if (!countByExampleStatementEnabled) {
//            xmlElement.addAttribute(new Attribute(
//                    "enableCountByExample", "false")); //$NON-NLS-1$ //$NON-NLS-2$
//        }
//
//        if (!updateByExampleStatementEnabled) {
//            xmlElement.addAttribute(new Attribute(
//                    "enableUpdateByExample", "false")); //$NON-NLS-1$ //$NON-NLS-2$
//        }

//        if (stringHasValue(selectByPrimaryKeyQueryId)) {
//            xmlElement.addAttribute(new Attribute(
//                    "selectByPrimaryKeyQueryId", selectByPrimaryKeyQueryId)); //$NON-NLS-1$
//        }
//
//        if (stringHasValue(selectByExampleQueryId)) {
//            xmlElement.addAttribute(new Attribute(
//                    "selectByExampleQueryId", selectByExampleQueryId)); //$NON-NLS-1$
//        }

        if (wildcardEscapingEnabled) {
            xmlElement.addAttribute(new Attribute("escapeWildcards", "true")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (isAllColumnDelimitingEnabled) {
            xmlElement.addAttribute(new Attribute("delimitAllColumns", "true")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (delimitIdentifiers) {
            xmlElement
                    .addAttribute(new Attribute("delimitIdentifiers", "true")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (stringHasValue(mapperName)) {
            xmlElement.addAttribute(new Attribute(
                    "mapperName", mapperName)); //$NON-NLS-1$
        }

        if (stringHasValue(sqlProviderName)) {
            xmlElement.addAttribute(new Attribute(
                    "sqlProviderName", sqlProviderName)); //$NON-NLS-1$
        }

        addPropertyXmlElements(xmlElement);

        if (generatedKey != null) {
            xmlElement.addElement(generatedKey.toXmlElement());
        }

        if (domainObjectRenamingRule != null) {
            xmlElement.addElement(domainObjectRenamingRule.toXmlElement());
        }

        if (columnRenamingRule != null) {
            xmlElement.addElement(columnRenamingRule.toXmlElement());
        }

        if (ignoredColumns.size() > 0) {
            for (IgnoredColumn ignoredColumn : ignoredColumns.keySet()) {
                xmlElement.addElement(ignoredColumn.toXmlElement());
            }
        }

        for (IgnoredColumnPattern ignoredColumnPattern : ignoredColumnPatterns) {
            xmlElement.addElement(ignoredColumnPattern.toXmlElement());
        }

        if (columnOverrides.size() > 0) {
            for (ColumnOverride columnOverride : columnOverrides) {
                xmlElement.addElement(columnOverride.toXmlElement());
            }
        }

        return xmlElement;
    }

    @Override
    public String toString() {
        return composeFullyQualifiedTableName(catalog, schema,
                tableName, '.');
    }

    public void validate(List<String> errors, int listPosition) {
        if (!stringHasValue(tableName)) {
            errors.add(getString(
                    "ValidationError.6", Integer.toString(listPosition))); //$NON-NLS-1$
        }

        String fqTableName = composeFullyQualifiedTableName(
                catalog, schema, tableName, '.');

        if (generatedKey != null) {
            generatedKey.validate(errors, fqTableName);
        }

//        // when using column indexes, either both or neither query ids
//        // should be set
//        if (isTrue(getProperty(PropertyRegistry.TABLE_USE_COLUMN_INDEXES))
//                && selectByExampleStatementEnabled
//                && enableSelectByPrimaryKey) {
//            boolean queryId1Set = stringHasValue(selectByExampleQueryId);
//            boolean queryId2Set = stringHasValue(selectByPrimaryKeyQueryId);
//
//            if (queryId1Set != queryId2Set) {
//                errors.add(getString("ValidationError.13", //$NON-NLS-1$
//                        fqTableName));
//            }
//        }

        if (domainObjectRenamingRule != null) {
            domainObjectRenamingRule.validate(errors, fqTableName);
        }

        if (columnRenamingRule != null) {
            columnRenamingRule.validate(errors, fqTableName);
        }

        for (ColumnOverride columnOverride : columnOverrides) {
            columnOverride.validate(errors, fqTableName);
        }

        for (IgnoredColumn ignoredColumn : ignoredColumns.keySet()) {
            ignoredColumn.validate(errors, fqTableName);
        }

        for (IgnoredColumnPattern ignoredColumnPattern : ignoredColumnPatterns) {
            ignoredColumnPattern.validate(errors, fqTableName);
        }
    }

    public boolean isEnableModel() {
        return enableModel;
    }

    public void setEnableModel(boolean enableModel) {
        this.enableModel = enableModel;
    }

    public boolean isEnableDTO() {
        return enableDTO;
    }

    public void setEnableDTO(boolean enableDTO) {
        this.enableDTO = enableDTO;
    }

    public boolean isEnableDAO() {
        return enableDAO;
    }

    public void setEnableDAO(boolean enableDAO) {
        this.enableDAO = enableDAO;
    }

    public boolean isEnableService() {
        return enableService;
    }

    public void setEnableService(boolean enableService) {
        this.enableService = enableService;
    }

    public boolean isEnableInsert() {
        return enableInsert;
    }

    public void setEnableInsert(boolean enableInsert) {
        this.enableInsert = enableInsert;
    }

    public boolean isEnableInsertSelective() {
        return enableInsertSelective;
    }

    public void setEnableInsertSelective(boolean enableInsertSelective) {
        this.enableInsertSelective = enableInsertSelective;
    }

    public boolean isEnableSelectByPrimaryKey() {
        return enableSelectByPrimaryKey;
    }

    public void setEnableSelectByPrimaryKey(boolean enableSelectByPrimaryKey) {
        this.enableSelectByPrimaryKey = enableSelectByPrimaryKey;
    }

    public boolean isEnableCountByCriteria() {
        return enableCountByCriteria;
    }

    public void setEnableCountByCriteria(boolean enableCountByCriteria) {
        this.enableCountByCriteria = enableCountByCriteria;
    }

    public boolean isEnableSelectByCriteria() {
        return enableSelectByCriteria;
    }

    public void setEnableSelectByCriteria(boolean enableSelectByCriteria) {
        this.enableSelectByCriteria = enableSelectByCriteria;
    }

    public boolean isEnableUpdateByPrimaryKey() {
        return enableUpdateByPrimaryKey;
    }

    public void setEnableUpdateByPrimaryKey(boolean enableUpdateByPrimaryKey) {
        this.enableUpdateByPrimaryKey = enableUpdateByPrimaryKey;
    }

    public boolean isEnableUpdateByPrimaryKeySelective() {
        return enableUpdateByPrimaryKeySelective;
    }

    public void setEnableUpdateByPrimaryKeySelective(boolean enableUpdateByPrimaryKeySelective) {
        this.enableUpdateByPrimaryKeySelective = enableUpdateByPrimaryKeySelective;
    }

    public boolean isEnableDeleteByPrimaryKey() {
        return enableDeleteByPrimaryKey;
    }

    public void setEnableDeleteByPrimaryKey(boolean enableDeleteByPrimaryKey) {
        this.enableDeleteByPrimaryKey = enableDeleteByPrimaryKey;
    }

    public boolean isEnableDeleteByCriteria() {
        return enableDeleteByCriteria;
    }

    public void setEnableDeleteByCriteria(boolean enableDeleteByCriteria) {
        this.enableDeleteByCriteria = enableDeleteByCriteria;
    }

    public void setColumnOverrides(List<ColumnOverride> columnOverrides) {
        this.columnOverrides = columnOverrides;
    }

    public Map<IgnoredColumn, Boolean> getIgnoredColumns() {
        return ignoredColumns;
    }

    public void setIgnoredColumns(Map<IgnoredColumn, Boolean> ignoredColumns) {
        this.ignoredColumns = ignoredColumns;
    }

    public boolean isWildcardEscapingEnabled() {
        return wildcardEscapingEnabled;
    }

    public void setWildcardEscapingEnabled(boolean wildcardEscapingEnabled) {
        this.wildcardEscapingEnabled = wildcardEscapingEnabled;
    }

    public boolean isDelimitIdentifiers() {
        return delimitIdentifiers;
    }

    public void setDelimitIdentifiers(boolean delimitIdentifiers) {
        this.delimitIdentifiers = delimitIdentifiers;
    }

    public DomainObjectRenamingRule getDomainObjectRenamingRule() {
        return domainObjectRenamingRule;
    }

    public void setDomainObjectRenamingRule(DomainObjectRenamingRule domainObjectRenamingRule) {
        this.domainObjectRenamingRule = domainObjectRenamingRule;
    }

    public ColumnRenamingRule getColumnRenamingRule() {
        return columnRenamingRule;
    }

    public void setColumnRenamingRule(ColumnRenamingRule columnRenamingRule) {
        this.columnRenamingRule = columnRenamingRule;
    }

    public boolean isAllColumnDelimitingEnabled() {
        return isAllColumnDelimitingEnabled;
    }

    public void setAllColumnDelimitingEnabled(boolean allColumnDelimitingEnabled) {
        isAllColumnDelimitingEnabled = allColumnDelimitingEnabled;
    }

    public String getMapperName() {
        return mapperName;
    }

    public void setMapperName(String mapperName) {
        this.mapperName = mapperName;
    }

    public String getSqlProviderName() {
        return sqlProviderName;
    }

    public void setSqlProviderName(String sqlProviderName) {
        this.sqlProviderName = sqlProviderName;
    }

    public List<IgnoredColumnPattern> getIgnoredColumnPatterns() {
        return ignoredColumnPatterns;
    }

    public void setIgnoredColumnPatterns(List<IgnoredColumnPattern> ignoredColumnPatterns) {
        this.ignoredColumnPatterns = ignoredColumnPatterns;
    }
}
