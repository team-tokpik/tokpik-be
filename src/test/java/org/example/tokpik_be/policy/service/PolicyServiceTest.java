package org.example.tokpik_be.policy.service;

import static org.mockito.BDDMockito.*;

import java.util.List;

import org.assertj.core.api.SoftAssertions;
import org.example.tokpik_be.policy.domain.Policy;
import org.example.tokpik_be.policy.dto.response.PolicyResponse;
import org.example.tokpik_be.policy.repository.PolicyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PolicyServiceTest {

	@Mock
	private PolicyRepository policyRepository;

	@InjectMocks
	private PolicyService policyService;

	@DisplayName("개인정보정책을 조회한다.")
	@Test
	void getPolicy(){
	    // given
		List<Policy> policies = List.of(
			new Policy("데이터 분석", "일부 모바일 앱에서 사용자의.."),
			new Policy("서비스", "일부 서비스에서는.."));

		given(policyRepository.findAll()).willReturn(policies);

	    // when
		List<PolicyResponse> response = policyService.getAllPolicy();

	    // then
		SoftAssertions.assertSoftly(softly -> {
			softly.assertThat(response).hasSize(2);
			softly.assertThat(response.get(0).title()).isEqualTo("데이터 분석");
			softly.assertThat(response.get(0).content()).isEqualTo("일부 모바일 앱에서 사용자의..");
			softly.assertThat(response.get(1).title()).isEqualTo("서비스");
			softly.assertThat(response.get(1).content()).isEqualTo("일부 서비스에서는..");
		});
	}
}
