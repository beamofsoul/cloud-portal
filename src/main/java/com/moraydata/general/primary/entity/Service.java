package com.moraydata.general.primary.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "T_SERVICE")
public class Service extends BaseAbstractEntity {

	private static final long serialVersionUID = -6745086368720924693L;

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(columnDefinition = "varchar(50) not null comment '服务名称'")
	private String name;
	
	@Column(columnDefinition = "bit default 1 comment '是否可用'")
	private boolean available;
}
