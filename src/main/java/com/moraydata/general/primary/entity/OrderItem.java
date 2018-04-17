package com.moraydata.general.primary.entity;

import java.time.LocalDateTime;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data

@Entity
@Table(name = "T_ORDER_ITEM")
public class OrderItem extends BaseAbstractEntity {

	private static final long serialVersionUID = 1361607296859666027L;

	@Id
	@GeneratedValue
	protected Long id;

	@Column(columnDefinition = "bigint not null comment '用户ID'")
	private Long userId;
	
	@Column(columnDefinition = "bigint not null comment '服务ID'")
	private Long serviceId;
	
	@Transient
	private String serviceName;
	
	@Column(columnDefinition = "varchar(100) comment '备注'")
	private String description;
	
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "datetime comment '服务开始时间'")
	private LocalDateTime serviceBeginTime;
	
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(columnDefinition = "datetime comment '服务开始时间'")
	private LocalDateTime serviceEndTime;
	
	@Column(columnDefinition = "int default 1 comment '订单状态 - 1:新建,2:生效,3:过期'")
	private Integer status;
	
	public Status getStatusEnum() {
		if (this.status == null) {
			return null;
		}
		return Status.getInstance(status);
	}
	
	@RequiredArgsConstructor(access=AccessLevel.PROTECTED)
	public static enum Status {
		CREATED(1),EXECUTED(2),EXPIRED(3);
		@Getter private final int value;
		private static HashMap<Integer, Status> codeValueMap = new HashMap<>(3);
		static {
			for (Status status : Status.values()) {
				codeValueMap.put(status.value, status);
			}
		}
		public static Status getInstance(int code) {
			return codeValueMap.get(code);
		}
	}

	@Override
	public String toString() {
		return "OrderItem [id=" + id + ", userId=" + userId + ", serviceId=" + serviceId + ", description="
				+ description + ", serviceBeginTime=" + serviceBeginTime + ", serviceEndTime=" + serviceEndTime
				+ ", status=" + status + "]";
	}
}
