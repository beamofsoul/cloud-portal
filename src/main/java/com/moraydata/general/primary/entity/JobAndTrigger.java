package com.moraydata.general.primary.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data

@Entity
@Table(name = "V_JOB_COMBINE_TRIGGER")
public class JobAndTrigger implements Serializable {
	
	private static final long serialVersionUID = -7527558873596745876L;
	
	@Id
	@GeneratedValue
	protected Long id;
	
	@Column(insertable=false, updatable=false)
	private String jobName;
	
	@Column(insertable=false, updatable=false)
	private String jobGroup;
	
	@Column(insertable=false, updatable=false)
	private String jobClassName;
	
	@Column(insertable=false, updatable=false)
	private String triggerName;
	
	@Column(insertable=false, updatable=false)
	private String triggerGroup;
	
//	private BigInteger repeatInterval;
//	private BigInteger timesTriggered;
	
	@Column(insertable=false, updatable=false)
	private String cronExpression;
	
	@Column(insertable=false, updatable=false)
	private String timeZoneId;
}
