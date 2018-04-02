package com.moraydata.general.primary.entity;

import java.time.LocalDateTime;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "T_INVITATION_CODE")
public class InvitationCode extends BaseAbstractEntity {

	private static final long serialVersionUID = -8490905596080014122L;

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(columnDefinition = "bigint not null comment '持有用户'")
	private Long userId;
	
	@Column(columnDefinition = "varchar(100) not null comment '邀请码'")
	private String code;
	
	@Column(columnDefinition = "datetime comment '过期时间'")
	private LocalDateTime expiredDate;
	
	@Column(columnDefinition = "bit default 1 comment '邀请码状态 - 1:可用,0:不可用'")
	private Boolean available;
	
	@Column(columnDefinition = "tinyint default 1 comment '邀请码类型 - 1:绑定父账户邀请码'")
	private Integer type;
	
	public Type getTypeEnum() {
		return Type.getInstance(type);
	}
	
	@RequiredArgsConstructor(access=AccessLevel.PROTECTED)
	public static enum Type {
		BIND_PARENT_USER_ID(1);
		@Getter private final int value;
		private static HashMap<Integer, Type> codeValueMap = new HashMap<>(3);
		static {
			for (Type type : Type.values()) {
				codeValueMap.put(type.value, type);
			}
		}
		public static Type getInstance(int code) {
			return codeValueMap.get(code);
		}
	}

	@Override
	public String toString() {
		return "InvitationCode [id=" + id + ", userId=" + userId + ", code=" + code + ", expiredDate=" + expiredDate
				+ ", status=" + available + ", type=" + type + "]";
	}
}
