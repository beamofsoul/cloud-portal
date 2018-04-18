package com.moraydata.general.primary.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.JSON;
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
@Table(name = "T_ORDER")
public class Order extends BaseAbstractEntity {

	private static final long serialVersionUID = -393883982864703642L;

	@Id
	@GeneratedValue
	protected Long id;
	
//	@ManyToOne
//	@JoinColumn(name = "user_id", columnDefinition = "bigint(20) not null comment '用户ID'")
//	private User user;
	@Column(columnDefinition = "bigint(20) not null comment '用户ID'")
	private Long userId;
	
	@Transient
	private String username;
	
	@Column(columnDefinition = "varchar(20) comment '订单编号'")
	private String code;
	
	@Column(columnDefinition = "decimal(12,2) comment '订单金额'")
	private BigDecimal amount;
	
	@Column(columnDefinition = "varchar(100) comment '代理商'")
	private String agent;
	
	@Column(columnDefinition = "decimal(12,2) comment '代理商结算金额'")
	private BigDecimal amountForAgent;
	
	@Column(columnDefinition = "varchar(50) comment '运维人员'")
	private String operator;
	
	@Column(columnDefinition = "varchar(256) not null comment '具体服务'")
	private String serviceIds;
	
	@Transient
	private List<OrderItem> orderItems;
	
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
	
	public Order(Long id, Long userId, String username, String code, BigDecimal amount, String agent, BigDecimal amountForAgent, String operator, String serviceIds, String description, LocalDateTime serviceBeginTime, LocalDateTime serviceEndTime, Integer status) {
		this.id = id;
		this.userId = userId;
		this.username = username;
		this.code = code;
		this.amount = amount;
		this.agent = agent;
		this.amountForAgent = amountForAgent;
		this.operator = operator;
		this.serviceIds = serviceIds;
		this.description = description;
		this.serviceBeginTime = serviceBeginTime;
		this.serviceEndTime = serviceEndTime;
		this.status = status;
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
		public static boolean exists(int code) {
			return codeValueMap.containsKey(code);
		}
	}

	@Override
	public String toString() {
		String items = null;
		if (orderItems != null && orderItems.size() > 0) {
			items = ", orderItems=" + JSON.toJSONString(orderItems);
		}
		String toString = "Order [id=" + id + ", userId=" + userId + ", code=" + code + ", amount=" + amount + ", agent=" + agent
				+ ", amountForAgent=" + amountForAgent + ", operator=" + operator + ", serviceIds=" + serviceIds
				+ ", description=" + description + ", serviceBeginTime=" + serviceBeginTime + ", serviceEndTime="
				+ serviceEndTime + ", status=" + status + items + "]";
		
		return toString;
	}
}
