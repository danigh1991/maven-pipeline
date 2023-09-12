package com.core.datamodel.model.dbmodel;

import com.core.datamodel.model.wrapper.DomainWrapper;
import com.core.datamodel.util.json.JsonDateTimeSerializer;
import com.core.model.dbmodel.AbstractBaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "domainWrapperMapping",
                classes = {
                        @ConstructorResult(
                                targetClass = DomainWrapper.class,
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "domainUrl", type = String.class),
                                        @ColumnResult(name = "siteThemeId", type = Long.class),
                                        @ColumnResult(name = "siteThemeName", type = String.class),
                                        @ColumnResult(name = "languageId", type = Long.class),
                                        @ColumnResult(name = "languageShortName", type = String.class),
                                        @ColumnResult(name = "languageLtrDirection", type = Boolean.class),
                                        @ColumnResult(name = "countryId", type = Long.class),
                                        @ColumnResult(name = "countryName", type = String.class),
                                        @ColumnResult(name = "domainOrder", type = Integer.class)
                                }
                        )
                }
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(name = "Domain.getActiveDomainWrappersByUrl",
                query = "SELECT d.dmn_id AS  id, d.dmn_domain_url as  domainUrl,t.thm_id  siteThemeId,\n" +
                        "       t.thm_name AS siteThemeName,l.lng_id as languageId,l.lng_short_name AS languageShortName,\n" +
                        "       l.lng_left_to_right AS languageLtrDirection,c.cnt_id as countryId,c.cnt_name as countryName,\n" +
                        "       d.dmn_order as domainOrder\n" +
                        "FROM sc_domain d \n" +
                        "INNER JOIN sc_site_theme t ON (d.dmn_thm_id=t.thm_id)\n" +
                        "INNER JOIN sc_language l ON (d.dmn_lng_id=l.lng_id)\n" +
                        "inner JOIN sc_country c ON (d.dmn_default_cnt_Id=c.cnt_id)\n" +
                        "WHERE d.dmn_domain_url=:domainUrl and d.dmn_active AND l.lng_active AND t.thm_active AND c.cnt_isactive \n" +
                        "ORDER BY d.dmn_order ",
                resultSetMapping = "domainWrapperMapping"),
        @NamedNativeQuery(name = "Domain.getTop1ActiveDomainWrapperByUrl",
                query = "SELECT d.dmn_id AS  id, d.dmn_domain_url as  domainUrl,t.thm_id  siteThemeId,\n" +
                        "       t.thm_name AS siteThemeName,l.lng_id as languageId,l.lng_short_name AS languageShortName,\n" +
                        "       l.lng_left_to_right AS languageLtrDirection,c.cnt_id as countryId,c.cnt_name as countryName,\n" +
                        "       d.dmn_order as domainOrder\n" +
                        "FROM sc_domain d \n" +
                        "INNER JOIN sc_site_theme t ON (d.dmn_thm_id=t.thm_id)\n" +
                        "INNER JOIN sc_language l ON (d.dmn_lng_id=l.lng_id)\n" +
                        "inner JOIN sc_country c ON (d.dmn_default_cnt_Id=c.cnt_id)\n" +
                        "WHERE d.dmn_domain_url=:domainUrl and d.dmn_active AND l.lng_active AND t.thm_active AND c.cnt_isactive \n" +
                        "ORDER BY d.dmn_order  limit 1",
                resultSetMapping = "domainWrapperMapping"),
        @NamedNativeQuery(name = "Domain.getTop1ActiveDomainWrapper",
                query = "SELECT d.dmn_id AS  id, d.dmn_domain_url as  domainUrl,t.thm_id  siteThemeId,\n" +
                        "       t.thm_name AS siteThemeName,l.lng_id as languageId,l.lng_short_name AS languageShortName,\n" +
                        "       l.lng_left_to_right AS languageLtrDirection,c.cnt_id as countryId,c.cnt_name as countryName,\n" +
                        "       d.dmn_order as domainOrder\n" +
                        "FROM sc_domain d \n" +
                        "INNER JOIN sc_site_theme t ON (d.dmn_thm_id=t.thm_id)\n" +
                        "INNER JOIN sc_language l ON (d.dmn_lng_id=l.lng_id)\n" +
                        "inner JOIN sc_country c ON (d.dmn_default_cnt_Id=c.cnt_id)\n" +
                        "WHERE d.dmn_active AND l.lng_active AND t.thm_active AND c.cnt_isactive \n" +
                        "ORDER BY d.dmn_order  limit 1",
                resultSetMapping = "domainWrapperMapping"),
        @NamedNativeQuery(name = "Domain.getActiveDomainWrapperById",
                query = "SELECT d.dmn_id AS  id, d.dmn_domain_url as  domainUrl,t.thm_id  siteThemeId,\n" +
                        "       t.thm_name AS siteThemeName,l.lng_id as languageId,l.lng_short_name AS languageShortName,\n" +
                        "       l.lng_left_to_right AS languageLtrDirection,c.cnt_id as countryId,c.cnt_name as countryName,\n" +
                        "       d.dmn_order as domainOrder\n" +
                        "FROM sc_domain d \n" +
                        "INNER JOIN sc_site_theme t ON (d.dmn_thm_id=t.thm_id)\n" +
                        "INNER JOIN sc_language l ON (d.dmn_lng_id=l.lng_id)\n" +
                        "inner JOIN sc_country c ON (d.dmn_default_cnt_Id=c.cnt_id)\n" +
                        "WHERE d.dmn_id=:domainId AND d.dmn_active AND l.lng_active AND t.thm_active AND c.cnt_isactive \n" +
                        "ORDER BY d.dmn_order ",
                resultSetMapping = "domainWrapperMapping")
})

@Table(name = Domain.TABLE_NAME)
@SequenceGenerator(name = AbstractBaseEntity.DEFAULT_SEQ_GEN, sequenceName = Domain.TABLE_NAME + "_SEQ",
        allocationSize = AbstractBaseEntity.ALLOCATION_SIZE)
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "dmn_id")),
        @AttributeOverride(name = "version", column = @Column(name = "dmn_version")),
        @AttributeOverride(name = "createDate", column = @Column(name = "dmn_create_date")),
        @AttributeOverride(name = "modifyDate", column = @Column(name = "dmn_modify_date")),
        @AttributeOverride(name = "createBy", column = @Column(name = "dmn_create_by")),
        @AttributeOverride(name = "modifyBy", column = @Column(name = "dmn_modify_by"))
})
public class Domain extends AbstractBaseEntity<Long> {
    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "sc_domain";

    @Column(name = "dmn_domain_url",nullable = false)
    private String domainUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dmn_thm_id",nullable = false,referencedColumnName = "thm_id", foreignKey = @ForeignKey(name = "FK_dmn_thm_id"))
    @JsonIgnoreProperties("domains")
    private SiteTheme siteTheme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dmn_lng_id",nullable = false,referencedColumnName = "lng_id", foreignKey = @ForeignKey(name = "FK_dmn_lng_id"))
    @JsonIgnoreProperties("domains")
    private Language language;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dmn_default_cnt_Id",nullable = false,referencedColumnName = "cnt_Id", foreignKey = @ForeignKey(name = "FK_dmn_default_cnt_Id"))
    @JsonIgnoreProperties("domains")
    private Country country;

    @Column(name = "dmn_active",nullable = false)
    private Boolean active;

    @Column(name = "dmn_desc")
    private String description;

    @Column(name = "dmn_order",nullable = false)
    private Integer order;

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getCreateDate() {
        return createDate;
    }

    @JsonSerialize(using = JsonDateTimeSerializer.class)
    public Date getModifyDate() {
        return modifyDate;
    }

}
