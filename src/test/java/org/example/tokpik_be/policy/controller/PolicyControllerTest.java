package org.example.tokpik_be.policy.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.example.tokpik_be.policy.dto.response.PolicyResponse;
import org.example.tokpik_be.policy.service.PolicyService;
import org.example.tokpik_be.support.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.ResultActions;

public class PolicyControllerTest extends ControllerTestSupport {

	@Mock
	private PolicyService policyService;

	@InjectMocks
	private PolicyController policyController;

	@Override
	protected Object initController() {
		return policyController;
	}

	@DisplayName("개인정보정책 조회 시 성공한다.")
	@Test
	void getPolicySuccess() throws Exception {
	    // given
		List<PolicyResponse> response = List.of(
			new PolicyResponse("데이터 분석", "일부 모바일 앱에서 사용자의.."),
			new PolicyResponse("서비스", "일부 서비스에서는.."));

		given(policyService.getAllPolicy()).willReturn(response);

	    // when
		ResultActions resultActions = mockMvc.perform(get("/policies"));

	    // then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].title").value("데이터 분석"))
			.andExpect(jsonPath("$[0].content").value("일부 모바일 앱에서 사용자의.."))
			.andExpect(jsonPath("$[1].title").value("서비스"))
			.andExpect(jsonPath("$[1].content").value("일부 서비스에서는.."));
	}
}
