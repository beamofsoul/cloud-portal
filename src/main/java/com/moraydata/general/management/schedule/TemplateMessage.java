package com.moraydata.general.management.schedule;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateMessage {

	private static final String DEFAUL_FIRST_VALUE = "海鳗云平台检测到一个异常事件";
	private static final String DEFAULT_REMARK_VALUE = "请及时查看处置!";
	private static final String DEFAULT_COLOR_173177 = "#173177";
	
	private Long id;
	
	private String firstValue;
	private String firstColor;
	
	private String keyword1Value;
	private String keyword1Color;
	
	private String keyword2Value;
	private String keyword2Color;
	
	private String keyword3Value;
	private String keyword3Color;
	
	private String keyword4Value;
	private String keyword4Color;
	
	private String remarkValue;
	private String remarkColor;
	
	private String redirectUrl;
	private Integer action;
	private Integer type;
	private Boolean isHighlyRelevant;
	
	public TemplateMessage(String keyword1, String keyword2, String keyword3, String keyword4) {
		this.keyword1Value = keyword1;
		this.keyword2Value = keyword2;
		this.keyword3Value = keyword3;
		this.keyword4Value = keyword4;
	}
	
	public TemplateMessage(String keyword1, String keyword2, String keyword3, String keyword4, String redirectUrl) {
		this.keyword1Value = keyword1;
		this.keyword2Value = keyword2;
		this.keyword3Value = keyword3;
		this.keyword4Value = keyword4;
		this.redirectUrl = redirectUrl;
	}
	
	public String getFirstValue() {
		return StringUtils.isBlank(this.firstValue) ? DEFAUL_FIRST_VALUE : this.firstValue;
	}
	
	public String getRemarkValue() {
		return StringUtils.isBlank(this.remarkValue) ? DEFAULT_REMARK_VALUE : this.remarkValue;
	}
	
	public String getFirstColor() {
		return getReasonableColor(this.firstColor);
	}

	public String getKeyword1Color() {
		return getReasonableColor(this.keyword1Color);
	}
	
	public String getKeyword2Color() {
		return getReasonableColor(this.keyword2Color);
	}
	
	public String getKeyword3Color() {
		return getReasonableColor(this.keyword3Color);
	}
	
	public String getKeyword4Color() {
		return getReasonableColor(this.keyword4Color);
	}
	
	public String getRemarkColor() {
		return getReasonableColor(this.remarkColor);
	}
	
	private String getReasonableColor(String color) {
		return StringUtils.isBlank(color) ? DEFAULT_COLOR_173177 : color;
	}
	
	@RequiredArgsConstructor(access=AccessLevel.PROTECTED)
	public static enum Action {
		PUSH(2), VALIDATE(1), INVALIDATE(-1), UNTREATED(0);
		@Getter private final Integer value;
		public static final HashMap<Integer, Action> CODE_VALUE_MAP = new HashMap<>(3);
		static {
			for (Action action : Action.values()) {
				CODE_VALUE_MAP.put(action.value, action);
			}
		}
		public static Action getInstance(Integer code) {
			return CODE_VALUE_MAP.get(code);
		}
		public static boolean exists(Integer code) {
			return CODE_VALUE_MAP.containsKey(code);
		}
	}
}
