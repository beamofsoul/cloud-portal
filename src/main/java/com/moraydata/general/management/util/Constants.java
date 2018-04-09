package com.moraydata.general.management.util;

public final class Constants {

	public static final class CURRENT {
		public static final String USER = "CURRENT_USER";
		public static final String LOGIN = "CURRENT_LOGIN";
	}
	
	public static final class ENTITY {
		public static final String DEFAULT_PRIMARY_KEY = "id";
	}
	
	public static final class PAGEABLE {
		public static final String PAGE_NUMBER_NAME = "page";
		public static final String PAGE_SIZE_NAME = "size";
		public static final String SORT_BY_NAME = "sort";
		public static final String SORT_DIRECTION_NAME = "direction";
	}
	
	public static final class RESPONSE_ENTITY {
		public static final String ERROR = "100001";
		public static final String SUCCESS = "100000";
		
		public static final String OPERATION_FAILURE_CAUSED_BY_UNKNOWN_ERROR = "未知错误引起操作失败";
		public static final String OPERATION_SUCCESS = "操作成功";
	}
	
	public static final class ROLE {
		public static final String TRIAL_ROLE_NAME = "trial";
		public static final String SLAVE_ROLE_NAME = "slave";
	}
	
	public static final class ORDER {
		public static final String CODE_SEQUENCE_NAME = "order_code";
	}
}
