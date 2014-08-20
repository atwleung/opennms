/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.core.style.ToStringCreator;

@XmlRootElement(name = "hwEntityAttribute")
@Entity
@Table(name="hwEntityAttribute")
@XmlAccessorType(XmlAccessType.NONE)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OnmsHwEntityAttribute implements Serializable, Comparable<OnmsHwEntityAttribute> {

    private static final long serialVersionUID = 468469978315437731L;

    private Integer m_id;

    private HwEntityAttributeType m_attributeType;

    private String m_attributeValue;

    private OnmsHwEntity m_hwEntity;

    public OnmsHwEntityAttribute() {
    }

    public OnmsHwEntityAttribute(HwEntityAttributeType type, String value) {
        super();
        this.m_attributeType = type;
        this.m_attributeValue = value;
    }

    @Id
    @Column(nullable=false)
    @XmlTransient
    @SequenceGenerator(name="opennmsSequence", sequenceName="opennmsNxtId")
    @GeneratedValue(generator="opennmsSequence")    
    public Integer getId() {
        return m_id;
    }

    public void setId(Integer id) {
        m_id = id;
    }

    @ManyToOne(optional=false, fetch=FetchType.LAZY)
    @JoinColumn(name="hwEntityId")
    @XmlTransient
    public OnmsHwEntity getHwEntity() {
        return m_hwEntity;
    }

    public void setHwEntity(OnmsHwEntity hwEntity) {
        m_hwEntity = hwEntity;
    }

    @ManyToOne(optional=false, cascade={CascadeType.ALL})
    @JoinColumn(name="hwAttribTypeId")
    @XmlTransient
    public HwEntityAttributeType getType() {
        return m_attributeType;
    }

    public void setType(HwEntityAttributeType attributeType) {
        this.m_attributeType = attributeType;
    }

    @Transient
    @XmlAttribute(name="name")
    public String getTypeName() {
        return m_attributeType == null ? null : m_attributeType.getName();
    }

    @Transient
    @XmlAttribute(name="oid")
    public String getTypeOid() {
        return m_attributeType == null ? null : m_attributeType.getOid();
    }

    @Column(name="attribValue")
    @XmlAttribute(name="value")
    public String getValue() {
        return m_attributeValue;
    }

    public void setValue(String attributeValue) {
        this.m_attributeValue = attributeValue;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this)
        .append("entityIndex", m_hwEntity == null ? null : m_hwEntity.getEntPhysicalIndex())
        .append("type", m_attributeType)
        .append("value", m_attributeValue)
        .toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OnmsHwEntityAttribute) {
            OnmsHwEntityAttribute other = (OnmsHwEntityAttribute) obj;
            if (m_attributeType != null &&  other.m_attributeType != null && m_attributeType.equals(other.m_attributeType)) {
                if (m_hwEntity != null &&  other.m_hwEntity != null && m_hwEntity.equals(other.m_hwEntity)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int compareTo(OnmsHwEntityAttribute o) {
        return o.getType().compareTo(getType());
    }

}