package com.moraydata.general.secondary.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.moraydata.general.primary.entity.BaseAbstractEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "t_test")
public class Test extends BaseAbstractEntity {

	private static final long serialVersionUID = 5525338364450607625L;

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(length = 20)
	private String content;
}
