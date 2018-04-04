//package com.moraydata.general.management.message;
//
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ExecutionException;
//
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.http.converter.StringHttpMessageConverter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
///**
// * Default way to send given user a message code. 
// * @author Mingshu Jian  
// * @date 2018-03-30
// */
//@Component
//final class DefaultMessageCodeSender implements MessageCodeSender {
//	
//	private final String requestUrl = "http://api.feige.ee/SmsService/Send";
//	private final String account = "18600574873";
//	private final String password = "7f9d61947e5c6950edba933eb";
//	private final String signId = "40796";
//	
//	private RestTemplate template;
//
//	@Override
//	@SuppressWarnings({ "serial", "unchecked", "rawtypes" })
//	public boolean send(String phone, int code, String content) throws InterruptedException, ExecutionException {
//		ResponseEntity<Map> responseEntity = getRestTemplate().exchange(requestUrl, HttpMethod.POST, new HttpEntity(new HashMap<String, Object>() {{
//			put("Account", account);
//			put("Pwd", password);
//			put("Content", String.format(content, code));
//			put("Mobile", phone);
//			put("SignId", signId);
//		}}), Map.class);
//		Map body = responseEntity.getBody();
//		
////		SendId:2018032914390368030138724    // 短信回执编号，为唯一识别码，用户可通过此编号获取记录详情
////		InvalidCount:0                      // 无效号码（可能是非手机号码如02161531765）
////		SuccessCount:1                      // 成功数量（一般为该批次计费条数）
////		BlackCount:0                        // 黑名单号码（一般为该批次计费条数）
////		Code:0                              // 短信回执状态码，判断成功失败的标志（成功为0，其他请参照 API 错误代码）
////		Message:OK                          // 短信回执状态描述（成功为ok，其它请参考 API 错误代码）
//		
//		return "ok".equals(body.get("Message"));
//	}
//	
//	private RestTemplate getRestTemplate() {
//		if (this.template == null) {
//			RestTemplate template = new RestTemplate();
//			template.getMessageConverters().set(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
//			this.template = template;
//		}
//		return this.template; 
//	}
//	
////	//产品名称:云通信短信API产品,开发者无需替换
////    static final String product = "Dysmsapi";
////    //产品域名,开发者无需替换
////    static final String domain = "dysmsapi.aliyuncs.com";
////
////    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
////    static final String accessKeyId = "LTAImyRL3hwgWVqo"; 
////    static final String accessKeySecret = "oeXnKT9SZZu4lx4bOTg0F1dVnwCHJT";
////
////    public static SendSmsResponse sendSms() throws ClientException {
////
////        //可自助调整超时时间
////        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
////        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
////
////        //初始化acsClient,暂不支持region化
////        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
////        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
////        IAcsClient acsClient = new DefaultAcsClient(profile);
////
////        //组装请求对象-具体描述见控制台-文档部分内容
////        SendSmsRequest request = new SendSmsRequest();
////        //必填:待发送手机号
////        request.setPhoneNumbers("15000000000");
////        //必填:短信签名-可在短信控制台中找到
////        request.setSignName("海鳗数据");
////        //必填:短信模板-可在短信控制台中找到
////        request.setTemplateCode("SMS_129763401");
////        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
////        request.setTemplateParam(String.format("{\"code\":\"$s\"}", "code"));
////
////        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
////        request.setOutId("yourOutId");
////
////        //hint 此处可能会抛出异常，注意catch
////        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
////
////        return sendSmsResponse;
////    }
////
////    public static QuerySendDetailsResponse querySendDetails(String bizId) throws ClientException {
////
////        //可自助调整超时时间
////        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
////        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
////
////        //初始化acsClient,暂不支持region化
////        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
////        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
////        IAcsClient acsClient = new DefaultAcsClient(profile);
////
////        //组装请求对象
////        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
////        //必填-号码
////        request.setPhoneNumber("15000000000");
////        //可选-流水号
////        request.setBizId(bizId);
////        //必填-发送日期 支持30天内记录查询，格式yyyyMMdd
////        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
////        request.setSendDate(ft.format(new Date()));
////        //必填-页大小
////        request.setPageSize(10L);
////        //必填-当前页码从1开始计数
////        request.setCurrentPage(1L);
////
////        //hint 此处可能会抛出异常，注意catch
////        QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);
////
////        return querySendDetailsResponse;
////    }
////
////    public static void main(String[] args) throws ClientException, InterruptedException {
////
////        //发短信
////        SendSmsResponse response = sendSms();
////        System.out.println("短信接口返回的数据----------------");
////        System.out.println("Code=" + response.getCode());
////        System.out.println("Message=" + response.getMessage());
////        System.out.println("RequestId=" + response.getRequestId());
////        System.out.println("BizId=" + response.getBizId());
////
////        Thread.sleep(3000L);
////
////        //查明细
////        if(response.getCode() != null && response.getCode().equals("OK")) {
////            QuerySendDetailsResponse querySendDetailsResponse = querySendDetails(response.getBizId());
////            System.out.println("短信明细查询接口返回数据----------------");
////            System.out.println("Code=" + querySendDetailsResponse.getCode());
////            System.out.println("Message=" + querySendDetailsResponse.getMessage());
////            int i = 0;
////            for(QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse.getSmsSendDetailDTOs())
////            {
////                System.out.println("SmsSendDetailDTO["+i+"]:");
////                System.out.println("Content=" + smsSendDetailDTO.getContent());
////                System.out.println("ErrCode=" + smsSendDetailDTO.getErrCode());
////                System.out.println("OutId=" + smsSendDetailDTO.getOutId());
////                System.out.println("PhoneNum=" + smsSendDetailDTO.getPhoneNum());
////                System.out.println("ReceiveDate=" + smsSendDetailDTO.getReceiveDate());
////                System.out.println("SendDate=" + smsSendDetailDTO.getSendDate());
////                System.out.println("SendStatus=" + smsSendDetailDTO.getSendStatus());
////                System.out.println("Template=" + smsSendDetailDTO.getTemplateCode());
////            }
////            System.out.println("TotalCount=" + querySendDetailsResponse.getTotalCount());
////            System.out.println("RequestId=" + querySendDetailsResponse.getRequestId());
////        }
////
////    }
//}
