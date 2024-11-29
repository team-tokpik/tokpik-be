package org.example.tokpik_be.term.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.example.tokpik_be.support.ControllerTestSupport;
import org.example.tokpik_be.term.dto.response.TermResponse;
import org.example.tokpik_be.term.service.TermService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.servlet.ResultActions;

public class TermControllerTest extends ControllerTestSupport {

	@Mock
	private TermService termService;

	@InjectMocks
	private TermController termController;

	@Override
	protected Object initController() {
		return termController;
	}

	@DisplayName("이용약관 조회 시 성공한다.")
	@Test
	void getTermSuccess() throws Exception {
		// given
		List<TermResponse> response = List.of(
			new TermResponse(
				"콘텐츠에 대한 라이선스",
				List.of(
					new TermResponse.TermsSection("1", "1.1", "업로드된 콘텐츠", "이 콘텐츠에서는.."),
					new TermResponse.TermsSection("2", "2.1", "콘텐츠 사용 제한", "이 콘텐츠는 다음과 같이 제한..")
				)));

		given(termService.getAllTerms()).willReturn(response);

		// when
		ResultActions resultActions = mockMvc.perform(get("/terms"));

		// then
		resultActions
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].title").value("콘텐츠에 대한 라이선스"))
			.andExpect(jsonPath("$.[0].sections[0].mainCategory").value("1"))
			.andExpect(jsonPath("$.[0].sections[0].subCategory").value("1.1"))
			.andExpect(jsonPath("$.[0].sections[0].contentTitle").value("업로드된 콘텐츠"))
			.andExpect(jsonPath("$.[0].sections[0].content").value("이 콘텐츠에서는.."))
			.andExpect(jsonPath("$.[0].sections[1].mainCategory").value("2"))
			.andExpect(jsonPath("$.[0].sections[1].subCategory").value("2.1"))
			.andExpect(jsonPath("$.[0].sections[1].contentTitle").value("콘텐츠 사용 제한"))
			.andExpect(jsonPath("$.[0].sections[1].content").value("이 콘텐츠는 다음과 같이 제한.."));
	}
}

